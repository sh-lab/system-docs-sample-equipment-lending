package net.shlab.hogefugapiyo.equipmentlending.model.value;

import java.util.Arrays;

public enum LendingRequestStatus {
    PENDING_APPROVAL("PENDING_APPROVAL"),
    LENT("LENT"),
    PENDING_RETURN_CONFIRMATION("PENDING_RETURN_CONFIRMATION"),
    REJECTED("REJECTED"),
    COMPLETED("COMPLETED");

    private final String code;

    LendingRequestStatus(String code) {
        this.code = code;
    }

    public String code() {
        return code;
    }

    public static LendingRequestStatus fromCode(String code) {
        return Arrays.stream(values())
                .filter(value -> value.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown lending request status: " + code));
    }
}
