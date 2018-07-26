package com.kg.gettransfer.data.model.secondary;

import java.lang.System;

@kotlin.Metadata(mv = {1, 1, 10}, bv = {1, 0, 2}, k = 1, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u000b\b\u0017\u0018\u00002\u00020\u0001B\u001d\u0012\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\u0002\u0010\u0005J\b\u0010\f\u001a\u00020\u0003H\u0016J\u0006\u0010\r\u001a\u00020\u0003R \u0010\u0002\u001a\u0004\u0018\u00010\u00038\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0006\u0010\u0007\"\u0004\b\b\u0010\tR \u0010\u0004\u001a\u0004\u0018\u00010\u00038\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\n\u0010\u0007\"\u0004\b\u000b\u0010\t\u00a8\u0006\u000e"}, d2 = {"Lcom/kg/gettransfer/data/model/secondary/PriceConverted;", "Lio/realm/RealmObject;", "defaultCurrency", "", "preferredCurrency", "(Ljava/lang/String;Ljava/lang/String;)V", "getDefaultCurrency", "()Ljava/lang/String;", "setDefaultCurrency", "(Ljava/lang/String;)V", "getPreferredCurrency", "setPreferredCurrency", "toString", "toStringMultiline", "data_release"})
@io.realm.annotations.RealmClass()
public class PriceConverted extends io.realm.RealmObject {
    @org.jetbrains.annotations.Nullable()
    @com.google.gson.annotations.SerializedName(value = "default")
    @com.google.gson.annotations.Expose()
    private java.lang.String defaultCurrency;
    @org.jetbrains.annotations.Nullable()
    @com.google.gson.annotations.SerializedName(value = "preferred")
    @com.google.gson.annotations.Expose()
    private java.lang.String preferredCurrency;
    
    @org.jetbrains.annotations.NotNull()
    @java.lang.Override()
    public java.lang.String toString() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String toStringMultiline() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getDefaultCurrency() {
        return null;
    }
    
    public final void setDefaultCurrency(@org.jetbrains.annotations.Nullable()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getPreferredCurrency() {
        return null;
    }
    
    public final void setPreferredCurrency(@org.jetbrains.annotations.Nullable()
    java.lang.String p0) {
    }
    
    public PriceConverted(@org.jetbrains.annotations.Nullable()
    java.lang.String defaultCurrency, @org.jetbrains.annotations.Nullable()
    java.lang.String preferredCurrency) {
        super();
    }
    
    public PriceConverted() {
        super();
    }
}