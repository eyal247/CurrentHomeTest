package com.eyalengel.currenthometest.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.eyalengel.currenthometest.Model.Place;
import com.eyalengel.currenthometest.R;

import java.util.ArrayList;

public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<Place> placesList = new ArrayList<>();

    public PlacesAdapter(Context context, ArrayList<Place> placesList) {
        this.context = context;
        this.placesList = placesList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.place_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final Place place = placesList.get(position);
        holder.placeNameTV.setText(place.getName());
        setFormattedAddress(holder, place);
        setPriceLevel(holder, Place.createDollarSignString(place.getPriceLevel()));
        setStarsRanking(holder, place);
    }

    private void setFormattedAddress(MyViewHolder holder, Place place) {
        holder.placeAddressTV.setText(Place.formatAddress(place.getAddress()));
    }

    private void setStarsRanking(MyViewHolder holder, Place place) {
        holder.ratingBar.setRating(place.getRating());
    }

    private void setPriceLevel(MyViewHolder holder, String dollarSigns) {

        if(dollarSigns.toString().isEmpty())
            holder.dollarPriceTV.setText(" ");
        else
            holder.dollarPriceTV.setText(dollarSigns.toString());
    }

    @Override
    public int getItemCount() {
        return placesList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView placeNameTV, placeAddressTV, dollarPriceTV;
        private RatingBar ratingBar;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            placeNameTV = itemView.findViewById(R.id.place_name_tv);
            placeAddressTV = itemView.findViewById(R.id.place_address_tv);
            dollarPriceTV = itemView.findViewById(R.id.price_level_tv);
            ratingBar = itemView.findViewById(R.id.place_ranking_bar);
        }
    }
}