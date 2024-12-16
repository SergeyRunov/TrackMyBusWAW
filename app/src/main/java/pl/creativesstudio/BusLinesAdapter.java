/**
 * @file BusLinesAdapter.java
 * @brief Adapter listy linii autobusowych.
 *
 * Plik zarządza wyświetlaniem listy linii autobusowych w aplikacji.
 */


package pl.creativesstudio;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Adapter odpowiedzialny za wyświetlanie listy linii autobusowych
 * w interfejsie użytkownika.
 * Obsługuje widoki dla RecyclerView.
 *
 * @author Serhii
 * @version 1.0
 * @since 2024-12-16
 */
public class BusLinesAdapter extends RecyclerView.Adapter<BusLinesAdapter.ViewHolder> {
    public interface ViewHolder {
    }

    public class BusLinesAdapter extends RecyclerView.Adapter<BusLinesAdapter.ViewHolder> {
    /**
     * Tworzy nowy widok RecyclerView dla elementu listy.
     *
     * @param parent   Rodzic widoku.
     * @param viewType Typ widoku.
     * @return Nowy widok RecyclerView.
     */
    private final List<String> busLines;
    private final OnLineClickListener listener;

    public interface OnLineClickListener {
        void onLineClick(String line);
    }

    /**
     * @param busLines Lista linii autobusowych.
     * @brief Konstruktor klasy BusLinesAdapter.
     * <p>
     * Inicjalizuje listę linii autobusowych.
     */

    public BusLinesAdapter(List<String> busLines, OnLineClickListener listener) {
        this.busLines = busLines;
        this.listener = listener;
    }

    /**
     * @param parent   Widok nadrzędny.
     * @param viewType Typ widoku.
     * @return Nowo utworzony ViewHolder.
     * @brief Tworzy nowy widok elementu listy.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ViewHolder(view);
    }

    /**
     * @param holder   Obiekt ViewHolder do aktualizacji.
     * @param position Pozycja elementu na liście.
     * @brief Łączy dane z odpowiednim widokiem elementu listy.
     * <p>
     * Wiąże dane z linią autobusową do widoku.
     * Metoda ustawia dane wyświetlane w poszczególnych elementach listy.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.busLineName.setText(busLines.get(position));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String line = busLines.get(position);
        holder.textView.setText(line);
        holder.itemView.setOnClickListener(v -> listener.onLineClick(line));
    }

    /**
     * Zwraca liczbę elementów w liście.
     *
     * @return Liczba elementów w RecyclerView.
     */
    @Override
    public int getItemCount() {
        return busLines.size();
    }

    /**
     * @class ViewHolder
     * @brief Klasa wewnętrzna obsługująca elementy listy.
     */

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        /**
         * Inicjalizuje widok elementu listy.
         *
         * @param itemView Widok elementu listy.
         */

        ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(android.R.id.text1);
        }
    }
}
