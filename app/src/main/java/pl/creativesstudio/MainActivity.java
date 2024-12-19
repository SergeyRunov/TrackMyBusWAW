/**
 * @file MainActivity.java
 * @brief Main activity for the TrackMyBusWAW application.
 *
 * This activity handles the display and interaction with the Google Map,
 * manages bus data retrieval from the Warsaw API, and provides UI components
 * such as the menu and location buttons.
 *
 * @version 1.1
 * @since 2024-12-16
 */
package pl.creativesstudio;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.*;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.creativesstudio.api.WarsawApiService;
import pl.creativesstudio.models.ApiResponse;
import pl.creativesstudio.models.Bus;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/**
 * Main activity for the TrackMyBusWAW application.
 * Handles map initialization, location services, data retrieval from the API,
 * and user interactions such as menu and location buttons.
 *
 * @version 1.1
 * @since 2024-12-16
 */
public class MainActivity extends AppCompatActivity
        implements OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        GoogleMap.OnCameraIdleListener,
        ActivityCompat.OnRequestPermissionsResultCallback {

    /**
     * GoogleMap instance representing the map in the activity.
     */
    GoogleMap mMap;

    /**
     * Request code for location permission.
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    /**
     * Current center of the map.
     */
    private LatLng currentMapCenter;
    /**
     * Current visible bounds of the map.
     */
    LatLngBounds visibleBounds;
    /**
     * Service for interacting with the Warsaw API.
     */
    private WarsawApiService apiService;
    /**
     * Base URL for the Warsaw API.
     */
    private static final String BASE_URL = "https://api.um.warszawa.pl/";
    /**
     * API key for authenticating requests to the Warsaw API.
     */
    private static final String API_KEY = "3fb6fadd-9c21-43fc-998b-c41cc14663ff";
    /**
     * Resource ID for the buses/trams API endpoint.
     */
    private static final String RESOURCE_ID = "f2e5503e-927d-4ad3-9500-4ab9e55deb59";
    /**
     * Handler for managing delayed and periodic tasks.
     */

    private Handler handler = new Handler();
    /**
     * Runnable for periodic data refresh.
     */
    private Runnable runnable;
    /**
     * Runnable for map updates with a delay.
     */
    private Runnable mapUpdateRunnable;
    /**
     * Delay for map updates in milliseconds.
     */
    private static final long MAP_UPDATE_DELAY = 1000;
    /**
     * Data refresh interval when zoomed in, in milliseconds.
     */
    private static final long DATA_REFRESH_INTERVAL_HIGH_ZOOM = 5000;
    /**
     * Data refresh interval when zoomed out, in milliseconds.
     */
    private static final long DATA_REFRESH_INTERVAL_LOW_ZOOM = 15000;
    /**
     * Default data refresh interval, in milliseconds.
     */
    private static final long DATA_REFRESH_INTERVAL_DEFAULT = 10000;
    /**
     * Minimum interval between API calls, in milliseconds.
     */
    private static final long MIN_API_CALL_INTERVAL = 5000;
    /**
     * Minimum zoom level to display bus data.
     */
    private static final float MIN_ZOOM_LEVEL = 14.0f;
    /**
     * Map of active markers on the map, keyed by bus ID.
     */
    private Map<String, Marker> activeMarkers = new HashMap<>();
    /**
     * ID of the currently selected bus.
     */
    private String selectedBusId = null;
    /**
     * List of the last loaded buses.
     */
    private List<Bus> lastLoadedBuses = new ArrayList<>();
    /**
     * Timestamp of the last API call.
     */
    private long lastApiCallTime = 0;
    /**
     * Flag indicating if it's the initial data load.
     */
    private boolean isInitialLoad = true;
    /**
     * Executor service for handling background tasks.
     */
    private ExecutorService executorService;
    /**
     * Flag indicating if a specific bus line is selected.
     */
    private boolean lineSelected = false;
    /**
     * Called when the activity is first created.
     *
     * Initializes UI components, sets up map fragment, and configures Retrofit for API interactions.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           then this Bundle contains the data it most recently supplied.
     */
    List<Bus> allBuses = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ImageButton buttonMenu = findViewById(R.id.button_menu);
        buttonMenu.setOnClickListener(v -> showBottomSheetWithLines());

        ImageButton buttonCurrentLocation = findViewById(R.id.button_current_location);
        buttonCurrentLocation.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

                FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
                fusedLocationClient.getLastLocation().addOnSuccessListener(MainActivity.this, location -> {
                    if (location != null) {
                        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        if (mMap != null) {
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Nie udało się pobrać lokalizacji użytkownika", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {

                Toast.makeText(MainActivity.this, "Brak uprawnień do pobrania lokalizacji", Toast.LENGTH_SHORT).show();
            }
        });



        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.id_map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(WarsawApiService.class);

        executorService = Executors.newSingleThreadExecutor();
    }
    /**
     * Displays the bottom sheet dialog containing the list of bus lines.
     */
    void showBottomSheetWithLines() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_lines, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        RecyclerView recyclerView = bottomSheetView.findViewById(R.id.recycler_view_lines);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        List<String> busLines = new ArrayList<>();

        busLines.add("POKAŻ WSZYSTKIE AUTOBUSY");


        for (Bus bus : allBuses) {
            if (!busLines.contains(bus.getLines())) {
                busLines.add(bus.getLines());
            }
        }


        List<String> linesToSort = busLines.subList(1, busLines.size());
        linesToSort = sortBusLines(linesToSort);

        for (int i = 1; i < busLines.size(); i++) {
            busLines.set(i, linesToSort.get(i - 1));
        }

        BusLinesAdapter adapter = new BusLinesAdapter(busLines, line -> {
            bottomSheetDialog.dismiss();
            if (line.equals("POKAŻ WSZYSTKIE AUTOBUSY")) {

                lineSelected = false;

                lastLoadedBuses = new ArrayList<>(allBuses);

                Toast.makeText(MainActivity.this, "Wybrano: POKAŻ WSZYSTKIE AUTOBUSY", Toast.LENGTH_SHORT).show();


                if (currentMapCenter != null) {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentMapCenter, 15f));
                }


                displayBusesOnMap(lastLoadedBuses);
            } else {
                Toast.makeText(MainActivity.this, "Wybrano linię: " + line, Toast.LENGTH_SHORT).show();
                lineSelected = true;
                filterAndZoomToLine(line);
            }
        });

        recyclerView.setAdapter(adapter);
        bottomSheetDialog.show();
    }
    /**
     * Sorts the list of bus lines using a custom comparator.
     *
     * @param lines The list of bus lines to sort.
     * @return The sorted list of bus lines.
     */
    List<String> sortBusLines(List<String> lines) {

        lines.sort((line1, line2) -> {

            String numberPart1 = line1.replaceAll("[^0-9]", ""); // Wyodrębnij część numeryczną
            String numberPart2 = line2.replaceAll("[^0-9]", "");

            String letterPart1 = line1.replaceAll("[0-9]", ""); // Wyodrębnij część literową
            String letterPart2 = line2.replaceAll("[0-9]", "");


            int letterComparison = letterPart1.compareTo(letterPart2);
            if (letterComparison != 0) {
                return letterComparison;
            }


            if (!numberPart1.isEmpty() && !numberPart2.isEmpty()) {
                return Integer.compare(Integer.parseInt(numberPart1), Integer.parseInt(numberPart2));
            }


            return line1.compareTo(line2);
        });

        return lines;
    }
    /**
     * Filters buses by the specified line and zooms the map to show them.
     *
     * @param line The bus line to filter by.
     */
    void filterAndZoomToLine(String line) {
        if (mMap == null || allBuses.isEmpty()) return;

        List<Bus> filteredBuses = new ArrayList<>();
        for (Bus bus : allBuses) {
            if (bus.getLines().equals(line)) {
                filteredBuses.add(bus);
            }
        }

        if (filteredBuses.isEmpty()) {
            Toast.makeText(this, "Brak autobusów dla linii: " + line, Toast.LENGTH_SHORT).show();
            return;
        }


        displayBusesOnMap(filteredBuses);

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        for (Bus bus : filteredBuses) {
            boundsBuilder.include(new LatLng(bus.getLat(), bus.getLon()));
        }
        LatLngBounds bounds = boundsBuilder.build();
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
    }
    /**
     * Called when the map is ready to be used.
     *
     * Initializes map settings, enables location if permissions are granted,
     * sets up UI controls, and starts the data refresh handler.
     *
     * @param googleMap The GoogleMap object.
     */
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);

            FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            currentMapCenter = currentLocation;
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
                        } else {
                            LatLng defaultLocation = new LatLng(52.2881717, 21.0061544);
                            currentMapCenter = defaultLocation;
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 15));
                        }
                    });
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);

            LatLng defaultLocation = new LatLng(52.2881717, 21.0061544);
            currentMapCenter = defaultLocation;
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 15));
        }

        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        mMap.setOnCameraIdleListener(this);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);

        mMap.setOnMarkerClickListener(marker -> {
            if (marker.getSnippet() != null) {
                selectedBusId = marker.getSnippet();
            }
            return false;
        });

        updateVisibleBounds();
        loadBusData(true);

        runnable = new Runnable() {
            @Override
            public void run() {
                float currentZoom = mMap.getCameraPosition().zoom;
                if (currentZoom >= MIN_ZOOM_LEVEL) {
                    loadBusData(true);
                    handler.postDelayed(this, DATA_REFRESH_INTERVAL_HIGH_ZOOM);
                } else {
                    handler.postDelayed(this, DATA_REFRESH_INTERVAL_LOW_ZOOM);
                }
            }
        };
        handler.postDelayed(runnable, DATA_REFRESH_INTERVAL_HIGH_ZOOM);
    }
    /**
     * Updates the visible bounds of the map based on the current camera position.
     */
    private void updateVisibleBounds() {
        if (mMap != null) {
            visibleBounds = mMap.getProjection().getVisibleRegion().latLngBounds;
        }
    }
    /**
     * Called when the camera movement has stopped and the map is idle.
     *
     * Updates the current map center and visible bounds,
     * schedules a map update after a delay.
     */
    @Override
    public void onCameraIdle() {
        if (mMap == null) return;

        currentMapCenter = mMap.getCameraPosition().target;
        updateVisibleBounds();

        if (mapUpdateRunnable != null) {
            handler.removeCallbacks(mapUpdateRunnable);
        }

        mapUpdateRunnable = new Runnable() {
            @Override
            public void run() {
                updateMapWithCurrentData();
            }
        };
        handler.postDelayed(mapUpdateRunnable, MAP_UPDATE_DELAY);
    }
    /**
     * Updates the map with the current data based on zoom level and visibility.
     *
     * Filters buses within the visible bounds and displays them.
     * Manages data refresh intervals based on zoom level.
     */
    private void updateMapWithCurrentData() {
        if (mMap == null) return;
        if (lineSelected) return;

        float currentZoom = mMap.getCameraPosition().zoom;
        Log.d("ZoomLevel", "Aktualny poziom zoomu: " + currentZoom);

        if (currentZoom < MIN_ZOOM_LEVEL) {
            Log.d("ZoomLevel", "Zoom poniżej progu. Usuwanie markerów.");
            // Usuń markery
            for (Marker marker : activeMarkers.values()) {
                marker.remove();
            }
            activeMarkers.clear();
            return;
        }

        if (!lastLoadedBuses.isEmpty()) {
            List<Bus> visibleBuses = filterBusesWithinBounds(lastLoadedBuses);
            displayBusesOnMap(visibleBuses);
        }


        long currentTime = System.currentTimeMillis();
        if (currentTime - lastApiCallTime >= MIN_API_CALL_INTERVAL || isInitialLoad) {
            loadBusData(false);
            isInitialLoad = false;
        }
    }
    /**
     * Filters the list of buses to include only those within the visible map bounds.
     *
     * @param allBuses The complete list of buses.
     * @return A list of buses within the visible bounds.
     */
    List<Bus> filterBusesWithinBounds(List<Bus> allBuses) {
        List<Bus> visibleBuses = new ArrayList<>();
        if (visibleBounds != null) {
            for (Bus bus : allBuses) {
                LatLng busLocation = new LatLng(bus.getLat(), bus.getLon());
                if (visibleBounds.contains(busLocation)) {
                    visibleBuses.add(bus);
                }
            }
        }
        return visibleBuses;
    }
    /**
     * Loads bus data from the API.
     *
     * @param forced If true, forces data loading regardless of the last API call time.
     */
    private void loadBusData(boolean forced) {
        long currentTime = System.currentTimeMillis();
        if (!forced && currentTime - lastApiCallTime < MIN_API_CALL_INTERVAL) {
            return;
        }

        if (visibleBounds == null) {
            return;
        }

        double minLat = visibleBounds.southwest.latitude;
        double maxLat = visibleBounds.northeast.latitude;
        double minLon = visibleBounds.southwest.longitude;
        double maxLon = visibleBounds.northeast.longitude;

        executorService.execute(() -> {
            try {
                // Jeśli API wspiera pobieranie na podstawie granic, użyj poniższego wywołania
                // Call<ApiResponse> call = apiService.getBusesWithinBounds(
                //         RESOURCE_ID,
                //         API_KEY,
                //         minLat,
                //         maxLat,
                //         minLon,
                //         maxLon
                // );


                Call<ApiResponse> call = apiService.getBuses(
                        RESOURCE_ID,
                        API_KEY,
                        1,
                        null,
                        null
                );

                Response<ApiResponse> response = call.execute();

                if (response.isSuccessful() && response.body() != null) {
                    List<Bus> result = response.body().getResult();

                    allBuses = result;
                    lastLoadedBuses = new ArrayList<>(result);

                    if (result == null || result.isEmpty()) {

                        runOnUiThread(() -> {
                            if (!lastLoadedBuses.isEmpty()) {
                                Toast.makeText(MainActivity.this, "Brak nowych danych. Wyświetlam ostatnio pobrane dane z czasu: "
                                        + formatTimestamp(lastApiCallTime), Toast.LENGTH_LONG).show();
                                displayBusesOnMap(lastLoadedBuses);
                            } else {
                                Toast.makeText(MainActivity.this, "Brak danych do wyświetlenia.", Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {

                        lastLoadedBuses = result;
                        lastApiCallTime = currentTime;

                        runOnUiThread(() -> {
                            List<Bus> visibleBuses = filterBusesWithinBounds(lastLoadedBuses);
                            displayBusesOnMap(visibleBuses);
                        });
                    }
                } else {

                    runOnUiThread(() -> {
                        if (!lastLoadedBuses.isEmpty()) {
                            Toast.makeText(MainActivity.this, "Błąd API. Wyświetlam ostatnio pobrane dane z czasu: "
                                    + formatTimestamp(lastApiCallTime), Toast.LENGTH_LONG).show();
                            displayBusesOnMap(lastLoadedBuses);
                        } else {
                            Toast.makeText(MainActivity.this, "Błąd API i brak danych do wyświetlenia.", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } catch (Exception e) {
                // Błąd sieci: Wyświetl ostatnio pobrane dane
                runOnUiThread(() -> {
                    if (!lastLoadedBuses.isEmpty()) {
                        Toast.makeText(MainActivity.this, "Błąd połączenia. Wyświetlam ostatnio pobrane dane z czasu: "
                                + formatTimestamp(lastApiCallTime), Toast.LENGTH_LONG).show();
                        displayBusesOnMap(lastLoadedBuses);
                    } else {
                        Toast.makeText(MainActivity.this, "Błąd połączenia i brak danych do wyświetlenia.", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
    /**
     * Formats a timestamp into a readable date-time string.
     *
     * @param timestamp The timestamp to format.
     * @return A formatted date-time string.
     */
    private String formatTimestamp(long timestamp) {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        java.util.Date date = new java.util.Date(timestamp);
        return sdf.format(date);
    }
    /**
     * Displays the list of buses on the map by adding markers.
     *
     * @param buses The list of buses to display.
     */
    private void displayBusesOnMap(List<Bus> buses) {
        if (mMap == null) return;

        mMap.clear();



        activeMarkers.clear();
        for (Bus bus : buses) {
            double lat = bus.getLat();
            double lon = bus.getLon();
            String line = bus.getLines();
            String busId = bus.getVehicleNumber();

            if (lat != 0 && lon != 0) {
                LatLng position = new LatLng(lat, lon);
                BitmapDescriptor icon = createCustomMarker(line);
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(position)
                        .title("Linia: " + line + " | Nr pojazdu: " + busId)
                        .snippet(busId)
                        .icon(icon)
                        .anchor(0.5f, 1f); // Ustawienie kotwicy, aby marker był poprawnie wyświetlany

                Marker marker = mMap.addMarker(markerOptions);
                if (marker != null) {
                    activeMarkers.put(busId, marker);
                    if (busId.equals(selectedBusId)) {
                        marker.showInfoWindow();
                    }
                }
            }
        }
    }
    /**
     * Creates a custom marker icon with the bus line text.
     *
     * @param line The bus line to display on the marker.
     * @return A BitmapDescriptor representing the custom marker icon.
     */
    private BitmapDescriptor createCustomMarker(String line) {
        Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(50);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextAlign(Paint.Align.CENTER);

        Paint backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.WHITE);

        Rect textBounds = new Rect();
        textPaint.getTextBounds(line, 0, line.length(), textBounds);

        int textWidth = textBounds.width() + 20;
        int textHeight = textBounds.height() + 20;


        int pinWidth = 124;
        int pinHeight = 212;

        int width = Math.max(textWidth, pinWidth);
        int height = textHeight + pinHeight;

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);


        canvas.drawRect(0, 0, width, textHeight, backgroundPaint);


        canvas.drawText(line, width / 2, textHeight - 10, textPaint);


        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.ic_marker_icon);
        if (drawable != null) {
            drawable.setBounds((width - pinWidth) / 2, textHeight, (width + pinWidth) / 2, height);
            drawable.draw(canvas);
        }

        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
    /**
     * Called when the user's current location is clicked on the map.
     *
     * Displays a toast with the user's location information.
     *
     * @param location The user's current location.
     */
    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Obecna lokalizacja:\n" + location, Toast.LENGTH_SHORT).show();
    }
    /**
     * Called when the My Location button is clicked on the map.
     *
     * Displays a toast indicating location detection and allows the default behavior.
     *
     * @return False to indicate that the default behavior should occur.
     */
    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "Wykrywanie lokalizacji", Toast.LENGTH_SHORT).show();
        return false;
    }
    /**
     * Handles the result of permission requests.
     *
     * Specifically handles the result for location permission requests.
     *
     * @param requestCode  The request code passed in {@link ActivityCompat#requestPermissions}.
     * @param permissions  The requested permissions.
     * @param grantResults The grant results for the corresponding permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                }
            } else {
                Toast.makeText(this, "Uprawnienia nie zostały przyznane", Toast.LENGTH_SHORT).show();
            }
        }
    }
    /**
     * Called when the activity is destroyed.
     *
     * Cleans up handlers and executor services to prevent memory leaks.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacks(runnable);
            if (mapUpdateRunnable != null) {
                handler.removeCallbacks(mapUpdateRunnable);
            }
        }

        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}