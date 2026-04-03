package net.shlab.hogefugapiyo.equipmentlending.presentation;

import java.time.LocalDate;
import java.util.List;
import net.shlab.hogefugapiyo.equipmentlending.application.HfpElSas601AdminEquipmentSearchInitApplicationService;
import net.shlab.hogefugapiyo.equipmentlending.application.HfpElSas602SearchAdminEquipmentApplicationService;
import net.shlab.hogefugapiyo.equipmentlending.application.query.SearchAdminEquipmentQueryService;
import net.shlab.hogefugapiyo.equipmentlending.model.value.UserRole;
import net.shlab.hogefugapiyo.equipmentlending.presentation.controller.HfpElV600AdminEquipmentSearchController;
import net.shlab.hogefugapiyo.equipmentlending.presentation.route.RoutePaths;
import net.shlab.hogefugapiyo.framework.i18n.I18nMessageResolver;
import net.shlab.hogefugapiyo.equipmentlending.infrastructure.security.config.SecurityConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static net.shlab.hogefugapiyo.framework.security.SecurityMockMvcTestSupport.userPrincipal;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HfpElV600AdminEquipmentSearchController.class)
@Import(SecurityConfiguration.class)
class HfpElV600AdminEquipmentSearchControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HfpElSas601AdminEquipmentSearchInitApplicationService initApplicationService;

    @MockitoBean
    private HfpElSas602SearchAdminEquipmentApplicationService searchApplicationService;

    @MockitoBean
    private I18nMessageResolver i18nMessageResolver;

    @Test
    void redirectsUserToUserMypage() throws Exception {
        mockMvc.perform(get(RoutePaths.HFP_ELV600_ADMIN_EQUIPMENT_SEARCH)
                        .with(userPrincipal("USER01", UserRole.USER)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(RoutePaths.HFP_ELV100_USER_MYPAGE));
    }

    @Test
    void initialDisplayShowsAdminEquipmentList() throws Exception {
        given(initApplicationService.initialize()).willReturn(response());

        mockMvc.perform(get(RoutePaths.HFP_ELV600_ADMIN_EQUIPMENT_SEARCH)
                        .with(userPrincipal("ADMIN1", UserRole.ADMIN)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("管理者備品検索画面")))
                .andExpect(content().string(containsString("EQ-0001")))
                .andExpect(content().string(containsString(RoutePaths.HFP_ELV700_ADMIN_EQUIPMENT_EDIT)))
                .andExpect(content().string(containsString("新規備品登録")));
    }

    @Test
    void searchParsesSystemRegisteredDateRange() throws Exception {
        given(searchApplicationService.search("長机", "DESK", "AVAILABLE", LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31)))
                .willReturn(response());

        mockMvc.perform(get(RoutePaths.HFP_ELV600_ADMIN_EQUIPMENT_SEARCH)
                        .param("equipmentName", "長机")
                        .param("equipmentType", "DESK")
                        .param("statusCode", "AVAILABLE")
                        .param("systemRegisteredDateFrom", "2026-01-01")
                        .param("systemRegisteredDateTo", "2026-01-31")
                        .with(userPrincipal("ADMIN1", UserRole.ADMIN)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("2026-01-01")));
    }

    @Test
    void searchAcceptsOnlySystemRegisteredDateFrom() throws Exception {
        given(searchApplicationService.search("長机", "DESK", "AVAILABLE", LocalDate.of(2026, 1, 1), null))
                .willReturn(response());

        mockMvc.perform(get(RoutePaths.HFP_ELV600_ADMIN_EQUIPMENT_SEARCH)
                        .param("equipmentName", "長机")
                        .param("equipmentType", "DESK")
                        .param("statusCode", "AVAILABLE")
                        .param("systemRegisteredDateFrom", "2026-01-01")
                        .with(userPrincipal("ADMIN1", UserRole.ADMIN)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("2026-01-01")));
    }

    private SearchAdminEquipmentQueryService.Response response() {
        return new SearchAdminEquipmentQueryService.Response(
                List.of(new SearchAdminEquipmentQueryService.EquipmentItem(
                        1001L,
                        "EQ-0001",
                        "長机 2台",
                        "DESK",
                        "長机",
                        LocalDate.of(2026, 1, 1),
                        "第1倉庫",
                        "AVAILABLE",
                        "貸出可能",
                        0
                )),
                List.of(new SearchAdminEquipmentQueryService.Option("DESK", "長机")),
                List.of(new SearchAdminEquipmentQueryService.Option("ALL", "すべて")),
                false
        );
    }
}
