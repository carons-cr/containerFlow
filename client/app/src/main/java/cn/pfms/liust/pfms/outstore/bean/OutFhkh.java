package cn.pfms.liust.pfms.outstore.bean;

/**
 * 发货客户
 * Created by liust on 17-10-5.
 */

public class OutFhkh {
    private int id;
    private String khid;
    private String khmc;

    public OutFhkh(int id, String khid, String khmc) {
        this.id = id;
        this.khid = khid;
        this.khmc = khmc;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKhid() {
        return khid;
    }

    public void setKhid(String khid) {
        this.khid = khid;
    }

    public String getKhmc() {
        return khmc;
    }

    public void setKhmc(String khmc) {
        this.khmc = khmc;
    }
}
