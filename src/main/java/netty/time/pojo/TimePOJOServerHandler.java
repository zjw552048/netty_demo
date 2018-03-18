package netty.time.pojo;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * created by zjw
 * 2018/3/18
 */
public class TimePOJOServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        TimeBean timeBean = new TimeBean();
    
        final ChannelFuture f = ctx.writeAndFlush(timeBean); // (3)
        //代替备注是的代码
        f.addListener(ChannelFutureListener.CLOSE);
//        f.addListener(new ChannelFutureListener() {
//            @Override
//            public void operationComplete(ChannelFuture future) {
//                assert f == future;
//                ctx.close();
//            }
//        }); // (4)
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
    
    public static void main(String[] args) {
        System.out.println((System.currentTimeMillis() / 1000L + 2208988800L));
    }
}
