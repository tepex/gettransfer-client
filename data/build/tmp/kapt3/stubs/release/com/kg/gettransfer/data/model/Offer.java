package com.kg.gettransfer.data.model;

import java.lang.System;

@kotlin.Metadata(mv = {1, 1, 10}, bv = {1, 0, 2}, k = 1, d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0006\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\b\b\u0017\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002R \u0010\u0003\u001a\u0004\u0018\u00010\u00048\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0005\u0010\u0006\"\u0004\b\u0007\u0010\bR\u001e\u0010\t\u001a\u00020\n8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u000b\u0010\f\"\u0004\b\r\u0010\u000eR \u0010\u000f\u001a\u0004\u0018\u00010\u00108\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0011\u0010\u0012\"\u0004\b\u0013\u0010\u0014R\"\u0010\u0015\u001a\u0004\u0018\u00010\u00168\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0010\n\u0002\u0010\u001b\u001a\u0004\b\u0017\u0010\u0018\"\u0004\b\u0019\u0010\u001aR \u0010\u001c\u001a\u0004\u0018\u00010\u001d8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u001e\u0010\u001f\"\u0004\b \u0010!R \u0010\"\u001a\u0004\u0018\u00010#8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b$\u0010%\"\u0004\b&\u0010\'R\"\u0010(\u001a\u0004\u0018\u00010\u00168\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0010\n\u0002\u0010\u001b\u001a\u0004\b)\u0010\u0018\"\u0004\b*\u0010\u001a\u00a8\u0006+"}, d2 = {"Lcom/kg/gettransfer/data/model/Offer;", "Lio/realm/RealmObject;", "()V", "carrier", "Lcom/kg/gettransfer/data/model/Carrier;", "getCarrier", "()Lcom/kg/gettransfer/data/model/Carrier;", "setCarrier", "(Lcom/kg/gettransfer/data/model/Carrier;)V", "id", "", "getId", "()I", "setId", "(I)V", "price", "Lcom/kg/gettransfer/data/model/secondary/Price;", "getPrice", "()Lcom/kg/gettransfer/data/model/secondary/Price;", "setPrice", "(Lcom/kg/gettransfer/data/model/secondary/Price;)V", "refreshments", "", "getRefreshments", "()Ljava/lang/Boolean;", "setRefreshments", "(Ljava/lang/Boolean;)V", "Ljava/lang/Boolean;", "status", "", "getStatus", "()Ljava/lang/String;", "setStatus", "(Ljava/lang/String;)V", "vehicle", "Lcom/kg/gettransfer/data/model/Vehicle;", "getVehicle", "()Lcom/kg/gettransfer/data/model/Vehicle;", "setVehicle", "(Lcom/kg/gettransfer/data/model/Vehicle;)V", "wifi", "getWifi", "setWifi", "data_release"})
@io.realm.annotations.RealmClass()
public class Offer extends io.realm.RealmObject {
    @io.realm.annotations.PrimaryKey()
    @com.google.gson.annotations.SerializedName(value = "id")
    @com.google.gson.annotations.Expose()
    private int id;
    @org.jetbrains.annotations.Nullable()
    @com.google.gson.annotations.SerializedName(value = "price")
    @com.google.gson.annotations.Expose()
    private com.kg.gettransfer.data.model.secondary.Price price;
    @org.jetbrains.annotations.Nullable()
    @com.google.gson.annotations.SerializedName(value = "status")
    @com.google.gson.annotations.Expose()
    private java.lang.String status;
    @org.jetbrains.annotations.Nullable()
    @com.google.gson.annotations.SerializedName(value = "carrier")
    @com.google.gson.annotations.Expose()
    private com.kg.gettransfer.data.model.Carrier carrier;
    @org.jetbrains.annotations.Nullable()
    @com.google.gson.annotations.SerializedName(value = "vehicle")
    @com.google.gson.annotations.Expose()
    private com.kg.gettransfer.data.model.Vehicle vehicle;
    @org.jetbrains.annotations.Nullable()
    @com.google.gson.annotations.SerializedName(value = "wifi")
    @com.google.gson.annotations.Expose()
    private java.lang.Boolean wifi;
    @org.jetbrains.annotations.Nullable()
    @com.google.gson.annotations.SerializedName(value = "refreshments")
    @com.google.gson.annotations.Expose()
    private java.lang.Boolean refreshments;
    
    public final int getId() {
        return 0;
    }
    
    public final void setId(int p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.kg.gettransfer.data.model.secondary.Price getPrice() {
        return null;
    }
    
    public final void setPrice(@org.jetbrains.annotations.Nullable()
    com.kg.gettransfer.data.model.secondary.Price p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getStatus() {
        return null;
    }
    
    public final void setStatus(@org.jetbrains.annotations.Nullable()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.kg.gettransfer.data.model.Carrier getCarrier() {
        return null;
    }
    
    public final void setCarrier(@org.jetbrains.annotations.Nullable()
    com.kg.gettransfer.data.model.Carrier p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.kg.gettransfer.data.model.Vehicle getVehicle() {
        return null;
    }
    
    public final void setVehicle(@org.jetbrains.annotations.Nullable()
    com.kg.gettransfer.data.model.Vehicle p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Boolean getWifi() {
        return null;
    }
    
    public final void setWifi(@org.jetbrains.annotations.Nullable()
    java.lang.Boolean p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Boolean getRefreshments() {
        return null;
    }
    
    public final void setRefreshments(@org.jetbrains.annotations.Nullable()
    java.lang.Boolean p0) {
    }
    
    public Offer() {
        super();
    }
}