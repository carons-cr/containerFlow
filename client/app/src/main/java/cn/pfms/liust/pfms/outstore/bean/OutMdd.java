package cn.pfms.liust.pfms.outstore.bean;

/**
 * 库存的信息
 * 既是目的地，又是库存的
 * Created by liust on 17-10-5.
 */

public class OutMdd {
    private int id;
    private String kcdid;
    private String kcdmc;

    public OutMdd(int id, String kcdid, String kcdmc) {
        this.id = id;
        this.kcdid = kcdid;
        this.kcdmc = kcdmc;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKcdid() {
        return kcdid;
    }

    public void setKcdid(String kcdid) {
        this.kcdid = kcdid;
    }

    public String getKcdmc() {
        return kcdmc;
    }

    public void setKcdmc(String kcdmc) {
        this.kcdmc = kcdmc;
    }
}
