package netty.chat;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * created by zjw
 * 2018/3/18
 */
public class SimpleChatServerHandler extends SimpleChannelInboundHandler<String> {
    public static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        channelGroup.add(channel);
        System.out.println("[server] - [ " + channel.remoteAddress() + " ],加入......");
    }
    
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        // A closed Channel is automatically removed from ChannelGroup,
        // so there is no need to do "channelGroup.remove(ctx.channel());"
//        channelGroup.remove(channel);
        System.out.println("[server] - [ " + channel.remoteAddress() + " ],离开......");
        
    }
    
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        Channel currentChannel = ctx.channel();
        for(Channel channel:channelGroup){
            if(channel != currentChannel){
                channel.writeAndFlush("[ " + channel.remoteAddress() + " ]: " + msg + "\n");
            }else{
                channel.writeAndFlush("[ you ]: " + msg + "\n");
            }
        }
    }
    
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        System.out.println("[client] - [ " + channel.remoteAddress() + " ]: 上线...");
    }
    
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        System.out.println("[client] - [ " + channel.remoteAddress() + " ]: 下线...");
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Channel channel = ctx.channel();
        System.out.println("[client] - [ " + channel.remoteAddress() + " ]: 发生异常，断开连接...");
        cause.printStackTrace();
        ctx.close();
    }
}
