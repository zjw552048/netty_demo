package test;

import com.sun.org.apache.xpath.internal.SourceTree;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * created by zjw
 * 2018/3/25
 */
public class T2 {
//    private String a = "a";
//    private String b = "b";
//    private String c = a + b;
//    private final static String aaa = "aaa";
    private static int i = 0;
    private final Map<String, Object> map = new ConcurrentHashMap<>();
    
    static {
        System.out.println("execute static Code");
    }
    public static void main(String[] args) {
        T2 t = new T2();
//        String aa = "aa";
//        String bb = "bb";
//        String cc = "aabb";
//        String dd = aa + bb;
//        System.out.println(cc == dd);
    }
}
