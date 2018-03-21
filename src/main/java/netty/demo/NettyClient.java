package netty.demo;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;


/**
 * created by zjw
 * 2018/3/19
 */
public class NettyClient {
    private String ip = "127.0.0.1";
    private int port = 8080;
    
    public NettyClient() {
    
    }
    
    public void connect() throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new SimpleClientChannelInitializer())
                    .option(ChannelOption.TCP_NODELAY, true);
            
            ChannelFuture f = bootstrap.connect(ip, port).sync();
            
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }
    
    public static void main(String[] args) {
        NettyClient nettyClient = new NettyClient();
        try {
            nettyClient.connect();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    private class SimpleClientChannelInitializer extends ChannelInitializer<SocketChannel>{
    
        @Override
        protected void initChannel(SocketChannel ch) throws Exception {
            ch.pipeline().addLast(new SimpleClientHandler());
        }
    }
    private class SimpleClientHandler extends ChannelInboundHandlerAdapter {
        private ByteBuf clientMessage;
    
    
        public SimpleClientHandler() {
        
            byte [] req = "Call-User-Service".getBytes();
            clientMessage = Unpooled.buffer(req.length);
            clientMessage.writeBytes(req);
        }
    
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            ctx.writeAndFlush(clientMessage);
        }
    
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ByteBuf buf = (ByteBuf)msg;
            byte [] req = new byte[buf.readableBytes()];
    
            buf.readBytes(req);
    
            String message = new String(req,"UTF-8");
    
            System.out.println("Netty-Client:Receive Message,"+ message);
        }
    
        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
            ctx.close();
        }
    }
}
