package net.shlab.hogefugapiyo.equipmentlending.application.command;

import java.util.List;

public record LendingRequestAvailabilityResult(
        boolean valid,
        List<Long> unavailableEquipmentIds,
        List<String> invalidStatusCodes
) {
}
