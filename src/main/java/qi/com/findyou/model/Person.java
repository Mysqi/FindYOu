package qi.com.findyou.model;

import java.io.Serializable;

/**
 * Created by qi_fu on 2017/11/20.
 */

public class Person implements Serializable{
    private String id;
    private String name;//姓名
    private String weight;//体重
    private String height;//身高
    private Long reporttime;//位置上报时间
    private char infotype;//位置信息类型
    private int warntype;//告警类型
    private Long warntime;//告警时间
    private Long monitortime;//监控时间
    private String heartratevalue;//心率
    private double longitude;
    private double latitude;
    private int sp;
    private float di;
    private char sat;
    private String lbs;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public Long getReporttime() {
        return reporttime;
    }

    public void setReporttime(Long reporttime) {
        this.reporttime = reporttime;
    }

    public char getInfotype() {return infotype;}

    public void setInfotype(char infotype) {
        this.infotype = infotype;
    }

    public int getWarntype() {
        return warntype;
    }

    public void setWarntype(int warntype) {
        this.warntype = warntype;
    }

    public Long getWarntime() {
        return warntime;
    }

    public void setWarntime(Long warntime) {
        this.warntime = warntime;
    }

    public Long getMonitortime() {
        return monitortime;
    }

    public void setMonitortime(Long monitortime) {
        this.monitortime = monitortime;
    }

    public String getHeartratevalue() {
        return heartratevalue;
    }

    public void setHeartratevalue(String heartratevalue) {
        this.heartratevalue = heartratevalue;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public int getSp() {
        return sp;
    }

    public void setSp(int sp) {
        this.sp = sp;
    }

    public float getDi() {
        return di;
    }

    public void setDi(float di) {
        this.di = di;
    }

    public char getSat() {
        return sat;
    }

    public void setSat(char sat) {
        this.sat = sat;
    }

    public String getLbs() {
        return lbs;
    }

    public void setLbs(String lbs) {
        this.lbs = lbs;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", weight='" + weight + '\'' +
                ", height='" + height + '\'' +
                ", reporttime=" + reporttime +
                ", infotype=" + infotype +
                ", warntype=" + warntype +
                ", warntime=" + warntime +
                ", monitortime=" + monitortime +
                ", heartratevalue='" + heartratevalue + '\'' +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                ", sp=" + sp +
                ", di=" + di +
                ", sat=" + sat +
                ", lbs='" + lbs + '\'' +
                '}';
    }
}
