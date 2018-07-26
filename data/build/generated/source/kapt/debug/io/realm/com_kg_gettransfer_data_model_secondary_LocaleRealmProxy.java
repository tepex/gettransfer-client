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
public class com_kg_gettransfer_data_model_secondary_LocaleRealmProxy extends com.kg.gettransfer.data.model.secondary.Locale
    implements RealmObjectProxy, com_kg_gettransfer_data_model_secondary_LocaleRealmProxyInterface {

    static final class LocaleColumnInfo extends ColumnInfo {
        long codeIndex;
        long titleIndex;

        LocaleColumnInfo(OsSchemaInfo schemaInfo) {
            super(2);
            OsObjectSchemaInfo objectSchemaInfo = schemaInfo.getObjectSchemaInfo("Locale");
            this.codeIndex = addColumnDetails("code", "code", objectSchemaInfo);
            this.titleIndex = addColumnDetails("title", "title", objectSchemaInfo);
        }

        LocaleColumnInfo(ColumnInfo src, boolean mutable) {
            super(src, mutable);
            copy(src, this);
        }

        @Override
        protected final ColumnInfo copy(boolean mutable) {
            return new LocaleColumnInfo(this, mutable);
        }

        @Override
        protected final void copy(ColumnInfo rawSrc, ColumnInfo rawDst) {
            final LocaleColumnInfo src = (LocaleColumnInfo) rawSrc;
            final LocaleColumnInfo dst = (LocaleColumnInfo) rawDst;
            dst.codeIndex = src.codeIndex;
            dst.titleIndex = src.titleIndex;
        }
    }

    private static final OsObjectSchemaInfo expectedObjectSchemaInfo = createExpectedObjectSchemaInfo();

    private LocaleColumnInfo columnInfo;
    private ProxyState<com.kg.gettransfer.data.model.secondary.Locale> proxyState;

    com_kg_gettransfer_data_model_secondary_LocaleRealmProxy() {
        proxyState.setConstructionFinished();
    }

    @Override
    public void realm$injectObjectContext() {
        if (this.proxyState != null) {
            return;
        }
        final BaseRealm.RealmObjectContext context = BaseRealm.objectContext.get();
        this.columnInfo = (LocaleColumnInfo) context.getColumnInfo();
        this.proxyState = new ProxyState<com.kg.gettransfer.data.model.secondary.Locale>(this);
        proxyState.setRealm$realm(context.getRealm());
        proxyState.setRow$realm(context.getRow());
        proxyState.setAcceptDefaultValue$realm(context.getAcceptDefaultValue());
        proxyState.setExcludeFields$realm(context.getExcludeFields());
    }

    @Override
    @SuppressWarnings("cast")
    public String realmGet$code() {
        proxyState.getRealm$realm().checkIfValid();
        return (java.lang.String) proxyState.getRow$realm().getString(columnInfo.codeIndex);
    }

    @Override
    public void realmSet$code(String value) {
        if (proxyState.isUnderConstruction()) {
            if (!proxyState.getAcceptDefaultValue$realm()) {
                return;
            }
            final Row row = proxyState.getRow$realm();
            if (value == null) {
                throw new IllegalArgumentException("Trying to set non-nullable field 'code' to null.");
            }
            row.getTable().setString(columnInfo.codeIndex, row.getIndex(), value, true);
            return;
        }

        proxyState.getRealm$realm().checkIfValid();
        if (value == null) {
            throw new IllegalArgumentException("Trying to set non-nullable field 'code' to null.");
        }
        proxyState.getRow$realm().setString(columnInfo.codeIndex, value);
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

    private static OsObjectSchemaInfo createExpectedObjectSchemaInfo() {
        OsObjectSchemaInfo.Builder builder = new OsObjectSchemaInfo.Builder("Locale", 2, 0);
        builder.addPersistedProperty("code", RealmFieldType.STRING, !Property.PRIMARY_KEY, !Property.INDEXED, Property.REQUIRED);
        builder.addPersistedProperty("title", RealmFieldType.STRING, !Property.PRIMARY_KEY, !Property.INDEXED, Property.REQUIRED);
        return builder.build();
    }

    public static OsObjectSchemaInfo getExpectedObjectSchemaInfo() {
        return expectedObjectSchemaInfo;
    }

    public static LocaleColumnInfo createColumnInfo(OsSchemaInfo schemaInfo) {
        return new LocaleColumnInfo(schemaInfo);
    }

    public static String getSimpleClassName() {
        return "Locale";
    }

    public static final class ClassNameHelper {
        public static final String INTERNAL_CLASS_NAME = "Locale";
    }

    @SuppressWarnings("cast")
    public static com.kg.gettransfer.data.model.secondary.Locale createOrUpdateUsingJsonObject(Realm realm, JSONObject json, boolean update)
        throws JSONException {
        final List<String> excludeFields = Collections.<String> emptyList();
        com.kg.gettransfer.data.model.secondary.Locale obj = realm.createObjectInternal(com.kg.gettransfer.data.model.secondary.Locale.class, true, excludeFields);

        final com_kg_gettransfer_data_model_secondary_LocaleRealmProxyInterface objProxy = (com_kg_gettransfer_data_model_secondary_LocaleRealmProxyInterface) obj;
        if (json.has("code")) {
            if (json.isNull("code")) {
                objProxy.realmSet$code(null);
            } else {
                objProxy.realmSet$code((String) json.getString("code"));
            }
        }
        if (json.has("title")) {
            if (json.isNull("title")) {
                objProxy.realmSet$title(null);
            } else {
                objProxy.realmSet$title((String) json.getString("title"));
            }
        }
        return obj;
    }

    @SuppressWarnings("cast")
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static com.kg.gettransfer.data.model.secondary.Locale createUsingJsonStream(Realm realm, JsonReader reader)
        throws IOException {
        final com.kg.gettransfer.data.model.secondary.Locale obj = new com.kg.gettransfer.data.model.secondary.Locale();
        final com_kg_gettransfer_data_model_secondary_LocaleRealmProxyInterface objProxy = (com_kg_gettransfer_data_model_secondary_LocaleRealmProxyInterface) obj;
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (false) {
            } else if (name.equals("code")) {
                if (reader.peek() != JsonToken.NULL) {
                    objProxy.realmSet$code((String) reader.nextString());
                } else {
                    reader.skipValue();
                    objProxy.realmSet$code(null);
                }
            } else if (name.equals("title")) {
                if (reader.peek() != JsonToken.NULL) {
                    objProxy.realmSet$title((String) reader.nextString());
                } else {
                    reader.skipValue();
                    objProxy.realmSet$title(null);
                }
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return realm.copyToRealm(obj);
    }

    public static com.kg.gettransfer.data.model.secondary.Locale copyOrUpdate(Realm realm, com.kg.gettransfer.data.model.secondary.Locale object, boolean update, Map<RealmModel,RealmObjectProxy> cache) {
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
            return (com.kg.gettransfer.data.model.secondary.Locale) cachedRealmObject;
        }

        return copy(realm, object, update, cache);
    }

    public static com.kg.gettransfer.data.model.secondary.Locale copy(Realm realm, com.kg.gettransfer.data.model.secondary.Locale newObject, boolean update, Map<RealmModel,RealmObjectProxy> cache) {
        RealmObjectProxy cachedRealmObject = cache.get(newObject);
        if (cachedRealmObject != null) {
            return (com.kg.gettransfer.data.model.secondary.Locale) cachedRealmObject;
        }

        // rejecting default values to avoid creating unexpected objects from RealmModel/RealmList fields.
        com.kg.gettransfer.data.model.secondary.Locale realmObject = realm.createObjectInternal(com.kg.gettransfer.data.model.secondary.Locale.class, false, Collections.<String>emptyList());
        cache.put(newObject, (RealmObjectProxy) realmObject);

        com_kg_gettransfer_data_model_secondary_LocaleRealmProxyInterface realmObjectSource = (com_kg_gettransfer_data_model_secondary_LocaleRealmProxyInterface) newObject;
        com_kg_gettransfer_data_model_secondary_LocaleRealmProxyInterface realmObjectCopy = (com_kg_gettransfer_data_model_secondary_LocaleRealmProxyInterface) realmObject;

        realmObjectCopy.realmSet$code(realmObjectSource.realmGet$code());
        realmObjectCopy.realmSet$title(realmObjectSource.realmGet$title());
        return realmObject;
    }

    public static long insert(Realm realm, com.kg.gettransfer.data.model.secondary.Locale object, Map<RealmModel,Long> cache) {
        if (object instanceof RealmObjectProxy && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm() != null && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm().getPath().equals(realm.getPath())) {
            return ((RealmObjectProxy) object).realmGet$proxyState().getRow$realm().getIndex();
        }
        Table table = realm.getTable(com.kg.gettransfer.data.model.secondary.Locale.class);
        long tableNativePtr = table.getNativePtr();
        LocaleColumnInfo columnInfo = (LocaleColumnInfo) realm.getSchema().getColumnInfo(com.kg.gettransfer.data.model.secondary.Locale.class);
        long rowIndex = OsObject.createRow(table);
        cache.put(object, rowIndex);
        String realmGet$code = ((com_kg_gettransfer_data_model_secondary_LocaleRealmProxyInterface) object).realmGet$code();
        if (realmGet$code != null) {
            Table.nativeSetString(tableNativePtr, columnInfo.codeIndex, rowIndex, realmGet$code, false);
        }
        String realmGet$title = ((com_kg_gettransfer_data_model_secondary_LocaleRealmProxyInterface) object).realmGet$title();
        if (realmGet$title != null) {
            Table.nativeSetString(tableNativePtr, columnInfo.titleIndex, rowIndex, realmGet$title, false);
        }
        return rowIndex;
    }

    public static void insert(Realm realm, Iterator<? extends RealmModel> objects, Map<RealmModel,Long> cache) {
        Table table = realm.getTable(com.kg.gettransfer.data.model.secondary.Locale.class);
        long tableNativePtr = table.getNativePtr();
        LocaleColumnInfo columnInfo = (LocaleColumnInfo) realm.getSchema().getColumnInfo(com.kg.gettransfer.data.model.secondary.Locale.class);
        com.kg.gettransfer.data.model.secondary.Locale object = null;
        while (objects.hasNext()) {
            object = (com.kg.gettransfer.data.model.secondary.Locale) objects.next();
            if (cache.containsKey(object)) {
                continue;
            }
            if (object instanceof RealmObjectProxy && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm() != null && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm().getPath().equals(realm.getPath())) {
                cache.put(object, ((RealmObjectProxy) object).realmGet$proxyState().getRow$realm().getIndex());
                continue;
            }
            long rowIndex = OsObject.createRow(table);
            cache.put(object, rowIndex);
            String realmGet$code = ((com_kg_gettransfer_data_model_secondary_LocaleRealmProxyInterface) object).realmGet$code();
            if (realmGet$code != null) {
                Table.nativeSetString(tableNativePtr, columnInfo.codeIndex, rowIndex, realmGet$code, false);
            }
            String realmGet$title = ((com_kg_gettransfer_data_model_secondary_LocaleRealmProxyInterface) object).realmGet$title();
            if (realmGet$title != null) {
                Table.nativeSetString(tableNativePtr, columnInfo.titleIndex, rowIndex, realmGet$title, false);
            }
        }
    }

    public static long insertOrUpdate(Realm realm, com.kg.gettransfer.data.model.secondary.Locale object, Map<RealmModel,Long> cache) {
        if (object instanceof RealmObjectProxy && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm() != null && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm().getPath().equals(realm.getPath())) {
            return ((RealmObjectProxy) object).realmGet$proxyState().getRow$realm().getIndex();
        }
        Table table = realm.getTable(com.kg.gettransfer.data.model.secondary.Locale.class);
        long tableNativePtr = table.getNativePtr();
        LocaleColumnInfo columnInfo = (LocaleColumnInfo) realm.getSchema().getColumnInfo(com.kg.gettransfer.data.model.secondary.Locale.class);
        long rowIndex = OsObject.createRow(table);
        cache.put(object, rowIndex);
        String realmGet$code = ((com_kg_gettransfer_data_model_secondary_LocaleRealmProxyInterface) object).realmGet$code();
        if (realmGet$code != null) {
            Table.nativeSetString(tableNativePtr, columnInfo.codeIndex, rowIndex, realmGet$code, false);
        } else {
            Table.nativeSetNull(tableNativePtr, columnInfo.codeIndex, rowIndex, false);
        }
        String realmGet$title = ((com_kg_gettransfer_data_model_secondary_LocaleRealmProxyInterface) object).realmGet$title();
        if (realmGet$title != null) {
            Table.nativeSetString(tableNativePtr, columnInfo.titleIndex, rowIndex, realmGet$title, false);
        } else {
            Table.nativeSetNull(tableNativePtr, columnInfo.titleIndex, rowIndex, false);
        }
        return rowIndex;
    }

    public static void insertOrUpdate(Realm realm, Iterator<? extends RealmModel> objects, Map<RealmModel,Long> cache) {
        Table table = realm.getTable(com.kg.gettransfer.data.model.secondary.Locale.class);
        long tableNativePtr = table.getNativePtr();
        LocaleColumnInfo columnInfo = (LocaleColumnInfo) realm.getSchema().getColumnInfo(com.kg.gettransfer.data.model.secondary.Locale.class);
        com.kg.gettransfer.data.model.secondary.Locale object = null;
        while (objects.hasNext()) {
            object = (com.kg.gettransfer.data.model.secondary.Locale) objects.next();
            if (cache.containsKey(object)) {
                continue;
            }
            if (object instanceof RealmObjectProxy && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm() != null && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm().getPath().equals(realm.getPath())) {
                cache.put(object, ((RealmObjectProxy) object).realmGet$proxyState().getRow$realm().getIndex());
                continue;
            }
            long rowIndex = OsObject.createRow(table);
            cache.put(object, rowIndex);
            String realmGet$code = ((com_kg_gettransfer_data_model_secondary_LocaleRealmProxyInterface) object).realmGet$code();
            if (realmGet$code != null) {
                Table.nativeSetString(tableNativePtr, columnInfo.codeIndex, rowIndex, realmGet$code, false);
            } else {
                Table.nativeSetNull(tableNativePtr, columnInfo.codeIndex, rowIndex, false);
            }
            String realmGet$title = ((com_kg_gettransfer_data_model_secondary_LocaleRealmProxyInterface) object).realmGet$title();
            if (realmGet$title != null) {
                Table.nativeSetString(tableNativePtr, columnInfo.titleIndex, rowIndex, realmGet$title, false);
            } else {
                Table.nativeSetNull(tableNativePtr, columnInfo.titleIndex, rowIndex, false);
            }
        }
    }

    public static com.kg.gettransfer.data.model.secondary.Locale createDetachedCopy(com.kg.gettransfer.data.model.secondary.Locale realmObject, int currentDepth, int maxDepth, Map<RealmModel, CacheData<RealmModel>> cache) {
        if (currentDepth > maxDepth || realmObject == null) {
            return null;
        }
        CacheData<RealmModel> cachedObject = cache.get(realmObject);
        com.kg.gettransfer.data.model.secondary.Locale unmanagedObject;
        if (cachedObject == null) {
            unmanagedObject = new com.kg.gettransfer.data.model.secondary.Locale();
            cache.put(realmObject, new RealmObjectProxy.CacheData<RealmModel>(currentDepth, unmanagedObject));
        } else {
            // Reuse cached object or recreate it because it was encountered at a lower depth.
            if (currentDepth >= cachedObject.minDepth) {
                return (com.kg.gettransfer.data.model.secondary.Locale) cachedObject.object;
            }
            unmanagedObject = (com.kg.gettransfer.data.model.secondary.Locale) cachedObject.object;
            cachedObject.minDepth = currentDepth;
        }
        com_kg_gettransfer_data_model_secondary_LocaleRealmProxyInterface unmanagedCopy = (com_kg_gettransfer_data_model_secondary_LocaleRealmProxyInterface) unmanagedObject;
        com_kg_gettransfer_data_model_secondary_LocaleRealmProxyInterface realmSource = (com_kg_gettransfer_data_model_secondary_LocaleRealmProxyInterface) realmObject;
        unmanagedCopy.realmSet$code(realmSource.realmGet$code());
        unmanagedCopy.realmSet$title(realmSource.realmGet$title());

        return unmanagedObject;
    }

    @Override
    @SuppressWarnings("ArrayToString")
    public String toString() {
        if (!RealmObject.isValid(this)) {
            return "Invalid object";
        }
        StringBuilder stringBuilder = new StringBuilder("Locale = proxy[");
        stringBuilder.append("{code:");
        stringBuilder.append(realmGet$code());
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{title:");
        stringBuilder.append(realmGet$title());
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
        com_kg_gettransfer_data_model_secondary_LocaleRealmProxy aLocale = (com_kg_gettransfer_data_model_secondary_LocaleRealmProxy)o;

        String path = proxyState.getRealm$realm().getPath();
        String otherPath = aLocale.proxyState.getRealm$realm().getPath();
        if (path != null ? !path.equals(otherPath) : otherPath != null) return false;

        String tableName = proxyState.getRow$realm().getTable().getName();
        String otherTableName = aLocale.proxyState.getRow$realm().getTable().getName();
        if (tableName != null ? !tableName.equals(otherTableName) : otherTableName != null) return false;

        if (proxyState.getRow$realm().getIndex() != aLocale.proxyState.getRow$realm().getIndex()) return false;

        return true;
    }
}
