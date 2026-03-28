package net.shlab.hogefugapiyo.equipmentlending.application.pure.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import net.shlab.hogefugapiyo.equipmentlending.model.entity.Equipment;
import net.shlab.hogefugapiyo.equipmentlending.model.value.EquipmentStatus;
import org.junit.jupiter.api.Test;

class CheckLendingRequestAvailabilityServiceImplTest {

    private final CheckLendingRequestAvailabilityServiceImpl service = new CheckLendingRequestAvailabilityServiceImpl();

    @Test
    void checkTreatsAllNonAvailableStatusesAsUnavailable() {
        var availableEquipment = equipment(1001L, EquipmentStatus.AVAILABLE);
        var pendingEquipment = equipment(1002L, EquipmentStatus.PENDING_LENDING);
        var lentEquipment = equipment(1003L, EquipmentStatus.LENT);

        var result = service.check(List.of(availableEquipment, pendingEquipment, lentEquipment));

        assertThat(result.valid()).isFalse();
        assertThat(result.unavailableEquipmentIds()).containsExactly(1002L, 1003L);
        assertThat(result.invalidStatusCodes()).containsExactly("PENDING_LENDING", "LENT");
    }

    private Equipment equipment(long equipmentId, EquipmentStatus status) {
        Equipment equipment = new Equipment();
        equipment.setEquipmentId(equipmentId);
        equipment.setStatus(status);
        return equipment;
    }
}
