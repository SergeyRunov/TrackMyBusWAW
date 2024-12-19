/**
 * @file ApiResponse.java
 * @brief Represents the response from the Warsaw bus system API.
 *
 * This class stores the data returned by the API, specifically a list of buses.
 *
 * @version 1.0
 * @since 2024-12-16
 */
package pl.creativesstudio.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;
/**
 * Represents the response from the Warsaw bus system API.
 * Stores the data returned by the API, specifically a list of buses.
 *
 * @version 1.0
 * @since 2024-12-16
 */
public class ApiResponse {
    /**
     * The list of buses returned by the API.
     */
    @SerializedName("result")
    private List<Bus> result;
    /**
     * Retrieves the list of buses from the API response.
     *
     * @return A list of {@link Bus} objects representing the buses.
     */

    public List<Bus> getResult() { return result; }
    /**
     * Sets the list of buses in the API response.
     *
     * @param result A list of {@link Bus} objects to set as the API response.
     */
    public void setResult(List<Bus> result) { this.result = result; }
}
