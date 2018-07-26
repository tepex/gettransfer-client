package com.kg.gettransfer.data.model.secondary;

import java.lang.System;

@kotlin.Metadata(mv = {1, 1, 10}, bv = {1, 0, 2}, k = 1, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u000f\b\u0017\u0018\u00002\u00020\u0001B-\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0007R\u001e\u0010\u0002\u001a\u00020\u00038\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\b\u0010\t\"\u0004\b\n\u0010\u000bR\u001e\u0010\u0005\u001a\u00020\u00038\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\f\u0010\t\"\u0004\b\r\u0010\u000bR\u001e\u0010\u0006\u001a\u00020\u00038\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u000e\u0010\t\"\u0004\b\u000f\u0010\u000bR\u001e\u0010\u0004\u001a\u00020\u00038\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0010\u0010\t\"\u0004\b\u0011\u0010\u000b\u00a8\u0006\u0012"}, d2 = {"Lcom/kg/gettransfer/data/model/secondary/Rating;", "Lio/realm/RealmObject;", "average", "", "vehicle", "drive", "fair", "(IIII)V", "getAverage", "()I", "setAverage", "(I)V", "getDrive", "setDrive", "getFair", "setFair", "getVehicle", "setVehicle", "data_debug"})
@io.realm.annotations.RealmClass()
public class Rating extends io.realm.RealmObject {
    @com.google.gson.annotations.SerializedName(value = "average")
    @com.google.gson.annotations.Expose()
    private int average;
    @com.google.gson.annotations.SerializedName(value = "vehicle")
    @com.google.gson.annotations.Expose()
    private int vehicle;
    @com.google.gson.annotations.SerializedName(value = "drive")
    @com.google.gson.annotations.Expose()
    private int drive;
    @com.google.gson.annotations.SerializedName(value = "fair")
    @com.google.gson.annotations.Expose()
    private int fair;
    
    public final int getAverage() {
        return 0;
    }
    
    public final void setAverage(int p0) {
    }
    
    public final int getVehicle() {
        return 0;
    }
    
    public final void setVehicle(int p0) {
    }
    
    public final int getDrive() {
        return 0;
    }
    
    public final void setDrive(int p0) {
    }
    
    public final int getFair() {
        return 0;
    }
    
    public final void setFair(int p0) {
    }
    
    public Rating(int average, int vehicle, int drive, int fair) {
        super();
    }
    
    public Rating() {
        super();
    }
}