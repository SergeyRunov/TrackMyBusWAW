package pl.creativesstudio.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;
/**
 * Reprezentuje odpowiedź API Warszawa.
 * Przechowuje dane zwracane przez API, które są wykorzystywane przez aplikację.
 *
 * @version 1.0
 * @since 2024-12-16
 */

public class ApiResponse {
    @SerializedName("result")
    private List<Bus> result;

    /**
     * Zwraca wynik odpowiedzi API.
     *
     * @return Obiekt wynikowy odpowiedzi API.
     */
    public List<Bus> getResult() { return result; }
    /**
     * Ustawia wynik odpowiedzi API.
     *
     * @param result Obiekt wynikowy odpowiedzi API.
     */
    public void setResult(List<Bus> result) { this.result = result; }
}
