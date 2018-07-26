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
public class com_kg_gettransfer_data_model_secondary_ZonedDateRealmProxy extends com.kg.gettransfer.data.model.secondary.ZonedDate
    implements RealmObjectProxy, com_kg_gettransfer_data_model_secondary_ZonedDateRealmProxyInterface {

    static final class ZonedDateColumnInfo extends ColumnInfo {
        long dateIndex;
        long zoneStringIndex;

        ZonedDateColumnInfo(OsSchemaInfo schemaInfo) {
            super(2);
            OsObjectSchemaInfo objectSchemaInfo = schemaInfo.getObjectSchemaInfo("ZonedDate");
            this.dateIndex = addColumnDetails("date", "date", objectSchemaInfo);
            this.zoneStringIndex = addColumnDetails("zoneString", "zoneString", objectSchemaInfo);
        }

        ZonedDateColumnInfo(ColumnInfo src, boolean mutable) {
            super(src, mutable);
            copy(src, this);
        }

        @Override
        protected final ColumnInfo copy(boolean mutable) {
            return new ZonedDateColumnInfo(this, mutable);
        }

        @Override
        protected final void copy(ColumnInfo rawSrc, ColumnInfo rawDst) {
            final ZonedDateColumnInfo src = (ZonedDateColumnInfo) rawSrc;
            final ZonedDateColumnInfo dst = (ZonedDateColumnInfo) rawDst;
            dst.dateIndex = src.dateIndex;
            dst.zoneStringIndex = src.zoneStringIndex;
        }
    }

    private static final OsObjectSchemaInfo expectedObjectSchemaInfo = createExpectedObjectSchemaInfo();

    private ZonedDateColumnInfo columnInfo;
    private ProxyState<com.kg.gettransfer.data.model.secondary.ZonedDate> proxyState;

    com_kg_gettransfer_data_model_secondary_ZonedDateRealmProxy() {
        proxyState.setConstructionFinished();
    }

    @Override
    public void realm$injectObjectContext() {
        if (this.proxyState != null) {
            return;
        }
        final BaseRealm.RealmObjectContext context = BaseRealm.objectContext.get();
        this.columnInfo = (ZonedDateColumnInfo) context.getColumnInfo();
        this.proxyState = new ProxyState<com.kg.gettransfer.data.model.secondary.ZonedDate>(this);
        proxyState.setRealm$realm(context.getRealm());
        proxyState.setRow$realm(context.getRow());
        proxyState.setAcceptDefaultValue$realm(context.getAcceptDefaultValue());
        proxyState.setExcludeFields$realm(context.getExcludeFields());
    }

    @Override
    @SuppressWarnings("cast")
    public Date realmGet$date() {
        proxyState.getRealm$realm().checkIfValid();
        return (java.util.Date) proxyState.getRow$realm().getDate(columnInfo.dateIndex);
    }

    @Override
    public void realmSet$date(Date value) {
        if (proxyState.isUnderConstruction()) {
            if (!proxyState.getAcceptDefaultValue$realm()) {
                return;
            }
            final Row row = proxyState.getRow$realm();
            if (value == null) {
                throw new IllegalArgumentException("Trying to set non-nullable field 'date' to null.");
            }
            row.getTable().setDate(columnInfo.dateIndex, row.getIndex(), value, true);
            return;
        }

        proxyState.getRealm$realm().checkIfValid();
        if (value == null) {
            throw new IllegalArgumentException("Trying to set non-nullable field 'date' to null.");
        }
        proxyState.getRow$realm().setDate(columnInfo.dateIndex, value);
    }

    @Override
    @SuppressWarnings("cast")
    public String realmGet$zoneString() {
        proxyState.getRealm$realm().checkIfValid();
        return (java.lang.String) proxyState.getRow$realm().getString(columnInfo.zoneStringIndex);
    }

    @Override
    public void realmSet$zoneString(String value) {
        if (proxyState.isUnderConstruction()) {
            if (!proxyState.getAcceptDefaultValue$realm()) {
                return;
            }
            final Row row = proxyState.getRow$realm();
            if (value == null) {
                row.getTable().setNull(columnInfo.zoneStringIndex, row.getIndex(), true);
                return;
            }
            row.getTable().setString(columnInfo.zoneStringIndex, row.getIndex(), value, true);
            return;
        }

        proxyState.getRealm$realm().checkIfValid();
        if (value == null) {
            proxyState.getRow$realm().setNull(columnInfo.zoneStringIndex);
            return;
        }
        proxyState.getRow$realm().setString(columnInfo.zoneStringIndex, value);
    }

    private static OsObjectSchemaInfo createExpectedObjectSchemaInfo() {
        OsObjectSchemaInfo.Builder builder = new OsObjectSchemaInfo.Builder("ZonedDate", 2, 0);
        builder.addPersistedProperty("date", RealmFieldType.DATE, !Property.PRIMARY_KEY, !Property.INDEXED, Property.REQUIRED);
        builder.addPersistedProperty("zoneString", RealmFieldType.STRING, !Property.PRIMARY_KEY, !Property.INDEXED, !Property.REQUIRED);
        return builder.build();
    }

    public static OsObjectSchemaInfo getExpectedObjectSchemaInfo() {
        return expectedObjectSchemaInfo;
    }

    public static ZonedDateColumnInfo createColumnInfo(OsSchemaInfo schemaInfo) {
        return new ZonedDateColumnInfo(schemaInfo);
    }

    public static String getSimpleClassName() {
        return "ZonedDate";
    }

    public static final class ClassNameHelper {
        public static final String INTERNAL_CLASS_NAME = "ZonedDate";
    }

    @SuppressWarnings("cast")
    public static com.kg.gettransfer.data.model.secondary.ZonedDate createOrUpdateUsingJsonObject(Realm realm, JSONObject json, boolean update)
        throws JSONException {
        final List<String> excludeFields = Collections.<String> emptyList();
        com.kg.gettransfer.data.model.secondary.ZonedDate obj = realm.createObjectInternal(com.kg.gettransfer.data.model.secondary.ZonedDate.class, true, excludeFields);

        final com_kg_gettransfer_data_model_secondary_ZonedDateRealmProxyInterface objProxy = (com_kg_gettransfer_data_model_secondary_ZonedDateRealmProxyInterface) obj;
        if (json.has("date")) {
            if (json.isNull("date")) {
                objProxy.realmSet$date(null);
            } else {
                Object timestamp = json.get("date");
                if (timestamp instanceof String) {
                    objProxy.realmSet$date(JsonUtils.stringToDate((String) timestamp));
                } else {
                    objProxy.realmSet$date(new Date(json.getLong("date")));
                }
            }
        }
        if (json.has("zoneString")) {
            if (json.isNull("zoneString")) {
                objProxy.realmSet$zoneString(null);
            } else {
                objProxy.realmSet$zoneString((String) json.getString("zoneString"));
            }
        }
        return obj;
    }

    @SuppressWarnings("cast")
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static com.kg.gettransfer.data.model.secondary.ZonedDate createUsingJsonStream(Realm realm, JsonReader reader)
        throws IOException {
        final com.kg.gettransfer.data.model.secondary.ZonedDate obj = new com.kg.gettransfer.data.model.secondary.ZonedDate();
        final com_kg_gettransfer_data_model_secondary_ZonedDateRealmProxyInterface objProxy = (com_kg_gettransfer_data_model_secondary_ZonedDateRealmProxyInterface) obj;
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (false) {
            } else if (name.equals("date")) {
                if (reader.peek() == JsonToken.NULL) {
                    reader.skipValue();
                    objProxy.realmSet$date(null);
                } else if (reader.peek() == JsonToken.NUMBER) {
                    long timestamp = reader.nextLong();
                    if (timestamp > -1) {
                        objProxy.realmSet$date(new Date(timestamp));
                    }
                } else {
                    objProxy.realmSet$date(JsonUtils.stringToDate(reader.nextString()));
                }
            } else if (name.equals("zoneString")) {
                if (reader.peek() != JsonToken.NULL) {
                    objProxy.realmSet$zoneString((String) reader.nextString());
                } else {
                    reader.skipValue();
                    objProxy.realmSet$zoneString(null);
                }
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return realm.copyToRealm(obj);
    }

    public static com.kg.gettransfer.data.model.secondary.ZonedDate copyOrUpdate(Realm realm, com.kg.gettransfer.data.model.secondary.ZonedDate object, boolean update, Map<RealmModel,RealmObjectProxy> cache) {
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
            return (com.kg.gettransfer.data.model.secondary.ZonedDate) cachedRealmObject;
        }

        return copy(realm, object, update, cache);
    }

    public static com.kg.gettransfer.data.model.secondary.ZonedDate copy(Realm realm, com.kg.gettransfer.data.model.secondary.ZonedDate newObject, boolean update, Map<RealmModel,RealmObjectProxy> cache) {
        RealmObjectProxy cachedRealmObject = cache.get(newObject);
        if (cachedRealmObject != null) {
            return (com.kg.gettransfer.data.model.secondary.ZonedDate) cachedRealmObject;
        }

        // rejecting default values to avoid creating unexpected objects from RealmModel/RealmList fields.
        com.kg.gettransfer.data.model.secondary.ZonedDate realmObject = realm.createObjectInternal(com.kg.gettransfer.data.model.secondary.ZonedDate.class, false, Collections.<String>emptyList());
        cache.put(newObject, (RealmObjectProxy) realmObject);

        com_kg_gettransfer_data_model_secondary_ZonedDateRealmProxyInterface realmObjectSource = (com_kg_gettransfer_data_model_secondary_ZonedDateRealmProxyInterface) newObject;
        com_kg_gettransfer_data_model_secondary_ZonedDateRealmProxyInterface realmObjectCopy = (com_kg_gettransfer_data_model_secondary_ZonedDateRealmProxyInterface) realmObject;

        realmObjectCopy.realmSet$date(realmObjectSource.realmGet$date());
        realmObjectCopy.realmSet$zoneString(realmObjectSource.realmGet$zoneString());
        return realmObject;
    }

    public static long insert(Realm realm, com.kg.gettransfer.data.model.secondary.ZonedDate object, Map<RealmModel,Long> cache) {
        if (object instanceof RealmObjectProxy && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm() != null && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm().getPath().equals(realm.getPath())) {
            return ((RealmObjectProxy) object).realmGet$proxyState().getRow$realm().getIndex();
        }
        Table table = realm.getTable(com.kg.gettransfer.data.model.secondary.ZonedDate.class);
        long tableNativePtr = table.getNativePtr();
        ZonedDateColumnInfo columnInfo = (ZonedDateColumnInfo) realm.getSchema().getColumnInfo(com.kg.gettransfer.data.model.secondary.ZonedDate.class);
        long rowIndex = OsObject.createRow(table);
        cache.put(object, rowIndex);
        java.util.Date realmGet$date = ((com_kg_gettransfer_data_model_secondary_ZonedDateRealmProxyInterface) object).realmGet$date();
        if (realmGet$date != null) {
            Table.nativeSetTimestamp(tableNativePtr, columnInfo.dateIndex, rowIndex, realmGet$date.getTime(), false);
        }
        String realmGet$zoneString = ((com_kg_gettransfer_data_model_secondary_ZonedDateRealmProxyInterface) object).realmGet$zoneString();
        if (realmGet$zoneString != null) {
            Table.nativeSetString(tableNativePtr, columnInfo.zoneStringIndex, rowIndex, realmGet$zoneString, false);
        }
        return rowIndex;
    }

    public static void insert(Realm realm, Iterator<? extends RealmModel> objects, Map<RealmModel,Long> cache) {
        Table table = realm.getTable(com.kg.gettransfer.data.model.secondary.ZonedDate.class);
        long tableNativePtr = table.getNativePtr();
        ZonedDateColumnInfo columnInfo = (ZonedDateColumnInfo) realm.getSchema().getColumnInfo(com.kg.gettransfer.data.model.secondary.ZonedDate.class);
        com.kg.gettransfer.data.model.secondary.ZonedDate object = null;
        while (objects.hasNext()) {
            object = (com.kg.gettransfer.data.model.secondary.ZonedDate) objects.next();
            if (cache.containsKey(object)) {
                continue;
            }
            if (object instanceof RealmObjectProxy && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm() != null && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm().getPath().equals(realm.getPath())) {
                cache.put(object, ((RealmObjectProxy) object).realmGet$proxyState().getRow$realm().getIndex());
                continue;
            }
            long rowIndex = OsObject.createRow(table);
            cache.put(object, rowIndex);
            java.util.Date realmGet$date = ((com_kg_gettransfer_data_model_secondary_ZonedDateRealmProxyInterface) object).realmGet$date();
            if (realmGet$date != null) {
                Table.nativeSetTimestamp(tableNativePtr, columnInfo.dateIndex, rowIndex, realmGet$date.getTime(), false);
            }
            String realmGet$zoneString = ((com_kg_gettransfer_data_model_secondary_ZonedDateRealmProxyInterface) object).realmGet$zoneString();
            if (realmGet$zoneString != null) {
                Table.nativeSetString(tableNativePtr, columnInfo.zoneStringIndex, rowIndex, realmGet$zoneString, false);
            }
        }
    }

    public static long insertOrUpdate(Realm realm, com.kg.gettransfer.data.model.secondary.ZonedDate object, Map<RealmModel,Long> cache) {
        if (object instanceof RealmObjectProxy && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm() != null && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm().getPath().equals(realm.getPath())) {
            return ((RealmObjectProxy) object).realmGet$proxyState().getRow$realm().getIndex();
        }
        Table table = realm.getTable(com.kg.gettransfer.data.model.secondary.ZonedDate.class);
        long tableNativePtr = table.getNativePtr();
        ZonedDateColumnInfo columnInfo = (ZonedDateColumnInfo) realm.getSchema().getColumnInfo(com.kg.gettransfer.data.model.secondary.ZonedDate.class);
        long rowIndex = OsObject.createRow(table);
        cache.put(object, rowIndex);
        java.util.Date realmGet$date = ((com_kg_gettransfer_data_model_secondary_ZonedDateRealmProxyInterface) object).realmGet$date();
        if (realmGet$date != null) {
            Table.nativeSetTimestamp(tableNativePtr, columnInfo.dateIndex, rowIndex, realmGet$date.getTime(), false);
        } else {
            Table.nativeSetNull(tableNativePtr, columnInfo.dateIndex, rowIndex, false);
        }
        String realmGet$zoneString = ((com_kg_gettransfer_data_model_secondary_ZonedDateRealmProxyInterface) object).realmGet$zoneString();
        if (realmGet$zoneString != null) {
            Table.nativeSetString(tableNativePtr, columnInfo.zoneStringIndex, rowIndex, realmGet$zoneString, false);
        } else {
            Table.nativeSetNull(tableNativePtr, columnInfo.zoneStringIndex, rowIndex, false);
        }
        return rowIndex;
    }

    public static void insertOrUpdate(Realm realm, Iterator<? extends RealmModel> objects, Map<RealmModel,Long> cache) {
        Table table = realm.getTable(com.kg.gettransfer.data.model.secondary.ZonedDate.class);
        long tableNativePtr = table.getNativePtr();
        ZonedDateColumnInfo columnInfo = (ZonedDateColumnInfo) realm.getSchema().getColumnInfo(com.kg.gettransfer.data.model.secondary.ZonedDate.class);
        com.kg.gettransfer.data.model.secondary.ZonedDate object = null;
        while (objects.hasNext()) {
            object = (com.kg.gettransfer.data.model.secondary.ZonedDate) objects.next();
            if (cache.containsKey(object)) {
                continue;
            }
            if (object instanceof RealmObjectProxy && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm() != null && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm().getPath().equals(realm.getPath())) {
                cache.put(object, ((RealmObjectProxy) object).realmGet$proxyState().getRow$realm().getIndex());
                continue;
            }
            long rowIndex = OsObject.createRow(table);
            cache.put(object, rowIndex);
            java.util.Date realmGet$date = ((com_kg_gettransfer_data_model_secondary_ZonedDateRealmProxyInterface) object).realmGet$date();
            if (realmGet$date != null) {
                Table.nativeSetTimestamp(tableNativePtr, columnInfo.dateIndex, rowIndex, realmGet$date.getTime(), false);
            } else {
                Table.nativeSetNull(tableNativePtr, columnInfo.dateIndex, rowIndex, false);
            }
            String realmGet$zoneString = ((com_kg_gettransfer_data_model_secondary_ZonedDateRealmProxyInterface) object).realmGet$zoneString();
            if (realmGet$zoneString != null) {
                Table.nativeSetString(tableNativePtr, columnInfo.zoneStringIndex, rowIndex, realmGet$zoneString, false);
            } else {
                Table.nativeSetNull(tableNativePtr, columnInfo.zoneStringIndex, rowIndex, false);
            }
        }
    }

    public static com.kg.gettransfer.data.model.secondary.ZonedDate createDetachedCopy(com.kg.gettransfer.data.model.secondary.ZonedDate realmObject, int currentDepth, int maxDepth, Map<RealmModel, CacheData<RealmModel>> cache) {
        if (currentDepth > maxDepth || realmObject == null) {
            return null;
        }
        CacheData<RealmModel> cachedObject = cache.get(realmObject);
        com.kg.gettransfer.data.model.secondary.ZonedDate unmanagedObject;
        if (cachedObject == null) {
            unmanagedObject = new com.kg.gettransfer.data.model.secondary.ZonedDate();
            cache.put(realmObject, new RealmObjectProxy.CacheData<RealmModel>(currentDepth, unmanagedObject));
        } else {
            // Reuse cached object or recreate it because it was encountered at a lower depth.
            if (currentDepth >= cachedObject.minDepth) {
                return (com.kg.gettransfer.data.model.secondary.ZonedDate) cachedObject.object;
            }
            unmanagedObject = (com.kg.gettransfer.data.model.secondary.ZonedDate) cachedObject.object;
            cachedObject.minDepth = currentDepth;
        }
        com_kg_gettransfer_data_model_secondary_ZonedDateRealmProxyInterface unmanagedCopy = (com_kg_gettransfer_data_model_secondary_ZonedDateRealmProxyInterface) unmanagedObject;
        com_kg_gettransfer_data_model_secondary_ZonedDateRealmProxyInterface realmSource = (com_kg_gettransfer_data_model_secondary_ZonedDateRealmProxyInterface) realmObject;
        unmanagedCopy.realmSet$date(realmSource.realmGet$date());
        unmanagedCopy.realmSet$zoneString(realmSource.realmGet$zoneString());

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
        com_kg_gettransfer_data_model_secondary_ZonedDateRealmProxy aZonedDate = (com_kg_gettransfer_data_model_secondary_ZonedDateRealmProxy)o;

        String path = proxyState.getRealm$realm().getPath();
        String otherPath = aZonedDate.proxyState.getRealm$realm().getPath();
        if (path != null ? !path.equals(otherPath) : otherPath != null) return false;

        String tableName = proxyState.getRow$realm().getTable().getName();
        String otherTableName = aZonedDate.proxyState.getRow$realm().getTable().getName();
        if (tableName != null ? !tableName.equals(otherTableName) : otherTableName != null) return false;

        if (proxyState.getRow$realm().getIndex() != aZonedDate.proxyState.getRow$realm().getIndex()) return false;

        return true;
    }
}
