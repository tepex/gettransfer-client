package com.kg.gettransfer.data.model;

import java.lang.System;

@kotlin.Metadata(mv = {1, 1, 10}, bv = {1, 0, 2}, k = 1, d1 = {"\u0000X\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0005\n\u0002\u0010\u000e\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\b\n\u0002\b\u0019\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0010\t\n\u0002\b\"\n\u0002\u0010\u0002\n\u0002\b\u0003\b\u0017\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0006\u0010r\u001a\u00020\u0004J\u0006\u0010s\u001a\u00020\u0004J\u0010\u0010t\u001a\u00020u2\b\u0010v\u001a\u0004\u0018\u00010\u0000J\u0006\u0010w\u001a\u00020uR\u001e\u0010\u0003\u001a\u00020\u00048\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0005\u0010\u0006\"\u0004\b\u0007\u0010\bR \u0010\t\u001a\u0004\u0018\u00010\n8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u000b\u0010\f\"\u0004\b\r\u0010\u000eR \u0010\u000f\u001a\u0004\u0018\u00010\n8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0010\u0010\f\"\u0004\b\u0011\u0010\u000eR \u0010\u0012\u001a\u0004\u0018\u00010\u00138\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0014\u0010\u0015\"\u0004\b\u0016\u0010\u0017R \u0010\u0018\u001a\u0004\u0018\u00010\u00198\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u001a\u0010\u001b\"\u0004\b\u001c\u0010\u001dR \u0010\u001e\u001a\u0004\u0018\u00010\u00198\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u001f\u0010\u001b\"\u0004\b \u0010\u001dR \u0010!\u001a\u0004\u0018\u00010\n8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\"\u0010\f\"\u0004\b#\u0010\u000eR \u0010$\u001a\u0004\u0018\u00010%8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b&\u0010\'\"\u0004\b(\u0010)R\"\u0010*\u001a\u0004\u0018\u00010+8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0010\n\u0002\u00100\u001a\u0004\b,\u0010-\"\u0004\b.\u0010/R\u001e\u00101\u001a\u00020+8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b2\u00103\"\u0004\b4\u00105R\u0011\u00106\u001a\u00020\u00048F\u00a2\u0006\u0006\u001a\u0004\b7\u0010\u0006R\u001a\u00108\u001a\u00020\u0004X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b8\u0010\u0006\"\u0004\b9\u0010\bR\u0011\u0010:\u001a\u00020\u00048F\u00a2\u0006\u0006\u001a\u0004\b;\u0010\u0006R \u0010<\u001a\u0004\u0018\u00010\n8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b=\u0010\f\"\u0004\b>\u0010\u000eR \u0010?\u001a\u0004\u0018\u00010\n8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b@\u0010\f\"\u0004\bA\u0010\u000eR\u0011\u0010B\u001a\u00020\u00048F\u00a2\u0006\u0006\u001a\u0004\bC\u0010\u0006R \u0010D\u001a\b\u0012\u0004\u0012\u00020F0EX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\bG\u0010H\"\u0004\bI\u0010JR \u0010K\u001a\u0004\u0018\u00010\u00138\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\bL\u0010\u0015\"\u0004\bM\u0010\u0017R\u001e\u0010N\u001a\u00020+8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\bO\u00103\"\u0004\bP\u00105R\u001a\u0010Q\u001a\u00020RX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\bS\u0010T\"\u0004\bU\u0010VR\u001a\u0010W\u001a\u00020RX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\bX\u0010T\"\u0004\bY\u0010VR\u001e\u0010Z\u001a\u00020+8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b[\u00103\"\u0004\b\\\u00105R\"\u0010]\u001a\u0004\u0018\u00010+8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0010\n\u0002\u00100\u001a\u0004\b^\u0010-\"\u0004\b_\u0010/R\"\u0010`\u001a\u0004\u0018\u00010+8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0010\n\u0002\u00100\u001a\u0004\ba\u0010-\"\u0004\bb\u0010/R\"\u0010c\u001a\u0004\u0018\u00010+8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0010\n\u0002\u00100\u001a\u0004\bd\u0010-\"\u0004\be\u0010/R \u0010f\u001a\u0004\u0018\u00010\n8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\bg\u0010\f\"\u0004\bh\u0010\u000eR \u0010i\u001a\u0004\u0018\u00010%8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\bj\u0010\'\"\u0004\bk\u0010)R(\u0010l\u001a\f\u0012\u0006\u0012\u0004\u0018\u00010\n\u0018\u00010E8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\bm\u0010H\"\u0004\bn\u0010JR\u001c\u0010o\u001a\u0004\u0018\u00010\u0013X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\bp\u0010\u0015\"\u0004\bq\u0010\u0017\u00a8\u0006x"}, d2 = {"Lcom/kg/gettransfer/data/model/Transfer;", "Lio/realm/RealmObject;", "()V", "bookNow", "", "getBookNow", "()Z", "setBookNow", "(Z)V", "childSeats", "", "getChildSeats", "()Ljava/lang/String;", "setChildSeats", "(Ljava/lang/String;)V", "comment", "getComment", "setComment", "dateRefund", "Ljava/util/Date;", "getDateRefund", "()Ljava/util/Date;", "setDateRefund", "(Ljava/util/Date;)V", "dateReturn", "Lcom/kg/gettransfer/data/model/secondary/ZonedDate;", "getDateReturn", "()Lcom/kg/gettransfer/data/model/secondary/ZonedDate;", "setDateReturn", "(Lcom/kg/gettransfer/data/model/secondary/ZonedDate;)V", "dateTo", "getDateTo", "setDateTo", "flightNumber", "getFlightNumber", "setFlightNumber", "from", "Lcom/kg/gettransfer/data/model/secondary/Location;", "getFrom", "()Lcom/kg/gettransfer/data/model/secondary/Location;", "setFrom", "(Lcom/kg/gettransfer/data/model/secondary/Location;)V", "hireDuration", "", "getHireDuration", "()Ljava/lang/Integer;", "setHireDuration", "(Ljava/lang/Integer;)V", "Ljava/lang/Integer;", "id", "getId", "()I", "setId", "(I)V", "idValid", "getIdValid", "isActive", "setActive", "justUpdated", "getJustUpdated", "malinaCard", "getMalinaCard", "setMalinaCard", "nameSign", "getNameSign", "setNameSign", "needAndCanUpdateOffers", "getNeedAndCanUpdateOffers", "offers", "Lio/realm/RealmList;", "Lcom/kg/gettransfer/data/model/Offer;", "getOffers", "()Lio/realm/RealmList;", "setOffers", "(Lio/realm/RealmList;)V", "offersChangedDate", "getOffersChangedDate", "setOffersChangedDate", "offersCount", "getOffersCount", "setOffersCount", "offersTriedToUpdateDate", "", "getOffersTriedToUpdateDate", "()J", "setOffersTriedToUpdateDate", "(J)V", "offersUpdatedDate", "getOffersUpdatedDate", "setOffersUpdatedDate", "pax", "getPax", "setPax", "relevantCarrierProfilesCount", "getRelevantCarrierProfilesCount", "setRelevantCarrierProfilesCount", "routeDistance", "getRouteDistance", "setRouteDistance", "routeDuration", "getRouteDuration", "setRouteDuration", "status", "getStatus", "setStatus", "to", "getTo", "setTo", "transportTypes", "getTransportTypes", "setTransportTypes", "updated", "getUpdated", "setUpdated", "isActiveConfirmed", "isActiveNew", "populateFromOldTransfer", "", "oldTransfer", "update", "data_release"})
@io.realm.annotations.RealmClass()
public class Transfer extends io.realm.RealmObject {
    @io.realm.annotations.PrimaryKey()
    @com.google.gson.annotations.SerializedName(value = "id")
    @com.google.gson.annotations.Expose()
    private int id;
    @org.jetbrains.annotations.Nullable()
    private java.util.Date updated;
    private boolean isActive;
    @org.jetbrains.annotations.Nullable()
    @com.google.gson.annotations.SerializedName(value = "from")
    @com.google.gson.annotations.Expose()
    private com.kg.gettransfer.data.model.secondary.Location from;
    @org.jetbrains.annotations.Nullable()
    @com.google.gson.annotations.SerializedName(value = "to")
    @com.google.gson.annotations.Expose()
    private com.kg.gettransfer.data.model.secondary.Location to;
    @org.jetbrains.annotations.Nullable()
    @com.google.gson.annotations.SerializedName(value = "duration")
    @com.google.gson.annotations.Expose()
    private java.lang.Integer hireDuration;
    @org.jetbrains.annotations.Nullable()
    @com.google.gson.annotations.SerializedName(value = "distance")
    @com.google.gson.annotations.Expose()
    private java.lang.Integer routeDistance;
    @org.jetbrains.annotations.Nullable()
    @com.google.gson.annotations.SerializedName(value = "time")
    @com.google.gson.annotations.Expose()
    private java.lang.Integer routeDuration;
    @org.jetbrains.annotations.Nullable()
    @com.google.gson.annotations.SerializedName(value = "status")
    @com.google.gson.annotations.Expose()
    private java.lang.String status;
    @com.google.gson.annotations.SerializedName(value = "book_now")
    @com.google.gson.annotations.Expose()
    private boolean bookNow;
    @org.jetbrains.annotations.Nullable()
    @com.google.gson.annotations.SerializedName(value = "date_to_local")
    @com.google.gson.annotations.Expose()
    private com.kg.gettransfer.data.model.secondary.ZonedDate dateTo;
    @org.jetbrains.annotations.Nullable()
    @com.google.gson.annotations.SerializedName(value = "date_return_local")
    @com.google.gson.annotations.Expose()
    private com.kg.gettransfer.data.model.secondary.ZonedDate dateReturn;
    @org.jetbrains.annotations.Nullable()
    @com.google.gson.annotations.SerializedName(value = "date_refund")
    @com.google.gson.annotations.Expose()
    private java.util.Date dateRefund;
    @com.google.gson.annotations.SerializedName(value = "pax")
    @com.google.gson.annotations.Expose()
    private int pax;
    @org.jetbrains.annotations.Nullable()
    @com.google.gson.annotations.SerializedName(value = "name_sign")
    @com.google.gson.annotations.Expose()
    private java.lang.String nameSign;
    @org.jetbrains.annotations.Nullable()
    @io.realm.annotations.Required()
    @com.google.gson.annotations.SerializedName(value = "transport_type_ids")
    @com.google.gson.annotations.Expose()
    private io.realm.RealmList<java.lang.String> transportTypes;
    @org.jetbrains.annotations.Nullable()
    @com.google.gson.annotations.SerializedName(value = "child_seats")
    @com.google.gson.annotations.Expose()
    private java.lang.String childSeats;
    @org.jetbrains.annotations.Nullable()
    @com.google.gson.annotations.SerializedName(value = "comment")
    @com.google.gson.annotations.Expose()
    private java.lang.String comment;
    @org.jetbrains.annotations.Nullable()
    @com.google.gson.annotations.SerializedName(value = "flight_number")
    @com.google.gson.annotations.Expose()
    private java.lang.String flightNumber;
    @com.google.gson.annotations.SerializedName(value = "offers_count")
    @com.google.gson.annotations.Expose()
    private int offersCount;
    @org.jetbrains.annotations.Nullable()
    @com.google.gson.annotations.SerializedName(value = "offers_updated_at")
    @com.google.gson.annotations.Expose()
    private java.util.Date offersChangedDate;
    private long offersTriedToUpdateDate;
    private long offersUpdatedDate;
    @org.jetbrains.annotations.Nullable()
    @com.google.gson.annotations.SerializedName(value = "relevant_carrier_profiles_count")
    @com.google.gson.annotations.Expose()
    private java.lang.Integer relevantCarrierProfilesCount;
    @org.jetbrains.annotations.Nullable()
    @com.google.gson.annotations.SerializedName(value = "malina_card")
    @com.google.gson.annotations.Expose()
    private java.lang.String malinaCard;
    @org.jetbrains.annotations.NotNull()
    private io.realm.RealmList<com.kg.gettransfer.data.model.Offer> offers;
    
    public final int getId() {
        return 0;
    }
    
    public final void setId(int p0) {
    }
    
    public final boolean getIdValid() {
        return false;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.util.Date getUpdated() {
        return null;
    }
    
    public final void setUpdated(@org.jetbrains.annotations.Nullable()
    java.util.Date p0) {
    }
    
    public final boolean getJustUpdated() {
        return false;
    }
    
    public final boolean isActive() {
        return false;
    }
    
    public final void setActive(boolean p0) {
    }
    
    public final void update() {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.kg.gettransfer.data.model.secondary.Location getFrom() {
        return null;
    }
    
    public final void setFrom(@org.jetbrains.annotations.Nullable()
    com.kg.gettransfer.data.model.secondary.Location p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.kg.gettransfer.data.model.secondary.Location getTo() {
        return null;
    }
    
    public final void setTo(@org.jetbrains.annotations.Nullable()
    com.kg.gettransfer.data.model.secondary.Location p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer getHireDuration() {
        return null;
    }
    
    public final void setHireDuration(@org.jetbrains.annotations.Nullable()
    java.lang.Integer p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer getRouteDistance() {
        return null;
    }
    
    public final void setRouteDistance(@org.jetbrains.annotations.Nullable()
    java.lang.Integer p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer getRouteDuration() {
        return null;
    }
    
    public final void setRouteDuration(@org.jetbrains.annotations.Nullable()
    java.lang.Integer p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getStatus() {
        return null;
    }
    
    public final void setStatus(@org.jetbrains.annotations.Nullable()
    java.lang.String p0) {
    }
    
    public final boolean getBookNow() {
        return false;
    }
    
    public final void setBookNow(boolean p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.kg.gettransfer.data.model.secondary.ZonedDate getDateTo() {
        return null;
    }
    
    public final void setDateTo(@org.jetbrains.annotations.Nullable()
    com.kg.gettransfer.data.model.secondary.ZonedDate p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.kg.gettransfer.data.model.secondary.ZonedDate getDateReturn() {
        return null;
    }
    
    public final void setDateReturn(@org.jetbrains.annotations.Nullable()
    com.kg.gettransfer.data.model.secondary.ZonedDate p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.util.Date getDateRefund() {
        return null;
    }
    
    public final void setDateRefund(@org.jetbrains.annotations.Nullable()
    java.util.Date p0) {
    }
    
    public final int getPax() {
        return 0;
    }
    
    public final void setPax(int p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getNameSign() {
        return null;
    }
    
    public final void setNameSign(@org.jetbrains.annotations.Nullable()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final io.realm.RealmList<java.lang.String> getTransportTypes() {
        return null;
    }
    
    public final void setTransportTypes(@org.jetbrains.annotations.Nullable()
    io.realm.RealmList<java.lang.String> p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getChildSeats() {
        return null;
    }
    
    public final void setChildSeats(@org.jetbrains.annotations.Nullable()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getComment() {
        return null;
    }
    
    public final void setComment(@org.jetbrains.annotations.Nullable()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getFlightNumber() {
        return null;
    }
    
    public final void setFlightNumber(@org.jetbrains.annotations.Nullable()
    java.lang.String p0) {
    }
    
    public final int getOffersCount() {
        return 0;
    }
    
    public final void setOffersCount(int p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.util.Date getOffersChangedDate() {
        return null;
    }
    
    public final void setOffersChangedDate(@org.jetbrains.annotations.Nullable()
    java.util.Date p0) {
    }
    
    public final long getOffersTriedToUpdateDate() {
        return 0L;
    }
    
    public final void setOffersTriedToUpdateDate(long p0) {
    }
    
    public final long getOffersUpdatedDate() {
        return 0L;
    }
    
    public final void setOffersUpdatedDate(long p0) {
    }
    
    public final boolean getNeedAndCanUpdateOffers() {
        return false;
    }
    
    public final void populateFromOldTransfer(@org.jetbrains.annotations.Nullable()
    com.kg.gettransfer.data.model.Transfer oldTransfer) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer getRelevantCarrierProfilesCount() {
        return null;
    }
    
    public final void setRelevantCarrierProfilesCount(@org.jetbrains.annotations.Nullable()
    java.lang.Integer p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getMalinaCard() {
        return null;
    }
    
    public final void setMalinaCard(@org.jetbrains.annotations.Nullable()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final io.realm.RealmList<com.kg.gettransfer.data.model.Offer> getOffers() {
        return null;
    }
    
    public final void setOffers(@org.jetbrains.annotations.NotNull()
    io.realm.RealmList<com.kg.gettransfer.data.model.Offer> p0) {
    }
    
    public final boolean isActiveNew() {
        return false;
    }
    
    public final boolean isActiveConfirmed() {
        return false;
    }
    
    public Transfer() {
        super();
    }
}