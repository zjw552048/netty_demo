package test;

import java.util.ArrayList;
import java.util.List;

/**
 * created by zjw
 * 2018/4/1
 */
public class WidgetMaker extends Thread {
    List<Object> finishedWidgets=new ArrayList<Object>();
    
    public WidgetMaker(String name) {
        super(name);
    }
    
    public void run(){
        try{
            while(true){
                Thread.sleep(5000);//act busy
                Object o = new Object();
                //也就是说需要5秒钟才能新产生一个Widget，这决定了一定要用notify而不是notifyAll
                //因为上面两行代码不是同步的，如果用notifyAll则所有线程都企图冲出wait状态
                //第一个线程得到了锁，并取走了Widget（这个过程的时间小于5秒，新的Widget还没有生成）
                //并且解开了锁，然后第二个线程获得锁(因为用了notifyAll其他线程不再等待notify语句
                //，而是等待finishedWidgets上的锁，一旦锁放开了，他们就会竞争运行)，运行
                //finishedWidgets.remove(0)，但是由于finishedWidgets现在还是空的，
                //于是产生异常
                //***********这就是为什么下面的那一句不能用notifyAll而是要用notify
                
                synchronized(finishedWidgets){
                    System.out.println("added");
                    finishedWidgets.add(o);
                    finishedWidgets.notifyAll(); //这里只能是notify而不能是notifyAll
                }
            }
        }
        catch(InterruptedException e){}
    }
    
    public Object waitForWidget(){
        System.out.println(this);
        System.out.println(this.toString());
        synchronized(finishedWidgets){
            System.out.println(finishedWidgets.hashCode());
            System.out.println(Thread.currentThread().getName() + " in");
            if(finishedWidgets.size()==0){
                try{
                    finishedWidgets.wait();
                }
                catch(InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return finishedWidgets.remove(0);
        }
    }
}
