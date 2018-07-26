package com.eyalengel.currenthometest.Utils;

import com.eyalengel.currenthometest.Model.MyLatLng;
import com.eyalengel.currenthometest.Model.Place;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JSONParseHelper {

    public static ArrayList<Place> parseJSONArrayToPlaceList(JSONArray jsonArray) {
        ArrayList<Place> placesList = new ArrayList<>();
        int listSize = jsonArray.length();
        int priceLevel = 0;
        float rating = 0.0f;
        MyLatLng latLng;
        String photoRef;

        for (int i = 0; i < listSize; i++) {
            Place place = new Place();
            try {
                if (jsonArray.getJSONObject(i).has("formatted_address"))
                    place.setAddress(jsonArray.getJSONObject(i).getString("formatted_address"));
                if (jsonArray.getJSONObject(i).has("id"))
                    place.setId(jsonArray.getJSONObject(i).getString("id"));
                if (jsonArray.getJSONObject(i).has("name"))
                    place.setName(jsonArray.getJSONObject(i).getString("name"));
                if (jsonArray.getJSONObject(i).has("photos")) {
                    photoRef = parsePhotoReference(jsonArray.getJSONObject(i).getJSONArray("photos"));
                    place.setPhotoStr(photoRef);
                }
                if (jsonArray.getJSONObject(i).has("price_level")) {
                    priceLevel = Integer.valueOf(jsonArray.getJSONObject(i).getString("price_level"));
                    place.setPriceLevel(priceLevel);
                }
                if (jsonArray.getJSONObject(i).has("rating")) {
                    rating = Float.valueOf(jsonArray.getJSONObject(i).getString("rating"));
                    place.setRating(rating);
                }
                if (jsonArray.getJSONObject(i).has("geometry")) {
                    latLng = parseCoordinates(jsonArray.getJSONObject(i).getJSONObject("geometry"));
                    place.setCoordinates(latLng);
                }

                placesList.add(place);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return placesList;
    }

    private static String parsePhotoReference(JSONArray photosJA) {
        String photoRefStr = null;

        try {
            photoRefStr = photosJA.getJSONObject(0).getString("photo_reference");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return photoRefStr;
    }

    private static MyLatLng parseCoordinates(JSONObject geometry) {
        String latStr, lngStr;
        double lat = 0.0, lng = 0.0;
        JSONObject location = null;
        MyLatLng latLng = null;

        try {
            if (geometry.has("location"))
                location = geometry.getJSONObject("location");
            if (location.has("lat")) {
                latStr = location.getString("lat");
                lat = Double.valueOf(latStr);
            }
            if (location.has("lng")) {
                lngStr = location.getString("lng");
                lng = Double.valueOf(lngStr);
            }
            latLng = new MyLatLng(lat, lng);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return latLng;
    }
}
