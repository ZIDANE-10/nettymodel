package com.jian.nettyclient.service;

import com.jian.nettyclient.domain.TTransRecord;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@ChannelHandler.Sharable
@Component
public class HttpClientHandler extends SimpleChannelInboundHandler<FullHttpResponse> {

    private volatile AtomicBoolean start = new AtomicBoolean(false);

    @Resource
    private TTransService tTransService;

//    @Override
//    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        FullHttpRequest request = null;
//        ByteBuf byteBuf = null;
//        while (true){
//            while (!this.start.get()){
//                Thread.sleep(1000L);
//            }
//            List<TTransRecord> records = tTransService.getData();
//            if (records == null || records.isEmpty()){
//                break;
//            }
//            for (TTransRecord record : records) {
//                byteBuf = PooledByteBufAllocator.DEFAULT.directBuffer(record.getReq().length);
//                byteBuf.writeBytes(record.getReq());
//                request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, "/sendTest",byteBuf);
//                request.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/xml; charset=UTF-8");
//                request.headers().set(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());
//                ctx.channel().writeAndFlush(request);
//            }
//
//        }
//    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpResponse response) throws Exception {
        ByteBuf content = response.content();
        InputStream xml = getXmlStr(content);
        String ser23 = getSer23(xml);
        System.out.println(ser23);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }

    public boolean pause() {
        this.start.set(true);
        return true;
    }

    public boolean send() {
        this.start.set(false);
        return true;
    }

    public InputStream getXmlStr(ByteBuf byteBuf) {
        int i = byteBuf.readableBytes();
        byte[] bytes = new byte[i];
        byteBuf.readBytes(bytes);
        return new ByteArrayInputStream(bytes);
    }

    public static String getSer23(InputStream inputStream) {
        String val = null;
        SAXReader reader = new SAXReader();
        try {
            Document document = reader.read(inputStream);
            Element root = document.getRootElement();
            Element xKzrD = root.element("xKzrD");
            Element resp = xKzrD.element("RESP");
            val = resp.getStringValue();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return val;
    }
}
