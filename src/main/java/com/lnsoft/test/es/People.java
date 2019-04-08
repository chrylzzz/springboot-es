package com.lnsoft.test.es;

import java.io.Serializable;

/**
 * Created By Chr on 2019/3/16/0016.
 */
public class People implements Serializable {
    private static final long serialVersionUID = 3005604807209505337L;
    //微信号
    private String wxNo;
    //性别
    private String sax;
    //名字
    private String name;
    //坐标
//    double[] pointLat;
//    double[] pointLon;
    private double pointLat;
    private double pointLon;

    public People() {
    }

    public People(String wxNo, String sax, String name, double pointLat, double pointLon) {
        this.wxNo = wxNo;
        this.sax = sax;
        this.name = name;
        this.pointLat = pointLat;
        this.pointLon = pointLon;
    }

    public String getWxNo() {
        return wxNo;
    }

    public void setWxNo(String wxNo) {
        this.wxNo = wxNo;
    }

    public String getSax() {
        return sax;
    }

    public void setSax(String sax) {
        this.sax = sax;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPointLat() {
        return pointLat;
    }

    public void setPointLat(double pointLat) {
        this.pointLat = pointLat;
    }

    public double getPointLon() {
        return pointLon;
    }

    public void setPointLon(double pointLon) {
        this.pointLon = pointLon;
    }
}
