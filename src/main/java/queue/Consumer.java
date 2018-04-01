package queue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * created by zjw
 * 2018/4/1
 */
public class Consumer implements Runnable {
    private boolean isRunning;
    private BlockingQueue<String> queue;
    
    public Consumer(BlockingQueue<String> queue) {
        this.isRunning = true;
        this.queue = queue;
    }
    
    @Override
    public void run() {
       while(isRunning){
           try {
               String product = queue.poll(2, TimeUnit.SECONDS);
               if(product != null){
                   System.out.println(Thread.currentThread().getName() + "消费了一个产品！剩余产品数量："  + queue.size());
                   Thread.sleep((long) (Math.random() * 1000));
               }else{
                   isRunning = false;
               }
           } catch (InterruptedException e) {
               e.printStackTrace();
               Thread.currentThread().interrupt();
           }
       }
    }
}
