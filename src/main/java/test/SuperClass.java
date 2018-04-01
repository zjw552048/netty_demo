package test;

/**
 * created by zjw
 * 2018/4/1
 */
public class SuperClass {
    static void staticMethod(){
        System.out.println("SuperClass - staticMethod");
    }
    
    void nonStaticMethod(){
        System.out.println("SuperClass - nonStaticMethod");
    }
    
    public static void main(String[] args) {
        SuperClass bean = new ChildClass();
        bean.nonStaticMethod();
        bean.staticMethod();
    
        ChildClass bean2 = new ChildClass();
        bean2.nonStaticMethod();
        bean2.staticMethod();
    
        SuperClass bean3 = (SuperClass) bean2;
        bean3.nonStaticMethod();
        bean3.staticMethod();
    }
}

class ChildClass extends SuperClass {
    
    static void staticMethod(){
        System.out.println("ChildClass - staticMethod");
    }
    
    @Override
    void nonStaticMethod() {
        System.out.println("ChildClass - nonStaticMethod");
    }
}
