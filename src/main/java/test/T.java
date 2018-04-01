package test;

import java.util.Random;

/**
 * created by zjw
 * 2018/3/24
 */
public class T {
    private static T singleTon = new T();
    public static int count1;
    public static int count2 = 0;
    
    private T() {
        count1++;
        count2++;
    }
    
    public static T getInstance() {
        return singleTon;
    }
    
    public static void main(String[] args) {
        T singleTon = T.getInstance();
        System.out.println("count1=" + singleTon.count1);
        System.out.println("count2=" + singleTon.count2);
        
        Random random = new Random();
        System.out.println(random.nextInt());
        System.out.println(random.nextInt(10));
    
        System.out.println(Math.random());
    }
}
