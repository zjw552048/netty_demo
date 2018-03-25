package netty.echo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * created by zjw
 * 2018/3/18
 */
public class EchoClientHandler extends ChannelInboundHandlerAdapter {
    
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        for (int i = 0; i < 100; i++) {
            byte[] message = ("hello,Server. NO." + i + "\n").getBytes();
            ByteBuf firstMessage = Unpooled.buffer(message.length);
            firstMessage.clear();
            firstMessage.writeBytes(message);
            ctx.writeAndFlush(firstMessage);
        }
    }
    
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        System.out.println(msg);
        ctx.write(msg);
    }
    
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {         // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }
}
