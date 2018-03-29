package nio.my3;

import nio.my.CharsetUtil;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;

/**
 * created by zjw
 * 2018/3/29
 */
public class MyNIOClient3 {
    private ByteBuffer byteBuffer;
    private String address;
    private int port;
    private boolean isOver = false;
    private Selector selector;
    
    public MyNIOClient3(int capacity, String address, int port) throws IOException {
        this.byteBuffer = ByteBuffer.allocate(capacity);
        this.address = address;
        this.port = port;
        
        selector = Selector.open();
    
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        
        socketChannel.register(selector, SelectionKey.OP_CONNECT);
        socketChannel.connect(new InetSocketAddress(address, port));
    }
    
    public void run() throws IOException {
        while (!isOver){
            int count = selector.select();
            Iterator<SelectionKey> it = selector.selectedKeys().iterator();
            while (it.hasNext()){
                SelectionKey key = it.next();
                it.remove();
                handler(key);
            }
        }
    }
    
    public void handler(SelectionKey key) throws IOException {
        if(key.isConnectable()){
            SocketChannel socketChannel = (SocketChannel) key.channel();
            if(socketChannel.isConnectionPending()){
                socketChannel.finishConnect();
                socketChannel.register(selector, SelectionKey.OP_READ);
                ByteBuffer buffer = CharsetUtil.encode(CharBuffer.wrap("Connected!"));
                while(buffer.hasRemaining()){
                    socketChannel.write(buffer);
                }
            }
        }else if(key.isReadable()){
            SocketChannel socketChannel = (SocketChannel) key.channel();
            byteBuffer.clear();
            int count = socketChannel.read(byteBuffer);
            if(count>0){
                byteBuffer.flip();
                String content = CharsetUtil.decode(byteBuffer).toString();
                System.out.println(content);
//                socketChannel.register(selector, SelectionKey.OP_READ);
            }else{
                //isOver = true;
                //socketChannel.close();
            }
        }else if(key.isWritable()){
        
        }
    }
    public static void main(String[] args) throws IOException {
        MyNIOClient3 client3 = new MyNIOClient3(128, "127.0.0.1", 8080);
        client3.run();
    }
}
