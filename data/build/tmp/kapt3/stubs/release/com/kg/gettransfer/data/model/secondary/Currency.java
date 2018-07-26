package com.kg.gettransfer.data.model.secondary;

import java.lang.System;

@kotlin.Metadata(mv = {1, 1, 10}, bv = {1, 0, 2}, k = 1, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\t\b\u0017\u0018\u00002\u00020\u0001B\u0019\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0005R\u001e\u0010\u0002\u001a\u00020\u00038\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0006\u0010\u0007\"\u0004\b\b\u0010\tR\u001e\u0010\u0004\u001a\u00020\u00038\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\n\u0010\u0007\"\u0004\b\u000b\u0010\t\u00a8\u0006\f"}, d2 = {"Lcom/kg/gettransfer/data/model/secondary/Currency;", "Lio/realm/RealmObject;", "isoCode", "", "symbol", "(Ljava/lang/String;Ljava/lang/String;)V", "getIsoCode", "()Ljava/lang/String;", "setIsoCode", "(Ljava/lang/String;)V", "getSymbol", "setSymbol", "data_release"})
@io.realm.annotations.RealmClass()
public class Currency extends io.realm.RealmObject {
    @org.jetbrains.annotations.NotNull()
    @com.google.gson.annotations.SerializedName(value = "iso_code")
    @com.google.gson.annotations.Expose()
    private java.lang.String isoCode;
    @org.jetbrains.annotations.NotNull()
    @com.google.gson.annotations.SerializedName(value = "symbol")
    @com.google.gson.annotations.Expose()
    private java.lang.String symbol;
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getIsoCode() {
        return null;
    }
    
    public final void setIsoCode(@org.jetbrains.annotations.NotNull()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getSymbol() {
        return null;
    }
    
    public final void setSymbol(@org.jetbrains.annotations.NotNull()
    java.lang.String p0) {
    }
    
    public Currency(@org.jetbrains.annotations.NotNull()
    java.lang.String isoCode, @org.jetbrains.annotations.NotNull()
    java.lang.String symbol) {
        super();
    }
    
    public Currency() {
        super();
    }
}