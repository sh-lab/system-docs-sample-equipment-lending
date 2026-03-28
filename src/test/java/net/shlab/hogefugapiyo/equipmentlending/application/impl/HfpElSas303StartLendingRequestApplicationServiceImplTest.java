package net.shlab.hogefugapiyo.equipmentlending.application.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.List;
import net.shlab.hogefugapiyo.equipmentlending.application.BusinessException;
import net.shlab.hogefugapiyo.equipmentlending.application.command.LendingRequestAvailabilityResult;
import net.shlab.hogefugapiyo.equipmentlending.application.pure.CheckLendingRequestAvailabilityService;
import net.shlab.hogefugapiyo.equipmentlending.infrastructure.repository.entity.EquipmentRepository;
import net.shlab.hogefugapiyo.equipmentlending.model.entity.Equipment;
import net.shlab.hogefugapiyo.equipmentlending.model.value.EquipmentStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HfpElSas303StartLendingRequestApplicationServiceImplTest {

    @Mock
    private EquipmentRepository equipmentRepository;

    @Mock
    private CheckLendingRequestAvailabilityService checkLendingRequestAvailabilityService;

    @InjectMocks
    private HfpElSas303StartLendingRequestApplicationServiceImpl applicationService;

    @Test
    void startReturnsEquipmentIdsWhenAllSelectedEquipmentIsAvailable() {
        List<Long> equipmentIds = List.of(1001L, 1004L);
        List<Equipment> equipments = List.of(equipment(1001L, EquipmentStatus.AVAILABLE), equipment(1004L, EquipmentStatus.AVAILABLE));
        when(equipmentRepository.findByIds(equipmentIds)).thenReturn(equipments);
        when(checkLendingRequestAvailabilityService.check(equipments))
                .thenReturn(new LendingRequestAvailabilityResult(true, List.of(), List.of()));

        List<Long> actual = applicationService.start(equipmentIds);

        assertThat(actual).containsExactly(1001L, 1004L);
    }

    @Test
    void startThrowsBusinessExceptionWhenUnavailableEquipmentIsIncluded() {
        List<Long> equipmentIds = List.of(1001L, 1004L);
        List<Equipment> equipments = List.of(equipment(1001L, EquipmentStatus.AVAILABLE), equipment(1004L, EquipmentStatus.PENDING_LENDING));
        when(equipmentRepository.findByIds(equipmentIds)).thenReturn(equipments);
        when(checkLendingRequestAvailabilityService.check(equipments))
                .thenReturn(new LendingRequestAvailabilityResult(false, List.of(1004L), List.of("PENDING_LENDING")));

        assertThatThrownBy(() -> applicationService.start(equipmentIds))
                .isInstanceOf(BusinessException.class)
                .extracting("messageId")
                .isEqualTo("MSG_E_001");
    }

    private Equipment equipment(long equipmentId, EquipmentStatus status) {
        Equipment equipment = new Equipment();
        equipment.setEquipmentId(equipmentId);
        equipment.setStatus(status);
        return equipment;
    }
}
