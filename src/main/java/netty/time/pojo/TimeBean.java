package netty.time.pojo;

import java.util.Date;

/**
 * created by zjw
 * 2018/3/18
 */
public class TimeBean {
    private final long value;
    
    public TimeBean() {
        this(System.currentTimeMillis() / 1000L + 2208988800L);
    }
    
    public TimeBean(long value) {
        this.value = value;
    }
    
    public long getValue() {
        return value;
    }
    
    @Override
    public String toString() {
        return new Date((getValue() - 2208988800L) * 1000L).toString();
    }
}
