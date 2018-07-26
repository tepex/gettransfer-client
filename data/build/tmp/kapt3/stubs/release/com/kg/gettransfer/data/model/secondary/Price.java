package com.kg.gettransfer.data.model.secondary;

import java.lang.System;

@kotlin.Metadata(mv = {1, 1, 10}, bv = {1, 0, 2}, k = 1, d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0006\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000e\n\u0002\b\f\b\u0017\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u001b\u001a\u00020\u0010H\u0016R\u001e\u0010\u0003\u001a\u00020\u00048\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0005\u0010\u0006\"\u0004\b\u0007\u0010\bR \u0010\t\u001a\u0004\u0018\u00010\n8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u000b\u0010\f\"\u0004\b\r\u0010\u000eR \u0010\u000f\u001a\u0004\u0018\u00010\u00108\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0011\u0010\u0012\"\u0004\b\u0013\u0010\u0014R \u0010\u0015\u001a\u0004\u0018\u00010\u00108\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0016\u0010\u0012\"\u0004\b\u0017\u0010\u0014R \u0010\u0018\u001a\u0004\u0018\u00010\n8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0019\u0010\f\"\u0004\b\u001a\u0010\u000e\u00a8\u0006\u001c"}, d2 = {"Lcom/kg/gettransfer/data/model/secondary/Price;", "Lio/realm/RealmObject;", "()V", "amount", "", "getAmount", "()D", "setAmount", "(D)V", "base", "Lcom/kg/gettransfer/data/model/secondary/PriceConverted;", "getBase", "()Lcom/kg/gettransfer/data/model/secondary/PriceConverted;", "setBase", "(Lcom/kg/gettransfer/data/model/secondary/PriceConverted;)V", "p30", "", "getP30", "()Ljava/lang/String;", "setP30", "(Ljava/lang/String;)V", "p70", "getP70", "setP70", "withoutDiscount", "getWithoutDiscount", "setWithoutDiscount", "toString", "data_release"})
@io.realm.annotations.RealmClass()
public class Price extends io.realm.RealmObject {
    @org.jetbrains.annotations.Nullable()
    @com.google.gson.annotations.SerializedName(value = "base")
    @com.google.gson.annotations.Expose()
    private com.kg.gettransfer.data.model.secondary.PriceConverted base;
    @org.jetbrains.annotations.Nullable()
    @com.google.gson.annotations.SerializedName(value = "percentage_30")
    @com.google.gson.annotations.Expose()
    private java.lang.String p30;
    @org.jetbrains.annotations.Nullable()
    @com.google.gson.annotations.SerializedName(value = "percentage_70")
    @com.google.gson.annotations.Expose()
    private java.lang.String p70;
    @org.jetbrains.annotations.Nullable()
    @com.google.gson.annotations.SerializedName(value = "without_discount")
    @com.google.gson.annotations.Expose()
    private com.kg.gettransfer.data.model.secondary.PriceConverted withoutDiscount;
    @com.google.gson.annotations.SerializedName(value = "amount")
    @com.google.gson.annotations.Expose()
    private double amount;
    
    @org.jetbrains.annotations.Nullable()
    public final com.kg.gettransfer.data.model.secondary.PriceConverted getBase() {
        return null;
    }
    
    public final void setBase(@org.jetbrains.annotations.Nullable()
    com.kg.gettransfer.data.model.secondary.PriceConverted p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getP30() {
        return null;
    }
    
    public final void setP30(@org.jetbrains.annotations.Nullable()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getP70() {
        return null;
    }
    
    public final void setP70(@org.jetbrains.annotations.Nullable()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.kg.gettransfer.data.model.secondary.PriceConverted getWithoutDiscount() {
        return null;
    }
    
    public final void setWithoutDiscount(@org.jetbrains.annotations.Nullable()
    com.kg.gettransfer.data.model.secondary.PriceConverted p0) {
    }
    
    public final double getAmount() {
        return 0.0;
    }
    
    public final void setAmount(double p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    @java.lang.Override()
    public java.lang.String toString() {
        return null;
    }
    
    public Price() {
        super();
    }
}