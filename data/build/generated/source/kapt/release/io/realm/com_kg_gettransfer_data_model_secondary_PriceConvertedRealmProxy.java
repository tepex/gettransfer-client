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
public class com_kg_gettransfer_data_model_secondary_PriceConvertedRealmProxy extends com.kg.gettransfer.data.model.secondary.PriceConverted
    implements RealmObjectProxy, com_kg_gettransfer_data_model_secondary_PriceConvertedRealmProxyInterface {

    static final class PriceConvertedColumnInfo extends ColumnInfo {
        long defaultCurrencyIndex;
        long preferredCurrencyIndex;

        PriceConvertedColumnInfo(OsSchemaInfo schemaInfo) {
            super(2);
            OsObjectSchemaInfo objectSchemaInfo = schemaInfo.getObjectSchemaInfo("PriceConverted");
            this.defaultCurrencyIndex = addColumnDetails("defaultCurrency", "defaultCurrency", objectSchemaInfo);
            this.preferredCurrencyIndex = addColumnDetails("preferredCurrency", "preferredCurrency", objectSchemaInfo);
        }

        PriceConvertedColumnInfo(ColumnInfo src, boolean mutable) {
            super(src, mutable);
            copy(src, this);
        }

        @Override
        protected final ColumnInfo copy(boolean mutable) {
            return new PriceConvertedColumnInfo(this, mutable);
        }

        @Override
        protected final void copy(ColumnInfo rawSrc, ColumnInfo rawDst) {
            final PriceConvertedColumnInfo src = (PriceConvertedColumnInfo) rawSrc;
            final PriceConvertedColumnInfo dst = (PriceConvertedColumnInfo) rawDst;
            dst.defaultCurrencyIndex = src.defaultCurrencyIndex;
            dst.preferredCurrencyIndex = src.preferredCurrencyIndex;
        }
    }

    private static final OsObjectSchemaInfo expectedObjectSchemaInfo = createExpectedObjectSchemaInfo();

    private PriceConvertedColumnInfo columnInfo;
    private ProxyState<com.kg.gettransfer.data.model.secondary.PriceConverted> proxyState;

    com_kg_gettransfer_data_model_secondary_PriceConvertedRealmProxy() {
        proxyState.setConstructionFinished();
    }

    @Override
    public void realm$injectObjectContext() {
        if (this.proxyState != null) {
            return;
        }
        final BaseRealm.RealmObjectContext context = BaseRealm.objectContext.get();
        this.columnInfo = (PriceConvertedColumnInfo) context.getColumnInfo();
        this.proxyState = new ProxyState<com.kg.gettransfer.data.model.secondary.PriceConverted>(this);
        proxyState.setRealm$realm(context.getRealm());
        proxyState.setRow$realm(context.getRow());
        proxyState.setAcceptDefaultValue$realm(context.getAcceptDefaultValue());
        proxyState.setExcludeFields$realm(context.getExcludeFields());
    }

    @Override
    @SuppressWarnings("cast")
    public String realmGet$defaultCurrency() {
        proxyState.getRealm$realm().checkIfValid();
        return (java.lang.String) proxyState.getRow$realm().getString(columnInfo.defaultCurrencyIndex);
    }

    @Override
    public void realmSet$defaultCurrency(String value) {
        if (proxyState.isUnderConstruction()) {
            if (!proxyState.getAcceptDefaultValue$realm()) {
                return;
            }
            final Row row = proxyState.getRow$realm();
            if (value == null) {
                row.getTable().setNull(columnInfo.defaultCurrencyIndex, row.getIndex(), true);
                return;
            }
            row.getTable().setString(columnInfo.defaultCurrencyIndex, row.getIndex(), value, true);
            return;
        }

        proxyState.getRealm$realm().checkIfValid();
        if (value == null) {
            proxyState.getRow$realm().setNull(columnInfo.defaultCurrencyIndex);
            return;
        }
        proxyState.getRow$realm().setString(columnInfo.defaultCurrencyIndex, value);
    }

    @Override
    @SuppressWarnings("cast")
    public String realmGet$preferredCurrency() {
        proxyState.getRealm$realm().checkIfValid();
        return (java.lang.String) proxyState.getRow$realm().getString(columnInfo.preferredCurrencyIndex);
    }

    @Override
    public void realmSet$preferredCurrency(String value) {
        if (proxyState.isUnderConstruction()) {
            if (!proxyState.getAcceptDefaultValue$realm()) {
                return;
            }
            final Row row = proxyState.getRow$realm();
            if (value == null) {
                row.getTable().setNull(columnInfo.preferredCurrencyIndex, row.getIndex(), true);
                return;
            }
            row.getTable().setString(columnInfo.preferredCurrencyIndex, row.getIndex(), value, true);
            return;
        }

        proxyState.getRealm$realm().checkIfValid();
        if (value == null) {
            proxyState.getRow$realm().setNull(columnInfo.preferredCurrencyIndex);
            return;
        }
        proxyState.getRow$realm().setString(columnInfo.preferredCurrencyIndex, value);
    }

    private static OsObjectSchemaInfo createExpectedObjectSchemaInfo() {
        OsObjectSchemaInfo.Builder builder = new OsObjectSchemaInfo.Builder("PriceConverted", 2, 0);
        builder.addPersistedProperty("defaultCurrency", RealmFieldType.STRING, !Property.PRIMARY_KEY, !Property.INDEXED, !Property.REQUIRED);
        builder.addPersistedProperty("preferredCurrency", RealmFieldType.STRING, !Property.PRIMARY_KEY, !Property.INDEXED, !Property.REQUIRED);
        return builder.build();
    }

    public static OsObjectSchemaInfo getExpectedObjectSchemaInfo() {
        return expectedObjectSchemaInfo;
    }

    public static PriceConvertedColumnInfo createColumnInfo(OsSchemaInfo schemaInfo) {
        return new PriceConvertedColumnInfo(schemaInfo);
    }

    public static String getSimpleClassName() {
        return "PriceConverted";
    }

    public static final class ClassNameHelper {
        public static final String INTERNAL_CLASS_NAME = "PriceConverted";
    }

    @SuppressWarnings("cast")
    public static com.kg.gettransfer.data.model.secondary.PriceConverted createOrUpdateUsingJsonObject(Realm realm, JSONObject json, boolean update)
        throws JSONException {
        final List<String> excludeFields = Collections.<String> emptyList();
        com.kg.gettransfer.data.model.secondary.PriceConverted obj = realm.createObjectInternal(com.kg.gettransfer.data.model.secondary.PriceConverted.class, true, excludeFields);

        final com_kg_gettransfer_data_model_secondary_PriceConvertedRealmProxyInterface objProxy = (com_kg_gettransfer_data_model_secondary_PriceConvertedRealmProxyInterface) obj;
        if (json.has("defaultCurrency")) {
            if (json.isNull("defaultCurrency")) {
                objProxy.realmSet$defaultCurrency(null);
            } else {
                objProxy.realmSet$defaultCurrency((String) json.getString("defaultCurrency"));
            }
        }
        if (json.has("preferredCurrency")) {
            if (json.isNull("preferredCurrency")) {
                objProxy.realmSet$preferredCurrency(null);
            } else {
                objProxy.realmSet$preferredCurrency((String) json.getString("preferredCurrency"));
            }
        }
        return obj;
    }

    @SuppressWarnings("cast")
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static com.kg.gettransfer.data.model.secondary.PriceConverted createUsingJsonStream(Realm realm, JsonReader reader)
        throws IOException {
        final com.kg.gettransfer.data.model.secondary.PriceConverted obj = new com.kg.gettransfer.data.model.secondary.PriceConverted();
        final com_kg_gettransfer_data_model_secondary_PriceConvertedRealmProxyInterface objProxy = (com_kg_gettransfer_data_model_secondary_PriceConvertedRealmProxyInterface) obj;
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (false) {
            } else if (name.equals("defaultCurrency")) {
                if (reader.peek() != JsonToken.NULL) {
                    objProxy.realmSet$defaultCurrency((String) reader.nextString());
                } else {
                    reader.skipValue();
                    objProxy.realmSet$defaultCurrency(null);
                }
            } else if (name.equals("preferredCurrency")) {
                if (reader.peek() != JsonToken.NULL) {
                    objProxy.realmSet$preferredCurrency((String) reader.nextString());
                } else {
                    reader.skipValue();
                    objProxy.realmSet$preferredCurrency(null);
                }
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return realm.copyToRealm(obj);
    }

    public static com.kg.gettransfer.data.model.secondary.PriceConverted copyOrUpdate(Realm realm, com.kg.gettransfer.data.model.secondary.PriceConverted object, boolean update, Map<RealmModel,RealmObjectProxy> cache) {
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
            return (com.kg.gettransfer.data.model.secondary.PriceConverted) cachedRealmObject;
        }

        return copy(realm, object, update, cache);
    }

    public static com.kg.gettransfer.data.model.secondary.PriceConverted copy(Realm realm, com.kg.gettransfer.data.model.secondary.PriceConverted newObject, boolean update, Map<RealmModel,RealmObjectProxy> cache) {
        RealmObjectProxy cachedRealmObject = cache.get(newObject);
        if (cachedRealmObject != null) {
            return (com.kg.gettransfer.data.model.secondary.PriceConverted) cachedRealmObject;
        }

        // rejecting default values to avoid creating unexpected objects from RealmModel/RealmList fields.
        com.kg.gettransfer.data.model.secondary.PriceConverted realmObject = realm.createObjectInternal(com.kg.gettransfer.data.model.secondary.PriceConverted.class, false, Collections.<String>emptyList());
        cache.put(newObject, (RealmObjectProxy) realmObject);

        com_kg_gettransfer_data_model_secondary_PriceConvertedRealmProxyInterface realmObjectSource = (com_kg_gettransfer_data_model_secondary_PriceConvertedRealmProxyInterface) newObject;
        com_kg_gettransfer_data_model_secondary_PriceConvertedRealmProxyInterface realmObjectCopy = (com_kg_gettransfer_data_model_secondary_PriceConvertedRealmProxyInterface) realmObject;

        realmObjectCopy.realmSet$defaultCurrency(realmObjectSource.realmGet$defaultCurrency());
        realmObjectCopy.realmSet$preferredCurrency(realmObjectSource.realmGet$preferredCurrency());
        return realmObject;
    }

    public static long insert(Realm realm, com.kg.gettransfer.data.model.secondary.PriceConverted object, Map<RealmModel,Long> cache) {
        if (object instanceof RealmObjectProxy && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm() != null && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm().getPath().equals(realm.getPath())) {
            return ((RealmObjectProxy) object).realmGet$proxyState().getRow$realm().getIndex();
        }
        Table table = realm.getTable(com.kg.gettransfer.data.model.secondary.PriceConverted.class);
        long tableNativePtr = table.getNativePtr();
        PriceConvertedColumnInfo columnInfo = (PriceConvertedColumnInfo) realm.getSchema().getColumnInfo(com.kg.gettransfer.data.model.secondary.PriceConverted.class);
        long rowIndex = OsObject.createRow(table);
        cache.put(object, rowIndex);
        String realmGet$defaultCurrency = ((com_kg_gettransfer_data_model_secondary_PriceConvertedRealmProxyInterface) object).realmGet$defaultCurrency();
        if (realmGet$defaultCurrency != null) {
            Table.nativeSetString(tableNativePtr, columnInfo.defaultCurrencyIndex, rowIndex, realmGet$defaultCurrency, false);
        }
        String realmGet$preferredCurrency = ((com_kg_gettransfer_data_model_secondary_PriceConvertedRealmProxyInterface) object).realmGet$preferredCurrency();
        if (realmGet$preferredCurrency != null) {
            Table.nativeSetString(tableNativePtr, columnInfo.preferredCurrencyIndex, rowIndex, realmGet$preferredCurrency, false);
        }
        return rowIndex;
    }

    public static void insert(Realm realm, Iterator<? extends RealmModel> objects, Map<RealmModel,Long> cache) {
        Table table = realm.getTable(com.kg.gettransfer.data.model.secondary.PriceConverted.class);
        long tableNativePtr = table.getNativePtr();
        PriceConvertedColumnInfo columnInfo = (PriceConvertedColumnInfo) realm.getSchema().getColumnInfo(com.kg.gettransfer.data.model.secondary.PriceConverted.class);
        com.kg.gettransfer.data.model.secondary.PriceConverted object = null;
        while (objects.hasNext()) {
            object = (com.kg.gettransfer.data.model.secondary.PriceConverted) objects.next();
            if (cache.containsKey(object)) {
                continue;
            }
            if (object instanceof RealmObjectProxy && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm() != null && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm().getPath().equals(realm.getPath())) {
                cache.put(object, ((RealmObjectProxy) object).realmGet$proxyState().getRow$realm().getIndex());
                continue;
            }
            long rowIndex = OsObject.createRow(table);
            cache.put(object, rowIndex);
            String realmGet$defaultCurrency = ((com_kg_gettransfer_data_model_secondary_PriceConvertedRealmProxyInterface) object).realmGet$defaultCurrency();
            if (realmGet$defaultCurrency != null) {
                Table.nativeSetString(tableNativePtr, columnInfo.defaultCurrencyIndex, rowIndex, realmGet$defaultCurrency, false);
            }
            String realmGet$preferredCurrency = ((com_kg_gettransfer_data_model_secondary_PriceConvertedRealmProxyInterface) object).realmGet$preferredCurrency();
            if (realmGet$preferredCurrency != null) {
                Table.nativeSetString(tableNativePtr, columnInfo.preferredCurrencyIndex, rowIndex, realmGet$preferredCurrency, false);
            }
        }
    }

    public static long insertOrUpdate(Realm realm, com.kg.gettransfer.data.model.secondary.PriceConverted object, Map<RealmModel,Long> cache) {
        if (object instanceof RealmObjectProxy && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm() != null && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm().getPath().equals(realm.getPath())) {
            return ((RealmObjectProxy) object).realmGet$proxyState().getRow$realm().getIndex();
        }
        Table table = realm.getTable(com.kg.gettransfer.data.model.secondary.PriceConverted.class);
        long tableNativePtr = table.getNativePtr();
        PriceConvertedColumnInfo columnInfo = (PriceConvertedColumnInfo) realm.getSchema().getColumnInfo(com.kg.gettransfer.data.model.secondary.PriceConverted.class);
        long rowIndex = OsObject.createRow(table);
        cache.put(object, rowIndex);
        String realmGet$defaultCurrency = ((com_kg_gettransfer_data_model_secondary_PriceConvertedRealmProxyInterface) object).realmGet$defaultCurrency();
        if (realmGet$defaultCurrency != null) {
            Table.nativeSetString(tableNativePtr, columnInfo.defaultCurrencyIndex, rowIndex, realmGet$defaultCurrency, false);
        } else {
            Table.nativeSetNull(tableNativePtr, columnInfo.defaultCurrencyIndex, rowIndex, false);
        }
        String realmGet$preferredCurrency = ((com_kg_gettransfer_data_model_secondary_PriceConvertedRealmProxyInterface) object).realmGet$preferredCurrency();
        if (realmGet$preferredCurrency != null) {
            Table.nativeSetString(tableNativePtr, columnInfo.preferredCurrencyIndex, rowIndex, realmGet$preferredCurrency, false);
        } else {
            Table.nativeSetNull(tableNativePtr, columnInfo.preferredCurrencyIndex, rowIndex, false);
        }
        return rowIndex;
    }

    public static void insertOrUpdate(Realm realm, Iterator<? extends RealmModel> objects, Map<RealmModel,Long> cache) {
        Table table = realm.getTable(com.kg.gettransfer.data.model.secondary.PriceConverted.class);
        long tableNativePtr = table.getNativePtr();
        PriceConvertedColumnInfo columnInfo = (PriceConvertedColumnInfo) realm.getSchema().getColumnInfo(com.kg.gettransfer.data.model.secondary.PriceConverted.class);
        com.kg.gettransfer.data.model.secondary.PriceConverted object = null;
        while (objects.hasNext()) {
            object = (com.kg.gettransfer.data.model.secondary.PriceConverted) objects.next();
            if (cache.containsKey(object)) {
                continue;
            }
            if (object instanceof RealmObjectProxy && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm() != null && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm().getPath().equals(realm.getPath())) {
                cache.put(object, ((RealmObjectProxy) object).realmGet$proxyState().getRow$realm().getIndex());
                continue;
            }
            long rowIndex = OsObject.createRow(table);
            cache.put(object, rowIndex);
            String realmGet$defaultCurrency = ((com_kg_gettransfer_data_model_secondary_PriceConvertedRealmProxyInterface) object).realmGet$defaultCurrency();
            if (realmGet$defaultCurrency != null) {
                Table.nativeSetString(tableNativePtr, columnInfo.defaultCurrencyIndex, rowIndex, realmGet$defaultCurrency, false);
            } else {
                Table.nativeSetNull(tableNativePtr, columnInfo.defaultCurrencyIndex, rowIndex, false);
            }
            String realmGet$preferredCurrency = ((com_kg_gettransfer_data_model_secondary_PriceConvertedRealmProxyInterface) object).realmGet$preferredCurrency();
            if (realmGet$preferredCurrency != null) {
                Table.nativeSetString(tableNativePtr, columnInfo.preferredCurrencyIndex, rowIndex, realmGet$preferredCurrency, false);
            } else {
                Table.nativeSetNull(tableNativePtr, columnInfo.preferredCurrencyIndex, rowIndex, false);
            }
        }
    }

    public static com.kg.gettransfer.data.model.secondary.PriceConverted createDetachedCopy(com.kg.gettransfer.data.model.secondary.PriceConverted realmObject, int currentDepth, int maxDepth, Map<RealmModel, CacheData<RealmModel>> cache) {
        if (currentDepth > maxDepth || realmObject == null) {
            return null;
        }
        CacheData<RealmModel> cachedObject = cache.get(realmObject);
        com.kg.gettransfer.data.model.secondary.PriceConverted unmanagedObject;
        if (cachedObject == null) {
            unmanagedObject = new com.kg.gettransfer.data.model.secondary.PriceConverted();
            cache.put(realmObject, new RealmObjectProxy.CacheData<RealmModel>(currentDepth, unmanagedObject));
        } else {
            // Reuse cached object or recreate it because it was encountered at a lower depth.
            if (currentDepth >= cachedObject.minDepth) {
                return (com.kg.gettransfer.data.model.secondary.PriceConverted) cachedObject.object;
            }
            unmanagedObject = (com.kg.gettransfer.data.model.secondary.PriceConverted) cachedObject.object;
            cachedObject.minDepth = currentDepth;
        }
        com_kg_gettransfer_data_model_secondary_PriceConvertedRealmProxyInterface unmanagedCopy = (com_kg_gettransfer_data_model_secondary_PriceConvertedRealmProxyInterface) unmanagedObject;
        com_kg_gettransfer_data_model_secondary_PriceConvertedRealmProxyInterface realmSource = (com_kg_gettransfer_data_model_secondary_PriceConvertedRealmProxyInterface) realmObject;
        unmanagedCopy.realmSet$defaultCurrency(realmSource.realmGet$defaultCurrency());
        unmanagedCopy.realmSet$preferredCurrency(realmSource.realmGet$preferredCurrency());

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
        com_kg_gettransfer_data_model_secondary_PriceConvertedRealmProxy aPriceConverted = (com_kg_gettransfer_data_model_secondary_PriceConvertedRealmProxy)o;

        String path = proxyState.getRealm$realm().getPath();
        String otherPath = aPriceConverted.proxyState.getRealm$realm().getPath();
        if (path != null ? !path.equals(otherPath) : otherPath != null) return false;

        String tableName = proxyState.getRow$realm().getTable().getName();
        String otherTableName = aPriceConverted.proxyState.getRow$realm().getTable().getName();
        if (tableName != null ? !tableName.equals(otherTableName) : otherTableName != null) return false;

        if (proxyState.getRow$realm().getIndex() != aPriceConverted.proxyState.getRow$realm().getIndex()) return false;

        return true;
    }
}
