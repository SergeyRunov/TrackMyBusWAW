package pl.creativesstudio.models;

import com.google.gson.annotations.SerializedName;
/**
 * Model przedstawiający autobus na mapie.
 * Przechowuje szczegóły dotyczące linii autobusowych, lokalizacji i godziny odjazdu.
 *
 * @version 1.0
 * @since 2024-12-16
 */
public class Bus {

    @SerializedName("Lines")
    private String lines;

    @SerializedName("Lon")
    private double lon;

    @SerializedName("Lat")
    private double lat;

    @SerializedName("Time")
    private String time;

    @SerializedName("VehicleNumber")
    private String vehicleNumber;

    @SerializedName("Brigade")
    private String brigade;

    /**
     * Zwraca linie autobusowe przypisane do autobusu.
     *
     * @return Linie autobusowe.
     */
    // Getters and Setters
    public String getLines() { return lines; }
    /**
     * Ustawia linie autobusowe przypisane do autobusu.
     *
     * @param lines Linie autobusowe.
     */
    public void setLines(String lines) { this.lines = lines; }
    /**
     * Zwraca długość geograficzną autobusu.
     *
     * @return Długość geograficzna.
     */
    public double getLon() { return lon; }
    /**
     * Ustawia długość geograficzną autobusu.
     *
     * @param lon Długość geograficzna.
     */
    public void setLon(double lon) { this.lon = lon; }
    public double getLat() { return lat; }
    public void setLat(double lat) { this.lat = lat; }
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }
    public String getBrigade() { return brigade; }
    public void setBrigade(String brigade) { this.brigade = brigade; }
}
