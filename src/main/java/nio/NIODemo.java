package nio;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * created by zjw
 * 2018/3/17
 */
public class NIODemo {
    public static void main(String[] args) throws Exception {
    
        //内存映射文件I/O缓存
        mappedBufferTest(10);
//        //直接缓冲区
//        directBufferTet(10);
//        //只读缓冲区
//        readOnlyBufferTest(10);
//        //缓冲区分片
//        sliceBufferTest(10);
//        //缓冲区的分配
//        wrapBufferTest(100);
//        //写入测试
//        writeTest(100);
//        //测试读取
//        readTest(100);
    }
    
    private static void mappedBufferTest(int capacity) throws IOException {
        //使用RandomAccessFile类，进行读写
        RandomAccessFile raf = new RandomAccessFile( "test.txt", "rw" );
        FileChannel fc = raf.getChannel();
        
        int start = 0;
        int size = 1024;
        MappedByteBuffer mbb = fc.map( FileChannel.MapMode.READ_WRITE, start, size );
    
        mbb.put( 0, (byte)97 );
        mbb.put( 1023, (byte)122 );
    
        raf.close();
    }
    
    private static void directBufferTet(int capacity) throws IOException {
        /**
         * 使用allocateDirect，而不是allocate.
         * 它会在每一次调用底层操作系统的本机I/O操作之前(或之后)，
         * 尝试避免将缓冲区的内容拷贝到一个中间缓冲区中 或者从一个中间缓冲区中拷贝数据。
         */
        ByteBuffer buffer = ByteBuffer.allocateDirect(capacity);
        buffer.get(new byte[]{});
        File file = new File("test.txt");
        FileInputStream fis = new FileInputStream( file );
        FileChannel readChannel = fis.getChannel();
        
        File fileCopy = new File("test_copy.txt");
        if(fileCopy.exists()){
            fileCopy.createNewFile();
        }
        FileOutputStream fos = new FileOutputStream( fileCopy );
        FileChannel writeChannel = fos.getChannel();
    
    
        while (true) {
            buffer.clear();
        
            int r = readChannel.read( buffer );
        
            if (r==-1) {
                break;
            }
        
            buffer.flip();
    
            writeChannel.write( buffer );
        }
        fis.close();
        fos.close();
    }
    
    private static void readOnlyBufferTest(int capacity){
        ByteBuffer buffer = ByteBuffer.allocate(capacity);
        for (int i = 0; i < buffer.capacity(); i++) {
            buffer.put((byte) i);
        }
        //创建只读缓冲区
        ByteBuffer readOnlyBuffer = buffer.asReadOnlyBuffer();
        // 改变原缓冲区的内容
        for (int i=0; i<buffer.capacity(); ++i) {
            byte b = buffer.get( i );
            b *= 10;
            buffer.put( i, b );
        }
    
        readOnlyBuffer.flip();
    
        // 只读缓冲区的内容也随之改变
        while (readOnlyBuffer.remaining()>0) {
            System.out.println( readOnlyBuffer.get());
        }
        //尝试更改只读缓冲区，抛出ReadOnlyBufferException
        readOnlyBuffer.put((byte) 1);
    }
    
    private static void sliceBufferTest(int capacity){
        ByteBuffer buffer = ByteBuffer.allocate(capacity);
        for (int i = 0; i < buffer.capacity(); i++) {
            buffer.put((byte) i);
        }
        System.out.println(buffer.position());
        System.out.println(buffer.limit());
        // 创建子缓冲区,基于当前position及limit创建，含头不含尾
        buffer.position(3);
        buffer.limit(7);
        /**
         * 缓冲区分片内容互相影响，但是position、limit、mark互相独立
         * Changes to this buffer's content will be visible in the new
         * buffer, and vice versa; the two buffers' position, limit, and mark
         * values will be independent.
         */
        ByteBuffer sliceBuffer = buffer.slice();
        System.out.println(buffer.position());
        System.out.println(buffer.limit());
        System.out.println(sliceBuffer.position());
        System.out.println(sliceBuffer.limit());
        for (int i = 0; i < sliceBuffer.capacity(); i++) {
            /**
             * 需要注意的是ByteBuffer.get()与ByteBuffer.get(int index)的区别，后者会使 position+1
             * 前者：Reads the byte at this buffer's current position, and then increments the position.
             * 后者：Reads the byte at the given index.
             */
            byte b = (byte) (sliceBuffer.get(i) * 10);
            sliceBuffer.put(b);
        }
        //这里需要手动控制limit，buffer.flip()指挥将limit移动到当前position位置，即3
        buffer.position( 0 );
        buffer.limit( buffer.capacity() );
        for (int i = 0; i < buffer.capacity(); i++) {
            System.out.println(buffer.get());
        }
    }
    
    private static void wrapBufferTest(int capacity) throws IOException {
        // 分配指定大小的缓冲区
        ByteBuffer buffer1 = ByteBuffer.allocate(capacity);
    
        // 包装一个现有的数组
        byte[] bytes = new byte[]{1,2,3,4,5,6,7,8,9,0};
        /**
         * 参考wrap方法的注释
         * modifications to the buffer will cause the array to be modified
         * and vice versa.  The new buffer's capacity and limit will be
         * <tt>array.length</tt>, its position will be zero, and its mark will be
         * undefined.
         */
        ByteBuffer buffer2 = ByteBuffer.wrap(bytes);
    }
    
    private static void writeTest(int capacity) throws IOException {
        File file = new File("test.txt");
        FileOutputStream fos = new FileOutputStream(file, true);
        FileChannel fileChannel = fos.getChannel();
        ByteBuffer buffer = ByteBuffer.allocate(capacity);
        //将要写入的内容放入缓存区
        byte[] text = "\nchange line and write a word.".getBytes();
        for (int i = 0; i < text.length; i++) {
            buffer.put(text[i]);
        }
        //将缓存准备就绪
        buffer.flip();
        //将缓存内容写入
        fileChannel.write(buffer);
        fos.close();
    }
    
    private static void readTest(int capacity) throws IOException {
        File file = new File("test.txt");
        FileInputStream fis = new FileInputStream(file);
        FileChannel fileChannel = fis.getChannel();
        ByteBuffer buffer = ByteBuffer.allocate(capacity);
        //将内容读取到缓存
        fileChannel.read(buffer);
        //将缓存准备就绪
        buffer.flip();
        while (buffer.remaining() > 0){
            System.out.print((char) buffer.get());
        }
        fis.close();
    }
}
