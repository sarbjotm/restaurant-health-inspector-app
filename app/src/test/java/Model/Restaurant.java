package Model;

public class Restaurant {
    private String trackingNUmber;
    private String restaurantName;
    private String address;
    private String city;
    private String factType;
    private double Latitude;
    private double longitude;

    public Restaurant(String trackingNUmber, String restaurantName, String address, String city, String factType, double latitude, double longitude) {
        this.trackingNUmber = trackingNUmber;
        this.restaurantName = restaurantName;
        this.address = address;
        this.city = city;
        this.factType = factType;
        Latitude = latitude;
        this.longitude = longitude;
    }

    public String getTrackingNUmber() {
        return trackingNUmber;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getFactType() {
        return factType;
    }

    public double getLatitude() {
        return Latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
