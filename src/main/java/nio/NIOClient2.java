package nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * created by zjw
 * 2018/3/17
 */
public class NIOClient2 {
    private static InetSocketAddress ip = new InetSocketAddress("localhost",12345);
    private static CharsetEncoder encoder = Charset.forName("GB2312").newEncoder();
    private static CharsetDecoder decoder = Charset.forName("GB2312").newDecoder();
    
    private static class MyRunnable implements Runnable {
        private int id;
        private Selector selector;
        private ByteBuffer buffer = ByteBuffer.allocate(10);
        private boolean isOver = false;
    
        public MyRunnable(int id) throws IOException {
            this.id = id;
            this.selector = getSelector();
        }
    
        protected Selector getSelector() throws IOException {
            Selector sel = Selector.open();
            SocketChannel channel = SocketChannel.open();
            channel.configureBlocking(false);
        
            channel.register(sel, SelectionKey.OP_CONNECT);
            channel.connect(ip);
            return sel;
        }
    
        private void handleKey(SelectionKey key) throws IOException {
            int total = 0;
            if (key.isConnectable()) {
                SocketChannel channel = (SocketChannel) key.channel();
                if (channel.isConnectionPending()) {
                    channel.finishConnect();
                    channel.write(encoder.encode(CharBuffer.wrap("Hello from thread" + id)));
                    channel.register(selector, SelectionKey.OP_READ);
    
                }
            } else if (key.isReadable()) {
                long start = System.currentTimeMillis();
                SocketChannel channel = (SocketChannel) key.channel();
                int count = channel.read(buffer);
                if (count > 0) {
                    buffer.flip();
                    CharBuffer charBuffer = decoder.decode(buffer);
                    System.out.println("receive: " + charBuffer.toString());
                    total += count;
                    buffer.clear();
                } else {
                    channel.close();
                    isOver = true;
                    return;
                }
                double last = (System.currentTimeMillis() - start) * 1.0 / 1000;
                System.out.println("Thread" + id + " download " + total + " bytes in " + last + "s.");
            }
            
        }
    
        @Override
        public void run() {
            try {
                while (!isOver) {
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
    }
    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            int threadPoolSize = 1;
            ExecutorService exec = Executors.newFixedThreadPool(threadPoolSize);
            for(int index = 0;index < threadPoolSize;index++){
                exec.execute(new MyRunnable(index));
            }
            exec.shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
