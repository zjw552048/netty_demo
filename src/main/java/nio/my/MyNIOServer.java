package nio.my;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;

/**
 * created by zjw
 * 2018/3/18
 */
public class MyNIOServer {
    ByteBuffer readBuffer = ByteBuffer.allocate(10);
    private Selector selector;
    
    public MyNIOServer(int port) throws IOException {
        this.selector = Selector.open();
        
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
    
        serverSocketChannel.socket().bind(new InetSocketAddress(port));
    
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
    }
    
    public void listen() throws IOException {
        while (true){
            int num = selector.select();
            if(num > 0 ){
                Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                while(it.hasNext()){
                    SelectionKey key = it.next();
                    it.remove();
                    keyHandler(key);
                }
            }
        }
    }
    
    private void keyHandler(SelectionKey key) throws IOException {
        if(key.isAcceptable()){
            ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
            SocketChannel socketChannel = serverSocketChannel.accept();
            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_READ);
        }else if(key.isReadable()){
            SocketChannel socketChannel = (SocketChannel) key.channel();
            readBuffer.clear();
            int count = socketChannel.read(readBuffer);
            if(count > 0){
                readBuffer.flip();
                CharBuffer charBuffer = CharsetUtil.decode(readBuffer);
                String content = charBuffer.toString();
                System.out.println(content);
                //答复客户端
                ByteBuffer byteBuffer = CharsetUtil.encode(CharBuffer.wrap("已收到：" + content));
                while(byteBuffer.hasRemaining()){
                    socketChannel.write(byteBuffer);
                }
//                socketChannel.close();
            }else{
            
            }
        }else if(key.isWritable()){
            //TODO
        }
    }
    
    public static void main(String[] args) {
        try {
            int port = 12345;
            MyNIOServer myNioServer = new MyNIOServer(port);
            myNioServer.listen();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] strings = new String[]{"a", "b", "c"};
        List<String> list = new ArrayList<>(strings.length);
        Collections.addAll(list, strings);
    }
}
