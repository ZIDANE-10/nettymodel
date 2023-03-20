package com.jian.nettyclient.service;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

public class NettyServerHandler extends ChannelInitializer<SocketChannel> {

    private BusinessHandler businessHandler;

    public NettyServerHandler(BusinessHandler businessHandler) {
        this.businessHandler = businessHandler;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast(new HttpServerCodec())
                .addLast(new HttpObjectAggregator(1024*1024))
                .addLast(businessHandler);
    }
}
