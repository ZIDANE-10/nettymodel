package com.jian.nettyclient.service;

import com.jian.nettyclient.mapper.TransRecordMockResultMapper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicLong;

@ChannelHandler.Sharable
@Component
public class BusinessHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    @Autowired
    private TransRecordMockResultMapper transRecordMockResultMapper;
    private volatile AtomicLong total = new AtomicLong(0L);
    private volatile AtomicLong tps = new AtomicLong(0L);
    public void startTps(){
        new Thread(()->{
            while (true){
                long l1 = total.get();
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                }
                long l2 = total.get();
                tps.set(l2-l1);
            }
        },"tps-monitor").start();
    }

    public long getTps(){
        return tps.get();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpRequest fullHttpRequest) throws Exception {
        ByteBuf content = fullHttpRequest.content();
        InputStream xml = getXmlStr(content);
        String ser23 = getSer23(xml);
        System.out.println("---------------mock:"+ser23);
        transRecordMockResultMapper.insertSelect(ser23);
        transRecordMockResultMapper.updateTouchFlag(ser23);
        byte[] resp = transRecordMockResultMapper.selectBySer23(ser23).getResp();
        ByteBuf byteBuf = Unpooled.directBuffer(resp.length);
        byteBuf.writeBytes(resp);
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, byteBuf);
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/xml; charset=UTF-8");
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());
        total.getAndIncrement();
        channelHandlerContext.channel().writeAndFlush(response);
    }

    public InputStream getXmlStr(ByteBuf byteBuf) {
        int i = byteBuf.readableBytes();
        byte[] bytes = new byte[i];
        byteBuf.readBytes(bytes);
        return new ByteArrayInputStream(bytes);
    }

    public String getSer23(InputStream inputStream) {
        String val = null;
        SAXReader reader = new SAXReader();
        try {
            Document document = reader.read(inputStream);
            Element root = document.getRootElement();
            Element req = root.element("REQ");
            val = req.getStringValue();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return val;
    }
}
