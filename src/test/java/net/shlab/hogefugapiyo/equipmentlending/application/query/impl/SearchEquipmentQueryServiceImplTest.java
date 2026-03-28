package net.shlab.hogefugapiyo.equipmentlending.application.query.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.stream.IntStream;
import net.shlab.hogefugapiyo.equipmentlending.infrastructure.repository.query.EquipmentSearchQueryRepository;
import net.shlab.hogefugapiyo.framework.i18n.I18nMessageResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SearchEquipmentQueryServiceImplTest {

    @Mock
    private EquipmentSearchQueryRepository equipmentSearchQueryRepository;

    @Mock
    private I18nMessageResolver i18nMessageResolver;

    @InjectMocks
    private SearchEquipmentQueryServiceImpl service;

    @Test
    void executeReturnsAllItemsWhenWithinDisplayLimit() {
        var query = new SearchEquipmentQueryServiceImpl.Request("プロジェクター", "PROJECTOR", "AVAILABLE");
        var criteria = new EquipmentSearchQueryRepository.Criteria("プロジェクター", "PROJECTOR", "AVAILABLE");
        var items = List.of(
                new EquipmentSearchQueryRepository.EquipmentRow(1L, "EQ-0001", "プロジェクターA", "PROJECTOR", "倉庫A", "AVAILABLE", true),
                new EquipmentSearchQueryRepository.EquipmentRow(2L, "EQ-0002", "プロジェクターB", "PROJECTOR", "倉庫B", "PENDING_LENDING", false)
        );
        var options = List.of(new EquipmentSearchQueryRepository.EquipmentTypeOptionRow("PROJECTOR"));
        when(equipmentSearchQueryRepository.findEquipmentByCriteria(criteria)).thenReturn(items);
        when(equipmentSearchQueryRepository.findEquipmentTypeOptions()).thenReturn(options);
        when(i18nMessageResolver.get("label.equipment-type.projector")).thenReturn("プロジェクター");
        when(i18nMessageResolver.get("label.status.available")).thenReturn("貸出可能");
        when(i18nMessageResolver.get("label.status.unavailable")).thenReturn("貸出不可");

        var actual = service.execute(query);

        assertThat(actual.equipmentItems()).containsExactly(
                new SearchEquipmentQueryServiceImpl.EquipmentItem(1L, "EQ-0001", "プロジェクターA", "プロジェクター", "倉庫A", "貸出可能", true),
                new SearchEquipmentQueryServiceImpl.EquipmentItem(2L, "EQ-0002", "プロジェクターB", "プロジェクター", "倉庫B", "貸出不可", false)
        );
        assertThat(actual.equipmentTypeOptions()).containsExactly(
                new SearchEquipmentQueryServiceImpl.Option("PROJECTOR", "プロジェクター")
        );
        assertThat(actual.hasMoreThanLimit()).isFalse();
        verify(equipmentSearchQueryRepository).findEquipmentByCriteria(criteria);
        verify(equipmentSearchQueryRepository).findEquipmentTypeOptions();
    }

    @Test
    void executeLimitsVisibleItemsToHundredWhenRepositoryReturnsMore() {
        var query = new SearchEquipmentQueryServiceImpl.Request("", "", "AVAILABLE");
        var criteria = new EquipmentSearchQueryRepository.Criteria("", "", "AVAILABLE");
        var items = IntStream.rangeClosed(1, 101)
                .mapToObj(index -> new EquipmentSearchQueryRepository.EquipmentRow(
                        index,
                        "EQ-%04d".formatted(index),
                        "備品%d".formatted(index),
                        "PROJECTOR",
                        "倉庫",
                        "AVAILABLE",
                        true
                ))
                .toList();
        when(equipmentSearchQueryRepository.findEquipmentByCriteria(criteria)).thenReturn(items);
        when(equipmentSearchQueryRepository.findEquipmentTypeOptions()).thenReturn(List.of());
        when(i18nMessageResolver.get("label.equipment-type.projector")).thenReturn("プロジェクター");
        when(i18nMessageResolver.get("label.status.available")).thenReturn("貸出可能");

        var actual = service.execute(query);

        assertThat(actual.equipmentItems()).hasSize(100);
        assertThat(actual.equipmentItems().getFirst().equipmentCode()).isEqualTo("EQ-0001");
        assertThat(actual.equipmentItems().getLast().equipmentCode()).isEqualTo("EQ-0100");
        assertThat(actual.hasMoreThanLimit()).isTrue();
    }
}
