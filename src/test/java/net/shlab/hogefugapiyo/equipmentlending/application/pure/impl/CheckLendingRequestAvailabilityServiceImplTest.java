package net.shlab.hogefugapiyo.equipmentlending.application.pure.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import net.shlab.hogefugapiyo.equipmentlending.application.pure.EquipmentAvailabilityInput;
import org.junit.jupiter.api.Test;

class CheckLendingRequestAvailabilityServiceImplTest {

    private final CheckLendingRequestAvailabilityServiceImpl service = new CheckLendingRequestAvailabilityServiceImpl();

    @Test
    void checkTreatsAllNonAvailableStatusesAsUnavailable() {
        var availableEquipment = new EquipmentAvailabilityInput(1001L, "AVAILABLE");
        var pendingEquipment = new EquipmentAvailabilityInput(1002L, "PENDING_LENDING");
        var lentEquipment = new EquipmentAvailabilityInput(1003L, "LENT");

        var result = service.check(List.of(availableEquipment, pendingEquipment, lentEquipment));

        assertThat(result.valid()).isFalse();
        assertThat(result.unavailableEquipmentIds()).containsExactly(1002L, 1003L);
        assertThat(result.invalidStatusCodes()).containsExactly("PENDING_LENDING", "LENT");
    }
}
