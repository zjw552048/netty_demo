package netty.time.pojo;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * created by zjw
 * 2018/3/18
 */
public class TimePOJOClient {
    private static String host = "127.0.0.1";
    private static int port = 8080;
    public static void main(String[] args) throws Exception {
        
        EventLoopGroup workerGroup = new NioEventLoopGroup();
    
        try {
            Bootstrap b = new Bootstrap(); // (1)
            b.group(workerGroup); // (2)
            b.channel(NioSocketChannel.class); // (3)
            b.option(ChannelOption.SO_KEEPALIVE, true); // (4)
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new TimePOJODecoder(), new TimePOJOClientHandler());
                }
            });
        
            // 启动客户端
            ChannelFuture f = b.connect(host, port).sync(); // (5)
        
            // 等待连接关闭
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }
}
