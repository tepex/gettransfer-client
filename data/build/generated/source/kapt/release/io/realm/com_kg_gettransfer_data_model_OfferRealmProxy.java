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
public class com_kg_gettransfer_data_model_OfferRealmProxy extends com.kg.gettransfer.data.model.Offer
    implements RealmObjectProxy, com_kg_gettransfer_data_model_OfferRealmProxyInterface {

    static final class OfferColumnInfo extends ColumnInfo {
        long idIndex;
        long priceIndex;
        long statusIndex;
        long carrierIndex;
        long vehicleIndex;
        long wifiIndex;
        long refreshmentsIndex;

        OfferColumnInfo(OsSchemaInfo schemaInfo) {
            super(7);
            OsObjectSchemaInfo objectSchemaInfo = schemaInfo.getObjectSchemaInfo("Offer");
            this.idIndex = addColumnDetails("id", "id", objectSchemaInfo);
            this.priceIndex = addColumnDetails("price", "price", objectSchemaInfo);
            this.statusIndex = addColumnDetails("status", "status", objectSchemaInfo);
            this.carrierIndex = addColumnDetails("carrier", "carrier", objectSchemaInfo);
            this.vehicleIndex = addColumnDetails("vehicle", "vehicle", objectSchemaInfo);
            this.wifiIndex = addColumnDetails("wifi", "wifi", objectSchemaInfo);
            this.refreshmentsIndex = addColumnDetails("refreshments", "refreshments", objectSchemaInfo);
        }

        OfferColumnInfo(ColumnInfo src, boolean mutable) {
            super(src, mutable);
            copy(src, this);
        }

        @Override
        protected final ColumnInfo copy(boolean mutable) {
            return new OfferColumnInfo(this, mutable);
        }

        @Override
        protected final void copy(ColumnInfo rawSrc, ColumnInfo rawDst) {
            final OfferColumnInfo src = (OfferColumnInfo) rawSrc;
            final OfferColumnInfo dst = (OfferColumnInfo) rawDst;
            dst.idIndex = src.idIndex;
            dst.priceIndex = src.priceIndex;
            dst.statusIndex = src.statusIndex;
            dst.carrierIndex = src.carrierIndex;
            dst.vehicleIndex = src.vehicleIndex;
            dst.wifiIndex = src.wifiIndex;
            dst.refreshmentsIndex = src.refreshmentsIndex;
        }
    }

    private static final OsObjectSchemaInfo expectedObjectSchemaInfo = createExpectedObjectSchemaInfo();

    private OfferColumnInfo columnInfo;
    private ProxyState<com.kg.gettransfer.data.model.Offer> proxyState;

    com_kg_gettransfer_data_model_OfferRealmProxy() {
        proxyState.setConstructionFinished();
    }

    @Override
    public void realm$injectObjectContext() {
        if (this.proxyState != null) {
            return;
        }
        final BaseRealm.RealmObjectContext context = BaseRealm.objectContext.get();
        this.columnInfo = (OfferColumnInfo) context.getColumnInfo();
        this.proxyState = new ProxyState<com.kg.gettransfer.data.model.Offer>(this);
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
    public com.kg.gettransfer.data.model.secondary.Price realmGet$price() {
        proxyState.getRealm$realm().checkIfValid();
        if (proxyState.getRow$realm().isNullLink(columnInfo.priceIndex)) {
            return null;
        }
        return proxyState.getRealm$realm().get(com.kg.gettransfer.data.model.secondary.Price.class, proxyState.getRow$realm().getLink(columnInfo.priceIndex), false, Collections.<String>emptyList());
    }

    @Override
    public void realmSet$price(com.kg.gettransfer.data.model.secondary.Price value) {
        if (proxyState.isUnderConstruction()) {
            if (!proxyState.getAcceptDefaultValue$realm()) {
                return;
            }
            if (proxyState.getExcludeFields$realm().contains("price")) {
                return;
            }
            if (value != null && !RealmObject.isManaged(value)) {
                value = ((Realm) proxyState.getRealm$realm()).copyToRealm(value);
            }
            final Row row = proxyState.getRow$realm();
            if (value == null) {
                // Table#nullifyLink() does not support default value. Just using Row.
                row.nullifyLink(columnInfo.priceIndex);
                return;
            }
            proxyState.checkValidObject(value);
            row.getTable().setLink(columnInfo.priceIndex, row.getIndex(), ((RealmObjectProxy) value).realmGet$proxyState().getRow$realm().getIndex(), true);
            return;
        }

        proxyState.getRealm$realm().checkIfValid();
        if (value == null) {
            proxyState.getRow$realm().nullifyLink(columnInfo.priceIndex);
            return;
        }
        proxyState.checkValidObject(value);
        proxyState.getRow$realm().setLink(columnInfo.priceIndex, ((RealmObjectProxy) value).realmGet$proxyState().getRow$realm().getIndex());
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
    public com.kg.gettransfer.data.model.Carrier realmGet$carrier() {
        proxyState.getRealm$realm().checkIfValid();
        if (proxyState.getRow$realm().isNullLink(columnInfo.carrierIndex)) {
            return null;
        }
        return proxyState.getRealm$realm().get(com.kg.gettransfer.data.model.Carrier.class, proxyState.getRow$realm().getLink(columnInfo.carrierIndex), false, Collections.<String>emptyList());
    }

    @Override
    public void realmSet$carrier(com.kg.gettransfer.data.model.Carrier value) {
        if (proxyState.isUnderConstruction()) {
            if (!proxyState.getAcceptDefaultValue$realm()) {
                return;
            }
            if (proxyState.getExcludeFields$realm().contains("carrier")) {
                return;
            }
            if (value != null && !RealmObject.isManaged(value)) {
                value = ((Realm) proxyState.getRealm$realm()).copyToRealm(value);
            }
            final Row row = proxyState.getRow$realm();
            if (value == null) {
                // Table#nullifyLink() does not support default value. Just using Row.
                row.nullifyLink(columnInfo.carrierIndex);
                return;
            }
            proxyState.checkValidObject(value);
            row.getTable().setLink(columnInfo.carrierIndex, row.getIndex(), ((RealmObjectProxy) value).realmGet$proxyState().getRow$realm().getIndex(), true);
            return;
        }

        proxyState.getRealm$realm().checkIfValid();
        if (value == null) {
            proxyState.getRow$realm().nullifyLink(columnInfo.carrierIndex);
            return;
        }
        proxyState.checkValidObject(value);
        proxyState.getRow$realm().setLink(columnInfo.carrierIndex, ((RealmObjectProxy) value).realmGet$proxyState().getRow$realm().getIndex());
    }

    @Override
    public com.kg.gettransfer.data.model.Vehicle realmGet$vehicle() {
        proxyState.getRealm$realm().checkIfValid();
        if (proxyState.getRow$realm().isNullLink(columnInfo.vehicleIndex)) {
            return null;
        }
        return proxyState.getRealm$realm().get(com.kg.gettransfer.data.model.Vehicle.class, proxyState.getRow$realm().getLink(columnInfo.vehicleIndex), false, Collections.<String>emptyList());
    }

    @Override
    public void realmSet$vehicle(com.kg.gettransfer.data.model.Vehicle value) {
        if (proxyState.isUnderConstruction()) {
            if (!proxyState.getAcceptDefaultValue$realm()) {
                return;
            }
            if (proxyState.getExcludeFields$realm().contains("vehicle")) {
                return;
            }
            if (value != null && !RealmObject.isManaged(value)) {
                value = ((Realm) proxyState.getRealm$realm()).copyToRealm(value);
            }
            final Row row = proxyState.getRow$realm();
            if (value == null) {
                // Table#nullifyLink() does not support default value. Just using Row.
                row.nullifyLink(columnInfo.vehicleIndex);
                return;
            }
            proxyState.checkValidObject(value);
            row.getTable().setLink(columnInfo.vehicleIndex, row.getIndex(), ((RealmObjectProxy) value).realmGet$proxyState().getRow$realm().getIndex(), true);
            return;
        }

        proxyState.getRealm$realm().checkIfValid();
        if (value == null) {
            proxyState.getRow$realm().nullifyLink(columnInfo.vehicleIndex);
            return;
        }
        proxyState.checkValidObject(value);
        proxyState.getRow$realm().setLink(columnInfo.vehicleIndex, ((RealmObjectProxy) value).realmGet$proxyState().getRow$realm().getIndex());
    }

    @Override
    @SuppressWarnings("cast")
    public Boolean realmGet$wifi() {
        proxyState.getRealm$realm().checkIfValid();
        if (proxyState.getRow$realm().isNull(columnInfo.wifiIndex)) {
            return null;
        }
        return (boolean) proxyState.getRow$realm().getBoolean(columnInfo.wifiIndex);
    }

    @Override
    public void realmSet$wifi(Boolean value) {
        if (proxyState.isUnderConstruction()) {
            if (!proxyState.getAcceptDefaultValue$realm()) {
                return;
            }
            final Row row = proxyState.getRow$realm();
            if (value == null) {
                row.getTable().setNull(columnInfo.wifiIndex, row.getIndex(), true);
                return;
            }
            row.getTable().setBoolean(columnInfo.wifiIndex, row.getIndex(), value, true);
            return;
        }

        proxyState.getRealm$realm().checkIfValid();
        if (value == null) {
            proxyState.getRow$realm().setNull(columnInfo.wifiIndex);
            return;
        }
        proxyState.getRow$realm().setBoolean(columnInfo.wifiIndex, value);
    }

    @Override
    @SuppressWarnings("cast")
    public Boolean realmGet$refreshments() {
        proxyState.getRealm$realm().checkIfValid();
        if (proxyState.getRow$realm().isNull(columnInfo.refreshmentsIndex)) {
            return null;
        }
        return (boolean) proxyState.getRow$realm().getBoolean(columnInfo.refreshmentsIndex);
    }

    @Override
    public void realmSet$refreshments(Boolean value) {
        if (proxyState.isUnderConstruction()) {
            if (!proxyState.getAcceptDefaultValue$realm()) {
                return;
            }
            final Row row = proxyState.getRow$realm();
            if (value == null) {
                row.getTable().setNull(columnInfo.refreshmentsIndex, row.getIndex(), true);
                return;
            }
            row.getTable().setBoolean(columnInfo.refreshmentsIndex, row.getIndex(), value, true);
            return;
        }

        proxyState.getRealm$realm().checkIfValid();
        if (value == null) {
            proxyState.getRow$realm().setNull(columnInfo.refreshmentsIndex);
            return;
        }
        proxyState.getRow$realm().setBoolean(columnInfo.refreshmentsIndex, value);
    }

    private static OsObjectSchemaInfo createExpectedObjectSchemaInfo() {
        OsObjectSchemaInfo.Builder builder = new OsObjectSchemaInfo.Builder("Offer", 7, 0);
        builder.addPersistedProperty("id", RealmFieldType.INTEGER, Property.PRIMARY_KEY, Property.INDEXED, Property.REQUIRED);
        builder.addPersistedLinkProperty("price", RealmFieldType.OBJECT, "Price");
        builder.addPersistedProperty("status", RealmFieldType.STRING, !Property.PRIMARY_KEY, !Property.INDEXED, !Property.REQUIRED);
        builder.addPersistedLinkProperty("carrier", RealmFieldType.OBJECT, "Carrier");
        builder.addPersistedLinkProperty("vehicle", RealmFieldType.OBJECT, "Vehicle");
        builder.addPersistedProperty("wifi", RealmFieldType.BOOLEAN, !Property.PRIMARY_KEY, !Property.INDEXED, !Property.REQUIRED);
        builder.addPersistedProperty("refreshments", RealmFieldType.BOOLEAN, !Property.PRIMARY_KEY, !Property.INDEXED, !Property.REQUIRED);
        return builder.build();
    }

    public static OsObjectSchemaInfo getExpectedObjectSchemaInfo() {
        return expectedObjectSchemaInfo;
    }

    public static OfferColumnInfo createColumnInfo(OsSchemaInfo schemaInfo) {
        return new OfferColumnInfo(schemaInfo);
    }

    public static String getSimpleClassName() {
        return "Offer";
    }

    public static final class ClassNameHelper {
        public static final String INTERNAL_CLASS_NAME = "Offer";
    }

    @SuppressWarnings("cast")
    public static com.kg.gettransfer.data.model.Offer createOrUpdateUsingJsonObject(Realm realm, JSONObject json, boolean update)
        throws JSONException {
        final List<String> excludeFields = new ArrayList<String>(3);
        com.kg.gettransfer.data.model.Offer obj = null;
        if (update) {
            Table table = realm.getTable(com.kg.gettransfer.data.model.Offer.class);
            OfferColumnInfo columnInfo = (OfferColumnInfo) realm.getSchema().getColumnInfo(com.kg.gettransfer.data.model.Offer.class);
            long pkColumnIndex = columnInfo.idIndex;
            long rowIndex = Table.NO_MATCH;
            if (!json.isNull("id")) {
                rowIndex = table.findFirstLong(pkColumnIndex, json.getLong("id"));
            }
            if (rowIndex != Table.NO_MATCH) {
                final BaseRealm.RealmObjectContext objectContext = BaseRealm.objectContext.get();
                try {
                    objectContext.set(realm, table.getUncheckedRow(rowIndex), realm.getSchema().getColumnInfo(com.kg.gettransfer.data.model.Offer.class), false, Collections.<String> emptyList());
                    obj = new io.realm.com_kg_gettransfer_data_model_OfferRealmProxy();
                } finally {
                    objectContext.clear();
                }
            }
        }
        if (obj == null) {
            if (json.has("price")) {
                excludeFields.add("price");
            }
            if (json.has("carrier")) {
                excludeFields.add("carrier");
            }
            if (json.has("vehicle")) {
                excludeFields.add("vehicle");
            }
            if (json.has("id")) {
                if (json.isNull("id")) {
                    obj = (io.realm.com_kg_gettransfer_data_model_OfferRealmProxy) realm.createObjectInternal(com.kg.gettransfer.data.model.Offer.class, null, true, excludeFields);
                } else {
                    obj = (io.realm.com_kg_gettransfer_data_model_OfferRealmProxy) realm.createObjectInternal(com.kg.gettransfer.data.model.Offer.class, json.getInt("id"), true, excludeFields);
                }
            } else {
                throw new IllegalArgumentException("JSON object doesn't have the primary key field 'id'.");
            }
        }

        final com_kg_gettransfer_data_model_OfferRealmProxyInterface objProxy = (com_kg_gettransfer_data_model_OfferRealmProxyInterface) obj;
        if (json.has("price")) {
            if (json.isNull("price")) {
                objProxy.realmSet$price(null);
            } else {
                com.kg.gettransfer.data.model.secondary.Price priceObj = com_kg_gettransfer_data_model_secondary_PriceRealmProxy.createOrUpdateUsingJsonObject(realm, json.getJSONObject("price"), update);
                objProxy.realmSet$price(priceObj);
            }
        }
        if (json.has("status")) {
            if (json.isNull("status")) {
                objProxy.realmSet$status(null);
            } else {
                objProxy.realmSet$status((String) json.getString("status"));
            }
        }
        if (json.has("carrier")) {
            if (json.isNull("carrier")) {
                objProxy.realmSet$carrier(null);
            } else {
                com.kg.gettransfer.data.model.Carrier carrierObj = com_kg_gettransfer_data_model_CarrierRealmProxy.createOrUpdateUsingJsonObject(realm, json.getJSONObject("carrier"), update);
                objProxy.realmSet$carrier(carrierObj);
            }
        }
        if (json.has("vehicle")) {
            if (json.isNull("vehicle")) {
                objProxy.realmSet$vehicle(null);
            } else {
                com.kg.gettransfer.data.model.Vehicle vehicleObj = com_kg_gettransfer_data_model_VehicleRealmProxy.createOrUpdateUsingJsonObject(realm, json.getJSONObject("vehicle"), update);
                objProxy.realmSet$vehicle(vehicleObj);
            }
        }
        if (json.has("wifi")) {
            if (json.isNull("wifi")) {
                objProxy.realmSet$wifi(null);
            } else {
                objProxy.realmSet$wifi((boolean) json.getBoolean("wifi"));
            }
        }
        if (json.has("refreshments")) {
            if (json.isNull("refreshments")) {
                objProxy.realmSet$refreshments(null);
            } else {
                objProxy.realmSet$refreshments((boolean) json.getBoolean("refreshments"));
            }
        }
        return obj;
    }

    @SuppressWarnings("cast")
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static com.kg.gettransfer.data.model.Offer createUsingJsonStream(Realm realm, JsonReader reader)
        throws IOException {
        boolean jsonHasPrimaryKey = false;
        final com.kg.gettransfer.data.model.Offer obj = new com.kg.gettransfer.data.model.Offer();
        final com_kg_gettransfer_data_model_OfferRealmProxyInterface objProxy = (com_kg_gettransfer_data_model_OfferRealmProxyInterface) obj;
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
            } else if (name.equals("price")) {
                if (reader.peek() == JsonToken.NULL) {
                    reader.skipValue();
                    objProxy.realmSet$price(null);
                } else {
                    com.kg.gettransfer.data.model.secondary.Price priceObj = com_kg_gettransfer_data_model_secondary_PriceRealmProxy.createUsingJsonStream(realm, reader);
                    objProxy.realmSet$price(priceObj);
                }
            } else if (name.equals("status")) {
                if (reader.peek() != JsonToken.NULL) {
                    objProxy.realmSet$status((String) reader.nextString());
                } else {
                    reader.skipValue();
                    objProxy.realmSet$status(null);
                }
            } else if (name.equals("carrier")) {
                if (reader.peek() == JsonToken.NULL) {
                    reader.skipValue();
                    objProxy.realmSet$carrier(null);
                } else {
                    com.kg.gettransfer.data.model.Carrier carrierObj = com_kg_gettransfer_data_model_CarrierRealmProxy.createUsingJsonStream(realm, reader);
                    objProxy.realmSet$carrier(carrierObj);
                }
            } else if (name.equals("vehicle")) {
                if (reader.peek() == JsonToken.NULL) {
                    reader.skipValue();
                    objProxy.realmSet$vehicle(null);
                } else {
                    com.kg.gettransfer.data.model.Vehicle vehicleObj = com_kg_gettransfer_data_model_VehicleRealmProxy.createUsingJsonStream(realm, reader);
                    objProxy.realmSet$vehicle(vehicleObj);
                }
            } else if (name.equals("wifi")) {
                if (reader.peek() != JsonToken.NULL) {
                    objProxy.realmSet$wifi((boolean) reader.nextBoolean());
                } else {
                    reader.skipValue();
                    objProxy.realmSet$wifi(null);
                }
            } else if (name.equals("refreshments")) {
                if (reader.peek() != JsonToken.NULL) {
                    objProxy.realmSet$refreshments((boolean) reader.nextBoolean());
                } else {
                    reader.skipValue();
                    objProxy.realmSet$refreshments(null);
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

    public static com.kg.gettransfer.data.model.Offer copyOrUpdate(Realm realm, com.kg.gettransfer.data.model.Offer object, boolean update, Map<RealmModel,RealmObjectProxy> cache) {
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
            return (com.kg.gettransfer.data.model.Offer) cachedRealmObject;
        }

        com.kg.gettransfer.data.model.Offer realmObject = null;
        boolean canUpdate = update;
        if (canUpdate) {
            Table table = realm.getTable(com.kg.gettransfer.data.model.Offer.class);
            OfferColumnInfo columnInfo = (OfferColumnInfo) realm.getSchema().getColumnInfo(com.kg.gettransfer.data.model.Offer.class);
            long pkColumnIndex = columnInfo.idIndex;
            long rowIndex = table.findFirstLong(pkColumnIndex, ((com_kg_gettransfer_data_model_OfferRealmProxyInterface) object).realmGet$id());
            if (rowIndex == Table.NO_MATCH) {
                canUpdate = false;
            } else {
                try {
                    objectContext.set(realm, table.getUncheckedRow(rowIndex), realm.getSchema().getColumnInfo(com.kg.gettransfer.data.model.Offer.class), false, Collections.<String> emptyList());
                    realmObject = new io.realm.com_kg_gettransfer_data_model_OfferRealmProxy();
                    cache.put(object, (RealmObjectProxy) realmObject);
                } finally {
                    objectContext.clear();
                }
            }
        }

        return (canUpdate) ? update(realm, realmObject, object, cache) : copy(realm, object, update, cache);
    }

    public static com.kg.gettransfer.data.model.Offer copy(Realm realm, com.kg.gettransfer.data.model.Offer newObject, boolean update, Map<RealmModel,RealmObjectProxy> cache) {
        RealmObjectProxy cachedRealmObject = cache.get(newObject);
        if (cachedRealmObject != null) {
            return (com.kg.gettransfer.data.model.Offer) cachedRealmObject;
        }

        // rejecting default values to avoid creating unexpected objects from RealmModel/RealmList fields.
        com.kg.gettransfer.data.model.Offer realmObject = realm.createObjectInternal(com.kg.gettransfer.data.model.Offer.class, ((com_kg_gettransfer_data_model_OfferRealmProxyInterface) newObject).realmGet$id(), false, Collections.<String>emptyList());
        cache.put(newObject, (RealmObjectProxy) realmObject);

        com_kg_gettransfer_data_model_OfferRealmProxyInterface realmObjectSource = (com_kg_gettransfer_data_model_OfferRealmProxyInterface) newObject;
        com_kg_gettransfer_data_model_OfferRealmProxyInterface realmObjectCopy = (com_kg_gettransfer_data_model_OfferRealmProxyInterface) realmObject;


        com.kg.gettransfer.data.model.secondary.Price priceObj = realmObjectSource.realmGet$price();
        if (priceObj == null) {
            realmObjectCopy.realmSet$price(null);
        } else {
            com.kg.gettransfer.data.model.secondary.Price cacheprice = (com.kg.gettransfer.data.model.secondary.Price) cache.get(priceObj);
            if (cacheprice != null) {
                realmObjectCopy.realmSet$price(cacheprice);
            } else {
                realmObjectCopy.realmSet$price(com_kg_gettransfer_data_model_secondary_PriceRealmProxy.copyOrUpdate(realm, priceObj, update, cache));
            }
        }
        realmObjectCopy.realmSet$status(realmObjectSource.realmGet$status());

        com.kg.gettransfer.data.model.Carrier carrierObj = realmObjectSource.realmGet$carrier();
        if (carrierObj == null) {
            realmObjectCopy.realmSet$carrier(null);
        } else {
            com.kg.gettransfer.data.model.Carrier cachecarrier = (com.kg.gettransfer.data.model.Carrier) cache.get(carrierObj);
            if (cachecarrier != null) {
                realmObjectCopy.realmSet$carrier(cachecarrier);
            } else {
                realmObjectCopy.realmSet$carrier(com_kg_gettransfer_data_model_CarrierRealmProxy.copyOrUpdate(realm, carrierObj, update, cache));
            }
        }

        com.kg.gettransfer.data.model.Vehicle vehicleObj = realmObjectSource.realmGet$vehicle();
        if (vehicleObj == null) {
            realmObjectCopy.realmSet$vehicle(null);
        } else {
            com.kg.gettransfer.data.model.Vehicle cachevehicle = (com.kg.gettransfer.data.model.Vehicle) cache.get(vehicleObj);
            if (cachevehicle != null) {
                realmObjectCopy.realmSet$vehicle(cachevehicle);
            } else {
                realmObjectCopy.realmSet$vehicle(com_kg_gettransfer_data_model_VehicleRealmProxy.copyOrUpdate(realm, vehicleObj, update, cache));
            }
        }
        realmObjectCopy.realmSet$wifi(realmObjectSource.realmGet$wifi());
        realmObjectCopy.realmSet$refreshments(realmObjectSource.realmGet$refreshments());
        return realmObject;
    }

    public static long insert(Realm realm, com.kg.gettransfer.data.model.Offer object, Map<RealmModel,Long> cache) {
        if (object instanceof RealmObjectProxy && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm() != null && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm().getPath().equals(realm.getPath())) {
            return ((RealmObjectProxy) object).realmGet$proxyState().getRow$realm().getIndex();
        }
        Table table = realm.getTable(com.kg.gettransfer.data.model.Offer.class);
        long tableNativePtr = table.getNativePtr();
        OfferColumnInfo columnInfo = (OfferColumnInfo) realm.getSchema().getColumnInfo(com.kg.gettransfer.data.model.Offer.class);
        long pkColumnIndex = columnInfo.idIndex;
        long rowIndex = Table.NO_MATCH;
        Object primaryKeyValue = ((com_kg_gettransfer_data_model_OfferRealmProxyInterface) object).realmGet$id();
        if (primaryKeyValue != null) {
            rowIndex = Table.nativeFindFirstInt(tableNativePtr, pkColumnIndex, ((com_kg_gettransfer_data_model_OfferRealmProxyInterface) object).realmGet$id());
        }
        if (rowIndex == Table.NO_MATCH) {
            rowIndex = OsObject.createRowWithPrimaryKey(table, pkColumnIndex, ((com_kg_gettransfer_data_model_OfferRealmProxyInterface) object).realmGet$id());
        } else {
            Table.throwDuplicatePrimaryKeyException(primaryKeyValue);
        }
        cache.put(object, rowIndex);

        com.kg.gettransfer.data.model.secondary.Price priceObj = ((com_kg_gettransfer_data_model_OfferRealmProxyInterface) object).realmGet$price();
        if (priceObj != null) {
            Long cacheprice = cache.get(priceObj);
            if (cacheprice == null) {
                cacheprice = com_kg_gettransfer_data_model_secondary_PriceRealmProxy.insert(realm, priceObj, cache);
            }
            Table.nativeSetLink(tableNativePtr, columnInfo.priceIndex, rowIndex, cacheprice, false);
        }
        String realmGet$status = ((com_kg_gettransfer_data_model_OfferRealmProxyInterface) object).realmGet$status();
        if (realmGet$status != null) {
            Table.nativeSetString(tableNativePtr, columnInfo.statusIndex, rowIndex, realmGet$status, false);
        }

        com.kg.gettransfer.data.model.Carrier carrierObj = ((com_kg_gettransfer_data_model_OfferRealmProxyInterface) object).realmGet$carrier();
        if (carrierObj != null) {
            Long cachecarrier = cache.get(carrierObj);
            if (cachecarrier == null) {
                cachecarrier = com_kg_gettransfer_data_model_CarrierRealmProxy.insert(realm, carrierObj, cache);
            }
            Table.nativeSetLink(tableNativePtr, columnInfo.carrierIndex, rowIndex, cachecarrier, false);
        }

        com.kg.gettransfer.data.model.Vehicle vehicleObj = ((com_kg_gettransfer_data_model_OfferRealmProxyInterface) object).realmGet$vehicle();
        if (vehicleObj != null) {
            Long cachevehicle = cache.get(vehicleObj);
            if (cachevehicle == null) {
                cachevehicle = com_kg_gettransfer_data_model_VehicleRealmProxy.insert(realm, vehicleObj, cache);
            }
            Table.nativeSetLink(tableNativePtr, columnInfo.vehicleIndex, rowIndex, cachevehicle, false);
        }
        Boolean realmGet$wifi = ((com_kg_gettransfer_data_model_OfferRealmProxyInterface) object).realmGet$wifi();
        if (realmGet$wifi != null) {
            Table.nativeSetBoolean(tableNativePtr, columnInfo.wifiIndex, rowIndex, realmGet$wifi, false);
        }
        Boolean realmGet$refreshments = ((com_kg_gettransfer_data_model_OfferRealmProxyInterface) object).realmGet$refreshments();
        if (realmGet$refreshments != null) {
            Table.nativeSetBoolean(tableNativePtr, columnInfo.refreshmentsIndex, rowIndex, realmGet$refreshments, false);
        }
        return rowIndex;
    }

    public static void insert(Realm realm, Iterator<? extends RealmModel> objects, Map<RealmModel,Long> cache) {
        Table table = realm.getTable(com.kg.gettransfer.data.model.Offer.class);
        long tableNativePtr = table.getNativePtr();
        OfferColumnInfo columnInfo = (OfferColumnInfo) realm.getSchema().getColumnInfo(com.kg.gettransfer.data.model.Offer.class);
        long pkColumnIndex = columnInfo.idIndex;
        com.kg.gettransfer.data.model.Offer object = null;
        while (objects.hasNext()) {
            object = (com.kg.gettransfer.data.model.Offer) objects.next();
            if (cache.containsKey(object)) {
                continue;
            }
            if (object instanceof RealmObjectProxy && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm() != null && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm().getPath().equals(realm.getPath())) {
                cache.put(object, ((RealmObjectProxy) object).realmGet$proxyState().getRow$realm().getIndex());
                continue;
            }
            long rowIndex = Table.NO_MATCH;
            Object primaryKeyValue = ((com_kg_gettransfer_data_model_OfferRealmProxyInterface) object).realmGet$id();
            if (primaryKeyValue != null) {
                rowIndex = Table.nativeFindFirstInt(tableNativePtr, pkColumnIndex, ((com_kg_gettransfer_data_model_OfferRealmProxyInterface) object).realmGet$id());
            }
            if (rowIndex == Table.NO_MATCH) {
                rowIndex = OsObject.createRowWithPrimaryKey(table, pkColumnIndex, ((com_kg_gettransfer_data_model_OfferRealmProxyInterface) object).realmGet$id());
            } else {
                Table.throwDuplicatePrimaryKeyException(primaryKeyValue);
            }
            cache.put(object, rowIndex);

            com.kg.gettransfer.data.model.secondary.Price priceObj = ((com_kg_gettransfer_data_model_OfferRealmProxyInterface) object).realmGet$price();
            if (priceObj != null) {
                Long cacheprice = cache.get(priceObj);
                if (cacheprice == null) {
                    cacheprice = com_kg_gettransfer_data_model_secondary_PriceRealmProxy.insert(realm, priceObj, cache);
                }
                table.setLink(columnInfo.priceIndex, rowIndex, cacheprice, false);
            }
            String realmGet$status = ((com_kg_gettransfer_data_model_OfferRealmProxyInterface) object).realmGet$status();
            if (realmGet$status != null) {
                Table.nativeSetString(tableNativePtr, columnInfo.statusIndex, rowIndex, realmGet$status, false);
            }

            com.kg.gettransfer.data.model.Carrier carrierObj = ((com_kg_gettransfer_data_model_OfferRealmProxyInterface) object).realmGet$carrier();
            if (carrierObj != null) {
                Long cachecarrier = cache.get(carrierObj);
                if (cachecarrier == null) {
                    cachecarrier = com_kg_gettransfer_data_model_CarrierRealmProxy.insert(realm, carrierObj, cache);
                }
                table.setLink(columnInfo.carrierIndex, rowIndex, cachecarrier, false);
            }

            com.kg.gettransfer.data.model.Vehicle vehicleObj = ((com_kg_gettransfer_data_model_OfferRealmProxyInterface) object).realmGet$vehicle();
            if (vehicleObj != null) {
                Long cachevehicle = cache.get(vehicleObj);
                if (cachevehicle == null) {
                    cachevehicle = com_kg_gettransfer_data_model_VehicleRealmProxy.insert(realm, vehicleObj, cache);
                }
                table.setLink(columnInfo.vehicleIndex, rowIndex, cachevehicle, false);
            }
            Boolean realmGet$wifi = ((com_kg_gettransfer_data_model_OfferRealmProxyInterface) object).realmGet$wifi();
            if (realmGet$wifi != null) {
                Table.nativeSetBoolean(tableNativePtr, columnInfo.wifiIndex, rowIndex, realmGet$wifi, false);
            }
            Boolean realmGet$refreshments = ((com_kg_gettransfer_data_model_OfferRealmProxyInterface) object).realmGet$refreshments();
            if (realmGet$refreshments != null) {
                Table.nativeSetBoolean(tableNativePtr, columnInfo.refreshmentsIndex, rowIndex, realmGet$refreshments, false);
            }
        }
    }

    public static long insertOrUpdate(Realm realm, com.kg.gettransfer.data.model.Offer object, Map<RealmModel,Long> cache) {
        if (object instanceof RealmObjectProxy && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm() != null && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm().getPath().equals(realm.getPath())) {
            return ((RealmObjectProxy) object).realmGet$proxyState().getRow$realm().getIndex();
        }
        Table table = realm.getTable(com.kg.gettransfer.data.model.Offer.class);
        long tableNativePtr = table.getNativePtr();
        OfferColumnInfo columnInfo = (OfferColumnInfo) realm.getSchema().getColumnInfo(com.kg.gettransfer.data.model.Offer.class);
        long pkColumnIndex = columnInfo.idIndex;
        long rowIndex = Table.NO_MATCH;
        Object primaryKeyValue = ((com_kg_gettransfer_data_model_OfferRealmProxyInterface) object).realmGet$id();
        if (primaryKeyValue != null) {
            rowIndex = Table.nativeFindFirstInt(tableNativePtr, pkColumnIndex, ((com_kg_gettransfer_data_model_OfferRealmProxyInterface) object).realmGet$id());
        }
        if (rowIndex == Table.NO_MATCH) {
            rowIndex = OsObject.createRowWithPrimaryKey(table, pkColumnIndex, ((com_kg_gettransfer_data_model_OfferRealmProxyInterface) object).realmGet$id());
        }
        cache.put(object, rowIndex);

        com.kg.gettransfer.data.model.secondary.Price priceObj = ((com_kg_gettransfer_data_model_OfferRealmProxyInterface) object).realmGet$price();
        if (priceObj != null) {
            Long cacheprice = cache.get(priceObj);
            if (cacheprice == null) {
                cacheprice = com_kg_gettransfer_data_model_secondary_PriceRealmProxy.insertOrUpdate(realm, priceObj, cache);
            }
            Table.nativeSetLink(tableNativePtr, columnInfo.priceIndex, rowIndex, cacheprice, false);
        } else {
            Table.nativeNullifyLink(tableNativePtr, columnInfo.priceIndex, rowIndex);
        }
        String realmGet$status = ((com_kg_gettransfer_data_model_OfferRealmProxyInterface) object).realmGet$status();
        if (realmGet$status != null) {
            Table.nativeSetString(tableNativePtr, columnInfo.statusIndex, rowIndex, realmGet$status, false);
        } else {
            Table.nativeSetNull(tableNativePtr, columnInfo.statusIndex, rowIndex, false);
        }

        com.kg.gettransfer.data.model.Carrier carrierObj = ((com_kg_gettransfer_data_model_OfferRealmProxyInterface) object).realmGet$carrier();
        if (carrierObj != null) {
            Long cachecarrier = cache.get(carrierObj);
            if (cachecarrier == null) {
                cachecarrier = com_kg_gettransfer_data_model_CarrierRealmProxy.insertOrUpdate(realm, carrierObj, cache);
            }
            Table.nativeSetLink(tableNativePtr, columnInfo.carrierIndex, rowIndex, cachecarrier, false);
        } else {
            Table.nativeNullifyLink(tableNativePtr, columnInfo.carrierIndex, rowIndex);
        }

        com.kg.gettransfer.data.model.Vehicle vehicleObj = ((com_kg_gettransfer_data_model_OfferRealmProxyInterface) object).realmGet$vehicle();
        if (vehicleObj != null) {
            Long cachevehicle = cache.get(vehicleObj);
            if (cachevehicle == null) {
                cachevehicle = com_kg_gettransfer_data_model_VehicleRealmProxy.insertOrUpdate(realm, vehicleObj, cache);
            }
            Table.nativeSetLink(tableNativePtr, columnInfo.vehicleIndex, rowIndex, cachevehicle, false);
        } else {
            Table.nativeNullifyLink(tableNativePtr, columnInfo.vehicleIndex, rowIndex);
        }
        Boolean realmGet$wifi = ((com_kg_gettransfer_data_model_OfferRealmProxyInterface) object).realmGet$wifi();
        if (realmGet$wifi != null) {
            Table.nativeSetBoolean(tableNativePtr, columnInfo.wifiIndex, rowIndex, realmGet$wifi, false);
        } else {
            Table.nativeSetNull(tableNativePtr, columnInfo.wifiIndex, rowIndex, false);
        }
        Boolean realmGet$refreshments = ((com_kg_gettransfer_data_model_OfferRealmProxyInterface) object).realmGet$refreshments();
        if (realmGet$refreshments != null) {
            Table.nativeSetBoolean(tableNativePtr, columnInfo.refreshmentsIndex, rowIndex, realmGet$refreshments, false);
        } else {
            Table.nativeSetNull(tableNativePtr, columnInfo.refreshmentsIndex, rowIndex, false);
        }
        return rowIndex;
    }

    public static void insertOrUpdate(Realm realm, Iterator<? extends RealmModel> objects, Map<RealmModel,Long> cache) {
        Table table = realm.getTable(com.kg.gettransfer.data.model.Offer.class);
        long tableNativePtr = table.getNativePtr();
        OfferColumnInfo columnInfo = (OfferColumnInfo) realm.getSchema().getColumnInfo(com.kg.gettransfer.data.model.Offer.class);
        long pkColumnIndex = columnInfo.idIndex;
        com.kg.gettransfer.data.model.Offer object = null;
        while (objects.hasNext()) {
            object = (com.kg.gettransfer.data.model.Offer) objects.next();
            if (cache.containsKey(object)) {
                continue;
            }
            if (object instanceof RealmObjectProxy && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm() != null && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm().getPath().equals(realm.getPath())) {
                cache.put(object, ((RealmObjectProxy) object).realmGet$proxyState().getRow$realm().getIndex());
                continue;
            }
            long rowIndex = Table.NO_MATCH;
            Object primaryKeyValue = ((com_kg_gettransfer_data_model_OfferRealmProxyInterface) object).realmGet$id();
            if (primaryKeyValue != null) {
                rowIndex = Table.nativeFindFirstInt(tableNativePtr, pkColumnIndex, ((com_kg_gettransfer_data_model_OfferRealmProxyInterface) object).realmGet$id());
            }
            if (rowIndex == Table.NO_MATCH) {
                rowIndex = OsObject.createRowWithPrimaryKey(table, pkColumnIndex, ((com_kg_gettransfer_data_model_OfferRealmProxyInterface) object).realmGet$id());
            }
            cache.put(object, rowIndex);

            com.kg.gettransfer.data.model.secondary.Price priceObj = ((com_kg_gettransfer_data_model_OfferRealmProxyInterface) object).realmGet$price();
            if (priceObj != null) {
                Long cacheprice = cache.get(priceObj);
                if (cacheprice == null) {
                    cacheprice = com_kg_gettransfer_data_model_secondary_PriceRealmProxy.insertOrUpdate(realm, priceObj, cache);
                }
                Table.nativeSetLink(tableNativePtr, columnInfo.priceIndex, rowIndex, cacheprice, false);
            } else {
                Table.nativeNullifyLink(tableNativePtr, columnInfo.priceIndex, rowIndex);
            }
            String realmGet$status = ((com_kg_gettransfer_data_model_OfferRealmProxyInterface) object).realmGet$status();
            if (realmGet$status != null) {
                Table.nativeSetString(tableNativePtr, columnInfo.statusIndex, rowIndex, realmGet$status, false);
            } else {
                Table.nativeSetNull(tableNativePtr, columnInfo.statusIndex, rowIndex, false);
            }

            com.kg.gettransfer.data.model.Carrier carrierObj = ((com_kg_gettransfer_data_model_OfferRealmProxyInterface) object).realmGet$carrier();
            if (carrierObj != null) {
                Long cachecarrier = cache.get(carrierObj);
                if (cachecarrier == null) {
                    cachecarrier = com_kg_gettransfer_data_model_CarrierRealmProxy.insertOrUpdate(realm, carrierObj, cache);
                }
                Table.nativeSetLink(tableNativePtr, columnInfo.carrierIndex, rowIndex, cachecarrier, false);
            } else {
                Table.nativeNullifyLink(tableNativePtr, columnInfo.carrierIndex, rowIndex);
            }

            com.kg.gettransfer.data.model.Vehicle vehicleObj = ((com_kg_gettransfer_data_model_OfferRealmProxyInterface) object).realmGet$vehicle();
            if (vehicleObj != null) {
                Long cachevehicle = cache.get(vehicleObj);
                if (cachevehicle == null) {
                    cachevehicle = com_kg_gettransfer_data_model_VehicleRealmProxy.insertOrUpdate(realm, vehicleObj, cache);
                }
                Table.nativeSetLink(tableNativePtr, columnInfo.vehicleIndex, rowIndex, cachevehicle, false);
            } else {
                Table.nativeNullifyLink(tableNativePtr, columnInfo.vehicleIndex, rowIndex);
            }
            Boolean realmGet$wifi = ((com_kg_gettransfer_data_model_OfferRealmProxyInterface) object).realmGet$wifi();
            if (realmGet$wifi != null) {
                Table.nativeSetBoolean(tableNativePtr, columnInfo.wifiIndex, rowIndex, realmGet$wifi, false);
            } else {
                Table.nativeSetNull(tableNativePtr, columnInfo.wifiIndex, rowIndex, false);
            }
            Boolean realmGet$refreshments = ((com_kg_gettransfer_data_model_OfferRealmProxyInterface) object).realmGet$refreshments();
            if (realmGet$refreshments != null) {
                Table.nativeSetBoolean(tableNativePtr, columnInfo.refreshmentsIndex, rowIndex, realmGet$refreshments, false);
            } else {
                Table.nativeSetNull(tableNativePtr, columnInfo.refreshmentsIndex, rowIndex, false);
            }
        }
    }

    public static com.kg.gettransfer.data.model.Offer createDetachedCopy(com.kg.gettransfer.data.model.Offer realmObject, int currentDepth, int maxDepth, Map<RealmModel, CacheData<RealmModel>> cache) {
        if (currentDepth > maxDepth || realmObject == null) {
            return null;
        }
        CacheData<RealmModel> cachedObject = cache.get(realmObject);
        com.kg.gettransfer.data.model.Offer unmanagedObject;
        if (cachedObject == null) {
            unmanagedObject = new com.kg.gettransfer.data.model.Offer();
            cache.put(realmObject, new RealmObjectProxy.CacheData<RealmModel>(currentDepth, unmanagedObject));
        } else {
            // Reuse cached object or recreate it because it was encountered at a lower depth.
            if (currentDepth >= cachedObject.minDepth) {
                return (com.kg.gettransfer.data.model.Offer) cachedObject.object;
            }
            unmanagedObject = (com.kg.gettransfer.data.model.Offer) cachedObject.object;
            cachedObject.minDepth = currentDepth;
        }
        com_kg_gettransfer_data_model_OfferRealmProxyInterface unmanagedCopy = (com_kg_gettransfer_data_model_OfferRealmProxyInterface) unmanagedObject;
        com_kg_gettransfer_data_model_OfferRealmProxyInterface realmSource = (com_kg_gettransfer_data_model_OfferRealmProxyInterface) realmObject;
        unmanagedCopy.realmSet$id(realmSource.realmGet$id());

        // Deep copy of price
        unmanagedCopy.realmSet$price(com_kg_gettransfer_data_model_secondary_PriceRealmProxy.createDetachedCopy(realmSource.realmGet$price(), currentDepth + 1, maxDepth, cache));
        unmanagedCopy.realmSet$status(realmSource.realmGet$status());

        // Deep copy of carrier
        unmanagedCopy.realmSet$carrier(com_kg_gettransfer_data_model_CarrierRealmProxy.createDetachedCopy(realmSource.realmGet$carrier(), currentDepth + 1, maxDepth, cache));

        // Deep copy of vehicle
        unmanagedCopy.realmSet$vehicle(com_kg_gettransfer_data_model_VehicleRealmProxy.createDetachedCopy(realmSource.realmGet$vehicle(), currentDepth + 1, maxDepth, cache));
        unmanagedCopy.realmSet$wifi(realmSource.realmGet$wifi());
        unmanagedCopy.realmSet$refreshments(realmSource.realmGet$refreshments());

        return unmanagedObject;
    }

    static com.kg.gettransfer.data.model.Offer update(Realm realm, com.kg.gettransfer.data.model.Offer realmObject, com.kg.gettransfer.data.model.Offer newObject, Map<RealmModel, RealmObjectProxy> cache) {
        com_kg_gettransfer_data_model_OfferRealmProxyInterface realmObjectTarget = (com_kg_gettransfer_data_model_OfferRealmProxyInterface) realmObject;
        com_kg_gettransfer_data_model_OfferRealmProxyInterface realmObjectSource = (com_kg_gettransfer_data_model_OfferRealmProxyInterface) newObject;
        com.kg.gettransfer.data.model.secondary.Price priceObj = realmObjectSource.realmGet$price();
        if (priceObj == null) {
            realmObjectTarget.realmSet$price(null);
        } else {
            com.kg.gettransfer.data.model.secondary.Price cacheprice = (com.kg.gettransfer.data.model.secondary.Price) cache.get(priceObj);
            if (cacheprice != null) {
                realmObjectTarget.realmSet$price(cacheprice);
            } else {
                realmObjectTarget.realmSet$price(com_kg_gettransfer_data_model_secondary_PriceRealmProxy.copyOrUpdate(realm, priceObj, true, cache));
            }
        }
        realmObjectTarget.realmSet$status(realmObjectSource.realmGet$status());
        com.kg.gettransfer.data.model.Carrier carrierObj = realmObjectSource.realmGet$carrier();
        if (carrierObj == null) {
            realmObjectTarget.realmSet$carrier(null);
        } else {
            com.kg.gettransfer.data.model.Carrier cachecarrier = (com.kg.gettransfer.data.model.Carrier) cache.get(carrierObj);
            if (cachecarrier != null) {
                realmObjectTarget.realmSet$carrier(cachecarrier);
            } else {
                realmObjectTarget.realmSet$carrier(com_kg_gettransfer_data_model_CarrierRealmProxy.copyOrUpdate(realm, carrierObj, true, cache));
            }
        }
        com.kg.gettransfer.data.model.Vehicle vehicleObj = realmObjectSource.realmGet$vehicle();
        if (vehicleObj == null) {
            realmObjectTarget.realmSet$vehicle(null);
        } else {
            com.kg.gettransfer.data.model.Vehicle cachevehicle = (com.kg.gettransfer.data.model.Vehicle) cache.get(vehicleObj);
            if (cachevehicle != null) {
                realmObjectTarget.realmSet$vehicle(cachevehicle);
            } else {
                realmObjectTarget.realmSet$vehicle(com_kg_gettransfer_data_model_VehicleRealmProxy.copyOrUpdate(realm, vehicleObj, true, cache));
            }
        }
        realmObjectTarget.realmSet$wifi(realmObjectSource.realmGet$wifi());
        realmObjectTarget.realmSet$refreshments(realmObjectSource.realmGet$refreshments());
        return realmObject;
    }

    @Override
    @SuppressWarnings("ArrayToString")
    public String toString() {
        if (!RealmObject.isValid(this)) {
            return "Invalid object";
        }
        StringBuilder stringBuilder = new StringBuilder("Offer = proxy[");
        stringBuilder.append("{id:");
        stringBuilder.append(realmGet$id());
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{price:");
        stringBuilder.append(realmGet$price() != null ? "Price" : "null");
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{status:");
        stringBuilder.append(realmGet$status() != null ? realmGet$status() : "null");
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{carrier:");
        stringBuilder.append(realmGet$carrier() != null ? "Carrier" : "null");
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{vehicle:");
        stringBuilder.append(realmGet$vehicle() != null ? "Vehicle" : "null");
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{wifi:");
        stringBuilder.append(realmGet$wifi() != null ? realmGet$wifi() : "null");
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{refreshments:");
        stringBuilder.append(realmGet$refreshments() != null ? realmGet$refreshments() : "null");
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
        com_kg_gettransfer_data_model_OfferRealmProxy aOffer = (com_kg_gettransfer_data_model_OfferRealmProxy)o;

        String path = proxyState.getRealm$realm().getPath();
        String otherPath = aOffer.proxyState.getRealm$realm().getPath();
        if (path != null ? !path.equals(otherPath) : otherPath != null) return false;

        String tableName = proxyState.getRow$realm().getTable().getName();
        String otherTableName = aOffer.proxyState.getRow$realm().getTable().getName();
        if (tableName != null ? !tableName.equals(otherTableName) : otherTableName != null) return false;

        if (proxyState.getRow$realm().getIndex() != aOffer.proxyState.getRow$realm().getIndex()) return false;

        return true;
    }
}
