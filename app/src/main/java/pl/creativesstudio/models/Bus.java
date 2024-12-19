package pl.creativesstudio.models;

import com.google.gson.annotations.SerializedName;

/**
 * @file Bus.java
 * @brief Represents a bus in the TrackMyBusWAW application.
 *
 * This class stores details about a bus, including its lines, location, departure time, vehicle number, and brigade.
 *
 * @version 1.0
 * @since 2024-12-16
 */
public class Bus {
    /**
     * The bus lines assigned to this bus.
     */
    @SerializedName("Lines")
    private String lines;

    /**
     * The geographical longitude of the bus.
     */
    @SerializedName("Lon")
    private double lon;
    /**
     * The geographical latitude of the bus.
     */
    @SerializedName("Lat")
    private double lat;
    /**
     * The departure time of the bus.
     */
    @SerializedName("Time")
    private String time;
    /**
     * The vehicle number of the bus.
     */
    @SerializedName("VehicleNumber")
    private String vehicleNumber;
    /**
     * The brigade to which the bus belongs.
     */
    @SerializedName("Brigade")
    private String brigade;

    /**
     * Retrieves the bus lines assigned to this bus.
     *
     * @return A {@link String} representing the bus lines.
     */
    public String getLines() { return lines; }
    /**
     * Sets the bus lines assigned to this bus.
     *
     * @param lines A {@link String} representing the bus lines.
     */
    public void setLines(String lines) { this.lines = lines; }
    /**
     * Retrieves the geographical longitude of the bus.
     *
     * @return A {@link double} representing the longitude.
     */
    public double getLon() { return lon; }
    /**
     * Sets the geographical longitude of the bus.
     *
     * @param lon A {@link double} representing the longitude.
     */
    public void setLon(double lon) { this.lon = lon; }
    /**
     * Retrieves the geographical latitude of the bus.
     *
     * @return A {@link double} representing the latitude.
     */
    public double getLat() { return lat; }
    /**
     * Sets the geographical latitude of the bus.
     *
     * @param lat A {@link double} representing the latitude.
     */
    public void setLat(double lat) { this.lat = lat; }
    /**
     * Retrieves the departure time of the bus.
     *
     * @return A {@link String} representing the departure time.
     */
    public String getTime() { return time; }
    /**
     * Sets the departure time of the bus.
     *
     * @param time A {@link String} representing the departure time.
     */
    public void setTime(String time) { this.time = time; }
    /**
     * Retrieves the vehicle number of the bus.
     *
     * @return A {@link String} representing the vehicle number.
     */
    public String getVehicleNumber() { return vehicleNumber; }
    /**
     * Sets the vehicle number of the bus.
     *
     * @param vehicleNumber A {@link String} representing the vehicle number.
     */
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }
    /**
     * Retrieves the brigade to which the bus belongs.
     *
     * @return A {@link String} representing the brigade.
     */
    public String getBrigade() { return brigade; }
    /**
     * Sets the brigade to which the bus belongs.
     *
     * @param brigade A {@link String} representing the brigade.
     */
    public void setBrigade(String brigade) { this.brigade = brigade; }
}
