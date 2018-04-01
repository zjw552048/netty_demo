package queue;

import java.util.concurrent.BlockingQueue;

/**
 * created by zjw
 * 2018/4/1
 */
public class Producer implements Runnable {
    private BlockingQueue<String> queue;
    private boolean isRunning;
    
    public Producer(BlockingQueue<String> queue) {
        this.isRunning = true;
        this.queue = queue;
    }
    
    @Override
    public void run() {
        while(isRunning){
            String product = "Made by " + Thread.currentThread().getName();
            try {
                queue.put(product);
                System.out.println(Thread.currentThread().getName() + "生产了一个产品！余产品数量： " + queue.size());
                Thread.sleep((long) (Math.random() * 1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
    }
    
    public void stop(){
        isRunning = false;
    }
}
