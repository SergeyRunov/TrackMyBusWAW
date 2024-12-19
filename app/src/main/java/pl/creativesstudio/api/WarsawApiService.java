
/**
 * @file WarsawApiService.java
 * @brief Interface for the Warsaw bus system API.
 *
 * Defines methods for communicating with the API using Retrofit.
 */
package pl.creativesstudio.api;

import pl.creativesstudio.models.ApiResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
/**
 * @interface WarsawApiService
 * @brief Interface for the Warsaw bus system API.
 *
 * Defines methods for communicating with an external API.
 */
public interface WarsawApiService {
    /**
     * @brief Retrieves data about bus positions.
     *
     * Sends a request to the API and returns a list of bus positions in Warsaw.
     *
     * @param resourceId The API resource ID.
     * @param apiKey The API key required for authorization.
     * @param type The type of transportation (e.g., bus, tram).
     * @param line The bus line identifier. *Optional.*
     * @param brigade The brigade identifier. *Optional.*
     * @return A {@link Call} object containing the API response data.
     */
    @GET("api/action/busestrams_get/")
    Call<ApiResponse> getBuses(
            @Query("resource_id") String resourceId,
            @Query("apikey") String apiKey,
            @Query("type") int type,
            @Query("line") String line,      // Opcjonalny
            @Query("brigade") String brigade // Opcjonalny
    );
}
