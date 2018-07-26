package com.kg.gettransfer.data.model;

import java.lang.System;

@kotlin.Metadata(mv = {1, 1, 10}, bv = {1, 0, 2}, k = 1, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\r\b\u0017\u0018\u00002\u00020\u0001B\'\u0012\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u0012\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\u0002\u0010\u0007R \u0010\u0002\u001a\u0004\u0018\u00010\u00038\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\b\u0010\t\"\u0004\b\n\u0010\u000bR \u0010\u0006\u001a\u0004\u0018\u00010\u00038\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\f\u0010\t\"\u0004\b\r\u0010\u000bR\u001e\u0010\u0004\u001a\u00020\u00058\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u000e\u0010\u000f\"\u0004\b\u0010\u0010\u0011\u00a8\u0006\u0012"}, d2 = {"Lcom/kg/gettransfer/data/model/Vehicle;", "Lio/realm/RealmObject;", "name", "", "year", "", "transportTypeID", "(Ljava/lang/String;ILjava/lang/String;)V", "getName", "()Ljava/lang/String;", "setName", "(Ljava/lang/String;)V", "getTransportTypeID", "setTransportTypeID", "getYear", "()I", "setYear", "(I)V", "data_release"})
@io.realm.annotations.RealmClass()
public class Vehicle extends io.realm.RealmObject {
    @org.jetbrains.annotations.Nullable()
    @com.google.gson.annotations.SerializedName(value = "name")
    @com.google.gson.annotations.Expose()
    private java.lang.String name;
    @com.google.gson.annotations.SerializedName(value = "completed_transfers")
    @com.google.gson.annotations.Expose()
    private int year;
    @org.jetbrains.annotations.Nullable()
    @com.google.gson.annotations.SerializedName(value = "transport_type_id")
    @com.google.gson.annotations.Expose()
    private java.lang.String transportTypeID;
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getName() {
        return null;
    }
    
    public final void setName(@org.jetbrains.annotations.Nullable()
    java.lang.String p0) {
    }
    
    public final int getYear() {
        return 0;
    }
    
    public final void setYear(int p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getTransportTypeID() {
        return null;
    }
    
    public final void setTransportTypeID(@org.jetbrains.annotations.Nullable()
    java.lang.String p0) {
    }
    
    public Vehicle(@org.jetbrains.annotations.Nullable()
    java.lang.String name, int year, @org.jetbrains.annotations.Nullable()
    java.lang.String transportTypeID) {
        super();
    }
    
    public Vehicle() {
        super();
    }
}