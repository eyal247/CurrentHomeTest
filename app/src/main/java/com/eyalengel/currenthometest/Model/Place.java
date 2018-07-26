package com.eyalengel.currenthometest.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class Place implements Parcelable{
    private String id;
    private String name, address, photoStr;
    private MyLatLng coordinates;
    private int priceLevel;
    private float rating;

    public Place(String id, String name, String address, String photoStr, MyLatLng coordinates,
                 int priceLevel, float rating) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.photoStr = photoStr;
        this.coordinates = coordinates;
        this.priceLevel = priceLevel;
        this.rating = rating;
    }

    public Place(){}

    public Place(Parcel in) {
        id = in.readString();
        name = in.readString();
        address = in.readString();
        photoStr = in.readString();
        coordinates = in.readParcelable(MyLatLng.class.getClassLoader());
        priceLevel = in.readInt();
        rating = in.readFloat();
    }

    public static final Creator<Place> CREATOR = new Creator<Place>() {
        @Override
        public Place createFromParcel(Parcel in) {
            return new Place(in);
        }

        @Override
        public Place[] newArray(int size) {
            return new Place[size];
        }
    };

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getPhotoStr() {
        return photoStr;
    }

    public MyLatLng getCoordinates() {
        return coordinates;
    }

    public int getPriceLevel() {
        return priceLevel;
    }

    public float getRating() {
        return rating;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPhotoStr(String photoStr) {
        this.photoStr = photoStr;
    }

    public void setCoordinates(MyLatLng coordinates) {
        this.coordinates = coordinates;
    }

    public void setPriceLevel(int priceLevel) {
        this.priceLevel = priceLevel;
    }

    public void setRating(float rank) {
        this.rating = rank;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(name);
        parcel.writeString(address);
        parcel.writeString(photoStr);
        parcel.writeParcelable(coordinates, i);
        parcel.writeInt(priceLevel);
        parcel.writeFloat(rating);
    }

    // Remove everything after the first comma in the address (use only street name and number)
    public static String formatAddress(String fullAddress){
        String formattedAddress;

        if(fullAddress!=null) {
            Character firstChar = fullAddress.charAt(0);
            // Handling the case where address starts with a suite number (e.g. 260, 232 W 18th St)
            if(Character.isDigit(firstChar)) {
                int secondCommaIndex = fullAddress.indexOf(',', fullAddress.indexOf(',')+1);
                return fullAddress.substring(0, secondCommaIndex);
            }
            else // The normal case (e.g. 118 E 34th St)
                return fullAddress.substring(0, fullAddress.indexOf(","));
        }
        else
            return "";
    }

    public static String createDollarSignString(int priceLevel){
        StringBuilder dollarSigns = new StringBuilder();

        for(int i = 0 ; i < priceLevel ; i++){
            dollarSigns.append('$');
        }

        return dollarSigns.toString();
    }
}
