package com.eyalengel.currenthometest.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class MyLatLng implements Parcelable {

    private double latitude, longitude;


    public MyLatLng(){}

    public MyLatLng(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeDouble(latitude);
        parcel.writeDouble(longitude);
    }

    public MyLatLng(Parcel in){
        readFromParcel(in);
    }

    private void readFromParcel(Parcel in){
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public MyLatLng createFromParcel(Parcel in) {
            return new MyLatLng(in);
        }

        @Override
        public Object[] newArray(int size) {
            return new Object[size];
        }

        public MyLatLng newTripPlace() {return new MyLatLng();}
    };
}
