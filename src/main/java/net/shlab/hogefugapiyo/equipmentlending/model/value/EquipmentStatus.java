package net.shlab.hogefugapiyo.equipmentlending.model.value;

import java.util.Arrays;

public enum EquipmentStatus {
    AVAILABLE("AVAILABLE"),
    PENDING_LENDING("PENDING_LENDING"),
    LENT("LENT"),
    UNAVAILABLE("UNAVAILABLE"),
    DISPOSED("DISPOSED");

    private final String code;

    EquipmentStatus(String code) {
        this.code = code;
    }

    public String code() {
        return code;
    }

    public static EquipmentStatus fromCode(String code) {
        return Arrays.stream(values())
                .filter(value -> value.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown equipment status: " + code));
    }
}
