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
public class NIOClient {
    private static InetSocketAddress ip = new InetSocketAddress("localhost",12345);
    private static CharsetEncoder encoder = Charset.forName("GB2312").newEncoder();
    private static CharsetDecoder decoder = Charset.forName("GB2312").newDecoder();
    
    private static class MyRunnable implements Runnable{
        int id;
        
        public MyRunnable(int id){
            this.id = id;
        }
        
        @Override
        public void run() {
            try {
                long start = System.currentTimeMillis();
                Selector selector = Selector.open();
                
                SocketChannel client = SocketChannel.open();
                client.configureBlocking(false);
                
                client.register(selector, SelectionKey.OP_CONNECT);
                client.connect(ip);
                
                ByteBuffer buffer = ByteBuffer.allocate(10);
                int total = 0;
                FOR: for(;;){
                    int num = selector.select();
                    Iterator iter = selector.selectedKeys().iterator();
                    while(iter.hasNext()){
                        SelectionKey key = (SelectionKey) iter.next();
                        iter.remove();
                        if(key.isConnectable()){
                            SocketChannel channel = (SocketChannel) key.channel();
                            if(channel.isConnectionPending()){
                                channel.finishConnect();
                                channel.write(encoder.encode(CharBuffer.wrap("Hello from "+id)));
                                channel.register(selector, SelectionKey.OP_READ);
                                
                            }
                        }else if (key.isReadable()){
                            SocketChannel channel = (SocketChannel) key.channel();
                            int count = channel.read(buffer);
                            if(count > 0){
                                buffer.flip();
                                CharBuffer charBuffer = decoder.decode(buffer);
                                System.out.println("receive: " + charBuffer.toString());
                                total += count;
                                buffer.clear();
                            }else{
                                client.close();
                                break FOR;
                            }
                        }
                    }
                    
                    double last = (System.currentTimeMillis()-start)*1.0/1000;
                    System.out.println("Thread"+id+" download "+total+" bytes in "+last+"s.");
                    
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
        int threadPoolSize = 1;
        ExecutorService exec = Executors.newFixedThreadPool(threadPoolSize);
        for(int index = 0;index < threadPoolSize;index++){
            exec.execute(new MyRunnable(index));
        }
        exec.shutdown();
    }
    
}
