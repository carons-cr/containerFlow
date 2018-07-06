package cn.pfms.liust.pfms.outstore.bean;

/**
 *
 * Created by liust on 17-10-11.
 *
 */

public class ListviewOutStored {
    private String rqys;
    private String wpid;
    private String rqxh;
    private int sign;

    public ListviewOutStored() {

    }

    public ListviewOutStored(String rqys, String wpid, String rqxh, int sign) {
        this.rqys = rqys;
        this.wpid = wpid;
        this.rqxh = rqxh;
        this.sign = sign;

    }

    public int getSign() {
        return sign;
    }

    public void setSign(int sign) {
        this.sign = sign;
    }

    public String getRqys() {
        return rqys;
    }

    public void setRqys(String rqys) {
        this.rqys = rqys;
    }

    public String getWpid() {
        return wpid;
    }

    public void setWpid(String wpid) {
        this.wpid = wpid;
    }

    public String getRqxh() {
        return rqxh;
    }

    public void setRqxh(String rqxh) {
        this.rqxh = rqxh;
    }
}
