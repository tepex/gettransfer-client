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
public class com_kg_gettransfer_data_model_AccountInfoRealmProxy extends com.kg.gettransfer.data.model.AccountInfo
    implements RealmObjectProxy, com_kg_gettransfer_data_model_AccountInfoRealmProxyInterface {

    static final class AccountInfoColumnInfo extends ColumnInfo {
        long emailIndex;
        long phoneIndex;
        long localeIndex;
        long currencyIndex;
        long distanceUnitIndex;
        long dateUpdatedIndex;

        AccountInfoColumnInfo(OsSchemaInfo schemaInfo) {
            super(6);
            OsObjectSchemaInfo objectSchemaInfo = schemaInfo.getObjectSchemaInfo("AccountInfo");
            this.emailIndex = addColumnDetails("email", "email", objectSchemaInfo);
            this.phoneIndex = addColumnDetails("phone", "phone", objectSchemaInfo);
            this.localeIndex = addColumnDetails("locale", "locale", objectSchemaInfo);
            this.currencyIndex = addColumnDetails("currency", "currency", objectSchemaInfo);
            this.distanceUnitIndex = addColumnDetails("distanceUnit", "distanceUnit", objectSchemaInfo);
            this.dateUpdatedIndex = addColumnDetails("dateUpdated", "dateUpdated", objectSchemaInfo);
        }

        AccountInfoColumnInfo(ColumnInfo src, boolean mutable) {
            super(src, mutable);
            copy(src, this);
        }

        @Override
        protected final ColumnInfo copy(boolean mutable) {
            return new AccountInfoColumnInfo(this, mutable);
        }

        @Override
        protected final void copy(ColumnInfo rawSrc, ColumnInfo rawDst) {
            final AccountInfoColumnInfo src = (AccountInfoColumnInfo) rawSrc;
            final AccountInfoColumnInfo dst = (AccountInfoColumnInfo) rawDst;
            dst.emailIndex = src.emailIndex;
            dst.phoneIndex = src.phoneIndex;
            dst.localeIndex = src.localeIndex;
            dst.currencyIndex = src.currencyIndex;
            dst.distanceUnitIndex = src.distanceUnitIndex;
            dst.dateUpdatedIndex = src.dateUpdatedIndex;
        }
    }

    private static final OsObjectSchemaInfo expectedObjectSchemaInfo = createExpectedObjectSchemaInfo();

    private AccountInfoColumnInfo columnInfo;
    private ProxyState<com.kg.gettransfer.data.model.AccountInfo> proxyState;

    com_kg_gettransfer_data_model_AccountInfoRealmProxy() {
        proxyState.setConstructionFinished();
    }

    @Override
    public void realm$injectObjectContext() {
        if (this.proxyState != null) {
            return;
        }
        final BaseRealm.RealmObjectContext context = BaseRealm.objectContext.get();
        this.columnInfo = (AccountInfoColumnInfo) context.getColumnInfo();
        this.proxyState = new ProxyState<com.kg.gettransfer.data.model.AccountInfo>(this);
        proxyState.setRealm$realm(context.getRealm());
        proxyState.setRow$realm(context.getRow());
        proxyState.setAcceptDefaultValue$realm(context.getAcceptDefaultValue());
        proxyState.setExcludeFields$realm(context.getExcludeFields());
    }

    @Override
    @SuppressWarnings("cast")
    public String realmGet$email() {
        proxyState.getRealm$realm().checkIfValid();
        return (java.lang.String) proxyState.getRow$realm().getString(columnInfo.emailIndex);
    }

    @Override
    public void realmSet$email(String value) {
        if (proxyState.isUnderConstruction()) {
            // default value of the primary key is always ignored.
            return;
        }

        proxyState.getRealm$realm().checkIfValid();
        throw new io.realm.exceptions.RealmException("Primary key field 'email' cannot be changed after object was created.");
    }

    @Override
    @SuppressWarnings("cast")
    public String realmGet$phone() {
        proxyState.getRealm$realm().checkIfValid();
        return (java.lang.String) proxyState.getRow$realm().getString(columnInfo.phoneIndex);
    }

    @Override
    public void realmSet$phone(String value) {
        if (proxyState.isUnderConstruction()) {
            if (!proxyState.getAcceptDefaultValue$realm()) {
                return;
            }
            final Row row = proxyState.getRow$realm();
            if (value == null) {
                row.getTable().setNull(columnInfo.phoneIndex, row.getIndex(), true);
                return;
            }
            row.getTable().setString(columnInfo.phoneIndex, row.getIndex(), value, true);
            return;
        }

        proxyState.getRealm$realm().checkIfValid();
        if (value == null) {
            proxyState.getRow$realm().setNull(columnInfo.phoneIndex);
            return;
        }
        proxyState.getRow$realm().setString(columnInfo.phoneIndex, value);
    }

    @Override
    @SuppressWarnings("cast")
    public String realmGet$locale() {
        proxyState.getRealm$realm().checkIfValid();
        return (java.lang.String) proxyState.getRow$realm().getString(columnInfo.localeIndex);
    }

    @Override
    public void realmSet$locale(String value) {
        if (proxyState.isUnderConstruction()) {
            if (!proxyState.getAcceptDefaultValue$realm()) {
                return;
            }
            final Row row = proxyState.getRow$realm();
            if (value == null) {
                row.getTable().setNull(columnInfo.localeIndex, row.getIndex(), true);
                return;
            }
            row.getTable().setString(columnInfo.localeIndex, row.getIndex(), value, true);
            return;
        }

        proxyState.getRealm$realm().checkIfValid();
        if (value == null) {
            proxyState.getRow$realm().setNull(columnInfo.localeIndex);
            return;
        }
        proxyState.getRow$realm().setString(columnInfo.localeIndex, value);
    }

    @Override
    @SuppressWarnings("cast")
    public String realmGet$currency() {
        proxyState.getRealm$realm().checkIfValid();
        return (java.lang.String) proxyState.getRow$realm().getString(columnInfo.currencyIndex);
    }

    @Override
    public void realmSet$currency(String value) {
        if (proxyState.isUnderConstruction()) {
            if (!proxyState.getAcceptDefaultValue$realm()) {
                return;
            }
            final Row row = proxyState.getRow$realm();
            if (value == null) {
                throw new IllegalArgumentException("Trying to set non-nullable field 'currency' to null.");
            }
            row.getTable().setString(columnInfo.currencyIndex, row.getIndex(), value, true);
            return;
        }

        proxyState.getRealm$realm().checkIfValid();
        if (value == null) {
            throw new IllegalArgumentException("Trying to set non-nullable field 'currency' to null.");
        }
        proxyState.getRow$realm().setString(columnInfo.currencyIndex, value);
    }

    @Override
    @SuppressWarnings("cast")
    public String realmGet$distanceUnit() {
        proxyState.getRealm$realm().checkIfValid();
        return (java.lang.String) proxyState.getRow$realm().getString(columnInfo.distanceUnitIndex);
    }

    @Override
    public void realmSet$distanceUnit(String value) {
        if (proxyState.isUnderConstruction()) {
            if (!proxyState.getAcceptDefaultValue$realm()) {
                return;
            }
            final Row row = proxyState.getRow$realm();
            if (value == null) {
                throw new IllegalArgumentException("Trying to set non-nullable field 'distanceUnit' to null.");
            }
            row.getTable().setString(columnInfo.distanceUnitIndex, row.getIndex(), value, true);
            return;
        }

        proxyState.getRealm$realm().checkIfValid();
        if (value == null) {
            throw new IllegalArgumentException("Trying to set non-nullable field 'distanceUnit' to null.");
        }
        proxyState.getRow$realm().setString(columnInfo.distanceUnitIndex, value);
    }

    @Override
    @SuppressWarnings("cast")
    public Date realmGet$dateUpdated() {
        proxyState.getRealm$realm().checkIfValid();
        if (proxyState.getRow$realm().isNull(columnInfo.dateUpdatedIndex)) {
            return null;
        }
        return (java.util.Date) proxyState.getRow$realm().getDate(columnInfo.dateUpdatedIndex);
    }

    @Override
    public void realmSet$dateUpdated(Date value) {
        if (proxyState.isUnderConstruction()) {
            if (!proxyState.getAcceptDefaultValue$realm()) {
                return;
            }
            final Row row = proxyState.getRow$realm();
            if (value == null) {
                row.getTable().setNull(columnInfo.dateUpdatedIndex, row.getIndex(), true);
                return;
            }
            row.getTable().setDate(columnInfo.dateUpdatedIndex, row.getIndex(), value, true);
            return;
        }

        proxyState.getRealm$realm().checkIfValid();
        if (value == null) {
            proxyState.getRow$realm().setNull(columnInfo.dateUpdatedIndex);
            return;
        }
        proxyState.getRow$realm().setDate(columnInfo.dateUpdatedIndex, value);
    }

    private static OsObjectSchemaInfo createExpectedObjectSchemaInfo() {
        OsObjectSchemaInfo.Builder builder = new OsObjectSchemaInfo.Builder("AccountInfo", 6, 0);
        builder.addPersistedProperty("email", RealmFieldType.STRING, Property.PRIMARY_KEY, Property.INDEXED, !Property.REQUIRED);
        builder.addPersistedProperty("phone", RealmFieldType.STRING, !Property.PRIMARY_KEY, !Property.INDEXED, !Property.REQUIRED);
        builder.addPersistedProperty("locale", RealmFieldType.STRING, !Property.PRIMARY_KEY, !Property.INDEXED, !Property.REQUIRED);
        builder.addPersistedProperty("currency", RealmFieldType.STRING, !Property.PRIMARY_KEY, !Property.INDEXED, Property.REQUIRED);
        builder.addPersistedProperty("distanceUnit", RealmFieldType.STRING, !Property.PRIMARY_KEY, !Property.INDEXED, Property.REQUIRED);
        builder.addPersistedProperty("dateUpdated", RealmFieldType.DATE, !Property.PRIMARY_KEY, !Property.INDEXED, !Property.REQUIRED);
        return builder.build();
    }

    public static OsObjectSchemaInfo getExpectedObjectSchemaInfo() {
        return expectedObjectSchemaInfo;
    }

    public static AccountInfoColumnInfo createColumnInfo(OsSchemaInfo schemaInfo) {
        return new AccountInfoColumnInfo(schemaInfo);
    }

    public static String getSimpleClassName() {
        return "AccountInfo";
    }

    public static final class ClassNameHelper {
        public static final String INTERNAL_CLASS_NAME = "AccountInfo";
    }

    @SuppressWarnings("cast")
    public static com.kg.gettransfer.data.model.AccountInfo createOrUpdateUsingJsonObject(Realm realm, JSONObject json, boolean update)
        throws JSONException {
        final List<String> excludeFields = Collections.<String> emptyList();
        com.kg.gettransfer.data.model.AccountInfo obj = null;
        if (update) {
            Table table = realm.getTable(com.kg.gettransfer.data.model.AccountInfo.class);
            AccountInfoColumnInfo columnInfo = (AccountInfoColumnInfo) realm.getSchema().getColumnInfo(com.kg.gettransfer.data.model.AccountInfo.class);
            long pkColumnIndex = columnInfo.emailIndex;
            long rowIndex = Table.NO_MATCH;
            if (json.isNull("email")) {
                rowIndex = table.findFirstNull(pkColumnIndex);
            } else {
                rowIndex = table.findFirstString(pkColumnIndex, json.getString("email"));
            }
            if (rowIndex != Table.NO_MATCH) {
                final BaseRealm.RealmObjectContext objectContext = BaseRealm.objectContext.get();
                try {
                    objectContext.set(realm, table.getUncheckedRow(rowIndex), realm.getSchema().getColumnInfo(com.kg.gettransfer.data.model.AccountInfo.class), false, Collections.<String> emptyList());
                    obj = new io.realm.com_kg_gettransfer_data_model_AccountInfoRealmProxy();
                } finally {
                    objectContext.clear();
                }
            }
        }
        if (obj == null) {
            if (json.has("email")) {
                if (json.isNull("email")) {
                    obj = (io.realm.com_kg_gettransfer_data_model_AccountInfoRealmProxy) realm.createObjectInternal(com.kg.gettransfer.data.model.AccountInfo.class, null, true, excludeFields);
                } else {
                    obj = (io.realm.com_kg_gettransfer_data_model_AccountInfoRealmProxy) realm.createObjectInternal(com.kg.gettransfer.data.model.AccountInfo.class, json.getString("email"), true, excludeFields);
                }
            } else {
                throw new IllegalArgumentException("JSON object doesn't have the primary key field 'email'.");
            }
        }

        final com_kg_gettransfer_data_model_AccountInfoRealmProxyInterface objProxy = (com_kg_gettransfer_data_model_AccountInfoRealmProxyInterface) obj;
        if (json.has("phone")) {
            if (json.isNull("phone")) {
                objProxy.realmSet$phone(null);
            } else {
                objProxy.realmSet$phone((String) json.getString("phone"));
            }
        }
        if (json.has("locale")) {
            if (json.isNull("locale")) {
                objProxy.realmSet$locale(null);
            } else {
                objProxy.realmSet$locale((String) json.getString("locale"));
            }
        }
        if (json.has("currency")) {
            if (json.isNull("currency")) {
                objProxy.realmSet$currency(null);
            } else {
                objProxy.realmSet$currency((String) json.getString("currency"));
            }
        }
        if (json.has("distanceUnit")) {
            if (json.isNull("distanceUnit")) {
                objProxy.realmSet$distanceUnit(null);
            } else {
                objProxy.realmSet$distanceUnit((String) json.getString("distanceUnit"));
            }
        }
        if (json.has("dateUpdated")) {
            if (json.isNull("dateUpdated")) {
                objProxy.realmSet$dateUpdated(null);
            } else {
                Object timestamp = json.get("dateUpdated");
                if (timestamp instanceof String) {
                    objProxy.realmSet$dateUpdated(JsonUtils.stringToDate((String) timestamp));
                } else {
                    objProxy.realmSet$dateUpdated(new Date(json.getLong("dateUpdated")));
                }
            }
        }
        return obj;
    }

    @SuppressWarnings("cast")
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static com.kg.gettransfer.data.model.AccountInfo createUsingJsonStream(Realm realm, JsonReader reader)
        throws IOException {
        boolean jsonHasPrimaryKey = false;
        final com.kg.gettransfer.data.model.AccountInfo obj = new com.kg.gettransfer.data.model.AccountInfo();
        final com_kg_gettransfer_data_model_AccountInfoRealmProxyInterface objProxy = (com_kg_gettransfer_data_model_AccountInfoRealmProxyInterface) obj;
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (false) {
            } else if (name.equals("email")) {
                if (reader.peek() != JsonToken.NULL) {
                    objProxy.realmSet$email((String) reader.nextString());
                } else {
                    reader.skipValue();
                    objProxy.realmSet$email(null);
                }
                jsonHasPrimaryKey = true;
            } else if (name.equals("phone")) {
                if (reader.peek() != JsonToken.NULL) {
                    objProxy.realmSet$phone((String) reader.nextString());
                } else {
                    reader.skipValue();
                    objProxy.realmSet$phone(null);
                }
            } else if (name.equals("locale")) {
                if (reader.peek() != JsonToken.NULL) {
                    objProxy.realmSet$locale((String) reader.nextString());
                } else {
                    reader.skipValue();
                    objProxy.realmSet$locale(null);
                }
            } else if (name.equals("currency")) {
                if (reader.peek() != JsonToken.NULL) {
                    objProxy.realmSet$currency((String) reader.nextString());
                } else {
                    reader.skipValue();
                    objProxy.realmSet$currency(null);
                }
            } else if (name.equals("distanceUnit")) {
                if (reader.peek() != JsonToken.NULL) {
                    objProxy.realmSet$distanceUnit((String) reader.nextString());
                } else {
                    reader.skipValue();
                    objProxy.realmSet$distanceUnit(null);
                }
            } else if (name.equals("dateUpdated")) {
                if (reader.peek() == JsonToken.NULL) {
                    reader.skipValue();
                    objProxy.realmSet$dateUpdated(null);
                } else if (reader.peek() == JsonToken.NUMBER) {
                    long timestamp = reader.nextLong();
                    if (timestamp > -1) {
                        objProxy.realmSet$dateUpdated(new Date(timestamp));
                    }
                } else {
                    objProxy.realmSet$dateUpdated(JsonUtils.stringToDate(reader.nextString()));
                }
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        if (!jsonHasPrimaryKey) {
            throw new IllegalArgumentException("JSON object doesn't have the primary key field 'email'.");
        }
        return realm.copyToRealm(obj);
    }

    public static com.kg.gettransfer.data.model.AccountInfo copyOrUpdate(Realm realm, com.kg.gettransfer.data.model.AccountInfo object, boolean update, Map<RealmModel,RealmObjectProxy> cache) {
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
            return (com.kg.gettransfer.data.model.AccountInfo) cachedRealmObject;
        }

        com.kg.gettransfer.data.model.AccountInfo realmObject = null;
        boolean canUpdate = update;
        if (canUpdate) {
            Table table = realm.getTable(com.kg.gettransfer.data.model.AccountInfo.class);
            AccountInfoColumnInfo columnInfo = (AccountInfoColumnInfo) realm.getSchema().getColumnInfo(com.kg.gettransfer.data.model.AccountInfo.class);
            long pkColumnIndex = columnInfo.emailIndex;
            String value = ((com_kg_gettransfer_data_model_AccountInfoRealmProxyInterface) object).realmGet$email();
            long rowIndex = Table.NO_MATCH;
            if (value == null) {
                rowIndex = table.findFirstNull(pkColumnIndex);
            } else {
                rowIndex = table.findFirstString(pkColumnIndex, value);
            }
            if (rowIndex == Table.NO_MATCH) {
                canUpdate = false;
            } else {
                try {
                    objectContext.set(realm, table.getUncheckedRow(rowIndex), realm.getSchema().getColumnInfo(com.kg.gettransfer.data.model.AccountInfo.class), false, Collections.<String> emptyList());
                    realmObject = new io.realm.com_kg_gettransfer_data_model_AccountInfoRealmProxy();
                    cache.put(object, (RealmObjectProxy) realmObject);
                } finally {
                    objectContext.clear();
                }
            }
        }

        return (canUpdate) ? update(realm, realmObject, object, cache) : copy(realm, object, update, cache);
    }

    public static com.kg.gettransfer.data.model.AccountInfo copy(Realm realm, com.kg.gettransfer.data.model.AccountInfo newObject, boolean update, Map<RealmModel,RealmObjectProxy> cache) {
        RealmObjectProxy cachedRealmObject = cache.get(newObject);
        if (cachedRealmObject != null) {
            return (com.kg.gettransfer.data.model.AccountInfo) cachedRealmObject;
        }

        // rejecting default values to avoid creating unexpected objects from RealmModel/RealmList fields.
        com.kg.gettransfer.data.model.AccountInfo realmObject = realm.createObjectInternal(com.kg.gettransfer.data.model.AccountInfo.class, ((com_kg_gettransfer_data_model_AccountInfoRealmProxyInterface) newObject).realmGet$email(), false, Collections.<String>emptyList());
        cache.put(newObject, (RealmObjectProxy) realmObject);

        com_kg_gettransfer_data_model_AccountInfoRealmProxyInterface realmObjectSource = (com_kg_gettransfer_data_model_AccountInfoRealmProxyInterface) newObject;
        com_kg_gettransfer_data_model_AccountInfoRealmProxyInterface realmObjectCopy = (com_kg_gettransfer_data_model_AccountInfoRealmProxyInterface) realmObject;

        realmObjectCopy.realmSet$phone(realmObjectSource.realmGet$phone());
        realmObjectCopy.realmSet$locale(realmObjectSource.realmGet$locale());
        realmObjectCopy.realmSet$currency(realmObjectSource.realmGet$currency());
        realmObjectCopy.realmSet$distanceUnit(realmObjectSource.realmGet$distanceUnit());
        realmObjectCopy.realmSet$dateUpdated(realmObjectSource.realmGet$dateUpdated());
        return realmObject;
    }

    public static long insert(Realm realm, com.kg.gettransfer.data.model.AccountInfo object, Map<RealmModel,Long> cache) {
        if (object instanceof RealmObjectProxy && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm() != null && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm().getPath().equals(realm.getPath())) {
            return ((RealmObjectProxy) object).realmGet$proxyState().getRow$realm().getIndex();
        }
        Table table = realm.getTable(com.kg.gettransfer.data.model.AccountInfo.class);
        long tableNativePtr = table.getNativePtr();
        AccountInfoColumnInfo columnInfo = (AccountInfoColumnInfo) realm.getSchema().getColumnInfo(com.kg.gettransfer.data.model.AccountInfo.class);
        long pkColumnIndex = columnInfo.emailIndex;
        String primaryKeyValue = ((com_kg_gettransfer_data_model_AccountInfoRealmProxyInterface) object).realmGet$email();
        long rowIndex = Table.NO_MATCH;
        if (primaryKeyValue == null) {
            rowIndex = Table.nativeFindFirstNull(tableNativePtr, pkColumnIndex);
        } else {
            rowIndex = Table.nativeFindFirstString(tableNativePtr, pkColumnIndex, primaryKeyValue);
        }
        if (rowIndex == Table.NO_MATCH) {
            rowIndex = OsObject.createRowWithPrimaryKey(table, pkColumnIndex, primaryKeyValue);
        } else {
            Table.throwDuplicatePrimaryKeyException(primaryKeyValue);
        }
        cache.put(object, rowIndex);
        String realmGet$phone = ((com_kg_gettransfer_data_model_AccountInfoRealmProxyInterface) object).realmGet$phone();
        if (realmGet$phone != null) {
            Table.nativeSetString(tableNativePtr, columnInfo.phoneIndex, rowIndex, realmGet$phone, false);
        }
        String realmGet$locale = ((com_kg_gettransfer_data_model_AccountInfoRealmProxyInterface) object).realmGet$locale();
        if (realmGet$locale != null) {
            Table.nativeSetString(tableNativePtr, columnInfo.localeIndex, rowIndex, realmGet$locale, false);
        }
        String realmGet$currency = ((com_kg_gettransfer_data_model_AccountInfoRealmProxyInterface) object).realmGet$currency();
        if (realmGet$currency != null) {
            Table.nativeSetString(tableNativePtr, columnInfo.currencyIndex, rowIndex, realmGet$currency, false);
        }
        String realmGet$distanceUnit = ((com_kg_gettransfer_data_model_AccountInfoRealmProxyInterface) object).realmGet$distanceUnit();
        if (realmGet$distanceUnit != null) {
            Table.nativeSetString(tableNativePtr, columnInfo.distanceUnitIndex, rowIndex, realmGet$distanceUnit, false);
        }
        java.util.Date realmGet$dateUpdated = ((com_kg_gettransfer_data_model_AccountInfoRealmProxyInterface) object).realmGet$dateUpdated();
        if (realmGet$dateUpdated != null) {
            Table.nativeSetTimestamp(tableNativePtr, columnInfo.dateUpdatedIndex, rowIndex, realmGet$dateUpdated.getTime(), false);
        }
        return rowIndex;
    }

    public static void insert(Realm realm, Iterator<? extends RealmModel> objects, Map<RealmModel,Long> cache) {
        Table table = realm.getTable(com.kg.gettransfer.data.model.AccountInfo.class);
        long tableNativePtr = table.getNativePtr();
        AccountInfoColumnInfo columnInfo = (AccountInfoColumnInfo) realm.getSchema().getColumnInfo(com.kg.gettransfer.data.model.AccountInfo.class);
        long pkColumnIndex = columnInfo.emailIndex;
        com.kg.gettransfer.data.model.AccountInfo object = null;
        while (objects.hasNext()) {
            object = (com.kg.gettransfer.data.model.AccountInfo) objects.next();
            if (cache.containsKey(object)) {
                continue;
            }
            if (object instanceof RealmObjectProxy && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm() != null && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm().getPath().equals(realm.getPath())) {
                cache.put(object, ((RealmObjectProxy) object).realmGet$proxyState().getRow$realm().getIndex());
                continue;
            }
            String primaryKeyValue = ((com_kg_gettransfer_data_model_AccountInfoRealmProxyInterface) object).realmGet$email();
            long rowIndex = Table.NO_MATCH;
            if (primaryKeyValue == null) {
                rowIndex = Table.nativeFindFirstNull(tableNativePtr, pkColumnIndex);
            } else {
                rowIndex = Table.nativeFindFirstString(tableNativePtr, pkColumnIndex, primaryKeyValue);
            }
            if (rowIndex == Table.NO_MATCH) {
                rowIndex = OsObject.createRowWithPrimaryKey(table, pkColumnIndex, primaryKeyValue);
            } else {
                Table.throwDuplicatePrimaryKeyException(primaryKeyValue);
            }
            cache.put(object, rowIndex);
            String realmGet$phone = ((com_kg_gettransfer_data_model_AccountInfoRealmProxyInterface) object).realmGet$phone();
            if (realmGet$phone != null) {
                Table.nativeSetString(tableNativePtr, columnInfo.phoneIndex, rowIndex, realmGet$phone, false);
            }
            String realmGet$locale = ((com_kg_gettransfer_data_model_AccountInfoRealmProxyInterface) object).realmGet$locale();
            if (realmGet$locale != null) {
                Table.nativeSetString(tableNativePtr, columnInfo.localeIndex, rowIndex, realmGet$locale, false);
            }
            String realmGet$currency = ((com_kg_gettransfer_data_model_AccountInfoRealmProxyInterface) object).realmGet$currency();
            if (realmGet$currency != null) {
                Table.nativeSetString(tableNativePtr, columnInfo.currencyIndex, rowIndex, realmGet$currency, false);
            }
            String realmGet$distanceUnit = ((com_kg_gettransfer_data_model_AccountInfoRealmProxyInterface) object).realmGet$distanceUnit();
            if (realmGet$distanceUnit != null) {
                Table.nativeSetString(tableNativePtr, columnInfo.distanceUnitIndex, rowIndex, realmGet$distanceUnit, false);
            }
            java.util.Date realmGet$dateUpdated = ((com_kg_gettransfer_data_model_AccountInfoRealmProxyInterface) object).realmGet$dateUpdated();
            if (realmGet$dateUpdated != null) {
                Table.nativeSetTimestamp(tableNativePtr, columnInfo.dateUpdatedIndex, rowIndex, realmGet$dateUpdated.getTime(), false);
            }
        }
    }

    public static long insertOrUpdate(Realm realm, com.kg.gettransfer.data.model.AccountInfo object, Map<RealmModel,Long> cache) {
        if (object instanceof RealmObjectProxy && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm() != null && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm().getPath().equals(realm.getPath())) {
            return ((RealmObjectProxy) object).realmGet$proxyState().getRow$realm().getIndex();
        }
        Table table = realm.getTable(com.kg.gettransfer.data.model.AccountInfo.class);
        long tableNativePtr = table.getNativePtr();
        AccountInfoColumnInfo columnInfo = (AccountInfoColumnInfo) realm.getSchema().getColumnInfo(com.kg.gettransfer.data.model.AccountInfo.class);
        long pkColumnIndex = columnInfo.emailIndex;
        String primaryKeyValue = ((com_kg_gettransfer_data_model_AccountInfoRealmProxyInterface) object).realmGet$email();
        long rowIndex = Table.NO_MATCH;
        if (primaryKeyValue == null) {
            rowIndex = Table.nativeFindFirstNull(tableNativePtr, pkColumnIndex);
        } else {
            rowIndex = Table.nativeFindFirstString(tableNativePtr, pkColumnIndex, primaryKeyValue);
        }
        if (rowIndex == Table.NO_MATCH) {
            rowIndex = OsObject.createRowWithPrimaryKey(table, pkColumnIndex, primaryKeyValue);
        }
        cache.put(object, rowIndex);
        String realmGet$phone = ((com_kg_gettransfer_data_model_AccountInfoRealmProxyInterface) object).realmGet$phone();
        if (realmGet$phone != null) {
            Table.nativeSetString(tableNativePtr, columnInfo.phoneIndex, rowIndex, realmGet$phone, false);
        } else {
            Table.nativeSetNull(tableNativePtr, columnInfo.phoneIndex, rowIndex, false);
        }
        String realmGet$locale = ((com_kg_gettransfer_data_model_AccountInfoRealmProxyInterface) object).realmGet$locale();
        if (realmGet$locale != null) {
            Table.nativeSetString(tableNativePtr, columnInfo.localeIndex, rowIndex, realmGet$locale, false);
        } else {
            Table.nativeSetNull(tableNativePtr, columnInfo.localeIndex, rowIndex, false);
        }
        String realmGet$currency = ((com_kg_gettransfer_data_model_AccountInfoRealmProxyInterface) object).realmGet$currency();
        if (realmGet$currency != null) {
            Table.nativeSetString(tableNativePtr, columnInfo.currencyIndex, rowIndex, realmGet$currency, false);
        } else {
            Table.nativeSetNull(tableNativePtr, columnInfo.currencyIndex, rowIndex, false);
        }
        String realmGet$distanceUnit = ((com_kg_gettransfer_data_model_AccountInfoRealmProxyInterface) object).realmGet$distanceUnit();
        if (realmGet$distanceUnit != null) {
            Table.nativeSetString(tableNativePtr, columnInfo.distanceUnitIndex, rowIndex, realmGet$distanceUnit, false);
        } else {
            Table.nativeSetNull(tableNativePtr, columnInfo.distanceUnitIndex, rowIndex, false);
        }
        java.util.Date realmGet$dateUpdated = ((com_kg_gettransfer_data_model_AccountInfoRealmProxyInterface) object).realmGet$dateUpdated();
        if (realmGet$dateUpdated != null) {
            Table.nativeSetTimestamp(tableNativePtr, columnInfo.dateUpdatedIndex, rowIndex, realmGet$dateUpdated.getTime(), false);
        } else {
            Table.nativeSetNull(tableNativePtr, columnInfo.dateUpdatedIndex, rowIndex, false);
        }
        return rowIndex;
    }

    public static void insertOrUpdate(Realm realm, Iterator<? extends RealmModel> objects, Map<RealmModel,Long> cache) {
        Table table = realm.getTable(com.kg.gettransfer.data.model.AccountInfo.class);
        long tableNativePtr = table.getNativePtr();
        AccountInfoColumnInfo columnInfo = (AccountInfoColumnInfo) realm.getSchema().getColumnInfo(com.kg.gettransfer.data.model.AccountInfo.class);
        long pkColumnIndex = columnInfo.emailIndex;
        com.kg.gettransfer.data.model.AccountInfo object = null;
        while (objects.hasNext()) {
            object = (com.kg.gettransfer.data.model.AccountInfo) objects.next();
            if (cache.containsKey(object)) {
                continue;
            }
            if (object instanceof RealmObjectProxy && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm() != null && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm().getPath().equals(realm.getPath())) {
                cache.put(object, ((RealmObjectProxy) object).realmGet$proxyState().getRow$realm().getIndex());
                continue;
            }
            String primaryKeyValue = ((com_kg_gettransfer_data_model_AccountInfoRealmProxyInterface) object).realmGet$email();
            long rowIndex = Table.NO_MATCH;
            if (primaryKeyValue == null) {
                rowIndex = Table.nativeFindFirstNull(tableNativePtr, pkColumnIndex);
            } else {
                rowIndex = Table.nativeFindFirstString(tableNativePtr, pkColumnIndex, primaryKeyValue);
            }
            if (rowIndex == Table.NO_MATCH) {
                rowIndex = OsObject.createRowWithPrimaryKey(table, pkColumnIndex, primaryKeyValue);
            }
            cache.put(object, rowIndex);
            String realmGet$phone = ((com_kg_gettransfer_data_model_AccountInfoRealmProxyInterface) object).realmGet$phone();
            if (realmGet$phone != null) {
                Table.nativeSetString(tableNativePtr, columnInfo.phoneIndex, rowIndex, realmGet$phone, false);
            } else {
                Table.nativeSetNull(tableNativePtr, columnInfo.phoneIndex, rowIndex, false);
            }
            String realmGet$locale = ((com_kg_gettransfer_data_model_AccountInfoRealmProxyInterface) object).realmGet$locale();
            if (realmGet$locale != null) {
                Table.nativeSetString(tableNativePtr, columnInfo.localeIndex, rowIndex, realmGet$locale, false);
            } else {
                Table.nativeSetNull(tableNativePtr, columnInfo.localeIndex, rowIndex, false);
            }
            String realmGet$currency = ((com_kg_gettransfer_data_model_AccountInfoRealmProxyInterface) object).realmGet$currency();
            if (realmGet$currency != null) {
                Table.nativeSetString(tableNativePtr, columnInfo.currencyIndex, rowIndex, realmGet$currency, false);
            } else {
                Table.nativeSetNull(tableNativePtr, columnInfo.currencyIndex, rowIndex, false);
            }
            String realmGet$distanceUnit = ((com_kg_gettransfer_data_model_AccountInfoRealmProxyInterface) object).realmGet$distanceUnit();
            if (realmGet$distanceUnit != null) {
                Table.nativeSetString(tableNativePtr, columnInfo.distanceUnitIndex, rowIndex, realmGet$distanceUnit, false);
            } else {
                Table.nativeSetNull(tableNativePtr, columnInfo.distanceUnitIndex, rowIndex, false);
            }
            java.util.Date realmGet$dateUpdated = ((com_kg_gettransfer_data_model_AccountInfoRealmProxyInterface) object).realmGet$dateUpdated();
            if (realmGet$dateUpdated != null) {
                Table.nativeSetTimestamp(tableNativePtr, columnInfo.dateUpdatedIndex, rowIndex, realmGet$dateUpdated.getTime(), false);
            } else {
                Table.nativeSetNull(tableNativePtr, columnInfo.dateUpdatedIndex, rowIndex, false);
            }
        }
    }

    public static com.kg.gettransfer.data.model.AccountInfo createDetachedCopy(com.kg.gettransfer.data.model.AccountInfo realmObject, int currentDepth, int maxDepth, Map<RealmModel, CacheData<RealmModel>> cache) {
        if (currentDepth > maxDepth || realmObject == null) {
            return null;
        }
        CacheData<RealmModel> cachedObject = cache.get(realmObject);
        com.kg.gettransfer.data.model.AccountInfo unmanagedObject;
        if (cachedObject == null) {
            unmanagedObject = new com.kg.gettransfer.data.model.AccountInfo();
            cache.put(realmObject, new RealmObjectProxy.CacheData<RealmModel>(currentDepth, unmanagedObject));
        } else {
            // Reuse cached object or recreate it because it was encountered at a lower depth.
            if (currentDepth >= cachedObject.minDepth) {
                return (com.kg.gettransfer.data.model.AccountInfo) cachedObject.object;
            }
            unmanagedObject = (com.kg.gettransfer.data.model.AccountInfo) cachedObject.object;
            cachedObject.minDepth = currentDepth;
        }
        com_kg_gettransfer_data_model_AccountInfoRealmProxyInterface unmanagedCopy = (com_kg_gettransfer_data_model_AccountInfoRealmProxyInterface) unmanagedObject;
        com_kg_gettransfer_data_model_AccountInfoRealmProxyInterface realmSource = (com_kg_gettransfer_data_model_AccountInfoRealmProxyInterface) realmObject;
        unmanagedCopy.realmSet$email(realmSource.realmGet$email());
        unmanagedCopy.realmSet$phone(realmSource.realmGet$phone());
        unmanagedCopy.realmSet$locale(realmSource.realmGet$locale());
        unmanagedCopy.realmSet$currency(realmSource.realmGet$currency());
        unmanagedCopy.realmSet$distanceUnit(realmSource.realmGet$distanceUnit());
        unmanagedCopy.realmSet$dateUpdated(realmSource.realmGet$dateUpdated());

        return unmanagedObject;
    }

    static com.kg.gettransfer.data.model.AccountInfo update(Realm realm, com.kg.gettransfer.data.model.AccountInfo realmObject, com.kg.gettransfer.data.model.AccountInfo newObject, Map<RealmModel, RealmObjectProxy> cache) {
        com_kg_gettransfer_data_model_AccountInfoRealmProxyInterface realmObjectTarget = (com_kg_gettransfer_data_model_AccountInfoRealmProxyInterface) realmObject;
        com_kg_gettransfer_data_model_AccountInfoRealmProxyInterface realmObjectSource = (com_kg_gettransfer_data_model_AccountInfoRealmProxyInterface) newObject;
        realmObjectTarget.realmSet$phone(realmObjectSource.realmGet$phone());
        realmObjectTarget.realmSet$locale(realmObjectSource.realmGet$locale());
        realmObjectTarget.realmSet$currency(realmObjectSource.realmGet$currency());
        realmObjectTarget.realmSet$distanceUnit(realmObjectSource.realmGet$distanceUnit());
        realmObjectTarget.realmSet$dateUpdated(realmObjectSource.realmGet$dateUpdated());
        return realmObject;
    }

    @Override
    @SuppressWarnings("ArrayToString")
    public String toString() {
        if (!RealmObject.isValid(this)) {
            return "Invalid object";
        }
        StringBuilder stringBuilder = new StringBuilder("AccountInfo = proxy[");
        stringBuilder.append("{email:");
        stringBuilder.append(realmGet$email() != null ? realmGet$email() : "null");
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{phone:");
        stringBuilder.append(realmGet$phone() != null ? realmGet$phone() : "null");
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{locale:");
        stringBuilder.append(realmGet$locale() != null ? realmGet$locale() : "null");
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{currency:");
        stringBuilder.append(realmGet$currency());
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{distanceUnit:");
        stringBuilder.append(realmGet$distanceUnit());
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{dateUpdated:");
        stringBuilder.append(realmGet$dateUpdated() != null ? realmGet$dateUpdated() : "null");
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
        com_kg_gettransfer_data_model_AccountInfoRealmProxy aAccountInfo = (com_kg_gettransfer_data_model_AccountInfoRealmProxy)o;

        String path = proxyState.getRealm$realm().getPath();
        String otherPath = aAccountInfo.proxyState.getRealm$realm().getPath();
        if (path != null ? !path.equals(otherPath) : otherPath != null) return false;

        String tableName = proxyState.getRow$realm().getTable().getName();
        String otherTableName = aAccountInfo.proxyState.getRow$realm().getTable().getName();
        if (tableName != null ? !tableName.equals(otherTableName) : otherTableName != null) return false;

        if (proxyState.getRow$realm().getIndex() != aAccountInfo.proxyState.getRow$realm().getIndex()) return false;

        return true;
    }
}
