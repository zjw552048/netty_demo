//package netty.chat;
//
//import io.netty.bootstrap.Bootstrap;
//import io.netty.channel.Channel;
//import io.netty.channel.EventLoopGroup;
//import io.netty.channel.nio.NioEventLoopGroup;
//import io.netty.channel.socket.nio.NioSocketChannel;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//
///**
// * created by zjw
// * 2018/3/18
// */
//public class SimpleChatClient2 {
//    private String ip;
//    private int port;
//
//    public SimpleChatClient2(String ip, int port) {
//        this.ip = ip;
//        this.port = port;
//    }
//
//    public void run() throws InterruptedException {
//        EventLoopGroup boss = new NioEventLoopGroup();
//
//        try {
//            Bootstrap bootstrap = new Bootstrap();
//            bootstrap.group(boss).channel(NioSocketChannel.class)
//                                .handler(new SimpleChatClientInitializer());
////                                .option(ChannelOption.SO_KEEPALIVE, true);
//
//            Channel channel = bootstrap.connect(ip, port).sync().channel();
//
//            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
//            while(true){
//                channel.writeAndFlush(in.readLine() + "\r\n");
//            }
////            channelFuture.channel().closeFuture().sync();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            boss.shutdownGracefully();
//        }
//    }
//
//    public static void main(String[] args) {
//        try {
//            SimpleChatClient2 simpleChatClient = new SimpleChatClient2("127.0.0.1", 8080);
//            simpleChatClient.run();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
//}
