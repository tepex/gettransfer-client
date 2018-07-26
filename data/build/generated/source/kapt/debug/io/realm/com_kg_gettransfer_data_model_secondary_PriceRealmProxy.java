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
public class com_kg_gettransfer_data_model_secondary_PriceRealmProxy extends com.kg.gettransfer.data.model.secondary.Price
    implements RealmObjectProxy, com_kg_gettransfer_data_model_secondary_PriceRealmProxyInterface {

    static final class PriceColumnInfo extends ColumnInfo {
        long baseIndex;
        long p30Index;
        long p70Index;
        long withoutDiscountIndex;
        long amountIndex;

        PriceColumnInfo(OsSchemaInfo schemaInfo) {
            super(5);
            OsObjectSchemaInfo objectSchemaInfo = schemaInfo.getObjectSchemaInfo("Price");
            this.baseIndex = addColumnDetails("base", "base", objectSchemaInfo);
            this.p30Index = addColumnDetails("p30", "p30", objectSchemaInfo);
            this.p70Index = addColumnDetails("p70", "p70", objectSchemaInfo);
            this.withoutDiscountIndex = addColumnDetails("withoutDiscount", "withoutDiscount", objectSchemaInfo);
            this.amountIndex = addColumnDetails("amount", "amount", objectSchemaInfo);
        }

        PriceColumnInfo(ColumnInfo src, boolean mutable) {
            super(src, mutable);
            copy(src, this);
        }

        @Override
        protected final ColumnInfo copy(boolean mutable) {
            return new PriceColumnInfo(this, mutable);
        }

        @Override
        protected final void copy(ColumnInfo rawSrc, ColumnInfo rawDst) {
            final PriceColumnInfo src = (PriceColumnInfo) rawSrc;
            final PriceColumnInfo dst = (PriceColumnInfo) rawDst;
            dst.baseIndex = src.baseIndex;
            dst.p30Index = src.p30Index;
            dst.p70Index = src.p70Index;
            dst.withoutDiscountIndex = src.withoutDiscountIndex;
            dst.amountIndex = src.amountIndex;
        }
    }

    private static final OsObjectSchemaInfo expectedObjectSchemaInfo = createExpectedObjectSchemaInfo();

    private PriceColumnInfo columnInfo;
    private ProxyState<com.kg.gettransfer.data.model.secondary.Price> proxyState;

    com_kg_gettransfer_data_model_secondary_PriceRealmProxy() {
        proxyState.setConstructionFinished();
    }

    @Override
    public void realm$injectObjectContext() {
        if (this.proxyState != null) {
            return;
        }
        final BaseRealm.RealmObjectContext context = BaseRealm.objectContext.get();
        this.columnInfo = (PriceColumnInfo) context.getColumnInfo();
        this.proxyState = new ProxyState<com.kg.gettransfer.data.model.secondary.Price>(this);
        proxyState.setRealm$realm(context.getRealm());
        proxyState.setRow$realm(context.getRow());
        proxyState.setAcceptDefaultValue$realm(context.getAcceptDefaultValue());
        proxyState.setExcludeFields$realm(context.getExcludeFields());
    }

    @Override
    public com.kg.gettransfer.data.model.secondary.PriceConverted realmGet$base() {
        proxyState.getRealm$realm().checkIfValid();
        if (proxyState.getRow$realm().isNullLink(columnInfo.baseIndex)) {
            return null;
        }
        return proxyState.getRealm$realm().get(com.kg.gettransfer.data.model.secondary.PriceConverted.class, proxyState.getRow$realm().getLink(columnInfo.baseIndex), false, Collections.<String>emptyList());
    }

    @Override
    public void realmSet$base(com.kg.gettransfer.data.model.secondary.PriceConverted value) {
        if (proxyState.isUnderConstruction()) {
            if (!proxyState.getAcceptDefaultValue$realm()) {
                return;
            }
            if (proxyState.getExcludeFields$realm().contains("base")) {
                return;
            }
            if (value != null && !RealmObject.isManaged(value)) {
                value = ((Realm) proxyState.getRealm$realm()).copyToRealm(value);
            }
            final Row row = proxyState.getRow$realm();
            if (value == null) {
                // Table#nullifyLink() does not support default value. Just using Row.
                row.nullifyLink(columnInfo.baseIndex);
                return;
            }
            proxyState.checkValidObject(value);
            row.getTable().setLink(columnInfo.baseIndex, row.getIndex(), ((RealmObjectProxy) value).realmGet$proxyState().getRow$realm().getIndex(), true);
            return;
        }

        proxyState.getRealm$realm().checkIfValid();
        if (value == null) {
            proxyState.getRow$realm().nullifyLink(columnInfo.baseIndex);
            return;
        }
        proxyState.checkValidObject(value);
        proxyState.getRow$realm().setLink(columnInfo.baseIndex, ((RealmObjectProxy) value).realmGet$proxyState().getRow$realm().getIndex());
    }

    @Override
    @SuppressWarnings("cast")
    public String realmGet$p30() {
        proxyState.getRealm$realm().checkIfValid();
        return (java.lang.String) proxyState.getRow$realm().getString(columnInfo.p30Index);
    }

    @Override
    public void realmSet$p30(String value) {
        if (proxyState.isUnderConstruction()) {
            if (!proxyState.getAcceptDefaultValue$realm()) {
                return;
            }
            final Row row = proxyState.getRow$realm();
            if (value == null) {
                row.getTable().setNull(columnInfo.p30Index, row.getIndex(), true);
                return;
            }
            row.getTable().setString(columnInfo.p30Index, row.getIndex(), value, true);
            return;
        }

        proxyState.getRealm$realm().checkIfValid();
        if (value == null) {
            proxyState.getRow$realm().setNull(columnInfo.p30Index);
            return;
        }
        proxyState.getRow$realm().setString(columnInfo.p30Index, value);
    }

    @Override
    @SuppressWarnings("cast")
    public String realmGet$p70() {
        proxyState.getRealm$realm().checkIfValid();
        return (java.lang.String) proxyState.getRow$realm().getString(columnInfo.p70Index);
    }

    @Override
    public void realmSet$p70(String value) {
        if (proxyState.isUnderConstruction()) {
            if (!proxyState.getAcceptDefaultValue$realm()) {
                return;
            }
            final Row row = proxyState.getRow$realm();
            if (value == null) {
                row.getTable().setNull(columnInfo.p70Index, row.getIndex(), true);
                return;
            }
            row.getTable().setString(columnInfo.p70Index, row.getIndex(), value, true);
            return;
        }

        proxyState.getRealm$realm().checkIfValid();
        if (value == null) {
            proxyState.getRow$realm().setNull(columnInfo.p70Index);
            return;
        }
        proxyState.getRow$realm().setString(columnInfo.p70Index, value);
    }

    @Override
    public com.kg.gettransfer.data.model.secondary.PriceConverted realmGet$withoutDiscount() {
        proxyState.getRealm$realm().checkIfValid();
        if (proxyState.getRow$realm().isNullLink(columnInfo.withoutDiscountIndex)) {
            return null;
        }
        return proxyState.getRealm$realm().get(com.kg.gettransfer.data.model.secondary.PriceConverted.class, proxyState.getRow$realm().getLink(columnInfo.withoutDiscountIndex), false, Collections.<String>emptyList());
    }

    @Override
    public void realmSet$withoutDiscount(com.kg.gettransfer.data.model.secondary.PriceConverted value) {
        if (proxyState.isUnderConstruction()) {
            if (!proxyState.getAcceptDefaultValue$realm()) {
                return;
            }
            if (proxyState.getExcludeFields$realm().contains("withoutDiscount")) {
                return;
            }
            if (value != null && !RealmObject.isManaged(value)) {
                value = ((Realm) proxyState.getRealm$realm()).copyToRealm(value);
            }
            final Row row = proxyState.getRow$realm();
            if (value == null) {
                // Table#nullifyLink() does not support default value. Just using Row.
                row.nullifyLink(columnInfo.withoutDiscountIndex);
                return;
            }
            proxyState.checkValidObject(value);
            row.getTable().setLink(columnInfo.withoutDiscountIndex, row.getIndex(), ((RealmObjectProxy) value).realmGet$proxyState().getRow$realm().getIndex(), true);
            return;
        }

        proxyState.getRealm$realm().checkIfValid();
        if (value == null) {
            proxyState.getRow$realm().nullifyLink(columnInfo.withoutDiscountIndex);
            return;
        }
        proxyState.checkValidObject(value);
        proxyState.getRow$realm().setLink(columnInfo.withoutDiscountIndex, ((RealmObjectProxy) value).realmGet$proxyState().getRow$realm().getIndex());
    }

    @Override
    @SuppressWarnings("cast")
    public double realmGet$amount() {
        proxyState.getRealm$realm().checkIfValid();
        return (double) proxyState.getRow$realm().getDouble(columnInfo.amountIndex);
    }

    @Override
    public void realmSet$amount(double value) {
        if (proxyState.isUnderConstruction()) {
            if (!proxyState.getAcceptDefaultValue$realm()) {
                return;
            }
            final Row row = proxyState.getRow$realm();
            row.getTable().setDouble(columnInfo.amountIndex, row.getIndex(), value, true);
            return;
        }

        proxyState.getRealm$realm().checkIfValid();
        proxyState.getRow$realm().setDouble(columnInfo.amountIndex, value);
    }

    private static OsObjectSchemaInfo createExpectedObjectSchemaInfo() {
        OsObjectSchemaInfo.Builder builder = new OsObjectSchemaInfo.Builder("Price", 5, 0);
        builder.addPersistedLinkProperty("base", RealmFieldType.OBJECT, "PriceConverted");
        builder.addPersistedProperty("p30", RealmFieldType.STRING, !Property.PRIMARY_KEY, !Property.INDEXED, !Property.REQUIRED);
        builder.addPersistedProperty("p70", RealmFieldType.STRING, !Property.PRIMARY_KEY, !Property.INDEXED, !Property.REQUIRED);
        builder.addPersistedLinkProperty("withoutDiscount", RealmFieldType.OBJECT, "PriceConverted");
        builder.addPersistedProperty("amount", RealmFieldType.DOUBLE, !Property.PRIMARY_KEY, !Property.INDEXED, Property.REQUIRED);
        return builder.build();
    }

    public static OsObjectSchemaInfo getExpectedObjectSchemaInfo() {
        return expectedObjectSchemaInfo;
    }

    public static PriceColumnInfo createColumnInfo(OsSchemaInfo schemaInfo) {
        return new PriceColumnInfo(schemaInfo);
    }

    public static String getSimpleClassName() {
        return "Price";
    }

    public static final class ClassNameHelper {
        public static final String INTERNAL_CLASS_NAME = "Price";
    }

    @SuppressWarnings("cast")
    public static com.kg.gettransfer.data.model.secondary.Price createOrUpdateUsingJsonObject(Realm realm, JSONObject json, boolean update)
        throws JSONException {
        final List<String> excludeFields = new ArrayList<String>(2);
        if (json.has("base")) {
            excludeFields.add("base");
        }
        if (json.has("withoutDiscount")) {
            excludeFields.add("withoutDiscount");
        }
        com.kg.gettransfer.data.model.secondary.Price obj = realm.createObjectInternal(com.kg.gettransfer.data.model.secondary.Price.class, true, excludeFields);

        final com_kg_gettransfer_data_model_secondary_PriceRealmProxyInterface objProxy = (com_kg_gettransfer_data_model_secondary_PriceRealmProxyInterface) obj;
        if (json.has("base")) {
            if (json.isNull("base")) {
                objProxy.realmSet$base(null);
            } else {
                com.kg.gettransfer.data.model.secondary.PriceConverted baseObj = com_kg_gettransfer_data_model_secondary_PriceConvertedRealmProxy.createOrUpdateUsingJsonObject(realm, json.getJSONObject("base"), update);
                objProxy.realmSet$base(baseObj);
            }
        }
        if (json.has("p30")) {
            if (json.isNull("p30")) {
                objProxy.realmSet$p30(null);
            } else {
                objProxy.realmSet$p30((String) json.getString("p30"));
            }
        }
        if (json.has("p70")) {
            if (json.isNull("p70")) {
                objProxy.realmSet$p70(null);
            } else {
                objProxy.realmSet$p70((String) json.getString("p70"));
            }
        }
        if (json.has("withoutDiscount")) {
            if (json.isNull("withoutDiscount")) {
                objProxy.realmSet$withoutDiscount(null);
            } else {
                com.kg.gettransfer.data.model.secondary.PriceConverted withoutDiscountObj = com_kg_gettransfer_data_model_secondary_PriceConvertedRealmProxy.createOrUpdateUsingJsonObject(realm, json.getJSONObject("withoutDiscount"), update);
                objProxy.realmSet$withoutDiscount(withoutDiscountObj);
            }
        }
        if (json.has("amount")) {
            if (json.isNull("amount")) {
                throw new IllegalArgumentException("Trying to set non-nullable field 'amount' to null.");
            } else {
                objProxy.realmSet$amount((double) json.getDouble("amount"));
            }
        }
        return obj;
    }

    @SuppressWarnings("cast")
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static com.kg.gettransfer.data.model.secondary.Price createUsingJsonStream(Realm realm, JsonReader reader)
        throws IOException {
        final com.kg.gettransfer.data.model.secondary.Price obj = new com.kg.gettransfer.data.model.secondary.Price();
        final com_kg_gettransfer_data_model_secondary_PriceRealmProxyInterface objProxy = (com_kg_gettransfer_data_model_secondary_PriceRealmProxyInterface) obj;
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (false) {
            } else if (name.equals("base")) {
                if (reader.peek() == JsonToken.NULL) {
                    reader.skipValue();
                    objProxy.realmSet$base(null);
                } else {
                    com.kg.gettransfer.data.model.secondary.PriceConverted baseObj = com_kg_gettransfer_data_model_secondary_PriceConvertedRealmProxy.createUsingJsonStream(realm, reader);
                    objProxy.realmSet$base(baseObj);
                }
            } else if (name.equals("p30")) {
                if (reader.peek() != JsonToken.NULL) {
                    objProxy.realmSet$p30((String) reader.nextString());
                } else {
                    reader.skipValue();
                    objProxy.realmSet$p30(null);
                }
            } else if (name.equals("p70")) {
                if (reader.peek() != JsonToken.NULL) {
                    objProxy.realmSet$p70((String) reader.nextString());
                } else {
                    reader.skipValue();
                    objProxy.realmSet$p70(null);
                }
            } else if (name.equals("withoutDiscount")) {
                if (reader.peek() == JsonToken.NULL) {
                    reader.skipValue();
                    objProxy.realmSet$withoutDiscount(null);
                } else {
                    com.kg.gettransfer.data.model.secondary.PriceConverted withoutDiscountObj = com_kg_gettransfer_data_model_secondary_PriceConvertedRealmProxy.createUsingJsonStream(realm, reader);
                    objProxy.realmSet$withoutDiscount(withoutDiscountObj);
                }
            } else if (name.equals("amount")) {
                if (reader.peek() != JsonToken.NULL) {
                    objProxy.realmSet$amount((double) reader.nextDouble());
                } else {
                    reader.skipValue();
                    throw new IllegalArgumentException("Trying to set non-nullable field 'amount' to null.");
                }
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return realm.copyToRealm(obj);
    }

    public static com.kg.gettransfer.data.model.secondary.Price copyOrUpdate(Realm realm, com.kg.gettransfer.data.model.secondary.Price object, boolean update, Map<RealmModel,RealmObjectProxy> cache) {
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
            return (com.kg.gettransfer.data.model.secondary.Price) cachedRealmObject;
        }

        return copy(realm, object, update, cache);
    }

    public static com.kg.gettransfer.data.model.secondary.Price copy(Realm realm, com.kg.gettransfer.data.model.secondary.Price newObject, boolean update, Map<RealmModel,RealmObjectProxy> cache) {
        RealmObjectProxy cachedRealmObject = cache.get(newObject);
        if (cachedRealmObject != null) {
            return (com.kg.gettransfer.data.model.secondary.Price) cachedRealmObject;
        }

        // rejecting default values to avoid creating unexpected objects from RealmModel/RealmList fields.
        com.kg.gettransfer.data.model.secondary.Price realmObject = realm.createObjectInternal(com.kg.gettransfer.data.model.secondary.Price.class, false, Collections.<String>emptyList());
        cache.put(newObject, (RealmObjectProxy) realmObject);

        com_kg_gettransfer_data_model_secondary_PriceRealmProxyInterface realmObjectSource = (com_kg_gettransfer_data_model_secondary_PriceRealmProxyInterface) newObject;
        com_kg_gettransfer_data_model_secondary_PriceRealmProxyInterface realmObjectCopy = (com_kg_gettransfer_data_model_secondary_PriceRealmProxyInterface) realmObject;


        com.kg.gettransfer.data.model.secondary.PriceConverted baseObj = realmObjectSource.realmGet$base();
        if (baseObj == null) {
            realmObjectCopy.realmSet$base(null);
        } else {
            com.kg.gettransfer.data.model.secondary.PriceConverted cachebase = (com.kg.gettransfer.data.model.secondary.PriceConverted) cache.get(baseObj);
            if (cachebase != null) {
                realmObjectCopy.realmSet$base(cachebase);
            } else {
                realmObjectCopy.realmSet$base(com_kg_gettransfer_data_model_secondary_PriceConvertedRealmProxy.copyOrUpdate(realm, baseObj, update, cache));
            }
        }
        realmObjectCopy.realmSet$p30(realmObjectSource.realmGet$p30());
        realmObjectCopy.realmSet$p70(realmObjectSource.realmGet$p70());

        com.kg.gettransfer.data.model.secondary.PriceConverted withoutDiscountObj = realmObjectSource.realmGet$withoutDiscount();
        if (withoutDiscountObj == null) {
            realmObjectCopy.realmSet$withoutDiscount(null);
        } else {
            com.kg.gettransfer.data.model.secondary.PriceConverted cachewithoutDiscount = (com.kg.gettransfer.data.model.secondary.PriceConverted) cache.get(withoutDiscountObj);
            if (cachewithoutDiscount != null) {
                realmObjectCopy.realmSet$withoutDiscount(cachewithoutDiscount);
            } else {
                realmObjectCopy.realmSet$withoutDiscount(com_kg_gettransfer_data_model_secondary_PriceConvertedRealmProxy.copyOrUpdate(realm, withoutDiscountObj, update, cache));
            }
        }
        realmObjectCopy.realmSet$amount(realmObjectSource.realmGet$amount());
        return realmObject;
    }

    public static long insert(Realm realm, com.kg.gettransfer.data.model.secondary.Price object, Map<RealmModel,Long> cache) {
        if (object instanceof RealmObjectProxy && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm() != null && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm().getPath().equals(realm.getPath())) {
            return ((RealmObjectProxy) object).realmGet$proxyState().getRow$realm().getIndex();
        }
        Table table = realm.getTable(com.kg.gettransfer.data.model.secondary.Price.class);
        long tableNativePtr = table.getNativePtr();
        PriceColumnInfo columnInfo = (PriceColumnInfo) realm.getSchema().getColumnInfo(com.kg.gettransfer.data.model.secondary.Price.class);
        long rowIndex = OsObject.createRow(table);
        cache.put(object, rowIndex);

        com.kg.gettransfer.data.model.secondary.PriceConverted baseObj = ((com_kg_gettransfer_data_model_secondary_PriceRealmProxyInterface) object).realmGet$base();
        if (baseObj != null) {
            Long cachebase = cache.get(baseObj);
            if (cachebase == null) {
                cachebase = com_kg_gettransfer_data_model_secondary_PriceConvertedRealmProxy.insert(realm, baseObj, cache);
            }
            Table.nativeSetLink(tableNativePtr, columnInfo.baseIndex, rowIndex, cachebase, false);
        }
        String realmGet$p30 = ((com_kg_gettransfer_data_model_secondary_PriceRealmProxyInterface) object).realmGet$p30();
        if (realmGet$p30 != null) {
            Table.nativeSetString(tableNativePtr, columnInfo.p30Index, rowIndex, realmGet$p30, false);
        }
        String realmGet$p70 = ((com_kg_gettransfer_data_model_secondary_PriceRealmProxyInterface) object).realmGet$p70();
        if (realmGet$p70 != null) {
            Table.nativeSetString(tableNativePtr, columnInfo.p70Index, rowIndex, realmGet$p70, false);
        }

        com.kg.gettransfer.data.model.secondary.PriceConverted withoutDiscountObj = ((com_kg_gettransfer_data_model_secondary_PriceRealmProxyInterface) object).realmGet$withoutDiscount();
        if (withoutDiscountObj != null) {
            Long cachewithoutDiscount = cache.get(withoutDiscountObj);
            if (cachewithoutDiscount == null) {
                cachewithoutDiscount = com_kg_gettransfer_data_model_secondary_PriceConvertedRealmProxy.insert(realm, withoutDiscountObj, cache);
            }
            Table.nativeSetLink(tableNativePtr, columnInfo.withoutDiscountIndex, rowIndex, cachewithoutDiscount, false);
        }
        Table.nativeSetDouble(tableNativePtr, columnInfo.amountIndex, rowIndex, ((com_kg_gettransfer_data_model_secondary_PriceRealmProxyInterface) object).realmGet$amount(), false);
        return rowIndex;
    }

    public static void insert(Realm realm, Iterator<? extends RealmModel> objects, Map<RealmModel,Long> cache) {
        Table table = realm.getTable(com.kg.gettransfer.data.model.secondary.Price.class);
        long tableNativePtr = table.getNativePtr();
        PriceColumnInfo columnInfo = (PriceColumnInfo) realm.getSchema().getColumnInfo(com.kg.gettransfer.data.model.secondary.Price.class);
        com.kg.gettransfer.data.model.secondary.Price object = null;
        while (objects.hasNext()) {
            object = (com.kg.gettransfer.data.model.secondary.Price) objects.next();
            if (cache.containsKey(object)) {
                continue;
            }
            if (object instanceof RealmObjectProxy && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm() != null && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm().getPath().equals(realm.getPath())) {
                cache.put(object, ((RealmObjectProxy) object).realmGet$proxyState().getRow$realm().getIndex());
                continue;
            }
            long rowIndex = OsObject.createRow(table);
            cache.put(object, rowIndex);

            com.kg.gettransfer.data.model.secondary.PriceConverted baseObj = ((com_kg_gettransfer_data_model_secondary_PriceRealmProxyInterface) object).realmGet$base();
            if (baseObj != null) {
                Long cachebase = cache.get(baseObj);
                if (cachebase == null) {
                    cachebase = com_kg_gettransfer_data_model_secondary_PriceConvertedRealmProxy.insert(realm, baseObj, cache);
                }
                table.setLink(columnInfo.baseIndex, rowIndex, cachebase, false);
            }
            String realmGet$p30 = ((com_kg_gettransfer_data_model_secondary_PriceRealmProxyInterface) object).realmGet$p30();
            if (realmGet$p30 != null) {
                Table.nativeSetString(tableNativePtr, columnInfo.p30Index, rowIndex, realmGet$p30, false);
            }
            String realmGet$p70 = ((com_kg_gettransfer_data_model_secondary_PriceRealmProxyInterface) object).realmGet$p70();
            if (realmGet$p70 != null) {
                Table.nativeSetString(tableNativePtr, columnInfo.p70Index, rowIndex, realmGet$p70, false);
            }

            com.kg.gettransfer.data.model.secondary.PriceConverted withoutDiscountObj = ((com_kg_gettransfer_data_model_secondary_PriceRealmProxyInterface) object).realmGet$withoutDiscount();
            if (withoutDiscountObj != null) {
                Long cachewithoutDiscount = cache.get(withoutDiscountObj);
                if (cachewithoutDiscount == null) {
                    cachewithoutDiscount = com_kg_gettransfer_data_model_secondary_PriceConvertedRealmProxy.insert(realm, withoutDiscountObj, cache);
                }
                table.setLink(columnInfo.withoutDiscountIndex, rowIndex, cachewithoutDiscount, false);
            }
            Table.nativeSetDouble(tableNativePtr, columnInfo.amountIndex, rowIndex, ((com_kg_gettransfer_data_model_secondary_PriceRealmProxyInterface) object).realmGet$amount(), false);
        }
    }

    public static long insertOrUpdate(Realm realm, com.kg.gettransfer.data.model.secondary.Price object, Map<RealmModel,Long> cache) {
        if (object instanceof RealmObjectProxy && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm() != null && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm().getPath().equals(realm.getPath())) {
            return ((RealmObjectProxy) object).realmGet$proxyState().getRow$realm().getIndex();
        }
        Table table = realm.getTable(com.kg.gettransfer.data.model.secondary.Price.class);
        long tableNativePtr = table.getNativePtr();
        PriceColumnInfo columnInfo = (PriceColumnInfo) realm.getSchema().getColumnInfo(com.kg.gettransfer.data.model.secondary.Price.class);
        long rowIndex = OsObject.createRow(table);
        cache.put(object, rowIndex);

        com.kg.gettransfer.data.model.secondary.PriceConverted baseObj = ((com_kg_gettransfer_data_model_secondary_PriceRealmProxyInterface) object).realmGet$base();
        if (baseObj != null) {
            Long cachebase = cache.get(baseObj);
            if (cachebase == null) {
                cachebase = com_kg_gettransfer_data_model_secondary_PriceConvertedRealmProxy.insertOrUpdate(realm, baseObj, cache);
            }
            Table.nativeSetLink(tableNativePtr, columnInfo.baseIndex, rowIndex, cachebase, false);
        } else {
            Table.nativeNullifyLink(tableNativePtr, columnInfo.baseIndex, rowIndex);
        }
        String realmGet$p30 = ((com_kg_gettransfer_data_model_secondary_PriceRealmProxyInterface) object).realmGet$p30();
        if (realmGet$p30 != null) {
            Table.nativeSetString(tableNativePtr, columnInfo.p30Index, rowIndex, realmGet$p30, false);
        } else {
            Table.nativeSetNull(tableNativePtr, columnInfo.p30Index, rowIndex, false);
        }
        String realmGet$p70 = ((com_kg_gettransfer_data_model_secondary_PriceRealmProxyInterface) object).realmGet$p70();
        if (realmGet$p70 != null) {
            Table.nativeSetString(tableNativePtr, columnInfo.p70Index, rowIndex, realmGet$p70, false);
        } else {
            Table.nativeSetNull(tableNativePtr, columnInfo.p70Index, rowIndex, false);
        }

        com.kg.gettransfer.data.model.secondary.PriceConverted withoutDiscountObj = ((com_kg_gettransfer_data_model_secondary_PriceRealmProxyInterface) object).realmGet$withoutDiscount();
        if (withoutDiscountObj != null) {
            Long cachewithoutDiscount = cache.get(withoutDiscountObj);
            if (cachewithoutDiscount == null) {
                cachewithoutDiscount = com_kg_gettransfer_data_model_secondary_PriceConvertedRealmProxy.insertOrUpdate(realm, withoutDiscountObj, cache);
            }
            Table.nativeSetLink(tableNativePtr, columnInfo.withoutDiscountIndex, rowIndex, cachewithoutDiscount, false);
        } else {
            Table.nativeNullifyLink(tableNativePtr, columnInfo.withoutDiscountIndex, rowIndex);
        }
        Table.nativeSetDouble(tableNativePtr, columnInfo.amountIndex, rowIndex, ((com_kg_gettransfer_data_model_secondary_PriceRealmProxyInterface) object).realmGet$amount(), false);
        return rowIndex;
    }

    public static void insertOrUpdate(Realm realm, Iterator<? extends RealmModel> objects, Map<RealmModel,Long> cache) {
        Table table = realm.getTable(com.kg.gettransfer.data.model.secondary.Price.class);
        long tableNativePtr = table.getNativePtr();
        PriceColumnInfo columnInfo = (PriceColumnInfo) realm.getSchema().getColumnInfo(com.kg.gettransfer.data.model.secondary.Price.class);
        com.kg.gettransfer.data.model.secondary.Price object = null;
        while (objects.hasNext()) {
            object = (com.kg.gettransfer.data.model.secondary.Price) objects.next();
            if (cache.containsKey(object)) {
                continue;
            }
            if (object instanceof RealmObjectProxy && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm() != null && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm().getPath().equals(realm.getPath())) {
                cache.put(object, ((RealmObjectProxy) object).realmGet$proxyState().getRow$realm().getIndex());
                continue;
            }
            long rowIndex = OsObject.createRow(table);
            cache.put(object, rowIndex);

            com.kg.gettransfer.data.model.secondary.PriceConverted baseObj = ((com_kg_gettransfer_data_model_secondary_PriceRealmProxyInterface) object).realmGet$base();
            if (baseObj != null) {
                Long cachebase = cache.get(baseObj);
                if (cachebase == null) {
                    cachebase = com_kg_gettransfer_data_model_secondary_PriceConvertedRealmProxy.insertOrUpdate(realm, baseObj, cache);
                }
                Table.nativeSetLink(tableNativePtr, columnInfo.baseIndex, rowIndex, cachebase, false);
            } else {
                Table.nativeNullifyLink(tableNativePtr, columnInfo.baseIndex, rowIndex);
            }
            String realmGet$p30 = ((com_kg_gettransfer_data_model_secondary_PriceRealmProxyInterface) object).realmGet$p30();
            if (realmGet$p30 != null) {
                Table.nativeSetString(tableNativePtr, columnInfo.p30Index, rowIndex, realmGet$p30, false);
            } else {
                Table.nativeSetNull(tableNativePtr, columnInfo.p30Index, rowIndex, false);
            }
            String realmGet$p70 = ((com_kg_gettransfer_data_model_secondary_PriceRealmProxyInterface) object).realmGet$p70();
            if (realmGet$p70 != null) {
                Table.nativeSetString(tableNativePtr, columnInfo.p70Index, rowIndex, realmGet$p70, false);
            } else {
                Table.nativeSetNull(tableNativePtr, columnInfo.p70Index, rowIndex, false);
            }

            com.kg.gettransfer.data.model.secondary.PriceConverted withoutDiscountObj = ((com_kg_gettransfer_data_model_secondary_PriceRealmProxyInterface) object).realmGet$withoutDiscount();
            if (withoutDiscountObj != null) {
                Long cachewithoutDiscount = cache.get(withoutDiscountObj);
                if (cachewithoutDiscount == null) {
                    cachewithoutDiscount = com_kg_gettransfer_data_model_secondary_PriceConvertedRealmProxy.insertOrUpdate(realm, withoutDiscountObj, cache);
                }
                Table.nativeSetLink(tableNativePtr, columnInfo.withoutDiscountIndex, rowIndex, cachewithoutDiscount, false);
            } else {
                Table.nativeNullifyLink(tableNativePtr, columnInfo.withoutDiscountIndex, rowIndex);
            }
            Table.nativeSetDouble(tableNativePtr, columnInfo.amountIndex, rowIndex, ((com_kg_gettransfer_data_model_secondary_PriceRealmProxyInterface) object).realmGet$amount(), false);
        }
    }

    public static com.kg.gettransfer.data.model.secondary.Price createDetachedCopy(com.kg.gettransfer.data.model.secondary.Price realmObject, int currentDepth, int maxDepth, Map<RealmModel, CacheData<RealmModel>> cache) {
        if (currentDepth > maxDepth || realmObject == null) {
            return null;
        }
        CacheData<RealmModel> cachedObject = cache.get(realmObject);
        com.kg.gettransfer.data.model.secondary.Price unmanagedObject;
        if (cachedObject == null) {
            unmanagedObject = new com.kg.gettransfer.data.model.secondary.Price();
            cache.put(realmObject, new RealmObjectProxy.CacheData<RealmModel>(currentDepth, unmanagedObject));
        } else {
            // Reuse cached object or recreate it because it was encountered at a lower depth.
            if (currentDepth >= cachedObject.minDepth) {
                return (com.kg.gettransfer.data.model.secondary.Price) cachedObject.object;
            }
            unmanagedObject = (com.kg.gettransfer.data.model.secondary.Price) cachedObject.object;
            cachedObject.minDepth = currentDepth;
        }
        com_kg_gettransfer_data_model_secondary_PriceRealmProxyInterface unmanagedCopy = (com_kg_gettransfer_data_model_secondary_PriceRealmProxyInterface) unmanagedObject;
        com_kg_gettransfer_data_model_secondary_PriceRealmProxyInterface realmSource = (com_kg_gettransfer_data_model_secondary_PriceRealmProxyInterface) realmObject;

        // Deep copy of base
        unmanagedCopy.realmSet$base(com_kg_gettransfer_data_model_secondary_PriceConvertedRealmProxy.createDetachedCopy(realmSource.realmGet$base(), currentDepth + 1, maxDepth, cache));
        unmanagedCopy.realmSet$p30(realmSource.realmGet$p30());
        unmanagedCopy.realmSet$p70(realmSource.realmGet$p70());

        // Deep copy of withoutDiscount
        unmanagedCopy.realmSet$withoutDiscount(com_kg_gettransfer_data_model_secondary_PriceConvertedRealmProxy.createDetachedCopy(realmSource.realmGet$withoutDiscount(), currentDepth + 1, maxDepth, cache));
        unmanagedCopy.realmSet$amount(realmSource.realmGet$amount());

        return unmanagedObject;
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
        com_kg_gettransfer_data_model_secondary_PriceRealmProxy aPrice = (com_kg_gettransfer_data_model_secondary_PriceRealmProxy)o;

        String path = proxyState.getRealm$realm().getPath();
        String otherPath = aPrice.proxyState.getRealm$realm().getPath();
        if (path != null ? !path.equals(otherPath) : otherPath != null) return false;

        String tableName = proxyState.getRow$realm().getTable().getName();
        String otherTableName = aPrice.proxyState.getRow$realm().getTable().getName();
        if (tableName != null ? !tableName.equals(otherTableName) : otherTableName != null) return false;

        if (proxyState.getRow$realm().getIndex() != aPrice.proxyState.getRow$realm().getIndex()) return false;

        return true;
    }
}
