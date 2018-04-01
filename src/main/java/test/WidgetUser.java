package test;

/**
 * created by zjw
 * 2018/4/1
 */
public class WidgetUser extends Thread {
    private WidgetMaker maker;
    public WidgetUser(String name,WidgetMaker maker){
        super(name);
        this.maker=maker;
    }
    public void run(){
        Object w = maker.waitForWidget();
        System.out.println(getName()+" got a widget");
    }
    
    
    public static void main(String[] args) {
        WidgetMaker maker=new WidgetMaker("test");
        maker.start();
        new WidgetUser("Lenny",maker).start();
        new WidgetUser("Moe",maker).start();
        new WidgetUser("Curly",maker).start();
        
    }
}
