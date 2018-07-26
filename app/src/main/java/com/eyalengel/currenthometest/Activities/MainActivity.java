package com.eyalengel.currenthometest.Activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.eyalengel.currenthometest.Adapters.PlacesAdapter;
import com.eyalengel.currenthometest.Application.MyApplication;
import com.eyalengel.currenthometest.Listeners.EndlessRecyclerOnScrollListener;
import com.eyalengel.currenthometest.Listeners.OnSnackBarActionClickListener;
import com.eyalengel.currenthometest.Listeners.RecyclerTouchListener;
import com.eyalengel.currenthometest.Model.Place;
import com.eyalengel.currenthometest.R;
import com.eyalengel.currenthometest.Utils.JSONParseHelper;
import com.eyalengel.currenthometest.Utils.NetworkUtils;
import com.eyalengel.currenthometest.Utils.SnackbarUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.eyalengel.currenthometest.Model.AppConstants.ACCEPT;
import static com.eyalengel.currenthometest.Model.AppConstants.DENY;
import static com.eyalengel.currenthometest.Model.AppConstants.DISTANCE;
import static com.eyalengel.currenthometest.Model.AppConstants.ERROR;
import static com.eyalengel.currenthometest.Model.AppConstants.MAX_RESULTS;
import static com.eyalengel.currenthometest.Model.AppConstants.NEXT_PAGE_TOKEN;
import static com.eyalengel.currenthometest.Model.AppConstants.PLACE;
import static com.eyalengel.currenthometest.Model.AppConstants.PLACES_URL_PREFIX;
import static com.eyalengel.currenthometest.Model.AppConstants.QUERY;
import static com.eyalengel.currenthometest.Model.AppConstants.RESULTS;
import static com.eyalengel.currenthometest.Model.AppConstants.RETRY;

public class MainActivity extends AppCompatActivity implements OnSnackBarActionClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int LOCATION_PERMISSION_CONSTANT = 100;

    private RelativeLayout mainRelativeLayout;
    private ProgressBar progressBar;
    private TextView checkConnection;
    private TextView goToSettingsTV;
    private String finePermissionRequired = Manifest.permission.ACCESS_FINE_LOCATION;
    private String coarsePermissionRequired = Manifest.permission.ACCESS_COARSE_LOCATION;
    private FusedLocationProviderClient mFusedLocationClient;

    private RecyclerView placesRecyclerView;
    private LinearLayoutManager layoutManager;
    private ArrayList<Place> adapterPlacesList = new ArrayList<>();
    private PlacesAdapter placesAdapter;
    private Location userLocation;
    private String nextResultsPageToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        placesAdapter = new PlacesAdapter(this, adapterPlacesList);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        setUIComponents(); // Getting UI components from xml layout file
        setRecyclerView(); // Setting up the ReyclerView that will hold the Places list

        // Entry point to app's main flow: Asking for user's permission to use their location and
        // continuing the app's flow including retrieving user's location, making a request from
        // Google Places API, parsing JSON response and loading the Places list.
        handleLocationPermission();
    }

    // Setting RecyclerView's layout manager, adapter, and touch+scroll listeners
    private void setRecyclerView() {
        layoutManager = new LinearLayoutManager(getApplicationContext());
        placesRecyclerView.setLayoutManager(layoutManager);
        placesRecyclerView.setItemAnimator(new DefaultItemAnimator());
        placesRecyclerView.setAdapter(placesAdapter);
        setRecyclerViewTouchListener();
        setRecyclerViewScrollListener();
    }

    // Loading more places when user reaches the bottom of the Places list
    private void setRecyclerViewScrollListener() {
        placesRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(layoutManager)
        {
            @Override
            public void onLoadMore(int currentPage)
            {
                //Reached end of list, load more places (20 at a time, up to 60)
                if (adapterPlacesList.size() < MAX_RESULTS) {
                    fetchPlaces();
                }
            }
        });
    }

    private void setRecyclerViewTouchListener() {
        placesRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), placesRecyclerView, new RecyclerTouchListener.ClickListener() {
            // Switching to Map Activity after user clicks on a Place on the list
            @Override
            public void onClick(View view, int position) {
                Place place = adapterPlacesList.get(position); // Getting clicked place
                showLocationOnMap(place); // Showing chosen place on Map
            }

            // Long Click on a place on the list - perhaps implemented in the future
            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    // Switching current activity to Map Activity based on user's chosen Place
    private void showLocationOnMap(Place place) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(PLACE, place); // Sending chosen place to Map Activity
        Intent intent = new Intent(MainActivity.this, MapActivity.class);
        intent.putExtras(bundle);
        // Using flags to make sure that Places list data will be saved when coming back from Map
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);

        //add fade animation when switching to Map Activity
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    // Checking user's permission to use location and asking for it if necessary
    private void handleLocationPermission() {
        // If permission wasn't granted before
        if (ActivityCompat.checkSelfPermission(MainActivity.this,
                finePermissionRequired) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            // If device version requires permission request then ask for it
            getUserPermission();

            // Location permission already given
        } else {
            loadBurritoPlaces(); // load places
        }
    }

    // Asking for user permission to use their location using an AlertDialog
    private void getUserPermission() {
        // If location permission has not been given yet
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                finePermissionRequired) && ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                coarsePermissionRequired)) {
            // Present an AlertDialog asking for permission
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle(getResources().getString(R.string.location_permission_title));
            builder.setMessage(getResources().getString(R.string.location_permission_msg));
            setBuilderButtons(builder); // Set "Accept" and "Deny" buttons and handle user choice
            builder.show();
        } else {
            // Older device version - just get the permission from device
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    finePermissionRequired, coarsePermissionRequired}, LOCATION_PERMISSION_CONSTANT);
            loadBurritoPlaces(); // Load list of places
        }
    }

    // Build the AlertDialog positive and negative buttons and handle user response
    private void setBuilderButtons(AlertDialog.Builder builder) {
        builder.setPositiveButton(ACCEPT, new DialogInterface.OnClickListener() {
            // User agree to share location
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                        finePermissionRequired, coarsePermissionRequired}, LOCATION_PERMISSION_CONSTANT);
                loadBurritoPlaces(); // load places after permission granted
            }
        });
        builder.setNegativeButton(DENY, new DialogInterface.OnClickListener() {
            // User disagrees to share location - put msg on screen
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Let users know they will need to turn on location permission in device settings
                goToSettingsTV.setVisibility(View.VISIBLE);
                dialog.cancel();
            }
        });
    }

    // Load places if there is internet connection
    private void loadBurritoPlaces() {
        // If no internet connection
        if (!NetworkUtils.isConnectedToInternet(getApplicationContext())) {
            checkConnection.setVisibility(View.VISIBLE); // Show message in TextView
            SnackbarUtils.showSnackBarMsg(mainRelativeLayout, // Add a "retry" button at bottom of screen
                    getResources().getString(R.string.no_connection_msg), this, RETRY);
        }
        else {
            checkConnection.setVisibility(View.GONE);
            makeBurritoPlacesRequest(); // Make request from Google Places API
        }

    }

    private void makeBurritoPlacesRequest() {
        showProgressSpinner(); // Show spinner until response arrive from Places API
        getUserLocation(); // Get user location to find nearby places
    }

    // Getting user last known location and moving on to Fetch Places from Google API
    private void getUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            getUserPermission();
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            userLocation = location;
                            fetchPlaces(); // Make Places request based on found location
                        }
                    }
                });
    }

    // Making a request for Google Places using the URL
    private void fetchPlaces() {
        StringBuilder URL = buildURL(userLocation);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL.toString(), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response == null) {
                            Toast.makeText(getApplicationContext(),
                                    getResources().getString(R.string.burrito_null_response_toast),
                                    Toast.LENGTH_LONG).show();
                            return;
                        }
                        // If response is not null, parse the JSON response
                        nextResultsPageToken = getNextPageTokenFromResponse(response);
                        ArrayList<Place> placesListFromJSON = handleJSONResponse(response);
                        setPlacesAdapter(placesListFromJSON); // send Places list to adapter
                        hideProgressSpinner(); // Hide the spinner as we have results to show
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error in getting json
                Log.e(TAG, ERROR + error.getMessage());
                Toast.makeText(getApplicationContext(), ERROR + error.getMessage(), Toast.LENGTH_SHORT).show();
                hideProgressSpinner(); // Hide spinning progress bar
            }
        });

        MyApplication.getInstance().addToRequestQueue(request); // Add request to Queue
    }

    // Getting the next page token (for next 20 results) from the JSON response we got from the API
    private String getNextPageTokenFromResponse(JSONObject response) {
        String token = null;

        if (response.has(NEXT_PAGE_TOKEN)) {
            try {
                token = response.getString(NEXT_PAGE_TOKEN);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return token;
    }

    // stop animating progress spinner
    private void hideProgressSpinner() {
        progressBar.setVisibility(View.GONE);
    }

    private void showProgressSpinner() {
        progressBar.setVisibility(View.VISIBLE);
    }

    // Adding Places from server response into list adapter
    private void setPlacesAdapter(ArrayList<Place> placesListFromJSON) {
        adapterPlacesList.addAll(placesListFromJSON);
        placesAdapter.notifyDataSetChanged();
    }

    private ArrayList<Place> handleJSONResponse(JSONObject response) {
        JSONArray resultsArray = null;

        if (response.has(RESULTS)) {
            try {
                // Get the JSONArray of the places recieved from API request
                resultsArray = response.getJSONArray(RESULTS);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // Parse the JSONArray into an ArrayList of Place Objects
        return JSONParseHelper.parseJSONArrayToPlaceList(resultsArray);
    }

    // Build the URL to use for the Places request
    private StringBuilder buildURL(Location userLocation) {

        //******** IMPORTANT (cause I used an emulator with no real loaction) ********//
        // If not getting any results, please comment next 2 lines and uncomment the following 2 lines //
        String lat = String.valueOf(userLocation.getLatitude());
        String lng = String.valueOf(userLocation.getLongitude());

//        String lat = "40.788832";
//        String lng = "-73.9858806";

        StringBuilder googlePlacesUrl = new StringBuilder(PLACES_URL_PREFIX);
        googlePlacesUrl.append("location=").append(lat).append(",").append(lng);
        googlePlacesUrl.append("&query=").append(QUERY);
        googlePlacesUrl.append("&rankby=").append(DISTANCE);
        googlePlacesUrl.append("&sensor=true");
        if(nextResultsPageToken!=null)
            googlePlacesUrl.append("&pagetoken=").append(nextResultsPageToken);
        googlePlacesUrl.append("&key=" + getResources().getString(R.string.google_maps_api_key));

        return googlePlacesUrl;
    }

    private void setUIComponents() {
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mainRelativeLayout = (RelativeLayout) findViewById(R.id.main_rl);
        placesRecyclerView = (RecyclerView) findViewById(R.id.places_recycler_view);
        checkConnection = (TextView) findViewById(R.id.verify_connection_tv);
        goToSettingsTV = (TextView) findViewById(R.id.go_to_settings_tv);
    }

    // When user clicks "Retry" on SnackBar message
    // (user is trying to load places after not having internet connection)
    @Override
    public void onSnackBarActionClick() {
        loadBurritoPlaces();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
