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
public class com_kg_gettransfer_data_model_ConfigRealmProxy extends com.kg.gettransfer.data.model.Config
    implements RealmObjectProxy, com_kg_gettransfer_data_model_ConfigRealmProxyInterface {

    static final class ConfigColumnInfo extends ColumnInfo {
        long idIndex;
        long availableLocalesIndex;
        long preferredLocaleIndex;
        long supportedCurrenciesIndex;
        long supportedDistanceUnitsIndex;

        ConfigColumnInfo(OsSchemaInfo schemaInfo) {
            super(5);
            OsObjectSchemaInfo objectSchemaInfo = schemaInfo.getObjectSchemaInfo("Config");
            this.idIndex = addColumnDetails("id", "id", objectSchemaInfo);
            this.availableLocalesIndex = addColumnDetails("availableLocales", "availableLocales", objectSchemaInfo);
            this.preferredLocaleIndex = addColumnDetails("preferredLocale", "preferredLocale", objectSchemaInfo);
            this.supportedCurrenciesIndex = addColumnDetails("supportedCurrencies", "supportedCurrencies", objectSchemaInfo);
            this.supportedDistanceUnitsIndex = addColumnDetails("supportedDistanceUnits", "supportedDistanceUnits", objectSchemaInfo);
        }

        ConfigColumnInfo(ColumnInfo src, boolean mutable) {
            super(src, mutable);
            copy(src, this);
        }

        @Override
        protected final ColumnInfo copy(boolean mutable) {
            return new ConfigColumnInfo(this, mutable);
        }

        @Override
        protected final void copy(ColumnInfo rawSrc, ColumnInfo rawDst) {
            final ConfigColumnInfo src = (ConfigColumnInfo) rawSrc;
            final ConfigColumnInfo dst = (ConfigColumnInfo) rawDst;
            dst.idIndex = src.idIndex;
            dst.availableLocalesIndex = src.availableLocalesIndex;
            dst.preferredLocaleIndex = src.preferredLocaleIndex;
            dst.supportedCurrenciesIndex = src.supportedCurrenciesIndex;
            dst.supportedDistanceUnitsIndex = src.supportedDistanceUnitsIndex;
        }
    }

    private static final OsObjectSchemaInfo expectedObjectSchemaInfo = createExpectedObjectSchemaInfo();

    private ConfigColumnInfo columnInfo;
    private ProxyState<com.kg.gettransfer.data.model.Config> proxyState;
    private RealmList<com.kg.gettransfer.data.model.secondary.Locale> availableLocalesRealmList;
    private RealmList<com.kg.gettransfer.data.model.secondary.Currency> supportedCurrenciesRealmList;
    private RealmList<String> supportedDistanceUnitsRealmList;

    com_kg_gettransfer_data_model_ConfigRealmProxy() {
        proxyState.setConstructionFinished();
    }

    @Override
    public void realm$injectObjectContext() {
        if (this.proxyState != null) {
            return;
        }
        final BaseRealm.RealmObjectContext context = BaseRealm.objectContext.get();
        this.columnInfo = (ConfigColumnInfo) context.getColumnInfo();
        this.proxyState = new ProxyState<com.kg.gettransfer.data.model.Config>(this);
        proxyState.setRealm$realm(context.getRealm());
        proxyState.setRow$realm(context.getRow());
        proxyState.setAcceptDefaultValue$realm(context.getAcceptDefaultValue());
        proxyState.setExcludeFields$realm(context.getExcludeFields());
    }

    @Override
    @SuppressWarnings("cast")
    public int realmGet$id() {
        proxyState.getRealm$realm().checkIfValid();
        return (int) proxyState.getRow$realm().getLong(columnInfo.idIndex);
    }

    @Override
    public void realmSet$id(int value) {
        if (proxyState.isUnderConstruction()) {
            // default value of the primary key is always ignored.
            return;
        }

        proxyState.getRealm$realm().checkIfValid();
        throw new io.realm.exceptions.RealmException("Primary key field 'id' cannot be changed after object was created.");
    }

    @Override
    public RealmList<com.kg.gettransfer.data.model.secondary.Locale> realmGet$availableLocales() {
        proxyState.getRealm$realm().checkIfValid();
        // use the cached value if available
        if (availableLocalesRealmList != null) {
            return availableLocalesRealmList;
        } else {
            OsList osList = proxyState.getRow$realm().getModelList(columnInfo.availableLocalesIndex);
            availableLocalesRealmList = new RealmList<com.kg.gettransfer.data.model.secondary.Locale>(com.kg.gettransfer.data.model.secondary.Locale.class, osList, proxyState.getRealm$realm());
            return availableLocalesRealmList;
        }
    }

    @Override
    public void realmSet$availableLocales(RealmList<com.kg.gettransfer.data.model.secondary.Locale> value) {
        if (proxyState.isUnderConstruction()) {
            if (!proxyState.getAcceptDefaultValue$realm()) {
                return;
            }
            if (proxyState.getExcludeFields$realm().contains("availableLocales")) {
                return;
            }
            // if the list contains unmanaged RealmObjects, convert them to managed.
            if (value != null && !value.isManaged()) {
                final Realm realm = (Realm) proxyState.getRealm$realm();
                final RealmList<com.kg.gettransfer.data.model.secondary.Locale> original = value;
                value = new RealmList<com.kg.gettransfer.data.model.secondary.Locale>();
                for (com.kg.gettransfer.data.model.secondary.Locale item : original) {
                    if (item == null || RealmObject.isManaged(item)) {
                        value.add(item);
                    } else {
                        value.add(realm.copyToRealm(item));
                    }
                }
            }
        }

        proxyState.getRealm$realm().checkIfValid();
        OsList osList = proxyState.getRow$realm().getModelList(columnInfo.availableLocalesIndex);
        // For lists of equal lengths, we need to set each element directly as clearing the receiver list can be wrong if the input and target list are the same.
        if (value != null && value.size() == osList.size()) {
            int objects = value.size();
            for (int i = 0; i < objects; i++) {
                com.kg.gettransfer.data.model.secondary.Locale linkedObject = value.get(i);
                proxyState.checkValidObject(linkedObject);
                osList.setRow(i, ((RealmObjectProxy) linkedObject).realmGet$proxyState().getRow$realm().getIndex());
            }
        } else {
            osList.removeAll();
            if (value == null) {
                return;
            }
            int objects = value.size();
            for (int i = 0; i < objects; i++) {
                com.kg.gettransfer.data.model.secondary.Locale linkedObject = value.get(i);
                proxyState.checkValidObject(linkedObject);
                osList.addRow(((RealmObjectProxy) linkedObject).realmGet$proxyState().getRow$realm().getIndex());
            }
        }
    }

    @Override
    @SuppressWarnings("cast")
    public String realmGet$preferredLocale() {
        proxyState.getRealm$realm().checkIfValid();
        return (java.lang.String) proxyState.getRow$realm().getString(columnInfo.preferredLocaleIndex);
    }

    @Override
    public void realmSet$preferredLocale(String value) {
        if (proxyState.isUnderConstruction()) {
            if (!proxyState.getAcceptDefaultValue$realm()) {
                return;
            }
            final Row row = proxyState.getRow$realm();
            if (value == null) {
                row.getTable().setNull(columnInfo.preferredLocaleIndex, row.getIndex(), true);
                return;
            }
            row.getTable().setString(columnInfo.preferredLocaleIndex, row.getIndex(), value, true);
            return;
        }

        proxyState.getRealm$realm().checkIfValid();
        if (value == null) {
            proxyState.getRow$realm().setNull(columnInfo.preferredLocaleIndex);
            return;
        }
        proxyState.getRow$realm().setString(columnInfo.preferredLocaleIndex, value);
    }

    @Override
    public RealmList<com.kg.gettransfer.data.model.secondary.Currency> realmGet$supportedCurrencies() {
        proxyState.getRealm$realm().checkIfValid();
        // use the cached value if available
        if (supportedCurrenciesRealmList != null) {
            return supportedCurrenciesRealmList;
        } else {
            OsList osList = proxyState.getRow$realm().getModelList(columnInfo.supportedCurrenciesIndex);
            supportedCurrenciesRealmList = new RealmList<com.kg.gettransfer.data.model.secondary.Currency>(com.kg.gettransfer.data.model.secondary.Currency.class, osList, proxyState.getRealm$realm());
            return supportedCurrenciesRealmList;
        }
    }

    @Override
    public void realmSet$supportedCurrencies(RealmList<com.kg.gettransfer.data.model.secondary.Currency> value) {
        if (proxyState.isUnderConstruction()) {
            if (!proxyState.getAcceptDefaultValue$realm()) {
                return;
            }
            if (proxyState.getExcludeFields$realm().contains("supportedCurrencies")) {
                return;
            }
            // if the list contains unmanaged RealmObjects, convert them to managed.
            if (value != null && !value.isManaged()) {
                final Realm realm = (Realm) proxyState.getRealm$realm();
                final RealmList<com.kg.gettransfer.data.model.secondary.Currency> original = value;
                value = new RealmList<com.kg.gettransfer.data.model.secondary.Currency>();
                for (com.kg.gettransfer.data.model.secondary.Currency item : original) {
                    if (item == null || RealmObject.isManaged(item)) {
                        value.add(item);
                    } else {
                        value.add(realm.copyToRealm(item));
                    }
                }
            }
        }

        proxyState.getRealm$realm().checkIfValid();
        OsList osList = proxyState.getRow$realm().getModelList(columnInfo.supportedCurrenciesIndex);
        // For lists of equal lengths, we need to set each element directly as clearing the receiver list can be wrong if the input and target list are the same.
        if (value != null && value.size() == osList.size()) {
            int objects = value.size();
            for (int i = 0; i < objects; i++) {
                com.kg.gettransfer.data.model.secondary.Currency linkedObject = value.get(i);
                proxyState.checkValidObject(linkedObject);
                osList.setRow(i, ((RealmObjectProxy) linkedObject).realmGet$proxyState().getRow$realm().getIndex());
            }
        } else {
            osList.removeAll();
            if (value == null) {
                return;
            }
            int objects = value.size();
            for (int i = 0; i < objects; i++) {
                com.kg.gettransfer.data.model.secondary.Currency linkedObject = value.get(i);
                proxyState.checkValidObject(linkedObject);
                osList.addRow(((RealmObjectProxy) linkedObject).realmGet$proxyState().getRow$realm().getIndex());
            }
        }
    }

    @Override
    public RealmList<String> realmGet$supportedDistanceUnits() {
        proxyState.getRealm$realm().checkIfValid();
        // use the cached value if available
        if (supportedDistanceUnitsRealmList != null) {
            return supportedDistanceUnitsRealmList;
        } else {
            OsList osList = proxyState.getRow$realm().getValueList(columnInfo.supportedDistanceUnitsIndex, RealmFieldType.STRING_LIST);
            supportedDistanceUnitsRealmList = new RealmList<java.lang.String>(java.lang.String.class, osList, proxyState.getRealm$realm());
            return supportedDistanceUnitsRealmList;
        }
    }

    @Override
    public void realmSet$supportedDistanceUnits(RealmList<String> value) {
        if (proxyState.isUnderConstruction()) {
            if (!proxyState.getAcceptDefaultValue$realm()) {
                return;
            }
            if (proxyState.getExcludeFields$realm().contains("supportedDistanceUnits")) {
                return;
            }
        }

        proxyState.getRealm$realm().checkIfValid();
        OsList osList = proxyState.getRow$realm().getValueList(columnInfo.supportedDistanceUnitsIndex, RealmFieldType.STRING_LIST);
        osList.removeAll();
        if (value == null) {
            return;
        }
        for (java.lang.String item : value) {
            if (item == null) {
                osList.addNull();
            } else {
                osList.addString(item);
            }
        }
    }

    private static OsObjectSchemaInfo createExpectedObjectSchemaInfo() {
        OsObjectSchemaInfo.Builder builder = new OsObjectSchemaInfo.Builder("Config", 5, 0);
        builder.addPersistedProperty("id", RealmFieldType.INTEGER, Property.PRIMARY_KEY, Property.INDEXED, Property.REQUIRED);
        builder.addPersistedLinkProperty("availableLocales", RealmFieldType.LIST, "Locale");
        builder.addPersistedProperty("preferredLocale", RealmFieldType.STRING, !Property.PRIMARY_KEY, !Property.INDEXED, !Property.REQUIRED);
        builder.addPersistedLinkProperty("supportedCurrencies", RealmFieldType.LIST, "Currency");
        builder.addPersistedValueListProperty("supportedDistanceUnits", RealmFieldType.STRING_LIST, !Property.REQUIRED);
        return builder.build();
    }

    public static OsObjectSchemaInfo getExpectedObjectSchemaInfo() {
        return expectedObjectSchemaInfo;
    }

    public static ConfigColumnInfo createColumnInfo(OsSchemaInfo schemaInfo) {
        return new ConfigColumnInfo(schemaInfo);
    }

    public static String getSimpleClassName() {
        return "Config";
    }

    public static final class ClassNameHelper {
        public static final String INTERNAL_CLASS_NAME = "Config";
    }

    @SuppressWarnings("cast")
    public static com.kg.gettransfer.data.model.Config createOrUpdateUsingJsonObject(Realm realm, JSONObject json, boolean update)
        throws JSONException {
        final List<String> excludeFields = new ArrayList<String>(3);
        com.kg.gettransfer.data.model.Config obj = null;
        if (update) {
            Table table = realm.getTable(com.kg.gettransfer.data.model.Config.class);
            ConfigColumnInfo columnInfo = (ConfigColumnInfo) realm.getSchema().getColumnInfo(com.kg.gettransfer.data.model.Config.class);
            long pkColumnIndex = columnInfo.idIndex;
            long rowIndex = Table.NO_MATCH;
            if (!json.isNull("id")) {
                rowIndex = table.findFirstLong(pkColumnIndex, json.getLong("id"));
            }
            if (rowIndex != Table.NO_MATCH) {
                final BaseRealm.RealmObjectContext objectContext = BaseRealm.objectContext.get();
                try {
                    objectContext.set(realm, table.getUncheckedRow(rowIndex), realm.getSchema().getColumnInfo(com.kg.gettransfer.data.model.Config.class), false, Collections.<String> emptyList());
                    obj = new io.realm.com_kg_gettransfer_data_model_ConfigRealmProxy();
                } finally {
                    objectContext.clear();
                }
            }
        }
        if (obj == null) {
            if (json.has("availableLocales")) {
                excludeFields.add("availableLocales");
            }
            if (json.has("supportedCurrencies")) {
                excludeFields.add("supportedCurrencies");
            }
            if (json.has("supportedDistanceUnits")) {
                excludeFields.add("supportedDistanceUnits");
            }
            if (json.has("id")) {
                if (json.isNull("id")) {
                    obj = (io.realm.com_kg_gettransfer_data_model_ConfigRealmProxy) realm.createObjectInternal(com.kg.gettransfer.data.model.Config.class, null, true, excludeFields);
                } else {
                    obj = (io.realm.com_kg_gettransfer_data_model_ConfigRealmProxy) realm.createObjectInternal(com.kg.gettransfer.data.model.Config.class, json.getInt("id"), true, excludeFields);
                }
            } else {
                throw new IllegalArgumentException("JSON object doesn't have the primary key field 'id'.");
            }
        }

        final com_kg_gettransfer_data_model_ConfigRealmProxyInterface objProxy = (com_kg_gettransfer_data_model_ConfigRealmProxyInterface) obj;
        if (json.has("availableLocales")) {
            if (json.isNull("availableLocales")) {
                objProxy.realmSet$availableLocales(null);
            } else {
                objProxy.realmGet$availableLocales().clear();
                JSONArray array = json.getJSONArray("availableLocales");
                for (int i = 0; i < array.length(); i++) {
                    com.kg.gettransfer.data.model.secondary.Locale item = com_kg_gettransfer_data_model_secondary_LocaleRealmProxy.createOrUpdateUsingJsonObject(realm, array.getJSONObject(i), update);
                    objProxy.realmGet$availableLocales().add(item);
                }
            }
        }
        if (json.has("preferredLocale")) {
            if (json.isNull("preferredLocale")) {
                objProxy.realmSet$preferredLocale(null);
            } else {
                objProxy.realmSet$preferredLocale((String) json.getString("preferredLocale"));
            }
        }
        if (json.has("supportedCurrencies")) {
            if (json.isNull("supportedCurrencies")) {
                objProxy.realmSet$supportedCurrencies(null);
            } else {
                objProxy.realmGet$supportedCurrencies().clear();
                JSONArray array = json.getJSONArray("supportedCurrencies");
                for (int i = 0; i < array.length(); i++) {
                    com.kg.gettransfer.data.model.secondary.Currency item = com_kg_gettransfer_data_model_secondary_CurrencyRealmProxy.createOrUpdateUsingJsonObject(realm, array.getJSONObject(i), update);
                    objProxy.realmGet$supportedCurrencies().add(item);
                }
            }
        }
        ProxyUtils.setRealmListWithJsonObject(objProxy.realmGet$supportedDistanceUnits(), json, "supportedDistanceUnits");
        return obj;
    }

    @SuppressWarnings("cast")
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static com.kg.gettransfer.data.model.Config createUsingJsonStream(Realm realm, JsonReader reader)
        throws IOException {
        boolean jsonHasPrimaryKey = false;
        final com.kg.gettransfer.data.model.Config obj = new com.kg.gettransfer.data.model.Config();
        final com_kg_gettransfer_data_model_ConfigRealmProxyInterface objProxy = (com_kg_gettransfer_data_model_ConfigRealmProxyInterface) obj;
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (false) {
            } else if (name.equals("id")) {
                if (reader.peek() != JsonToken.NULL) {
                    objProxy.realmSet$id((int) reader.nextInt());
                } else {
                    reader.skipValue();
                    throw new IllegalArgumentException("Trying to set non-nullable field 'id' to null.");
                }
                jsonHasPrimaryKey = true;
            } else if (name.equals("availableLocales")) {
                if (reader.peek() == JsonToken.NULL) {
                    reader.skipValue();
                    objProxy.realmSet$availableLocales(null);
                } else {
                    objProxy.realmSet$availableLocales(new RealmList<com.kg.gettransfer.data.model.secondary.Locale>());
                    reader.beginArray();
                    while (reader.hasNext()) {
                        com.kg.gettransfer.data.model.secondary.Locale item = com_kg_gettransfer_data_model_secondary_LocaleRealmProxy.createUsingJsonStream(realm, reader);
                        objProxy.realmGet$availableLocales().add(item);
                    }
                    reader.endArray();
                }
            } else if (name.equals("preferredLocale")) {
                if (reader.peek() != JsonToken.NULL) {
                    objProxy.realmSet$preferredLocale((String) reader.nextString());
                } else {
                    reader.skipValue();
                    objProxy.realmSet$preferredLocale(null);
                }
            } else if (name.equals("supportedCurrencies")) {
                if (reader.peek() == JsonToken.NULL) {
                    reader.skipValue();
                    objProxy.realmSet$supportedCurrencies(null);
                } else {
                    objProxy.realmSet$supportedCurrencies(new RealmList<com.kg.gettransfer.data.model.secondary.Currency>());
                    reader.beginArray();
                    while (reader.hasNext()) {
                        com.kg.gettransfer.data.model.secondary.Currency item = com_kg_gettransfer_data_model_secondary_CurrencyRealmProxy.createUsingJsonStream(realm, reader);
                        objProxy.realmGet$supportedCurrencies().add(item);
                    }
                    reader.endArray();
                }
            } else if (name.equals("supportedDistanceUnits")) {
                objProxy.realmSet$supportedDistanceUnits(ProxyUtils.createRealmListWithJsonStream(java.lang.String.class, reader));
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

    public static com.kg.gettransfer.data.model.Config copyOrUpdate(Realm realm, com.kg.gettransfer.data.model.Config object, boolean update, Map<RealmModel,RealmObjectProxy> cache) {
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
            return (com.kg.gettransfer.data.model.Config) cachedRealmObject;
        }

        com.kg.gettransfer.data.model.Config realmObject = null;
        boolean canUpdate = update;
        if (canUpdate) {
            Table table = realm.getTable(com.kg.gettransfer.data.model.Config.class);
            ConfigColumnInfo columnInfo = (ConfigColumnInfo) realm.getSchema().getColumnInfo(com.kg.gettransfer.data.model.Config.class);
            long pkColumnIndex = columnInfo.idIndex;
            long rowIndex = table.findFirstLong(pkColumnIndex, ((com_kg_gettransfer_data_model_ConfigRealmProxyInterface) object).realmGet$id());
            if (rowIndex == Table.NO_MATCH) {
                canUpdate = false;
            } else {
                try {
                    objectContext.set(realm, table.getUncheckedRow(rowIndex), realm.getSchema().getColumnInfo(com.kg.gettransfer.data.model.Config.class), false, Collections.<String> emptyList());
                    realmObject = new io.realm.com_kg_gettransfer_data_model_ConfigRealmProxy();
                    cache.put(object, (RealmObjectProxy) realmObject);
                } finally {
                    objectContext.clear();
                }
            }
        }

        return (canUpdate) ? update(realm, realmObject, object, cache) : copy(realm, object, update, cache);
    }

    public static com.kg.gettransfer.data.model.Config copy(Realm realm, com.kg.gettransfer.data.model.Config newObject, boolean update, Map<RealmModel,RealmObjectProxy> cache) {
        RealmObjectProxy cachedRealmObject = cache.get(newObject);
        if (cachedRealmObject != null) {
            return (com.kg.gettransfer.data.model.Config) cachedRealmObject;
        }

        // rejecting default values to avoid creating unexpected objects from RealmModel/RealmList fields.
        com.kg.gettransfer.data.model.Config realmObject = realm.createObjectInternal(com.kg.gettransfer.data.model.Config.class, ((com_kg_gettransfer_data_model_ConfigRealmProxyInterface) newObject).realmGet$id(), false, Collections.<String>emptyList());
        cache.put(newObject, (RealmObjectProxy) realmObject);

        com_kg_gettransfer_data_model_ConfigRealmProxyInterface realmObjectSource = (com_kg_gettransfer_data_model_ConfigRealmProxyInterface) newObject;
        com_kg_gettransfer_data_model_ConfigRealmProxyInterface realmObjectCopy = (com_kg_gettransfer_data_model_ConfigRealmProxyInterface) realmObject;


        RealmList<com.kg.gettransfer.data.model.secondary.Locale> availableLocalesList = realmObjectSource.realmGet$availableLocales();
        if (availableLocalesList != null) {
            RealmList<com.kg.gettransfer.data.model.secondary.Locale> availableLocalesRealmList = realmObjectCopy.realmGet$availableLocales();
            availableLocalesRealmList.clear();
            for (int i = 0; i < availableLocalesList.size(); i++) {
                com.kg.gettransfer.data.model.secondary.Locale availableLocalesItem = availableLocalesList.get(i);
                com.kg.gettransfer.data.model.secondary.Locale cacheavailableLocales = (com.kg.gettransfer.data.model.secondary.Locale) cache.get(availableLocalesItem);
                if (cacheavailableLocales != null) {
                    availableLocalesRealmList.add(cacheavailableLocales);
                } else {
                    availableLocalesRealmList.add(com_kg_gettransfer_data_model_secondary_LocaleRealmProxy.copyOrUpdate(realm, availableLocalesItem, update, cache));
                }
            }
        }

        realmObjectCopy.realmSet$preferredLocale(realmObjectSource.realmGet$preferredLocale());

        RealmList<com.kg.gettransfer.data.model.secondary.Currency> supportedCurrenciesList = realmObjectSource.realmGet$supportedCurrencies();
        if (supportedCurrenciesList != null) {
            RealmList<com.kg.gettransfer.data.model.secondary.Currency> supportedCurrenciesRealmList = realmObjectCopy.realmGet$supportedCurrencies();
            supportedCurrenciesRealmList.clear();
            for (int i = 0; i < supportedCurrenciesList.size(); i++) {
                com.kg.gettransfer.data.model.secondary.Currency supportedCurrenciesItem = supportedCurrenciesList.get(i);
                com.kg.gettransfer.data.model.secondary.Currency cachesupportedCurrencies = (com.kg.gettransfer.data.model.secondary.Currency) cache.get(supportedCurrenciesItem);
                if (cachesupportedCurrencies != null) {
                    supportedCurrenciesRealmList.add(cachesupportedCurrencies);
                } else {
                    supportedCurrenciesRealmList.add(com_kg_gettransfer_data_model_secondary_CurrencyRealmProxy.copyOrUpdate(realm, supportedCurrenciesItem, update, cache));
                }
            }
        }

        realmObjectCopy.realmSet$supportedDistanceUnits(realmObjectSource.realmGet$supportedDistanceUnits());
        return realmObject;
    }

    public static long insert(Realm realm, com.kg.gettransfer.data.model.Config object, Map<RealmModel,Long> cache) {
        if (object instanceof RealmObjectProxy && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm() != null && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm().getPath().equals(realm.getPath())) {
            return ((RealmObjectProxy) object).realmGet$proxyState().getRow$realm().getIndex();
        }
        Table table = realm.getTable(com.kg.gettransfer.data.model.Config.class);
        long tableNativePtr = table.getNativePtr();
        ConfigColumnInfo columnInfo = (ConfigColumnInfo) realm.getSchema().getColumnInfo(com.kg.gettransfer.data.model.Config.class);
        long pkColumnIndex = columnInfo.idIndex;
        long rowIndex = Table.NO_MATCH;
        Object primaryKeyValue = ((com_kg_gettransfer_data_model_ConfigRealmProxyInterface) object).realmGet$id();
        if (primaryKeyValue != null) {
            rowIndex = Table.nativeFindFirstInt(tableNativePtr, pkColumnIndex, ((com_kg_gettransfer_data_model_ConfigRealmProxyInterface) object).realmGet$id());
        }
        if (rowIndex == Table.NO_MATCH) {
            rowIndex = OsObject.createRowWithPrimaryKey(table, pkColumnIndex, ((com_kg_gettransfer_data_model_ConfigRealmProxyInterface) object).realmGet$id());
        } else {
            Table.throwDuplicatePrimaryKeyException(primaryKeyValue);
        }
        cache.put(object, rowIndex);

        RealmList<com.kg.gettransfer.data.model.secondary.Locale> availableLocalesList = ((com_kg_gettransfer_data_model_ConfigRealmProxyInterface) object).realmGet$availableLocales();
        if (availableLocalesList != null) {
            OsList availableLocalesOsList = new OsList(table.getUncheckedRow(rowIndex), columnInfo.availableLocalesIndex);
            for (com.kg.gettransfer.data.model.secondary.Locale availableLocalesItem : availableLocalesList) {
                Long cacheItemIndexavailableLocales = cache.get(availableLocalesItem);
                if (cacheItemIndexavailableLocales == null) {
                    cacheItemIndexavailableLocales = com_kg_gettransfer_data_model_secondary_LocaleRealmProxy.insert(realm, availableLocalesItem, cache);
                }
                availableLocalesOsList.addRow(cacheItemIndexavailableLocales);
            }
        }
        String realmGet$preferredLocale = ((com_kg_gettransfer_data_model_ConfigRealmProxyInterface) object).realmGet$preferredLocale();
        if (realmGet$preferredLocale != null) {
            Table.nativeSetString(tableNativePtr, columnInfo.preferredLocaleIndex, rowIndex, realmGet$preferredLocale, false);
        }

        RealmList<com.kg.gettransfer.data.model.secondary.Currency> supportedCurrenciesList = ((com_kg_gettransfer_data_model_ConfigRealmProxyInterface) object).realmGet$supportedCurrencies();
        if (supportedCurrenciesList != null) {
            OsList supportedCurrenciesOsList = new OsList(table.getUncheckedRow(rowIndex), columnInfo.supportedCurrenciesIndex);
            for (com.kg.gettransfer.data.model.secondary.Currency supportedCurrenciesItem : supportedCurrenciesList) {
                Long cacheItemIndexsupportedCurrencies = cache.get(supportedCurrenciesItem);
                if (cacheItemIndexsupportedCurrencies == null) {
                    cacheItemIndexsupportedCurrencies = com_kg_gettransfer_data_model_secondary_CurrencyRealmProxy.insert(realm, supportedCurrenciesItem, cache);
                }
                supportedCurrenciesOsList.addRow(cacheItemIndexsupportedCurrencies);
            }
        }

        RealmList<java.lang.String> supportedDistanceUnitsList = ((com_kg_gettransfer_data_model_ConfigRealmProxyInterface) object).realmGet$supportedDistanceUnits();
        if (supportedDistanceUnitsList != null) {
            OsList supportedDistanceUnitsOsList = new OsList(table.getUncheckedRow(rowIndex), columnInfo.supportedDistanceUnitsIndex);
            for (java.lang.String supportedDistanceUnitsItem : supportedDistanceUnitsList) {
                if (supportedDistanceUnitsItem == null) {
                    supportedDistanceUnitsOsList.addNull();
                } else {
                    supportedDistanceUnitsOsList.addString(supportedDistanceUnitsItem);
                }
            }
        }
        return rowIndex;
    }

    public static void insert(Realm realm, Iterator<? extends RealmModel> objects, Map<RealmModel,Long> cache) {
        Table table = realm.getTable(com.kg.gettransfer.data.model.Config.class);
        long tableNativePtr = table.getNativePtr();
        ConfigColumnInfo columnInfo = (ConfigColumnInfo) realm.getSchema().getColumnInfo(com.kg.gettransfer.data.model.Config.class);
        long pkColumnIndex = columnInfo.idIndex;
        com.kg.gettransfer.data.model.Config object = null;
        while (objects.hasNext()) {
            object = (com.kg.gettransfer.data.model.Config) objects.next();
            if (cache.containsKey(object)) {
                continue;
            }
            if (object instanceof RealmObjectProxy && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm() != null && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm().getPath().equals(realm.getPath())) {
                cache.put(object, ((RealmObjectProxy) object).realmGet$proxyState().getRow$realm().getIndex());
                continue;
            }
            long rowIndex = Table.NO_MATCH;
            Object primaryKeyValue = ((com_kg_gettransfer_data_model_ConfigRealmProxyInterface) object).realmGet$id();
            if (primaryKeyValue != null) {
                rowIndex = Table.nativeFindFirstInt(tableNativePtr, pkColumnIndex, ((com_kg_gettransfer_data_model_ConfigRealmProxyInterface) object).realmGet$id());
            }
            if (rowIndex == Table.NO_MATCH) {
                rowIndex = OsObject.createRowWithPrimaryKey(table, pkColumnIndex, ((com_kg_gettransfer_data_model_ConfigRealmProxyInterface) object).realmGet$id());
            } else {
                Table.throwDuplicatePrimaryKeyException(primaryKeyValue);
            }
            cache.put(object, rowIndex);

            RealmList<com.kg.gettransfer.data.model.secondary.Locale> availableLocalesList = ((com_kg_gettransfer_data_model_ConfigRealmProxyInterface) object).realmGet$availableLocales();
            if (availableLocalesList != null) {
                OsList availableLocalesOsList = new OsList(table.getUncheckedRow(rowIndex), columnInfo.availableLocalesIndex);
                for (com.kg.gettransfer.data.model.secondary.Locale availableLocalesItem : availableLocalesList) {
                    Long cacheItemIndexavailableLocales = cache.get(availableLocalesItem);
                    if (cacheItemIndexavailableLocales == null) {
                        cacheItemIndexavailableLocales = com_kg_gettransfer_data_model_secondary_LocaleRealmProxy.insert(realm, availableLocalesItem, cache);
                    }
                    availableLocalesOsList.addRow(cacheItemIndexavailableLocales);
                }
            }
            String realmGet$preferredLocale = ((com_kg_gettransfer_data_model_ConfigRealmProxyInterface) object).realmGet$preferredLocale();
            if (realmGet$preferredLocale != null) {
                Table.nativeSetString(tableNativePtr, columnInfo.preferredLocaleIndex, rowIndex, realmGet$preferredLocale, false);
            }

            RealmList<com.kg.gettransfer.data.model.secondary.Currency> supportedCurrenciesList = ((com_kg_gettransfer_data_model_ConfigRealmProxyInterface) object).realmGet$supportedCurrencies();
            if (supportedCurrenciesList != null) {
                OsList supportedCurrenciesOsList = new OsList(table.getUncheckedRow(rowIndex), columnInfo.supportedCurrenciesIndex);
                for (com.kg.gettransfer.data.model.secondary.Currency supportedCurrenciesItem : supportedCurrenciesList) {
                    Long cacheItemIndexsupportedCurrencies = cache.get(supportedCurrenciesItem);
                    if (cacheItemIndexsupportedCurrencies == null) {
                        cacheItemIndexsupportedCurrencies = com_kg_gettransfer_data_model_secondary_CurrencyRealmProxy.insert(realm, supportedCurrenciesItem, cache);
                    }
                    supportedCurrenciesOsList.addRow(cacheItemIndexsupportedCurrencies);
                }
            }

            RealmList<java.lang.String> supportedDistanceUnitsList = ((com_kg_gettransfer_data_model_ConfigRealmProxyInterface) object).realmGet$supportedDistanceUnits();
            if (supportedDistanceUnitsList != null) {
                OsList supportedDistanceUnitsOsList = new OsList(table.getUncheckedRow(rowIndex), columnInfo.supportedDistanceUnitsIndex);
                for (java.lang.String supportedDistanceUnitsItem : supportedDistanceUnitsList) {
                    if (supportedDistanceUnitsItem == null) {
                        supportedDistanceUnitsOsList.addNull();
                    } else {
                        supportedDistanceUnitsOsList.addString(supportedDistanceUnitsItem);
                    }
                }
            }
        }
    }

    public static long insertOrUpdate(Realm realm, com.kg.gettransfer.data.model.Config object, Map<RealmModel,Long> cache) {
        if (object instanceof RealmObjectProxy && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm() != null && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm().getPath().equals(realm.getPath())) {
            return ((RealmObjectProxy) object).realmGet$proxyState().getRow$realm().getIndex();
        }
        Table table = realm.getTable(com.kg.gettransfer.data.model.Config.class);
        long tableNativePtr = table.getNativePtr();
        ConfigColumnInfo columnInfo = (ConfigColumnInfo) realm.getSchema().getColumnInfo(com.kg.gettransfer.data.model.Config.class);
        long pkColumnIndex = columnInfo.idIndex;
        long rowIndex = Table.NO_MATCH;
        Object primaryKeyValue = ((com_kg_gettransfer_data_model_ConfigRealmProxyInterface) object).realmGet$id();
        if (primaryKeyValue != null) {
            rowIndex = Table.nativeFindFirstInt(tableNativePtr, pkColumnIndex, ((com_kg_gettransfer_data_model_ConfigRealmProxyInterface) object).realmGet$id());
        }
        if (rowIndex == Table.NO_MATCH) {
            rowIndex = OsObject.createRowWithPrimaryKey(table, pkColumnIndex, ((com_kg_gettransfer_data_model_ConfigRealmProxyInterface) object).realmGet$id());
        }
        cache.put(object, rowIndex);

        OsList availableLocalesOsList = new OsList(table.getUncheckedRow(rowIndex), columnInfo.availableLocalesIndex);
        RealmList<com.kg.gettransfer.data.model.secondary.Locale> availableLocalesList = ((com_kg_gettransfer_data_model_ConfigRealmProxyInterface) object).realmGet$availableLocales();
        if (availableLocalesList != null && availableLocalesList.size() == availableLocalesOsList.size()) {
            // For lists of equal lengths, we need to set each element directly as clearing the receiver list can be wrong if the input and target list are the same.
            int objects = availableLocalesList.size();
            for (int i = 0; i < objects; i++) {
                com.kg.gettransfer.data.model.secondary.Locale availableLocalesItem = availableLocalesList.get(i);
                Long cacheItemIndexavailableLocales = cache.get(availableLocalesItem);
                if (cacheItemIndexavailableLocales == null) {
                    cacheItemIndexavailableLocales = com_kg_gettransfer_data_model_secondary_LocaleRealmProxy.insertOrUpdate(realm, availableLocalesItem, cache);
                }
                availableLocalesOsList.setRow(i, cacheItemIndexavailableLocales);
            }
        } else {
            availableLocalesOsList.removeAll();
            if (availableLocalesList != null) {
                for (com.kg.gettransfer.data.model.secondary.Locale availableLocalesItem : availableLocalesList) {
                    Long cacheItemIndexavailableLocales = cache.get(availableLocalesItem);
                    if (cacheItemIndexavailableLocales == null) {
                        cacheItemIndexavailableLocales = com_kg_gettransfer_data_model_secondary_LocaleRealmProxy.insertOrUpdate(realm, availableLocalesItem, cache);
                    }
                    availableLocalesOsList.addRow(cacheItemIndexavailableLocales);
                }
            }
        }

        String realmGet$preferredLocale = ((com_kg_gettransfer_data_model_ConfigRealmProxyInterface) object).realmGet$preferredLocale();
        if (realmGet$preferredLocale != null) {
            Table.nativeSetString(tableNativePtr, columnInfo.preferredLocaleIndex, rowIndex, realmGet$preferredLocale, false);
        } else {
            Table.nativeSetNull(tableNativePtr, columnInfo.preferredLocaleIndex, rowIndex, false);
        }

        OsList supportedCurrenciesOsList = new OsList(table.getUncheckedRow(rowIndex), columnInfo.supportedCurrenciesIndex);
        RealmList<com.kg.gettransfer.data.model.secondary.Currency> supportedCurrenciesList = ((com_kg_gettransfer_data_model_ConfigRealmProxyInterface) object).realmGet$supportedCurrencies();
        if (supportedCurrenciesList != null && supportedCurrenciesList.size() == supportedCurrenciesOsList.size()) {
            // For lists of equal lengths, we need to set each element directly as clearing the receiver list can be wrong if the input and target list are the same.
            int objects = supportedCurrenciesList.size();
            for (int i = 0; i < objects; i++) {
                com.kg.gettransfer.data.model.secondary.Currency supportedCurrenciesItem = supportedCurrenciesList.get(i);
                Long cacheItemIndexsupportedCurrencies = cache.get(supportedCurrenciesItem);
                if (cacheItemIndexsupportedCurrencies == null) {
                    cacheItemIndexsupportedCurrencies = com_kg_gettransfer_data_model_secondary_CurrencyRealmProxy.insertOrUpdate(realm, supportedCurrenciesItem, cache);
                }
                supportedCurrenciesOsList.setRow(i, cacheItemIndexsupportedCurrencies);
            }
        } else {
            supportedCurrenciesOsList.removeAll();
            if (supportedCurrenciesList != null) {
                for (com.kg.gettransfer.data.model.secondary.Currency supportedCurrenciesItem : supportedCurrenciesList) {
                    Long cacheItemIndexsupportedCurrencies = cache.get(supportedCurrenciesItem);
                    if (cacheItemIndexsupportedCurrencies == null) {
                        cacheItemIndexsupportedCurrencies = com_kg_gettransfer_data_model_secondary_CurrencyRealmProxy.insertOrUpdate(realm, supportedCurrenciesItem, cache);
                    }
                    supportedCurrenciesOsList.addRow(cacheItemIndexsupportedCurrencies);
                }
            }
        }


        OsList supportedDistanceUnitsOsList = new OsList(table.getUncheckedRow(rowIndex), columnInfo.supportedDistanceUnitsIndex);
        supportedDistanceUnitsOsList.removeAll();
        RealmList<java.lang.String> supportedDistanceUnitsList = ((com_kg_gettransfer_data_model_ConfigRealmProxyInterface) object).realmGet$supportedDistanceUnits();
        if (supportedDistanceUnitsList != null) {
            for (java.lang.String supportedDistanceUnitsItem : supportedDistanceUnitsList) {
                if (supportedDistanceUnitsItem == null) {
                    supportedDistanceUnitsOsList.addNull();
                } else {
                    supportedDistanceUnitsOsList.addString(supportedDistanceUnitsItem);
                }
            }
        }

        return rowIndex;
    }

    public static void insertOrUpdate(Realm realm, Iterator<? extends RealmModel> objects, Map<RealmModel,Long> cache) {
        Table table = realm.getTable(com.kg.gettransfer.data.model.Config.class);
        long tableNativePtr = table.getNativePtr();
        ConfigColumnInfo columnInfo = (ConfigColumnInfo) realm.getSchema().getColumnInfo(com.kg.gettransfer.data.model.Config.class);
        long pkColumnIndex = columnInfo.idIndex;
        com.kg.gettransfer.data.model.Config object = null;
        while (objects.hasNext()) {
            object = (com.kg.gettransfer.data.model.Config) objects.next();
            if (cache.containsKey(object)) {
                continue;
            }
            if (object instanceof RealmObjectProxy && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm() != null && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm().getPath().equals(realm.getPath())) {
                cache.put(object, ((RealmObjectProxy) object).realmGet$proxyState().getRow$realm().getIndex());
                continue;
            }
            long rowIndex = Table.NO_MATCH;
            Object primaryKeyValue = ((com_kg_gettransfer_data_model_ConfigRealmProxyInterface) object).realmGet$id();
            if (primaryKeyValue != null) {
                rowIndex = Table.nativeFindFirstInt(tableNativePtr, pkColumnIndex, ((com_kg_gettransfer_data_model_ConfigRealmProxyInterface) object).realmGet$id());
            }
            if (rowIndex == Table.NO_MATCH) {
                rowIndex = OsObject.createRowWithPrimaryKey(table, pkColumnIndex, ((com_kg_gettransfer_data_model_ConfigRealmProxyInterface) object).realmGet$id());
            }
            cache.put(object, rowIndex);

            OsList availableLocalesOsList = new OsList(table.getUncheckedRow(rowIndex), columnInfo.availableLocalesIndex);
            RealmList<com.kg.gettransfer.data.model.secondary.Locale> availableLocalesList = ((com_kg_gettransfer_data_model_ConfigRealmProxyInterface) object).realmGet$availableLocales();
            if (availableLocalesList != null && availableLocalesList.size() == availableLocalesOsList.size()) {
                // For lists of equal lengths, we need to set each element directly as clearing the receiver list can be wrong if the input and target list are the same.
                int objectCount = availableLocalesList.size();
                for (int i = 0; i < objectCount; i++) {
                    com.kg.gettransfer.data.model.secondary.Locale availableLocalesItem = availableLocalesList.get(i);
                    Long cacheItemIndexavailableLocales = cache.get(availableLocalesItem);
                    if (cacheItemIndexavailableLocales == null) {
                        cacheItemIndexavailableLocales = com_kg_gettransfer_data_model_secondary_LocaleRealmProxy.insertOrUpdate(realm, availableLocalesItem, cache);
                    }
                    availableLocalesOsList.setRow(i, cacheItemIndexavailableLocales);
                }
            } else {
                availableLocalesOsList.removeAll();
                if (availableLocalesList != null) {
                    for (com.kg.gettransfer.data.model.secondary.Locale availableLocalesItem : availableLocalesList) {
                        Long cacheItemIndexavailableLocales = cache.get(availableLocalesItem);
                        if (cacheItemIndexavailableLocales == null) {
                            cacheItemIndexavailableLocales = com_kg_gettransfer_data_model_secondary_LocaleRealmProxy.insertOrUpdate(realm, availableLocalesItem, cache);
                        }
                        availableLocalesOsList.addRow(cacheItemIndexavailableLocales);
                    }
                }
            }

            String realmGet$preferredLocale = ((com_kg_gettransfer_data_model_ConfigRealmProxyInterface) object).realmGet$preferredLocale();
            if (realmGet$preferredLocale != null) {
                Table.nativeSetString(tableNativePtr, columnInfo.preferredLocaleIndex, rowIndex, realmGet$preferredLocale, false);
            } else {
                Table.nativeSetNull(tableNativePtr, columnInfo.preferredLocaleIndex, rowIndex, false);
            }

            OsList supportedCurrenciesOsList = new OsList(table.getUncheckedRow(rowIndex), columnInfo.supportedCurrenciesIndex);
            RealmList<com.kg.gettransfer.data.model.secondary.Currency> supportedCurrenciesList = ((com_kg_gettransfer_data_model_ConfigRealmProxyInterface) object).realmGet$supportedCurrencies();
            if (supportedCurrenciesList != null && supportedCurrenciesList.size() == supportedCurrenciesOsList.size()) {
                // For lists of equal lengths, we need to set each element directly as clearing the receiver list can be wrong if the input and target list are the same.
                int objectCount = supportedCurrenciesList.size();
                for (int i = 0; i < objectCount; i++) {
                    com.kg.gettransfer.data.model.secondary.Currency supportedCurrenciesItem = supportedCurrenciesList.get(i);
                    Long cacheItemIndexsupportedCurrencies = cache.get(supportedCurrenciesItem);
                    if (cacheItemIndexsupportedCurrencies == null) {
                        cacheItemIndexsupportedCurrencies = com_kg_gettransfer_data_model_secondary_CurrencyRealmProxy.insertOrUpdate(realm, supportedCurrenciesItem, cache);
                    }
                    supportedCurrenciesOsList.setRow(i, cacheItemIndexsupportedCurrencies);
                }
            } else {
                supportedCurrenciesOsList.removeAll();
                if (supportedCurrenciesList != null) {
                    for (com.kg.gettransfer.data.model.secondary.Currency supportedCurrenciesItem : supportedCurrenciesList) {
                        Long cacheItemIndexsupportedCurrencies = cache.get(supportedCurrenciesItem);
                        if (cacheItemIndexsupportedCurrencies == null) {
                            cacheItemIndexsupportedCurrencies = com_kg_gettransfer_data_model_secondary_CurrencyRealmProxy.insertOrUpdate(realm, supportedCurrenciesItem, cache);
                        }
                        supportedCurrenciesOsList.addRow(cacheItemIndexsupportedCurrencies);
                    }
                }
            }


            OsList supportedDistanceUnitsOsList = new OsList(table.getUncheckedRow(rowIndex), columnInfo.supportedDistanceUnitsIndex);
            supportedDistanceUnitsOsList.removeAll();
            RealmList<java.lang.String> supportedDistanceUnitsList = ((com_kg_gettransfer_data_model_ConfigRealmProxyInterface) object).realmGet$supportedDistanceUnits();
            if (supportedDistanceUnitsList != null) {
                for (java.lang.String supportedDistanceUnitsItem : supportedDistanceUnitsList) {
                    if (supportedDistanceUnitsItem == null) {
                        supportedDistanceUnitsOsList.addNull();
                    } else {
                        supportedDistanceUnitsOsList.addString(supportedDistanceUnitsItem);
                    }
                }
            }

        }
    }

    public static com.kg.gettransfer.data.model.Config createDetachedCopy(com.kg.gettransfer.data.model.Config realmObject, int currentDepth, int maxDepth, Map<RealmModel, CacheData<RealmModel>> cache) {
        if (currentDepth > maxDepth || realmObject == null) {
            return null;
        }
        CacheData<RealmModel> cachedObject = cache.get(realmObject);
        com.kg.gettransfer.data.model.Config unmanagedObject;
        if (cachedObject == null) {
            unmanagedObject = new com.kg.gettransfer.data.model.Config();
            cache.put(realmObject, new RealmObjectProxy.CacheData<RealmModel>(currentDepth, unmanagedObject));
        } else {
            // Reuse cached object or recreate it because it was encountered at a lower depth.
            if (currentDepth >= cachedObject.minDepth) {
                return (com.kg.gettransfer.data.model.Config) cachedObject.object;
            }
            unmanagedObject = (com.kg.gettransfer.data.model.Config) cachedObject.object;
            cachedObject.minDepth = currentDepth;
        }
        com_kg_gettransfer_data_model_ConfigRealmProxyInterface unmanagedCopy = (com_kg_gettransfer_data_model_ConfigRealmProxyInterface) unmanagedObject;
        com_kg_gettransfer_data_model_ConfigRealmProxyInterface realmSource = (com_kg_gettransfer_data_model_ConfigRealmProxyInterface) realmObject;
        unmanagedCopy.realmSet$id(realmSource.realmGet$id());

        // Deep copy of availableLocales
        if (currentDepth == maxDepth) {
            unmanagedCopy.realmSet$availableLocales(null);
        } else {
            RealmList<com.kg.gettransfer.data.model.secondary.Locale> managedavailableLocalesList = realmSource.realmGet$availableLocales();
            RealmList<com.kg.gettransfer.data.model.secondary.Locale> unmanagedavailableLocalesList = new RealmList<com.kg.gettransfer.data.model.secondary.Locale>();
            unmanagedCopy.realmSet$availableLocales(unmanagedavailableLocalesList);
            int nextDepth = currentDepth + 1;
            int size = managedavailableLocalesList.size();
            for (int i = 0; i < size; i++) {
                com.kg.gettransfer.data.model.secondary.Locale item = com_kg_gettransfer_data_model_secondary_LocaleRealmProxy.createDetachedCopy(managedavailableLocalesList.get(i), nextDepth, maxDepth, cache);
                unmanagedavailableLocalesList.add(item);
            }
        }
        unmanagedCopy.realmSet$preferredLocale(realmSource.realmGet$preferredLocale());

        // Deep copy of supportedCurrencies
        if (currentDepth == maxDepth) {
            unmanagedCopy.realmSet$supportedCurrencies(null);
        } else {
            RealmList<com.kg.gettransfer.data.model.secondary.Currency> managedsupportedCurrenciesList = realmSource.realmGet$supportedCurrencies();
            RealmList<com.kg.gettransfer.data.model.secondary.Currency> unmanagedsupportedCurrenciesList = new RealmList<com.kg.gettransfer.data.model.secondary.Currency>();
            unmanagedCopy.realmSet$supportedCurrencies(unmanagedsupportedCurrenciesList);
            int nextDepth = currentDepth + 1;
            int size = managedsupportedCurrenciesList.size();
            for (int i = 0; i < size; i++) {
                com.kg.gettransfer.data.model.secondary.Currency item = com_kg_gettransfer_data_model_secondary_CurrencyRealmProxy.createDetachedCopy(managedsupportedCurrenciesList.get(i), nextDepth, maxDepth, cache);
                unmanagedsupportedCurrenciesList.add(item);
            }
        }

        unmanagedCopy.realmSet$supportedDistanceUnits(new RealmList<java.lang.String>());
        unmanagedCopy.realmGet$supportedDistanceUnits().addAll(realmSource.realmGet$supportedDistanceUnits());

        return unmanagedObject;
    }

    static com.kg.gettransfer.data.model.Config update(Realm realm, com.kg.gettransfer.data.model.Config realmObject, com.kg.gettransfer.data.model.Config newObject, Map<RealmModel, RealmObjectProxy> cache) {
        com_kg_gettransfer_data_model_ConfigRealmProxyInterface realmObjectTarget = (com_kg_gettransfer_data_model_ConfigRealmProxyInterface) realmObject;
        com_kg_gettransfer_data_model_ConfigRealmProxyInterface realmObjectSource = (com_kg_gettransfer_data_model_ConfigRealmProxyInterface) newObject;
        RealmList<com.kg.gettransfer.data.model.secondary.Locale> availableLocalesList = realmObjectSource.realmGet$availableLocales();
        RealmList<com.kg.gettransfer.data.model.secondary.Locale> availableLocalesRealmList = realmObjectTarget.realmGet$availableLocales();
        if (availableLocalesList != null && availableLocalesList.size() == availableLocalesRealmList.size()) {
            // For lists of equal lengths, we need to set each element directly as clearing the receiver list can be wrong if the input and target list are the same.
            int objects = availableLocalesList.size();
            for (int i = 0; i < objects; i++) {
                com.kg.gettransfer.data.model.secondary.Locale availableLocalesItem = availableLocalesList.get(i);
                com.kg.gettransfer.data.model.secondary.Locale cacheavailableLocales = (com.kg.gettransfer.data.model.secondary.Locale) cache.get(availableLocalesItem);
                if (cacheavailableLocales != null) {
                    availableLocalesRealmList.set(i, cacheavailableLocales);
                } else {
                    availableLocalesRealmList.set(i, com_kg_gettransfer_data_model_secondary_LocaleRealmProxy.copyOrUpdate(realm, availableLocalesItem, true, cache));
                }
            }
        } else {
            availableLocalesRealmList.clear();
            if (availableLocalesList != null) {
                for (int i = 0; i < availableLocalesList.size(); i++) {
                    com.kg.gettransfer.data.model.secondary.Locale availableLocalesItem = availableLocalesList.get(i);
                    com.kg.gettransfer.data.model.secondary.Locale cacheavailableLocales = (com.kg.gettransfer.data.model.secondary.Locale) cache.get(availableLocalesItem);
                    if (cacheavailableLocales != null) {
                        availableLocalesRealmList.add(cacheavailableLocales);
                    } else {
                        availableLocalesRealmList.add(com_kg_gettransfer_data_model_secondary_LocaleRealmProxy.copyOrUpdate(realm, availableLocalesItem, true, cache));
                    }
                }
            }
        }
        realmObjectTarget.realmSet$preferredLocale(realmObjectSource.realmGet$preferredLocale());
        RealmList<com.kg.gettransfer.data.model.secondary.Currency> supportedCurrenciesList = realmObjectSource.realmGet$supportedCurrencies();
        RealmList<com.kg.gettransfer.data.model.secondary.Currency> supportedCurrenciesRealmList = realmObjectTarget.realmGet$supportedCurrencies();
        if (supportedCurrenciesList != null && supportedCurrenciesList.size() == supportedCurrenciesRealmList.size()) {
            // For lists of equal lengths, we need to set each element directly as clearing the receiver list can be wrong if the input and target list are the same.
            int objects = supportedCurrenciesList.size();
            for (int i = 0; i < objects; i++) {
                com.kg.gettransfer.data.model.secondary.Currency supportedCurrenciesItem = supportedCurrenciesList.get(i);
                com.kg.gettransfer.data.model.secondary.Currency cachesupportedCurrencies = (com.kg.gettransfer.data.model.secondary.Currency) cache.get(supportedCurrenciesItem);
                if (cachesupportedCurrencies != null) {
                    supportedCurrenciesRealmList.set(i, cachesupportedCurrencies);
                } else {
                    supportedCurrenciesRealmList.set(i, com_kg_gettransfer_data_model_secondary_CurrencyRealmProxy.copyOrUpdate(realm, supportedCurrenciesItem, true, cache));
                }
            }
        } else {
            supportedCurrenciesRealmList.clear();
            if (supportedCurrenciesList != null) {
                for (int i = 0; i < supportedCurrenciesList.size(); i++) {
                    com.kg.gettransfer.data.model.secondary.Currency supportedCurrenciesItem = supportedCurrenciesList.get(i);
                    com.kg.gettransfer.data.model.secondary.Currency cachesupportedCurrencies = (com.kg.gettransfer.data.model.secondary.Currency) cache.get(supportedCurrenciesItem);
                    if (cachesupportedCurrencies != null) {
                        supportedCurrenciesRealmList.add(cachesupportedCurrencies);
                    } else {
                        supportedCurrenciesRealmList.add(com_kg_gettransfer_data_model_secondary_CurrencyRealmProxy.copyOrUpdate(realm, supportedCurrenciesItem, true, cache));
                    }
                }
            }
        }
        realmObjectTarget.realmSet$supportedDistanceUnits(realmObjectSource.realmGet$supportedDistanceUnits());
        return realmObject;
    }

    @Override
    @SuppressWarnings("ArrayToString")
    public String toString() {
        if (!RealmObject.isValid(this)) {
            return "Invalid object";
        }
        StringBuilder stringBuilder = new StringBuilder("Config = proxy[");
        stringBuilder.append("{id:");
        stringBuilder.append(realmGet$id());
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{availableLocales:");
        stringBuilder.append("RealmList<Locale>[").append(realmGet$availableLocales().size()).append("]");
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{preferredLocale:");
        stringBuilder.append(realmGet$preferredLocale() != null ? realmGet$preferredLocale() : "null");
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{supportedCurrencies:");
        stringBuilder.append("RealmList<Currency>[").append(realmGet$supportedCurrencies().size()).append("]");
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{supportedDistanceUnits:");
        stringBuilder.append("RealmList<String>[").append(realmGet$supportedDistanceUnits().size()).append("]");
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
        com_kg_gettransfer_data_model_ConfigRealmProxy aConfig = (com_kg_gettransfer_data_model_ConfigRealmProxy)o;

        String path = proxyState.getRealm$realm().getPath();
        String otherPath = aConfig.proxyState.getRealm$realm().getPath();
        if (path != null ? !path.equals(otherPath) : otherPath != null) return false;

        String tableName = proxyState.getRow$realm().getTable().getName();
        String otherTableName = aConfig.proxyState.getRow$realm().getTable().getName();
        if (tableName != null ? !tableName.equals(otherTableName) : otherTableName != null) return false;

        if (proxyState.getRow$realm().getIndex() != aConfig.proxyState.getRow$realm().getIndex()) return false;

        return true;
    }
}
