package io.realm;


import android.annotation.TargetApi;
import android.os.Build;
import android.util.JsonReader;
import android.util.JsonToken;
import io.realm.ProxyUtils;
import io.realm.exceptions.RealmMigrationNeededException;
import io.realm.internal.ColumnInfo;
import io.realm.internal.OsList;
import io.realm.internal.OsObject;
import io.realm.internal.OsObjectSchemaInfo;
import io.realm.internal.OsSchemaInfo;
import io.realm.internal.Property;
import io.realm.internal.RealmObjectProxy;
import io.realm.internal.Row;
import io.realm.internal.Table;
import io.realm.internal.android.JsonUtils;
import io.realm.log.RealmLog;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@SuppressWarnings("all")
public class com_kg_gettransfer_data_model_TransferRealmProxy extends com.kg.gettransfer.data.model.Transfer
    implements RealmObjectProxy, com_kg_gettransfer_data_model_TransferRealmProxyInterface {

    static final class TransferColumnInfo extends ColumnInfo {
        long idIndex;
        long updatedIndex;
        long isActiveIndex;
        long fromIndex;
        long toIndex;
        long hireDurationIndex;
        long routeDistanceIndex;
        long routeDurationIndex;
        long statusIndex;
        long bookNowIndex;
        long dateToIndex;
        long dateReturnIndex;
        long dateRefundIndex;
        long paxIndex;
        long nameSignIndex;
        long transportTypesIndex;
        long childSeatsIndex;
        long commentIndex;
        long flightNumberIndex;
        long offersCountIndex;
        long offersChangedDateIndex;
        long offersTriedToUpdateDateIndex;
        long offersUpdatedDateIndex;
        long relevantCarrierProfilesCountIndex;
        long malinaCardIndex;
        long offersIndex;

        TransferColumnInfo(OsSchemaInfo schemaInfo) {
            super(26);
            OsObjectSchemaInfo objectSchemaInfo = schemaInfo.getObjectSchemaInfo("Transfer");
            this.idIndex = addColumnDetails("id", "id", objectSchemaInfo);
            this.updatedIndex = addColumnDetails("updated", "updated", objectSchemaInfo);
            this.isActiveIndex = addColumnDetails("isActive", "isActive", objectSchemaInfo);
            this.fromIndex = addColumnDetails("from", "from", objectSchemaInfo);
            this.toIndex = addColumnDetails("to", "to", objectSchemaInfo);
            this.hireDurationIndex = addColumnDetails("hireDuration", "hireDuration", objectSchemaInfo);
            this.routeDistanceIndex = addColumnDetails("routeDistance", "routeDistance", objectSchemaInfo);
            this.routeDurationIndex = addColumnDetails("routeDuration", "routeDuration", objectSchemaInfo);
            this.statusIndex = addColumnDetails("status", "status", objectSchemaInfo);
            this.bookNowIndex = addColumnDetails("bookNow", "bookNow", objectSchemaInfo);
            this.dateToIndex = addColumnDetails("dateTo", "dateTo", objectSchemaInfo);
            this.dateReturnIndex = addColumnDetails("dateReturn", "dateReturn", objectSchemaInfo);
            this.dateRefundIndex = addColumnDetails("dateRefund", "dateRefund", objectSchemaInfo);
            this.paxIndex = addColumnDetails("pax", "pax", objectSchemaInfo);
            this.nameSignIndex = addColumnDetails("nameSign", "nameSign", objectSchemaInfo);
            this.transportTypesIndex = addColumnDetails("transportTypes", "transportTypes", objectSchemaInfo);
            this.childSeatsIndex = addColumnDetails("childSeats", "childSeats", objectSchemaInfo);
            this.commentIndex = addColumnDetails("comment", "comment", objectSchemaInfo);
            this.flightNumberIndex = addColumnDetails("flightNumber", "flightNumber", objectSchemaInfo);
            this.offersCountIndex = addColumnDetails("offersCount", "offersCount", objectSchemaInfo);
            this.offersChangedDateIndex = addColumnDetails("offersChangedDate", "offersChangedDate", objectSchemaInfo);
            this.offersTriedToUpdateDateIndex = addColumnDetails("offersTriedToUpdateDate", "offersTriedToUpdateDate", objectSchemaInfo);
            this.offersUpdatedDateIndex = addColumnDetails("offersUpdatedDate", "offersUpdatedDate", objectSchemaInfo);
            this.relevantCarrierProfilesCountIndex = addColumnDetails("relevantCarrierProfilesCount", "relevantCarrierProfilesCount", objectSchemaInfo);
            this.malinaCardIndex = addColumnDetails("malinaCard", "malinaCard", objectSchemaInfo);
            this.offersIndex = addColumnDetails("offers", "offers", objectSchemaInfo);
        }

        TransferColumnInfo(ColumnInfo src, boolean mutable) {
            super(src, mutable);
            copy(src, this);
        }

        @Override
        protected final ColumnInfo copy(boolean mutable) {
            return new TransferColumnInfo(this, mutable);
        }

        @Override
        protected final void copy(ColumnInfo rawSrc, ColumnInfo rawDst) {
            final TransferColumnInfo src = (TransferColumnInfo) rawSrc;
            final TransferColumnInfo dst = (TransferColumnInfo) rawDst;
            dst.idIndex = src.idIndex;
            dst.updatedIndex = src.updatedIndex;
            dst.isActiveIndex = src.isActiveIndex;
            dst.fromIndex = src.fromIndex;
            dst.toIndex = src.toIndex;
            dst.hireDurationIndex = src.hireDurationIndex;
            dst.routeDistanceIndex = src.routeDistanceIndex;
            dst.routeDurationIndex = src.routeDurationIndex;
            dst.statusIndex = src.statusIndex;
            dst.bookNowIndex = src.bookNowIndex;
            dst.dateToIndex = src.dateToIndex;
            dst.dateReturnIndex = src.dateReturnIndex;
            dst.dateRefundIndex = src.dateRefundIndex;
            dst.paxIndex = src.paxIndex;
            dst.nameSignIndex = src.nameSignIndex;
            dst.transportTypesIndex = src.transportTypesIndex;
            dst.childSeatsIndex = src.childSeatsIndex;
            dst.commentIndex = src.commentIndex;
            dst.flightNumberIndex = src.flightNumberIndex;
            dst.offersCountIndex = src.offersCountIndex;
            dst.offersChangedDateIndex = src.offersChangedDateIndex;
            dst.offersTriedToUpdateDateIndex = src.offersTriedToUpdateDateIndex;
            dst.offersUpdatedDateIndex = src.offersUpdatedDateIndex;
            dst.relevantCarrierProfilesCountIndex = src.relevantCarrierProfilesCountIndex;
            dst.malinaCardIndex = src.malinaCardIndex;
            dst.offersIndex = src.offersIndex;
        }
    }

    private static final OsObjectSchemaInfo expectedObjectSchemaInfo = createExpectedObjectSchemaInfo();

    private TransferColumnInfo columnInfo;
    private ProxyState<com.kg.gettransfer.data.model.Transfer> proxyState;
    private RealmList<String> transportTypesRealmList;
    private RealmList<com.kg.gettransfer.data.model.Offer> offersRealmList;

    com_kg_gettransfer_data_model_TransferRealmProxy() {
        proxyState.setConstructionFinished();
    }

    @Override
    public void realm$injectObjectContext() {
        if (this.proxyState != null) {
            return;
        }
        final BaseRealm.RealmObjectContext context = BaseRealm.objectContext.get();
        this.columnInfo = (TransferColumnInfo) context.getColumnInfo();
        this.proxyState = new ProxyState<com.kg.gettransfer.data.model.Transfer>(this);
        proxyState.setRealm$realm(context.getRealm());
        proxyState.setRow$realm(context.getRow());
        proxyState.setAcceptDefaultValue$realm(context.getAcceptDefaultValue());
        proxyState.setExcludeFields$realm(context.getExcludeFields());
    }

    @Override
    @SuppressWarnings("cast")
    public int realmGet$id() {
        proxyState.getRealm$realm().checkIfValid();
        return (int) proxyState.getRow$realm().getLong(columnInfo.idIndex);
    }

    @Override
    public void realmSet$id(int value) {
        if (proxyState.isUnderConstruction()) {
            // default value of the primary key is always ignored.
            return;
        }

        proxyState.getRealm$realm().checkIfValid();
        throw new io.realm.exceptions.RealmException("Primary key field 'id' cannot be changed after object was created.");
    }

    @Override
    @SuppressWarnings("cast")
    public Date realmGet$updated() {
        proxyState.getRealm$realm().checkIfValid();
        if (proxyState.getRow$realm().isNull(columnInfo.updatedIndex)) {
            return null;
        }
        return (java.util.Date) proxyState.getRow$realm().getDate(columnInfo.updatedIndex);
    }

    @Override
    public void realmSet$updated(Date value) {
        if (proxyState.isUnderConstruction()) {
            if (!proxyState.getAcceptDefaultValue$realm()) {
                return;
            }
            final Row row = proxyState.getRow$realm();
            if (value == null) {
                row.getTable().setNull(columnInfo.updatedIndex, row.getIndex(), true);
                return;
            }
            row.getTable().setDate(columnInfo.updatedIndex, row.getIndex(), value, true);
            return;
        }

        proxyState.getRealm$realm().checkIfValid();
        if (value == null) {
            proxyState.getRow$realm().setNull(columnInfo.updatedIndex);
            return;
        }
        proxyState.getRow$realm().setDate(columnInfo.updatedIndex, value);
    }

    @Override
    @SuppressWarnings("cast")
    public boolean realmGet$isActive() {
        proxyState.getRealm$realm().checkIfValid();
        return (boolean) proxyState.getRow$realm().getBoolean(columnInfo.isActiveIndex);
    }

    @Override
    public void realmSet$isActive(boolean value) {
        if (proxyState.isUnderConstruction()) {
            if (!proxyState.getAcceptDefaultValue$realm()) {
                return;
            }
            final Row row = proxyState.getRow$realm();
            row.getTable().setBoolean(columnInfo.isActiveIndex, row.getIndex(), value, true);
            return;
        }

        proxyState.getRealm$realm().checkIfValid();
        proxyState.getRow$realm().setBoolean(columnInfo.isActiveIndex, value);
    }

    @Override
    public com.kg.gettransfer.data.model.secondary.Location realmGet$from() {
        proxyState.getRealm$realm().checkIfValid();
        if (proxyState.getRow$realm().isNullLink(columnInfo.fromIndex)) {
            return null;
        }
        return proxyState.getRealm$realm().get(com.kg.gettransfer.data.model.secondary.Location.class, proxyState.getRow$realm().getLink(columnInfo.fromIndex), false, Collections.<String>emptyList());
    }

    @Override
    public void realmSet$from(com.kg.gettransfer.data.model.secondary.Location value) {
        if (proxyState.isUnderConstruction()) {
            if (!proxyState.getAcceptDefaultValue$realm()) {
                return;
            }
            if (proxyState.getExcludeFields$realm().contains("from")) {
                return;
            }
            if (value != null && !RealmObject.isManaged(value)) {
                value = ((Realm) proxyState.getRealm$realm()).copyToRealm(value);
            }
            final Row row = proxyState.getRow$realm();
            if (value == null) {
                // Table#nullifyLink() does not support default value. Just using Row.
                row.nullifyLink(columnInfo.fromIndex);
                return;
            }
            proxyState.checkValidObject(value);
            row.getTable().setLink(columnInfo.fromIndex, row.getIndex(), ((RealmObjectProxy) value).realmGet$proxyState().getRow$realm().getIndex(), true);
            return;
        }

        proxyState.getRealm$realm().checkIfValid();
        if (value == null) {
            proxyState.getRow$realm().nullifyLink(columnInfo.fromIndex);
            return;
        }
        proxyState.checkValidObject(value);
        proxyState.getRow$realm().setLink(columnInfo.fromIndex, ((RealmObjectProxy) value).realmGet$proxyState().getRow$realm().getIndex());
    }

    @Override
    public com.kg.gettransfer.data.model.secondary.Location realmGet$to() {
        proxyState.getRealm$realm().checkIfValid();
        if (proxyState.getRow$realm().isNullLink(columnInfo.toIndex)) {
            return null;
        }
        return proxyState.getRealm$realm().get(com.kg.gettransfer.data.model.secondary.Location.class, proxyState.getRow$realm().getLink(columnInfo.toIndex), false, Collections.<String>emptyList());
    }

    @Override
    public void realmSet$to(com.kg.gettransfer.data.model.secondary.Location value) {
        if (proxyState.isUnderConstruction()) {
            if (!proxyState.getAcceptDefaultValue$realm()) {
                return;
            }
            if (proxyState.getExcludeFields$realm().contains("to")) {
                return;
            }
            if (value != null && !RealmObject.isManaged(value)) {
                value = ((Realm) proxyState.getRealm$realm()).copyToRealm(value);
            }
            final Row row = proxyState.getRow$realm();
            if (value == null) {
                // Table#nullifyLink() does not support default value. Just using Row.
                row.nullifyLink(columnInfo.toIndex);
                return;
            }
            proxyState.checkValidObject(value);
            row.getTable().setLink(columnInfo.toIndex, row.getIndex(), ((RealmObjectProxy) value).realmGet$proxyState().getRow$realm().getIndex(), true);
            return;
        }

        proxyState.getRealm$realm().checkIfValid();
        if (value == null) {
            proxyState.getRow$realm().nullifyLink(columnInfo.toIndex);
            return;
        }
        proxyState.checkValidObject(value);
        proxyState.getRow$realm().setLink(columnInfo.toIndex, ((RealmObjectProxy) value).realmGet$proxyState().getRow$realm().getIndex());
    }

    @Override
    @SuppressWarnings("cast")
    public Integer realmGet$hireDuration() {
        proxyState.getRealm$realm().checkIfValid();
        if (proxyState.getRow$realm().isNull(columnInfo.hireDurationIndex)) {
            return null;
        }
        return (int) proxyState.getRow$realm().getLong(columnInfo.hireDurationIndex);
    }

    @Override
    public void realmSet$hireDuration(Integer value) {
        if (proxyState.isUnderConstruction()) {
            if (!proxyState.getAcceptDefaultValue$realm()) {
                return;
            }
            final Row row = proxyState.getRow$realm();
            if (value == null) {
                row.getTable().setNull(columnInfo.hireDurationIndex, row.getIndex(), true);
                return;
            }
            row.getTable().setLong(columnInfo.hireDurationIndex, row.getIndex(), value, true);
            return;
        }

        proxyState.getRealm$realm().checkIfValid();
        if (value == null) {
            proxyState.getRow$realm().setNull(columnInfo.hireDurationIndex);
            return;
        }
        proxyState.getRow$realm().setLong(columnInfo.hireDurationIndex, value);
    }

    @Override
    @SuppressWarnings("cast")
    public Integer realmGet$routeDistance() {
        proxyState.getRealm$realm().checkIfValid();
        if (proxyState.getRow$realm().isNull(columnInfo.routeDistanceIndex)) {
            return null;
        }
        return (int) proxyState.getRow$realm().getLong(columnInfo.routeDistanceIndex);
    }

    @Override
    public void realmSet$routeDistance(Integer value) {
        if (proxyState.isUnderConstruction()) {
            if (!proxyState.getAcceptDefaultValue$realm()) {
                return;
            }
            final Row row = proxyState.getRow$realm();
            if (value == null) {
                row.getTable().setNull(columnInfo.routeDistanceIndex, row.getIndex(), true);
                return;
            }
            row.getTable().setLong(columnInfo.routeDistanceIndex, row.getIndex(), value, true);
            return;
        }

        proxyState.getRealm$realm().checkIfValid();
        if (value == null) {
            proxyState.getRow$realm().setNull(columnInfo.routeDistanceIndex);
            return;
        }
        proxyState.getRow$realm().setLong(columnInfo.routeDistanceIndex, value);
    }

    @Override
    @SuppressWarnings("cast")
    public Integer realmGet$routeDuration() {
        proxyState.getRealm$realm().checkIfValid();
        if (proxyState.getRow$realm().isNull(columnInfo.routeDurationIndex)) {
            return null;
        }
        return (int) proxyState.getRow$realm().getLong(columnInfo.routeDurationIndex);
    }

    @Override
    public void realmSet$routeDuration(Integer value) {
        if (proxyState.isUnderConstruction()) {
            if (!proxyState.getAcceptDefaultValue$realm()) {
                return;
            }
            final Row row = proxyState.getRow$realm();
            if (value == null) {
                row.getTable().setNull(columnInfo.routeDurationIndex, row.getIndex(), true);
                return;
            }
            row.getTable().setLong(columnInfo.routeDurationIndex, row.getIndex(), value, true);
            return;
        }

        proxyState.getRealm$realm().checkIfValid();
        if (value == null) {
            proxyState.getRow$realm().setNull(columnInfo.routeDurationIndex);
            return;
        }
        proxyState.getRow$realm().setLong(columnInfo.routeDurationIndex, value);
    }

    @Override
    @SuppressWarnings("cast")
    public String realmGet$status() {
        proxyState.getRealm$realm().checkIfValid();
        return (java.lang.String) proxyState.getRow$realm().getString(columnInfo.statusIndex);
    }

    @Override
    public void realmSet$status(String value) {
        if (proxyState.isUnderConstruction()) {
            if (!proxyState.getAcceptDefaultValue$realm()) {
                return;
            }
            final Row row = proxyState.getRow$realm();
            if (value == null) {
                row.getTable().setNull(columnInfo.statusIndex, row.getIndex(), true);
                return;
            }
            row.getTable().setString(columnInfo.statusIndex, row.getIndex(), value, true);
            return;
        }

        proxyState.getRealm$realm().checkIfValid();
        if (value == null) {
            proxyState.getRow$realm().setNull(columnInfo.statusIndex);
            return;
        }
        proxyState.getRow$realm().setString(columnInfo.statusIndex, value);
    }

    @Override
    @SuppressWarnings("cast")
    public boolean realmGet$bookNow() {
        proxyState.getRealm$realm().checkIfValid();
        return (boolean) proxyState.getRow$realm().getBoolean(columnInfo.bookNowIndex);
    }

    @Override
    public void realmSet$bookNow(boolean value) {
        if (proxyState.isUnderConstruction()) {
            if (!proxyState.getAcceptDefaultValue$realm()) {
                return;
            }
            final Row row = proxyState.getRow$realm();
            row.getTable().setBoolean(columnInfo.bookNowIndex, row.getIndex(), value, true);
            return;
        }

        proxyState.getRealm$realm().checkIfValid();
        proxyState.getRow$realm().setBoolean(columnInfo.bookNowIndex, value);
    }

    @Override
    public com.kg.gettransfer.data.model.secondary.ZonedDate realmGet$dateTo() {
        proxyState.getRealm$realm().checkIfValid();
        if (proxyState.getRow$realm().isNullLink(columnInfo.dateToIndex)) {
            return null;
        }
        return proxyState.getRealm$realm().get(com.kg.gettransfer.data.model.secondary.ZonedDate.class, proxyState.getRow$realm().getLink(columnInfo.dateToIndex), false, Collections.<String>emptyList());
    }

    @Override
    public void realmSet$dateTo(com.kg.gettransfer.data.model.secondary.ZonedDate value) {
        if (proxyState.isUnderConstruction()) {
            if (!proxyState.getAcceptDefaultValue$realm()) {
                return;
            }
            if (proxyState.getExcludeFields$realm().contains("dateTo")) {
                return;
            }
            if (value != null && !RealmObject.isManaged(value)) {
                value = ((Realm) proxyState.getRealm$realm()).copyToRealm(value);
            }
            final Row row = proxyState.getRow$realm();
            if (value == null) {
                // Table#nullifyLink() does not support default value. Just using Row.
                row.nullifyLink(columnInfo.dateToIndex);
                return;
            }
            proxyState.checkValidObject(value);
            row.getTable().setLink(columnInfo.dateToIndex, row.getIndex(), ((RealmObjectProxy) value).realmGet$proxyState().getRow$realm().getIndex(), true);
            return;
        }

        proxyState.getRealm$realm().checkIfValid();
        if (value == null) {
            proxyState.getRow$realm().nullifyLink(columnInfo.dateToIndex);
            return;
        }
        proxyState.checkValidObject(value);
        proxyState.getRow$realm().setLink(columnInfo.dateToIndex, ((RealmObjectProxy) value).realmGet$proxyState().getRow$realm().getIndex());
    }

    @Override
    public com.kg.gettransfer.data.model.secondary.ZonedDate realmGet$dateReturn() {
        proxyState.getRealm$realm().checkIfValid();
        if (proxyState.getRow$realm().isNullLink(columnInfo.dateReturnIndex)) {
            return null;
        }
        return proxyState.getRealm$realm().get(com.kg.gettransfer.data.model.secondary.ZonedDate.class, proxyState.getRow$realm().getLink(columnInfo.dateReturnIndex), false, Collections.<String>emptyList());
    }

    @Override
    public void realmSet$dateReturn(com.kg.gettransfer.data.model.secondary.ZonedDate value) {
        if (proxyState.isUnderConstruction()) {
            if (!proxyState.getAcceptDefaultValue$realm()) {
                return;
            }
            if (proxyState.getExcludeFields$realm().contains("dateReturn")) {
                return;
            }
            if (value != null && !RealmObject.isManaged(value)) {
                value = ((Realm) proxyState.getRealm$realm()).copyToRealm(value);
            }
            final Row row = proxyState.getRow$realm();
            if (value == null) {
                // Table#nullifyLink() does not support default value. Just using Row.
                row.nullifyLink(columnInfo.dateReturnIndex);
                return;
            }
            proxyState.checkValidObject(value);
            row.getTable().setLink(columnInfo.dateReturnIndex, row.getIndex(), ((RealmObjectProxy) value).realmGet$proxyState().getRow$realm().getIndex(), true);
            return;
        }

        proxyState.getRealm$realm().checkIfValid();
        if (value == null) {
            proxyState.getRow$realm().nullifyLink(columnInfo.dateReturnIndex);
            return;
        }
        proxyState.checkValidObject(value);
        proxyState.getRow$realm().setLink(columnInfo.dateReturnIndex, ((RealmObjectProxy) value).realmGet$proxyState().getRow$realm().getIndex());
    }

    @Override
    @SuppressWarnings("cast")
    public Date realmGet$dateRefund() {
        proxyState.getRealm$realm().checkIfValid();
        if (proxyState.getRow$realm().isNull(columnInfo.dateRefundIndex)) {
            return null;
        }
        return (java.util.Date) proxyState.getRow$realm().getDate(columnInfo.dateRefundIndex);
    }

    @Override
    public void realmSet$dateRefund(Date value) {
        if (proxyState.isUnderConstruction()) {
            if (!proxyState.getAcceptDefaultValue$realm()) {
                return;
            }
            final Row row = proxyState.getRow$realm();
            if (value == null) {
                row.getTable().setNull(columnInfo.dateRefundIndex, row.getIndex(), true);
                return;
            }
            row.getTable().setDate(columnInfo.dateRefundIndex, row.getIndex(), value, true);
            return;
        }

        proxyState.getRealm$realm().checkIfValid();
        if (value == null) {
            proxyState.getRow$realm().setNull(columnInfo.dateRefundIndex);
            return;
        }
        proxyState.getRow$realm().setDate(columnInfo.dateRefundIndex, value);
    }

    @Override
    @SuppressWarnings("cast")
    public int realmGet$pax() {
        proxyState.getRealm$realm().checkIfValid();
        return (int) proxyState.getRow$realm().getLong(columnInfo.paxIndex);
    }

    @Override
    public void realmSet$pax(int value) {
        if (proxyState.isUnderConstruction()) {
            if (!proxyState.getAcceptDefaultValue$realm()) {
                return;
            }
            final Row row = proxyState.getRow$realm();
            row.getTable().setLong(columnInfo.paxIndex, row.getIndex(), value, true);
            return;
        }

        proxyState.getRealm$realm().checkIfValid();
        proxyState.getRow$realm().setLong(columnInfo.paxIndex, value);
    }

    @Override
    @SuppressWarnings("cast")
    public String realmGet$nameSign() {
        proxyState.getRealm$realm().checkIfValid();
        return (java.lang.String) proxyState.getRow$realm().getString(columnInfo.nameSignIndex);
    }

    @Override
    public void realmSet$nameSign(String value) {
        if (proxyState.isUnderConstruction()) {
            if (!proxyState.getAcceptDefaultValue$realm()) {
                return;
            }
            final Row row = proxyState.getRow$realm();
            if (value == null) {
                row.getTable().setNull(columnInfo.nameSignIndex, row.getIndex(), true);
                return;
            }
            row.getTable().setString(columnInfo.nameSignIndex, row.getIndex(), value, true);
            return;
        }

        proxyState.getRealm$realm().checkIfValid();
        if (value == null) {
            proxyState.getRow$realm().setNull(columnInfo.nameSignIndex);
            return;
        }
        proxyState.getRow$realm().setString(columnInfo.nameSignIndex, value);
    }

    @Override
    public RealmList<String> realmGet$transportTypes() {
        proxyState.getRealm$realm().checkIfValid();
        // use the cached value if available
        if (transportTypesRealmList != null) {
            return transportTypesRealmList;
        } else {
            OsList osList = proxyState.getRow$realm().getValueList(columnInfo.transportTypesIndex, RealmFieldType.STRING_LIST);
            transportTypesRealmList = new RealmList<java.lang.String>(java.lang.String.class, osList, proxyState.getRealm$realm());
            return transportTypesRealmList;
        }
    }

    @Override
    public void realmSet$transportTypes(RealmList<String> value) {
        if (proxyState.isUnderConstruction()) {
            if (!proxyState.getAcceptDefaultValue$realm()) {
                return;
            }
            if (proxyState.getExcludeFields$realm().contains("transportTypes")) {
                return;
            }
        }

        proxyState.getRealm$realm().checkIfValid();
        OsList osList = proxyState.getRow$realm().getValueList(columnInfo.transportTypesIndex, RealmFieldType.STRING_LIST);
        osList.removeAll();
        if (value == null) {
            return;
        }
        for (java.lang.String item : value) {
            if (item == null) {
                throw new IllegalArgumentException("Storing 'null' into transportTypes' is not allowed by the schema.");
            } else {
                osList.addString(item);
            }
        }
    }

    @Override
    @SuppressWarnings("cast")
    public String realmGet$childSeats() {
        proxyState.getRealm$realm().checkIfValid();
        return (java.lang.String) proxyState.getRow$realm().getString(columnInfo.childSeatsIndex);
    }

    @Override
    public void realmSet$childSeats(String value) {
        if (proxyState.isUnderConstruction()) {
            if (!proxyState.getAcceptDefaultValue$realm()) {
                return;
            }
            final Row row = proxyState.getRow$realm();
            if (value == null) {
                row.getTable().setNull(columnInfo.childSeatsIndex, row.getIndex(), true);
                return;
            }
            row.getTable().setString(columnInfo.childSeatsIndex, row.getIndex(), value, true);
            return;
        }

        proxyState.getRealm$realm().checkIfValid();
        if (value == null) {
            proxyState.getRow$realm().setNull(columnInfo.childSeatsIndex);
            return;
        }
        proxyState.getRow$realm().setString(columnInfo.childSeatsIndex, value);
    }

    @Override
    @SuppressWarnings("cast")
    public String realmGet$comment() {
        proxyState.getRealm$realm().checkIfValid();
        return (java.lang.String) proxyState.getRow$realm().getString(columnInfo.commentIndex);
    }

    @Override
    public void realmSet$comment(String value) {
        if (proxyState.isUnderConstruction()) {
            if (!proxyState.getAcceptDefaultValue$realm()) {
                return;
            }
            final Row row = proxyState.getRow$realm();
            if (value == null) {
                row.getTable().setNull(columnInfo.commentIndex, row.getIndex(), true);
                return;
            }
            row.getTable().setString(columnInfo.commentIndex, row.getIndex(), value, true);
            return;
        }

        proxyState.getRealm$realm().checkIfValid();
        if (value == null) {
            proxyState.getRow$realm().setNull(columnInfo.commentIndex);
            return;
        }
        proxyState.getRow$realm().setString(columnInfo.commentIndex, value);
    }

    @Override
    @SuppressWarnings("cast")
    public String realmGet$flightNumber() {
        proxyState.getRealm$realm().checkIfValid();
        return (java.lang.String) proxyState.getRow$realm().getString(columnInfo.flightNumberIndex);
    }

    @Override
    public void realmSet$flightNumber(String value) {
        if (proxyState.isUnderConstruction()) {
            if (!proxyState.getAcceptDefaultValue$realm()) {
                return;
            }
            final Row row = proxyState.getRow$realm();
            if (value == null) {
                row.getTable().setNull(columnInfo.flightNumberIndex, row.getIndex(), true);
                return;
            }
            row.getTable().setString(columnInfo.flightNumberIndex, row.getIndex(), value, true);
            return;
        }

        proxyState.getRealm$realm().checkIfValid();
        if (value == null) {
            proxyState.getRow$realm().setNull(columnInfo.flightNumberIndex);
            return;
        }
        proxyState.getRow$realm().setString(columnInfo.flightNumberIndex, value);
    }

    @Override
    @SuppressWarnings("cast")
    public int realmGet$offersCount() {
        proxyState.getRealm$realm().checkIfValid();
        return (int) proxyState.getRow$realm().getLong(columnInfo.offersCountIndex);
    }

    @Override
    public void realmSet$offersCount(int value) {
        if (proxyState.isUnderConstruction()) {
            if (!proxyState.getAcceptDefaultValue$realm()) {
                return;
            }
            final Row row = proxyState.getRow$realm();
            row.getTable().setLong(columnInfo.offersCountIndex, row.getIndex(), value, true);
            return;
        }

        proxyState.getRealm$realm().checkIfValid();
        proxyState.getRow$realm().setLong(columnInfo.offersCountIndex, value);
    }

    @Override
    @SuppressWarnings("cast")
    public Date realmGet$offersChangedDate() {
        proxyState.getRealm$realm().checkIfValid();
        if (proxyState.getRow$realm().isNull(columnInfo.offersChangedDateIndex)) {
            return null;
        }
        return (java.util.Date) proxyState.getRow$realm().getDate(columnInfo.offersChangedDateIndex);
    }

    @Override
    public void realmSet$offersChangedDate(Date value) {
        if (proxyState.isUnderConstruction()) {
            if (!proxyState.getAcceptDefaultValue$realm()) {
                return;
            }
            final Row row = proxyState.getRow$realm();
            if (value == null) {
                row.getTable().setNull(columnInfo.offersChangedDateIndex, row.getIndex(), true);
                return;
            }
            row.getTable().setDate(columnInfo.offersChangedDateIndex, row.getIndex(), value, true);
            return;
        }

        proxyState.getRealm$realm().checkIfValid();
        if (value == null) {
            proxyState.getRow$realm().setNull(columnInfo.offersChangedDateIndex);
            return;
        }
        proxyState.getRow$realm().setDate(columnInfo.offersChangedDateIndex, value);
    }

    @Override
    @SuppressWarnings("cast")
    public long realmGet$offersTriedToUpdateDate() {
        proxyState.getRealm$realm().checkIfValid();
        return (long) proxyState.getRow$realm().getLong(columnInfo.offersTriedToUpdateDateIndex);
    }

    @Override
    public void realmSet$offersTriedToUpdateDate(long value) {
        if (proxyState.isUnderConstruction()) {
            if (!proxyState.getAcceptDefaultValue$realm()) {
                return;
            }
            final Row row = proxyState.getRow$realm();
            row.getTable().setLong(columnInfo.offersTriedToUpdateDateIndex, row.getIndex(), value, true);
            return;
        }

        proxyState.getRealm$realm().checkIfValid();
        proxyState.getRow$realm().setLong(columnInfo.offersTriedToUpdateDateIndex, value);
    }

    @Override
    @SuppressWarnings("cast")
    public long realmGet$offersUpdatedDate() {
        proxyState.getRealm$realm().checkIfValid();
        return (long) proxyState.getRow$realm().getLong(columnInfo.offersUpdatedDateIndex);
    }

    @Override
    public void realmSet$offersUpdatedDate(long value) {
        if (proxyState.isUnderConstruction()) {
            if (!proxyState.getAcceptDefaultValue$realm()) {
                return;
            }
            final Row row = proxyState.getRow$realm();
            row.getTable().setLong(columnInfo.offersUpdatedDateIndex, row.getIndex(), value, true);
            return;
        }

        proxyState.getRealm$realm().checkIfValid();
        proxyState.getRow$realm().setLong(columnInfo.offersUpdatedDateIndex, value);
    }

    @Override
    @SuppressWarnings("cast")
    public Integer realmGet$relevantCarrierProfilesCount() {
        proxyState.getRealm$realm().checkIfValid();
        if (proxyState.getRow$realm().isNull(columnInfo.relevantCarrierProfilesCountIndex)) {
            return null;
        }
        return (int) proxyState.getRow$realm().getLong(columnInfo.relevantCarrierProfilesCountIndex);
    }

    @Override
    public void realmSet$relevantCarrierProfilesCount(Integer value) {
        if (proxyState.isUnderConstruction()) {
            if (!proxyState.getAcceptDefaultValue$realm()) {
                return;
            }
            final Row row = proxyState.getRow$realm();
            if (value == null) {
                row.getTable().setNull(columnInfo.relevantCarrierProfilesCountIndex, row.getIndex(), true);
                return;
            }
            row.getTable().setLong(columnInfo.relevantCarrierProfilesCountIndex, row.getIndex(), value, true);
            return;
        }

        proxyState.getRealm$realm().checkIfValid();
        if (value == null) {
            proxyState.getRow$realm().setNull(columnInfo.relevantCarrierProfilesCountIndex);
            return;
        }
        proxyState.getRow$realm().setLong(columnInfo.relevantCarrierProfilesCountIndex, value);
    }

    @Override
    @SuppressWarnings("cast")
    public String realmGet$malinaCard() {
        proxyState.getRealm$realm().checkIfValid();
        return (java.lang.String) proxyState.getRow$realm().getString(columnInfo.malinaCardIndex);
    }

    @Override
    public void realmSet$malinaCard(String value) {
        if (proxyState.isUnderConstruction()) {
            if (!proxyState.getAcceptDefaultValue$realm()) {
                return;
            }
            final Row row = proxyState.getRow$realm();
            if (value == null) {
                row.getTable().setNull(columnInfo.malinaCardIndex, row.getIndex(), true);
                return;
            }
            row.getTable().setString(columnInfo.malinaCardIndex, row.getIndex(), value, true);
            return;
        }

        proxyState.getRealm$realm().checkIfValid();
        if (value == null) {
            proxyState.getRow$realm().setNull(columnInfo.malinaCardIndex);
            return;
        }
        proxyState.getRow$realm().setString(columnInfo.malinaCardIndex, value);
    }

    @Override
    public RealmList<com.kg.gettransfer.data.model.Offer> realmGet$offers() {
        proxyState.getRealm$realm().checkIfValid();
        // use the cached value if available
        if (offersRealmList != null) {
            return offersRealmList;
        } else {
            OsList osList = proxyState.getRow$realm().getModelList(columnInfo.offersIndex);
            offersRealmList = new RealmList<com.kg.gettransfer.data.model.Offer>(com.kg.gettransfer.data.model.Offer.class, osList, proxyState.getRealm$realm());
            return offersRealmList;
        }
    }

    @Override
    public void realmSet$offers(RealmList<com.kg.gettransfer.data.model.Offer> value) {
        if (proxyState.isUnderConstruction()) {
            if (!proxyState.getAcceptDefaultValue$realm()) {
                return;
            }
            if (proxyState.getExcludeFields$realm().contains("offers")) {
                return;
            }
            // if the list contains unmanaged RealmObjects, convert them to managed.
            if (value != null && !value.isManaged()) {
                final Realm realm = (Realm) proxyState.getRealm$realm();
                final RealmList<com.kg.gettransfer.data.model.Offer> original = value;
                value = new RealmList<com.kg.gettransfer.data.model.Offer>();
                for (com.kg.gettransfer.data.model.Offer item : original) {
                    if (item == null || RealmObject.isManaged(item)) {
                        value.add(item);
                    } else {
                        value.add(realm.copyToRealm(item));
                    }
                }
            }
        }

        proxyState.getRealm$realm().checkIfValid();
        OsList osList = proxyState.getRow$realm().getModelList(columnInfo.offersIndex);
        // For lists of equal lengths, we need to set each element directly as clearing the receiver list can be wrong if the input and target list are the same.
        if (value != null && value.size() == osList.size()) {
            int objects = value.size();
            for (int i = 0; i < objects; i++) {
                com.kg.gettransfer.data.model.Offer linkedObject = value.get(i);
                proxyState.checkValidObject(linkedObject);
                osList.setRow(i, ((RealmObjectProxy) linkedObject).realmGet$proxyState().getRow$realm().getIndex());
            }
        } else {
            osList.removeAll();
            if (value == null) {
                return;
            }
            int objects = value.size();
            for (int i = 0; i < objects; i++) {
                com.kg.gettransfer.data.model.Offer linkedObject = value.get(i);
                proxyState.checkValidObject(linkedObject);
                osList.addRow(((RealmObjectProxy) linkedObject).realmGet$proxyState().getRow$realm().getIndex());
            }
        }
    }

    private static OsObjectSchemaInfo createExpectedObjectSchemaInfo() {
        OsObjectSchemaInfo.Builder builder = new OsObjectSchemaInfo.Builder("Transfer", 26, 0);
        builder.addPersistedProperty("id", RealmFieldType.INTEGER, Property.PRIMARY_KEY, Property.INDEXED, Property.REQUIRED);
        builder.addPersistedProperty("updated", RealmFieldType.DATE, !Property.PRIMARY_KEY, !Property.INDEXED, !Property.REQUIRED);
        builder.addPersistedProperty("isActive", RealmFieldType.BOOLEAN, !Property.PRIMARY_KEY, !Property.INDEXED, Property.REQUIRED);
        builder.addPersistedLinkProperty("from", RealmFieldType.OBJECT, "Location");
        builder.addPersistedLinkProperty("to", RealmFieldType.OBJECT, "Location");
        builder.addPersistedProperty("hireDuration", RealmFieldType.INTEGER, !Property.PRIMARY_KEY, !Property.INDEXED, !Property.REQUIRED);
        builder.addPersistedProperty("routeDistance", RealmFieldType.INTEGER, !Property.PRIMARY_KEY, !Property.INDEXED, !Property.REQUIRED);
        builder.addPersistedProperty("routeDuration", RealmFieldType.INTEGER, !Property.PRIMARY_KEY, !Property.INDEXED, !Property.REQUIRED);
        builder.addPersistedProperty("status", RealmFieldType.STRING, !Property.PRIMARY_KEY, !Property.INDEXED, !Property.REQUIRED);
        builder.addPersistedProperty("bookNow", RealmFieldType.BOOLEAN, !Property.PRIMARY_KEY, !Property.INDEXED, Property.REQUIRED);
        builder.addPersistedLinkProperty("dateTo", RealmFieldType.OBJECT, "ZonedDate");
        builder.addPersistedLinkProperty("dateReturn", RealmFieldType.OBJECT, "ZonedDate");
        builder.addPersistedProperty("dateRefund", RealmFieldType.DATE, !Property.PRIMARY_KEY, !Property.INDEXED, !Property.REQUIRED);
        builder.addPersistedProperty("pax", RealmFieldType.INTEGER, !Property.PRIMARY_KEY, !Property.INDEXED, Property.REQUIRED);
        builder.addPersistedProperty("nameSign", RealmFieldType.STRING, !Property.PRIMARY_KEY, !Property.INDEXED, !Property.REQUIRED);
        builder.addPersistedValueListProperty("transportTypes", RealmFieldType.STRING_LIST, Property.REQUIRED);
        builder.addPersistedProperty("childSeats", RealmFieldType.STRING, !Property.PRIMARY_KEY, !Property.INDEXED, !Property.REQUIRED);
        builder.addPersistedProperty("comment", RealmFieldType.STRING, !Property.PRIMARY_KEY, !Property.INDEXED, !Property.REQUIRED);
        builder.addPersistedProperty("flightNumber", RealmFieldType.STRING, !Property.PRIMARY_KEY, !Property.INDEXED, !Property.REQUIRED);
        builder.addPersistedProperty("offersCount", RealmFieldType.INTEGER, !Property.PRIMARY_KEY, !Property.INDEXED, Property.REQUIRED);
        builder.addPersistedProperty("offersChangedDate", RealmFieldType.DATE, !Property.PRIMARY_KEY, !Property.INDEXED, !Property.REQUIRED);
        builder.addPersistedProperty("offersTriedToUpdateDate", RealmFieldType.INTEGER, !Property.PRIMARY_KEY, !Property.INDEXED, Property.REQUIRED);
        builder.addPersistedProperty("offersUpdatedDate", RealmFieldType.INTEGER, !Property.PRIMARY_KEY, !Property.INDEXED, Property.REQUIRED);
        builder.addPersistedProperty("relevantCarrierProfilesCount", RealmFieldType.INTEGER, !Property.PRIMARY_KEY, !Property.INDEXED, !Property.REQUIRED);
        builder.addPersistedProperty("malinaCard", RealmFieldType.STRING, !Property.PRIMARY_KEY, !Property.INDEXED, !Property.REQUIRED);
        builder.addPersistedLinkProperty("offers", RealmFieldType.LIST, "Offer");
        return builder.build();
    }

    public static OsObjectSchemaInfo getExpectedObjectSchemaInfo() {
        return expectedObjectSchemaInfo;
    }

    public static TransferColumnInfo createColumnInfo(OsSchemaInfo schemaInfo) {
        return new TransferColumnInfo(schemaInfo);
    }

    public static String getSimpleClassName() {
        return "Transfer";
    }

    public static final class ClassNameHelper {
        public static final String INTERNAL_CLASS_NAME = "Transfer";
    }

    @SuppressWarnings("cast")
    public static com.kg.gettransfer.data.model.Transfer createOrUpdateUsingJsonObject(Realm realm, JSONObject json, boolean update)
        throws JSONException {
        final List<String> excludeFields = new ArrayList<String>(6);
        com.kg.gettransfer.data.model.Transfer obj = null;
        if (update) {
            Table table = realm.getTable(com.kg.gettransfer.data.model.Transfer.class);
            TransferColumnInfo columnInfo = (TransferColumnInfo) realm.getSchema().getColumnInfo(com.kg.gettransfer.data.model.Transfer.class);
            long pkColumnIndex = columnInfo.idIndex;
            long rowIndex = Table.NO_MATCH;
            if (!json.isNull("id")) {
                rowIndex = table.findFirstLong(pkColumnIndex, json.getLong("id"));
            }
            if (rowIndex != Table.NO_MATCH) {
                final BaseRealm.RealmObjectContext objectContext = BaseRealm.objectContext.get();
                try {
                    objectContext.set(realm, table.getUncheckedRow(rowIndex), realm.getSchema().getColumnInfo(com.kg.gettransfer.data.model.Transfer.class), false, Collections.<String> emptyList());
                    obj = new io.realm.com_kg_gettransfer_data_model_TransferRealmProxy();
                } finally {
                    objectContext.clear();
                }
            }
        }
        if (obj == null) {
            if (json.has("from")) {
                excludeFields.add("from");
            }
            if (json.has("to")) {
                excludeFields.add("to");
            }
            if (json.has("dateTo")) {
                excludeFields.add("dateTo");
            }
            if (json.has("dateReturn")) {
                excludeFields.add("dateReturn");
            }
            if (json.has("transportTypes")) {
                excludeFields.add("transportTypes");
            }
            if (json.has("offers")) {
                excludeFields.add("offers");
            }
            if (json.has("id")) {
                if (json.isNull("id")) {
                    obj = (io.realm.com_kg_gettransfer_data_model_TransferRealmProxy) realm.createObjectInternal(com.kg.gettransfer.data.model.Transfer.class, null, true, excludeFields);
                } else {
                    obj = (io.realm.com_kg_gettransfer_data_model_TransferRealmProxy) realm.createObjectInternal(com.kg.gettransfer.data.model.Transfer.class, json.getInt("id"), true, excludeFields);
                }
            } else {
                throw new IllegalArgumentException("JSON object doesn't have the primary key field 'id'.");
            }
        }

        final com_kg_gettransfer_data_model_TransferRealmProxyInterface objProxy = (com_kg_gettransfer_data_model_TransferRealmProxyInterface) obj;
        if (json.has("updated")) {
            if (json.isNull("updated")) {
                objProxy.realmSet$updated(null);
            } else {
                Object timestamp = json.get("updated");
                if (timestamp instanceof String) {
                    objProxy.realmSet$updated(JsonUtils.stringToDate((String) timestamp));
                } else {
                    objProxy.realmSet$updated(new Date(json.getLong("updated")));
                }
            }
        }
        if (json.has("isActive")) {
            if (json.isNull("isActive")) {
                throw new IllegalArgumentException("Trying to set non-nullable field 'isActive' to null.");
            } else {
                objProxy.realmSet$isActive((boolean) json.getBoolean("isActive"));
            }
        }
        if (json.has("from")) {
            if (json.isNull("from")) {
                objProxy.realmSet$from(null);
            } else {
                com.kg.gettransfer.data.model.secondary.Location fromObj = com_kg_gettransfer_data_model_secondary_LocationRealmProxy.createOrUpdateUsingJsonObject(realm, json.getJSONObject("from"), update);
                objProxy.realmSet$from(fromObj);
            }
        }
        if (json.has("to")) {
            if (json.isNull("to")) {
                objProxy.realmSet$to(null);
            } else {
                com.kg.gettransfer.data.model.secondary.Location toObj = com_kg_gettransfer_data_model_secondary_LocationRealmProxy.createOrUpdateUsingJsonObject(realm, json.getJSONObject("to"), update);
                objProxy.realmSet$to(toObj);
            }
        }
        if (json.has("hireDuration")) {
            if (json.isNull("hireDuration")) {
                objProxy.realmSet$hireDuration(null);
            } else {
                objProxy.realmSet$hireDuration((int) json.getInt("hireDuration"));
            }
        }
        if (json.has("routeDistance")) {
            if (json.isNull("routeDistance")) {
                objProxy.realmSet$routeDistance(null);
            } else {
                objProxy.realmSet$routeDistance((int) json.getInt("routeDistance"));
            }
        }
        if (json.has("routeDuration")) {
            if (json.isNull("routeDuration")) {
                objProxy.realmSet$routeDuration(null);
            } else {
                objProxy.realmSet$routeDuration((int) json.getInt("routeDuration"));
            }
        }
        if (json.has("status")) {
            if (json.isNull("status")) {
                objProxy.realmSet$status(null);
            } else {
                objProxy.realmSet$status((String) json.getString("status"));
            }
        }
        if (json.has("bookNow")) {
            if (json.isNull("bookNow")) {
                throw new IllegalArgumentException("Trying to set non-nullable field 'bookNow' to null.");
            } else {
                objProxy.realmSet$bookNow((boolean) json.getBoolean("bookNow"));
            }
        }
        if (json.has("dateTo")) {
            if (json.isNull("dateTo")) {
                objProxy.realmSet$dateTo(null);
            } else {
                com.kg.gettransfer.data.model.secondary.ZonedDate dateToObj = com_kg_gettransfer_data_model_secondary_ZonedDateRealmProxy.createOrUpdateUsingJsonObject(realm, json.getJSONObject("dateTo"), update);
                objProxy.realmSet$dateTo(dateToObj);
            }
        }
        if (json.has("dateReturn")) {
            if (json.isNull("dateReturn")) {
                objProxy.realmSet$dateReturn(null);
            } else {
                com.kg.gettransfer.data.model.secondary.ZonedDate dateReturnObj = com_kg_gettransfer_data_model_secondary_ZonedDateRealmProxy.createOrUpdateUsingJsonObject(realm, json.getJSONObject("dateReturn"), update);
                objProxy.realmSet$dateReturn(dateReturnObj);
            }
        }
        if (json.has("dateRefund")) {
            if (json.isNull("dateRefund")) {
                objProxy.realmSet$dateRefund(null);
            } else {
                Object timestamp = json.get("dateRefund");
                if (timestamp instanceof String) {
                    objProxy.realmSet$dateRefund(JsonUtils.stringToDate((String) timestamp));
                } else {
                    objProxy.realmSet$dateRefund(new Date(json.getLong("dateRefund")));
                }
            }
        }
        if (json.has("pax")) {
            if (json.isNull("pax")) {
                throw new IllegalArgumentException("Trying to set non-nullable field 'pax' to null.");
            } else {
                objProxy.realmSet$pax((int) json.getInt("pax"));
            }
        }
        if (json.has("nameSign")) {
            if (json.isNull("nameSign")) {
                objProxy.realmSet$nameSign(null);
            } else {
                objProxy.realmSet$nameSign((String) json.getString("nameSign"));
            }
        }
        ProxyUtils.setRealmListWithJsonObject(objProxy.realmGet$transportTypes(), json, "transportTypes");
        if (json.has("childSeats")) {
            if (json.isNull("childSeats")) {
                objProxy.realmSet$childSeats(null);
            } else {
                objProxy.realmSet$childSeats((String) json.getString("childSeats"));
            }
        }
        if (json.has("comment")) {
            if (json.isNull("comment")) {
                objProxy.realmSet$comment(null);
            } else {
                objProxy.realmSet$comment((String) json.getString("comment"));
            }
        }
        if (json.has("flightNumber")) {
            if (json.isNull("flightNumber")) {
                objProxy.realmSet$flightNumber(null);
            } else {
                objProxy.realmSet$flightNumber((String) json.getString("flightNumber"));
            }
        }
        if (json.has("offersCount")) {
            if (json.isNull("offersCount")) {
                throw new IllegalArgumentException("Trying to set non-nullable field 'offersCount' to null.");
            } else {
                objProxy.realmSet$offersCount((int) json.getInt("offersCount"));
            }
        }
        if (json.has("offersChangedDate")) {
            if (json.isNull("offersChangedDate")) {
                objProxy.realmSet$offersChangedDate(null);
            } else {
                Object timestamp = json.get("offersChangedDate");
                if (timestamp instanceof String) {
                    objProxy.realmSet$offersChangedDate(JsonUtils.stringToDate((String) timestamp));
                } else {
                    objProxy.realmSet$offersChangedDate(new Date(json.getLong("offersChangedDate")));
                }
            }
        }
        if (json.has("offersTriedToUpdateDate")) {
            if (json.isNull("offersTriedToUpdateDate")) {
                throw new IllegalArgumentException("Trying to set non-nullable field 'offersTriedToUpdateDate' to null.");
            } else {
                objProxy.realmSet$offersTriedToUpdateDate((long) json.getLong("offersTriedToUpdateDate"));
            }
        }
        if (json.has("offersUpdatedDate")) {
            if (json.isNull("offersUpdatedDate")) {
                throw new IllegalArgumentException("Trying to set non-nullable field 'offersUpdatedDate' to null.");
            } else {
                objProxy.realmSet$offersUpdatedDate((long) json.getLong("offersUpdatedDate"));
            }
        }
        if (json.has("relevantCarrierProfilesCount")) {
            if (json.isNull("relevantCarrierProfilesCount")) {
                objProxy.realmSet$relevantCarrierProfilesCount(null);
            } else {
                objProxy.realmSet$relevantCarrierProfilesCount((int) json.getInt("relevantCarrierProfilesCount"));
            }
        }
        if (json.has("malinaCard")) {
            if (json.isNull("malinaCard")) {
                objProxy.realmSet$malinaCard(null);
            } else {
                objProxy.realmSet$malinaCard((String) json.getString("malinaCard"));
            }
        }
        if (json.has("offers")) {
            if (json.isNull("offers")) {
                objProxy.realmSet$offers(null);
            } else {
                objProxy.realmGet$offers().clear();
                JSONArray array = json.getJSONArray("offers");
                for (int i = 0; i < array.length(); i++) {
                    com.kg.gettransfer.data.model.Offer item = com_kg_gettransfer_data_model_OfferRealmProxy.createOrUpdateUsingJsonObject(realm, array.getJSONObject(i), update);
                    objProxy.realmGet$offers().add(item);
                }
            }
        }
        return obj;
    }

    @SuppressWarnings("cast")
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static com.kg.gettransfer.data.model.Transfer createUsingJsonStream(Realm realm, JsonReader reader)
        throws IOException {
        boolean jsonHasPrimaryKey = false;
        final com.kg.gettransfer.data.model.Transfer obj = new com.kg.gettransfer.data.model.Transfer();
        final com_kg_gettransfer_data_model_TransferRealmProxyInterface objProxy = (com_kg_gettransfer_data_model_TransferRealmProxyInterface) obj;
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (false) {
            } else if (name.equals("id")) {
                if (reader.peek() != JsonToken.NULL) {
                    objProxy.realmSet$id((int) reader.nextInt());
                } else {
                    reader.skipValue();
                    throw new IllegalArgumentException("Trying to set non-nullable field 'id' to null.");
                }
                jsonHasPrimaryKey = true;
            } else if (name.equals("updated")) {
                if (reader.peek() == JsonToken.NULL) {
                    reader.skipValue();
                    objProxy.realmSet$updated(null);
                } else if (reader.peek() == JsonToken.NUMBER) {
                    long timestamp = reader.nextLong();
                    if (timestamp > -1) {
                        objProxy.realmSet$updated(new Date(timestamp));
                    }
                } else {
                    objProxy.realmSet$updated(JsonUtils.stringToDate(reader.nextString()));
                }
            } else if (name.equals("isActive")) {
                if (reader.peek() != JsonToken.NULL) {
                    objProxy.realmSet$isActive((boolean) reader.nextBoolean());
                } else {
                    reader.skipValue();
                    throw new IllegalArgumentException("Trying to set non-nullable field 'isActive' to null.");
                }
            } else if (name.equals("from")) {
                if (reader.peek() == JsonToken.NULL) {
                    reader.skipValue();
                    objProxy.realmSet$from(null);
                } else {
                    com.kg.gettransfer.data.model.secondary.Location fromObj = com_kg_gettransfer_data_model_secondary_LocationRealmProxy.createUsingJsonStream(realm, reader);
                    objProxy.realmSet$from(fromObj);
                }
            } else if (name.equals("to")) {
                if (reader.peek() == JsonToken.NULL) {
                    reader.skipValue();
                    objProxy.realmSet$to(null);
                } else {
                    com.kg.gettransfer.data.model.secondary.Location toObj = com_kg_gettransfer_data_model_secondary_LocationRealmProxy.createUsingJsonStream(realm, reader);
                    objProxy.realmSet$to(toObj);
                }
            } else if (name.equals("hireDuration")) {
                if (reader.peek() != JsonToken.NULL) {
                    objProxy.realmSet$hireDuration((int) reader.nextInt());
                } else {
                    reader.skipValue();
                    objProxy.realmSet$hireDuration(null);
                }
            } else if (name.equals("routeDistance")) {
                if (reader.peek() != JsonToken.NULL) {
                    objProxy.realmSet$routeDistance((int) reader.nextInt());
                } else {
                    reader.skipValue();
                    objProxy.realmSet$routeDistance(null);
                }
            } else if (name.equals("routeDuration")) {
                if (reader.peek() != JsonToken.NULL) {
                    objProxy.realmSet$routeDuration((int) reader.nextInt());
                } else {
                    reader.skipValue();
                    objProxy.realmSet$routeDuration(null);
                }
            } else if (name.equals("status")) {
                if (reader.peek() != JsonToken.NULL) {
                    objProxy.realmSet$status((String) reader.nextString());
                } else {
                    reader.skipValue();
                    objProxy.realmSet$status(null);
                }
            } else if (name.equals("bookNow")) {
                if (reader.peek() != JsonToken.NULL) {
                    objProxy.realmSet$bookNow((boolean) reader.nextBoolean());
                } else {
                    reader.skipValue();
                    throw new IllegalArgumentException("Trying to set non-nullable field 'bookNow' to null.");
                }
            } else if (name.equals("dateTo")) {
                if (reader.peek() == JsonToken.NULL) {
                    reader.skipValue();
                    objProxy.realmSet$dateTo(null);
                } else {
                    com.kg.gettransfer.data.model.secondary.ZonedDate dateToObj = com_kg_gettransfer_data_model_secondary_ZonedDateRealmProxy.createUsingJsonStream(realm, reader);
                    objProxy.realmSet$dateTo(dateToObj);
                }
            } else if (name.equals("dateReturn")) {
                if (reader.peek() == JsonToken.NULL) {
                    reader.skipValue();
                    objProxy.realmSet$dateReturn(null);
                } else {
                    com.kg.gettransfer.data.model.secondary.ZonedDate dateReturnObj = com_kg_gettransfer_data_model_secondary_ZonedDateRealmProxy.createUsingJsonStream(realm, reader);
                    objProxy.realmSet$dateReturn(dateReturnObj);
                }
            } else if (name.equals("dateRefund")) {
                if (reader.peek() == JsonToken.NULL) {
                    reader.skipValue();
                    objProxy.realmSet$dateRefund(null);
                } else if (reader.peek() == JsonToken.NUMBER) {
                    long timestamp = reader.nextLong();
                    if (timestamp > -1) {
                        objProxy.realmSet$dateRefund(new Date(timestamp));
                    }
                } else {
                    objProxy.realmSet$dateRefund(JsonUtils.stringToDate(reader.nextString()));
                }
            } else if (name.equals("pax")) {
                if (reader.peek() != JsonToken.NULL) {
                    objProxy.realmSet$pax((int) reader.nextInt());
                } else {
                    reader.skipValue();
                    throw new IllegalArgumentException("Trying to set non-nullable field 'pax' to null.");
                }
            } else if (name.equals("nameSign")) {
                if (reader.peek() != JsonToken.NULL) {
                    objProxy.realmSet$nameSign((String) reader.nextString());
                } else {
                    reader.skipValue();
                    objProxy.realmSet$nameSign(null);
                }
            } else if (name.equals("transportTypes")) {
                objProxy.realmSet$transportTypes(ProxyUtils.createRealmListWithJsonStream(java.lang.String.class, reader));
            } else if (name.equals("childSeats")) {
                if (reader.peek() != JsonToken.NULL) {
                    objProxy.realmSet$childSeats((String) reader.nextString());
                } else {
                    reader.skipValue();
                    objProxy.realmSet$childSeats(null);
                }
            } else if (name.equals("comment")) {
                if (reader.peek() != JsonToken.NULL) {
                    objProxy.realmSet$comment((String) reader.nextString());
                } else {
                    reader.skipValue();
                    objProxy.realmSet$comment(null);
                }
            } else if (name.equals("flightNumber")) {
                if (reader.peek() != JsonToken.NULL) {
                    objProxy.realmSet$flightNumber((String) reader.nextString());
                } else {
                    reader.skipValue();
                    objProxy.realmSet$flightNumber(null);
                }
            } else if (name.equals("offersCount")) {
                if (reader.peek() != JsonToken.NULL) {
                    objProxy.realmSet$offersCount((int) reader.nextInt());
                } else {
                    reader.skipValue();
                    throw new IllegalArgumentException("Trying to set non-nullable field 'offersCount' to null.");
                }
            } else if (name.equals("offersChangedDate")) {
                if (reader.peek() == JsonToken.NULL) {
                    reader.skipValue();
                    objProxy.realmSet$offersChangedDate(null);
                } else if (reader.peek() == JsonToken.NUMBER) {
                    long timestamp = reader.nextLong();
                    if (timestamp > -1) {
                        objProxy.realmSet$offersChangedDate(new Date(timestamp));
                    }
                } else {
                    objProxy.realmSet$offersChangedDate(JsonUtils.stringToDate(reader.nextString()));
                }
            } else if (name.equals("offersTriedToUpdateDate")) {
                if (reader.peek() != JsonToken.NULL) {
                    objProxy.realmSet$offersTriedToUpdateDate((long) reader.nextLong());
                } else {
                    reader.skipValue();
                    throw new IllegalArgumentException("Trying to set non-nullable field 'offersTriedToUpdateDate' to null.");
                }
            } else if (name.equals("offersUpdatedDate")) {
                if (reader.peek() != JsonToken.NULL) {
                    objProxy.realmSet$offersUpdatedDate((long) reader.nextLong());
                } else {
                    reader.skipValue();
                    throw new IllegalArgumentException("Trying to set non-nullable field 'offersUpdatedDate' to null.");
                }
            } else if (name.equals("relevantCarrierProfilesCount")) {
                if (reader.peek() != JsonToken.NULL) {
                    objProxy.realmSet$relevantCarrierProfilesCount((int) reader.nextInt());
                } else {
                    reader.skipValue();
                    objProxy.realmSet$relevantCarrierProfilesCount(null);
                }
            } else if (name.equals("malinaCard")) {
                if (reader.peek() != JsonToken.NULL) {
                    objProxy.realmSet$malinaCard((String) reader.nextString());
                } else {
                    reader.skipValue();
                    objProxy.realmSet$malinaCard(null);
                }
            } else if (name.equals("offers")) {
                if (reader.peek() == JsonToken.NULL) {
                    reader.skipValue();
                    objProxy.realmSet$offers(null);
                } else {
                    objProxy.realmSet$offers(new RealmList<com.kg.gettransfer.data.model.Offer>());
                    reader.beginArray();
                    while (reader.hasNext()) {
                        com.kg.gettransfer.data.model.Offer item = com_kg_gettransfer_data_model_OfferRealmProxy.createUsingJsonStream(realm, reader);
                        objProxy.realmGet$offers().add(item);
                    }
                    reader.endArray();
                }
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        if (!jsonHasPrimaryKey) {
            throw new IllegalArgumentException("JSON object doesn't have the primary key field 'id'.");
        }
        return realm.copyToRealm(obj);
    }

    public static com.kg.gettransfer.data.model.Transfer copyOrUpdate(Realm realm, com.kg.gettransfer.data.model.Transfer object, boolean update, Map<RealmModel,RealmObjectProxy> cache) {
        if (object instanceof RealmObjectProxy && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm() != null) {
            final BaseRealm otherRealm = ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm();
            if (otherRealm.threadId != realm.threadId) {
                throw new IllegalArgumentException("Objects which belong to Realm instances in other threads cannot be copied into this Realm instance.");
            }
            if (otherRealm.getPath().equals(realm.getPath())) {
                return object;
            }
        }
        final BaseRealm.RealmObjectContext objectContext = BaseRealm.objectContext.get();
        RealmObjectProxy cachedRealmObject = cache.get(object);
        if (cachedRealmObject != null) {
            return (com.kg.gettransfer.data.model.Transfer) cachedRealmObject;
        }

        com.kg.gettransfer.data.model.Transfer realmObject = null;
        boolean canUpdate = update;
        if (canUpdate) {
            Table table = realm.getTable(com.kg.gettransfer.data.model.Transfer.class);
            TransferColumnInfo columnInfo = (TransferColumnInfo) realm.getSchema().getColumnInfo(com.kg.gettransfer.data.model.Transfer.class);
            long pkColumnIndex = columnInfo.idIndex;
            long rowIndex = table.findFirstLong(pkColumnIndex, ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$id());
            if (rowIndex == Table.NO_MATCH) {
                canUpdate = false;
            } else {
                try {
                    objectContext.set(realm, table.getUncheckedRow(rowIndex), realm.getSchema().getColumnInfo(com.kg.gettransfer.data.model.Transfer.class), false, Collections.<String> emptyList());
                    realmObject = new io.realm.com_kg_gettransfer_data_model_TransferRealmProxy();
                    cache.put(object, (RealmObjectProxy) realmObject);
                } finally {
                    objectContext.clear();
                }
            }
        }

        return (canUpdate) ? update(realm, realmObject, object, cache) : copy(realm, object, update, cache);
    }

    public static com.kg.gettransfer.data.model.Transfer copy(Realm realm, com.kg.gettransfer.data.model.Transfer newObject, boolean update, Map<RealmModel,RealmObjectProxy> cache) {
        RealmObjectProxy cachedRealmObject = cache.get(newObject);
        if (cachedRealmObject != null) {
            return (com.kg.gettransfer.data.model.Transfer) cachedRealmObject;
        }

        // rejecting default values to avoid creating unexpected objects from RealmModel/RealmList fields.
        com.kg.gettransfer.data.model.Transfer realmObject = realm.createObjectInternal(com.kg.gettransfer.data.model.Transfer.class, ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) newObject).realmGet$id(), false, Collections.<String>emptyList());
        cache.put(newObject, (RealmObjectProxy) realmObject);

        com_kg_gettransfer_data_model_TransferRealmProxyInterface realmObjectSource = (com_kg_gettransfer_data_model_TransferRealmProxyInterface) newObject;
        com_kg_gettransfer_data_model_TransferRealmProxyInterface realmObjectCopy = (com_kg_gettransfer_data_model_TransferRealmProxyInterface) realmObject;

        realmObjectCopy.realmSet$updated(realmObjectSource.realmGet$updated());
        realmObjectCopy.realmSet$isActive(realmObjectSource.realmGet$isActive());

        com.kg.gettransfer.data.model.secondary.Location fromObj = realmObjectSource.realmGet$from();
        if (fromObj == null) {
            realmObjectCopy.realmSet$from(null);
        } else {
            com.kg.gettransfer.data.model.secondary.Location cachefrom = (com.kg.gettransfer.data.model.secondary.Location) cache.get(fromObj);
            if (cachefrom != null) {
                realmObjectCopy.realmSet$from(cachefrom);
            } else {
                realmObjectCopy.realmSet$from(com_kg_gettransfer_data_model_secondary_LocationRealmProxy.copyOrUpdate(realm, fromObj, update, cache));
            }
        }

        com.kg.gettransfer.data.model.secondary.Location toObj = realmObjectSource.realmGet$to();
        if (toObj == null) {
            realmObjectCopy.realmSet$to(null);
        } else {
            com.kg.gettransfer.data.model.secondary.Location cacheto = (com.kg.gettransfer.data.model.secondary.Location) cache.get(toObj);
            if (cacheto != null) {
                realmObjectCopy.realmSet$to(cacheto);
            } else {
                realmObjectCopy.realmSet$to(com_kg_gettransfer_data_model_secondary_LocationRealmProxy.copyOrUpdate(realm, toObj, update, cache));
            }
        }
        realmObjectCopy.realmSet$hireDuration(realmObjectSource.realmGet$hireDuration());
        realmObjectCopy.realmSet$routeDistance(realmObjectSource.realmGet$routeDistance());
        realmObjectCopy.realmSet$routeDuration(realmObjectSource.realmGet$routeDuration());
        realmObjectCopy.realmSet$status(realmObjectSource.realmGet$status());
        realmObjectCopy.realmSet$bookNow(realmObjectSource.realmGet$bookNow());

        com.kg.gettransfer.data.model.secondary.ZonedDate dateToObj = realmObjectSource.realmGet$dateTo();
        if (dateToObj == null) {
            realmObjectCopy.realmSet$dateTo(null);
        } else {
            com.kg.gettransfer.data.model.secondary.ZonedDate cachedateTo = (com.kg.gettransfer.data.model.secondary.ZonedDate) cache.get(dateToObj);
            if (cachedateTo != null) {
                realmObjectCopy.realmSet$dateTo(cachedateTo);
            } else {
                realmObjectCopy.realmSet$dateTo(com_kg_gettransfer_data_model_secondary_ZonedDateRealmProxy.copyOrUpdate(realm, dateToObj, update, cache));
            }
        }

        com.kg.gettransfer.data.model.secondary.ZonedDate dateReturnObj = realmObjectSource.realmGet$dateReturn();
        if (dateReturnObj == null) {
            realmObjectCopy.realmSet$dateReturn(null);
        } else {
            com.kg.gettransfer.data.model.secondary.ZonedDate cachedateReturn = (com.kg.gettransfer.data.model.secondary.ZonedDate) cache.get(dateReturnObj);
            if (cachedateReturn != null) {
                realmObjectCopy.realmSet$dateReturn(cachedateReturn);
            } else {
                realmObjectCopy.realmSet$dateReturn(com_kg_gettransfer_data_model_secondary_ZonedDateRealmProxy.copyOrUpdate(realm, dateReturnObj, update, cache));
            }
        }
        realmObjectCopy.realmSet$dateRefund(realmObjectSource.realmGet$dateRefund());
        realmObjectCopy.realmSet$pax(realmObjectSource.realmGet$pax());
        realmObjectCopy.realmSet$nameSign(realmObjectSource.realmGet$nameSign());
        realmObjectCopy.realmSet$transportTypes(realmObjectSource.realmGet$transportTypes());
        realmObjectCopy.realmSet$childSeats(realmObjectSource.realmGet$childSeats());
        realmObjectCopy.realmSet$comment(realmObjectSource.realmGet$comment());
        realmObjectCopy.realmSet$flightNumber(realmObjectSource.realmGet$flightNumber());
        realmObjectCopy.realmSet$offersCount(realmObjectSource.realmGet$offersCount());
        realmObjectCopy.realmSet$offersChangedDate(realmObjectSource.realmGet$offersChangedDate());
        realmObjectCopy.realmSet$offersTriedToUpdateDate(realmObjectSource.realmGet$offersTriedToUpdateDate());
        realmObjectCopy.realmSet$offersUpdatedDate(realmObjectSource.realmGet$offersUpdatedDate());
        realmObjectCopy.realmSet$relevantCarrierProfilesCount(realmObjectSource.realmGet$relevantCarrierProfilesCount());
        realmObjectCopy.realmSet$malinaCard(realmObjectSource.realmGet$malinaCard());

        RealmList<com.kg.gettransfer.data.model.Offer> offersList = realmObjectSource.realmGet$offers();
        if (offersList != null) {
            RealmList<com.kg.gettransfer.data.model.Offer> offersRealmList = realmObjectCopy.realmGet$offers();
            offersRealmList.clear();
            for (int i = 0; i < offersList.size(); i++) {
                com.kg.gettransfer.data.model.Offer offersItem = offersList.get(i);
                com.kg.gettransfer.data.model.Offer cacheoffers = (com.kg.gettransfer.data.model.Offer) cache.get(offersItem);
                if (cacheoffers != null) {
                    offersRealmList.add(cacheoffers);
                } else {
                    offersRealmList.add(com_kg_gettransfer_data_model_OfferRealmProxy.copyOrUpdate(realm, offersItem, update, cache));
                }
            }
        }

        return realmObject;
    }

    public static long insert(Realm realm, com.kg.gettransfer.data.model.Transfer object, Map<RealmModel,Long> cache) {
        if (object instanceof RealmObjectProxy && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm() != null && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm().getPath().equals(realm.getPath())) {
            return ((RealmObjectProxy) object).realmGet$proxyState().getRow$realm().getIndex();
        }
        Table table = realm.getTable(com.kg.gettransfer.data.model.Transfer.class);
        long tableNativePtr = table.getNativePtr();
        TransferColumnInfo columnInfo = (TransferColumnInfo) realm.getSchema().getColumnInfo(com.kg.gettransfer.data.model.Transfer.class);
        long pkColumnIndex = columnInfo.idIndex;
        long rowIndex = Table.NO_MATCH;
        Object primaryKeyValue = ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$id();
        if (primaryKeyValue != null) {
            rowIndex = Table.nativeFindFirstInt(tableNativePtr, pkColumnIndex, ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$id());
        }
        if (rowIndex == Table.NO_MATCH) {
            rowIndex = OsObject.createRowWithPrimaryKey(table, pkColumnIndex, ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$id());
        } else {
            Table.throwDuplicatePrimaryKeyException(primaryKeyValue);
        }
        cache.put(object, rowIndex);
        java.util.Date realmGet$updated = ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$updated();
        if (realmGet$updated != null) {
            Table.nativeSetTimestamp(tableNativePtr, columnInfo.updatedIndex, rowIndex, realmGet$updated.getTime(), false);
        }
        Table.nativeSetBoolean(tableNativePtr, columnInfo.isActiveIndex, rowIndex, ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$isActive(), false);

        com.kg.gettransfer.data.model.secondary.Location fromObj = ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$from();
        if (fromObj != null) {
            Long cachefrom = cache.get(fromObj);
            if (cachefrom == null) {
                cachefrom = com_kg_gettransfer_data_model_secondary_LocationRealmProxy.insert(realm, fromObj, cache);
            }
            Table.nativeSetLink(tableNativePtr, columnInfo.fromIndex, rowIndex, cachefrom, false);
        }

        com.kg.gettransfer.data.model.secondary.Location toObj = ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$to();
        if (toObj != null) {
            Long cacheto = cache.get(toObj);
            if (cacheto == null) {
                cacheto = com_kg_gettransfer_data_model_secondary_LocationRealmProxy.insert(realm, toObj, cache);
            }
            Table.nativeSetLink(tableNativePtr, columnInfo.toIndex, rowIndex, cacheto, false);
        }
        Number realmGet$hireDuration = ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$hireDuration();
        if (realmGet$hireDuration != null) {
            Table.nativeSetLong(tableNativePtr, columnInfo.hireDurationIndex, rowIndex, realmGet$hireDuration.longValue(), false);
        }
        Number realmGet$routeDistance = ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$routeDistance();
        if (realmGet$routeDistance != null) {
            Table.nativeSetLong(tableNativePtr, columnInfo.routeDistanceIndex, rowIndex, realmGet$routeDistance.longValue(), false);
        }
        Number realmGet$routeDuration = ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$routeDuration();
        if (realmGet$routeDuration != null) {
            Table.nativeSetLong(tableNativePtr, columnInfo.routeDurationIndex, rowIndex, realmGet$routeDuration.longValue(), false);
        }
        String realmGet$status = ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$status();
        if (realmGet$status != null) {
            Table.nativeSetString(tableNativePtr, columnInfo.statusIndex, rowIndex, realmGet$status, false);
        }
        Table.nativeSetBoolean(tableNativePtr, columnInfo.bookNowIndex, rowIndex, ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$bookNow(), false);

        com.kg.gettransfer.data.model.secondary.ZonedDate dateToObj = ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$dateTo();
        if (dateToObj != null) {
            Long cachedateTo = cache.get(dateToObj);
            if (cachedateTo == null) {
                cachedateTo = com_kg_gettransfer_data_model_secondary_ZonedDateRealmProxy.insert(realm, dateToObj, cache);
            }
            Table.nativeSetLink(tableNativePtr, columnInfo.dateToIndex, rowIndex, cachedateTo, false);
        }

        com.kg.gettransfer.data.model.secondary.ZonedDate dateReturnObj = ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$dateReturn();
        if (dateReturnObj != null) {
            Long cachedateReturn = cache.get(dateReturnObj);
            if (cachedateReturn == null) {
                cachedateReturn = com_kg_gettransfer_data_model_secondary_ZonedDateRealmProxy.insert(realm, dateReturnObj, cache);
            }
            Table.nativeSetLink(tableNativePtr, columnInfo.dateReturnIndex, rowIndex, cachedateReturn, false);
        }
        java.util.Date realmGet$dateRefund = ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$dateRefund();
        if (realmGet$dateRefund != null) {
            Table.nativeSetTimestamp(tableNativePtr, columnInfo.dateRefundIndex, rowIndex, realmGet$dateRefund.getTime(), false);
        }
        Table.nativeSetLong(tableNativePtr, columnInfo.paxIndex, rowIndex, ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$pax(), false);
        String realmGet$nameSign = ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$nameSign();
        if (realmGet$nameSign != null) {
            Table.nativeSetString(tableNativePtr, columnInfo.nameSignIndex, rowIndex, realmGet$nameSign, false);
        }

        RealmList<java.lang.String> transportTypesList = ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$transportTypes();
        if (transportTypesList != null) {
            OsList transportTypesOsList = new OsList(table.getUncheckedRow(rowIndex), columnInfo.transportTypesIndex);
            for (java.lang.String transportTypesItem : transportTypesList) {
                if (transportTypesItem == null) {
                    transportTypesOsList.addNull();
                } else {
                    transportTypesOsList.addString(transportTypesItem);
                }
            }
        }
        String realmGet$childSeats = ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$childSeats();
        if (realmGet$childSeats != null) {
            Table.nativeSetString(tableNativePtr, columnInfo.childSeatsIndex, rowIndex, realmGet$childSeats, false);
        }
        String realmGet$comment = ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$comment();
        if (realmGet$comment != null) {
            Table.nativeSetString(tableNativePtr, columnInfo.commentIndex, rowIndex, realmGet$comment, false);
        }
        String realmGet$flightNumber = ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$flightNumber();
        if (realmGet$flightNumber != null) {
            Table.nativeSetString(tableNativePtr, columnInfo.flightNumberIndex, rowIndex, realmGet$flightNumber, false);
        }
        Table.nativeSetLong(tableNativePtr, columnInfo.offersCountIndex, rowIndex, ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$offersCount(), false);
        java.util.Date realmGet$offersChangedDate = ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$offersChangedDate();
        if (realmGet$offersChangedDate != null) {
            Table.nativeSetTimestamp(tableNativePtr, columnInfo.offersChangedDateIndex, rowIndex, realmGet$offersChangedDate.getTime(), false);
        }
        Table.nativeSetLong(tableNativePtr, columnInfo.offersTriedToUpdateDateIndex, rowIndex, ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$offersTriedToUpdateDate(), false);
        Table.nativeSetLong(tableNativePtr, columnInfo.offersUpdatedDateIndex, rowIndex, ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$offersUpdatedDate(), false);
        Number realmGet$relevantCarrierProfilesCount = ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$relevantCarrierProfilesCount();
        if (realmGet$relevantCarrierProfilesCount != null) {
            Table.nativeSetLong(tableNativePtr, columnInfo.relevantCarrierProfilesCountIndex, rowIndex, realmGet$relevantCarrierProfilesCount.longValue(), false);
        }
        String realmGet$malinaCard = ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$malinaCard();
        if (realmGet$malinaCard != null) {
            Table.nativeSetString(tableNativePtr, columnInfo.malinaCardIndex, rowIndex, realmGet$malinaCard, false);
        }

        RealmList<com.kg.gettransfer.data.model.Offer> offersList = ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$offers();
        if (offersList != null) {
            OsList offersOsList = new OsList(table.getUncheckedRow(rowIndex), columnInfo.offersIndex);
            for (com.kg.gettransfer.data.model.Offer offersItem : offersList) {
                Long cacheItemIndexoffers = cache.get(offersItem);
                if (cacheItemIndexoffers == null) {
                    cacheItemIndexoffers = com_kg_gettransfer_data_model_OfferRealmProxy.insert(realm, offersItem, cache);
                }
                offersOsList.addRow(cacheItemIndexoffers);
            }
        }
        return rowIndex;
    }

    public static void insert(Realm realm, Iterator<? extends RealmModel> objects, Map<RealmModel,Long> cache) {
        Table table = realm.getTable(com.kg.gettransfer.data.model.Transfer.class);
        long tableNativePtr = table.getNativePtr();
        TransferColumnInfo columnInfo = (TransferColumnInfo) realm.getSchema().getColumnInfo(com.kg.gettransfer.data.model.Transfer.class);
        long pkColumnIndex = columnInfo.idIndex;
        com.kg.gettransfer.data.model.Transfer object = null;
        while (objects.hasNext()) {
            object = (com.kg.gettransfer.data.model.Transfer) objects.next();
            if (cache.containsKey(object)) {
                continue;
            }
            if (object instanceof RealmObjectProxy && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm() != null && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm().getPath().equals(realm.getPath())) {
                cache.put(object, ((RealmObjectProxy) object).realmGet$proxyState().getRow$realm().getIndex());
                continue;
            }
            long rowIndex = Table.NO_MATCH;
            Object primaryKeyValue = ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$id();
            if (primaryKeyValue != null) {
                rowIndex = Table.nativeFindFirstInt(tableNativePtr, pkColumnIndex, ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$id());
            }
            if (rowIndex == Table.NO_MATCH) {
                rowIndex = OsObject.createRowWithPrimaryKey(table, pkColumnIndex, ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$id());
            } else {
                Table.throwDuplicatePrimaryKeyException(primaryKeyValue);
            }
            cache.put(object, rowIndex);
            java.util.Date realmGet$updated = ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$updated();
            if (realmGet$updated != null) {
                Table.nativeSetTimestamp(tableNativePtr, columnInfo.updatedIndex, rowIndex, realmGet$updated.getTime(), false);
            }
            Table.nativeSetBoolean(tableNativePtr, columnInfo.isActiveIndex, rowIndex, ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$isActive(), false);

            com.kg.gettransfer.data.model.secondary.Location fromObj = ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$from();
            if (fromObj != null) {
                Long cachefrom = cache.get(fromObj);
                if (cachefrom == null) {
                    cachefrom = com_kg_gettransfer_data_model_secondary_LocationRealmProxy.insert(realm, fromObj, cache);
                }
                table.setLink(columnInfo.fromIndex, rowIndex, cachefrom, false);
            }

            com.kg.gettransfer.data.model.secondary.Location toObj = ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$to();
            if (toObj != null) {
                Long cacheto = cache.get(toObj);
                if (cacheto == null) {
                    cacheto = com_kg_gettransfer_data_model_secondary_LocationRealmProxy.insert(realm, toObj, cache);
                }
                table.setLink(columnInfo.toIndex, rowIndex, cacheto, false);
            }
            Number realmGet$hireDuration = ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$hireDuration();
            if (realmGet$hireDuration != null) {
                Table.nativeSetLong(tableNativePtr, columnInfo.hireDurationIndex, rowIndex, realmGet$hireDuration.longValue(), false);
            }
            Number realmGet$routeDistance = ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$routeDistance();
            if (realmGet$routeDistance != null) {
                Table.nativeSetLong(tableNativePtr, columnInfo.routeDistanceIndex, rowIndex, realmGet$routeDistance.longValue(), false);
            }
            Number realmGet$routeDuration = ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$routeDuration();
            if (realmGet$routeDuration != null) {
                Table.nativeSetLong(tableNativePtr, columnInfo.routeDurationIndex, rowIndex, realmGet$routeDuration.longValue(), false);
            }
            String realmGet$status = ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$status();
            if (realmGet$status != null) {
                Table.nativeSetString(tableNativePtr, columnInfo.statusIndex, rowIndex, realmGet$status, false);
            }
            Table.nativeSetBoolean(tableNativePtr, columnInfo.bookNowIndex, rowIndex, ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$bookNow(), false);

            com.kg.gettransfer.data.model.secondary.ZonedDate dateToObj = ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$dateTo();
            if (dateToObj != null) {
                Long cachedateTo = cache.get(dateToObj);
                if (cachedateTo == null) {
                    cachedateTo = com_kg_gettransfer_data_model_secondary_ZonedDateRealmProxy.insert(realm, dateToObj, cache);
                }
                table.setLink(columnInfo.dateToIndex, rowIndex, cachedateTo, false);
            }

            com.kg.gettransfer.data.model.secondary.ZonedDate dateReturnObj = ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$dateReturn();
            if (dateReturnObj != null) {
                Long cachedateReturn = cache.get(dateReturnObj);
                if (cachedateReturn == null) {
                    cachedateReturn = com_kg_gettransfer_data_model_secondary_ZonedDateRealmProxy.insert(realm, dateReturnObj, cache);
                }
                table.setLink(columnInfo.dateReturnIndex, rowIndex, cachedateReturn, false);
            }
            java.util.Date realmGet$dateRefund = ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$dateRefund();
            if (realmGet$dateRefund != null) {
                Table.nativeSetTimestamp(tableNativePtr, columnInfo.dateRefundIndex, rowIndex, realmGet$dateRefund.getTime(), false);
            }
            Table.nativeSetLong(tableNativePtr, columnInfo.paxIndex, rowIndex, ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$pax(), false);
            String realmGet$nameSign = ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$nameSign();
            if (realmGet$nameSign != null) {
                Table.nativeSetString(tableNativePtr, columnInfo.nameSignIndex, rowIndex, realmGet$nameSign, false);
            }

            RealmList<java.lang.String> transportTypesList = ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$transportTypes();
            if (transportTypesList != null) {
                OsList transportTypesOsList = new OsList(table.getUncheckedRow(rowIndex), columnInfo.transportTypesIndex);
                for (java.lang.String transportTypesItem : transportTypesList) {
                    if (transportTypesItem == null) {
                        transportTypesOsList.addNull();
                    } else {
                        transportTypesOsList.addString(transportTypesItem);
                    }
                }
            }
            String realmGet$childSeats = ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$childSeats();
            if (realmGet$childSeats != null) {
                Table.nativeSetString(tableNativePtr, columnInfo.childSeatsIndex, rowIndex, realmGet$childSeats, false);
            }
            String realmGet$comment = ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$comment();
            if (realmGet$comment != null) {
                Table.nativeSetString(tableNativePtr, columnInfo.commentIndex, rowIndex, realmGet$comment, false);
            }
            String realmGet$flightNumber = ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$flightNumber();
            if (realmGet$flightNumber != null) {
                Table.nativeSetString(tableNativePtr, columnInfo.flightNumberIndex, rowIndex, realmGet$flightNumber, false);
            }
            Table.nativeSetLong(tableNativePtr, columnInfo.offersCountIndex, rowIndex, ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$offersCount(), false);
            java.util.Date realmGet$offersChangedDate = ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$offersChangedDate();
            if (realmGet$offersChangedDate != null) {
                Table.nativeSetTimestamp(tableNativePtr, columnInfo.offersChangedDateIndex, rowIndex, realmGet$offersChangedDate.getTime(), false);
            }
            Table.nativeSetLong(tableNativePtr, columnInfo.offersTriedToUpdateDateIndex, rowIndex, ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$offersTriedToUpdateDate(), false);
            Table.nativeSetLong(tableNativePtr, columnInfo.offersUpdatedDateIndex, rowIndex, ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$offersUpdatedDate(), false);
            Number realmGet$relevantCarrierProfilesCount = ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$relevantCarrierProfilesCount();
            if (realmGet$relevantCarrierProfilesCount != null) {
                Table.nativeSetLong(tableNativePtr, columnInfo.relevantCarrierProfilesCountIndex, rowIndex, realmGet$relevantCarrierProfilesCount.longValue(), false);
            }
            String realmGet$malinaCard = ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$malinaCard();
            if (realmGet$malinaCard != null) {
                Table.nativeSetString(tableNativePtr, columnInfo.malinaCardIndex, rowIndex, realmGet$malinaCard, false);
            }

            RealmList<com.kg.gettransfer.data.model.Offer> offersList = ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$offers();
            if (offersList != null) {
                OsList offersOsList = new OsList(table.getUncheckedRow(rowIndex), columnInfo.offersIndex);
                for (com.kg.gettransfer.data.model.Offer offersItem : offersList) {
                    Long cacheItemIndexoffers = cache.get(offersItem);
                    if (cacheItemIndexoffers == null) {
                        cacheItemIndexoffers = com_kg_gettransfer_data_model_OfferRealmProxy.insert(realm, offersItem, cache);
                    }
                    offersOsList.addRow(cacheItemIndexoffers);
                }
            }
        }
    }

    public static long insertOrUpdate(Realm realm, com.kg.gettransfer.data.model.Transfer object, Map<RealmModel,Long> cache) {
        if (object instanceof RealmObjectProxy && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm() != null && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm().getPath().equals(realm.getPath())) {
            return ((RealmObjectProxy) object).realmGet$proxyState().getRow$realm().getIndex();
        }
        Table table = realm.getTable(com.kg.gettransfer.data.model.Transfer.class);
        long tableNativePtr = table.getNativePtr();
        TransferColumnInfo columnInfo = (TransferColumnInfo) realm.getSchema().getColumnInfo(com.kg.gettransfer.data.model.Transfer.class);
        long pkColumnIndex = columnInfo.idIndex;
        long rowIndex = Table.NO_MATCH;
        Object primaryKeyValue = ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$id();
        if (primaryKeyValue != null) {
            rowIndex = Table.nativeFindFirstInt(tableNativePtr, pkColumnIndex, ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$id());
        }
        if (rowIndex == Table.NO_MATCH) {
            rowIndex = OsObject.createRowWithPrimaryKey(table, pkColumnIndex, ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$id());
        }
        cache.put(object, rowIndex);
        java.util.Date realmGet$updated = ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$updated();
        if (realmGet$updated != null) {
            Table.nativeSetTimestamp(tableNativePtr, columnInfo.updatedIndex, rowIndex, realmGet$updated.getTime(), false);
        } else {
            Table.nativeSetNull(tableNativePtr, columnInfo.updatedIndex, rowIndex, false);
        }
        Table.nativeSetBoolean(tableNativePtr, columnInfo.isActiveIndex, rowIndex, ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$isActive(), false);

        com.kg.gettransfer.data.model.secondary.Location fromObj = ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$from();
        if (fromObj != null) {
            Long cachefrom = cache.get(fromObj);
            if (cachefrom == null) {
                cachefrom = com_kg_gettransfer_data_model_secondary_LocationRealmProxy.insertOrUpdate(realm, fromObj, cache);
            }
            Table.nativeSetLink(tableNativePtr, columnInfo.fromIndex, rowIndex, cachefrom, false);
        } else {
            Table.nativeNullifyLink(tableNativePtr, columnInfo.fromIndex, rowIndex);
        }

        com.kg.gettransfer.data.model.secondary.Location toObj = ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$to();
        if (toObj != null) {
            Long cacheto = cache.get(toObj);
            if (cacheto == null) {
                cacheto = com_kg_gettransfer_data_model_secondary_LocationRealmProxy.insertOrUpdate(realm, toObj, cache);
            }
            Table.nativeSetLink(tableNativePtr, columnInfo.toIndex, rowIndex, cacheto, false);
        } else {
            Table.nativeNullifyLink(tableNativePtr, columnInfo.toIndex, rowIndex);
        }
        Number realmGet$hireDuration = ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$hireDuration();
        if (realmGet$hireDuration != null) {
            Table.nativeSetLong(tableNativePtr, columnInfo.hireDurationIndex, rowIndex, realmGet$hireDuration.longValue(), false);
        } else {
            Table.nativeSetNull(tableNativePtr, columnInfo.hireDurationIndex, rowIndex, false);
        }
        Number realmGet$routeDistance = ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$routeDistance();
        if (realmGet$routeDistance != null) {
            Table.nativeSetLong(tableNativePtr, columnInfo.routeDistanceIndex, rowIndex, realmGet$routeDistance.longValue(), false);
        } else {
            Table.nativeSetNull(tableNativePtr, columnInfo.routeDistanceIndex, rowIndex, false);
        }
        Number realmGet$routeDuration = ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$routeDuration();
        if (realmGet$routeDuration != null) {
            Table.nativeSetLong(tableNativePtr, columnInfo.routeDurationIndex, rowIndex, realmGet$routeDuration.longValue(), false);
        } else {
            Table.nativeSetNull(tableNativePtr, columnInfo.routeDurationIndex, rowIndex, false);
        }
        String realmGet$status = ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$status();
        if (realmGet$status != null) {
            Table.nativeSetString(tableNativePtr, columnInfo.statusIndex, rowIndex, realmGet$status, false);
        } else {
            Table.nativeSetNull(tableNativePtr, columnInfo.statusIndex, rowIndex, false);
        }
        Table.nativeSetBoolean(tableNativePtr, columnInfo.bookNowIndex, rowIndex, ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$bookNow(), false);

        com.kg.gettransfer.data.model.secondary.ZonedDate dateToObj = ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$dateTo();
        if (dateToObj != null) {
            Long cachedateTo = cache.get(dateToObj);
            if (cachedateTo == null) {
                cachedateTo = com_kg_gettransfer_data_model_secondary_ZonedDateRealmProxy.insertOrUpdate(realm, dateToObj, cache);
            }
            Table.nativeSetLink(tableNativePtr, columnInfo.dateToIndex, rowIndex, cachedateTo, false);
        } else {
            Table.nativeNullifyLink(tableNativePtr, columnInfo.dateToIndex, rowIndex);
        }

        com.kg.gettransfer.data.model.secondary.ZonedDate dateReturnObj = ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$dateReturn();
        if (dateReturnObj != null) {
            Long cachedateReturn = cache.get(dateReturnObj);
            if (cachedateReturn == null) {
                cachedateReturn = com_kg_gettransfer_data_model_secondary_ZonedDateRealmProxy.insertOrUpdate(realm, dateReturnObj, cache);
            }
            Table.nativeSetLink(tableNativePtr, columnInfo.dateReturnIndex, rowIndex, cachedateReturn, false);
        } else {
            Table.nativeNullifyLink(tableNativePtr, columnInfo.dateReturnIndex, rowIndex);
        }
        java.util.Date realmGet$dateRefund = ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$dateRefund();
        if (realmGet$dateRefund != null) {
            Table.nativeSetTimestamp(tableNativePtr, columnInfo.dateRefundIndex, rowIndex, realmGet$dateRefund.getTime(), false);
        } else {
            Table.nativeSetNull(tableNativePtr, columnInfo.dateRefundIndex, rowIndex, false);
        }
        Table.nativeSetLong(tableNativePtr, columnInfo.paxIndex, rowIndex, ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$pax(), false);
        String realmGet$nameSign = ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$nameSign();
        if (realmGet$nameSign != null) {
            Table.nativeSetString(tableNativePtr, columnInfo.nameSignIndex, rowIndex, realmGet$nameSign, false);
        } else {
            Table.nativeSetNull(tableNativePtr, columnInfo.nameSignIndex, rowIndex, false);
        }

        OsList transportTypesOsList = new OsList(table.getUncheckedRow(rowIndex), columnInfo.transportTypesIndex);
        transportTypesOsList.removeAll();
        RealmList<java.lang.String> transportTypesList = ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$transportTypes();
        if (transportTypesList != null) {
            for (java.lang.String transportTypesItem : transportTypesList) {
                if (transportTypesItem == null) {
                    transportTypesOsList.addNull();
                } else {
                    transportTypesOsList.addString(transportTypesItem);
                }
            }
        }

        String realmGet$childSeats = ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$childSeats();
        if (realmGet$childSeats != null) {
            Table.nativeSetString(tableNativePtr, columnInfo.childSeatsIndex, rowIndex, realmGet$childSeats, false);
        } else {
            Table.nativeSetNull(tableNativePtr, columnInfo.childSeatsIndex, rowIndex, false);
        }
        String realmGet$comment = ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$comment();
        if (realmGet$comment != null) {
            Table.nativeSetString(tableNativePtr, columnInfo.commentIndex, rowIndex, realmGet$comment, false);
        } else {
            Table.nativeSetNull(tableNativePtr, columnInfo.commentIndex, rowIndex, false);
        }
        String realmGet$flightNumber = ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$flightNumber();
        if (realmGet$flightNumber != null) {
            Table.nativeSetString(tableNativePtr, columnInfo.flightNumberIndex, rowIndex, realmGet$flightNumber, false);
        } else {
            Table.nativeSetNull(tableNativePtr, columnInfo.flightNumberIndex, rowIndex, false);
        }
        Table.nativeSetLong(tableNativePtr, columnInfo.offersCountIndex, rowIndex, ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$offersCount(), false);
        java.util.Date realmGet$offersChangedDate = ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$offersChangedDate();
        if (realmGet$offersChangedDate != null) {
            Table.nativeSetTimestamp(tableNativePtr, columnInfo.offersChangedDateIndex, rowIndex, realmGet$offersChangedDate.getTime(), false);
        } else {
            Table.nativeSetNull(tableNativePtr, columnInfo.offersChangedDateIndex, rowIndex, false);
        }
        Table.nativeSetLong(tableNativePtr, columnInfo.offersTriedToUpdateDateIndex, rowIndex, ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$offersTriedToUpdateDate(), false);
        Table.nativeSetLong(tableNativePtr, columnInfo.offersUpdatedDateIndex, rowIndex, ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$offersUpdatedDate(), false);
        Number realmGet$relevantCarrierProfilesCount = ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$relevantCarrierProfilesCount();
        if (realmGet$relevantCarrierProfilesCount != null) {
            Table.nativeSetLong(tableNativePtr, columnInfo.relevantCarrierProfilesCountIndex, rowIndex, realmGet$relevantCarrierProfilesCount.longValue(), false);
        } else {
            Table.nativeSetNull(tableNativePtr, columnInfo.relevantCarrierProfilesCountIndex, rowIndex, false);
        }
        String realmGet$malinaCard = ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$malinaCard();
        if (realmGet$malinaCard != null) {
            Table.nativeSetString(tableNativePtr, columnInfo.malinaCardIndex, rowIndex, realmGet$malinaCard, false);
        } else {
            Table.nativeSetNull(tableNativePtr, columnInfo.malinaCardIndex, rowIndex, false);
        }

        OsList offersOsList = new OsList(table.getUncheckedRow(rowIndex), columnInfo.offersIndex);
        RealmList<com.kg.gettransfer.data.model.Offer> offersList = ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$offers();
        if (offersList != null && offersList.size() == offersOsList.size()) {
            // For lists of equal lengths, we need to set each element directly as clearing the receiver list can be wrong if the input and target list are the same.
            int objects = offersList.size();
            for (int i = 0; i < objects; i++) {
                com.kg.gettransfer.data.model.Offer offersItem = offersList.get(i);
                Long cacheItemIndexoffers = cache.get(offersItem);
                if (cacheItemIndexoffers == null) {
                    cacheItemIndexoffers = com_kg_gettransfer_data_model_OfferRealmProxy.insertOrUpdate(realm, offersItem, cache);
                }
                offersOsList.setRow(i, cacheItemIndexoffers);
            }
        } else {
            offersOsList.removeAll();
            if (offersList != null) {
                for (com.kg.gettransfer.data.model.Offer offersItem : offersList) {
                    Long cacheItemIndexoffers = cache.get(offersItem);
                    if (cacheItemIndexoffers == null) {
                        cacheItemIndexoffers = com_kg_gettransfer_data_model_OfferRealmProxy.insertOrUpdate(realm, offersItem, cache);
                    }
                    offersOsList.addRow(cacheItemIndexoffers);
                }
            }
        }

        return rowIndex;
    }

    public static void insertOrUpdate(Realm realm, Iterator<? extends RealmModel> objects, Map<RealmModel,Long> cache) {
        Table table = realm.getTable(com.kg.gettransfer.data.model.Transfer.class);
        long tableNativePtr = table.getNativePtr();
        TransferColumnInfo columnInfo = (TransferColumnInfo) realm.getSchema().getColumnInfo(com.kg.gettransfer.data.model.Transfer.class);
        long pkColumnIndex = columnInfo.idIndex;
        com.kg.gettransfer.data.model.Transfer object = null;
        while (objects.hasNext()) {
            object = (com.kg.gettransfer.data.model.Transfer) objects.next();
            if (cache.containsKey(object)) {
                continue;
            }
            if (object instanceof RealmObjectProxy && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm() != null && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm().getPath().equals(realm.getPath())) {
                cache.put(object, ((RealmObjectProxy) object).realmGet$proxyState().getRow$realm().getIndex());
                continue;
            }
            long rowIndex = Table.NO_MATCH;
            Object primaryKeyValue = ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$id();
            if (primaryKeyValue != null) {
                rowIndex = Table.nativeFindFirstInt(tableNativePtr, pkColumnIndex, ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$id());
            }
            if (rowIndex == Table.NO_MATCH) {
                rowIndex = OsObject.createRowWithPrimaryKey(table, pkColumnIndex, ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$id());
            }
            cache.put(object, rowIndex);
            java.util.Date realmGet$updated = ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$updated();
            if (realmGet$updated != null) {
                Table.nativeSetTimestamp(tableNativePtr, columnInfo.updatedIndex, rowIndex, realmGet$updated.getTime(), false);
            } else {
                Table.nativeSetNull(tableNativePtr, columnInfo.updatedIndex, rowIndex, false);
            }
            Table.nativeSetBoolean(tableNativePtr, columnInfo.isActiveIndex, rowIndex, ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$isActive(), false);

            com.kg.gettransfer.data.model.secondary.Location fromObj = ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$from();
            if (fromObj != null) {
                Long cachefrom = cache.get(fromObj);
                if (cachefrom == null) {
                    cachefrom = com_kg_gettransfer_data_model_secondary_LocationRealmProxy.insertOrUpdate(realm, fromObj, cache);
                }
                Table.nativeSetLink(tableNativePtr, columnInfo.fromIndex, rowIndex, cachefrom, false);
            } else {
                Table.nativeNullifyLink(tableNativePtr, columnInfo.fromIndex, rowIndex);
            }

            com.kg.gettransfer.data.model.secondary.Location toObj = ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$to();
            if (toObj != null) {
                Long cacheto = cache.get(toObj);
                if (cacheto == null) {
                    cacheto = com_kg_gettransfer_data_model_secondary_LocationRealmProxy.insertOrUpdate(realm, toObj, cache);
                }
                Table.nativeSetLink(tableNativePtr, columnInfo.toIndex, rowIndex, cacheto, false);
            } else {
                Table.nativeNullifyLink(tableNativePtr, columnInfo.toIndex, rowIndex);
            }
            Number realmGet$hireDuration = ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$hireDuration();
            if (realmGet$hireDuration != null) {
                Table.nativeSetLong(tableNativePtr, columnInfo.hireDurationIndex, rowIndex, realmGet$hireDuration.longValue(), false);
            } else {
                Table.nativeSetNull(tableNativePtr, columnInfo.hireDurationIndex, rowIndex, false);
            }
            Number realmGet$routeDistance = ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$routeDistance();
            if (realmGet$routeDistance != null) {
                Table.nativeSetLong(tableNativePtr, columnInfo.routeDistanceIndex, rowIndex, realmGet$routeDistance.longValue(), false);
            } else {
                Table.nativeSetNull(tableNativePtr, columnInfo.routeDistanceIndex, rowIndex, false);
            }
            Number realmGet$routeDuration = ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$routeDuration();
            if (realmGet$routeDuration != null) {
                Table.nativeSetLong(tableNativePtr, columnInfo.routeDurationIndex, rowIndex, realmGet$routeDuration.longValue(), false);
            } else {
                Table.nativeSetNull(tableNativePtr, columnInfo.routeDurationIndex, rowIndex, false);
            }
            String realmGet$status = ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$status();
            if (realmGet$status != null) {
                Table.nativeSetString(tableNativePtr, columnInfo.statusIndex, rowIndex, realmGet$status, false);
            } else {
                Table.nativeSetNull(tableNativePtr, columnInfo.statusIndex, rowIndex, false);
            }
            Table.nativeSetBoolean(tableNativePtr, columnInfo.bookNowIndex, rowIndex, ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$bookNow(), false);

            com.kg.gettransfer.data.model.secondary.ZonedDate dateToObj = ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$dateTo();
            if (dateToObj != null) {
                Long cachedateTo = cache.get(dateToObj);
                if (cachedateTo == null) {
                    cachedateTo = com_kg_gettransfer_data_model_secondary_ZonedDateRealmProxy.insertOrUpdate(realm, dateToObj, cache);
                }
                Table.nativeSetLink(tableNativePtr, columnInfo.dateToIndex, rowIndex, cachedateTo, false);
            } else {
                Table.nativeNullifyLink(tableNativePtr, columnInfo.dateToIndex, rowIndex);
            }

            com.kg.gettransfer.data.model.secondary.ZonedDate dateReturnObj = ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$dateReturn();
            if (dateReturnObj != null) {
                Long cachedateReturn = cache.get(dateReturnObj);
                if (cachedateReturn == null) {
                    cachedateReturn = com_kg_gettransfer_data_model_secondary_ZonedDateRealmProxy.insertOrUpdate(realm, dateReturnObj, cache);
                }
                Table.nativeSetLink(tableNativePtr, columnInfo.dateReturnIndex, rowIndex, cachedateReturn, false);
            } else {
                Table.nativeNullifyLink(tableNativePtr, columnInfo.dateReturnIndex, rowIndex);
            }
            java.util.Date realmGet$dateRefund = ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$dateRefund();
            if (realmGet$dateRefund != null) {
                Table.nativeSetTimestamp(tableNativePtr, columnInfo.dateRefundIndex, rowIndex, realmGet$dateRefund.getTime(), false);
            } else {
                Table.nativeSetNull(tableNativePtr, columnInfo.dateRefundIndex, rowIndex, false);
            }
            Table.nativeSetLong(tableNativePtr, columnInfo.paxIndex, rowIndex, ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$pax(), false);
            String realmGet$nameSign = ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$nameSign();
            if (realmGet$nameSign != null) {
                Table.nativeSetString(tableNativePtr, columnInfo.nameSignIndex, rowIndex, realmGet$nameSign, false);
            } else {
                Table.nativeSetNull(tableNativePtr, columnInfo.nameSignIndex, rowIndex, false);
            }

            OsList transportTypesOsList = new OsList(table.getUncheckedRow(rowIndex), columnInfo.transportTypesIndex);
            transportTypesOsList.removeAll();
            RealmList<java.lang.String> transportTypesList = ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$transportTypes();
            if (transportTypesList != null) {
                for (java.lang.String transportTypesItem : transportTypesList) {
                    if (transportTypesItem == null) {
                        transportTypesOsList.addNull();
                    } else {
                        transportTypesOsList.addString(transportTypesItem);
                    }
                }
            }

            String realmGet$childSeats = ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$childSeats();
            if (realmGet$childSeats != null) {
                Table.nativeSetString(tableNativePtr, columnInfo.childSeatsIndex, rowIndex, realmGet$childSeats, false);
            } else {
                Table.nativeSetNull(tableNativePtr, columnInfo.childSeatsIndex, rowIndex, false);
            }
            String realmGet$comment = ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$comment();
            if (realmGet$comment != null) {
                Table.nativeSetString(tableNativePtr, columnInfo.commentIndex, rowIndex, realmGet$comment, false);
            } else {
                Table.nativeSetNull(tableNativePtr, columnInfo.commentIndex, rowIndex, false);
            }
            String realmGet$flightNumber = ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$flightNumber();
            if (realmGet$flightNumber != null) {
                Table.nativeSetString(tableNativePtr, columnInfo.flightNumberIndex, rowIndex, realmGet$flightNumber, false);
            } else {
                Table.nativeSetNull(tableNativePtr, columnInfo.flightNumberIndex, rowIndex, false);
            }
            Table.nativeSetLong(tableNativePtr, columnInfo.offersCountIndex, rowIndex, ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$offersCount(), false);
            java.util.Date realmGet$offersChangedDate = ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$offersChangedDate();
            if (realmGet$offersChangedDate != null) {
                Table.nativeSetTimestamp(tableNativePtr, columnInfo.offersChangedDateIndex, rowIndex, realmGet$offersChangedDate.getTime(), false);
            } else {
                Table.nativeSetNull(tableNativePtr, columnInfo.offersChangedDateIndex, rowIndex, false);
            }
            Table.nativeSetLong(tableNativePtr, columnInfo.offersTriedToUpdateDateIndex, rowIndex, ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$offersTriedToUpdateDate(), false);
            Table.nativeSetLong(tableNativePtr, columnInfo.offersUpdatedDateIndex, rowIndex, ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$offersUpdatedDate(), false);
            Number realmGet$relevantCarrierProfilesCount = ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$relevantCarrierProfilesCount();
            if (realmGet$relevantCarrierProfilesCount != null) {
                Table.nativeSetLong(tableNativePtr, columnInfo.relevantCarrierProfilesCountIndex, rowIndex, realmGet$relevantCarrierProfilesCount.longValue(), false);
            } else {
                Table.nativeSetNull(tableNativePtr, columnInfo.relevantCarrierProfilesCountIndex, rowIndex, false);
            }
            String realmGet$malinaCard = ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$malinaCard();
            if (realmGet$malinaCard != null) {
                Table.nativeSetString(tableNativePtr, columnInfo.malinaCardIndex, rowIndex, realmGet$malinaCard, false);
            } else {
                Table.nativeSetNull(tableNativePtr, columnInfo.malinaCardIndex, rowIndex, false);
            }

            OsList offersOsList = new OsList(table.getUncheckedRow(rowIndex), columnInfo.offersIndex);
            RealmList<com.kg.gettransfer.data.model.Offer> offersList = ((com_kg_gettransfer_data_model_TransferRealmProxyInterface) object).realmGet$offers();
            if (offersList != null && offersList.size() == offersOsList.size()) {
                // For lists of equal lengths, we need to set each element directly as clearing the receiver list can be wrong if the input and target list are the same.
                int objectCount = offersList.size();
                for (int i = 0; i < objectCount; i++) {
                    com.kg.gettransfer.data.model.Offer offersItem = offersList.get(i);
                    Long cacheItemIndexoffers = cache.get(offersItem);
                    if (cacheItemIndexoffers == null) {
                        cacheItemIndexoffers = com_kg_gettransfer_data_model_OfferRealmProxy.insertOrUpdate(realm, offersItem, cache);
                    }
                    offersOsList.setRow(i, cacheItemIndexoffers);
                }
            } else {
                offersOsList.removeAll();
                if (offersList != null) {
                    for (com.kg.gettransfer.data.model.Offer offersItem : offersList) {
                        Long cacheItemIndexoffers = cache.get(offersItem);
                        if (cacheItemIndexoffers == null) {
                            cacheItemIndexoffers = com_kg_gettransfer_data_model_OfferRealmProxy.insertOrUpdate(realm, offersItem, cache);
                        }
                        offersOsList.addRow(cacheItemIndexoffers);
                    }
                }
            }

        }
    }

    public static com.kg.gettransfer.data.model.Transfer createDetachedCopy(com.kg.gettransfer.data.model.Transfer realmObject, int currentDepth, int maxDepth, Map<RealmModel, CacheData<RealmModel>> cache) {
        if (currentDepth > maxDepth || realmObject == null) {
            return null;
        }
        CacheData<RealmModel> cachedObject = cache.get(realmObject);
        com.kg.gettransfer.data.model.Transfer unmanagedObject;
        if (cachedObject == null) {
            unmanagedObject = new com.kg.gettransfer.data.model.Transfer();
            cache.put(realmObject, new RealmObjectProxy.CacheData<RealmModel>(currentDepth, unmanagedObject));
        } else {
            // Reuse cached object or recreate it because it was encountered at a lower depth.
            if (currentDepth >= cachedObject.minDepth) {
                return (com.kg.gettransfer.data.model.Transfer) cachedObject.object;
            }
            unmanagedObject = (com.kg.gettransfer.data.model.Transfer) cachedObject.object;
            cachedObject.minDepth = currentDepth;
        }
        com_kg_gettransfer_data_model_TransferRealmProxyInterface unmanagedCopy = (com_kg_gettransfer_data_model_TransferRealmProxyInterface) unmanagedObject;
        com_kg_gettransfer_data_model_TransferRealmProxyInterface realmSource = (com_kg_gettransfer_data_model_TransferRealmProxyInterface) realmObject;
        unmanagedCopy.realmSet$id(realmSource.realmGet$id());
        unmanagedCopy.realmSet$updated(realmSource.realmGet$updated());
        unmanagedCopy.realmSet$isActive(realmSource.realmGet$isActive());

        // Deep copy of from
        unmanagedCopy.realmSet$from(com_kg_gettransfer_data_model_secondary_LocationRealmProxy.createDetachedCopy(realmSource.realmGet$from(), currentDepth + 1, maxDepth, cache));

        // Deep copy of to
        unmanagedCopy.realmSet$to(com_kg_gettransfer_data_model_secondary_LocationRealmProxy.createDetachedCopy(realmSource.realmGet$to(), currentDepth + 1, maxDepth, cache));
        unmanagedCopy.realmSet$hireDuration(realmSource.realmGet$hireDuration());
        unmanagedCopy.realmSet$routeDistance(realmSource.realmGet$routeDistance());
        unmanagedCopy.realmSet$routeDuration(realmSource.realmGet$routeDuration());
        unmanagedCopy.realmSet$status(realmSource.realmGet$status());
        unmanagedCopy.realmSet$bookNow(realmSource.realmGet$bookNow());

        // Deep copy of dateTo
        unmanagedCopy.realmSet$dateTo(com_kg_gettransfer_data_model_secondary_ZonedDateRealmProxy.createDetachedCopy(realmSource.realmGet$dateTo(), currentDepth + 1, maxDepth, cache));

        // Deep copy of dateReturn
        unmanagedCopy.realmSet$dateReturn(com_kg_gettransfer_data_model_secondary_ZonedDateRealmProxy.createDetachedCopy(realmSource.realmGet$dateReturn(), currentDepth + 1, maxDepth, cache));
        unmanagedCopy.realmSet$dateRefund(realmSource.realmGet$dateRefund());
        unmanagedCopy.realmSet$pax(realmSource.realmGet$pax());
        unmanagedCopy.realmSet$nameSign(realmSource.realmGet$nameSign());

        unmanagedCopy.realmSet$transportTypes(new RealmList<java.lang.String>());
        unmanagedCopy.realmGet$transportTypes().addAll(realmSource.realmGet$transportTypes());
        unmanagedCopy.realmSet$childSeats(realmSource.realmGet$childSeats());
        unmanagedCopy.realmSet$comment(realmSource.realmGet$comment());
        unmanagedCopy.realmSet$flightNumber(realmSource.realmGet$flightNumber());
        unmanagedCopy.realmSet$offersCount(realmSource.realmGet$offersCount());
        unmanagedCopy.realmSet$offersChangedDate(realmSource.realmGet$offersChangedDate());
        unmanagedCopy.realmSet$offersTriedToUpdateDate(realmSource.realmGet$offersTriedToUpdateDate());
        unmanagedCopy.realmSet$offersUpdatedDate(realmSource.realmGet$offersUpdatedDate());
        unmanagedCopy.realmSet$relevantCarrierProfilesCount(realmSource.realmGet$relevantCarrierProfilesCount());
        unmanagedCopy.realmSet$malinaCard(realmSource.realmGet$malinaCard());

        // Deep copy of offers
        if (currentDepth == maxDepth) {
            unmanagedCopy.realmSet$offers(null);
        } else {
            RealmList<com.kg.gettransfer.data.model.Offer> managedoffersList = realmSource.realmGet$offers();
            RealmList<com.kg.gettransfer.data.model.Offer> unmanagedoffersList = new RealmList<com.kg.gettransfer.data.model.Offer>();
            unmanagedCopy.realmSet$offers(unmanagedoffersList);
            int nextDepth = currentDepth + 1;
            int size = managedoffersList.size();
            for (int i = 0; i < size; i++) {
                com.kg.gettransfer.data.model.Offer item = com_kg_gettransfer_data_model_OfferRealmProxy.createDetachedCopy(managedoffersList.get(i), nextDepth, maxDepth, cache);
                unmanagedoffersList.add(item);
            }
        }

        return unmanagedObject;
    }

    static com.kg.gettransfer.data.model.Transfer update(Realm realm, com.kg.gettransfer.data.model.Transfer realmObject, com.kg.gettransfer.data.model.Transfer newObject, Map<RealmModel, RealmObjectProxy> cache) {
        com_kg_gettransfer_data_model_TransferRealmProxyInterface realmObjectTarget = (com_kg_gettransfer_data_model_TransferRealmProxyInterface) realmObject;
        com_kg_gettransfer_data_model_TransferRealmProxyInterface realmObjectSource = (com_kg_gettransfer_data_model_TransferRealmProxyInterface) newObject;
        realmObjectTarget.realmSet$updated(realmObjectSource.realmGet$updated());
        realmObjectTarget.realmSet$isActive(realmObjectSource.realmGet$isActive());
        com.kg.gettransfer.data.model.secondary.Location fromObj = realmObjectSource.realmGet$from();
        if (fromObj == null) {
            realmObjectTarget.realmSet$from(null);
        } else {
            com.kg.gettransfer.data.model.secondary.Location cachefrom = (com.kg.gettransfer.data.model.secondary.Location) cache.get(fromObj);
            if (cachefrom != null) {
                realmObjectTarget.realmSet$from(cachefrom);
            } else {
                realmObjectTarget.realmSet$from(com_kg_gettransfer_data_model_secondary_LocationRealmProxy.copyOrUpdate(realm, fromObj, true, cache));
            }
        }
        com.kg.gettransfer.data.model.secondary.Location toObj = realmObjectSource.realmGet$to();
        if (toObj == null) {
            realmObjectTarget.realmSet$to(null);
        } else {
            com.kg.gettransfer.data.model.secondary.Location cacheto = (com.kg.gettransfer.data.model.secondary.Location) cache.get(toObj);
            if (cacheto != null) {
                realmObjectTarget.realmSet$to(cacheto);
            } else {
                realmObjectTarget.realmSet$to(com_kg_gettransfer_data_model_secondary_LocationRealmProxy.copyOrUpdate(realm, toObj, true, cache));
            }
        }
        realmObjectTarget.realmSet$hireDuration(realmObjectSource.realmGet$hireDuration());
        realmObjectTarget.realmSet$routeDistance(realmObjectSource.realmGet$routeDistance());
        realmObjectTarget.realmSet$routeDuration(realmObjectSource.realmGet$routeDuration());
        realmObjectTarget.realmSet$status(realmObjectSource.realmGet$status());
        realmObjectTarget.realmSet$bookNow(realmObjectSource.realmGet$bookNow());
        com.kg.gettransfer.data.model.secondary.ZonedDate dateToObj = realmObjectSource.realmGet$dateTo();
        if (dateToObj == null) {
            realmObjectTarget.realmSet$dateTo(null);
        } else {
            com.kg.gettransfer.data.model.secondary.ZonedDate cachedateTo = (com.kg.gettransfer.data.model.secondary.ZonedDate) cache.get(dateToObj);
            if (cachedateTo != null) {
                realmObjectTarget.realmSet$dateTo(cachedateTo);
            } else {
                realmObjectTarget.realmSet$dateTo(com_kg_gettransfer_data_model_secondary_ZonedDateRealmProxy.copyOrUpdate(realm, dateToObj, true, cache));
            }
        }
        com.kg.gettransfer.data.model.secondary.ZonedDate dateReturnObj = realmObjectSource.realmGet$dateReturn();
        if (dateReturnObj == null) {
            realmObjectTarget.realmSet$dateReturn(null);
        } else {
            com.kg.gettransfer.data.model.secondary.ZonedDate cachedateReturn = (com.kg.gettransfer.data.model.secondary.ZonedDate) cache.get(dateReturnObj);
            if (cachedateReturn != null) {
                realmObjectTarget.realmSet$dateReturn(cachedateReturn);
            } else {
                realmObjectTarget.realmSet$dateReturn(com_kg_gettransfer_data_model_secondary_ZonedDateRealmProxy.copyOrUpdate(realm, dateReturnObj, true, cache));
            }
        }
        realmObjectTarget.realmSet$dateRefund(realmObjectSource.realmGet$dateRefund());
        realmObjectTarget.realmSet$pax(realmObjectSource.realmGet$pax());
        realmObjectTarget.realmSet$nameSign(realmObjectSource.realmGet$nameSign());
        realmObjectTarget.realmSet$transportTypes(realmObjectSource.realmGet$transportTypes());
        realmObjectTarget.realmSet$childSeats(realmObjectSource.realmGet$childSeats());
        realmObjectTarget.realmSet$comment(realmObjectSource.realmGet$comment());
        realmObjectTarget.realmSet$flightNumber(realmObjectSource.realmGet$flightNumber());
        realmObjectTarget.realmSet$offersCount(realmObjectSource.realmGet$offersCount());
        realmObjectTarget.realmSet$offersChangedDate(realmObjectSource.realmGet$offersChangedDate());
        realmObjectTarget.realmSet$offersTriedToUpdateDate(realmObjectSource.realmGet$offersTriedToUpdateDate());
        realmObjectTarget.realmSet$offersUpdatedDate(realmObjectSource.realmGet$offersUpdatedDate());
        realmObjectTarget.realmSet$relevantCarrierProfilesCount(realmObjectSource.realmGet$relevantCarrierProfilesCount());
        realmObjectTarget.realmSet$malinaCard(realmObjectSource.realmGet$malinaCard());
        RealmList<com.kg.gettransfer.data.model.Offer> offersList = realmObjectSource.realmGet$offers();
        RealmList<com.kg.gettransfer.data.model.Offer> offersRealmList = realmObjectTarget.realmGet$offers();
        if (offersList != null && offersList.size() == offersRealmList.size()) {
            // For lists of equal lengths, we need to set each element directly as clearing the receiver list can be wrong if the input and target list are the same.
            int objects = offersList.size();
            for (int i = 0; i < objects; i++) {
                com.kg.gettransfer.data.model.Offer offersItem = offersList.get(i);
                com.kg.gettransfer.data.model.Offer cacheoffers = (com.kg.gettransfer.data.model.Offer) cache.get(offersItem);
                if (cacheoffers != null) {
                    offersRealmList.set(i, cacheoffers);
                } else {
                    offersRealmList.set(i, com_kg_gettransfer_data_model_OfferRealmProxy.copyOrUpdate(realm, offersItem, true, cache));
                }
            }
        } else {
            offersRealmList.clear();
            if (offersList != null) {
                for (int i = 0; i < offersList.size(); i++) {
                    com.kg.gettransfer.data.model.Offer offersItem = offersList.get(i);
                    com.kg.gettransfer.data.model.Offer cacheoffers = (com.kg.gettransfer.data.model.Offer) cache.get(offersItem);
                    if (cacheoffers != null) {
                        offersRealmList.add(cacheoffers);
                    } else {
                        offersRealmList.add(com_kg_gettransfer_data_model_OfferRealmProxy.copyOrUpdate(realm, offersItem, true, cache));
                    }
                }
            }
        }
        return realmObject;
    }

    @Override
    @SuppressWarnings("ArrayToString")
    public String toString() {
        if (!RealmObject.isValid(this)) {
            return "Invalid object";
        }
        StringBuilder stringBuilder = new StringBuilder("Transfer = proxy[");
        stringBuilder.append("{id:");
        stringBuilder.append(realmGet$id());
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{updated:");
        stringBuilder.append(realmGet$updated() != null ? realmGet$updated() : "null");
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{isActive:");
        stringBuilder.append(realmGet$isActive());
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{from:");
        stringBuilder.append(realmGet$from() != null ? "Location" : "null");
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{to:");
        stringBuilder.append(realmGet$to() != null ? "Location" : "null");
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{hireDuration:");
        stringBuilder.append(realmGet$hireDuration() != null ? realmGet$hireDuration() : "null");
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{routeDistance:");
        stringBuilder.append(realmGet$routeDistance() != null ? realmGet$routeDistance() : "null");
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{routeDuration:");
        stringBuilder.append(realmGet$routeDuration() != null ? realmGet$routeDuration() : "null");
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{status:");
        stringBuilder.append(realmGet$status() != null ? realmGet$status() : "null");
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{bookNow:");
        stringBuilder.append(realmGet$bookNow());
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{dateTo:");
        stringBuilder.append(realmGet$dateTo() != null ? "ZonedDate" : "null");
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{dateReturn:");
        stringBuilder.append(realmGet$dateReturn() != null ? "ZonedDate" : "null");
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{dateRefund:");
        stringBuilder.append(realmGet$dateRefund() != null ? realmGet$dateRefund() : "null");
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{pax:");
        stringBuilder.append(realmGet$pax());
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{nameSign:");
        stringBuilder.append(realmGet$nameSign() != null ? realmGet$nameSign() : "null");
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{transportTypes:");
        stringBuilder.append("RealmList<String>[").append(realmGet$transportTypes().size()).append("]");
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{childSeats:");
        stringBuilder.append(realmGet$childSeats() != null ? realmGet$childSeats() : "null");
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{comment:");
        stringBuilder.append(realmGet$comment() != null ? realmGet$comment() : "null");
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{flightNumber:");
        stringBuilder.append(realmGet$flightNumber() != null ? realmGet$flightNumber() : "null");
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{offersCount:");
        stringBuilder.append(realmGet$offersCount());
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{offersChangedDate:");
        stringBuilder.append(realmGet$offersChangedDate() != null ? realmGet$offersChangedDate() : "null");
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{offersTriedToUpdateDate:");
        stringBuilder.append(realmGet$offersTriedToUpdateDate());
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{offersUpdatedDate:");
        stringBuilder.append(realmGet$offersUpdatedDate());
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{relevantCarrierProfilesCount:");
        stringBuilder.append(realmGet$relevantCarrierProfilesCount() != null ? realmGet$relevantCarrierProfilesCount() : "null");
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{malinaCard:");
        stringBuilder.append(realmGet$malinaCard() != null ? realmGet$malinaCard() : "null");
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{offers:");
        stringBuilder.append("RealmList<Offer>[").append(realmGet$offers().size()).append("]");
        stringBuilder.append("}");
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

    @Override
    public ProxyState<?> realmGet$proxyState() {
        return proxyState;
    }

    @Override
    public int hashCode() {
        String realmName = proxyState.getRealm$realm().getPath();
        String tableName = proxyState.getRow$realm().getTable().getName();
        long rowIndex = proxyState.getRow$realm().getIndex();

        int result = 17;
        result = 31 * result + ((realmName != null) ? realmName.hashCode() : 0);
        result = 31 * result + ((tableName != null) ? tableName.hashCode() : 0);
        result = 31 * result + (int) (rowIndex ^ (rowIndex >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        com_kg_gettransfer_data_model_TransferRealmProxy aTransfer = (com_kg_gettransfer_data_model_TransferRealmProxy)o;

        String path = proxyState.getRealm$realm().getPath();
        String otherPath = aTransfer.proxyState.getRealm$realm().getPath();
        if (path != null ? !path.equals(otherPath) : otherPath != null) return false;

        String tableName = proxyState.getRow$realm().getTable().getName();
        String otherTableName = aTransfer.proxyState.getRow$realm().getTable().getName();
        if (tableName != null ? !tableName.equals(otherTableName) : otherTableName != null) return false;

        if (proxyState.getRow$realm().getIndex() != aTransfer.proxyState.getRow$realm().getIndex()) return false;

        return true;
    }
}
