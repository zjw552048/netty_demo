package nio.my3;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;

/**
 * created by zjw
 * 2018/3/29
 */
public class MyNIOServer3 {
    private ByteBuffer byteBuffer;
    private int port;
    private Selector selector;
    
    public MyNIOServer3(int capacity, int port) throws IOException {
        this.byteBuffer = ByteBuffer.allocate(capacity);
        this.port = port;
        
        selector = Selector.open();
        
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        
        serverSocketChannel.socket().bind(new InetSocketAddress(port));
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
    }
    
    public void run() throws IOException {
        while(true){
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
        if(key.isAcceptable()){
            ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
            SocketChannel socketChannel = serverSocketChannel.accept();
            socketChannel.configureBlocking(false);
            
            socketChannel.register(selector, SelectionKey.OP_READ);
        }else if(key.isReadable()){
            SocketChannel socketChannel = (SocketChannel) key.channel();
            byteBuffer.clear();
            int count = socketChannel.read(byteBuffer);
            if(count>0){
                byteBuffer.flip();
                String content = Charset.forName("utf-8").decode(byteBuffer).toString();
                socketChannel.register(selector, SelectionKey.OP_READ);
                ByteBuffer buffer = Charset.forName("utf-8").encode("message received: " + content);
                System.out.println(content);
                while (buffer.hasRemaining()){
                    socketChannel.write(buffer);
                }
            }
        }else if(key.isWritable()){
        
        }
    }
    
    public static void main(String[] args) {
        try {
            MyNIOServer3 server3 = new MyNIOServer3(128, 8080);
            server3.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
