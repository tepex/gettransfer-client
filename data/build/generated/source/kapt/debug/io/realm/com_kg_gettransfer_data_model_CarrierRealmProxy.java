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
public class com_kg_gettransfer_data_model_CarrierRealmProxy extends com.kg.gettransfer.data.model.Carrier
    implements RealmObjectProxy, com_kg_gettransfer_data_model_CarrierRealmProxyInterface {

    static final class CarrierColumnInfo extends ColumnInfo {
        long idIndex;
        long titleIndex;
        long approvedIndex;
        long completedTransfersIndex;
        long languagesIndex;
        long ratingIndex;
        long emailIndex;
        long phoneIndex;
        long alternatePhoneIndex;

        CarrierColumnInfo(OsSchemaInfo schemaInfo) {
            super(9);
            OsObjectSchemaInfo objectSchemaInfo = schemaInfo.getObjectSchemaInfo("Carrier");
            this.idIndex = addColumnDetails("id", "id", objectSchemaInfo);
            this.titleIndex = addColumnDetails("title", "title", objectSchemaInfo);
            this.approvedIndex = addColumnDetails("approved", "approved", objectSchemaInfo);
            this.completedTransfersIndex = addColumnDetails("completedTransfers", "completedTransfers", objectSchemaInfo);
            this.languagesIndex = addColumnDetails("languages", "languages", objectSchemaInfo);
            this.ratingIndex = addColumnDetails("rating", "rating", objectSchemaInfo);
            this.emailIndex = addColumnDetails("email", "email", objectSchemaInfo);
            this.phoneIndex = addColumnDetails("phone", "phone", objectSchemaInfo);
            this.alternatePhoneIndex = addColumnDetails("alternatePhone", "alternatePhone", objectSchemaInfo);
        }

        CarrierColumnInfo(ColumnInfo src, boolean mutable) {
            super(src, mutable);
            copy(src, this);
        }

        @Override
        protected final ColumnInfo copy(boolean mutable) {
            return new CarrierColumnInfo(this, mutable);
        }

        @Override
        protected final void copy(ColumnInfo rawSrc, ColumnInfo rawDst) {
            final CarrierColumnInfo src = (CarrierColumnInfo) rawSrc;
            final CarrierColumnInfo dst = (CarrierColumnInfo) rawDst;
            dst.idIndex = src.idIndex;
            dst.titleIndex = src.titleIndex;
            dst.approvedIndex = src.approvedIndex;
            dst.completedTransfersIndex = src.completedTransfersIndex;
            dst.languagesIndex = src.languagesIndex;
            dst.ratingIndex = src.ratingIndex;
            dst.emailIndex = src.emailIndex;
            dst.phoneIndex = src.phoneIndex;
            dst.alternatePhoneIndex = src.alternatePhoneIndex;
        }
    }

    private static final OsObjectSchemaInfo expectedObjectSchemaInfo = createExpectedObjectSchemaInfo();

    private CarrierColumnInfo columnInfo;
    private ProxyState<com.kg.gettransfer.data.model.Carrier> proxyState;
    private RealmList<com.kg.gettransfer.data.model.secondary.Language> languagesRealmList;

    com_kg_gettransfer_data_model_CarrierRealmProxy() {
        proxyState.setConstructionFinished();
    }

    @Override
    public void realm$injectObjectContext() {
        if (this.proxyState != null) {
            return;
        }
        final BaseRealm.RealmObjectContext context = BaseRealm.objectContext.get();
        this.columnInfo = (CarrierColumnInfo) context.getColumnInfo();
        this.proxyState = new ProxyState<com.kg.gettransfer.data.model.Carrier>(this);
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
                row.getTable().setNull(columnInfo.titleIndex, row.getIndex(), true);
                return;
            }
            row.getTable().setString(columnInfo.titleIndex, row.getIndex(), value, true);
            return;
        }

        proxyState.getRealm$realm().checkIfValid();
        if (value == null) {
            proxyState.getRow$realm().setNull(columnInfo.titleIndex);
            return;
        }
        proxyState.getRow$realm().setString(columnInfo.titleIndex, value);
    }

    @Override
    @SuppressWarnings("cast")
    public Boolean realmGet$approved() {
        proxyState.getRealm$realm().checkIfValid();
        if (proxyState.getRow$realm().isNull(columnInfo.approvedIndex)) {
            return null;
        }
        return (boolean) proxyState.getRow$realm().getBoolean(columnInfo.approvedIndex);
    }

    @Override
    public void realmSet$approved(Boolean value) {
        if (proxyState.isUnderConstruction()) {
            if (!proxyState.getAcceptDefaultValue$realm()) {
                return;
            }
            final Row row = proxyState.getRow$realm();
            if (value == null) {
                row.getTable().setNull(columnInfo.approvedIndex, row.getIndex(), true);
                return;
            }
            row.getTable().setBoolean(columnInfo.approvedIndex, row.getIndex(), value, true);
            return;
        }

        proxyState.getRealm$realm().checkIfValid();
        if (value == null) {
            proxyState.getRow$realm().setNull(columnInfo.approvedIndex);
            return;
        }
        proxyState.getRow$realm().setBoolean(columnInfo.approvedIndex, value);
    }

    @Override
    @SuppressWarnings("cast")
    public int realmGet$completedTransfers() {
        proxyState.getRealm$realm().checkIfValid();
        return (int) proxyState.getRow$realm().getLong(columnInfo.completedTransfersIndex);
    }

    @Override
    public void realmSet$completedTransfers(int value) {
        if (proxyState.isUnderConstruction()) {
            if (!proxyState.getAcceptDefaultValue$realm()) {
                return;
            }
            final Row row = proxyState.getRow$realm();
            row.getTable().setLong(columnInfo.completedTransfersIndex, row.getIndex(), value, true);
            return;
        }

        proxyState.getRealm$realm().checkIfValid();
        proxyState.getRow$realm().setLong(columnInfo.completedTransfersIndex, value);
    }

    @Override
    public RealmList<com.kg.gettransfer.data.model.secondary.Language> realmGet$languages() {
        proxyState.getRealm$realm().checkIfValid();
        // use the cached value if available
        if (languagesRealmList != null) {
            return languagesRealmList;
        } else {
            OsList osList = proxyState.getRow$realm().getModelList(columnInfo.languagesIndex);
            languagesRealmList = new RealmList<com.kg.gettransfer.data.model.secondary.Language>(com.kg.gettransfer.data.model.secondary.Language.class, osList, proxyState.getRealm$realm());
            return languagesRealmList;
        }
    }

    @Override
    public void realmSet$languages(RealmList<com.kg.gettransfer.data.model.secondary.Language> value) {
        if (proxyState.isUnderConstruction()) {
            if (!proxyState.getAcceptDefaultValue$realm()) {
                return;
            }
            if (proxyState.getExcludeFields$realm().contains("languages")) {
                return;
            }
            // if the list contains unmanaged RealmObjects, convert them to managed.
            if (value != null && !value.isManaged()) {
                final Realm realm = (Realm) proxyState.getRealm$realm();
                final RealmList<com.kg.gettransfer.data.model.secondary.Language> original = value;
                value = new RealmList<com.kg.gettransfer.data.model.secondary.Language>();
                for (com.kg.gettransfer.data.model.secondary.Language item : original) {
                    if (item == null || RealmObject.isManaged(item)) {
                        value.add(item);
                    } else {
                        value.add(realm.copyToRealm(item));
                    }
                }
            }
        }

        proxyState.getRealm$realm().checkIfValid();
        OsList osList = proxyState.getRow$realm().getModelList(columnInfo.languagesIndex);
        // For lists of equal lengths, we need to set each element directly as clearing the receiver list can be wrong if the input and target list are the same.
        if (value != null && value.size() == osList.size()) {
            int objects = value.size();
            for (int i = 0; i < objects; i++) {
                com.kg.gettransfer.data.model.secondary.Language linkedObject = value.get(i);
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
                com.kg.gettransfer.data.model.secondary.Language linkedObject = value.get(i);
                proxyState.checkValidObject(linkedObject);
                osList.addRow(((RealmObjectProxy) linkedObject).realmGet$proxyState().getRow$realm().getIndex());
            }
        }
    }

    @Override
    public com.kg.gettransfer.data.model.secondary.Rating realmGet$rating() {
        proxyState.getRealm$realm().checkIfValid();
        if (proxyState.getRow$realm().isNullLink(columnInfo.ratingIndex)) {
            return null;
        }
        return proxyState.getRealm$realm().get(com.kg.gettransfer.data.model.secondary.Rating.class, proxyState.getRow$realm().getLink(columnInfo.ratingIndex), false, Collections.<String>emptyList());
    }

    @Override
    public void realmSet$rating(com.kg.gettransfer.data.model.secondary.Rating value) {
        if (proxyState.isUnderConstruction()) {
            if (!proxyState.getAcceptDefaultValue$realm()) {
                return;
            }
            if (proxyState.getExcludeFields$realm().contains("rating")) {
                return;
            }
            if (value != null && !RealmObject.isManaged(value)) {
                value = ((Realm) proxyState.getRealm$realm()).copyToRealm(value);
            }
            final Row row = proxyState.getRow$realm();
            if (value == null) {
                // Table#nullifyLink() does not support default value. Just using Row.
                row.nullifyLink(columnInfo.ratingIndex);
                return;
            }
            proxyState.checkValidObject(value);
            row.getTable().setLink(columnInfo.ratingIndex, row.getIndex(), ((RealmObjectProxy) value).realmGet$proxyState().getRow$realm().getIndex(), true);
            return;
        }

        proxyState.getRealm$realm().checkIfValid();
        if (value == null) {
            proxyState.getRow$realm().nullifyLink(columnInfo.ratingIndex);
            return;
        }
        proxyState.checkValidObject(value);
        proxyState.getRow$realm().setLink(columnInfo.ratingIndex, ((RealmObjectProxy) value).realmGet$proxyState().getRow$realm().getIndex());
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
            if (!proxyState.getAcceptDefaultValue$realm()) {
                return;
            }
            final Row row = proxyState.getRow$realm();
            if (value == null) {
                row.getTable().setNull(columnInfo.emailIndex, row.getIndex(), true);
                return;
            }
            row.getTable().setString(columnInfo.emailIndex, row.getIndex(), value, true);
            return;
        }

        proxyState.getRealm$realm().checkIfValid();
        if (value == null) {
            proxyState.getRow$realm().setNull(columnInfo.emailIndex);
            return;
        }
        proxyState.getRow$realm().setString(columnInfo.emailIndex, value);
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
    public String realmGet$alternatePhone() {
        proxyState.getRealm$realm().checkIfValid();
        return (java.lang.String) proxyState.getRow$realm().getString(columnInfo.alternatePhoneIndex);
    }

    @Override
    public void realmSet$alternatePhone(String value) {
        if (proxyState.isUnderConstruction()) {
            if (!proxyState.getAcceptDefaultValue$realm()) {
                return;
            }
            final Row row = proxyState.getRow$realm();
            if (value == null) {
                row.getTable().setNull(columnInfo.alternatePhoneIndex, row.getIndex(), true);
                return;
            }
            row.getTable().setString(columnInfo.alternatePhoneIndex, row.getIndex(), value, true);
            return;
        }

        proxyState.getRealm$realm().checkIfValid();
        if (value == null) {
            proxyState.getRow$realm().setNull(columnInfo.alternatePhoneIndex);
            return;
        }
        proxyState.getRow$realm().setString(columnInfo.alternatePhoneIndex, value);
    }

    private static OsObjectSchemaInfo createExpectedObjectSchemaInfo() {
        OsObjectSchemaInfo.Builder builder = new OsObjectSchemaInfo.Builder("Carrier", 9, 0);
        builder.addPersistedProperty("id", RealmFieldType.INTEGER, Property.PRIMARY_KEY, Property.INDEXED, Property.REQUIRED);
        builder.addPersistedProperty("title", RealmFieldType.STRING, !Property.PRIMARY_KEY, !Property.INDEXED, !Property.REQUIRED);
        builder.addPersistedProperty("approved", RealmFieldType.BOOLEAN, !Property.PRIMARY_KEY, !Property.INDEXED, !Property.REQUIRED);
        builder.addPersistedProperty("completedTransfers", RealmFieldType.INTEGER, !Property.PRIMARY_KEY, !Property.INDEXED, Property.REQUIRED);
        builder.addPersistedLinkProperty("languages", RealmFieldType.LIST, "Language");
        builder.addPersistedLinkProperty("rating", RealmFieldType.OBJECT, "Rating");
        builder.addPersistedProperty("email", RealmFieldType.STRING, !Property.PRIMARY_KEY, !Property.INDEXED, !Property.REQUIRED);
        builder.addPersistedProperty("phone", RealmFieldType.STRING, !Property.PRIMARY_KEY, !Property.INDEXED, !Property.REQUIRED);
        builder.addPersistedProperty("alternatePhone", RealmFieldType.STRING, !Property.PRIMARY_KEY, !Property.INDEXED, !Property.REQUIRED);
        return builder.build();
    }

    public static OsObjectSchemaInfo getExpectedObjectSchemaInfo() {
        return expectedObjectSchemaInfo;
    }

    public static CarrierColumnInfo createColumnInfo(OsSchemaInfo schemaInfo) {
        return new CarrierColumnInfo(schemaInfo);
    }

    public static String getSimpleClassName() {
        return "Carrier";
    }

    public static final class ClassNameHelper {
        public static final String INTERNAL_CLASS_NAME = "Carrier";
    }

    @SuppressWarnings("cast")
    public static com.kg.gettransfer.data.model.Carrier createOrUpdateUsingJsonObject(Realm realm, JSONObject json, boolean update)
        throws JSONException {
        final List<String> excludeFields = new ArrayList<String>(2);
        com.kg.gettransfer.data.model.Carrier obj = null;
        if (update) {
            Table table = realm.getTable(com.kg.gettransfer.data.model.Carrier.class);
            CarrierColumnInfo columnInfo = (CarrierColumnInfo) realm.getSchema().getColumnInfo(com.kg.gettransfer.data.model.Carrier.class);
            long pkColumnIndex = columnInfo.idIndex;
            long rowIndex = Table.NO_MATCH;
            if (!json.isNull("id")) {
                rowIndex = table.findFirstLong(pkColumnIndex, json.getLong("id"));
            }
            if (rowIndex != Table.NO_MATCH) {
                final BaseRealm.RealmObjectContext objectContext = BaseRealm.objectContext.get();
                try {
                    objectContext.set(realm, table.getUncheckedRow(rowIndex), realm.getSchema().getColumnInfo(com.kg.gettransfer.data.model.Carrier.class), false, Collections.<String> emptyList());
                    obj = new io.realm.com_kg_gettransfer_data_model_CarrierRealmProxy();
                } finally {
                    objectContext.clear();
                }
            }
        }
        if (obj == null) {
            if (json.has("languages")) {
                excludeFields.add("languages");
            }
            if (json.has("rating")) {
                excludeFields.add("rating");
            }
            if (json.has("id")) {
                if (json.isNull("id")) {
                    obj = (io.realm.com_kg_gettransfer_data_model_CarrierRealmProxy) realm.createObjectInternal(com.kg.gettransfer.data.model.Carrier.class, null, true, excludeFields);
                } else {
                    obj = (io.realm.com_kg_gettransfer_data_model_CarrierRealmProxy) realm.createObjectInternal(com.kg.gettransfer.data.model.Carrier.class, json.getInt("id"), true, excludeFields);
                }
            } else {
                throw new IllegalArgumentException("JSON object doesn't have the primary key field 'id'.");
            }
        }

        final com_kg_gettransfer_data_model_CarrierRealmProxyInterface objProxy = (com_kg_gettransfer_data_model_CarrierRealmProxyInterface) obj;
        if (json.has("title")) {
            if (json.isNull("title")) {
                objProxy.realmSet$title(null);
            } else {
                objProxy.realmSet$title((String) json.getString("title"));
            }
        }
        if (json.has("approved")) {
            if (json.isNull("approved")) {
                objProxy.realmSet$approved(null);
            } else {
                objProxy.realmSet$approved((boolean) json.getBoolean("approved"));
            }
        }
        if (json.has("completedTransfers")) {
            if (json.isNull("completedTransfers")) {
                throw new IllegalArgumentException("Trying to set non-nullable field 'completedTransfers' to null.");
            } else {
                objProxy.realmSet$completedTransfers((int) json.getInt("completedTransfers"));
            }
        }
        if (json.has("languages")) {
            if (json.isNull("languages")) {
                objProxy.realmSet$languages(null);
            } else {
                objProxy.realmGet$languages().clear();
                JSONArray array = json.getJSONArray("languages");
                for (int i = 0; i < array.length(); i++) {
                    com.kg.gettransfer.data.model.secondary.Language item = com_kg_gettransfer_data_model_secondary_LanguageRealmProxy.createOrUpdateUsingJsonObject(realm, array.getJSONObject(i), update);
                    objProxy.realmGet$languages().add(item);
                }
            }
        }
        if (json.has("rating")) {
            if (json.isNull("rating")) {
                objProxy.realmSet$rating(null);
            } else {
                com.kg.gettransfer.data.model.secondary.Rating ratingObj = com_kg_gettransfer_data_model_secondary_RatingRealmProxy.createOrUpdateUsingJsonObject(realm, json.getJSONObject("rating"), update);
                objProxy.realmSet$rating(ratingObj);
            }
        }
        if (json.has("email")) {
            if (json.isNull("email")) {
                objProxy.realmSet$email(null);
            } else {
                objProxy.realmSet$email((String) json.getString("email"));
            }
        }
        if (json.has("phone")) {
            if (json.isNull("phone")) {
                objProxy.realmSet$phone(null);
            } else {
                objProxy.realmSet$phone((String) json.getString("phone"));
            }
        }
        if (json.has("alternatePhone")) {
            if (json.isNull("alternatePhone")) {
                objProxy.realmSet$alternatePhone(null);
            } else {
                objProxy.realmSet$alternatePhone((String) json.getString("alternatePhone"));
            }
        }
        return obj;
    }

    @SuppressWarnings("cast")
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static com.kg.gettransfer.data.model.Carrier createUsingJsonStream(Realm realm, JsonReader reader)
        throws IOException {
        boolean jsonHasPrimaryKey = false;
        final com.kg.gettransfer.data.model.Carrier obj = new com.kg.gettransfer.data.model.Carrier();
        final com_kg_gettransfer_data_model_CarrierRealmProxyInterface objProxy = (com_kg_gettransfer_data_model_CarrierRealmProxyInterface) obj;
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
            } else if (name.equals("title")) {
                if (reader.peek() != JsonToken.NULL) {
                    objProxy.realmSet$title((String) reader.nextString());
                } else {
                    reader.skipValue();
                    objProxy.realmSet$title(null);
                }
            } else if (name.equals("approved")) {
                if (reader.peek() != JsonToken.NULL) {
                    objProxy.realmSet$approved((boolean) reader.nextBoolean());
                } else {
                    reader.skipValue();
                    objProxy.realmSet$approved(null);
                }
            } else if (name.equals("completedTransfers")) {
                if (reader.peek() != JsonToken.NULL) {
                    objProxy.realmSet$completedTransfers((int) reader.nextInt());
                } else {
                    reader.skipValue();
                    throw new IllegalArgumentException("Trying to set non-nullable field 'completedTransfers' to null.");
                }
            } else if (name.equals("languages")) {
                if (reader.peek() == JsonToken.NULL) {
                    reader.skipValue();
                    objProxy.realmSet$languages(null);
                } else {
                    objProxy.realmSet$languages(new RealmList<com.kg.gettransfer.data.model.secondary.Language>());
                    reader.beginArray();
                    while (reader.hasNext()) {
                        com.kg.gettransfer.data.model.secondary.Language item = com_kg_gettransfer_data_model_secondary_LanguageRealmProxy.createUsingJsonStream(realm, reader);
                        objProxy.realmGet$languages().add(item);
                    }
                    reader.endArray();
                }
            } else if (name.equals("rating")) {
                if (reader.peek() == JsonToken.NULL) {
                    reader.skipValue();
                    objProxy.realmSet$rating(null);
                } else {
                    com.kg.gettransfer.data.model.secondary.Rating ratingObj = com_kg_gettransfer_data_model_secondary_RatingRealmProxy.createUsingJsonStream(realm, reader);
                    objProxy.realmSet$rating(ratingObj);
                }
            } else if (name.equals("email")) {
                if (reader.peek() != JsonToken.NULL) {
                    objProxy.realmSet$email((String) reader.nextString());
                } else {
                    reader.skipValue();
                    objProxy.realmSet$email(null);
                }
            } else if (name.equals("phone")) {
                if (reader.peek() != JsonToken.NULL) {
                    objProxy.realmSet$phone((String) reader.nextString());
                } else {
                    reader.skipValue();
                    objProxy.realmSet$phone(null);
                }
            } else if (name.equals("alternatePhone")) {
                if (reader.peek() != JsonToken.NULL) {
                    objProxy.realmSet$alternatePhone((String) reader.nextString());
                } else {
                    reader.skipValue();
                    objProxy.realmSet$alternatePhone(null);
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

    public static com.kg.gettransfer.data.model.Carrier copyOrUpdate(Realm realm, com.kg.gettransfer.data.model.Carrier object, boolean update, Map<RealmModel,RealmObjectProxy> cache) {
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
            return (com.kg.gettransfer.data.model.Carrier) cachedRealmObject;
        }

        com.kg.gettransfer.data.model.Carrier realmObject = null;
        boolean canUpdate = update;
        if (canUpdate) {
            Table table = realm.getTable(com.kg.gettransfer.data.model.Carrier.class);
            CarrierColumnInfo columnInfo = (CarrierColumnInfo) realm.getSchema().getColumnInfo(com.kg.gettransfer.data.model.Carrier.class);
            long pkColumnIndex = columnInfo.idIndex;
            long rowIndex = table.findFirstLong(pkColumnIndex, ((com_kg_gettransfer_data_model_CarrierRealmProxyInterface) object).realmGet$id());
            if (rowIndex == Table.NO_MATCH) {
                canUpdate = false;
            } else {
                try {
                    objectContext.set(realm, table.getUncheckedRow(rowIndex), realm.getSchema().getColumnInfo(com.kg.gettransfer.data.model.Carrier.class), false, Collections.<String> emptyList());
                    realmObject = new io.realm.com_kg_gettransfer_data_model_CarrierRealmProxy();
                    cache.put(object, (RealmObjectProxy) realmObject);
                } finally {
                    objectContext.clear();
                }
            }
        }

        return (canUpdate) ? update(realm, realmObject, object, cache) : copy(realm, object, update, cache);
    }

    public static com.kg.gettransfer.data.model.Carrier copy(Realm realm, com.kg.gettransfer.data.model.Carrier newObject, boolean update, Map<RealmModel,RealmObjectProxy> cache) {
        RealmObjectProxy cachedRealmObject = cache.get(newObject);
        if (cachedRealmObject != null) {
            return (com.kg.gettransfer.data.model.Carrier) cachedRealmObject;
        }

        // rejecting default values to avoid creating unexpected objects from RealmModel/RealmList fields.
        com.kg.gettransfer.data.model.Carrier realmObject = realm.createObjectInternal(com.kg.gettransfer.data.model.Carrier.class, ((com_kg_gettransfer_data_model_CarrierRealmProxyInterface) newObject).realmGet$id(), false, Collections.<String>emptyList());
        cache.put(newObject, (RealmObjectProxy) realmObject);

        com_kg_gettransfer_data_model_CarrierRealmProxyInterface realmObjectSource = (com_kg_gettransfer_data_model_CarrierRealmProxyInterface) newObject;
        com_kg_gettransfer_data_model_CarrierRealmProxyInterface realmObjectCopy = (com_kg_gettransfer_data_model_CarrierRealmProxyInterface) realmObject;

        realmObjectCopy.realmSet$title(realmObjectSource.realmGet$title());
        realmObjectCopy.realmSet$approved(realmObjectSource.realmGet$approved());
        realmObjectCopy.realmSet$completedTransfers(realmObjectSource.realmGet$completedTransfers());

        RealmList<com.kg.gettransfer.data.model.secondary.Language> languagesList = realmObjectSource.realmGet$languages();
        if (languagesList != null) {
            RealmList<com.kg.gettransfer.data.model.secondary.Language> languagesRealmList = realmObjectCopy.realmGet$languages();
            languagesRealmList.clear();
            for (int i = 0; i < languagesList.size(); i++) {
                com.kg.gettransfer.data.model.secondary.Language languagesItem = languagesList.get(i);
                com.kg.gettransfer.data.model.secondary.Language cachelanguages = (com.kg.gettransfer.data.model.secondary.Language) cache.get(languagesItem);
                if (cachelanguages != null) {
                    languagesRealmList.add(cachelanguages);
                } else {
                    languagesRealmList.add(com_kg_gettransfer_data_model_secondary_LanguageRealmProxy.copyOrUpdate(realm, languagesItem, update, cache));
                }
            }
        }


        com.kg.gettransfer.data.model.secondary.Rating ratingObj = realmObjectSource.realmGet$rating();
        if (ratingObj == null) {
            realmObjectCopy.realmSet$rating(null);
        } else {
            com.kg.gettransfer.data.model.secondary.Rating cacherating = (com.kg.gettransfer.data.model.secondary.Rating) cache.get(ratingObj);
            if (cacherating != null) {
                realmObjectCopy.realmSet$rating(cacherating);
            } else {
                realmObjectCopy.realmSet$rating(com_kg_gettransfer_data_model_secondary_RatingRealmProxy.copyOrUpdate(realm, ratingObj, update, cache));
            }
        }
        realmObjectCopy.realmSet$email(realmObjectSource.realmGet$email());
        realmObjectCopy.realmSet$phone(realmObjectSource.realmGet$phone());
        realmObjectCopy.realmSet$alternatePhone(realmObjectSource.realmGet$alternatePhone());
        return realmObject;
    }

    public static long insert(Realm realm, com.kg.gettransfer.data.model.Carrier object, Map<RealmModel,Long> cache) {
        if (object instanceof RealmObjectProxy && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm() != null && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm().getPath().equals(realm.getPath())) {
            return ((RealmObjectProxy) object).realmGet$proxyState().getRow$realm().getIndex();
        }
        Table table = realm.getTable(com.kg.gettransfer.data.model.Carrier.class);
        long tableNativePtr = table.getNativePtr();
        CarrierColumnInfo columnInfo = (CarrierColumnInfo) realm.getSchema().getColumnInfo(com.kg.gettransfer.data.model.Carrier.class);
        long pkColumnIndex = columnInfo.idIndex;
        long rowIndex = Table.NO_MATCH;
        Object primaryKeyValue = ((com_kg_gettransfer_data_model_CarrierRealmProxyInterface) object).realmGet$id();
        if (primaryKeyValue != null) {
            rowIndex = Table.nativeFindFirstInt(tableNativePtr, pkColumnIndex, ((com_kg_gettransfer_data_model_CarrierRealmProxyInterface) object).realmGet$id());
        }
        if (rowIndex == Table.NO_MATCH) {
            rowIndex = OsObject.createRowWithPrimaryKey(table, pkColumnIndex, ((com_kg_gettransfer_data_model_CarrierRealmProxyInterface) object).realmGet$id());
        } else {
            Table.throwDuplicatePrimaryKeyException(primaryKeyValue);
        }
        cache.put(object, rowIndex);
        String realmGet$title = ((com_kg_gettransfer_data_model_CarrierRealmProxyInterface) object).realmGet$title();
        if (realmGet$title != null) {
            Table.nativeSetString(tableNativePtr, columnInfo.titleIndex, rowIndex, realmGet$title, false);
        }
        Boolean realmGet$approved = ((com_kg_gettransfer_data_model_CarrierRealmProxyInterface) object).realmGet$approved();
        if (realmGet$approved != null) {
            Table.nativeSetBoolean(tableNativePtr, columnInfo.approvedIndex, rowIndex, realmGet$approved, false);
        }
        Table.nativeSetLong(tableNativePtr, columnInfo.completedTransfersIndex, rowIndex, ((com_kg_gettransfer_data_model_CarrierRealmProxyInterface) object).realmGet$completedTransfers(), false);

        RealmList<com.kg.gettransfer.data.model.secondary.Language> languagesList = ((com_kg_gettransfer_data_model_CarrierRealmProxyInterface) object).realmGet$languages();
        if (languagesList != null) {
            OsList languagesOsList = new OsList(table.getUncheckedRow(rowIndex), columnInfo.languagesIndex);
            for (com.kg.gettransfer.data.model.secondary.Language languagesItem : languagesList) {
                Long cacheItemIndexlanguages = cache.get(languagesItem);
                if (cacheItemIndexlanguages == null) {
                    cacheItemIndexlanguages = com_kg_gettransfer_data_model_secondary_LanguageRealmProxy.insert(realm, languagesItem, cache);
                }
                languagesOsList.addRow(cacheItemIndexlanguages);
            }
        }

        com.kg.gettransfer.data.model.secondary.Rating ratingObj = ((com_kg_gettransfer_data_model_CarrierRealmProxyInterface) object).realmGet$rating();
        if (ratingObj != null) {
            Long cacherating = cache.get(ratingObj);
            if (cacherating == null) {
                cacherating = com_kg_gettransfer_data_model_secondary_RatingRealmProxy.insert(realm, ratingObj, cache);
            }
            Table.nativeSetLink(tableNativePtr, columnInfo.ratingIndex, rowIndex, cacherating, false);
        }
        String realmGet$email = ((com_kg_gettransfer_data_model_CarrierRealmProxyInterface) object).realmGet$email();
        if (realmGet$email != null) {
            Table.nativeSetString(tableNativePtr, columnInfo.emailIndex, rowIndex, realmGet$email, false);
        }
        String realmGet$phone = ((com_kg_gettransfer_data_model_CarrierRealmProxyInterface) object).realmGet$phone();
        if (realmGet$phone != null) {
            Table.nativeSetString(tableNativePtr, columnInfo.phoneIndex, rowIndex, realmGet$phone, false);
        }
        String realmGet$alternatePhone = ((com_kg_gettransfer_data_model_CarrierRealmProxyInterface) object).realmGet$alternatePhone();
        if (realmGet$alternatePhone != null) {
            Table.nativeSetString(tableNativePtr, columnInfo.alternatePhoneIndex, rowIndex, realmGet$alternatePhone, false);
        }
        return rowIndex;
    }

    public static void insert(Realm realm, Iterator<? extends RealmModel> objects, Map<RealmModel,Long> cache) {
        Table table = realm.getTable(com.kg.gettransfer.data.model.Carrier.class);
        long tableNativePtr = table.getNativePtr();
        CarrierColumnInfo columnInfo = (CarrierColumnInfo) realm.getSchema().getColumnInfo(com.kg.gettransfer.data.model.Carrier.class);
        long pkColumnIndex = columnInfo.idIndex;
        com.kg.gettransfer.data.model.Carrier object = null;
        while (objects.hasNext()) {
            object = (com.kg.gettransfer.data.model.Carrier) objects.next();
            if (cache.containsKey(object)) {
                continue;
            }
            if (object instanceof RealmObjectProxy && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm() != null && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm().getPath().equals(realm.getPath())) {
                cache.put(object, ((RealmObjectProxy) object).realmGet$proxyState().getRow$realm().getIndex());
                continue;
            }
            long rowIndex = Table.NO_MATCH;
            Object primaryKeyValue = ((com_kg_gettransfer_data_model_CarrierRealmProxyInterface) object).realmGet$id();
            if (primaryKeyValue != null) {
                rowIndex = Table.nativeFindFirstInt(tableNativePtr, pkColumnIndex, ((com_kg_gettransfer_data_model_CarrierRealmProxyInterface) object).realmGet$id());
            }
            if (rowIndex == Table.NO_MATCH) {
                rowIndex = OsObject.createRowWithPrimaryKey(table, pkColumnIndex, ((com_kg_gettransfer_data_model_CarrierRealmProxyInterface) object).realmGet$id());
            } else {
                Table.throwDuplicatePrimaryKeyException(primaryKeyValue);
            }
            cache.put(object, rowIndex);
            String realmGet$title = ((com_kg_gettransfer_data_model_CarrierRealmProxyInterface) object).realmGet$title();
            if (realmGet$title != null) {
                Table.nativeSetString(tableNativePtr, columnInfo.titleIndex, rowIndex, realmGet$title, false);
            }
            Boolean realmGet$approved = ((com_kg_gettransfer_data_model_CarrierRealmProxyInterface) object).realmGet$approved();
            if (realmGet$approved != null) {
                Table.nativeSetBoolean(tableNativePtr, columnInfo.approvedIndex, rowIndex, realmGet$approved, false);
            }
            Table.nativeSetLong(tableNativePtr, columnInfo.completedTransfersIndex, rowIndex, ((com_kg_gettransfer_data_model_CarrierRealmProxyInterface) object).realmGet$completedTransfers(), false);

            RealmList<com.kg.gettransfer.data.model.secondary.Language> languagesList = ((com_kg_gettransfer_data_model_CarrierRealmProxyInterface) object).realmGet$languages();
            if (languagesList != null) {
                OsList languagesOsList = new OsList(table.getUncheckedRow(rowIndex), columnInfo.languagesIndex);
                for (com.kg.gettransfer.data.model.secondary.Language languagesItem : languagesList) {
                    Long cacheItemIndexlanguages = cache.get(languagesItem);
                    if (cacheItemIndexlanguages == null) {
                        cacheItemIndexlanguages = com_kg_gettransfer_data_model_secondary_LanguageRealmProxy.insert(realm, languagesItem, cache);
                    }
                    languagesOsList.addRow(cacheItemIndexlanguages);
                }
            }

            com.kg.gettransfer.data.model.secondary.Rating ratingObj = ((com_kg_gettransfer_data_model_CarrierRealmProxyInterface) object).realmGet$rating();
            if (ratingObj != null) {
                Long cacherating = cache.get(ratingObj);
                if (cacherating == null) {
                    cacherating = com_kg_gettransfer_data_model_secondary_RatingRealmProxy.insert(realm, ratingObj, cache);
                }
                table.setLink(columnInfo.ratingIndex, rowIndex, cacherating, false);
            }
            String realmGet$email = ((com_kg_gettransfer_data_model_CarrierRealmProxyInterface) object).realmGet$email();
            if (realmGet$email != null) {
                Table.nativeSetString(tableNativePtr, columnInfo.emailIndex, rowIndex, realmGet$email, false);
            }
            String realmGet$phone = ((com_kg_gettransfer_data_model_CarrierRealmProxyInterface) object).realmGet$phone();
            if (realmGet$phone != null) {
                Table.nativeSetString(tableNativePtr, columnInfo.phoneIndex, rowIndex, realmGet$phone, false);
            }
            String realmGet$alternatePhone = ((com_kg_gettransfer_data_model_CarrierRealmProxyInterface) object).realmGet$alternatePhone();
            if (realmGet$alternatePhone != null) {
                Table.nativeSetString(tableNativePtr, columnInfo.alternatePhoneIndex, rowIndex, realmGet$alternatePhone, false);
            }
        }
    }

    public static long insertOrUpdate(Realm realm, com.kg.gettransfer.data.model.Carrier object, Map<RealmModel,Long> cache) {
        if (object instanceof RealmObjectProxy && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm() != null && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm().getPath().equals(realm.getPath())) {
            return ((RealmObjectProxy) object).realmGet$proxyState().getRow$realm().getIndex();
        }
        Table table = realm.getTable(com.kg.gettransfer.data.model.Carrier.class);
        long tableNativePtr = table.getNativePtr();
        CarrierColumnInfo columnInfo = (CarrierColumnInfo) realm.getSchema().getColumnInfo(com.kg.gettransfer.data.model.Carrier.class);
        long pkColumnIndex = columnInfo.idIndex;
        long rowIndex = Table.NO_MATCH;
        Object primaryKeyValue = ((com_kg_gettransfer_data_model_CarrierRealmProxyInterface) object).realmGet$id();
        if (primaryKeyValue != null) {
            rowIndex = Table.nativeFindFirstInt(tableNativePtr, pkColumnIndex, ((com_kg_gettransfer_data_model_CarrierRealmProxyInterface) object).realmGet$id());
        }
        if (rowIndex == Table.NO_MATCH) {
            rowIndex = OsObject.createRowWithPrimaryKey(table, pkColumnIndex, ((com_kg_gettransfer_data_model_CarrierRealmProxyInterface) object).realmGet$id());
        }
        cache.put(object, rowIndex);
        String realmGet$title = ((com_kg_gettransfer_data_model_CarrierRealmProxyInterface) object).realmGet$title();
        if (realmGet$title != null) {
            Table.nativeSetString(tableNativePtr, columnInfo.titleIndex, rowIndex, realmGet$title, false);
        } else {
            Table.nativeSetNull(tableNativePtr, columnInfo.titleIndex, rowIndex, false);
        }
        Boolean realmGet$approved = ((com_kg_gettransfer_data_model_CarrierRealmProxyInterface) object).realmGet$approved();
        if (realmGet$approved != null) {
            Table.nativeSetBoolean(tableNativePtr, columnInfo.approvedIndex, rowIndex, realmGet$approved, false);
        } else {
            Table.nativeSetNull(tableNativePtr, columnInfo.approvedIndex, rowIndex, false);
        }
        Table.nativeSetLong(tableNativePtr, columnInfo.completedTransfersIndex, rowIndex, ((com_kg_gettransfer_data_model_CarrierRealmProxyInterface) object).realmGet$completedTransfers(), false);

        OsList languagesOsList = new OsList(table.getUncheckedRow(rowIndex), columnInfo.languagesIndex);
        RealmList<com.kg.gettransfer.data.model.secondary.Language> languagesList = ((com_kg_gettransfer_data_model_CarrierRealmProxyInterface) object).realmGet$languages();
        if (languagesList != null && languagesList.size() == languagesOsList.size()) {
            // For lists of equal lengths, we need to set each element directly as clearing the receiver list can be wrong if the input and target list are the same.
            int objects = languagesList.size();
            for (int i = 0; i < objects; i++) {
                com.kg.gettransfer.data.model.secondary.Language languagesItem = languagesList.get(i);
                Long cacheItemIndexlanguages = cache.get(languagesItem);
                if (cacheItemIndexlanguages == null) {
                    cacheItemIndexlanguages = com_kg_gettransfer_data_model_secondary_LanguageRealmProxy.insertOrUpdate(realm, languagesItem, cache);
                }
                languagesOsList.setRow(i, cacheItemIndexlanguages);
            }
        } else {
            languagesOsList.removeAll();
            if (languagesList != null) {
                for (com.kg.gettransfer.data.model.secondary.Language languagesItem : languagesList) {
                    Long cacheItemIndexlanguages = cache.get(languagesItem);
                    if (cacheItemIndexlanguages == null) {
                        cacheItemIndexlanguages = com_kg_gettransfer_data_model_secondary_LanguageRealmProxy.insertOrUpdate(realm, languagesItem, cache);
                    }
                    languagesOsList.addRow(cacheItemIndexlanguages);
                }
            }
        }


        com.kg.gettransfer.data.model.secondary.Rating ratingObj = ((com_kg_gettransfer_data_model_CarrierRealmProxyInterface) object).realmGet$rating();
        if (ratingObj != null) {
            Long cacherating = cache.get(ratingObj);
            if (cacherating == null) {
                cacherating = com_kg_gettransfer_data_model_secondary_RatingRealmProxy.insertOrUpdate(realm, ratingObj, cache);
            }
            Table.nativeSetLink(tableNativePtr, columnInfo.ratingIndex, rowIndex, cacherating, false);
        } else {
            Table.nativeNullifyLink(tableNativePtr, columnInfo.ratingIndex, rowIndex);
        }
        String realmGet$email = ((com_kg_gettransfer_data_model_CarrierRealmProxyInterface) object).realmGet$email();
        if (realmGet$email != null) {
            Table.nativeSetString(tableNativePtr, columnInfo.emailIndex, rowIndex, realmGet$email, false);
        } else {
            Table.nativeSetNull(tableNativePtr, columnInfo.emailIndex, rowIndex, false);
        }
        String realmGet$phone = ((com_kg_gettransfer_data_model_CarrierRealmProxyInterface) object).realmGet$phone();
        if (realmGet$phone != null) {
            Table.nativeSetString(tableNativePtr, columnInfo.phoneIndex, rowIndex, realmGet$phone, false);
        } else {
            Table.nativeSetNull(tableNativePtr, columnInfo.phoneIndex, rowIndex, false);
        }
        String realmGet$alternatePhone = ((com_kg_gettransfer_data_model_CarrierRealmProxyInterface) object).realmGet$alternatePhone();
        if (realmGet$alternatePhone != null) {
            Table.nativeSetString(tableNativePtr, columnInfo.alternatePhoneIndex, rowIndex, realmGet$alternatePhone, false);
        } else {
            Table.nativeSetNull(tableNativePtr, columnInfo.alternatePhoneIndex, rowIndex, false);
        }
        return rowIndex;
    }

    public static void insertOrUpdate(Realm realm, Iterator<? extends RealmModel> objects, Map<RealmModel,Long> cache) {
        Table table = realm.getTable(com.kg.gettransfer.data.model.Carrier.class);
        long tableNativePtr = table.getNativePtr();
        CarrierColumnInfo columnInfo = (CarrierColumnInfo) realm.getSchema().getColumnInfo(com.kg.gettransfer.data.model.Carrier.class);
        long pkColumnIndex = columnInfo.idIndex;
        com.kg.gettransfer.data.model.Carrier object = null;
        while (objects.hasNext()) {
            object = (com.kg.gettransfer.data.model.Carrier) objects.next();
            if (cache.containsKey(object)) {
                continue;
            }
            if (object instanceof RealmObjectProxy && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm() != null && ((RealmObjectProxy) object).realmGet$proxyState().getRealm$realm().getPath().equals(realm.getPath())) {
                cache.put(object, ((RealmObjectProxy) object).realmGet$proxyState().getRow$realm().getIndex());
                continue;
            }
            long rowIndex = Table.NO_MATCH;
            Object primaryKeyValue = ((com_kg_gettransfer_data_model_CarrierRealmProxyInterface) object).realmGet$id();
            if (primaryKeyValue != null) {
                rowIndex = Table.nativeFindFirstInt(tableNativePtr, pkColumnIndex, ((com_kg_gettransfer_data_model_CarrierRealmProxyInterface) object).realmGet$id());
            }
            if (rowIndex == Table.NO_MATCH) {
                rowIndex = OsObject.createRowWithPrimaryKey(table, pkColumnIndex, ((com_kg_gettransfer_data_model_CarrierRealmProxyInterface) object).realmGet$id());
            }
            cache.put(object, rowIndex);
            String realmGet$title = ((com_kg_gettransfer_data_model_CarrierRealmProxyInterface) object).realmGet$title();
            if (realmGet$title != null) {
                Table.nativeSetString(tableNativePtr, columnInfo.titleIndex, rowIndex, realmGet$title, false);
            } else {
                Table.nativeSetNull(tableNativePtr, columnInfo.titleIndex, rowIndex, false);
            }
            Boolean realmGet$approved = ((com_kg_gettransfer_data_model_CarrierRealmProxyInterface) object).realmGet$approved();
            if (realmGet$approved != null) {
                Table.nativeSetBoolean(tableNativePtr, columnInfo.approvedIndex, rowIndex, realmGet$approved, false);
            } else {
                Table.nativeSetNull(tableNativePtr, columnInfo.approvedIndex, rowIndex, false);
            }
            Table.nativeSetLong(tableNativePtr, columnInfo.completedTransfersIndex, rowIndex, ((com_kg_gettransfer_data_model_CarrierRealmProxyInterface) object).realmGet$completedTransfers(), false);

            OsList languagesOsList = new OsList(table.getUncheckedRow(rowIndex), columnInfo.languagesIndex);
            RealmList<com.kg.gettransfer.data.model.secondary.Language> languagesList = ((com_kg_gettransfer_data_model_CarrierRealmProxyInterface) object).realmGet$languages();
            if (languagesList != null && languagesList.size() == languagesOsList.size()) {
                // For lists of equal lengths, we need to set each element directly as clearing the receiver list can be wrong if the input and target list are the same.
                int objectCount = languagesList.size();
                for (int i = 0; i < objectCount; i++) {
                    com.kg.gettransfer.data.model.secondary.Language languagesItem = languagesList.get(i);
                    Long cacheItemIndexlanguages = cache.get(languagesItem);
                    if (cacheItemIndexlanguages == null) {
                        cacheItemIndexlanguages = com_kg_gettransfer_data_model_secondary_LanguageRealmProxy.insertOrUpdate(realm, languagesItem, cache);
                    }
                    languagesOsList.setRow(i, cacheItemIndexlanguages);
                }
            } else {
                languagesOsList.removeAll();
                if (languagesList != null) {
                    for (com.kg.gettransfer.data.model.secondary.Language languagesItem : languagesList) {
                        Long cacheItemIndexlanguages = cache.get(languagesItem);
                        if (cacheItemIndexlanguages == null) {
                            cacheItemIndexlanguages = com_kg_gettransfer_data_model_secondary_LanguageRealmProxy.insertOrUpdate(realm, languagesItem, cache);
                        }
                        languagesOsList.addRow(cacheItemIndexlanguages);
                    }
                }
            }


            com.kg.gettransfer.data.model.secondary.Rating ratingObj = ((com_kg_gettransfer_data_model_CarrierRealmProxyInterface) object).realmGet$rating();
            if (ratingObj != null) {
                Long cacherating = cache.get(ratingObj);
                if (cacherating == null) {
                    cacherating = com_kg_gettransfer_data_model_secondary_RatingRealmProxy.insertOrUpdate(realm, ratingObj, cache);
                }
                Table.nativeSetLink(tableNativePtr, columnInfo.ratingIndex, rowIndex, cacherating, false);
            } else {
                Table.nativeNullifyLink(tableNativePtr, columnInfo.ratingIndex, rowIndex);
            }
            String realmGet$email = ((com_kg_gettransfer_data_model_CarrierRealmProxyInterface) object).realmGet$email();
            if (realmGet$email != null) {
                Table.nativeSetString(tableNativePtr, columnInfo.emailIndex, rowIndex, realmGet$email, false);
            } else {
                Table.nativeSetNull(tableNativePtr, columnInfo.emailIndex, rowIndex, false);
            }
            String realmGet$phone = ((com_kg_gettransfer_data_model_CarrierRealmProxyInterface) object).realmGet$phone();
            if (realmGet$phone != null) {
                Table.nativeSetString(tableNativePtr, columnInfo.phoneIndex, rowIndex, realmGet$phone, false);
            } else {
                Table.nativeSetNull(tableNativePtr, columnInfo.phoneIndex, rowIndex, false);
            }
            String realmGet$alternatePhone = ((com_kg_gettransfer_data_model_CarrierRealmProxyInterface) object).realmGet$alternatePhone();
            if (realmGet$alternatePhone != null) {
                Table.nativeSetString(tableNativePtr, columnInfo.alternatePhoneIndex, rowIndex, realmGet$alternatePhone, false);
            } else {
                Table.nativeSetNull(tableNativePtr, columnInfo.alternatePhoneIndex, rowIndex, false);
            }
        }
    }

    public static com.kg.gettransfer.data.model.Carrier createDetachedCopy(com.kg.gettransfer.data.model.Carrier realmObject, int currentDepth, int maxDepth, Map<RealmModel, CacheData<RealmModel>> cache) {
        if (currentDepth > maxDepth || realmObject == null) {
            return null;
        }
        CacheData<RealmModel> cachedObject = cache.get(realmObject);
        com.kg.gettransfer.data.model.Carrier unmanagedObject;
        if (cachedObject == null) {
            unmanagedObject = new com.kg.gettransfer.data.model.Carrier();
            cache.put(realmObject, new RealmObjectProxy.CacheData<RealmModel>(currentDepth, unmanagedObject));
        } else {
            // Reuse cached object or recreate it because it was encountered at a lower depth.
            if (currentDepth >= cachedObject.minDepth) {
                return (com.kg.gettransfer.data.model.Carrier) cachedObject.object;
            }
            unmanagedObject = (com.kg.gettransfer.data.model.Carrier) cachedObject.object;
            cachedObject.minDepth = currentDepth;
        }
        com_kg_gettransfer_data_model_CarrierRealmProxyInterface unmanagedCopy = (com_kg_gettransfer_data_model_CarrierRealmProxyInterface) unmanagedObject;
        com_kg_gettransfer_data_model_CarrierRealmProxyInterface realmSource = (com_kg_gettransfer_data_model_CarrierRealmProxyInterface) realmObject;
        unmanagedCopy.realmSet$id(realmSource.realmGet$id());
        unmanagedCopy.realmSet$title(realmSource.realmGet$title());
        unmanagedCopy.realmSet$approved(realmSource.realmGet$approved());
        unmanagedCopy.realmSet$completedTransfers(realmSource.realmGet$completedTransfers());

        // Deep copy of languages
        if (currentDepth == maxDepth) {
            unmanagedCopy.realmSet$languages(null);
        } else {
            RealmList<com.kg.gettransfer.data.model.secondary.Language> managedlanguagesList = realmSource.realmGet$languages();
            RealmList<com.kg.gettransfer.data.model.secondary.Language> unmanagedlanguagesList = new RealmList<com.kg.gettransfer.data.model.secondary.Language>();
            unmanagedCopy.realmSet$languages(unmanagedlanguagesList);
            int nextDepth = currentDepth + 1;
            int size = managedlanguagesList.size();
            for (int i = 0; i < size; i++) {
                com.kg.gettransfer.data.model.secondary.Language item = com_kg_gettransfer_data_model_secondary_LanguageRealmProxy.createDetachedCopy(managedlanguagesList.get(i), nextDepth, maxDepth, cache);
                unmanagedlanguagesList.add(item);
            }
        }

        // Deep copy of rating
        unmanagedCopy.realmSet$rating(com_kg_gettransfer_data_model_secondary_RatingRealmProxy.createDetachedCopy(realmSource.realmGet$rating(), currentDepth + 1, maxDepth, cache));
        unmanagedCopy.realmSet$email(realmSource.realmGet$email());
        unmanagedCopy.realmSet$phone(realmSource.realmGet$phone());
        unmanagedCopy.realmSet$alternatePhone(realmSource.realmGet$alternatePhone());

        return unmanagedObject;
    }

    static com.kg.gettransfer.data.model.Carrier update(Realm realm, com.kg.gettransfer.data.model.Carrier realmObject, com.kg.gettransfer.data.model.Carrier newObject, Map<RealmModel, RealmObjectProxy> cache) {
        com_kg_gettransfer_data_model_CarrierRealmProxyInterface realmObjectTarget = (com_kg_gettransfer_data_model_CarrierRealmProxyInterface) realmObject;
        com_kg_gettransfer_data_model_CarrierRealmProxyInterface realmObjectSource = (com_kg_gettransfer_data_model_CarrierRealmProxyInterface) newObject;
        realmObjectTarget.realmSet$title(realmObjectSource.realmGet$title());
        realmObjectTarget.realmSet$approved(realmObjectSource.realmGet$approved());
        realmObjectTarget.realmSet$completedTransfers(realmObjectSource.realmGet$completedTransfers());
        RealmList<com.kg.gettransfer.data.model.secondary.Language> languagesList = realmObjectSource.realmGet$languages();
        RealmList<com.kg.gettransfer.data.model.secondary.Language> languagesRealmList = realmObjectTarget.realmGet$languages();
        if (languagesList != null && languagesList.size() == languagesRealmList.size()) {
            // For lists of equal lengths, we need to set each element directly as clearing the receiver list can be wrong if the input and target list are the same.
            int objects = languagesList.size();
            for (int i = 0; i < objects; i++) {
                com.kg.gettransfer.data.model.secondary.Language languagesItem = languagesList.get(i);
                com.kg.gettransfer.data.model.secondary.Language cachelanguages = (com.kg.gettransfer.data.model.secondary.Language) cache.get(languagesItem);
                if (cachelanguages != null) {
                    languagesRealmList.set(i, cachelanguages);
                } else {
                    languagesRealmList.set(i, com_kg_gettransfer_data_model_secondary_LanguageRealmProxy.copyOrUpdate(realm, languagesItem, true, cache));
                }
            }
        } else {
            languagesRealmList.clear();
            if (languagesList != null) {
                for (int i = 0; i < languagesList.size(); i++) {
                    com.kg.gettransfer.data.model.secondary.Language languagesItem = languagesList.get(i);
                    com.kg.gettransfer.data.model.secondary.Language cachelanguages = (com.kg.gettransfer.data.model.secondary.Language) cache.get(languagesItem);
                    if (cachelanguages != null) {
                        languagesRealmList.add(cachelanguages);
                    } else {
                        languagesRealmList.add(com_kg_gettransfer_data_model_secondary_LanguageRealmProxy.copyOrUpdate(realm, languagesItem, true, cache));
                    }
                }
            }
        }
        com.kg.gettransfer.data.model.secondary.Rating ratingObj = realmObjectSource.realmGet$rating();
        if (ratingObj == null) {
            realmObjectTarget.realmSet$rating(null);
        } else {
            com.kg.gettransfer.data.model.secondary.Rating cacherating = (com.kg.gettransfer.data.model.secondary.Rating) cache.get(ratingObj);
            if (cacherating != null) {
                realmObjectTarget.realmSet$rating(cacherating);
            } else {
                realmObjectTarget.realmSet$rating(com_kg_gettransfer_data_model_secondary_RatingRealmProxy.copyOrUpdate(realm, ratingObj, true, cache));
            }
        }
        realmObjectTarget.realmSet$email(realmObjectSource.realmGet$email());
        realmObjectTarget.realmSet$phone(realmObjectSource.realmGet$phone());
        realmObjectTarget.realmSet$alternatePhone(realmObjectSource.realmGet$alternatePhone());
        return realmObject;
    }

    @Override
    @SuppressWarnings("ArrayToString")
    public String toString() {
        if (!RealmObject.isValid(this)) {
            return "Invalid object";
        }
        StringBuilder stringBuilder = new StringBuilder("Carrier = proxy[");
        stringBuilder.append("{id:");
        stringBuilder.append(realmGet$id());
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{title:");
        stringBuilder.append(realmGet$title() != null ? realmGet$title() : "null");
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{approved:");
        stringBuilder.append(realmGet$approved() != null ? realmGet$approved() : "null");
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{completedTransfers:");
        stringBuilder.append(realmGet$completedTransfers());
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{languages:");
        stringBuilder.append("RealmList<Language>[").append(realmGet$languages().size()).append("]");
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{rating:");
        stringBuilder.append(realmGet$rating() != null ? "Rating" : "null");
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{email:");
        stringBuilder.append(realmGet$email() != null ? realmGet$email() : "null");
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{phone:");
        stringBuilder.append(realmGet$phone() != null ? realmGet$phone() : "null");
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{alternatePhone:");
        stringBuilder.append(realmGet$alternatePhone() != null ? realmGet$alternatePhone() : "null");
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
        com_kg_gettransfer_data_model_CarrierRealmProxy aCarrier = (com_kg_gettransfer_data_model_CarrierRealmProxy)o;

        String path = proxyState.getRealm$realm().getPath();
        String otherPath = aCarrier.proxyState.getRealm$realm().getPath();
        if (path != null ? !path.equals(otherPath) : otherPath != null) return false;

        String tableName = proxyState.getRow$realm().getTable().getName();
        String otherTableName = aCarrier.proxyState.getRow$realm().getTable().getName();
        if (tableName != null ? !tableName.equals(otherTableName) : otherTableName != null) return false;

        if (proxyState.getRow$realm().getIndex() != aCarrier.proxyState.getRow$realm().getIndex()) return false;

        return true;
    }
}
