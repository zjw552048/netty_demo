package test;

import java.util.Arrays;

/**
 * created by zjw
 * 2018/4/2
 */
public class T3 {
    public static int[] removeDuplicates(int a[]) {
        if(a.length <= 1) {
            return a;
        }
        int index = 0;
        for(int i = 1; i < a.length; i++) {
            if(a[index] != a[i]) {
                a[++index] = a[i];
            }
        }
        int[] b = new int[index + 1];
        System.arraycopy(a, 0, b, 0, b.length);
        return b;
    }
    
    public static void main(String[] args) {
        int[] a = {1, 1, 2, 2, 3};
        a = removeDuplicates(a);
        System.out.println(Arrays.toString(a));
    }
}
