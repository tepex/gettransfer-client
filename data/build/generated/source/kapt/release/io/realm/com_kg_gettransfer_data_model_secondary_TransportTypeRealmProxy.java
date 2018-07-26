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
public class com_kg_gettransfer_data_model_secondary_TransportTypeRealmProxy extends com.kg.gettransfer.data.model.secondary.TransportType
    implements RealmObjectProxy, com_kg_gettransfer_data_model_secondary_TransportTypeRealmProxyInterface {

    static final class TransportTypeColumnInfo extends ColumnInfo {
        long idIndex;
        long titleIndex;
        long paxMaxIndex;
        long luggageMaxIndex;

        TransportTypeColumnInfo(OsSchemaInfo schemaInfo) {
            super(4);
            OsObjectSchemaInfo objectSchemaInfo = schemaInfo.getObjectSchemaInfo("TransportType");
            this.idIndex = addColumnDetails("id", "id", objectSchemaInfo);
            this.titleIndex = addColumnDetails("title", "title", objectSchemaInfo);
            this.paxMaxIndex = addColumnDetails("paxMax", "paxMax", objectSchemaInfo);
            this.luggageMaxIndex = addColumnDetails("luggageMax", "luggageMax", objectSchemaInfo);
        }

        TransportTypeColumnInfo(ColumnInfo src, boolean mutable) {
            super(src, mutable);
            copy(src, this);
        }

        @Override
        protected final ColumnInfo copy(boolean mutable) {
            return new TransportTypeColumnInfo(this, mutable);
        }

        @Override
        protected final void copy(ColumnInfo rawSrc, ColumnInfo rawDst) {
            final TransportTypeColumnInfo src = (TransportTypeColumnInfo) rawSrc;
            final TransportTypeColumnInfo dst = (TransportTypeColumnInfo) rawDst;
            dst.idIndex = src.idIndex;
            dst.titleIndex = src.titleIndex;
            dst.paxMaxIndex = src.paxMaxIndex;
            dst.luggageMaxIndex = src.luggageMaxIndex;
        }
    }

    private static final OsObjectSchemaInfo expectedObjectSchemaInfo = createExpectedObjectSchemaInfo();

    private TransportTypeColumnInfo columnInfo;
    private ProxyState<com.kg.gettransfer.data.model.secondary.TransportType> proxyState;

    com_kg_gettransfer_data_model_secondary_TransportTypeRealmProxy() {
        proxyState.setConstructionFinished();
    }

    @Override
    public void realm$injectObjectContext() {
        if (this.proxyState != null) {
            return;
        }
        final BaseRealm.RealmObjectContext context = BaseRealm.objectContext.get();
        this.columnInfo = (TransportTypeColumnInfo) context.getColumnInfo();
        this.proxyState = new ProxyState<com.kg.gettransfer.data.model.secondary.TransportType>(this);
        proxyState.setRealm$realm(context.getRealm());
        proxyState.setRow$realm(context.getRow());
        proxyState.setAcceptDefaultValue$realm(context.getAcceptDefaultValue());
        proxyState.setExcludeFields$realm(context.getExcludeFields());
    }

    @Override
    @SuppressWarnings("cast")
    public String realmGet$id() {
        proxyState.getRealm$realm().checkIfValid();
        return (java.lang.String) proxyState.getRow$realm().getString(columnInfo.idIndex);
    }

    @Override
    public void realmSet$id(String value) {
        if (proxyState.isUnderConstruction()) {
            // default value of the primary key is always ignored.
            return;
        }

        proxyState.getRealm$realm().checkIfValid();
        throw new io.realm.exceptions.RealmException("Primary key field 'id' cannot be changed after object was created.");
    }

    @Override
    @SuppressWarnings("cast")
    public String realmGet$title() {
        proxyState.getRealm$realm().checkIfValid();
        return (java.lang.String) proxyState.getRow$realm().getString(columnInfo.titleIndex);
    }

    @Override
    public void realmSet$title(String value) {
        if (proxyState.isUnderConstruction()) {
            if (!proxyState.getAcceptDefaultValue$realm()) {
                return;
            }
            final Row row = proxyState.getRow$realm();
            if (value == null) {
                throw new IllegalArgumentException("Trying to set non-nullable field 'title' to null.");
            }
            row.getTable().setString(columnInfo.titleIndex, row.getIndex(), value, true);
            return;
        }

        proxyState.getRealm$realm().checkIfValid();
        if (value == null) {
            throw new IllegalArgumentException("Trying to set non-nullable field 'title' to null.");
        }
        proxyState.getRow$realm().setString(columnInfo.titleIndex, value);
    }

    @Override
    @SuppressWarnings("cast")
    public int realmGet$paxMax() {
        proxyState.getRealm$realm().checkIfValid();
        return (int) proxyState.getRow$realm().getLong(columnInfo.paxMaxIndex);
    }

    @Override
    public void realmSet$paxMax(int value) {
        if (proxyState.isUnderConstruction()) {
            if (!proxyState.getAcceptDefaultValue$realm()) {
                return;
            }
            final Row row = proxyState.getRow$realm();
            row.getTable().setLong(columnInfo.paxMaxIndex, row.getIndex(), value, true);
            return;
        }

        proxyState.getRealm$realm().checkIfValid();
        proxyState.getRow$realm().setLong(columnInfo.paxMaxIndex, value);
    }

    @Override
    @SuppressWarnings("cast")
    public int realmGet$luggageMax() {
        proxyState.getRealm$realm().checkIfValid();
        return (int) proxyState.getRow$realm().getLong(columnInfo.luggageMaxIndex);
    }

    @Override
    public void realmSet$luggageMax(int value) {
        if (proxyState.isUnderConstruction()) {
            if (!proxyState.getAcceptDefaultValue$realm()) {
                return;
            }
            final Row row = proxyState.getRow$realm();
            row.getTable().setLong(columnInfo.luggageMaxIndex, row.getIndex(), value, true);
            return;
        }

        proxyState.getRealm$realm().checkIfValid();
        proxyState.getRow$realm().setLong(columnInfo.luggageMaxIndex, value);
    }

    private static OsObjectSchemaInfo createExpectedObjectSchemaInfo() {
        OsObjectSchemaInfo.Builder builder = new OsObjectSchemaInfo.Builder("TransportType", 4, 0);
        builder.addPersistedProperty("id", RealmFieldType.STRING, Property.PRIMARY_KEY, Property.INDEXED, Property.REQUIRED);
        builder.addPersistedProperty("title", RealmFieldType.STRING, !Property.PRIMARY_KEY, !Property.INDEXED, Property.REQUIRED);
        builder.addPersistedProperty("paxMax", RealmFieldType.INTEGER, !Property.PRIMARY_KEY, !Property.INDEXED, Property.REQUIRED);
        builder.addPersistedProperty("luggageMax", RealmFieldType.INTEGER, !Property.PRIMARY_KEY, !Property.INDEXED, Property.REQUIRED);
        return builder.build();
    }

    public static OsObjectSchemaInfo getExpectedObjectSchemaInfo() {
        return expectedObjectSchemaInfo;
    }

    public static TransportTypeColumnInfo createColumnInfo(OsSchemaInfo schemaInfo) {
        return new TransportTypeColumnInfo(schemaInfo);
    }

    public static String getSimpleClassName() {
        return "TransportType";
    }

    public static final class ClassNameHelper {
        public static final String INTERNAL_CLASS_NAME = "TransportType";
    }

    @SuppressWarnings("cast")
    public static com.kg.gettransfer.data.model.secondary.TransportType createOrUpdateUsingJsonObject(Realm realm, JSONObject json, boolean update)
        throws JSONException {
        final List<String> excludeFields = Collections.<String> emptyList();
        com.kg.gettransfer.data.model.secondary.TransportType obj = null;
        if (update) {
            Table table = realm.getTable(com.kg.gettransfer.data.model.secondary.TransportType.class);
            TransportTypeColumnInfo columnInfo = (TransportTypeColumnInfo) realm.getSchema().getColumnInfo(com.kg.gettransfer.data.model.secondary.TransportType.class);
            long pkColumnIndex = columnInfo.idIndex;
            long rowIndex = Table.NO_MATCH;
            if (!json.isNull("id")) {
                rowIndex = table.findFirstString(pkColumnIndex, json.getString("id"));
            }
            if (rowIndex != Table.NO_MATCH) {
                final BaseRealm.RealmObjectContext objectContext = BaseRealm.objectContext.get();
                try {
                    objectContext.set(realm, table.getUncheckedRow(rowIndex), realm.getSchema().getColumnInfo(com.kg.gettransfer.data.model.secondary.TransportType.class), false, Collections.<String> emptyList());
                    obj = new io.realm.com_kg_gettransfer_data_model_secondary_TransportTypeRealmProxy();
                } finally {
                    objectContext.clear();
                }
            }
        }
        if (obj == null) {
            if (json.has("id")) {
                if (json.isNull("id")) {
                    obj = (io.realm.com_kg_gettransfer_data_model_secondary_TransportTypeRealmProxy) realm.createObjectInternal(com.kg.gettransfer.data.model.secondary.TransportType.class, null, true, excludeFields);
                } else {
                    obj = (io.realm.com_kg_gettransfer_data_model_secondary_TransportTypeRealmProxy) realm.createObjectInternal(com.kg.gettransfer.data.model.secondary.TransportType.class, json.getString("id"), true, excludeFields);
                }
            } else {
                throw new IllegalArgumentException("JSON object doesn't have the primary key field 'id'.");
            }
        }

        final com_kg_gettransfer_data_model_secondary_TransportTypeRealmProxyInterface objProxy = (com_kg_gettransfer_data_model_secondary_TransportTypeRealmProxyInterface) obj;
        if (json.has("title")) {
            if (json.isNull("title")) {
                objProxy.realmSet$title(null);
            } else {
                objProxy.realmSet$title((String) json.getString("title"));
            }
        }
        if (json.has("paxMax")) {
            if (json.isNull("paxMax")) {
                throw new IllegalArgumentException("Trying to set non-nullable field 'paxMax' to null.");
            } else {
                objProxy.realmSet$paxMax((int) json.getInt("paxMax"));
            }
        }
        if (json.has("luggageMax")) {
            if (json.isNull("luggageMax")) {
                throw new IllegalArgumentException("Trying to set non-nullable field 'luggageMax' to null.");
            } else {
                objProxy.realmSet$luggageMax((int) json.getInt("luggageMax"));
            }
        }
        return obj;
    }

    @SuppressWarnings("cast")
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static com.kg.gettransfer.data.model.secondary.TransportType createUsingJsonStream(Realm realm, JsonReader reader)
        throws IOException {
        boolean jsonHasPrimaryKey = false;
        final com.kg.gettransfer.data.model.secondary.TransportType obj = new com.kg.gettransfer.data.model.secondary.TransportType();
        final com_kg_gettransfer_data_model_secondary_TransportTypeRealmProxyInterface objProxy = (com_kg_gettransfer_data_model_secondary_TransportTypeRealmProxyInterface) obj;
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (false) {
            } else if (name.equals("id")) {
                if (reader.peek() != JsonToken.NULL) {
                    objProxy.realmSet$id((String) reader.nextString());
                } else {
                    reader.skipValue();
                    objProxy.realmSet$id(null);
                }
                jsonHasPrimaryKey = true;
            } else if (name.equals("title")) {
                if (reader.peek() != JsonToken.NULL) {
                    objProxy.realmSet$title((String) reader.nextString());
                } else {
                    reader.skipValue();
                    objProxy.realmSet$title(null);
                }
            } else if (name.equals("paxMax")) {
                if (reader.peek() != JsonToken.NULL) {
                    objProxy.realmSet$paxMax((int) reader.nextInt());
                } else {
                    reader.skipValue();
                    throw new IllegalArgumentException("Trying to set non-nullable field 'paxMax' to null.");
                }
            } else if (name.equals("luggageMax")) {
                if (reader.peek() != JsonToken.NULL) {
                    objProxy.realmSet$luggageMax((int) reader.nextInt());
                } else {
                    reader.skipValue();
                    throw new IllegalArgumentException("Trying to set non-nullable field 'luggageMax' to null.");
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

    public static com.kg.gettransfer.data.model.secondary.TransportType copyOrUpdate(Realm realm, com.kg.gettransfer.data.model.secondary.TransportType object, boolean update, Map<RealmModel,RealmObjectProxy> cache) {
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
            return (com.kg.gettransfer.data.model.secondary.TransportType) cachedRealmObject;
        }

        com.kg.gettransfer.data.model.secondary.TransportType realmObject = null;
        boolean canUpdate = update;
        if (canUpdate) {
            Table table = realm.getTable(com.kg.gettransfer.data.model.secondary.TransportType.class);
            TransportTypeColumnInfo columnInfo = (TransportTypeColumnInfo) realm.getSchema().getColumnInfo(com.kg.gettransfer.data.model.secondary.TransportType.class);
            long pkColumnIndex = columnInfo.idIndex;
            long rowIndex = table.findFirstString(pkColumnIndex, ((com_kg_gettransfer_data_model_secondary_TransportTypeRealmProxyInterface) object).realmGet$id());
            if (rowIndex == Table.NO_MATCH) {
                canUpdate = false;
            } else {
                try {
                    objectContext.set(realm, table.getUncheckedRow(rowIndex), realm.getSchema().getColumnInfo(com.kg.gettransfer.data.model.secondary.TransportType.class), false, Collections.<String> emptyList());
                    realmObject = new io.realm.com_kg_gettransfer_data_model_secondary_TransportTypeRealmProxy();
                    cache.put(object, (RealmObjectProxy) realmObject);
                } finally {
                    objectContext.clear();
                }
            }
        }

        return (canUpdate) ? update(realm, realmObject, object, cache) : copy(realm, object, update, cache);
    }

    public static com.kg.gettransfer.data.model.secondary.TransportType copy(Realm realm, com.kg.gettransfer.data.model.secondary.TransportType newObject, boolean update, Map<RealmModel,RealmObjectProxy> cache) {
        RealmObjectProxy cachedRealmObject = cache.get(newObject);
        if (cachedRealmObject != null) {
            return (com.kg.gettransfer.data.model.secondary.TransportType) cachedRealmObject;
        }

        // rejecting default values to avoid creating unexpected objects from RealmModel/RealmList fields.
        com.kg.gettransfer.data.model.secondary.TransportType realmObject = realm.createObjectInternal(com.kg.gettransfer.data.model.secondary.TransportType.class, ((com_kg_gettransfer_data_model_secondary_TransportTypeRealmProxyInterface) newObject).realmGet$id(), false, Collections.<String>emptyList());
        cache.put(newObject, (RealmObjectProxy) realmObject);

        com_kg_gettransfer_data_model_secondary_TransportTypeRealmProxyInterface realmObjectSource = (com_kg_gettransfer_data_model_secondary_TransportTypeRealmProxyInterface) newObject;
        com_kg_gettransfer_data_model_secondary_TransportTypeRealmProxyInterface realmObjectCopy = (com_kg_gettransfer_data_model_secondary_TransportTypeRealmProxyInterface) realmObject;

        realmObjectCopy.realmSet$title(realmObjectSource.realmGet$title());
        realmObjectCopy.realmSet$paxMax(realmObjectSource.realmGet$paxMax());
        realmObjectCopy.realmSet$luggageMax(realmObjectSource.realmGet$luggageMax());
        return realmObject;
    }

    public static long insert(Realm realm, com.kg.gettransfer.data.model.secondary.TransportType object, Map<RealmModel,Long> cache) {
        if (object instanceof RealmObjectProxy && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm() != null && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm().getPath().equals(realm.getPath())) {
            return ((RealmObjectProxy) object).realmGet$proxyState().getRow$realm().getIndex();
        }
        Table table = realm.getTable(com.kg.gettransfer.data.model.secondary.TransportType.class);
        long tableNativePtr = table.getNativePtr();
        TransportTypeColumnInfo columnInfo = (TransportTypeColumnInfo) realm.getSchema().getColumnInfo(com.kg.gettransfer.data.model.secondary.TransportType.class);
        long pkColumnIndex = columnInfo.idIndex;
        long rowIndex = Table.NO_MATCH;
        Object primaryKeyValue = ((com_kg_gettransfer_data_model_secondary_TransportTypeRealmProxyInterface) object).realmGet$id();
        if (primaryKeyValue != null) {
            rowIndex = Table.nativeFindFirstString(tableNativePtr, pkColumnIndex, (String)primaryKeyValue);
        }
        if (rowIndex == Table.NO_MATCH) {
            rowIndex = OsObject.createRowWithPrimaryKey(table, pkColumnIndex, primaryKeyValue);
        } else {
            Table.throwDuplicatePrimaryKeyException(primaryKeyValue);
        }
        cache.put(object, rowIndex);
        String realmGet$title = ((com_kg_gettransfer_data_model_secondary_TransportTypeRealmProxyInterface) object).realmGet$title();
        if (realmGet$title != null) {
            Table.nativeSetString(tableNativePtr, columnInfo.titleIndex, rowIndex, realmGet$title, false);
        }
        Table.nativeSetLong(tableNativePtr, columnInfo.paxMaxIndex, rowIndex, ((com_kg_gettransfer_data_model_secondary_TransportTypeRealmProxyInterface) object).realmGet$paxMax(), false);
        Table.nativeSetLong(tableNativePtr, columnInfo.luggageMaxIndex, rowIndex, ((com_kg_gettransfer_data_model_secondary_TransportTypeRealmProxyInterface) object).realmGet$luggageMax(), false);
        return rowIndex;
    }

    public static void insert(Realm realm, Iterator<? extends RealmModel> objects, Map<RealmModel,Long> cache) {
        Table table = realm.getTable(com.kg.gettransfer.data.model.secondary.TransportType.class);
        long tableNativePtr = table.getNativePtr();
        TransportTypeColumnInfo columnInfo = (TransportTypeColumnInfo) realm.getSchema().getColumnInfo(com.kg.gettransfer.data.model.secondary.TransportType.class);
        long pkColumnIndex = columnInfo.idIndex;
        com.kg.gettransfer.data.model.secondary.TransportType object = null;
        while (objects.hasNext()) {
            object = (com.kg.gettransfer.data.model.secondary.TransportType) objects.next();
            if (cache.containsKey(object)) {
                continue;
            }
            if (object instanceof RealmObjectProxy && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm() != null && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm().getPath().equals(realm.getPath())) {
                cache.put(object, ((RealmObjectProxy) object).realmGet$proxyState().getRow$realm().getIndex());
                continue;
            }
            long rowIndex = Table.NO_MATCH;
            Object primaryKeyValue = ((com_kg_gettransfer_data_model_secondary_TransportTypeRealmProxyInterface) object).realmGet$id();
            if (primaryKeyValue != null) {
                rowIndex = Table.nativeFindFirstString(tableNativePtr, pkColumnIndex, (String)primaryKeyValue);
            }
            if (rowIndex == Table.NO_MATCH) {
                rowIndex = OsObject.createRowWithPrimaryKey(table, pkColumnIndex, primaryKeyValue);
            } else {
                Table.throwDuplicatePrimaryKeyException(primaryKeyValue);
            }
            cache.put(object, rowIndex);
            String realmGet$title = ((com_kg_gettransfer_data_model_secondary_TransportTypeRealmProxyInterface) object).realmGet$title();
            if (realmGet$title != null) {
                Table.nativeSetString(tableNativePtr, columnInfo.titleIndex, rowIndex, realmGet$title, false);
            }
            Table.nativeSetLong(tableNativePtr, columnInfo.paxMaxIndex, rowIndex, ((com_kg_gettransfer_data_model_secondary_TransportTypeRealmProxyInterface) object).realmGet$paxMax(), false);
            Table.nativeSetLong(tableNativePtr, columnInfo.luggageMaxIndex, rowIndex, ((com_kg_gettransfer_data_model_secondary_TransportTypeRealmProxyInterface) object).realmGet$luggageMax(), false);
        }
    }

    public static long insertOrUpdate(Realm realm, com.kg.gettransfer.data.model.secondary.TransportType object, Map<RealmModel,Long> cache) {
        if (object instanceof RealmObjectProxy && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm() != null && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm().getPath().equals(realm.getPath())) {
            return ((RealmObjectProxy) object).realmGet$proxyState().getRow$realm().getIndex();
        }
        Table table = realm.getTable(com.kg.gettransfer.data.model.secondary.TransportType.class);
        long tableNativePtr = table.getNativePtr();
        TransportTypeColumnInfo columnInfo = (TransportTypeColumnInfo) realm.getSchema().getColumnInfo(com.kg.gettransfer.data.model.secondary.TransportType.class);
        long pkColumnIndex = columnInfo.idIndex;
        long rowIndex = Table.NO_MATCH;
        Object primaryKeyValue = ((com_kg_gettransfer_data_model_secondary_TransportTypeRealmProxyInterface) object).realmGet$id();
        if (primaryKeyValue != null) {
            rowIndex = Table.nativeFindFirstString(tableNativePtr, pkColumnIndex, (String)primaryKeyValue);
        }
        if (rowIndex == Table.NO_MATCH) {
            rowIndex = OsObject.createRowWithPrimaryKey(table, pkColumnIndex, primaryKeyValue);
        }
        cache.put(object, rowIndex);
        String realmGet$title = ((com_kg_gettransfer_data_model_secondary_TransportTypeRealmProxyInterface) object).realmGet$title();
        if (realmGet$title != null) {
            Table.nativeSetString(tableNativePtr, columnInfo.titleIndex, rowIndex, realmGet$title, false);
        } else {
            Table.nativeSetNull(tableNativePtr, columnInfo.titleIndex, rowIndex, false);
        }
        Table.nativeSetLong(tableNativePtr, columnInfo.paxMaxIndex, rowIndex, ((com_kg_gettransfer_data_model_secondary_TransportTypeRealmProxyInterface) object).realmGet$paxMax(), false);
        Table.nativeSetLong(tableNativePtr, columnInfo.luggageMaxIndex, rowIndex, ((com_kg_gettransfer_data_model_secondary_TransportTypeRealmProxyInterface) object).realmGet$luggageMax(), false);
        return rowIndex;
    }

    public static void insertOrUpdate(Realm realm, Iterator<? extends RealmModel> objects, Map<RealmModel,Long> cache) {
        Table table = realm.getTable(com.kg.gettransfer.data.model.secondary.TransportType.class);
        long tableNativePtr = table.getNativePtr();
        TransportTypeColumnInfo columnInfo = (TransportTypeColumnInfo) realm.getSchema().getColumnInfo(com.kg.gettransfer.data.model.secondary.TransportType.class);
        long pkColumnIndex = columnInfo.idIndex;
        com.kg.gettransfer.data.model.secondary.TransportType object = null;
        while (objects.hasNext()) {
            object = (com.kg.gettransfer.data.model.secondary.TransportType) objects.next();
            if (cache.containsKey(object)) {
                continue;
            }
            if (object instanceof RealmObjectProxy && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm() != null && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm().getPath().equals(realm.getPath())) {
                cache.put(object, ((RealmObjectProxy) object).realmGet$proxyState().getRow$realm().getIndex());
                continue;
            }
            long rowIndex = Table.NO_MATCH;
            Object primaryKeyValue = ((com_kg_gettransfer_data_model_secondary_TransportTypeRealmProxyInterface) object).realmGet$id();
            if (primaryKeyValue != null) {
                rowIndex = Table.nativeFindFirstString(tableNativePtr, pkColumnIndex, (String)primaryKeyValue);
            }
            if (rowIndex == Table.NO_MATCH) {
                rowIndex = OsObject.createRowWithPrimaryKey(table, pkColumnIndex, primaryKeyValue);
            }
            cache.put(object, rowIndex);
            String realmGet$title = ((com_kg_gettransfer_data_model_secondary_TransportTypeRealmProxyInterface) object).realmGet$title();
            if (realmGet$title != null) {
                Table.nativeSetString(tableNativePtr, columnInfo.titleIndex, rowIndex, realmGet$title, false);
            } else {
                Table.nativeSetNull(tableNativePtr, columnInfo.titleIndex, rowIndex, false);
            }
            Table.nativeSetLong(tableNativePtr, columnInfo.paxMaxIndex, rowIndex, ((com_kg_gettransfer_data_model_secondary_TransportTypeRealmProxyInterface) object).realmGet$paxMax(), false);
            Table.nativeSetLong(tableNativePtr, columnInfo.luggageMaxIndex, rowIndex, ((com_kg_gettransfer_data_model_secondary_TransportTypeRealmProxyInterface) object).realmGet$luggageMax(), false);
        }
    }

    public static com.kg.gettransfer.data.model.secondary.TransportType createDetachedCopy(com.kg.gettransfer.data.model.secondary.TransportType realmObject, int currentDepth, int maxDepth, Map<RealmModel, CacheData<RealmModel>> cache) {
        if (currentDepth > maxDepth || realmObject == null) {
            return null;
        }
        CacheData<RealmModel> cachedObject = cache.get(realmObject);
        com.kg.gettransfer.data.model.secondary.TransportType unmanagedObject;
        if (cachedObject == null) {
            unmanagedObject = new com.kg.gettransfer.data.model.secondary.TransportType();
            cache.put(realmObject, new RealmObjectProxy.CacheData<RealmModel>(currentDepth, unmanagedObject));
        } else {
            // Reuse cached object or recreate it because it was encountered at a lower depth.
            if (currentDepth >= cachedObject.minDepth) {
                return (com.kg.gettransfer.data.model.secondary.TransportType) cachedObject.object;
            }
            unmanagedObject = (com.kg.gettransfer.data.model.secondary.TransportType) cachedObject.object;
            cachedObject.minDepth = currentDepth;
        }
        com_kg_gettransfer_data_model_secondary_TransportTypeRealmProxyInterface unmanagedCopy = (com_kg_gettransfer_data_model_secondary_TransportTypeRealmProxyInterface) unmanagedObject;
        com_kg_gettransfer_data_model_secondary_TransportTypeRealmProxyInterface realmSource = (com_kg_gettransfer_data_model_secondary_TransportTypeRealmProxyInterface) realmObject;
        unmanagedCopy.realmSet$id(realmSource.realmGet$id());
        unmanagedCopy.realmSet$title(realmSource.realmGet$title());
        unmanagedCopy.realmSet$paxMax(realmSource.realmGet$paxMax());
        unmanagedCopy.realmSet$luggageMax(realmSource.realmGet$luggageMax());

        return unmanagedObject;
    }

    static com.kg.gettransfer.data.model.secondary.TransportType update(Realm realm, com.kg.gettransfer.data.model.secondary.TransportType realmObject, com.kg.gettransfer.data.model.secondary.TransportType newObject, Map<RealmModel, RealmObjectProxy> cache) {
        com_kg_gettransfer_data_model_secondary_TransportTypeRealmProxyInterface realmObjectTarget = (com_kg_gettransfer_data_model_secondary_TransportTypeRealmProxyInterface) realmObject;
        com_kg_gettransfer_data_model_secondary_TransportTypeRealmProxyInterface realmObjectSource = (com_kg_gettransfer_data_model_secondary_TransportTypeRealmProxyInterface) newObject;
        realmObjectTarget.realmSet$title(realmObjectSource.realmGet$title());
        realmObjectTarget.realmSet$paxMax(realmObjectSource.realmGet$paxMax());
        realmObjectTarget.realmSet$luggageMax(realmObjectSource.realmGet$luggageMax());
        return realmObject;
    }

    @Override
    @SuppressWarnings("ArrayToString")
    public String toString() {
        if (!RealmObject.isValid(this)) {
            return "Invalid object";
        }
        StringBuilder stringBuilder = new StringBuilder("TransportType = proxy[");
        stringBuilder.append("{id:");
        stringBuilder.append(realmGet$id());
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{title:");
        stringBuilder.append(realmGet$title());
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{paxMax:");
        stringBuilder.append(realmGet$paxMax());
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{luggageMax:");
        stringBuilder.append(realmGet$luggageMax());
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
        com_kg_gettransfer_data_model_secondary_TransportTypeRealmProxy aTransportType = (com_kg_gettransfer_data_model_secondary_TransportTypeRealmProxy)o;

        String path = proxyState.getRealm$realm().getPath();
        String otherPath = aTransportType.proxyState.getRealm$realm().getPath();
        if (path != null ? !path.equals(otherPath) : otherPath != null) return false;

        String tableName = proxyState.getRow$realm().getTable().getName();
        String otherTableName = aTransportType.proxyState.getRow$realm().getTable().getName();
        if (tableName != null ? !tableName.equals(otherTableName) : otherTableName != null) return false;

        if (proxyState.getRow$realm().getIndex() != aTransportType.proxyState.getRow$realm().getIndex()) return false;

        return true;
    }
}
