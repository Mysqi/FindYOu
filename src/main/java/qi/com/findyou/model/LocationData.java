package qi.com.findyou.model;

import java.io.Serializable;

/**
 * Created by qi_fu on 2017/11/19.
 */

public class LocationData implements Serializable
{
    private double latitude;
    private double longitude;
    private String direction;
    private String accuracy;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(String accuracy) {
        this.accuracy = accuracy;
    }

    @Override
    public String toString() {
        return "LocationData{" +
                "latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", direction='" + direction + '\'' +
                ", accuracy='" + accuracy + '\'' +
                '}';
    }
}
