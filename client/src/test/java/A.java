import com.jian.nettyclient.service.NettyServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class A {

    public static void main(String[] args) {

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        NioEventLoopGroup boss = new NioEventLoopGroup(1, r -> {
            return new Thread(r, "boss");
        });
        NioEventLoopGroup worker = new NioEventLoopGroup(Math.min(Runtime.getRuntime().availableProcessors(), 32), r -> {
            return new Thread(r, "worker");
        });
        serverBootstrap.group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInboundHandlerAdapter(){
                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                        if (msg instanceof ByteBuf){
                            int read = ((ByteBuf) msg).readableBytes();
                            byte[] bytes = new byte[read];
                            ((ByteBuf) msg).readBytes(bytes);
                            System.out.print(new String(bytes));
                        }
                    }
                });
        try {
            ChannelFuture future = serverBootstrap.bind(8001).sync();
            if (future.isDone()) {
                future.channel().closeFuture();
            }
        } catch (
                Exception e) {
            e.printStackTrace();
        }
    }
}
