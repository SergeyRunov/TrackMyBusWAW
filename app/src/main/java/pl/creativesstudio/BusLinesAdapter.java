/**
 * @file BusLinesAdapter.java
 * @brief Adapter for displaying bus lines in a RecyclerView.
 *
 * This adapter manages the display of a list of bus lines within a RecyclerView.
 * It handles the creation and binding of ViewHolder instances that represent each bus line item.
 *
 * @version 1.0
 * @since 2024-12-16
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
 * Adapter responsible for displaying a list of bus lines
 * in the user interface.
 * Handles views for RecyclerView.
 *
 * @version 1.0
 * @since 2024-12-16
 */
public class BusLinesAdapter extends RecyclerView.Adapter<BusLinesAdapter.ViewHolder> {

    /**
     * Listener interface for handling click events on bus lines.
     */
    public interface OnLineClickListener {
        /**
         * Called when a bus line is clicked.
         *
         * @param line The bus line that was clicked.
         */
        void onLineClick(String line);
    }

    /**
     * The list of bus lines to display in the RecyclerView.
     */
    private final List<String> busLines;

    /**
     * The listener for handling bus line click events.
     */
    private final OnLineClickListener listener;

    /**
     * Constructor for BusLinesAdapter.
     *
     * @param busLines The list of bus lines to display.
     * @param listener The listener for handling click events on bus lines.
     */
    public BusLinesAdapter(List<String> busLines, OnLineClickListener listener) {
        this.busLines = busLines;
        this.listener = listener;
    }

    /**
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent an item.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to an adapter position.
     * @param viewType The view type of the new View.
     * @return A new {@link ViewHolder} that holds a View of the given view type.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the simple_list_item_1 layout for each bus line item
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     *
     * This method updates the contents of the {@link ViewHolder#textView} to reflect the bus line
     * at the given position and sets up a click listener to handle user interactions.
     *
     * @param holder   The {@link ViewHolder} which should be updated to represent the contents of the item at the given position.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String line = busLines.get(position);
        holder.textView.setText(line);
        holder.itemView.setOnClickListener(v -> listener.onLineClick(line));
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of bus lines.
     */
    @Override
    public int getItemCount() {
        return busLines.size();
    }

    /**
     * {@link ViewHolder} class for BusLinesAdapter.
     *
     * This inner class holds references to the views for each bus line item in the RecyclerView.
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        /**
         * TextView that displays the bus line.
         */
        TextView textView;

        /**
         * Constructor for ViewHolder.
         *
         * @param itemView The view of the bus line item.
         */
        ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(android.R.id.text1);
        }
    }
}
