package be.christophebernard.thermostat.bot.common.utils;

public class KeyValuePair<KType, VType> {
    private KType key;
    private VType value;

    public KeyValuePair(KType key, VType value) {
        this.key = key;
        this.value = value;
    }

    public KType getKey() {
        return key;
    }

    public VType getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "{ " +key + ": " + value + " }";
    }
}
