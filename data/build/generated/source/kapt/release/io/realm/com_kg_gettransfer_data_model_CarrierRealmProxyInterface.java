package io.realm;


public interface com_kg_gettransfer_data_model_CarrierRealmProxyInterface {
    public int realmGet$id();
    public void realmSet$id(int value);
    public String realmGet$title();
    public void realmSet$title(String value);
    public Boolean realmGet$approved();
    public void realmSet$approved(Boolean value);
    public int realmGet$completedTransfers();
    public void realmSet$completedTransfers(int value);
    public RealmList<com.kg.gettransfer.data.model.secondary.Language> realmGet$languages();
    public void realmSet$languages(RealmList<com.kg.gettransfer.data.model.secondary.Language> value);
    public com.kg.gettransfer.data.model.secondary.Rating realmGet$rating();
    public void realmSet$rating(com.kg.gettransfer.data.model.secondary.Rating value);
    public String realmGet$email();
    public void realmSet$email(String value);
    public String realmGet$phone();
    public void realmSet$phone(String value);
    public String realmGet$alternatePhone();
    public void realmSet$alternatePhone(String value);
}
