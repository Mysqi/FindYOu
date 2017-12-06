package qi.com.findyou.model;

import java.io.Serializable;

/**
 * Created by qi_fu on 2017/11/22.
 */

public class Station implements Serializable{

    /**
     * errcode : 0
     * lat : 40.00490952
     * lon : 116.48304749
     * radius : 242
     * address : 北京市朝阳区崔各庄地区溪阳东路;望京东路与溪阳东路路口东138米
     */

    private int errcode;
    private String lat;
    private String lon;
    private String radius;
    private String address;

    public int getErrcode() {
        return errcode;
    }

    public void setErrcode(int errcode) {
        this.errcode = errcode;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getRadius() {
        return radius;
    }

    public void setRadius(String radius) {
        this.radius = radius;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "Station{" +
                "errcode=" + errcode +
                ", lat='" + lat + '\'' +
                ", lon='" + lon + '\'' +
                ", radius='" + radius + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
