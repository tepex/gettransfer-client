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
public class com_kg_gettransfer_data_model_VehicleRealmProxy extends com.kg.gettransfer.data.model.Vehicle
    implements RealmObjectProxy, com_kg_gettransfer_data_model_VehicleRealmProxyInterface {

    static final class VehicleColumnInfo extends ColumnInfo {
        long nameIndex;
        long yearIndex;
        long transportTypeIDIndex;

        VehicleColumnInfo(OsSchemaInfo schemaInfo) {
            super(3);
            OsObjectSchemaInfo objectSchemaInfo = schemaInfo.getObjectSchemaInfo("Vehicle");
            this.nameIndex = addColumnDetails("name", "name", objectSchemaInfo);
            this.yearIndex = addColumnDetails("year", "year", objectSchemaInfo);
            this.transportTypeIDIndex = addColumnDetails("transportTypeID", "transportTypeID", objectSchemaInfo);
        }

        VehicleColumnInfo(ColumnInfo src, boolean mutable) {
            super(src, mutable);
            copy(src, this);
        }

        @Override
        protected final ColumnInfo copy(boolean mutable) {
            return new VehicleColumnInfo(this, mutable);
        }

        @Override
        protected final void copy(ColumnInfo rawSrc, ColumnInfo rawDst) {
            final VehicleColumnInfo src = (VehicleColumnInfo) rawSrc;
            final VehicleColumnInfo dst = (VehicleColumnInfo) rawDst;
            dst.nameIndex = src.nameIndex;
            dst.yearIndex = src.yearIndex;
            dst.transportTypeIDIndex = src.transportTypeIDIndex;
        }
    }

    private static final OsObjectSchemaInfo expectedObjectSchemaInfo = createExpectedObjectSchemaInfo();

    private VehicleColumnInfo columnInfo;
    private ProxyState<com.kg.gettransfer.data.model.Vehicle> proxyState;

    com_kg_gettransfer_data_model_VehicleRealmProxy() {
        proxyState.setConstructionFinished();
    }

    @Override
    public void realm$injectObjectContext() {
        if (this.proxyState != null) {
            return;
        }
        final BaseRealm.RealmObjectContext context = BaseRealm.objectContext.get();
        this.columnInfo = (VehicleColumnInfo) context.getColumnInfo();
        this.proxyState = new ProxyState<com.kg.gettransfer.data.model.Vehicle>(this);
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
                row.getTable().setNull(columnInfo.nameIndex, row.getIndex(), true);
                return;
            }
            row.getTable().setString(columnInfo.nameIndex, row.getIndex(), value, true);
            return;
        }

        proxyState.getRealm$realm().checkIfValid();
        if (value == null) {
            proxyState.getRow$realm().setNull(columnInfo.nameIndex);
            return;
        }
        proxyState.getRow$realm().setString(columnInfo.nameIndex, value);
    }

    @Override
    @SuppressWarnings("cast")
    public int realmGet$year() {
        proxyState.getRealm$realm().checkIfValid();
        return (int) proxyState.getRow$realm().getLong(columnInfo.yearIndex);
    }

    @Override
    public void realmSet$year(int value) {
        if (proxyState.isUnderConstruction()) {
            if (!proxyState.getAcceptDefaultValue$realm()) {
                return;
            }
            final Row row = proxyState.getRow$realm();
            row.getTable().setLong(columnInfo.yearIndex, row.getIndex(), value, true);
            return;
        }

        proxyState.getRealm$realm().checkIfValid();
        proxyState.getRow$realm().setLong(columnInfo.yearIndex, value);
    }

    @Override
    @SuppressWarnings("cast")
    public String realmGet$transportTypeID() {
        proxyState.getRealm$realm().checkIfValid();
        return (java.lang.String) proxyState.getRow$realm().getString(columnInfo.transportTypeIDIndex);
    }

    @Override
    public void realmSet$transportTypeID(String value) {
        if (proxyState.isUnderConstruction()) {
            if (!proxyState.getAcceptDefaultValue$realm()) {
                return;
            }
            final Row row = proxyState.getRow$realm();
            if (value == null) {
                row.getTable().setNull(columnInfo.transportTypeIDIndex, row.getIndex(), true);
                return;
            }
            row.getTable().setString(columnInfo.transportTypeIDIndex, row.getIndex(), value, true);
            return;
        }

        proxyState.getRealm$realm().checkIfValid();
        if (value == null) {
            proxyState.getRow$realm().setNull(columnInfo.transportTypeIDIndex);
            return;
        }
        proxyState.getRow$realm().setString(columnInfo.transportTypeIDIndex, value);
    }

    private static OsObjectSchemaInfo createExpectedObjectSchemaInfo() {
        OsObjectSchemaInfo.Builder builder = new OsObjectSchemaInfo.Builder("Vehicle", 3, 0);
        builder.addPersistedProperty("name", RealmFieldType.STRING, !Property.PRIMARY_KEY, !Property.INDEXED, !Property.REQUIRED);
        builder.addPersistedProperty("year", RealmFieldType.INTEGER, !Property.PRIMARY_KEY, !Property.INDEXED, Property.REQUIRED);
        builder.addPersistedProperty("transportTypeID", RealmFieldType.STRING, !Property.PRIMARY_KEY, !Property.INDEXED, !Property.REQUIRED);
        return builder.build();
    }

    public static OsObjectSchemaInfo getExpectedObjectSchemaInfo() {
        return expectedObjectSchemaInfo;
    }

    public static VehicleColumnInfo createColumnInfo(OsSchemaInfo schemaInfo) {
        return new VehicleColumnInfo(schemaInfo);
    }

    public static String getSimpleClassName() {
        return "Vehicle";
    }

    public static final class ClassNameHelper {
        public static final String INTERNAL_CLASS_NAME = "Vehicle";
    }

    @SuppressWarnings("cast")
    public static com.kg.gettransfer.data.model.Vehicle createOrUpdateUsingJsonObject(Realm realm, JSONObject json, boolean update)
        throws JSONException {
        final List<String> excludeFields = Collections.<String> emptyList();
        com.kg.gettransfer.data.model.Vehicle obj = realm.createObjectInternal(com.kg.gettransfer.data.model.Vehicle.class, true, excludeFields);

        final com_kg_gettransfer_data_model_VehicleRealmProxyInterface objProxy = (com_kg_gettransfer_data_model_VehicleRealmProxyInterface) obj;
        if (json.has("name")) {
            if (json.isNull("name")) {
                objProxy.realmSet$name(null);
            } else {
                objProxy.realmSet$name((String) json.getString("name"));
            }
        }
        if (json.has("year")) {
            if (json.isNull("year")) {
                throw new IllegalArgumentException("Trying to set non-nullable field 'year' to null.");
            } else {
                objProxy.realmSet$year((int) json.getInt("year"));
            }
        }
        if (json.has("transportTypeID")) {
            if (json.isNull("transportTypeID")) {
                objProxy.realmSet$transportTypeID(null);
            } else {
                objProxy.realmSet$transportTypeID((String) json.getString("transportTypeID"));
            }
        }
        return obj;
    }

    @SuppressWarnings("cast")
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static com.kg.gettransfer.data.model.Vehicle createUsingJsonStream(Realm realm, JsonReader reader)
        throws IOException {
        final com.kg.gettransfer.data.model.Vehicle obj = new com.kg.gettransfer.data.model.Vehicle();
        final com_kg_gettransfer_data_model_VehicleRealmProxyInterface objProxy = (com_kg_gettransfer_data_model_VehicleRealmProxyInterface) obj;
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
            } else if (name.equals("year")) {
                if (reader.peek() != JsonToken.NULL) {
                    objProxy.realmSet$year((int) reader.nextInt());
                } else {
                    reader.skipValue();
                    throw new IllegalArgumentException("Trying to set non-nullable field 'year' to null.");
                }
            } else if (name.equals("transportTypeID")) {
                if (reader.peek() != JsonToken.NULL) {
                    objProxy.realmSet$transportTypeID((String) reader.nextString());
                } else {
                    reader.skipValue();
                    objProxy.realmSet$transportTypeID(null);
                }
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return realm.copyToRealm(obj);
    }

    public static com.kg.gettransfer.data.model.Vehicle copyOrUpdate(Realm realm, com.kg.gettransfer.data.model.Vehicle object, boolean update, Map<RealmModel,RealmObjectProxy> cache) {
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
            return (com.kg.gettransfer.data.model.Vehicle) cachedRealmObject;
        }

        return copy(realm, object, update, cache);
    }

    public static com.kg.gettransfer.data.model.Vehicle copy(Realm realm, com.kg.gettransfer.data.model.Vehicle newObject, boolean update, Map<RealmModel,RealmObjectProxy> cache) {
        RealmObjectProxy cachedRealmObject = cache.get(newObject);
        if (cachedRealmObject != null) {
            return (com.kg.gettransfer.data.model.Vehicle) cachedRealmObject;
        }

        // rejecting default values to avoid creating unexpected objects from RealmModel/RealmList fields.
        com.kg.gettransfer.data.model.Vehicle realmObject = realm.createObjectInternal(com.kg.gettransfer.data.model.Vehicle.class, false, Collections.<String>emptyList());
        cache.put(newObject, (RealmObjectProxy) realmObject);

        com_kg_gettransfer_data_model_VehicleRealmProxyInterface realmObjectSource = (com_kg_gettransfer_data_model_VehicleRealmProxyInterface) newObject;
        com_kg_gettransfer_data_model_VehicleRealmProxyInterface realmObjectCopy = (com_kg_gettransfer_data_model_VehicleRealmProxyInterface) realmObject;

        realmObjectCopy.realmSet$name(realmObjectSource.realmGet$name());
        realmObjectCopy.realmSet$year(realmObjectSource.realmGet$year());
        realmObjectCopy.realmSet$transportTypeID(realmObjectSource.realmGet$transportTypeID());
        return realmObject;
    }

    public static long insert(Realm realm, com.kg.gettransfer.data.model.Vehicle object, Map<RealmModel,Long> cache) {
        if (object instanceof RealmObjectProxy && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm() != null && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm().getPath().equals(realm.getPath())) {
            return ((RealmObjectProxy) object).realmGet$proxyState().getRow$realm().getIndex();
        }
        Table table = realm.getTable(com.kg.gettransfer.data.model.Vehicle.class);
        long tableNativePtr = table.getNativePtr();
        VehicleColumnInfo columnInfo = (VehicleColumnInfo) realm.getSchema().getColumnInfo(com.kg.gettransfer.data.model.Vehicle.class);
        long rowIndex = OsObject.createRow(table);
        cache.put(object, rowIndex);
        String realmGet$name = ((com_kg_gettransfer_data_model_VehicleRealmProxyInterface) object).realmGet$name();
        if (realmGet$name != null) {
            Table.nativeSetString(tableNativePtr, columnInfo.nameIndex, rowIndex, realmGet$name, false);
        }
        Table.nativeSetLong(tableNativePtr, columnInfo.yearIndex, rowIndex, ((com_kg_gettransfer_data_model_VehicleRealmProxyInterface) object).realmGet$year(), false);
        String realmGet$transportTypeID = ((com_kg_gettransfer_data_model_VehicleRealmProxyInterface) object).realmGet$transportTypeID();
        if (realmGet$transportTypeID != null) {
            Table.nativeSetString(tableNativePtr, columnInfo.transportTypeIDIndex, rowIndex, realmGet$transportTypeID, false);
        }
        return rowIndex;
    }

    public static void insert(Realm realm, Iterator<? extends RealmModel> objects, Map<RealmModel,Long> cache) {
        Table table = realm.getTable(com.kg.gettransfer.data.model.Vehicle.class);
        long tableNativePtr = table.getNativePtr();
        VehicleColumnInfo columnInfo = (VehicleColumnInfo) realm.getSchema().getColumnInfo(com.kg.gettransfer.data.model.Vehicle.class);
        com.kg.gettransfer.data.model.Vehicle object = null;
        while (objects.hasNext()) {
            object = (com.kg.gettransfer.data.model.Vehicle) objects.next();
            if (cache.containsKey(object)) {
                continue;
            }
            if (object instanceof RealmObjectProxy && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm() != null && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm().getPath().equals(realm.getPath())) {
                cache.put(object, ((RealmObjectProxy) object).realmGet$proxyState().getRow$realm().getIndex());
                continue;
            }
            long rowIndex = OsObject.createRow(table);
            cache.put(object, rowIndex);
            String realmGet$name = ((com_kg_gettransfer_data_model_VehicleRealmProxyInterface) object).realmGet$name();
            if (realmGet$name != null) {
                Table.nativeSetString(tableNativePtr, columnInfo.nameIndex, rowIndex, realmGet$name, false);
            }
            Table.nativeSetLong(tableNativePtr, columnInfo.yearIndex, rowIndex, ((com_kg_gettransfer_data_model_VehicleRealmProxyInterface) object).realmGet$year(), false);
            String realmGet$transportTypeID = ((com_kg_gettransfer_data_model_VehicleRealmProxyInterface) object).realmGet$transportTypeID();
            if (realmGet$transportTypeID != null) {
                Table.nativeSetString(tableNativePtr, columnInfo.transportTypeIDIndex, rowIndex, realmGet$transportTypeID, false);
            }
        }
    }

    public static long insertOrUpdate(Realm realm, com.kg.gettransfer.data.model.Vehicle object, Map<RealmModel,Long> cache) {
        if (object instanceof RealmObjectProxy && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm() != null && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm().getPath().equals(realm.getPath())) {
            return ((RealmObjectProxy) object).realmGet$proxyState().getRow$realm().getIndex();
        }
        Table table = realm.getTable(com.kg.gettransfer.data.model.Vehicle.class);
        long tableNativePtr = table.getNativePtr();
        VehicleColumnInfo columnInfo = (VehicleColumnInfo) realm.getSchema().getColumnInfo(com.kg.gettransfer.data.model.Vehicle.class);
        long rowIndex = OsObject.createRow(table);
        cache.put(object, rowIndex);
        String realmGet$name = ((com_kg_gettransfer_data_model_VehicleRealmProxyInterface) object).realmGet$name();
        if (realmGet$name != null) {
            Table.nativeSetString(tableNativePtr, columnInfo.nameIndex, rowIndex, realmGet$name, false);
        } else {
            Table.nativeSetNull(tableNativePtr, columnInfo.nameIndex, rowIndex, false);
        }
        Table.nativeSetLong(tableNativePtr, columnInfo.yearIndex, rowIndex, ((com_kg_gettransfer_data_model_VehicleRealmProxyInterface) object).realmGet$year(), false);
        String realmGet$transportTypeID = ((com_kg_gettransfer_data_model_VehicleRealmProxyInterface) object).realmGet$transportTypeID();
        if (realmGet$transportTypeID != null) {
            Table.nativeSetString(tableNativePtr, columnInfo.transportTypeIDIndex, rowIndex, realmGet$transportTypeID, false);
        } else {
            Table.nativeSetNull(tableNativePtr, columnInfo.transportTypeIDIndex, rowIndex, false);
        }
        return rowIndex;
    }

    public static void insertOrUpdate(Realm realm, Iterator<? extends RealmModel> objects, Map<RealmModel,Long> cache) {
        Table table = realm.getTable(com.kg.gettransfer.data.model.Vehicle.class);
        long tableNativePtr = table.getNativePtr();
        VehicleColumnInfo columnInfo = (VehicleColumnInfo) realm.getSchema().getColumnInfo(com.kg.gettransfer.data.model.Vehicle.class);
        com.kg.gettransfer.data.model.Vehicle object = null;
        while (objects.hasNext()) {
            object = (com.kg.gettransfer.data.model.Vehicle) objects.next();
            if (cache.containsKey(object)) {
                continue;
            }
            if (object instanceof RealmObjectProxy && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm() != null && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm().getPath().equals(realm.getPath())) {
                cache.put(object, ((RealmObjectProxy) object).realmGet$proxyState().getRow$realm().getIndex());
                continue;
            }
            long rowIndex = OsObject.createRow(table);
            cache.put(object, rowIndex);
            String realmGet$name = ((com_kg_gettransfer_data_model_VehicleRealmProxyInterface) object).realmGet$name();
            if (realmGet$name != null) {
                Table.nativeSetString(tableNativePtr, columnInfo.nameIndex, rowIndex, realmGet$name, false);
            } else {
                Table.nativeSetNull(tableNativePtr, columnInfo.nameIndex, rowIndex, false);
            }
            Table.nativeSetLong(tableNativePtr, columnInfo.yearIndex, rowIndex, ((com_kg_gettransfer_data_model_VehicleRealmProxyInterface) object).realmGet$year(), false);
            String realmGet$transportTypeID = ((com_kg_gettransfer_data_model_VehicleRealmProxyInterface) object).realmGet$transportTypeID();
            if (realmGet$transportTypeID != null) {
                Table.nativeSetString(tableNativePtr, columnInfo.transportTypeIDIndex, rowIndex, realmGet$transportTypeID, false);
            } else {
                Table.nativeSetNull(tableNativePtr, columnInfo.transportTypeIDIndex, rowIndex, false);
            }
        }
    }

    public static com.kg.gettransfer.data.model.Vehicle createDetachedCopy(com.kg.gettransfer.data.model.Vehicle realmObject, int currentDepth, int maxDepth, Map<RealmModel, CacheData<RealmModel>> cache) {
        if (currentDepth > maxDepth || realmObject == null) {
            return null;
        }
        CacheData<RealmModel> cachedObject = cache.get(realmObject);
        com.kg.gettransfer.data.model.Vehicle unmanagedObject;
        if (cachedObject == null) {
            unmanagedObject = new com.kg.gettransfer.data.model.Vehicle();
            cache.put(realmObject, new RealmObjectProxy.CacheData<RealmModel>(currentDepth, unmanagedObject));
        } else {
            // Reuse cached object or recreate it because it was encountered at a lower depth.
            if (currentDepth >= cachedObject.minDepth) {
                return (com.kg.gettransfer.data.model.Vehicle) cachedObject.object;
            }
            unmanagedObject = (com.kg.gettransfer.data.model.Vehicle) cachedObject.object;
            cachedObject.minDepth = currentDepth;
        }
        com_kg_gettransfer_data_model_VehicleRealmProxyInterface unmanagedCopy = (com_kg_gettransfer_data_model_VehicleRealmProxyInterface) unmanagedObject;
        com_kg_gettransfer_data_model_VehicleRealmProxyInterface realmSource = (com_kg_gettransfer_data_model_VehicleRealmProxyInterface) realmObject;
        unmanagedCopy.realmSet$name(realmSource.realmGet$name());
        unmanagedCopy.realmSet$year(realmSource.realmGet$year());
        unmanagedCopy.realmSet$transportTypeID(realmSource.realmGet$transportTypeID());

        return unmanagedObject;
    }

    @Override
    @SuppressWarnings("ArrayToString")
    public String toString() {
        if (!RealmObject.isValid(this)) {
            return "Invalid object";
        }
        StringBuilder stringBuilder = new StringBuilder("Vehicle = proxy[");
        stringBuilder.append("{name:");
        stringBuilder.append(realmGet$name() != null ? realmGet$name() : "null");
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{year:");
        stringBuilder.append(realmGet$year());
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{transportTypeID:");
        stringBuilder.append(realmGet$transportTypeID() != null ? realmGet$transportTypeID() : "null");
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
        com_kg_gettransfer_data_model_VehicleRealmProxy aVehicle = (com_kg_gettransfer_data_model_VehicleRealmProxy)o;

        String path = proxyState.getRealm$realm().getPath();
        String otherPath = aVehicle.proxyState.getRealm$realm().getPath();
        if (path != null ? !path.equals(otherPath) : otherPath != null) return false;

        String tableName = proxyState.getRow$realm().getTable().getName();
        String otherTableName = aVehicle.proxyState.getRow$realm().getTable().getName();
        if (tableName != null ? !tableName.equals(otherTableName) : otherTableName != null) return false;

        if (proxyState.getRow$realm().getIndex() != aVehicle.proxyState.getRow$realm().getIndex()) return false;

        return true;
    }
}
