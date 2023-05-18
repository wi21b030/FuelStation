package org.example;

public class Station {

    private static int id = 0;
    private static String db_url = null;
    private static float lat;
    private static float lng;


    public Station(int id, String db_url, float lat, float lng) {
        this.id = id;
        this.db_url = db_url;
        this.lat = lat;
        this.lng = lng;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return db_url;
    }

    public void setUrl(String db_url) {
        this.db_url = db_url;
    }

    public float getLat() {
        return lat;
    }

    public void setLat(float lng) {
        this.lng = lng;
    }

    public float getLng() {
        return lng;
    }

    public void setLng(float lng) {
        this.lng = lng;
    }

    @Override
    public String toString() {
        return "Station{id='"+ id + "', db_url='" + db_url + "', lat='" + lat + "', lng='" + lng + "'}\n";
    }
}
