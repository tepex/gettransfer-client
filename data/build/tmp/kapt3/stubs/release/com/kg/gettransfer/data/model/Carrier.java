package com.kg.gettransfer.data.model;

import java.lang.System;

@kotlin.Metadata(mv = {1, 1, 10}, bv = {1, 0, 2}, k = 1, d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0006\n\u0002\u0010\b\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\b\b\u0017\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002R \u0010\u0003\u001a\u0004\u0018\u00010\u00048\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0005\u0010\u0006\"\u0004\b\u0007\u0010\bR\"\u0010\t\u001a\u0004\u0018\u00010\n8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0010\n\u0002\u0010\u000f\u001a\u0004\b\u000b\u0010\f\"\u0004\b\r\u0010\u000eR\u001e\u0010\u0010\u001a\u00020\u00118\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0012\u0010\u0013\"\u0004\b\u0014\u0010\u0015R \u0010\u0016\u001a\u0004\u0018\u00010\u00048\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0017\u0010\u0006\"\u0004\b\u0018\u0010\bR\u001e\u0010\u0019\u001a\u00020\u00118\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u001a\u0010\u0013\"\u0004\b\u001b\u0010\u0015R$\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\u001e0\u001d8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u001f\u0010 \"\u0004\b!\u0010\"R \u0010#\u001a\u0004\u0018\u00010\u00048\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b$\u0010\u0006\"\u0004\b%\u0010\bR \u0010&\u001a\u0004\u0018\u00010\'8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b(\u0010)\"\u0004\b*\u0010+R \u0010,\u001a\u0004\u0018\u00010\u00048\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b-\u0010\u0006\"\u0004\b.\u0010\b\u00a8\u0006/"}, d2 = {"Lcom/kg/gettransfer/data/model/Carrier;", "Lio/realm/RealmObject;", "()V", "alternatePhone", "", "getAlternatePhone", "()Ljava/lang/String;", "setAlternatePhone", "(Ljava/lang/String;)V", "approved", "", "getApproved", "()Ljava/lang/Boolean;", "setApproved", "(Ljava/lang/Boolean;)V", "Ljava/lang/Boolean;", "completedTransfers", "", "getCompletedTransfers", "()I", "setCompletedTransfers", "(I)V", "email", "getEmail", "setEmail", "id", "getId", "setId", "languages", "Lio/realm/RealmList;", "Lcom/kg/gettransfer/data/model/secondary/Language;", "getLanguages", "()Lio/realm/RealmList;", "setLanguages", "(Lio/realm/RealmList;)V", "phone", "getPhone", "setPhone", "rating", "Lcom/kg/gettransfer/data/model/secondary/Rating;", "getRating", "()Lcom/kg/gettransfer/data/model/secondary/Rating;", "setRating", "(Lcom/kg/gettransfer/data/model/secondary/Rating;)V", "title", "getTitle", "setTitle", "data_release"})
@io.realm.annotations.RealmClass()
public class Carrier extends io.realm.RealmObject {
    @io.realm.annotations.PrimaryKey()
    @com.google.gson.annotations.SerializedName(value = "id")
    @com.google.gson.annotations.Expose()
    private int id;
    @org.jetbrains.annotations.Nullable()
    @com.google.gson.annotations.SerializedName(value = "title")
    @com.google.gson.annotations.Expose()
    private java.lang.String title;
    @org.jetbrains.annotations.Nullable()
    @com.google.gson.annotations.SerializedName(value = "approved")
    @com.google.gson.annotations.Expose()
    private java.lang.Boolean approved;
    @com.google.gson.annotations.SerializedName(value = "completed_transfers")
    @com.google.gson.annotations.Expose()
    private int completedTransfers;
    @org.jetbrains.annotations.NotNull()
    @com.google.gson.annotations.SerializedName(value = "languages")
    @com.google.gson.annotations.Expose()
    private io.realm.RealmList<com.kg.gettransfer.data.model.secondary.Language> languages;
    @org.jetbrains.annotations.Nullable()
    @com.google.gson.annotations.SerializedName(value = "ratings")
    @com.google.gson.annotations.Expose()
    private com.kg.gettransfer.data.model.secondary.Rating rating;
    @org.jetbrains.annotations.Nullable()
    @com.google.gson.annotations.SerializedName(value = "email")
    @com.google.gson.annotations.Expose()
    private java.lang.String email;
    @org.jetbrains.annotations.Nullable()
    @com.google.gson.annotations.SerializedName(value = "phone")
    @com.google.gson.annotations.Expose()
    private java.lang.String phone;
    @org.jetbrains.annotations.Nullable()
    @com.google.gson.annotations.SerializedName(value = "alternate_phone")
    @com.google.gson.annotations.Expose()
    private java.lang.String alternatePhone;
    
    public final int getId() {
        return 0;
    }
    
    public final void setId(int p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getTitle() {
        return null;
    }
    
    public final void setTitle(@org.jetbrains.annotations.Nullable()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Boolean getApproved() {
        return null;
    }
    
    public final void setApproved(@org.jetbrains.annotations.Nullable()
    java.lang.Boolean p0) {
    }
    
    public final int getCompletedTransfers() {
        return 0;
    }
    
    public final void setCompletedTransfers(int p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final io.realm.RealmList<com.kg.gettransfer.data.model.secondary.Language> getLanguages() {
        return null;
    }
    
    public final void setLanguages(@org.jetbrains.annotations.NotNull()
    io.realm.RealmList<com.kg.gettransfer.data.model.secondary.Language> p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.kg.gettransfer.data.model.secondary.Rating getRating() {
        return null;
    }
    
    public final void setRating(@org.jetbrains.annotations.Nullable()
    com.kg.gettransfer.data.model.secondary.Rating p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getEmail() {
        return null;
    }
    
    public final void setEmail(@org.jetbrains.annotations.Nullable()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getPhone() {
        return null;
    }
    
    public final void setPhone(@org.jetbrains.annotations.Nullable()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getAlternatePhone() {
        return null;
    }
    
    public final void setAlternatePhone(@org.jetbrains.annotations.Nullable()
    java.lang.String p0) {
    }
    
    public Carrier() {
        super();
    }
}