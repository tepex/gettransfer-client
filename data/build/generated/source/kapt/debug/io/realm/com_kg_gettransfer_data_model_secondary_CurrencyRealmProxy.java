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
public class com_kg_gettransfer_data_model_secondary_CurrencyRealmProxy extends com.kg.gettransfer.data.model.secondary.Currency
    implements RealmObjectProxy, com_kg_gettransfer_data_model_secondary_CurrencyRealmProxyInterface {

    static final class CurrencyColumnInfo extends ColumnInfo {
        long isoCodeIndex;
        long symbolIndex;

        CurrencyColumnInfo(OsSchemaInfo schemaInfo) {
            super(2);
            OsObjectSchemaInfo objectSchemaInfo = schemaInfo.getObjectSchemaInfo("Currency");
            this.isoCodeIndex = addColumnDetails("isoCode", "isoCode", objectSchemaInfo);
            this.symbolIndex = addColumnDetails("symbol", "symbol", objectSchemaInfo);
        }

        CurrencyColumnInfo(ColumnInfo src, boolean mutable) {
            super(src, mutable);
            copy(src, this);
        }

        @Override
        protected final ColumnInfo copy(boolean mutable) {
            return new CurrencyColumnInfo(this, mutable);
        }

        @Override
        protected final void copy(ColumnInfo rawSrc, ColumnInfo rawDst) {
            final CurrencyColumnInfo src = (CurrencyColumnInfo) rawSrc;
            final CurrencyColumnInfo dst = (CurrencyColumnInfo) rawDst;
            dst.isoCodeIndex = src.isoCodeIndex;
            dst.symbolIndex = src.symbolIndex;
        }
    }

    private static final OsObjectSchemaInfo expectedObjectSchemaInfo = createExpectedObjectSchemaInfo();

    private CurrencyColumnInfo columnInfo;
    private ProxyState<com.kg.gettransfer.data.model.secondary.Currency> proxyState;

    com_kg_gettransfer_data_model_secondary_CurrencyRealmProxy() {
        proxyState.setConstructionFinished();
    }

    @Override
    public void realm$injectObjectContext() {
        if (this.proxyState != null) {
            return;
        }
        final BaseRealm.RealmObjectContext context = BaseRealm.objectContext.get();
        this.columnInfo = (CurrencyColumnInfo) context.getColumnInfo();
        this.proxyState = new ProxyState<com.kg.gettransfer.data.model.secondary.Currency>(this);
        proxyState.setRealm$realm(context.getRealm());
        proxyState.setRow$realm(context.getRow());
        proxyState.setAcceptDefaultValue$realm(context.getAcceptDefaultValue());
        proxyState.setExcludeFields$realm(context.getExcludeFields());
    }

    @Override
    @SuppressWarnings("cast")
    public String realmGet$isoCode() {
        proxyState.getRealm$realm().checkIfValid();
        return (java.lang.String) proxyState.getRow$realm().getString(columnInfo.isoCodeIndex);
    }

    @Override
    public void realmSet$isoCode(String value) {
        if (proxyState.isUnderConstruction()) {
            if (!proxyState.getAcceptDefaultValue$realm()) {
                return;
            }
            final Row row = proxyState.getRow$realm();
            if (value == null) {
                throw new IllegalArgumentException("Trying to set non-nullable field 'isoCode' to null.");
            }
            row.getTable().setString(columnInfo.isoCodeIndex, row.getIndex(), value, true);
            return;
        }

        proxyState.getRealm$realm().checkIfValid();
        if (value == null) {
            throw new IllegalArgumentException("Trying to set non-nullable field 'isoCode' to null.");
        }
        proxyState.getRow$realm().setString(columnInfo.isoCodeIndex, value);
    }

    @Override
    @SuppressWarnings("cast")
    public String realmGet$symbol() {
        proxyState.getRealm$realm().checkIfValid();
        return (java.lang.String) proxyState.getRow$realm().getString(columnInfo.symbolIndex);
    }

    @Override
    public void realmSet$symbol(String value) {
        if (proxyState.isUnderConstruction()) {
            if (!proxyState.getAcceptDefaultValue$realm()) {
                return;
            }
            final Row row = proxyState.getRow$realm();
            if (value == null) {
                throw new IllegalArgumentException("Trying to set non-nullable field 'symbol' to null.");
            }
            row.getTable().setString(columnInfo.symbolIndex, row.getIndex(), value, true);
            return;
        }

        proxyState.getRealm$realm().checkIfValid();
        if (value == null) {
            throw new IllegalArgumentException("Trying to set non-nullable field 'symbol' to null.");
        }
        proxyState.getRow$realm().setString(columnInfo.symbolIndex, value);
    }

    private static OsObjectSchemaInfo createExpectedObjectSchemaInfo() {
        OsObjectSchemaInfo.Builder builder = new OsObjectSchemaInfo.Builder("Currency", 2, 0);
        builder.addPersistedProperty("isoCode", RealmFieldType.STRING, !Property.PRIMARY_KEY, !Property.INDEXED, Property.REQUIRED);
        builder.addPersistedProperty("symbol", RealmFieldType.STRING, !Property.PRIMARY_KEY, !Property.INDEXED, Property.REQUIRED);
        return builder.build();
    }

    public static OsObjectSchemaInfo getExpectedObjectSchemaInfo() {
        return expectedObjectSchemaInfo;
    }

    public static CurrencyColumnInfo createColumnInfo(OsSchemaInfo schemaInfo) {
        return new CurrencyColumnInfo(schemaInfo);
    }

    public static String getSimpleClassName() {
        return "Currency";
    }

    public static final class ClassNameHelper {
        public static final String INTERNAL_CLASS_NAME = "Currency";
    }

    @SuppressWarnings("cast")
    public static com.kg.gettransfer.data.model.secondary.Currency createOrUpdateUsingJsonObject(Realm realm, JSONObject json, boolean update)
        throws JSONException {
        final List<String> excludeFields = Collections.<String> emptyList();
        com.kg.gettransfer.data.model.secondary.Currency obj = realm.createObjectInternal(com.kg.gettransfer.data.model.secondary.Currency.class, true, excludeFields);

        final com_kg_gettransfer_data_model_secondary_CurrencyRealmProxyInterface objProxy = (com_kg_gettransfer_data_model_secondary_CurrencyRealmProxyInterface) obj;
        if (json.has("isoCode")) {
            if (json.isNull("isoCode")) {
                objProxy.realmSet$isoCode(null);
            } else {
                objProxy.realmSet$isoCode((String) json.getString("isoCode"));
            }
        }
        if (json.has("symbol")) {
            if (json.isNull("symbol")) {
                objProxy.realmSet$symbol(null);
            } else {
                objProxy.realmSet$symbol((String) json.getString("symbol"));
            }
        }
        return obj;
    }

    @SuppressWarnings("cast")
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static com.kg.gettransfer.data.model.secondary.Currency createUsingJsonStream(Realm realm, JsonReader reader)
        throws IOException {
        final com.kg.gettransfer.data.model.secondary.Currency obj = new com.kg.gettransfer.data.model.secondary.Currency();
        final com_kg_gettransfer_data_model_secondary_CurrencyRealmProxyInterface objProxy = (com_kg_gettransfer_data_model_secondary_CurrencyRealmProxyInterface) obj;
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (false) {
            } else if (name.equals("isoCode")) {
                if (reader.peek() != JsonToken.NULL) {
                    objProxy.realmSet$isoCode((String) reader.nextString());
                } else {
                    reader.skipValue();
                    objProxy.realmSet$isoCode(null);
                }
            } else if (name.equals("symbol")) {
                if (reader.peek() != JsonToken.NULL) {
                    objProxy.realmSet$symbol((String) reader.nextString());
                } else {
                    reader.skipValue();
                    objProxy.realmSet$symbol(null);
                }
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return realm.copyToRealm(obj);
    }

    public static com.kg.gettransfer.data.model.secondary.Currency copyOrUpdate(Realm realm, com.kg.gettransfer.data.model.secondary.Currency object, boolean update, Map<RealmModel,RealmObjectProxy> cache) {
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
            return (com.kg.gettransfer.data.model.secondary.Currency) cachedRealmObject;
        }

        return copy(realm, object, update, cache);
    }

    public static com.kg.gettransfer.data.model.secondary.Currency copy(Realm realm, com.kg.gettransfer.data.model.secondary.Currency newObject, boolean update, Map<RealmModel,RealmObjectProxy> cache) {
        RealmObjectProxy cachedRealmObject = cache.get(newObject);
        if (cachedRealmObject != null) {
            return (com.kg.gettransfer.data.model.secondary.Currency) cachedRealmObject;
        }

        // rejecting default values to avoid creating unexpected objects from RealmModel/RealmList fields.
        com.kg.gettransfer.data.model.secondary.Currency realmObject = realm.createObjectInternal(com.kg.gettransfer.data.model.secondary.Currency.class, false, Collections.<String>emptyList());
        cache.put(newObject, (RealmObjectProxy) realmObject);

        com_kg_gettransfer_data_model_secondary_CurrencyRealmProxyInterface realmObjectSource = (com_kg_gettransfer_data_model_secondary_CurrencyRealmProxyInterface) newObject;
        com_kg_gettransfer_data_model_secondary_CurrencyRealmProxyInterface realmObjectCopy = (com_kg_gettransfer_data_model_secondary_CurrencyRealmProxyInterface) realmObject;

        realmObjectCopy.realmSet$isoCode(realmObjectSource.realmGet$isoCode());
        realmObjectCopy.realmSet$symbol(realmObjectSource.realmGet$symbol());
        return realmObject;
    }

    public static long insert(Realm realm, com.kg.gettransfer.data.model.secondary.Currency object, Map<RealmModel,Long> cache) {
        if (object instanceof RealmObjectProxy && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm() != null && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm().getPath().equals(realm.getPath())) {
            return ((RealmObjectProxy) object).realmGet$proxyState().getRow$realm().getIndex();
        }
        Table table = realm.getTable(com.kg.gettransfer.data.model.secondary.Currency.class);
        long tableNativePtr = table.getNativePtr();
        CurrencyColumnInfo columnInfo = (CurrencyColumnInfo) realm.getSchema().getColumnInfo(com.kg.gettransfer.data.model.secondary.Currency.class);
        long rowIndex = OsObject.createRow(table);
        cache.put(object, rowIndex);
        String realmGet$isoCode = ((com_kg_gettransfer_data_model_secondary_CurrencyRealmProxyInterface) object).realmGet$isoCode();
        if (realmGet$isoCode != null) {
            Table.nativeSetString(tableNativePtr, columnInfo.isoCodeIndex, rowIndex, realmGet$isoCode, false);
        }
        String realmGet$symbol = ((com_kg_gettransfer_data_model_secondary_CurrencyRealmProxyInterface) object).realmGet$symbol();
        if (realmGet$symbol != null) {
            Table.nativeSetString(tableNativePtr, columnInfo.symbolIndex, rowIndex, realmGet$symbol, false);
        }
        return rowIndex;
    }

    public static void insert(Realm realm, Iterator<? extends RealmModel> objects, Map<RealmModel,Long> cache) {
        Table table = realm.getTable(com.kg.gettransfer.data.model.secondary.Currency.class);
        long tableNativePtr = table.getNativePtr();
        CurrencyColumnInfo columnInfo = (CurrencyColumnInfo) realm.getSchema().getColumnInfo(com.kg.gettransfer.data.model.secondary.Currency.class);
        com.kg.gettransfer.data.model.secondary.Currency object = null;
        while (objects.hasNext()) {
            object = (com.kg.gettransfer.data.model.secondary.Currency) objects.next();
            if (cache.containsKey(object)) {
                continue;
            }
            if (object instanceof RealmObjectProxy && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm() != null && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm().getPath().equals(realm.getPath())) {
                cache.put(object, ((RealmObjectProxy) object).realmGet$proxyState().getRow$realm().getIndex());
                continue;
            }
            long rowIndex = OsObject.createRow(table);
            cache.put(object, rowIndex);
            String realmGet$isoCode = ((com_kg_gettransfer_data_model_secondary_CurrencyRealmProxyInterface) object).realmGet$isoCode();
            if (realmGet$isoCode != null) {
                Table.nativeSetString(tableNativePtr, columnInfo.isoCodeIndex, rowIndex, realmGet$isoCode, false);
            }
            String realmGet$symbol = ((com_kg_gettransfer_data_model_secondary_CurrencyRealmProxyInterface) object).realmGet$symbol();
            if (realmGet$symbol != null) {
                Table.nativeSetString(tableNativePtr, columnInfo.symbolIndex, rowIndex, realmGet$symbol, false);
            }
        }
    }

    public static long insertOrUpdate(Realm realm, com.kg.gettransfer.data.model.secondary.Currency object, Map<RealmModel,Long> cache) {
        if (object instanceof RealmObjectProxy && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm() != null && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm().getPath().equals(realm.getPath())) {
            return ((RealmObjectProxy) object).realmGet$proxyState().getRow$realm().getIndex();
        }
        Table table = realm.getTable(com.kg.gettransfer.data.model.secondary.Currency.class);
        long tableNativePtr = table.getNativePtr();
        CurrencyColumnInfo columnInfo = (CurrencyColumnInfo) realm.getSchema().getColumnInfo(com.kg.gettransfer.data.model.secondary.Currency.class);
        long rowIndex = OsObject.createRow(table);
        cache.put(object, rowIndex);
        String realmGet$isoCode = ((com_kg_gettransfer_data_model_secondary_CurrencyRealmProxyInterface) object).realmGet$isoCode();
        if (realmGet$isoCode != null) {
            Table.nativeSetString(tableNativePtr, columnInfo.isoCodeIndex, rowIndex, realmGet$isoCode, false);
        } else {
            Table.nativeSetNull(tableNativePtr, columnInfo.isoCodeIndex, rowIndex, false);
        }
        String realmGet$symbol = ((com_kg_gettransfer_data_model_secondary_CurrencyRealmProxyInterface) object).realmGet$symbol();
        if (realmGet$symbol != null) {
            Table.nativeSetString(tableNativePtr, columnInfo.symbolIndex, rowIndex, realmGet$symbol, false);
        } else {
            Table.nativeSetNull(tableNativePtr, columnInfo.symbolIndex, rowIndex, false);
        }
        return rowIndex;
    }

    public static void insertOrUpdate(Realm realm, Iterator<? extends RealmModel> objects, Map<RealmModel,Long> cache) {
        Table table = realm.getTable(com.kg.gettransfer.data.model.secondary.Currency.class);
        long tableNativePtr = table.getNativePtr();
        CurrencyColumnInfo columnInfo = (CurrencyColumnInfo) realm.getSchema().getColumnInfo(com.kg.gettransfer.data.model.secondary.Currency.class);
        com.kg.gettransfer.data.model.secondary.Currency object = null;
        while (objects.hasNext()) {
            object = (com.kg.gettransfer.data.model.secondary.Currency) objects.next();
            if (cache.containsKey(object)) {
                continue;
            }
            if (object instanceof RealmObjectProxy && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm() != null && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm().getPath().equals(realm.getPath())) {
                cache.put(object, ((RealmObjectProxy) object).realmGet$proxyState().getRow$realm().getIndex());
                continue;
            }
            long rowIndex = OsObject.createRow(table);
            cache.put(object, rowIndex);
            String realmGet$isoCode = ((com_kg_gettransfer_data_model_secondary_CurrencyRealmProxyInterface) object).realmGet$isoCode();
            if (realmGet$isoCode != null) {
                Table.nativeSetString(tableNativePtr, columnInfo.isoCodeIndex, rowIndex, realmGet$isoCode, false);
            } else {
                Table.nativeSetNull(tableNativePtr, columnInfo.isoCodeIndex, rowIndex, false);
            }
            String realmGet$symbol = ((com_kg_gettransfer_data_model_secondary_CurrencyRealmProxyInterface) object).realmGet$symbol();
            if (realmGet$symbol != null) {
                Table.nativeSetString(tableNativePtr, columnInfo.symbolIndex, rowIndex, realmGet$symbol, false);
            } else {
                Table.nativeSetNull(tableNativePtr, columnInfo.symbolIndex, rowIndex, false);
            }
        }
    }

    public static com.kg.gettransfer.data.model.secondary.Currency createDetachedCopy(com.kg.gettransfer.data.model.secondary.Currency realmObject, int currentDepth, int maxDepth, Map<RealmModel, CacheData<RealmModel>> cache) {
        if (currentDepth > maxDepth || realmObject == null) {
            return null;
        }
        CacheData<RealmModel> cachedObject = cache.get(realmObject);
        com.kg.gettransfer.data.model.secondary.Currency unmanagedObject;
        if (cachedObject == null) {
            unmanagedObject = new com.kg.gettransfer.data.model.secondary.Currency();
            cache.put(realmObject, new RealmObjectProxy.CacheData<RealmModel>(currentDepth, unmanagedObject));
        } else {
            // Reuse cached object or recreate it because it was encountered at a lower depth.
            if (currentDepth >= cachedObject.minDepth) {
                return (com.kg.gettransfer.data.model.secondary.Currency) cachedObject.object;
            }
            unmanagedObject = (com.kg.gettransfer.data.model.secondary.Currency) cachedObject.object;
            cachedObject.minDepth = currentDepth;
        }
        com_kg_gettransfer_data_model_secondary_CurrencyRealmProxyInterface unmanagedCopy = (com_kg_gettransfer_data_model_secondary_CurrencyRealmProxyInterface) unmanagedObject;
        com_kg_gettransfer_data_model_secondary_CurrencyRealmProxyInterface realmSource = (com_kg_gettransfer_data_model_secondary_CurrencyRealmProxyInterface) realmObject;
        unmanagedCopy.realmSet$isoCode(realmSource.realmGet$isoCode());
        unmanagedCopy.realmSet$symbol(realmSource.realmGet$symbol());

        return unmanagedObject;
    }

    @Override
    @SuppressWarnings("ArrayToString")
    public String toString() {
        if (!RealmObject.isValid(this)) {
            return "Invalid object";
        }
        StringBuilder stringBuilder = new StringBuilder("Currency = proxy[");
        stringBuilder.append("{isoCode:");
        stringBuilder.append(realmGet$isoCode());
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{symbol:");
        stringBuilder.append(realmGet$symbol());
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
        com_kg_gettransfer_data_model_secondary_CurrencyRealmProxy aCurrency = (com_kg_gettransfer_data_model_secondary_CurrencyRealmProxy)o;

        String path = proxyState.getRealm$realm().getPath();
        String otherPath = aCurrency.proxyState.getRealm$realm().getPath();
        if (path != null ? !path.equals(otherPath) : otherPath != null) return false;

        String tableName = proxyState.getRow$realm().getTable().getName();
        String otherTableName = aCurrency.proxyState.getRow$realm().getTable().getName();
        if (tableName != null ? !tableName.equals(otherTableName) : otherTableName != null) return false;

        if (proxyState.getRow$realm().getIndex() != aCurrency.proxyState.getRow$realm().getIndex()) return false;

        return true;
    }
}
