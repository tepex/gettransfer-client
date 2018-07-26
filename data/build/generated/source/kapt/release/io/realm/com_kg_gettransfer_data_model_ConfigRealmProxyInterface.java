package io.realm;


public interface com_kg_gettransfer_data_model_ConfigRealmProxyInterface {
    public int realmGet$id();
    public void realmSet$id(int value);
    public RealmList<com.kg.gettransfer.data.model.secondary.Locale> realmGet$availableLocales();
    public void realmSet$availableLocales(RealmList<com.kg.gettransfer.data.model.secondary.Locale> value);
    public String realmGet$preferredLocale();
    public void realmSet$preferredLocale(String value);
    public RealmList<com.kg.gettransfer.data.model.secondary.Currency> realmGet$supportedCurrencies();
    public void realmSet$supportedCurrencies(RealmList<com.kg.gettransfer.data.model.secondary.Currency> value);
    public RealmList<String> realmGet$supportedDistanceUnits();
    public void realmSet$supportedDistanceUnits(RealmList<String> value);
}
