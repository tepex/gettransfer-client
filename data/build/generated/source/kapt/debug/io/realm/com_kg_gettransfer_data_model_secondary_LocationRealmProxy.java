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
public class com_kg_gettransfer_data_model_secondary_LocationRealmProxy extends com.kg.gettransfer.data.model.secondary.Location
    implements RealmObjectProxy, com_kg_gettransfer_data_model_secondary_LocationRealmProxyInterface {

    static final class LocationColumnInfo extends ColumnInfo {
        long nameIndex;
        long pointIndex;

        LocationColumnInfo(OsSchemaInfo schemaInfo) {
            super(2);
            OsObjectSchemaInfo objectSchemaInfo = schemaInfo.getObjectSchemaInfo("Location");
            this.nameIndex = addColumnDetails("name", "name", objectSchemaInfo);
            this.pointIndex = addColumnDetails("point", "point", objectSchemaInfo);
        }

        LocationColumnInfo(ColumnInfo src, boolean mutable) {
            super(src, mutable);
            copy(src, this);
        }

        @Override
        protected final ColumnInfo copy(boolean mutable) {
            return new LocationColumnInfo(this, mutable);
        }

        @Override
        protected final void copy(ColumnInfo rawSrc, ColumnInfo rawDst) {
            final LocationColumnInfo src = (LocationColumnInfo) rawSrc;
            final LocationColumnInfo dst = (LocationColumnInfo) rawDst;
            dst.nameIndex = src.nameIndex;
            dst.pointIndex = src.pointIndex;
        }
    }

    private static final OsObjectSchemaInfo expectedObjectSchemaInfo = createExpectedObjectSchemaInfo();

    private LocationColumnInfo columnInfo;
    private ProxyState<com.kg.gettransfer.data.model.secondary.Location> proxyState;

    com_kg_gettransfer_data_model_secondary_LocationRealmProxy() {
        proxyState.setConstructionFinished();
    }

    @Override
    public void realm$injectObjectContext() {
        if (this.proxyState != null) {
            return;
        }
        final BaseRealm.RealmObjectContext context = BaseRealm.objectContext.get();
        this.columnInfo = (LocationColumnInfo) context.getColumnInfo();
        this.proxyState = new ProxyState<com.kg.gettransfer.data.model.secondary.Location>(this);
        proxyState.setRealm$realm(context.getRealm());
        proxyState.setRow$realm(context.getRow());
        proxyState.setAcceptDefaultValue$realm(context.getAcceptDefaultValue());
        proxyState.setExcludeFields$realm(context.getExcludeFields());
    }

    @Override
    @SuppressWarnings("cast")
    public String realmGet$name() {
        proxyState.getRealm$realm().checkIfValid();
        return (java.lang.String) proxyState.getRow$realm().getString(columnInfo.nameIndex);
    }

    @Override
    public void realmSet$name(String value) {
        if (proxyState.isUnderConstruction()) {
            if (!proxyState.getAcceptDefaultValue$realm()) {
                return;
            }
            final Row row = proxyState.getRow$realm();
            if (value == null) {
                throw new IllegalArgumentException("Trying to set non-nullable field 'name' to null.");
            }
            row.getTable().setString(columnInfo.nameIndex, row.getIndex(), value, true);
            return;
        }

        proxyState.getRealm$realm().checkIfValid();
        if (value == null) {
            throw new IllegalArgumentException("Trying to set non-nullable field 'name' to null.");
        }
        proxyState.getRow$realm().setString(columnInfo.nameIndex, value);
    }

    @Override
    @SuppressWarnings("cast")
    public String realmGet$point() {
        proxyState.getRealm$realm().checkIfValid();
        return (java.lang.String) proxyState.getRow$realm().getString(columnInfo.pointIndex);
    }

    @Override
    public void realmSet$point(String value) {
        if (proxyState.isUnderConstruction()) {
            if (!proxyState.getAcceptDefaultValue$realm()) {
                return;
            }
            final Row row = proxyState.getRow$realm();
            if (value == null) {
                throw new IllegalArgumentException("Trying to set non-nullable field 'point' to null.");
            }
            row.getTable().setString(columnInfo.pointIndex, row.getIndex(), value, true);
            return;
        }

        proxyState.getRealm$realm().checkIfValid();
        if (value == null) {
            throw new IllegalArgumentException("Trying to set non-nullable field 'point' to null.");
        }
        proxyState.getRow$realm().setString(columnInfo.pointIndex, value);
    }

    private static OsObjectSchemaInfo createExpectedObjectSchemaInfo() {
        OsObjectSchemaInfo.Builder builder = new OsObjectSchemaInfo.Builder("Location", 2, 0);
        builder.addPersistedProperty("name", RealmFieldType.STRING, !Property.PRIMARY_KEY, !Property.INDEXED, Property.REQUIRED);
        builder.addPersistedProperty("point", RealmFieldType.STRING, !Property.PRIMARY_KEY, !Property.INDEXED, Property.REQUIRED);
        return builder.build();
    }

    public static OsObjectSchemaInfo getExpectedObjectSchemaInfo() {
        return expectedObjectSchemaInfo;
    }

    public static LocationColumnInfo createColumnInfo(OsSchemaInfo schemaInfo) {
        return new LocationColumnInfo(schemaInfo);
    }

    public static String getSimpleClassName() {
        return "Location";
    }

    public static final class ClassNameHelper {
        public static final String INTERNAL_CLASS_NAME = "Location";
    }

    @SuppressWarnings("cast")
    public static com.kg.gettransfer.data.model.secondary.Location createOrUpdateUsingJsonObject(Realm realm, JSONObject json, boolean update)
        throws JSONException {
        final List<String> excludeFields = Collections.<String> emptyList();
        com.kg.gettransfer.data.model.secondary.Location obj = realm.createObjectInternal(com.kg.gettransfer.data.model.secondary.Location.class, true, excludeFields);

        final com_kg_gettransfer_data_model_secondary_LocationRealmProxyInterface objProxy = (com_kg_gettransfer_data_model_secondary_LocationRealmProxyInterface) obj;
        if (json.has("name")) {
            if (json.isNull("name")) {
                objProxy.realmSet$name(null);
            } else {
                objProxy.realmSet$name((String) json.getString("name"));
            }
        }
        if (json.has("point")) {
            if (json.isNull("point")) {
                objProxy.realmSet$point(null);
            } else {
                objProxy.realmSet$point((String) json.getString("point"));
            }
        }
        return obj;
    }

    @SuppressWarnings("cast")
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static com.kg.gettransfer.data.model.secondary.Location createUsingJsonStream(Realm realm, JsonReader reader)
        throws IOException {
        final com.kg.gettransfer.data.model.secondary.Location obj = new com.kg.gettransfer.data.model.secondary.Location();
        final com_kg_gettransfer_data_model_secondary_LocationRealmProxyInterface objProxy = (com_kg_gettransfer_data_model_secondary_LocationRealmProxyInterface) obj;
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (false) {
            } else if (name.equals("name")) {
                if (reader.peek() != JsonToken.NULL) {
                    objProxy.realmSet$name((String) reader.nextString());
                } else {
                    reader.skipValue();
                    objProxy.realmSet$name(null);
                }
            } else if (name.equals("point")) {
                if (reader.peek() != JsonToken.NULL) {
                    objProxy.realmSet$point((String) reader.nextString());
                } else {
                    reader.skipValue();
                    objProxy.realmSet$point(null);
                }
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return realm.copyToRealm(obj);
    }

    public static com.kg.gettransfer.data.model.secondary.Location copyOrUpdate(Realm realm, com.kg.gettransfer.data.model.secondary.Location object, boolean update, Map<RealmModel,RealmObjectProxy> cache) {
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
            return (com.kg.gettransfer.data.model.secondary.Location) cachedRealmObject;
        }

        return copy(realm, object, update, cache);
    }

    public static com.kg.gettransfer.data.model.secondary.Location copy(Realm realm, com.kg.gettransfer.data.model.secondary.Location newObject, boolean update, Map<RealmModel,RealmObjectProxy> cache) {
        RealmObjectProxy cachedRealmObject = cache.get(newObject);
        if (cachedRealmObject != null) {
            return (com.kg.gettransfer.data.model.secondary.Location) cachedRealmObject;
        }

        // rejecting default values to avoid creating unexpected objects from RealmModel/RealmList fields.
        com.kg.gettransfer.data.model.secondary.Location realmObject = realm.createObjectInternal(com.kg.gettransfer.data.model.secondary.Location.class, false, Collections.<String>emptyList());
        cache.put(newObject, (RealmObjectProxy) realmObject);

        com_kg_gettransfer_data_model_secondary_LocationRealmProxyInterface realmObjectSource = (com_kg_gettransfer_data_model_secondary_LocationRealmProxyInterface) newObject;
        com_kg_gettransfer_data_model_secondary_LocationRealmProxyInterface realmObjectCopy = (com_kg_gettransfer_data_model_secondary_LocationRealmProxyInterface) realmObject;

        realmObjectCopy.realmSet$name(realmObjectSource.realmGet$name());
        realmObjectCopy.realmSet$point(realmObjectSource.realmGet$point());
        return realmObject;
    }

    public static long insert(Realm realm, com.kg.gettransfer.data.model.secondary.Location object, Map<RealmModel,Long> cache) {
        if (object instanceof RealmObjectProxy && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm() != null && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm().getPath().equals(realm.getPath())) {
            return ((RealmObjectProxy) object).realmGet$proxyState().getRow$realm().getIndex();
        }
        Table table = realm.getTable(com.kg.gettransfer.data.model.secondary.Location.class);
        long tableNativePtr = table.getNativePtr();
        LocationColumnInfo columnInfo = (LocationColumnInfo) realm.getSchema().getColumnInfo(com.kg.gettransfer.data.model.secondary.Location.class);
        long rowIndex = OsObject.createRow(table);
        cache.put(object, rowIndex);
        String realmGet$name = ((com_kg_gettransfer_data_model_secondary_LocationRealmProxyInterface) object).realmGet$name();
        if (realmGet$name != null) {
            Table.nativeSetString(tableNativePtr, columnInfo.nameIndex, rowIndex, realmGet$name, false);
        }
        String realmGet$point = ((com_kg_gettransfer_data_model_secondary_LocationRealmProxyInterface) object).realmGet$point();
        if (realmGet$point != null) {
            Table.nativeSetString(tableNativePtr, columnInfo.pointIndex, rowIndex, realmGet$point, false);
        }
        return rowIndex;
    }

    public static void insert(Realm realm, Iterator<? extends RealmModel> objects, Map<RealmModel,Long> cache) {
        Table table = realm.getTable(com.kg.gettransfer.data.model.secondary.Location.class);
        long tableNativePtr = table.getNativePtr();
        LocationColumnInfo columnInfo = (LocationColumnInfo) realm.getSchema().getColumnInfo(com.kg.gettransfer.data.model.secondary.Location.class);
        com.kg.gettransfer.data.model.secondary.Location object = null;
        while (objects.hasNext()) {
            object = (com.kg.gettransfer.data.model.secondary.Location) objects.next();
            if (cache.containsKey(object)) {
                continue;
            }
            if (object instanceof RealmObjectProxy && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm() != null && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm().getPath().equals(realm.getPath())) {
                cache.put(object, ((RealmObjectProxy) object).realmGet$proxyState().getRow$realm().getIndex());
                continue;
            }
            long rowIndex = OsObject.createRow(table);
            cache.put(object, rowIndex);
            String realmGet$name = ((com_kg_gettransfer_data_model_secondary_LocationRealmProxyInterface) object).realmGet$name();
            if (realmGet$name != null) {
                Table.nativeSetString(tableNativePtr, columnInfo.nameIndex, rowIndex, realmGet$name, false);
            }
            String realmGet$point = ((com_kg_gettransfer_data_model_secondary_LocationRealmProxyInterface) object).realmGet$point();
            if (realmGet$point != null) {
                Table.nativeSetString(tableNativePtr, columnInfo.pointIndex, rowIndex, realmGet$point, false);
            }
        }
    }

    public static long insertOrUpdate(Realm realm, com.kg.gettransfer.data.model.secondary.Location object, Map<RealmModel,Long> cache) {
        if (object instanceof RealmObjectProxy && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm() != null && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm().getPath().equals(realm.getPath())) {
            return ((RealmObjectProxy) object).realmGet$proxyState().getRow$realm().getIndex();
        }
        Table table = realm.getTable(com.kg.gettransfer.data.model.secondary.Location.class);
        long tableNativePtr = table.getNativePtr();
        LocationColumnInfo columnInfo = (LocationColumnInfo) realm.getSchema().getColumnInfo(com.kg.gettransfer.data.model.secondary.Location.class);
        long rowIndex = OsObject.createRow(table);
        cache.put(object, rowIndex);
        String realmGet$name = ((com_kg_gettransfer_data_model_secondary_LocationRealmProxyInterface) object).realmGet$name();
        if (realmGet$name != null) {
            Table.nativeSetString(tableNativePtr, columnInfo.nameIndex, rowIndex, realmGet$name, false);
        } else {
            Table.nativeSetNull(tableNativePtr, columnInfo.nameIndex, rowIndex, false);
        }
        String realmGet$point = ((com_kg_gettransfer_data_model_secondary_LocationRealmProxyInterface) object).realmGet$point();
        if (realmGet$point != null) {
            Table.nativeSetString(tableNativePtr, columnInfo.pointIndex, rowIndex, realmGet$point, false);
        } else {
            Table.nativeSetNull(tableNativePtr, columnInfo.pointIndex, rowIndex, false);
        }
        return rowIndex;
    }

    public static void insertOrUpdate(Realm realm, Iterator<? extends RealmModel> objects, Map<RealmModel,Long> cache) {
        Table table = realm.getTable(com.kg.gettransfer.data.model.secondary.Location.class);
        long tableNativePtr = table.getNativePtr();
        LocationColumnInfo columnInfo = (LocationColumnInfo) realm.getSchema().getColumnInfo(com.kg.gettransfer.data.model.secondary.Location.class);
        com.kg.gettransfer.data.model.secondary.Location object = null;
        while (objects.hasNext()) {
            object = (com.kg.gettransfer.data.model.secondary.Location) objects.next();
            if (cache.containsKey(object)) {
                continue;
            }
            if (object instanceof RealmObjectProxy && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm() != null && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm().getPath().equals(realm.getPath())) {
                cache.put(object, ((RealmObjectProxy) object).realmGet$proxyState().getRow$realm().getIndex());
                continue;
            }
            long rowIndex = OsObject.createRow(table);
            cache.put(object, rowIndex);
            String realmGet$name = ((com_kg_gettransfer_data_model_secondary_LocationRealmProxyInterface) object).realmGet$name();
            if (realmGet$name != null) {
                Table.nativeSetString(tableNativePtr, columnInfo.nameIndex, rowIndex, realmGet$name, false);
            } else {
                Table.nativeSetNull(tableNativePtr, columnInfo.nameIndex, rowIndex, false);
            }
            String realmGet$point = ((com_kg_gettransfer_data_model_secondary_LocationRealmProxyInterface) object).realmGet$point();
            if (realmGet$point != null) {
                Table.nativeSetString(tableNativePtr, columnInfo.pointIndex, rowIndex, realmGet$point, false);
            } else {
                Table.nativeSetNull(tableNativePtr, columnInfo.pointIndex, rowIndex, false);
            }
        }
    }

    public static com.kg.gettransfer.data.model.secondary.Location createDetachedCopy(com.kg.gettransfer.data.model.secondary.Location realmObject, int currentDepth, int maxDepth, Map<RealmModel, CacheData<RealmModel>> cache) {
        if (currentDepth > maxDepth || realmObject == null) {
            return null;
        }
        CacheData<RealmModel> cachedObject = cache.get(realmObject);
        com.kg.gettransfer.data.model.secondary.Location unmanagedObject;
        if (cachedObject == null) {
            unmanagedObject = new com.kg.gettransfer.data.model.secondary.Location();
            cache.put(realmObject, new RealmObjectProxy.CacheData<RealmModel>(currentDepth, unmanagedObject));
        } else {
            // Reuse cached object or recreate it because it was encountered at a lower depth.
            if (currentDepth >= cachedObject.minDepth) {
                return (com.kg.gettransfer.data.model.secondary.Location) cachedObject.object;
            }
            unmanagedObject = (com.kg.gettransfer.data.model.secondary.Location) cachedObject.object;
            cachedObject.minDepth = currentDepth;
        }
        com_kg_gettransfer_data_model_secondary_LocationRealmProxyInterface unmanagedCopy = (com_kg_gettransfer_data_model_secondary_LocationRealmProxyInterface) unmanagedObject;
        com_kg_gettransfer_data_model_secondary_LocationRealmProxyInterface realmSource = (com_kg_gettransfer_data_model_secondary_LocationRealmProxyInterface) realmObject;
        unmanagedCopy.realmSet$name(realmSource.realmGet$name());
        unmanagedCopy.realmSet$point(realmSource.realmGet$point());

        return unmanagedObject;
    }

    @Override
    @SuppressWarnings("ArrayToString")
    public String toString() {
        if (!RealmObject.isValid(this)) {
            return "Invalid object";
        }
        StringBuilder stringBuilder = new StringBuilder("Location = proxy[");
        stringBuilder.append("{name:");
        stringBuilder.append(realmGet$name());
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{point:");
        stringBuilder.append(realmGet$point());
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
        com_kg_gettransfer_data_model_secondary_LocationRealmProxy aLocation = (com_kg_gettransfer_data_model_secondary_LocationRealmProxy)o;

        String path = proxyState.getRealm$realm().getPath();
        String otherPath = aLocation.proxyState.getRealm$realm().getPath();
        if (path != null ? !path.equals(otherPath) : otherPath != null) return false;

        String tableName = proxyState.getRow$realm().getTable().getName();
        String otherTableName = aLocation.proxyState.getRow$realm().getTable().getName();
        if (tableName != null ? !tableName.equals(otherTableName) : otherTableName != null) return false;

        if (proxyState.getRow$realm().getIndex() != aLocation.proxyState.getRow$realm().getIndex()) return false;

        return true;
    }
}
