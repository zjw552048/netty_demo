package nio.my2;

import netty.demo.NettyClient;
import nio.my.CharsetUtil;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * created by zjw
 * 2018/3/19
 */
public class MyNIOClient2 {
    private boolean isOver = false;
    private ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
    private String ip = "127.0.0.1";
    private int port = 8090;
    private Selector selector;
    
    public MyNIOClient2() throws IOException {
        selector = Selector.open();
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
    
        socketChannel.register(selector, SelectionKey.OP_CONNECT);
        
        socketChannel.connect(new InetSocketAddress(ip, port));
    }
    
    public void listen() throws IOException {
        while(!isOver){
            int num = selector.select();
            Iterator<SelectionKey> it = selector.selectedKeys().iterator();
            while(it.hasNext()){
                SelectionKey key = it.next();
                //TODO 必须移除
                it.remove();
                clientHandler(key);
            }
        }
    }
    
    private void clientHandler(SelectionKey key) throws IOException {
        if(key.isConnectable()){
            SocketChannel socketChannel = (SocketChannel) key.channel();
            if(socketChannel.isConnectionPending()){
                socketChannel.finishConnect();
                socketChannel.register(selector, SelectionKey.OP_READ);
                ByteBuffer buffer = CharsetUtil.encode(CharBuffer.wrap("client: 建立连接成功"));
                while(buffer.hasRemaining()){
                    socketChannel.write(buffer);
                }
            }
        }else if(key.isReadable()){
            SocketChannel socketChannel = (SocketChannel) key.channel();
            byteBuffer.clear();
            int count = socketChannel.read(byteBuffer);
            if(count > 0){
                byteBuffer.flip();
                CharBuffer charBuffer = CharsetUtil.decode(byteBuffer);
                String content = charBuffer.toString();
                System.out.println(content);
            }else{
                socketChannel.close();
                isOver = true;
            }
        }
    }
    
    public static void main(String[] args) {
        try {
            MyNIOClient2 myNIOClient2 = new MyNIOClient2();
            myNIOClient2.listen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
