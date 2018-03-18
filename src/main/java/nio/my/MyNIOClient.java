package nio.my;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.CharacterCodingException;
import java.util.Iterator;

/**
 * created by zjw
 * 2018/3/18
 */
public class MyNIOClient {
    private boolean isOver = false;
    private String clientId;
    private String ip;
    private int port;
    private Selector selector;
    private ByteBuffer readBuffer = ByteBuffer.allocate(1024);
    
    public MyNIOClient(String clientId, String ip, int port) throws IOException {
        this.clientId = clientId;
        this.ip = ip;
        this.port = port;
        this.selector = Selector.open();
        
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        
        socketChannel.register(selector, SelectionKey.OP_CONNECT);
        
        socketChannel.connect(new InetSocketAddress(ip, port));
    }
    
    private void listen() throws IOException {
        while (!isOver){
            int num = selector.select();
            if(num > 0){
                Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                while (it.hasNext()){
                    SelectionKey key = it.next();
                    it.remove();
                    keyHandler(key);
                }
            }
        }
    }
    
    private void keyHandler(SelectionKey key) throws IOException {
        if(key.isConnectable()){
            SocketChannel socketChannel = (SocketChannel) key.channel();
            if (socketChannel.isConnectionPending()) {
                socketChannel.finishConnect();
                socketChannel.register(selector, SelectionKey.OP_READ);
                ByteBuffer byteBuffer = CharsetUtil.encode(CharBuffer.wrap("client " + clientId + ",连接成功！"));
                while(byteBuffer.hasRemaining()){
                    socketChannel.write(byteBuffer);
                }
            }
        }else if(key.isReadable()){
            SocketChannel socketChannel = (SocketChannel) key.channel();
            readBuffer.clear();
            int count = socketChannel.read(readBuffer);
            if(count > 0){
                readBuffer.flip();
                CharBuffer charBuffer = CharsetUtil.decode(readBuffer);
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
            MyNIOClient nioClient = new MyNIOClient("zjw", "127.0.0.1", 12345);
            nioClient.listen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
