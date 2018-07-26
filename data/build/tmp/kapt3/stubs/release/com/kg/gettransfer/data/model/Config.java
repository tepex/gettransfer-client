package com.kg.gettransfer.data.model;

import java.lang.System;

@kotlin.Metadata(mv = {1, 1, 10}, bv = {1, 0, 2}, k = 1, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0013\b\u0017\u0018\u00002\u00020\u0001BW\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\u0012\b\u0002\u0010\u0004\u001a\f\u0012\u0006\u0012\u0004\u0018\u00010\u0006\u0018\u00010\u0005\u0012\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\b\u0012\u0012\b\u0002\u0010\t\u001a\f\u0012\u0006\u0012\u0004\u0018\u00010\n\u0018\u00010\u0005\u0012\u0012\b\u0002\u0010\u000b\u001a\f\u0012\u0006\u0012\u0004\u0018\u00010\b\u0018\u00010\u0005\u00a2\u0006\u0002\u0010\fR(\u0010\u0004\u001a\f\u0012\u0006\u0012\u0004\u0018\u00010\u0006\u0018\u00010\u00058\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\r\u0010\u000e\"\u0004\b\u000f\u0010\u0010R\u001e\u0010\u0002\u001a\u00020\u00038\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0011\u0010\u0012\"\u0004\b\u0013\u0010\u0014R \u0010\u0007\u001a\u0004\u0018\u00010\b8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0015\u0010\u0016\"\u0004\b\u0017\u0010\u0018R(\u0010\t\u001a\f\u0012\u0006\u0012\u0004\u0018\u00010\n\u0018\u00010\u00058\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0019\u0010\u000e\"\u0004\b\u001a\u0010\u0010R(\u0010\u000b\u001a\f\u0012\u0006\u0012\u0004\u0018\u00010\b\u0018\u00010\u00058\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u001b\u0010\u000e\"\u0004\b\u001c\u0010\u0010\u00a8\u0006\u001d"}, d2 = {"Lcom/kg/gettransfer/data/model/Config;", "Lio/realm/RealmObject;", "id", "", "availableLocales", "Lio/realm/RealmList;", "Lcom/kg/gettransfer/data/model/secondary/Locale;", "preferredLocale", "", "supportedCurrencies", "Lcom/kg/gettransfer/data/model/secondary/Currency;", "supportedDistanceUnits", "(ILio/realm/RealmList;Ljava/lang/String;Lio/realm/RealmList;Lio/realm/RealmList;)V", "getAvailableLocales", "()Lio/realm/RealmList;", "setAvailableLocales", "(Lio/realm/RealmList;)V", "getId", "()I", "setId", "(I)V", "getPreferredLocale", "()Ljava/lang/String;", "setPreferredLocale", "(Ljava/lang/String;)V", "getSupportedCurrencies", "setSupportedCurrencies", "getSupportedDistanceUnits", "setSupportedDistanceUnits", "data_release"})
@io.realm.annotations.RealmClass()
public class Config extends io.realm.RealmObject {
    @io.realm.annotations.PrimaryKey()
    private int id;
    @org.jetbrains.annotations.Nullable()
    @com.google.gson.annotations.SerializedName(value = "available_locales")
    @com.google.gson.annotations.Expose()
    private io.realm.RealmList<com.kg.gettransfer.data.model.secondary.Locale> availableLocales;
    @org.jetbrains.annotations.Nullable()
    @com.google.gson.annotations.SerializedName(value = "preferred_locale")
    @com.google.gson.annotations.Expose()
    private java.lang.String preferredLocale;
    @org.jetbrains.annotations.Nullable()
    @com.google.gson.annotations.SerializedName(value = "supported_currencies")
    @com.google.gson.annotations.Expose()
    private io.realm.RealmList<com.kg.gettransfer.data.model.secondary.Currency> supportedCurrencies;
    @org.jetbrains.annotations.Nullable()
    @com.google.gson.annotations.SerializedName(value = "supported_distance_units")
    @com.google.gson.annotations.Expose()
    private io.realm.RealmList<java.lang.String> supportedDistanceUnits;
    
    public final int getId() {
        return 0;
    }
    
    public final void setId(int p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final io.realm.RealmList<com.kg.gettransfer.data.model.secondary.Locale> getAvailableLocales() {
        return null;
    }
    
    public final void setAvailableLocales(@org.jetbrains.annotations.Nullable()
    io.realm.RealmList<com.kg.gettransfer.data.model.secondary.Locale> p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getPreferredLocale() {
        return null;
    }
    
    public final void setPreferredLocale(@org.jetbrains.annotations.Nullable()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final io.realm.RealmList<com.kg.gettransfer.data.model.secondary.Currency> getSupportedCurrencies() {
        return null;
    }
    
    public final void setSupportedCurrencies(@org.jetbrains.annotations.Nullable()
    io.realm.RealmList<com.kg.gettransfer.data.model.secondary.Currency> p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final io.realm.RealmList<java.lang.String> getSupportedDistanceUnits() {
        return null;
    }
    
    public final void setSupportedDistanceUnits(@org.jetbrains.annotations.Nullable()
    io.realm.RealmList<java.lang.String> p0) {
    }
    
    public Config(int id, @org.jetbrains.annotations.Nullable()
    io.realm.RealmList<com.kg.gettransfer.data.model.secondary.Locale> availableLocales, @org.jetbrains.annotations.Nullable()
    java.lang.String preferredLocale, @org.jetbrains.annotations.Nullable()
    io.realm.RealmList<com.kg.gettransfer.data.model.secondary.Currency> supportedCurrencies, @org.jetbrains.annotations.Nullable()
    io.realm.RealmList<java.lang.String> supportedDistanceUnits) {
        super();
    }
    
    public Config() {
        super();
    }
}