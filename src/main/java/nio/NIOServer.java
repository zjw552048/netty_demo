package nio;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Iterator;

/**
 * created by zjw
 * 2018/3/17
 */
public class NIOServer {
    private final static int BLOCK_CAPACITY = 1024;
    private final static String FILENAME = "test.txt";
    private final static CharsetDecoder decoder = Charset.forName("GB2312").newDecoder();
    
    private Selector selector;
    private ByteBuffer serverBuffer = ByteBuffer.allocate(BLOCK_CAPACITY);
    
    
    public NIOServer(int port) throws IOException {
        //获取selector
        this.selector = this.getSelector(port);
    }
    
    //获取Selector
    protected Selector getSelector(int port) throws IOException {
        // 创建Selector对象
        Selector sel = Selector.open();
        
        // 创建可选择通道，并配置为非阻塞模式
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        
        // 绑定通道到指定端口
        ServerSocket socket = serverChannel.socket();
        InetSocketAddress address = new InetSocketAddress(port);
        socket.bind(address);
        
        // 向Selector中注册感兴趣的事件
        serverChannel.register(sel, SelectionKey.OP_ACCEPT);
        return sel;
    }
    
    //监听端口
    public void listen() {
        try {
            while (true) {
                // 该调用会阻塞，直到至少有一个事件发生
                int num = selector.select();
                Iterator iter = selector.selectedKeys().iterator();
                while (iter.hasNext()) {
                    SelectionKey key = (SelectionKey) iter.next();
                    iter.remove();
                    handleKey(key);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
    
    //处理事件
    protected void handleKey(SelectionKey key) throws IOException {
        if (key.isAcceptable()){
            //接收请求处理代码
            ServerSocketChannel server = (ServerSocketChannel) key.channel();
            SocketChannel channel = server.accept();
            channel.configureBlocking(false);
            channel.register(selector, SelectionKey.OP_READ);
        } else if (key.isReadable()){
            //读信息处理代码
            SocketChannel channel = (SocketChannel) key.channel();
            serverBuffer.clear();
            int count = channel.read(serverBuffer);
            if (count > 0) {
                serverBuffer.flip();
                CharBuffer charBuffer = decoder.decode(serverBuffer);
                System.out.println("Client>>" + charBuffer.toString());
                SelectionKey wkey = channel.register(selector, SelectionKey.OP_WRITE);
                wkey.attach(new HandleClient());
            } else {
                channel.close();
            }
        } else if (key.isWritable()){
            //写事件处理代码
            SocketChannel channel = (SocketChannel) key.channel();
            HandleClient handle = (HandleClient) key.attachment();
            ByteBuffer block = handle.readBlock();
            if (block != null) {
                channel.write(block);
            } else {
                handle.close();
                channel.close();
            }
        }
    }
    
    //处理与客户端的交互
    public class HandleClient {
        protected FileChannel channel;
        protected ByteBuffer buffer;
        
        public HandleClient() throws IOException {
            this.channel = new FileInputStream(FILENAME).getChannel();
            this.buffer = ByteBuffer.allocate(BLOCK_CAPACITY);
        }
        
        public ByteBuffer readBlock() {
            try {
                buffer.clear();
                int count = channel.read(buffer);
                if (count == -1) {
                    return null;
                }
                buffer.flip();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return buffer;
        }
        
        public void close() {
            try {
                channel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            int port = 12345;
            NIOServer server = new NIOServer(port);
            System.out.println("Listening on " + port);
            while (true) {
                server.listen();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
}
