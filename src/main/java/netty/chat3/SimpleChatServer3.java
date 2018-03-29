package netty.chat3;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import netty.chat.SimpleChatServer;

/**
 * created by zjw
 * 2018/3/30
 */
public class SimpleChatServer3 {
    private int port;
    
    public SimpleChatServer3(int port) {
        this.port = port;
    }
    
    public void run() throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try{
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new SimpleChatInitializer3())
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
    
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
    
            channelFuture.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
    public class SimpleChatInitializer3 extends ChannelInitializer<SocketChannel>{
    
        @Override
        protected void initChannel(SocketChannel ch) throws Exception {
            ChannelPipeline channelPipeline = ch.pipeline();
            channelPipeline.addLast("Encoder", new StringEncoder());
            
            channelPipeline.addLast("Delimiter", new DelimiterBasedFrameDecoder(1024, Delimiters.lineDelimiter()));
            channelPipeline.addLast("Decoder", new StringDecoder());
            channelPipeline.addLast("serverHander", new SimpleChatServerHandler3());
        }
    }
    
    public class SimpleChatServerHandler3 extends SimpleChannelInboundHandler<String>{
    
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
            System.out.println(msg);
            ctx.writeAndFlush("Server received: " + msg + "\r\n");
        }
    }
    
    public static void main(String[] args) throws InterruptedException {
        SimpleChatServer3 simpleChatServer3 = new SimpleChatServer3(8080);
        simpleChatServer3.run();
    }
}
