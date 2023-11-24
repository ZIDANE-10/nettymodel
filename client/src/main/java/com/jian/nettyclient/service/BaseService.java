package com.jian.nettyclient.service;

import com.jian.nettyclient.domain.TTransRecord;
import com.jian.nettyclient.mapper.TTransRecordMapper;
import com.jian.nettyclient.util.XMLUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class BaseService {

    private static Logger logger = LoggerFactory.getLogger(BaseService.class);
    private volatile AtomicBoolean start = new AtomicBoolean(false);

    @Resource
    private BusinessHandler businessHandler;
    @Resource
    private HttpClientHandler httpClientHandler;
    @Resource
    private SqlSessionFactory sqlSessionFactory;
    @Resource
    private TTransService transService;

    private NioEventLoopGroup boss;
    private NioEventLoopGroup worker;

    private NioEventLoopGroup clientWorker;

    private int port = 30000;
    private int concurrent = 20;

    public void initData() {
        AtomicLong serializeNo = new AtomicLong(100000000L);
        for (int i = 0; i < concurrent; i++) {
            new Thread(() -> {
                TTransRecord record = new TTransRecord();
                SqlSession sqlSession = sqlSessionFactory.openSession(false);
                TTransRecordMapper mapper = sqlSession.getMapper(TTransRecordMapper.class);
                int count = 0;
                while (true) {
                    long ser = serializeNo.getAndIncrement();
                    if (ser > 112000000) {
                        break;
                    }
                    String ser23 = "HTTPXML453812S" + ser;
                    record.setSer23(ser23);
                    byte[] req = XMLUtil.getReq().replaceAll("999999999", ser23).getBytes(CharsetUtil.UTF_8);
                    record.setReq(req);
                    byte[] resp = XMLUtil.getResp().replaceAll("888888888", ser23).getBytes(CharsetUtil.UTF_8);
                    record.setResp(resp);
                    mapper.insert(record);
                    count++;
                    if (count == 20000) {
                        sqlSession.commit();
                        count = 0;
                        logger.info("[{}] 20000 commit totalnum [{}]", Thread.currentThread().getName(), ser);
                    }
                }
                sqlSession.commit();
                sqlSession.close();
            }, "worker-" + i).start();
        }
    }

    public boolean startNettyServer() {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        boss = new NioEventLoopGroup(1, r -> {
            return new Thread(r, "boss");
        });
        worker = new NioEventLoopGroup(concurrent, r -> {
            return new Thread(r, "worker");
        });
        serverBootstrap.group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .childHandler(new NettyServerHandler(businessHandler));
        try {
            ChannelFuture future = serverBootstrap.bind(port).sync();
            if (future.isDone()) {
                future.channel().closeFuture();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    public boolean shutdownNettyServer() {
        boss.shutdownGracefully();
        worker.shutdownGracefully();
        return true;
    }

    public boolean startNettyClient() {
        Bootstrap bootstrap = new Bootstrap();
        clientWorker = new NioEventLoopGroup(concurrent);
        bootstrap.group(clientWorker)
                .remoteAddress("127.0.0.1", port)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .option(ChannelOption.SO_REUSEADDR,true)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .handler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline()
                                .addLast("clientCodec", new HttpClientCodec())
                                .addLast("agg", new HttpObjectAggregator(1024 * 1024))
                                .addLast(httpClientHandler);
                    }
                });
        try {
            ChannelFuture channelFuture = bootstrap.connect().sync();
            for (int i=0;i<concurrent;i++){
                new Thread(()->{
                    Channel channel = channelFuture.channel();
                    ByteBuf byteBuf = null;
                    FullHttpRequest request = null;
                    while (true){
                        List<TTransRecord> records = transService.getData();
                        if (records == null || records.isEmpty()){
                            break;
                        }
                        for (TTransRecord record : records) {
                            byteBuf = PooledByteBufAllocator.DEFAULT.directBuffer(record.getReq().length);
                            byteBuf.writeBytes(record.getReq());
                            request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, "/sendTest",byteBuf);
                            request.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/xml; charset=UTF-8");
                            request.headers().set(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());
                            ChannelFuture future = channel.writeAndFlush(request);
                            try {
                                future.sync().channel().read();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                },"client-"+i).start();

            }
            return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean shutdownNettyClient(){
        try {
            this.clientWorker.shutdownGracefully();
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean doSend() {

        CloseableHttpClient httpClient = HttpClients.custom().
                setMaxConnTotal(1000).
                setMaxConnPerRoute(1000).
                setConnectionTimeToLive(5000, TimeUnit.MILLISECONDS).build();

        for (int i = 0; i < concurrent; i++) {
            new Thread(() -> {

                HttpPost httpPost = new HttpPost("http://localhost:" + port + "/sendReq");
                httpPost.setHeader(HttpHeaders.CONNECTION, "Keep-alive");
                httpPost.setHeader(HttpHeaders.CONTENT_TYPE, "application/xml; charset=UTF-8");
                while (true) {
                    while (!this.start.get()) {
                        try {
                            Thread.sleep(1000L);
                        } catch (InterruptedException e) {
                        }
                    }
                    List<TTransRecord> records = transService.getData();
                    if (records == null || records.isEmpty()) {
                        break;
                    }
                    for (TTransRecord record : records) {
                        httpPost.setEntity(new ByteArrayEntity(record.getReq()));
                        CloseableHttpResponse response = null;
                        try {
                            response = httpClient.execute(httpPost);
                            HttpEntity entity = response.getEntity();
                            InputStream in = entity.getContent();
                            String ser23 = HttpClientHandler.getSer23(in);
                            System.out.println("resp------------------:" + ser23);

                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                response.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                }
            }).start();
        }
        return true;
    }

    public boolean pause() {
        this.start.set(false);
        return httpClientHandler.pause();
    }

    public boolean send() {
        this.start.set(true);
        return this.doSend();
    }

    public void startTps() {
        businessHandler.startTps();
    }

    public long getTps() {
        return businessHandler.getTps();
    }
}
