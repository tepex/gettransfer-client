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
public class com_kg_gettransfer_data_model_secondary_RatingRealmProxy extends com.kg.gettransfer.data.model.secondary.Rating
    implements RealmObjectProxy, com_kg_gettransfer_data_model_secondary_RatingRealmProxyInterface {

    static final class RatingColumnInfo extends ColumnInfo {
        long averageIndex;
        long vehicleIndex;
        long driveIndex;
        long fairIndex;

        RatingColumnInfo(OsSchemaInfo schemaInfo) {
            super(4);
            OsObjectSchemaInfo objectSchemaInfo = schemaInfo.getObjectSchemaInfo("Rating");
            this.averageIndex = addColumnDetails("average", "average", objectSchemaInfo);
            this.vehicleIndex = addColumnDetails("vehicle", "vehicle", objectSchemaInfo);
            this.driveIndex = addColumnDetails("drive", "drive", objectSchemaInfo);
            this.fairIndex = addColumnDetails("fair", "fair", objectSchemaInfo);
        }

        RatingColumnInfo(ColumnInfo src, boolean mutable) {
            super(src, mutable);
            copy(src, this);
        }

        @Override
        protected final ColumnInfo copy(boolean mutable) {
            return new RatingColumnInfo(this, mutable);
        }

        @Override
        protected final void copy(ColumnInfo rawSrc, ColumnInfo rawDst) {
            final RatingColumnInfo src = (RatingColumnInfo) rawSrc;
            final RatingColumnInfo dst = (RatingColumnInfo) rawDst;
            dst.averageIndex = src.averageIndex;
            dst.vehicleIndex = src.vehicleIndex;
            dst.driveIndex = src.driveIndex;
            dst.fairIndex = src.fairIndex;
        }
    }

    private static final OsObjectSchemaInfo expectedObjectSchemaInfo = createExpectedObjectSchemaInfo();

    private RatingColumnInfo columnInfo;
    private ProxyState<com.kg.gettransfer.data.model.secondary.Rating> proxyState;

    com_kg_gettransfer_data_model_secondary_RatingRealmProxy() {
        proxyState.setConstructionFinished();
    }

    @Override
    public void realm$injectObjectContext() {
        if (this.proxyState != null) {
            return;
        }
        final BaseRealm.RealmObjectContext context = BaseRealm.objectContext.get();
        this.columnInfo = (RatingColumnInfo) context.getColumnInfo();
        this.proxyState = new ProxyState<com.kg.gettransfer.data.model.secondary.Rating>(this);
        proxyState.setRealm$realm(context.getRealm());
        proxyState.setRow$realm(context.getRow());
        proxyState.setAcceptDefaultValue$realm(context.getAcceptDefaultValue());
        proxyState.setExcludeFields$realm(context.getExcludeFields());
    }

    @Override
    @SuppressWarnings("cast")
    public int realmGet$average() {
        proxyState.getRealm$realm().checkIfValid();
        return (int) proxyState.getRow$realm().getLong(columnInfo.averageIndex);
    }

    @Override
    public void realmSet$average(int value) {
        if (proxyState.isUnderConstruction()) {
            if (!proxyState.getAcceptDefaultValue$realm()) {
                return;
            }
            final Row row = proxyState.getRow$realm();
            row.getTable().setLong(columnInfo.averageIndex, row.getIndex(), value, true);
            return;
        }

        proxyState.getRealm$realm().checkIfValid();
        proxyState.getRow$realm().setLong(columnInfo.averageIndex, value);
    }

    @Override
    @SuppressWarnings("cast")
    public int realmGet$vehicle() {
        proxyState.getRealm$realm().checkIfValid();
        return (int) proxyState.getRow$realm().getLong(columnInfo.vehicleIndex);
    }

    @Override
    public void realmSet$vehicle(int value) {
        if (proxyState.isUnderConstruction()) {
            if (!proxyState.getAcceptDefaultValue$realm()) {
                return;
            }
            final Row row = proxyState.getRow$realm();
            row.getTable().setLong(columnInfo.vehicleIndex, row.getIndex(), value, true);
            return;
        }

        proxyState.getRealm$realm().checkIfValid();
        proxyState.getRow$realm().setLong(columnInfo.vehicleIndex, value);
    }

    @Override
    @SuppressWarnings("cast")
    public int realmGet$drive() {
        proxyState.getRealm$realm().checkIfValid();
        return (int) proxyState.getRow$realm().getLong(columnInfo.driveIndex);
    }

    @Override
    public void realmSet$drive(int value) {
        if (proxyState.isUnderConstruction()) {
            if (!proxyState.getAcceptDefaultValue$realm()) {
                return;
            }
            final Row row = proxyState.getRow$realm();
            row.getTable().setLong(columnInfo.driveIndex, row.getIndex(), value, true);
            return;
        }

        proxyState.getRealm$realm().checkIfValid();
        proxyState.getRow$realm().setLong(columnInfo.driveIndex, value);
    }

    @Override
    @SuppressWarnings("cast")
    public int realmGet$fair() {
        proxyState.getRealm$realm().checkIfValid();
        return (int) proxyState.getRow$realm().getLong(columnInfo.fairIndex);
    }

    @Override
    public void realmSet$fair(int value) {
        if (proxyState.isUnderConstruction()) {
            if (!proxyState.getAcceptDefaultValue$realm()) {
                return;
            }
            final Row row = proxyState.getRow$realm();
            row.getTable().setLong(columnInfo.fairIndex, row.getIndex(), value, true);
            return;
        }

        proxyState.getRealm$realm().checkIfValid();
        proxyState.getRow$realm().setLong(columnInfo.fairIndex, value);
    }

    private static OsObjectSchemaInfo createExpectedObjectSchemaInfo() {
        OsObjectSchemaInfo.Builder builder = new OsObjectSchemaInfo.Builder("Rating", 4, 0);
        builder.addPersistedProperty("average", RealmFieldType.INTEGER, !Property.PRIMARY_KEY, !Property.INDEXED, Property.REQUIRED);
        builder.addPersistedProperty("vehicle", RealmFieldType.INTEGER, !Property.PRIMARY_KEY, !Property.INDEXED, Property.REQUIRED);
        builder.addPersistedProperty("drive", RealmFieldType.INTEGER, !Property.PRIMARY_KEY, !Property.INDEXED, Property.REQUIRED);
        builder.addPersistedProperty("fair", RealmFieldType.INTEGER, !Property.PRIMARY_KEY, !Property.INDEXED, Property.REQUIRED);
        return builder.build();
    }

    public static OsObjectSchemaInfo getExpectedObjectSchemaInfo() {
        return expectedObjectSchemaInfo;
    }

    public static RatingColumnInfo createColumnInfo(OsSchemaInfo schemaInfo) {
        return new RatingColumnInfo(schemaInfo);
    }

    public static String getSimpleClassName() {
        return "Rating";
    }

    public static final class ClassNameHelper {
        public static final String INTERNAL_CLASS_NAME = "Rating";
    }

    @SuppressWarnings("cast")
    public static com.kg.gettransfer.data.model.secondary.Rating createOrUpdateUsingJsonObject(Realm realm, JSONObject json, boolean update)
        throws JSONException {
        final List<String> excludeFields = Collections.<String> emptyList();
        com.kg.gettransfer.data.model.secondary.Rating obj = realm.createObjectInternal(com.kg.gettransfer.data.model.secondary.Rating.class, true, excludeFields);

        final com_kg_gettransfer_data_model_secondary_RatingRealmProxyInterface objProxy = (com_kg_gettransfer_data_model_secondary_RatingRealmProxyInterface) obj;
        if (json.has("average")) {
            if (json.isNull("average")) {
                throw new IllegalArgumentException("Trying to set non-nullable field 'average' to null.");
            } else {
                objProxy.realmSet$average((int) json.getInt("average"));
            }
        }
        if (json.has("vehicle")) {
            if (json.isNull("vehicle")) {
                throw new IllegalArgumentException("Trying to set non-nullable field 'vehicle' to null.");
            } else {
                objProxy.realmSet$vehicle((int) json.getInt("vehicle"));
            }
        }
        if (json.has("drive")) {
            if (json.isNull("drive")) {
                throw new IllegalArgumentException("Trying to set non-nullable field 'drive' to null.");
            } else {
                objProxy.realmSet$drive((int) json.getInt("drive"));
            }
        }
        if (json.has("fair")) {
            if (json.isNull("fair")) {
                throw new IllegalArgumentException("Trying to set non-nullable field 'fair' to null.");
            } else {
                objProxy.realmSet$fair((int) json.getInt("fair"));
            }
        }
        return obj;
    }

    @SuppressWarnings("cast")
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static com.kg.gettransfer.data.model.secondary.Rating createUsingJsonStream(Realm realm, JsonReader reader)
        throws IOException {
        final com.kg.gettransfer.data.model.secondary.Rating obj = new com.kg.gettransfer.data.model.secondary.Rating();
        final com_kg_gettransfer_data_model_secondary_RatingRealmProxyInterface objProxy = (com_kg_gettransfer_data_model_secondary_RatingRealmProxyInterface) obj;
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (false) {
            } else if (name.equals("average")) {
                if (reader.peek() != JsonToken.NULL) {
                    objProxy.realmSet$average((int) reader.nextInt());
                } else {
                    reader.skipValue();
                    throw new IllegalArgumentException("Trying to set non-nullable field 'average' to null.");
                }
            } else if (name.equals("vehicle")) {
                if (reader.peek() != JsonToken.NULL) {
                    objProxy.realmSet$vehicle((int) reader.nextInt());
                } else {
                    reader.skipValue();
                    throw new IllegalArgumentException("Trying to set non-nullable field 'vehicle' to null.");
                }
            } else if (name.equals("drive")) {
                if (reader.peek() != JsonToken.NULL) {
                    objProxy.realmSet$drive((int) reader.nextInt());
                } else {
                    reader.skipValue();
                    throw new IllegalArgumentException("Trying to set non-nullable field 'drive' to null.");
                }
            } else if (name.equals("fair")) {
                if (reader.peek() != JsonToken.NULL) {
                    objProxy.realmSet$fair((int) reader.nextInt());
                } else {
                    reader.skipValue();
                    throw new IllegalArgumentException("Trying to set non-nullable field 'fair' to null.");
                }
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return realm.copyToRealm(obj);
    }

    public static com.kg.gettransfer.data.model.secondary.Rating copyOrUpdate(Realm realm, com.kg.gettransfer.data.model.secondary.Rating object, boolean update, Map<RealmModel,RealmObjectProxy> cache) {
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
            return (com.kg.gettransfer.data.model.secondary.Rating) cachedRealmObject;
        }

        return copy(realm, object, update, cache);
    }

    public static com.kg.gettransfer.data.model.secondary.Rating copy(Realm realm, com.kg.gettransfer.data.model.secondary.Rating newObject, boolean update, Map<RealmModel,RealmObjectProxy> cache) {
        RealmObjectProxy cachedRealmObject = cache.get(newObject);
        if (cachedRealmObject != null) {
            return (com.kg.gettransfer.data.model.secondary.Rating) cachedRealmObject;
        }

        // rejecting default values to avoid creating unexpected objects from RealmModel/RealmList fields.
        com.kg.gettransfer.data.model.secondary.Rating realmObject = realm.createObjectInternal(com.kg.gettransfer.data.model.secondary.Rating.class, false, Collections.<String>emptyList());
        cache.put(newObject, (RealmObjectProxy) realmObject);

        com_kg_gettransfer_data_model_secondary_RatingRealmProxyInterface realmObjectSource = (com_kg_gettransfer_data_model_secondary_RatingRealmProxyInterface) newObject;
        com_kg_gettransfer_data_model_secondary_RatingRealmProxyInterface realmObjectCopy = (com_kg_gettransfer_data_model_secondary_RatingRealmProxyInterface) realmObject;

        realmObjectCopy.realmSet$average(realmObjectSource.realmGet$average());
        realmObjectCopy.realmSet$vehicle(realmObjectSource.realmGet$vehicle());
        realmObjectCopy.realmSet$drive(realmObjectSource.realmGet$drive());
        realmObjectCopy.realmSet$fair(realmObjectSource.realmGet$fair());
        return realmObject;
    }

    public static long insert(Realm realm, com.kg.gettransfer.data.model.secondary.Rating object, Map<RealmModel,Long> cache) {
        if (object instanceof RealmObjectProxy && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm() != null && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm().getPath().equals(realm.getPath())) {
            return ((RealmObjectProxy) object).realmGet$proxyState().getRow$realm().getIndex();
        }
        Table table = realm.getTable(com.kg.gettransfer.data.model.secondary.Rating.class);
        long tableNativePtr = table.getNativePtr();
        RatingColumnInfo columnInfo = (RatingColumnInfo) realm.getSchema().getColumnInfo(com.kg.gettransfer.data.model.secondary.Rating.class);
        long rowIndex = OsObject.createRow(table);
        cache.put(object, rowIndex);
        Table.nativeSetLong(tableNativePtr, columnInfo.averageIndex, rowIndex, ((com_kg_gettransfer_data_model_secondary_RatingRealmProxyInterface) object).realmGet$average(), false);
        Table.nativeSetLong(tableNativePtr, columnInfo.vehicleIndex, rowIndex, ((com_kg_gettransfer_data_model_secondary_RatingRealmProxyInterface) object).realmGet$vehicle(), false);
        Table.nativeSetLong(tableNativePtr, columnInfo.driveIndex, rowIndex, ((com_kg_gettransfer_data_model_secondary_RatingRealmProxyInterface) object).realmGet$drive(), false);
        Table.nativeSetLong(tableNativePtr, columnInfo.fairIndex, rowIndex, ((com_kg_gettransfer_data_model_secondary_RatingRealmProxyInterface) object).realmGet$fair(), false);
        return rowIndex;
    }

    public static void insert(Realm realm, Iterator<? extends RealmModel> objects, Map<RealmModel,Long> cache) {
        Table table = realm.getTable(com.kg.gettransfer.data.model.secondary.Rating.class);
        long tableNativePtr = table.getNativePtr();
        RatingColumnInfo columnInfo = (RatingColumnInfo) realm.getSchema().getColumnInfo(com.kg.gettransfer.data.model.secondary.Rating.class);
        com.kg.gettransfer.data.model.secondary.Rating object = null;
        while (objects.hasNext()) {
            object = (com.kg.gettransfer.data.model.secondary.Rating) objects.next();
            if (cache.containsKey(object)) {
                continue;
            }
            if (object instanceof RealmObjectProxy && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm() != null && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm().getPath().equals(realm.getPath())) {
                cache.put(object, ((RealmObjectProxy) object).realmGet$proxyState().getRow$realm().getIndex());
                continue;
            }
            long rowIndex = OsObject.createRow(table);
            cache.put(object, rowIndex);
            Table.nativeSetLong(tableNativePtr, columnInfo.averageIndex, rowIndex, ((com_kg_gettransfer_data_model_secondary_RatingRealmProxyInterface) object).realmGet$average(), false);
            Table.nativeSetLong(tableNativePtr, columnInfo.vehicleIndex, rowIndex, ((com_kg_gettransfer_data_model_secondary_RatingRealmProxyInterface) object).realmGet$vehicle(), false);
            Table.nativeSetLong(tableNativePtr, columnInfo.driveIndex, rowIndex, ((com_kg_gettransfer_data_model_secondary_RatingRealmProxyInterface) object).realmGet$drive(), false);
            Table.nativeSetLong(tableNativePtr, columnInfo.fairIndex, rowIndex, ((com_kg_gettransfer_data_model_secondary_RatingRealmProxyInterface) object).realmGet$fair(), false);
        }
    }

    public static long insertOrUpdate(Realm realm, com.kg.gettransfer.data.model.secondary.Rating object, Map<RealmModel,Long> cache) {
        if (object instanceof RealmObjectProxy && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm() != null && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm().getPath().equals(realm.getPath())) {
            return ((RealmObjectProxy) object).realmGet$proxyState().getRow$realm().getIndex();
        }
        Table table = realm.getTable(com.kg.gettransfer.data.model.secondary.Rating.class);
        long tableNativePtr = table.getNativePtr();
        RatingColumnInfo columnInfo = (RatingColumnInfo) realm.getSchema().getColumnInfo(com.kg.gettransfer.data.model.secondary.Rating.class);
        long rowIndex = OsObject.createRow(table);
        cache.put(object, rowIndex);
        Table.nativeSetLong(tableNativePtr, columnInfo.averageIndex, rowIndex, ((com_kg_gettransfer_data_model_secondary_RatingRealmProxyInterface) object).realmGet$average(), false);
        Table.nativeSetLong(tableNativePtr, columnInfo.vehicleIndex, rowIndex, ((com_kg_gettransfer_data_model_secondary_RatingRealmProxyInterface) object).realmGet$vehicle(), false);
        Table.nativeSetLong(tableNativePtr, columnInfo.driveIndex, rowIndex, ((com_kg_gettransfer_data_model_secondary_RatingRealmProxyInterface) object).realmGet$drive(), false);
        Table.nativeSetLong(tableNativePtr, columnInfo.fairIndex, rowIndex, ((com_kg_gettransfer_data_model_secondary_RatingRealmProxyInterface) object).realmGet$fair(), false);
        return rowIndex;
    }

    public static void insertOrUpdate(Realm realm, Iterator<? extends RealmModel> objects, Map<RealmModel,Long> cache) {
        Table table = realm.getTable(com.kg.gettransfer.data.model.secondary.Rating.class);
        long tableNativePtr = table.getNativePtr();
        RatingColumnInfo columnInfo = (RatingColumnInfo) realm.getSchema().getColumnInfo(com.kg.gettransfer.data.model.secondary.Rating.class);
        com.kg.gettransfer.data.model.secondary.Rating object = null;
        while (objects.hasNext()) {
            object = (com.kg.gettransfer.data.model.secondary.Rating) objects.next();
            if (cache.containsKey(object)) {
                continue;
            }
            if (object instanceof RealmObjectProxy && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm() != null && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm().getPath().equals(realm.getPath())) {
                cache.put(object, ((RealmObjectProxy) object).realmGet$proxyState().getRow$realm().getIndex());
                continue;
            }
            long rowIndex = OsObject.createRow(table);
            cache.put(object, rowIndex);
            Table.nativeSetLong(tableNativePtr, columnInfo.averageIndex, rowIndex, ((com_kg_gettransfer_data_model_secondary_RatingRealmProxyInterface) object).realmGet$average(), false);
            Table.nativeSetLong(tableNativePtr, columnInfo.vehicleIndex, rowIndex, ((com_kg_gettransfer_data_model_secondary_RatingRealmProxyInterface) object).realmGet$vehicle(), false);
            Table.nativeSetLong(tableNativePtr, columnInfo.driveIndex, rowIndex, ((com_kg_gettransfer_data_model_secondary_RatingRealmProxyInterface) object).realmGet$drive(), false);
            Table.nativeSetLong(tableNativePtr, columnInfo.fairIndex, rowIndex, ((com_kg_gettransfer_data_model_secondary_RatingRealmProxyInterface) object).realmGet$fair(), false);
        }
    }

    public static com.kg.gettransfer.data.model.secondary.Rating createDetachedCopy(com.kg.gettransfer.data.model.secondary.Rating realmObject, int currentDepth, int maxDepth, Map<RealmModel, CacheData<RealmModel>> cache) {
        if (currentDepth > maxDepth || realmObject == null) {
            return null;
        }
        CacheData<RealmModel> cachedObject = cache.get(realmObject);
        com.kg.gettransfer.data.model.secondary.Rating unmanagedObject;
        if (cachedObject == null) {
            unmanagedObject = new com.kg.gettransfer.data.model.secondary.Rating();
            cache.put(realmObject, new RealmObjectProxy.CacheData<RealmModel>(currentDepth, unmanagedObject));
        } else {
            // Reuse cached object or recreate it because it was encountered at a lower depth.
            if (currentDepth >= cachedObject.minDepth) {
                return (com.kg.gettransfer.data.model.secondary.Rating) cachedObject.object;
            }
            unmanagedObject = (com.kg.gettransfer.data.model.secondary.Rating) cachedObject.object;
            cachedObject.minDepth = currentDepth;
        }
        com_kg_gettransfer_data_model_secondary_RatingRealmProxyInterface unmanagedCopy = (com_kg_gettransfer_data_model_secondary_RatingRealmProxyInterface) unmanagedObject;
        com_kg_gettransfer_data_model_secondary_RatingRealmProxyInterface realmSource = (com_kg_gettransfer_data_model_secondary_RatingRealmProxyInterface) realmObject;
        unmanagedCopy.realmSet$average(realmSource.realmGet$average());
        unmanagedCopy.realmSet$vehicle(realmSource.realmGet$vehicle());
        unmanagedCopy.realmSet$drive(realmSource.realmGet$drive());
        unmanagedCopy.realmSet$fair(realmSource.realmGet$fair());

        return unmanagedObject;
    }

    @Override
    @SuppressWarnings("ArrayToString")
    public String toString() {
        if (!RealmObject.isValid(this)) {
            return "Invalid object";
        }
        StringBuilder stringBuilder = new StringBuilder("Rating = proxy[");
        stringBuilder.append("{average:");
        stringBuilder.append(realmGet$average());
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{vehicle:");
        stringBuilder.append(realmGet$vehicle());
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{drive:");
        stringBuilder.append(realmGet$drive());
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{fair:");
        stringBuilder.append(realmGet$fair());
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
        com_kg_gettransfer_data_model_secondary_RatingRealmProxy aRating = (com_kg_gettransfer_data_model_secondary_RatingRealmProxy)o;

        String path = proxyState.getRealm$realm().getPath();
        String otherPath = aRating.proxyState.getRealm$realm().getPath();
        if (path != null ? !path.equals(otherPath) : otherPath != null) return false;

        String tableName = proxyState.getRow$realm().getTable().getName();
        String otherTableName = aRating.proxyState.getRow$realm().getTable().getName();
        if (tableName != null ? !tableName.equals(otherTableName) : otherTableName != null) return false;

        if (proxyState.getRow$realm().getIndex() != aRating.proxyState.getRow$realm().getIndex()) return false;

        return true;
    }
}
