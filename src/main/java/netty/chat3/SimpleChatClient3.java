package netty.chat3;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import netty.chat.SimpleChatClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.Buffer;

/**
 * created by zjw
 * 2018/3/30
 */
public class SimpleChatClient3 {
    private String address;
    private int port;
    
    public SimpleChatClient3(String address, int port) {
        this.address = address;
        this.port = port;
    }
    
    public void run() throws InterruptedException, IOException {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new SimpleChatClientInitializer3());
            
            ChannelFuture channelFuture = bootstrap.connect(address, port).sync();
    
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            
            while(true){
                channelFuture.channel().writeAndFlush(reader.readLine() + "\r\n");
            }
            
//            channelFuture.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
        
        
    }
    
    public class SimpleChatClientInitializer3 extends ChannelInitializer<SocketChannel>{
    
        @Override
        protected void initChannel(SocketChannel ch) throws Exception {
            ChannelPipeline channelPipeline = ch.pipeline();
            channelPipeline.addLast(new StringEncoder());
            
            channelPipeline.addLast(new DelimiterBasedFrameDecoder(1024, Delimiters.lineDelimiter()));
            channelPipeline.addLast(new StringDecoder());
            channelPipeline.addLast(new SimpleChatClientHander3());
        }
    }
    
    public class SimpleChatClientHander3 extends SimpleChannelInboundHandler<String>{
    
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
            System.out.println(msg);
            ctx.writeAndFlush("client send: " + msg + "\r\n");
        }
    }
    
    public static void main(String[] args) throws IOException, InterruptedException {
        SimpleChatClient3 simpleChatClient3 = new SimpleChatClient3("127.0.0.1", 8080);
        simpleChatClient3.run();
    }
}
