package netty.demo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * created by zjw
 * 2018/3/19
 */
public class NettyServer {
    private int port = 8080;
    
    public void run() throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new SimpleServerChannelInitializer())
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            
            ChannelFuture f = serverBootstrap.bind(port).sync();
            
            f.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
    
    public static void main(String[] args) {
        NettyServer nettyServer = new NettyServer();
        try {
            nettyServer.run();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    private class SimpleServerChannelInitializer extends ChannelInitializer<SocketChannel> {
    
        @Override
        protected void initChannel(SocketChannel ch) throws Exception {
            ch.pipeline().addLast(new SimpleServerHandler());
        }
    }
    private class SimpleServerHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ByteBuf buf = (ByteBuf)msg;
            
            byte [] req = new byte[buf.readableBytes()];

            buf.readBytes(req);

            String message = new String(req,"UTF-8");

            System.out.println("Netty-Server:Receive Message,"+ message);

            buf.writeBytes(req);
            ctx.writeAndFlush(msg);
        }
    
        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
            ctx.close();
        }
    }
}
