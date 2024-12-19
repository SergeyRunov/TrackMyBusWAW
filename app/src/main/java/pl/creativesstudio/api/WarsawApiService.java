/**
 * @file WarsawApiService.java
 * @brief Interfejs API warszawskiego systemu autobusowego.
 *
 * Definiuje metody komunikacji z API przy użyciu Retrofit.
 */

package pl.creativesstudio.api;

import pl.creativesstudio.models.ApiResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * @interface WarsawApiService
 * @brief Interfejs API warszawskiego systemu autobusowego.
 *
 * Definiuje metody do komunikacji z zewnętrznym API.
 */

public interface WarsawApiService {

    @GET("api/action/busestrams_get/")

    /**
     * @brief Pobiera dane o pozycjach autobusów.
     *
     * Wysyła żądanie do API i zwraca listę pozycji autobusów w Warszawie.
     *
     * @param resourceId ID zasobu API.
     * @param apiKey Klucz API wymagany do autoryzacji.
     * @return Obiekt Call zawierający dane odpowiedzi API.
     */
    
    Call<ApiResponse> getBuses(
            @Query("resource_id") String resourceId,
            @Query("apikey") String apiKey,
            @Query("type") int type,
            @Query("line") String line,      // Opcjonalny
            @Query("brigade") String brigade // Opcjonalny
    );
}
