package net.shlab.hogefugapiyo.equipmentlending.application.impl;

import net.shlab.hogefugapiyo.equipmentlending.application.query.impl.SearchEquipmentQueryServiceImpl;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import net.shlab.hogefugapiyo.equipmentlending.application.query.SearchEquipmentQueryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HfpElSas302SearchEquipmentApplicationServiceImplTest {

    @Mock
    private SearchEquipmentQueryService searchEquipmentQueryService;

    @InjectMocks
    private HfpElSas302SearchEquipmentApplicationServiceImpl applicationService;

    @Test
    void searchNormalizesTrimmedCriteriaAndAllowedStatus() {
        var expected = new SearchEquipmentQueryServiceImpl.Response(List.of(), List.of(), false);
        var query = new SearchEquipmentQueryServiceImpl.Request("プロジェクター", "PROJECTOR", "ALL");
        when(searchEquipmentQueryService.execute(eq(query))).thenReturn(expected);

        var actual = applicationService.search("  プロジェクター  ", "  PROJECTOR  ", "  ALL  ");

        assertThat(actual).isEqualTo(expected);
        verify(searchEquipmentQueryService).execute(query);
    }

    @Test
    void searchDefaultsNullOrUnsupportedCriteriaToAvailableStatus() {
        var expected = new SearchEquipmentQueryServiceImpl.Response(List.of(), List.of(), false);
        var query = new SearchEquipmentQueryServiceImpl.Request("", "", "AVAILABLE");
        when(searchEquipmentQueryService.execute(eq(query))).thenReturn(expected);

        var actual = applicationService.search(null, null, "UNKNOWN");

        assertThat(actual).isEqualTo(expected);
        verify(searchEquipmentQueryService).execute(query);
    }

    @Test
    void searchAllowsUnavailableStatus() {
        var expected = new SearchEquipmentQueryServiceImpl.Response(List.of(), List.of(), false);
        var query = new SearchEquipmentQueryServiceImpl.Request("", "", "UNAVAILABLE");
        when(searchEquipmentQueryService.execute(eq(query))).thenReturn(expected);

        var actual = applicationService.search("", "", "UNAVAILABLE");

        assertThat(actual).isEqualTo(expected);
        verify(searchEquipmentQueryService).execute(query);
    }
}
