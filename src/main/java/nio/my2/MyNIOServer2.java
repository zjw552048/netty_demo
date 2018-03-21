package nio.my2;

import nio.my.CharsetUtil;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;

/**
 * created by zjw
 * 2018/3/19
 */
public class MyNIOServer2 {
    ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
    private Selector selector;
    private int port;
    
    public MyNIOServer2(int port) throws IOException {
        this.port = port;
        selector = Selector.open();
    
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
    
        serverSocketChannel.socket().bind(new InetSocketAddress(port));
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
    
    }
    
    
    public void listen() throws IOException {
        while(true){
            int num = selector.select();
            Iterator<SelectionKey> it = selector.selectedKeys().iterator();
            while(it.hasNext()){
                SelectionKey key = it.next();
                //TODO 必须移除
                it.remove();
                serverHandler(key);
            }
        }
    }
    
    private void serverHandler(SelectionKey key) throws IOException {
        if(key.isAcceptable()){
            ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
            SocketChannel socketChannel = serverSocketChannel.accept();
            //TODO 必须设为非阻塞
            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_READ);
        }else if(key.isReadable()){
            SocketChannel socketChannel = (SocketChannel) key.channel();
            byteBuffer.clear();
            int count = socketChannel.read(byteBuffer);
            if(count > 0){
                //TODO 必须调用flip()
                byteBuffer.flip();
                CharBuffer charBuffer = CharsetUtil.decode(byteBuffer);
                String content = charBuffer.toString();
                System.out.println(content);
                ByteBuffer buffer = CharsetUtil.encode(CharBuffer.wrap("server已收到: " + content));
                while(buffer.hasRemaining()){
                    socketChannel.write(buffer);
                }
            }
        }
    }
    
    public static void main(String[] args) {
        try {
            MyNIOServer2 myNIOServer2 = new MyNIOServer2(8090);
            myNIOServer2.listen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
