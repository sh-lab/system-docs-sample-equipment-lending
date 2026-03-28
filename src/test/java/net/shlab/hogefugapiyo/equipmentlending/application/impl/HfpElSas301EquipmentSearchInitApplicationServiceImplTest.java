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
class HfpElSas301EquipmentSearchInitApplicationServiceImplTest {

    @Mock
    private SearchEquipmentQueryService searchEquipmentQueryService;

    @InjectMocks
    private HfpElSas301EquipmentSearchInitApplicationServiceImpl applicationService;

    @Test
    void initializeLoadsAvailableEquipmentWithEmptyCriteria() {
        var expected = new SearchEquipmentQueryServiceImpl.Response(List.of(), List.of(), false);
        when(searchEquipmentQueryService.execute(eq(new SearchEquipmentQueryServiceImpl.Request("", "", "AVAILABLE")))).thenReturn(expected);

        var actual = applicationService.initialize();

        assertThat(actual).isEqualTo(expected);
        verify(searchEquipmentQueryService).execute(new SearchEquipmentQueryServiceImpl.Request("", "", "AVAILABLE"));
    }
}
