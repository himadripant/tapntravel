package hp.tasks.tapntravel.models;

public enum TapType {
    ON,
    OFF;

    public static TapType tapType(String type) throws IllegalArgumentException {
        for (TapType tapType : TapType.values()) {
            if (tapType.name().equalsIgnoreCase(type)) {
                return tapType;
            }
        }
        throw new IllegalArgumentException("Invalid Tap Type");
    }
}
