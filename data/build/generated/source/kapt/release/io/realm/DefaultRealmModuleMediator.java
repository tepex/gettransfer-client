package io.realm;


import android.util.JsonReader;
import io.realm.internal.ColumnInfo;
import io.realm.internal.OsObjectSchemaInfo;
import io.realm.internal.OsSchemaInfo;
import io.realm.internal.RealmObjectProxy;
import io.realm.internal.RealmProxyMediator;
import io.realm.internal.Row;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.json.JSONException;
import org.json.JSONObject;

@io.realm.annotations.RealmModule
class DefaultRealmModuleMediator extends RealmProxyMediator {

    private static final Set<Class<? extends RealmModel>> MODEL_CLASSES;
    static {
        Set<Class<? extends RealmModel>> modelClasses = new HashSet<Class<? extends RealmModel>>(15);
        modelClasses.add(com.kg.gettransfer.data.model.Offer.class);
        modelClasses.add(com.kg.gettransfer.data.model.Config.class);
        modelClasses.add(com.kg.gettransfer.data.model.Transfer.class);
        modelClasses.add(com.kg.gettransfer.data.model.Vehicle.class);
        modelClasses.add(com.kg.gettransfer.data.model.Carrier.class);
        modelClasses.add(com.kg.gettransfer.data.model.AccountInfo.class);
        modelClasses.add(com.kg.gettransfer.data.model.secondary.PriceConverted.class);
        modelClasses.add(com.kg.gettransfer.data.model.secondary.Price.class);
        modelClasses.add(com.kg.gettransfer.data.model.secondary.ZonedDate.class);
        modelClasses.add(com.kg.gettransfer.data.model.secondary.Locale.class);
        modelClasses.add(com.kg.gettransfer.data.model.secondary.Currency.class);
        modelClasses.add(com.kg.gettransfer.data.model.secondary.Location.class);
        modelClasses.add(com.kg.gettransfer.data.model.secondary.Rating.class);
        modelClasses.add(com.kg.gettransfer.data.model.secondary.Language.class);
        modelClasses.add(com.kg.gettransfer.data.model.secondary.TransportType.class);
        MODEL_CLASSES = Collections.unmodifiableSet(modelClasses);
    }

    @Override
    public Map<Class<? extends RealmModel>, OsObjectSchemaInfo> getExpectedObjectSchemaInfoMap() {
        Map<Class<? extends RealmModel>, OsObjectSchemaInfo> infoMap = new HashMap<Class<? extends RealmModel>, OsObjectSchemaInfo>(15);
        infoMap.put(com.kg.gettransfer.data.model.Offer.class, io.realm.com_kg_gettransfer_data_model_OfferRealmProxy.getExpectedObjectSchemaInfo());
        infoMap.put(com.kg.gettransfer.data.model.Config.class, io.realm.com_kg_gettransfer_data_model_ConfigRealmProxy.getExpectedObjectSchemaInfo());
        infoMap.put(com.kg.gettransfer.data.model.Transfer.class, io.realm.com_kg_gettransfer_data_model_TransferRealmProxy.getExpectedObjectSchemaInfo());
        infoMap.put(com.kg.gettransfer.data.model.Vehicle.class, io.realm.com_kg_gettransfer_data_model_VehicleRealmProxy.getExpectedObjectSchemaInfo());
        infoMap.put(com.kg.gettransfer.data.model.Carrier.class, io.realm.com_kg_gettransfer_data_model_CarrierRealmProxy.getExpectedObjectSchemaInfo());
        infoMap.put(com.kg.gettransfer.data.model.AccountInfo.class, io.realm.com_kg_gettransfer_data_model_AccountInfoRealmProxy.getExpectedObjectSchemaInfo());
        infoMap.put(com.kg.gettransfer.data.model.secondary.PriceConverted.class, io.realm.com_kg_gettransfer_data_model_secondary_PriceConvertedRealmProxy.getExpectedObjectSchemaInfo());
        infoMap.put(com.kg.gettransfer.data.model.secondary.Price.class, io.realm.com_kg_gettransfer_data_model_secondary_PriceRealmProxy.getExpectedObjectSchemaInfo());
        infoMap.put(com.kg.gettransfer.data.model.secondary.ZonedDate.class, io.realm.com_kg_gettransfer_data_model_secondary_ZonedDateRealmProxy.getExpectedObjectSchemaInfo());
        infoMap.put(com.kg.gettransfer.data.model.secondary.Locale.class, io.realm.com_kg_gettransfer_data_model_secondary_LocaleRealmProxy.getExpectedObjectSchemaInfo());
        infoMap.put(com.kg.gettransfer.data.model.secondary.Currency.class, io.realm.com_kg_gettransfer_data_model_secondary_CurrencyRealmProxy.getExpectedObjectSchemaInfo());
        infoMap.put(com.kg.gettransfer.data.model.secondary.Location.class, io.realm.com_kg_gettransfer_data_model_secondary_LocationRealmProxy.getExpectedObjectSchemaInfo());
        infoMap.put(com.kg.gettransfer.data.model.secondary.Rating.class, io.realm.com_kg_gettransfer_data_model_secondary_RatingRealmProxy.getExpectedObjectSchemaInfo());
        infoMap.put(com.kg.gettransfer.data.model.secondary.Language.class, io.realm.com_kg_gettransfer_data_model_secondary_LanguageRealmProxy.getExpectedObjectSchemaInfo());
        infoMap.put(com.kg.gettransfer.data.model.secondary.TransportType.class, io.realm.com_kg_gettransfer_data_model_secondary_TransportTypeRealmProxy.getExpectedObjectSchemaInfo());
        return infoMap;
    }

    @Override
    public ColumnInfo createColumnInfo(Class<? extends RealmModel> clazz, OsSchemaInfo schemaInfo) {
        checkClass(clazz);

        if (clazz.equals(com.kg.gettransfer.data.model.Offer.class)) {
            return io.realm.com_kg_gettransfer_data_model_OfferRealmProxy.createColumnInfo(schemaInfo);
        }
        if (clazz.equals(com.kg.gettransfer.data.model.Config.class)) {
            return io.realm.com_kg_gettransfer_data_model_ConfigRealmProxy.createColumnInfo(schemaInfo);
        }
        if (clazz.equals(com.kg.gettransfer.data.model.Transfer.class)) {
            return io.realm.com_kg_gettransfer_data_model_TransferRealmProxy.createColumnInfo(schemaInfo);
        }
        if (clazz.equals(com.kg.gettransfer.data.model.Vehicle.class)) {
            return io.realm.com_kg_gettransfer_data_model_VehicleRealmProxy.createColumnInfo(schemaInfo);
        }
        if (clazz.equals(com.kg.gettransfer.data.model.Carrier.class)) {
            return io.realm.com_kg_gettransfer_data_model_CarrierRealmProxy.createColumnInfo(schemaInfo);
        }
        if (clazz.equals(com.kg.gettransfer.data.model.AccountInfo.class)) {
            return io.realm.com_kg_gettransfer_data_model_AccountInfoRealmProxy.createColumnInfo(schemaInfo);
        }
        if (clazz.equals(com.kg.gettransfer.data.model.secondary.PriceConverted.class)) {
            return io.realm.com_kg_gettransfer_data_model_secondary_PriceConvertedRealmProxy.createColumnInfo(schemaInfo);
        }
        if (clazz.equals(com.kg.gettransfer.data.model.secondary.Price.class)) {
            return io.realm.com_kg_gettransfer_data_model_secondary_PriceRealmProxy.createColumnInfo(schemaInfo);
        }
        if (clazz.equals(com.kg.gettransfer.data.model.secondary.ZonedDate.class)) {
            return io.realm.com_kg_gettransfer_data_model_secondary_ZonedDateRealmProxy.createColumnInfo(schemaInfo);
        }
        if (clazz.equals(com.kg.gettransfer.data.model.secondary.Locale.class)) {
            return io.realm.com_kg_gettransfer_data_model_secondary_LocaleRealmProxy.createColumnInfo(schemaInfo);
        }
        if (clazz.equals(com.kg.gettransfer.data.model.secondary.Currency.class)) {
            return io.realm.com_kg_gettransfer_data_model_secondary_CurrencyRealmProxy.createColumnInfo(schemaInfo);
        }
        if (clazz.equals(com.kg.gettransfer.data.model.secondary.Location.class)) {
            return io.realm.com_kg_gettransfer_data_model_secondary_LocationRealmProxy.createColumnInfo(schemaInfo);
        }
        if (clazz.equals(com.kg.gettransfer.data.model.secondary.Rating.class)) {
            return io.realm.com_kg_gettransfer_data_model_secondary_RatingRealmProxy.createColumnInfo(schemaInfo);
        }
        if (clazz.equals(com.kg.gettransfer.data.model.secondary.Language.class)) {
            return io.realm.com_kg_gettransfer_data_model_secondary_LanguageRealmProxy.createColumnInfo(schemaInfo);
        }
        if (clazz.equals(com.kg.gettransfer.data.model.secondary.TransportType.class)) {
            return io.realm.com_kg_gettransfer_data_model_secondary_TransportTypeRealmProxy.createColumnInfo(schemaInfo);
        }
        throw getMissingProxyClassException(clazz);
    }

    @Override
    public String getSimpleClassNameImpl(Class<? extends RealmModel> clazz) {
        checkClass(clazz);

        if (clazz.equals(com.kg.gettransfer.data.model.Offer.class)) {
            return "Offer";
        }
        if (clazz.equals(com.kg.gettransfer.data.model.Config.class)) {
            return "Config";
        }
        if (clazz.equals(com.kg.gettransfer.data.model.Transfer.class)) {
            return "Transfer";
        }
        if (clazz.equals(com.kg.gettransfer.data.model.Vehicle.class)) {
            return "Vehicle";
        }
        if (clazz.equals(com.kg.gettransfer.data.model.Carrier.class)) {
            return "Carrier";
        }
        if (clazz.equals(com.kg.gettransfer.data.model.AccountInfo.class)) {
            return "AccountInfo";
        }
        if (clazz.equals(com.kg.gettransfer.data.model.secondary.PriceConverted.class)) {
            return "PriceConverted";
        }
        if (clazz.equals(com.kg.gettransfer.data.model.secondary.Price.class)) {
            return "Price";
        }
        if (clazz.equals(com.kg.gettransfer.data.model.secondary.ZonedDate.class)) {
            return "ZonedDate";
        }
        if (clazz.equals(com.kg.gettransfer.data.model.secondary.Locale.class)) {
            return "Locale";
        }
        if (clazz.equals(com.kg.gettransfer.data.model.secondary.Currency.class)) {
            return "Currency";
        }
        if (clazz.equals(com.kg.gettransfer.data.model.secondary.Location.class)) {
            return "Location";
        }
        if (clazz.equals(com.kg.gettransfer.data.model.secondary.Rating.class)) {
            return "Rating";
        }
        if (clazz.equals(com.kg.gettransfer.data.model.secondary.Language.class)) {
            return "Language";
        }
        if (clazz.equals(com.kg.gettransfer.data.model.secondary.TransportType.class)) {
            return "TransportType";
        }
        throw getMissingProxyClassException(clazz);
    }

    @Override
    public <E extends RealmModel> E newInstance(Class<E> clazz, Object baseRealm, Row row, ColumnInfo columnInfo, boolean acceptDefaultValue, List<String> excludeFields) {
        final BaseRealm.RealmObjectContext objectContext = BaseRealm.objectContext.get();
        try {
            objectContext.set((BaseRealm) baseRealm, row, columnInfo, acceptDefaultValue, excludeFields);
            checkClass(clazz);

            if (clazz.equals(com.kg.gettransfer.data.model.Offer.class)) {
                return clazz.cast(new io.realm.com_kg_gettransfer_data_model_OfferRealmProxy());
            }
            if (clazz.equals(com.kg.gettransfer.data.model.Config.class)) {
                return clazz.cast(new io.realm.com_kg_gettransfer_data_model_ConfigRealmProxy());
            }
            if (clazz.equals(com.kg.gettransfer.data.model.Transfer.class)) {
                return clazz.cast(new io.realm.com_kg_gettransfer_data_model_TransferRealmProxy());
            }
            if (clazz.equals(com.kg.gettransfer.data.model.Vehicle.class)) {
                return clazz.cast(new io.realm.com_kg_gettransfer_data_model_VehicleRealmProxy());
            }
            if (clazz.equals(com.kg.gettransfer.data.model.Carrier.class)) {
                return clazz.cast(new io.realm.com_kg_gettransfer_data_model_CarrierRealmProxy());
            }
            if (clazz.equals(com.kg.gettransfer.data.model.AccountInfo.class)) {
                return clazz.cast(new io.realm.com_kg_gettransfer_data_model_AccountInfoRealmProxy());
            }
            if (clazz.equals(com.kg.gettransfer.data.model.secondary.PriceConverted.class)) {
                return clazz.cast(new io.realm.com_kg_gettransfer_data_model_secondary_PriceConvertedRealmProxy());
            }
            if (clazz.equals(com.kg.gettransfer.data.model.secondary.Price.class)) {
                return clazz.cast(new io.realm.com_kg_gettransfer_data_model_secondary_PriceRealmProxy());
            }
            if (clazz.equals(com.kg.gettransfer.data.model.secondary.ZonedDate.class)) {
                return clazz.cast(new io.realm.com_kg_gettransfer_data_model_secondary_ZonedDateRealmProxy());
            }
            if (clazz.equals(com.kg.gettransfer.data.model.secondary.Locale.class)) {
                return clazz.cast(new io.realm.com_kg_gettransfer_data_model_secondary_LocaleRealmProxy());
            }
            if (clazz.equals(com.kg.gettransfer.data.model.secondary.Currency.class)) {
                return clazz.cast(new io.realm.com_kg_gettransfer_data_model_secondary_CurrencyRealmProxy());
            }
            if (clazz.equals(com.kg.gettransfer.data.model.secondary.Location.class)) {
                return clazz.cast(new io.realm.com_kg_gettransfer_data_model_secondary_LocationRealmProxy());
            }
            if (clazz.equals(com.kg.gettransfer.data.model.secondary.Rating.class)) {
                return clazz.cast(new io.realm.com_kg_gettransfer_data_model_secondary_RatingRealmProxy());
            }
            if (clazz.equals(com.kg.gettransfer.data.model.secondary.Language.class)) {
                return clazz.cast(new io.realm.com_kg_gettransfer_data_model_secondary_LanguageRealmProxy());
            }
            if (clazz.equals(com.kg.gettransfer.data.model.secondary.TransportType.class)) {
                return clazz.cast(new io.realm.com_kg_gettransfer_data_model_secondary_TransportTypeRealmProxy());
            }
            throw getMissingProxyClassException(clazz);
        } finally {
            objectContext.clear();
        }
    }

    @Override
    public Set<Class<? extends RealmModel>> getModelClasses() {
        return MODEL_CLASSES;
    }

    @Override
    public <E extends RealmModel> E copyOrUpdate(Realm realm, E obj, boolean update, Map<RealmModel, RealmObjectProxy> cache) {
        // This cast is correct because obj is either
        // generated by RealmProxy or the original type extending directly from RealmObject
        @SuppressWarnings("unchecked") Class<E> clazz = (Class<E>) ((obj instanceof RealmObjectProxy) ? obj.getClass().getSuperclass() : obj.getClass());

        if (clazz.equals(com.kg.gettransfer.data.model.Offer.class)) {
            return clazz.cast(io.realm.com_kg_gettransfer_data_model_OfferRealmProxy.copyOrUpdate(realm, (com.kg.gettransfer.data.model.Offer) obj, update, cache));
        }
        if (clazz.equals(com.kg.gettransfer.data.model.Config.class)) {
            return clazz.cast(io.realm.com_kg_gettransfer_data_model_ConfigRealmProxy.copyOrUpdate(realm, (com.kg.gettransfer.data.model.Config) obj, update, cache));
        }
        if (clazz.equals(com.kg.gettransfer.data.model.Transfer.class)) {
            return clazz.cast(io.realm.com_kg_gettransfer_data_model_TransferRealmProxy.copyOrUpdate(realm, (com.kg.gettransfer.data.model.Transfer) obj, update, cache));
        }
        if (clazz.equals(com.kg.gettransfer.data.model.Vehicle.class)) {
            return clazz.cast(io.realm.com_kg_gettransfer_data_model_VehicleRealmProxy.copyOrUpdate(realm, (com.kg.gettransfer.data.model.Vehicle) obj, update, cache));
        }
        if (clazz.equals(com.kg.gettransfer.data.model.Carrier.class)) {
            return clazz.cast(io.realm.com_kg_gettransfer_data_model_CarrierRealmProxy.copyOrUpdate(realm, (com.kg.gettransfer.data.model.Carrier) obj, update, cache));
        }
        if (clazz.equals(com.kg.gettransfer.data.model.AccountInfo.class)) {
            return clazz.cast(io.realm.com_kg_gettransfer_data_model_AccountInfoRealmProxy.copyOrUpdate(realm, (com.kg.gettransfer.data.model.AccountInfo) obj, update, cache));
        }
        if (clazz.equals(com.kg.gettransfer.data.model.secondary.PriceConverted.class)) {
            return clazz.cast(io.realm.com_kg_gettransfer_data_model_secondary_PriceConvertedRealmProxy.copyOrUpdate(realm, (com.kg.gettransfer.data.model.secondary.PriceConverted) obj, update, cache));
        }
        if (clazz.equals(com.kg.gettransfer.data.model.secondary.Price.class)) {
            return clazz.cast(io.realm.com_kg_gettransfer_data_model_secondary_PriceRealmProxy.copyOrUpdate(realm, (com.kg.gettransfer.data.model.secondary.Price) obj, update, cache));
        }
        if (clazz.equals(com.kg.gettransfer.data.model.secondary.ZonedDate.class)) {
            return clazz.cast(io.realm.com_kg_gettransfer_data_model_secondary_ZonedDateRealmProxy.copyOrUpdate(realm, (com.kg.gettransfer.data.model.secondary.ZonedDate) obj, update, cache));
        }
        if (clazz.equals(com.kg.gettransfer.data.model.secondary.Locale.class)) {
            return clazz.cast(io.realm.com_kg_gettransfer_data_model_secondary_LocaleRealmProxy.copyOrUpdate(realm, (com.kg.gettransfer.data.model.secondary.Locale) obj, update, cache));
        }
        if (clazz.equals(com.kg.gettransfer.data.model.secondary.Currency.class)) {
            return clazz.cast(io.realm.com_kg_gettransfer_data_model_secondary_CurrencyRealmProxy.copyOrUpdate(realm, (com.kg.gettransfer.data.model.secondary.Currency) obj, update, cache));
        }
        if (clazz.equals(com.kg.gettransfer.data.model.secondary.Location.class)) {
            return clazz.cast(io.realm.com_kg_gettransfer_data_model_secondary_LocationRealmProxy.copyOrUpdate(realm, (com.kg.gettransfer.data.model.secondary.Location) obj, update, cache));
        }
        if (clazz.equals(com.kg.gettransfer.data.model.secondary.Rating.class)) {
            return clazz.cast(io.realm.com_kg_gettransfer_data_model_secondary_RatingRealmProxy.copyOrUpdate(realm, (com.kg.gettransfer.data.model.secondary.Rating) obj, update, cache));
        }
        if (clazz.equals(com.kg.gettransfer.data.model.secondary.Language.class)) {
            return clazz.cast(io.realm.com_kg_gettransfer_data_model_secondary_LanguageRealmProxy.copyOrUpdate(realm, (com.kg.gettransfer.data.model.secondary.Language) obj, update, cache));
        }
        if (clazz.equals(com.kg.gettransfer.data.model.secondary.TransportType.class)) {
            return clazz.cast(io.realm.com_kg_gettransfer_data_model_secondary_TransportTypeRealmProxy.copyOrUpdate(realm, (com.kg.gettransfer.data.model.secondary.TransportType) obj, update, cache));
        }
        throw getMissingProxyClassException(clazz);
    }

    @Override
    public void insert(Realm realm, RealmModel object, Map<RealmModel, Long> cache) {
        // This cast is correct because obj is either
        // generated by RealmProxy or the original type extending directly from RealmObject
        @SuppressWarnings("unchecked") Class<RealmModel> clazz = (Class<RealmModel>) ((object instanceof RealmObjectProxy) ? object.getClass().getSuperclass() : object.getClass());

        if (clazz.equals(com.kg.gettransfer.data.model.Offer.class)) {
            io.realm.com_kg_gettransfer_data_model_OfferRealmProxy.insert(realm, (com.kg.gettransfer.data.model.Offer) object, cache);
        } else if (clazz.equals(com.kg.gettransfer.data.model.Config.class)) {
            io.realm.com_kg_gettransfer_data_model_ConfigRealmProxy.insert(realm, (com.kg.gettransfer.data.model.Config) object, cache);
        } else if (clazz.equals(com.kg.gettransfer.data.model.Transfer.class)) {
            io.realm.com_kg_gettransfer_data_model_TransferRealmProxy.insert(realm, (com.kg.gettransfer.data.model.Transfer) object, cache);
        } else if (clazz.equals(com.kg.gettransfer.data.model.Vehicle.class)) {
            io.realm.com_kg_gettransfer_data_model_VehicleRealmProxy.insert(realm, (com.kg.gettransfer.data.model.Vehicle) object, cache);
        } else if (clazz.equals(com.kg.gettransfer.data.model.Carrier.class)) {
            io.realm.com_kg_gettransfer_data_model_CarrierRealmProxy.insert(realm, (com.kg.gettransfer.data.model.Carrier) object, cache);
        } else if (clazz.equals(com.kg.gettransfer.data.model.AccountInfo.class)) {
            io.realm.com_kg_gettransfer_data_model_AccountInfoRealmProxy.insert(realm, (com.kg.gettransfer.data.model.AccountInfo) object, cache);
        } else if (clazz.equals(com.kg.gettransfer.data.model.secondary.PriceConverted.class)) {
            io.realm.com_kg_gettransfer_data_model_secondary_PriceConvertedRealmProxy.insert(realm, (com.kg.gettransfer.data.model.secondary.PriceConverted) object, cache);
        } else if (clazz.equals(com.kg.gettransfer.data.model.secondary.Price.class)) {
            io.realm.com_kg_gettransfer_data_model_secondary_PriceRealmProxy.insert(realm, (com.kg.gettransfer.data.model.secondary.Price) object, cache);
        } else if (clazz.equals(com.kg.gettransfer.data.model.secondary.ZonedDate.class)) {
            io.realm.com_kg_gettransfer_data_model_secondary_ZonedDateRealmProxy.insert(realm, (com.kg.gettransfer.data.model.secondary.ZonedDate) object, cache);
        } else if (clazz.equals(com.kg.gettransfer.data.model.secondary.Locale.class)) {
            io.realm.com_kg_gettransfer_data_model_secondary_LocaleRealmProxy.insert(realm, (com.kg.gettransfer.data.model.secondary.Locale) object, cache);
        } else if (clazz.equals(com.kg.gettransfer.data.model.secondary.Currency.class)) {
            io.realm.com_kg_gettransfer_data_model_secondary_CurrencyRealmProxy.insert(realm, (com.kg.gettransfer.data.model.secondary.Currency) object, cache);
        } else if (clazz.equals(com.kg.gettransfer.data.model.secondary.Location.class)) {
            io.realm.com_kg_gettransfer_data_model_secondary_LocationRealmProxy.insert(realm, (com.kg.gettransfer.data.model.secondary.Location) object, cache);
        } else if (clazz.equals(com.kg.gettransfer.data.model.secondary.Rating.class)) {
            io.realm.com_kg_gettransfer_data_model_secondary_RatingRealmProxy.insert(realm, (com.kg.gettransfer.data.model.secondary.Rating) object, cache);
        } else if (clazz.equals(com.kg.gettransfer.data.model.secondary.Language.class)) {
            io.realm.com_kg_gettransfer_data_model_secondary_LanguageRealmProxy.insert(realm, (com.kg.gettransfer.data.model.secondary.Language) object, cache);
        } else if (clazz.equals(com.kg.gettransfer.data.model.secondary.TransportType.class)) {
            io.realm.com_kg_gettransfer_data_model_secondary_TransportTypeRealmProxy.insert(realm, (com.kg.gettransfer.data.model.secondary.TransportType) object, cache);
        } else {
            throw getMissingProxyClassException(clazz);
        }
    }

    @Override
    public void insert(Realm realm, Collection<? extends RealmModel> objects) {
        Iterator<? extends RealmModel> iterator = objects.iterator();
        RealmModel object = null;
        Map<RealmModel, Long> cache = new HashMap<RealmModel, Long>(objects.size());
        if (iterator.hasNext()) {
            //  access the first element to figure out the clazz for the routing below
            object = iterator.next();
            // This cast is correct because obj is either
            // generated by RealmProxy or the original type extending directly from RealmObject
            @SuppressWarnings("unchecked") Class<RealmModel> clazz = (Class<RealmModel>) ((object instanceof RealmObjectProxy) ? object.getClass().getSuperclass() : object.getClass());

            if (clazz.equals(com.kg.gettransfer.data.model.Offer.class)) {
                io.realm.com_kg_gettransfer_data_model_OfferRealmProxy.insert(realm, (com.kg.gettransfer.data.model.Offer) object, cache);
            } else if (clazz.equals(com.kg.gettransfer.data.model.Config.class)) {
                io.realm.com_kg_gettransfer_data_model_ConfigRealmProxy.insert(realm, (com.kg.gettransfer.data.model.Config) object, cache);
            } else if (clazz.equals(com.kg.gettransfer.data.model.Transfer.class)) {
                io.realm.com_kg_gettransfer_data_model_TransferRealmProxy.insert(realm, (com.kg.gettransfer.data.model.Transfer) object, cache);
            } else if (clazz.equals(com.kg.gettransfer.data.model.Vehicle.class)) {
                io.realm.com_kg_gettransfer_data_model_VehicleRealmProxy.insert(realm, (com.kg.gettransfer.data.model.Vehicle) object, cache);
            } else if (clazz.equals(com.kg.gettransfer.data.model.Carrier.class)) {
                io.realm.com_kg_gettransfer_data_model_CarrierRealmProxy.insert(realm, (com.kg.gettransfer.data.model.Carrier) object, cache);
            } else if (clazz.equals(com.kg.gettransfer.data.model.AccountInfo.class)) {
                io.realm.com_kg_gettransfer_data_model_AccountInfoRealmProxy.insert(realm, (com.kg.gettransfer.data.model.AccountInfo) object, cache);
            } else if (clazz.equals(com.kg.gettransfer.data.model.secondary.PriceConverted.class)) {
                io.realm.com_kg_gettransfer_data_model_secondary_PriceConvertedRealmProxy.insert(realm, (com.kg.gettransfer.data.model.secondary.PriceConverted) object, cache);
            } else if (clazz.equals(com.kg.gettransfer.data.model.secondary.Price.class)) {
                io.realm.com_kg_gettransfer_data_model_secondary_PriceRealmProxy.insert(realm, (com.kg.gettransfer.data.model.secondary.Price) object, cache);
            } else if (clazz.equals(com.kg.gettransfer.data.model.secondary.ZonedDate.class)) {
                io.realm.com_kg_gettransfer_data_model_secondary_ZonedDateRealmProxy.insert(realm, (com.kg.gettransfer.data.model.secondary.ZonedDate) object, cache);
            } else if (clazz.equals(com.kg.gettransfer.data.model.secondary.Locale.class)) {
                io.realm.com_kg_gettransfer_data_model_secondary_LocaleRealmProxy.insert(realm, (com.kg.gettransfer.data.model.secondary.Locale) object, cache);
            } else if (clazz.equals(com.kg.gettransfer.data.model.secondary.Currency.class)) {
                io.realm.com_kg_gettransfer_data_model_secondary_CurrencyRealmProxy.insert(realm, (com.kg.gettransfer.data.model.secondary.Currency) object, cache);
            } else if (clazz.equals(com.kg.gettransfer.data.model.secondary.Location.class)) {
                io.realm.com_kg_gettransfer_data_model_secondary_LocationRealmProxy.insert(realm, (com.kg.gettransfer.data.model.secondary.Location) object, cache);
            } else if (clazz.equals(com.kg.gettransfer.data.model.secondary.Rating.class)) {
                io.realm.com_kg_gettransfer_data_model_secondary_RatingRealmProxy.insert(realm, (com.kg.gettransfer.data.model.secondary.Rating) object, cache);
            } else if (clazz.equals(com.kg.gettransfer.data.model.secondary.Language.class)) {
                io.realm.com_kg_gettransfer_data_model_secondary_LanguageRealmProxy.insert(realm, (com.kg.gettransfer.data.model.secondary.Language) object, cache);
            } else if (clazz.equals(com.kg.gettransfer.data.model.secondary.TransportType.class)) {
                io.realm.com_kg_gettransfer_data_model_secondary_TransportTypeRealmProxy.insert(realm, (com.kg.gettransfer.data.model.secondary.TransportType) object, cache);
            } else {
                throw getMissingProxyClassException(clazz);
            }
            if (iterator.hasNext()) {
                if (clazz.equals(com.kg.gettransfer.data.model.Offer.class)) {
                    io.realm.com_kg_gettransfer_data_model_OfferRealmProxy.insert(realm, iterator, cache);
                } else if (clazz.equals(com.kg.gettransfer.data.model.Config.class)) {
                    io.realm.com_kg_gettransfer_data_model_ConfigRealmProxy.insert(realm, iterator, cache);
                } else if (clazz.equals(com.kg.gettransfer.data.model.Transfer.class)) {
                    io.realm.com_kg_gettransfer_data_model_TransferRealmProxy.insert(realm, iterator, cache);
                } else if (clazz.equals(com.kg.gettransfer.data.model.Vehicle.class)) {
                    io.realm.com_kg_gettransfer_data_model_VehicleRealmProxy.insert(realm, iterator, cache);
                } else if (clazz.equals(com.kg.gettransfer.data.model.Carrier.class)) {
                    io.realm.com_kg_gettransfer_data_model_CarrierRealmProxy.insert(realm, iterator, cache);
                } else if (clazz.equals(com.kg.gettransfer.data.model.AccountInfo.class)) {
                    io.realm.com_kg_gettransfer_data_model_AccountInfoRealmProxy.insert(realm, iterator, cache);
                } else if (clazz.equals(com.kg.gettransfer.data.model.secondary.PriceConverted.class)) {
                    io.realm.com_kg_gettransfer_data_model_secondary_PriceConvertedRealmProxy.insert(realm, iterator, cache);
                } else if (clazz.equals(com.kg.gettransfer.data.model.secondary.Price.class)) {
                    io.realm.com_kg_gettransfer_data_model_secondary_PriceRealmProxy.insert(realm, iterator, cache);
                } else if (clazz.equals(com.kg.gettransfer.data.model.secondary.ZonedDate.class)) {
                    io.realm.com_kg_gettransfer_data_model_secondary_ZonedDateRealmProxy.insert(realm, iterator, cache);
                } else if (clazz.equals(com.kg.gettransfer.data.model.secondary.Locale.class)) {
                    io.realm.com_kg_gettransfer_data_model_secondary_LocaleRealmProxy.insert(realm, iterator, cache);
                } else if (clazz.equals(com.kg.gettransfer.data.model.secondary.Currency.class)) {
                    io.realm.com_kg_gettransfer_data_model_secondary_CurrencyRealmProxy.insert(realm, iterator, cache);
                } else if (clazz.equals(com.kg.gettransfer.data.model.secondary.Location.class)) {
                    io.realm.com_kg_gettransfer_data_model_secondary_LocationRealmProxy.insert(realm, iterator, cache);
                } else if (clazz.equals(com.kg.gettransfer.data.model.secondary.Rating.class)) {
                    io.realm.com_kg_gettransfer_data_model_secondary_RatingRealmProxy.insert(realm, iterator, cache);
                } else if (clazz.equals(com.kg.gettransfer.data.model.secondary.Language.class)) {
                    io.realm.com_kg_gettransfer_data_model_secondary_LanguageRealmProxy.insert(realm, iterator, cache);
                } else if (clazz.equals(com.kg.gettransfer.data.model.secondary.TransportType.class)) {
                    io.realm.com_kg_gettransfer_data_model_secondary_TransportTypeRealmProxy.insert(realm, iterator, cache);
                } else {
                    throw getMissingProxyClassException(clazz);
                }
            }
        }
    }

    @Override
    public void insertOrUpdate(Realm realm, RealmModel obj, Map<RealmModel, Long> cache) {
        // This cast is correct because obj is either
        // generated by RealmProxy or the original type extending directly from RealmObject
        @SuppressWarnings("unchecked") Class<RealmModel> clazz = (Class<RealmModel>) ((obj instanceof RealmObjectProxy) ? obj.getClass().getSuperclass() : obj.getClass());

        if (clazz.equals(com.kg.gettransfer.data.model.Offer.class)) {
            io.realm.com_kg_gettransfer_data_model_OfferRealmProxy.insertOrUpdate(realm, (com.kg.gettransfer.data.model.Offer) obj, cache);
        } else if (clazz.equals(com.kg.gettransfer.data.model.Config.class)) {
            io.realm.com_kg_gettransfer_data_model_ConfigRealmProxy.insertOrUpdate(realm, (com.kg.gettransfer.data.model.Config) obj, cache);
        } else if (clazz.equals(com.kg.gettransfer.data.model.Transfer.class)) {
            io.realm.com_kg_gettransfer_data_model_TransferRealmProxy.insertOrUpdate(realm, (com.kg.gettransfer.data.model.Transfer) obj, cache);
        } else if (clazz.equals(com.kg.gettransfer.data.model.Vehicle.class)) {
            io.realm.com_kg_gettransfer_data_model_VehicleRealmProxy.insertOrUpdate(realm, (com.kg.gettransfer.data.model.Vehicle) obj, cache);
        } else if (clazz.equals(com.kg.gettransfer.data.model.Carrier.class)) {
            io.realm.com_kg_gettransfer_data_model_CarrierRealmProxy.insertOrUpdate(realm, (com.kg.gettransfer.data.model.Carrier) obj, cache);
        } else if (clazz.equals(com.kg.gettransfer.data.model.AccountInfo.class)) {
            io.realm.com_kg_gettransfer_data_model_AccountInfoRealmProxy.insertOrUpdate(realm, (com.kg.gettransfer.data.model.AccountInfo) obj, cache);
        } else if (clazz.equals(com.kg.gettransfer.data.model.secondary.PriceConverted.class)) {
            io.realm.com_kg_gettransfer_data_model_secondary_PriceConvertedRealmProxy.insertOrUpdate(realm, (com.kg.gettransfer.data.model.secondary.PriceConverted) obj, cache);
        } else if (clazz.equals(com.kg.gettransfer.data.model.secondary.Price.class)) {
            io.realm.com_kg_gettransfer_data_model_secondary_PriceRealmProxy.insertOrUpdate(realm, (com.kg.gettransfer.data.model.secondary.Price) obj, cache);
        } else if (clazz.equals(com.kg.gettransfer.data.model.secondary.ZonedDate.class)) {
            io.realm.com_kg_gettransfer_data_model_secondary_ZonedDateRealmProxy.insertOrUpdate(realm, (com.kg.gettransfer.data.model.secondary.ZonedDate) obj, cache);
        } else if (clazz.equals(com.kg.gettransfer.data.model.secondary.Locale.class)) {
            io.realm.com_kg_gettransfer_data_model_secondary_LocaleRealmProxy.insertOrUpdate(realm, (com.kg.gettransfer.data.model.secondary.Locale) obj, cache);
        } else if (clazz.equals(com.kg.gettransfer.data.model.secondary.Currency.class)) {
            io.realm.com_kg_gettransfer_data_model_secondary_CurrencyRealmProxy.insertOrUpdate(realm, (com.kg.gettransfer.data.model.secondary.Currency) obj, cache);
        } else if (clazz.equals(com.kg.gettransfer.data.model.secondary.Location.class)) {
            io.realm.com_kg_gettransfer_data_model_secondary_LocationRealmProxy.insertOrUpdate(realm, (com.kg.gettransfer.data.model.secondary.Location) obj, cache);
        } else if (clazz.equals(com.kg.gettransfer.data.model.secondary.Rating.class)) {
            io.realm.com_kg_gettransfer_data_model_secondary_RatingRealmProxy.insertOrUpdate(realm, (com.kg.gettransfer.data.model.secondary.Rating) obj, cache);
        } else if (clazz.equals(com.kg.gettransfer.data.model.secondary.Language.class)) {
            io.realm.com_kg_gettransfer_data_model_secondary_LanguageRealmProxy.insertOrUpdate(realm, (com.kg.gettransfer.data.model.secondary.Language) obj, cache);
        } else if (clazz.equals(com.kg.gettransfer.data.model.secondary.TransportType.class)) {
            io.realm.com_kg_gettransfer_data_model_secondary_TransportTypeRealmProxy.insertOrUpdate(realm, (com.kg.gettransfer.data.model.secondary.TransportType) obj, cache);
        } else {
            throw getMissingProxyClassException(clazz);
        }
    }

    @Override
    public void insertOrUpdate(Realm realm, Collection<? extends RealmModel> objects) {
        Iterator<? extends RealmModel> iterator = objects.iterator();
        RealmModel object = null;
        Map<RealmModel, Long> cache = new HashMap<RealmModel, Long>(objects.size());
        if (iterator.hasNext()) {
            //  access the first element to figure out the clazz for the routing below
            object = iterator.next();
            // This cast is correct because obj is either
            // generated by RealmProxy or the original type extending directly from RealmObject
            @SuppressWarnings("unchecked") Class<RealmModel> clazz = (Class<RealmModel>) ((object instanceof RealmObjectProxy) ? object.getClass().getSuperclass() : object.getClass());

            if (clazz.equals(com.kg.gettransfer.data.model.Offer.class)) {
                io.realm.com_kg_gettransfer_data_model_OfferRealmProxy.insertOrUpdate(realm, (com.kg.gettransfer.data.model.Offer) object, cache);
            } else if (clazz.equals(com.kg.gettransfer.data.model.Config.class)) {
                io.realm.com_kg_gettransfer_data_model_ConfigRealmProxy.insertOrUpdate(realm, (com.kg.gettransfer.data.model.Config) object, cache);
            } else if (clazz.equals(com.kg.gettransfer.data.model.Transfer.class)) {
                io.realm.com_kg_gettransfer_data_model_TransferRealmProxy.insertOrUpdate(realm, (com.kg.gettransfer.data.model.Transfer) object, cache);
            } else if (clazz.equals(com.kg.gettransfer.data.model.Vehicle.class)) {
                io.realm.com_kg_gettransfer_data_model_VehicleRealmProxy.insertOrUpdate(realm, (com.kg.gettransfer.data.model.Vehicle) object, cache);
            } else if (clazz.equals(com.kg.gettransfer.data.model.Carrier.class)) {
                io.realm.com_kg_gettransfer_data_model_CarrierRealmProxy.insertOrUpdate(realm, (com.kg.gettransfer.data.model.Carrier) object, cache);
            } else if (clazz.equals(com.kg.gettransfer.data.model.AccountInfo.class)) {
                io.realm.com_kg_gettransfer_data_model_AccountInfoRealmProxy.insertOrUpdate(realm, (com.kg.gettransfer.data.model.AccountInfo) object, cache);
            } else if (clazz.equals(com.kg.gettransfer.data.model.secondary.PriceConverted.class)) {
                io.realm.com_kg_gettransfer_data_model_secondary_PriceConvertedRealmProxy.insertOrUpdate(realm, (com.kg.gettransfer.data.model.secondary.PriceConverted) object, cache);
            } else if (clazz.equals(com.kg.gettransfer.data.model.secondary.Price.class)) {
                io.realm.com_kg_gettransfer_data_model_secondary_PriceRealmProxy.insertOrUpdate(realm, (com.kg.gettransfer.data.model.secondary.Price) object, cache);
            } else if (clazz.equals(com.kg.gettransfer.data.model.secondary.ZonedDate.class)) {
                io.realm.com_kg_gettransfer_data_model_secondary_ZonedDateRealmProxy.insertOrUpdate(realm, (com.kg.gettransfer.data.model.secondary.ZonedDate) object, cache);
            } else if (clazz.equals(com.kg.gettransfer.data.model.secondary.Locale.class)) {
                io.realm.com_kg_gettransfer_data_model_secondary_LocaleRealmProxy.insertOrUpdate(realm, (com.kg.gettransfer.data.model.secondary.Locale) object, cache);
            } else if (clazz.equals(com.kg.gettransfer.data.model.secondary.Currency.class)) {
                io.realm.com_kg_gettransfer_data_model_secondary_CurrencyRealmProxy.insertOrUpdate(realm, (com.kg.gettransfer.data.model.secondary.Currency) object, cache);
            } else if (clazz.equals(com.kg.gettransfer.data.model.secondary.Location.class)) {
                io.realm.com_kg_gettransfer_data_model_secondary_LocationRealmProxy.insertOrUpdate(realm, (com.kg.gettransfer.data.model.secondary.Location) object, cache);
            } else if (clazz.equals(com.kg.gettransfer.data.model.secondary.Rating.class)) {
                io.realm.com_kg_gettransfer_data_model_secondary_RatingRealmProxy.insertOrUpdate(realm, (com.kg.gettransfer.data.model.secondary.Rating) object, cache);
            } else if (clazz.equals(com.kg.gettransfer.data.model.secondary.Language.class)) {
                io.realm.com_kg_gettransfer_data_model_secondary_LanguageRealmProxy.insertOrUpdate(realm, (com.kg.gettransfer.data.model.secondary.Language) object, cache);
            } else if (clazz.equals(com.kg.gettransfer.data.model.secondary.TransportType.class)) {
                io.realm.com_kg_gettransfer_data_model_secondary_TransportTypeRealmProxy.insertOrUpdate(realm, (com.kg.gettransfer.data.model.secondary.TransportType) object, cache);
            } else {
                throw getMissingProxyClassException(clazz);
            }
            if (iterator.hasNext()) {
                if (clazz.equals(com.kg.gettransfer.data.model.Offer.class)) {
                    io.realm.com_kg_gettransfer_data_model_OfferRealmProxy.insertOrUpdate(realm, iterator, cache);
                } else if (clazz.equals(com.kg.gettransfer.data.model.Config.class)) {
                    io.realm.com_kg_gettransfer_data_model_ConfigRealmProxy.insertOrUpdate(realm, iterator, cache);
                } else if (clazz.equals(com.kg.gettransfer.data.model.Transfer.class)) {
                    io.realm.com_kg_gettransfer_data_model_TransferRealmProxy.insertOrUpdate(realm, iterator, cache);
                } else if (clazz.equals(com.kg.gettransfer.data.model.Vehicle.class)) {
                    io.realm.com_kg_gettransfer_data_model_VehicleRealmProxy.insertOrUpdate(realm, iterator, cache);
                } else if (clazz.equals(com.kg.gettransfer.data.model.Carrier.class)) {
                    io.realm.com_kg_gettransfer_data_model_CarrierRealmProxy.insertOrUpdate(realm, iterator, cache);
                } else if (clazz.equals(com.kg.gettransfer.data.model.AccountInfo.class)) {
                    io.realm.com_kg_gettransfer_data_model_AccountInfoRealmProxy.insertOrUpdate(realm, iterator, cache);
                } else if (clazz.equals(com.kg.gettransfer.data.model.secondary.PriceConverted.class)) {
                    io.realm.com_kg_gettransfer_data_model_secondary_PriceConvertedRealmProxy.insertOrUpdate(realm, iterator, cache);
                } else if (clazz.equals(com.kg.gettransfer.data.model.secondary.Price.class)) {
                    io.realm.com_kg_gettransfer_data_model_secondary_PriceRealmProxy.insertOrUpdate(realm, iterator, cache);
                } else if (clazz.equals(com.kg.gettransfer.data.model.secondary.ZonedDate.class)) {
                    io.realm.com_kg_gettransfer_data_model_secondary_ZonedDateRealmProxy.insertOrUpdate(realm, iterator, cache);
                } else if (clazz.equals(com.kg.gettransfer.data.model.secondary.Locale.class)) {
                    io.realm.com_kg_gettransfer_data_model_secondary_LocaleRealmProxy.insertOrUpdate(realm, iterator, cache);
                } else if (clazz.equals(com.kg.gettransfer.data.model.secondary.Currency.class)) {
                    io.realm.com_kg_gettransfer_data_model_secondary_CurrencyRealmProxy.insertOrUpdate(realm, iterator, cache);
                } else if (clazz.equals(com.kg.gettransfer.data.model.secondary.Location.class)) {
                    io.realm.com_kg_gettransfer_data_model_secondary_LocationRealmProxy.insertOrUpdate(realm, iterator, cache);
                } else if (clazz.equals(com.kg.gettransfer.data.model.secondary.Rating.class)) {
                    io.realm.com_kg_gettransfer_data_model_secondary_RatingRealmProxy.insertOrUpdate(realm, iterator, cache);
                } else if (clazz.equals(com.kg.gettransfer.data.model.secondary.Language.class)) {
                    io.realm.com_kg_gettransfer_data_model_secondary_LanguageRealmProxy.insertOrUpdate(realm, iterator, cache);
                } else if (clazz.equals(com.kg.gettransfer.data.model.secondary.TransportType.class)) {
                    io.realm.com_kg_gettransfer_data_model_secondary_TransportTypeRealmProxy.insertOrUpdate(realm, iterator, cache);
                } else {
                    throw getMissingProxyClassException(clazz);
                }
            }
        }
    }

    @Override
    public <E extends RealmModel> E createOrUpdateUsingJsonObject(Class<E> clazz, Realm realm, JSONObject json, boolean update)
        throws JSONException {
        checkClass(clazz);

        if (clazz.equals(com.kg.gettransfer.data.model.Offer.class)) {
            return clazz.cast(io.realm.com_kg_gettransfer_data_model_OfferRealmProxy.createOrUpdateUsingJsonObject(realm, json, update));
        }
        if (clazz.equals(com.kg.gettransfer.data.model.Config.class)) {
            return clazz.cast(io.realm.com_kg_gettransfer_data_model_ConfigRealmProxy.createOrUpdateUsingJsonObject(realm, json, update));
        }
        if (clazz.equals(com.kg.gettransfer.data.model.Transfer.class)) {
            return clazz.cast(io.realm.com_kg_gettransfer_data_model_TransferRealmProxy.createOrUpdateUsingJsonObject(realm, json, update));
        }
        if (clazz.equals(com.kg.gettransfer.data.model.Vehicle.class)) {
            return clazz.cast(io.realm.com_kg_gettransfer_data_model_VehicleRealmProxy.createOrUpdateUsingJsonObject(realm, json, update));
        }
        if (clazz.equals(com.kg.gettransfer.data.model.Carrier.class)) {
            return clazz.cast(io.realm.com_kg_gettransfer_data_model_CarrierRealmProxy.createOrUpdateUsingJsonObject(realm, json, update));
        }
        if (clazz.equals(com.kg.gettransfer.data.model.AccountInfo.class)) {
            return clazz.cast(io.realm.com_kg_gettransfer_data_model_AccountInfoRealmProxy.createOrUpdateUsingJsonObject(realm, json, update));
        }
        if (clazz.equals(com.kg.gettransfer.data.model.secondary.PriceConverted.class)) {
            return clazz.cast(io.realm.com_kg_gettransfer_data_model_secondary_PriceConvertedRealmProxy.createOrUpdateUsingJsonObject(realm, json, update));
        }
        if (clazz.equals(com.kg.gettransfer.data.model.secondary.Price.class)) {
            return clazz.cast(io.realm.com_kg_gettransfer_data_model_secondary_PriceRealmProxy.createOrUpdateUsingJsonObject(realm, json, update));
        }
        if (clazz.equals(com.kg.gettransfer.data.model.secondary.ZonedDate.class)) {
            return clazz.cast(io.realm.com_kg_gettransfer_data_model_secondary_ZonedDateRealmProxy.createOrUpdateUsingJsonObject(realm, json, update));
        }
        if (clazz.equals(com.kg.gettransfer.data.model.secondary.Locale.class)) {
            return clazz.cast(io.realm.com_kg_gettransfer_data_model_secondary_LocaleRealmProxy.createOrUpdateUsingJsonObject(realm, json, update));
        }
        if (clazz.equals(com.kg.gettransfer.data.model.secondary.Currency.class)) {
            return clazz.cast(io.realm.com_kg_gettransfer_data_model_secondary_CurrencyRealmProxy.createOrUpdateUsingJsonObject(realm, json, update));
        }
        if (clazz.equals(com.kg.gettransfer.data.model.secondary.Location.class)) {
            return clazz.cast(io.realm.com_kg_gettransfer_data_model_secondary_LocationRealmProxy.createOrUpdateUsingJsonObject(realm, json, update));
        }
        if (clazz.equals(com.kg.gettransfer.data.model.secondary.Rating.class)) {
            return clazz.cast(io.realm.com_kg_gettransfer_data_model_secondary_RatingRealmProxy.createOrUpdateUsingJsonObject(realm, json, update));
        }
        if (clazz.equals(com.kg.gettransfer.data.model.secondary.Language.class)) {
            return clazz.cast(io.realm.com_kg_gettransfer_data_model_secondary_LanguageRealmProxy.createOrUpdateUsingJsonObject(realm, json, update));
        }
        if (clazz.equals(com.kg.gettransfer.data.model.secondary.TransportType.class)) {
            return clazz.cast(io.realm.com_kg_gettransfer_data_model_secondary_TransportTypeRealmProxy.createOrUpdateUsingJsonObject(realm, json, update));
        }
        throw getMissingProxyClassException(clazz);
    }

    @Override
    public <E extends RealmModel> E createUsingJsonStream(Class<E> clazz, Realm realm, JsonReader reader)
        throws IOException {
        checkClass(clazz);

        if (clazz.equals(com.kg.gettransfer.data.model.Offer.class)) {
            return clazz.cast(io.realm.com_kg_gettransfer_data_model_OfferRealmProxy.createUsingJsonStream(realm, reader));
        }
        if (clazz.equals(com.kg.gettransfer.data.model.Config.class)) {
            return clazz.cast(io.realm.com_kg_gettransfer_data_model_ConfigRealmProxy.createUsingJsonStream(realm, reader));
        }
        if (clazz.equals(com.kg.gettransfer.data.model.Transfer.class)) {
            return clazz.cast(io.realm.com_kg_gettransfer_data_model_TransferRealmProxy.createUsingJsonStream(realm, reader));
        }
        if (clazz.equals(com.kg.gettransfer.data.model.Vehicle.class)) {
            return clazz.cast(io.realm.com_kg_gettransfer_data_model_VehicleRealmProxy.createUsingJsonStream(realm, reader));
        }
        if (clazz.equals(com.kg.gettransfer.data.model.Carrier.class)) {
            return clazz.cast(io.realm.com_kg_gettransfer_data_model_CarrierRealmProxy.createUsingJsonStream(realm, reader));
        }
        if (clazz.equals(com.kg.gettransfer.data.model.AccountInfo.class)) {
            return clazz.cast(io.realm.com_kg_gettransfer_data_model_AccountInfoRealmProxy.createUsingJsonStream(realm, reader));
        }
        if (clazz.equals(com.kg.gettransfer.data.model.secondary.PriceConverted.class)) {
            return clazz.cast(io.realm.com_kg_gettransfer_data_model_secondary_PriceConvertedRealmProxy.createUsingJsonStream(realm, reader));
        }
        if (clazz.equals(com.kg.gettransfer.data.model.secondary.Price.class)) {
            return clazz.cast(io.realm.com_kg_gettransfer_data_model_secondary_PriceRealmProxy.createUsingJsonStream(realm, reader));
        }
        if (clazz.equals(com.kg.gettransfer.data.model.secondary.ZonedDate.class)) {
            return clazz.cast(io.realm.com_kg_gettransfer_data_model_secondary_ZonedDateRealmProxy.createUsingJsonStream(realm, reader));
        }
        if (clazz.equals(com.kg.gettransfer.data.model.secondary.Locale.class)) {
            return clazz.cast(io.realm.com_kg_gettransfer_data_model_secondary_LocaleRealmProxy.createUsingJsonStream(realm, reader));
        }
        if (clazz.equals(com.kg.gettransfer.data.model.secondary.Currency.class)) {
            return clazz.cast(io.realm.com_kg_gettransfer_data_model_secondary_CurrencyRealmProxy.createUsingJsonStream(realm, reader));
        }
        if (clazz.equals(com.kg.gettransfer.data.model.secondary.Location.class)) {
            return clazz.cast(io.realm.com_kg_gettransfer_data_model_secondary_LocationRealmProxy.createUsingJsonStream(realm, reader));
        }
        if (clazz.equals(com.kg.gettransfer.data.model.secondary.Rating.class)) {
            return clazz.cast(io.realm.com_kg_gettransfer_data_model_secondary_RatingRealmProxy.createUsingJsonStream(realm, reader));
        }
        if (clazz.equals(com.kg.gettransfer.data.model.secondary.Language.class)) {
            return clazz.cast(io.realm.com_kg_gettransfer_data_model_secondary_LanguageRealmProxy.createUsingJsonStream(realm, reader));
        }
        if (clazz.equals(com.kg.gettransfer.data.model.secondary.TransportType.class)) {
            return clazz.cast(io.realm.com_kg_gettransfer_data_model_secondary_TransportTypeRealmProxy.createUsingJsonStream(realm, reader));
        }
        throw getMissingProxyClassException(clazz);
    }

    @Override
    public <E extends RealmModel> E createDetachedCopy(E realmObject, int maxDepth, Map<RealmModel, RealmObjectProxy.CacheData<RealmModel>> cache) {
        // This cast is correct because obj is either
        // generated by RealmProxy or the original type extending directly from RealmObject
        @SuppressWarnings("unchecked") Class<E> clazz = (Class<E>) realmObject.getClass().getSuperclass();

        if (clazz.equals(com.kg.gettransfer.data.model.Offer.class)) {
            return clazz.cast(io.realm.com_kg_gettransfer_data_model_OfferRealmProxy.createDetachedCopy((com.kg.gettransfer.data.model.Offer) realmObject, 0, maxDepth, cache));
        }
        if (clazz.equals(com.kg.gettransfer.data.model.Config.class)) {
            return clazz.cast(io.realm.com_kg_gettransfer_data_model_ConfigRealmProxy.createDetachedCopy((com.kg.gettransfer.data.model.Config) realmObject, 0, maxDepth, cache));
        }
        if (clazz.equals(com.kg.gettransfer.data.model.Transfer.class)) {
            return clazz.cast(io.realm.com_kg_gettransfer_data_model_TransferRealmProxy.createDetachedCopy((com.kg.gettransfer.data.model.Transfer) realmObject, 0, maxDepth, cache));
        }
        if (clazz.equals(com.kg.gettransfer.data.model.Vehicle.class)) {
            return clazz.cast(io.realm.com_kg_gettransfer_data_model_VehicleRealmProxy.createDetachedCopy((com.kg.gettransfer.data.model.Vehicle) realmObject, 0, maxDepth, cache));
        }
        if (clazz.equals(com.kg.gettransfer.data.model.Carrier.class)) {
            return clazz.cast(io.realm.com_kg_gettransfer_data_model_CarrierRealmProxy.createDetachedCopy((com.kg.gettransfer.data.model.Carrier) realmObject, 0, maxDepth, cache));
        }
        if (clazz.equals(com.kg.gettransfer.data.model.AccountInfo.class)) {
            return clazz.cast(io.realm.com_kg_gettransfer_data_model_AccountInfoRealmProxy.createDetachedCopy((com.kg.gettransfer.data.model.AccountInfo) realmObject, 0, maxDepth, cache));
        }
        if (clazz.equals(com.kg.gettransfer.data.model.secondary.PriceConverted.class)) {
            return clazz.cast(io.realm.com_kg_gettransfer_data_model_secondary_PriceConvertedRealmProxy.createDetachedCopy((com.kg.gettransfer.data.model.secondary.PriceConverted) realmObject, 0, maxDepth, cache));
        }
        if (clazz.equals(com.kg.gettransfer.data.model.secondary.Price.class)) {
            return clazz.cast(io.realm.com_kg_gettransfer_data_model_secondary_PriceRealmProxy.createDetachedCopy((com.kg.gettransfer.data.model.secondary.Price) realmObject, 0, maxDepth, cache));
        }
        if (clazz.equals(com.kg.gettransfer.data.model.secondary.ZonedDate.class)) {
            return clazz.cast(io.realm.com_kg_gettransfer_data_model_secondary_ZonedDateRealmProxy.createDetachedCopy((com.kg.gettransfer.data.model.secondary.ZonedDate) realmObject, 0, maxDepth, cache));
        }
        if (clazz.equals(com.kg.gettransfer.data.model.secondary.Locale.class)) {
            return clazz.cast(io.realm.com_kg_gettransfer_data_model_secondary_LocaleRealmProxy.createDetachedCopy((com.kg.gettransfer.data.model.secondary.Locale) realmObject, 0, maxDepth, cache));
        }
        if (clazz.equals(com.kg.gettransfer.data.model.secondary.Currency.class)) {
            return clazz.cast(io.realm.com_kg_gettransfer_data_model_secondary_CurrencyRealmProxy.createDetachedCopy((com.kg.gettransfer.data.model.secondary.Currency) realmObject, 0, maxDepth, cache));
        }
        if (clazz.equals(com.kg.gettransfer.data.model.secondary.Location.class)) {
            return clazz.cast(io.realm.com_kg_gettransfer_data_model_secondary_LocationRealmProxy.createDetachedCopy((com.kg.gettransfer.data.model.secondary.Location) realmObject, 0, maxDepth, cache));
        }
        if (clazz.equals(com.kg.gettransfer.data.model.secondary.Rating.class)) {
            return clazz.cast(io.realm.com_kg_gettransfer_data_model_secondary_RatingRealmProxy.createDetachedCopy((com.kg.gettransfer.data.model.secondary.Rating) realmObject, 0, maxDepth, cache));
        }
        if (clazz.equals(com.kg.gettransfer.data.model.secondary.Language.class)) {
            return clazz.cast(io.realm.com_kg_gettransfer_data_model_secondary_LanguageRealmProxy.createDetachedCopy((com.kg.gettransfer.data.model.secondary.Language) realmObject, 0, maxDepth, cache));
        }
        if (clazz.equals(com.kg.gettransfer.data.model.secondary.TransportType.class)) {
            return clazz.cast(io.realm.com_kg_gettransfer_data_model_secondary_TransportTypeRealmProxy.createDetachedCopy((com.kg.gettransfer.data.model.secondary.TransportType) realmObject, 0, maxDepth, cache));
        }
        throw getMissingProxyClassException(clazz);
    }

}
