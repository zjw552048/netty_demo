package netty.time.pojo;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * created by zjw
 * 2018/3/18
 */
public class TimePOJOEncoder2 extends MessageToByteEncoder<TimeBean> {
    @Override
    protected void encode(ChannelHandlerContext ctx, TimeBean msg, ByteBuf out) throws Exception {
        out.writeInt((int) msg.getValue());
    }
}
