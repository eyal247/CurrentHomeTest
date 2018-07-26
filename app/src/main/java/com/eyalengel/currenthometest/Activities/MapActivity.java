package com.eyalengel.currenthometest.Activities;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.RatingBar;
import android.widget.TextView;

import com.eyalengel.currenthometest.Model.Place;
import com.eyalengel.currenthometest.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import static com.eyalengel.currenthometest.Model.AppConstants.PLACE;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private Place chosenPlace;
    private TextView addressTV, dollarTV;
    private RatingBar ratingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        getPlaceFromIntent();
        setUIComponents();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

    }

    private void setUIComponents() {
        addressTV = (TextView)findViewById(R.id.map_place_address_tv);
        dollarTV = (TextView)findViewById(R.id.map_price_level_tv);
        ratingBar = (RatingBar) findViewById(R.id.map_place_ranking_bar);
    }

    private void getPlaceFromIntent() {
        this.chosenPlace = getIntent().getExtras().getParcelable(PLACE);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        setMapUIControls();
        setPlaceDetails();
        addPlaceMarkerToMap(); // Add a marker to the place the user chose
    }

    // Put place address, price level, and ranting below map
    private void setPlaceDetails() {
        addressTV.setText(Place.formatAddress(chosenPlace.getAddress()));
        dollarTV.setText(Place.createDollarSignString(chosenPlace.getPriceLevel()));
        ratingBar.setRating(chosenPlace.getRating());
    }

    // Add the pin marker where the chosen place is located
    private void addPlaceMarkerToMap() {
        LatLng chosenLatLng = new LatLng(chosenPlace.getCoordinates().getLatitude(),
                chosenPlace.getCoordinates().getLongitude());
        map.addMarker(new MarkerOptions().position(chosenLatLng)
                .title(chosenPlace.getName())
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin)));

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(chosenLatLng, 16.0f));

        // Option to animate camera to chosen location (comment line above and uncomment line below)
        //map.animateCamera(CameraUpdateFactory.newLatLngZoom(chosenLatLng, 16.0f), 3000, null);
    }

    private void setMapUIControls() {
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setCompassEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.getUiSettings().setZoomGesturesEnabled(true);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    // When hardware back button is presses, go back to previous Activity with animation
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.fade_out);
    }

    // When Home Up button is pressed (in action bar), go back to previous Activity with animation
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
