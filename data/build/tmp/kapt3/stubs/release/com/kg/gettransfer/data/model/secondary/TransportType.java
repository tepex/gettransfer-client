package com.kg.gettransfer.data.model.secondary;

import java.lang.System;

@kotlin.Metadata(mv = {1, 1, 10}, bv = {1, 0, 2}, k = 1, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u000f\b\u0017\u0018\u00002\u00020\u0001B-\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0006\u0012\b\b\u0002\u0010\u0007\u001a\u00020\u0006\u00a2\u0006\u0002\u0010\bR\u001e\u0010\u0002\u001a\u00020\u00038\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\t\u0010\n\"\u0004\b\u000b\u0010\fR\u001e\u0010\u0007\u001a\u00020\u00068\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\r\u0010\u000e\"\u0004\b\u000f\u0010\u0010R\u001e\u0010\u0005\u001a\u00020\u00068\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0011\u0010\u000e\"\u0004\b\u0012\u0010\u0010R\u001a\u0010\u0004\u001a\u00020\u0003X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0013\u0010\n\"\u0004\b\u0014\u0010\f\u00a8\u0006\u0015"}, d2 = {"Lcom/kg/gettransfer/data/model/secondary/TransportType;", "Lio/realm/RealmObject;", "id", "", "title", "paxMax", "", "luggageMax", "(Ljava/lang/String;Ljava/lang/String;II)V", "getId", "()Ljava/lang/String;", "setId", "(Ljava/lang/String;)V", "getLuggageMax", "()I", "setLuggageMax", "(I)V", "getPaxMax", "setPaxMax", "getTitle", "setTitle", "data_release"})
@io.realm.annotations.RealmClass()
public class TransportType extends io.realm.RealmObject {
    @org.jetbrains.annotations.NotNull()
    @com.google.gson.annotations.SerializedName(value = "id")
    @io.realm.annotations.PrimaryKey()
    @com.google.gson.annotations.Expose()
    private java.lang.String id;
    @org.jetbrains.annotations.NotNull()
    private java.lang.String title;
    @com.google.gson.annotations.SerializedName(value = "pax_max")
    @com.google.gson.annotations.Expose()
    private int paxMax;
    @com.google.gson.annotations.SerializedName(value = "luggage_max")
    @com.google.gson.annotations.Expose()
    private int luggageMax;
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getId() {
        return null;
    }
    
    public final void setId(@org.jetbrains.annotations.NotNull()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getTitle() {
        return null;
    }
    
    public final void setTitle(@org.jetbrains.annotations.NotNull()
    java.lang.String p0) {
    }
    
    public final int getPaxMax() {
        return 0;
    }
    
    public final void setPaxMax(int p0) {
    }
    
    public final int getLuggageMax() {
        return 0;
    }
    
    public final void setLuggageMax(int p0) {
    }
    
    public TransportType(@org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    java.lang.String title, int paxMax, int luggageMax) {
        super();
    }
    
    public TransportType() {
        super();
    }
}