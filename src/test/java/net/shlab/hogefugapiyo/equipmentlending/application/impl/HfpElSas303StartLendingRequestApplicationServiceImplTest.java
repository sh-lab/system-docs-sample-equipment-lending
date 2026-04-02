package net.shlab.hogefugapiyo.equipmentlending.application.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.List;
import net.shlab.hogefugapiyo.equipmentlending.application.BusinessException;
import net.shlab.hogefugapiyo.equipmentlending.application.command.LendingRequestAvailabilityResult;
import net.shlab.hogefugapiyo.equipmentlending.application.pure.CheckLendingRequestAvailabilityService;
import net.shlab.hogefugapiyo.equipmentlending.application.pure.EquipmentAvailabilityInput;
import net.shlab.hogefugapiyo.equipmentlending.application.query.FindEquipmentByIdsQueryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HfpElSas303StartLendingRequestApplicationServiceImplTest {

    @Mock
    private FindEquipmentByIdsQueryService findEquipmentByIdsQueryService;

    @Mock
    private CheckLendingRequestAvailabilityService checkLendingRequestAvailabilityService;

    @InjectMocks
    private HfpElSas303StartLendingRequestApplicationServiceImpl applicationService;

    @Test
    void startReturnsEquipmentIdsWhenAllSelectedEquipmentIsAvailable() {
        List<Long> equipmentIds = List.of(1001L, 1004L);
        var queryResponse = new FindEquipmentByIdsQueryService.Response(List.of(
                equipmentItem(1001L, "AVAILABLE"),
                equipmentItem(1004L, "AVAILABLE")
        ));
        when(findEquipmentByIdsQueryService.execute(new FindEquipmentByIdsQueryService.Request(equipmentIds)))
                .thenReturn(queryResponse);
        List<EquipmentAvailabilityInput> inputs = List.of(
                new EquipmentAvailabilityInput(1001L, "AVAILABLE"),
                new EquipmentAvailabilityInput(1004L, "AVAILABLE")
        );
        when(checkLendingRequestAvailabilityService.check(inputs))
                .thenReturn(new LendingRequestAvailabilityResult(true, List.of(), List.of()));

        List<Long> actual = applicationService.start(equipmentIds);

        assertThat(actual).containsExactly(1001L, 1004L);
    }

    @Test
    void startThrowsBusinessExceptionWhenUnavailableEquipmentIsIncluded() {
        List<Long> equipmentIds = List.of(1001L, 1004L);
        var queryResponse = new FindEquipmentByIdsQueryService.Response(List.of(
                equipmentItem(1001L, "AVAILABLE"),
                equipmentItem(1004L, "PENDING_LENDING")
        ));
        when(findEquipmentByIdsQueryService.execute(new FindEquipmentByIdsQueryService.Request(equipmentIds)))
                .thenReturn(queryResponse);
        List<EquipmentAvailabilityInput> inputs = List.of(
                new EquipmentAvailabilityInput(1001L, "AVAILABLE"),
                new EquipmentAvailabilityInput(1004L, "PENDING_LENDING")
        );
        when(checkLendingRequestAvailabilityService.check(inputs))
                .thenReturn(new LendingRequestAvailabilityResult(false, List.of(1004L), List.of("PENDING_LENDING")));

        assertThatThrownBy(() -> applicationService.start(equipmentIds))
                .isInstanceOf(BusinessException.class)
                .extracting("messageId")
                .isEqualTo("MSG_E_001");
    }

    private FindEquipmentByIdsQueryService.EquipmentItem equipmentItem(long equipmentId, String statusCode) {
        return new FindEquipmentByIdsQueryService.EquipmentItem(
                equipmentId, "EQ-" + equipmentId, "Equipment " + equipmentId,
                "TYPE1", "タイプ1", "倉庫A", statusCode
        );
    }
}
