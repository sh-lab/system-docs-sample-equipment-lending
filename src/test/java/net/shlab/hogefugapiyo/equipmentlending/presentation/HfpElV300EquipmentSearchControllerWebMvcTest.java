package net.shlab.hogefugapiyo.equipmentlending.presentation;

import net.shlab.hogefugapiyo.equipmentlending.application.query.impl.SearchEquipmentQueryServiceImpl;
import java.util.List;
import net.shlab.hogefugapiyo.equipmentlending.application.HfpElSas301EquipmentSearchInitApplicationService;
import net.shlab.hogefugapiyo.equipmentlending.application.HfpElSas302SearchEquipmentApplicationService;
import net.shlab.hogefugapiyo.equipmentlending.application.HfpElSas303StartLendingRequestApplicationService;
import net.shlab.hogefugapiyo.equipmentlending.application.BusinessException;
import net.shlab.hogefugapiyo.equipmentlending.presentation.controller.HfpElV300EquipmentSearchController;
import net.shlab.hogefugapiyo.equipmentlending.presentation.route.RoutePaths;
import net.shlab.hogefugapiyo.equipmentlending.model.value.UserRole;
import net.shlab.hogefugapiyo.equipmentlending.infrastructure.security.config.SecurityConfiguration;
import net.shlab.hogefugapiyo.framework.i18n.I18nMessageResolver;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static net.shlab.hogefugapiyo.framework.security.SecurityMockMvcTestSupport.userPrincipal;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HfpElV300EquipmentSearchController.class)
@Import(SecurityConfiguration.class)
class HfpElV300EquipmentSearchControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HfpElSas301EquipmentSearchInitApplicationService equipmentSearchInitApplicationService;

    @MockitoBean
    private HfpElSas302SearchEquipmentApplicationService searchEquipmentApplicationService;

    @MockitoBean
    private HfpElSas303StartLendingRequestApplicationService startLendingRequestApplicationService;

    @MockitoBean
    private I18nMessageResolver i18nMessageResolver;

    @Test
    void redirectsToLoginWhenSessionIsMissing() throws Exception {
        mockMvc.perform(get(RoutePaths.HFP_ELV300_EQUIPMENT_SEARCH))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost" + RoutePaths.LOGIN));
    }

    @Test
    void redirectsAdminToAdminMypage() throws Exception {
        mockMvc.perform(get(RoutePaths.HFP_ELV300_EQUIPMENT_SEARCH)
                        .with(userPrincipal("ADMIN1", UserRole.ADMIN)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(RoutePaths.HFP_ELV200_ADMIN_MYPAGE));
    }

    @Test
    void initialDisplayShowsAvailableEquipment() throws Exception {
        given(equipmentSearchInitApplicationService.initialize())
                .willReturn(new SearchEquipmentQueryServiceImpl.Response(
                        List.of(new SearchEquipmentQueryServiceImpl.EquipmentItem(1001L, "EQ-0001", "長机 2台", "長机", "第1倉庫", "貸出可能", true)),
                        equipmentTypeOptions(),
                        false
                ));

        mockMvc.perform(get(RoutePaths.HFP_ELV300_EQUIPMENT_SEARCH)
                        .with(userPrincipal("USER02", UserRole.USER)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("HFP備品貸出システム:備品検索画面")))
                .andExpect(content().string(containsString("備品検索画面")))
                .andExpect(content().string(containsString("貸出可能")))
                .andExpect(content().string(containsString("EQ-0001")))
                .andExpect(content().string(not(containsString("[MSG_I_001]該当する備品はありません。"))))
                .andExpect(content().string(containsString("マイページへ戻る")))
                .andExpect(content().string(containsString(RoutePaths.HFP_ELV300_EQUIPMENT_SEARCH_LENDING_START)))
                .andExpect(content().string(containsString("貸出申請")));
    }

    @Test
    void equipmentSearchCanFilterByKeywordAndStatus() throws Exception {
        given(searchEquipmentApplicationService.search("プロジェクター", "", "ALL"))
                .willReturn(new SearchEquipmentQueryServiceImpl.Response(
                        List.of(new SearchEquipmentQueryServiceImpl.EquipmentItem(1021L, "EQ-0021", "プロジェクター", "プロジェクター", "映像機材庫", "貸出可能", true)),
                        equipmentTypeOptions(),
                        false
                ));

        mockMvc.perform(get(RoutePaths.HFP_ELV300_EQUIPMENT_SEARCH)
                        .param("equipmentName", "プロジェクター")
                        .param("equipmentType", "")
                        .param("lendingStatus", "ALL")
                        .with(userPrincipal("USER02", UserRole.USER)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("プロジェクター")))
                .andExpect(content().string(not(containsString("EQ-0001"))))
                .andExpect(content().string(containsString("name=\"equipmentIds\"")));
    }

    @Test
    void equipmentSearchShowsInfoMessageWhenNoResultsFound() throws Exception {
        given(searchEquipmentApplicationService.search("存在しない備品", "", "ALL"))
                .willReturn(new SearchEquipmentQueryServiceImpl.Response(List.of(), equipmentTypeOptions(), false));
        given(i18nMessageResolver.getBusinessMessage("MSG_I_001"))
                .willReturn("[MSG_I_001]該当する備品はありません。");

        mockMvc.perform(get(RoutePaths.HFP_ELV300_EQUIPMENT_SEARCH)
                        .param("equipmentName", "存在しない備品")
                        .param("equipmentType", "")
                        .param("lendingStatus", "ALL")
                        .with(userPrincipal("USER02", UserRole.USER)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("[MSG_I_001]該当する備品はありません。")))
                .andExpect(content().string(not(containsString("EQ-0001"))));
    }

    @Test
    void equipmentSearchShowsWarningWhenResultsExceedDisplayLimit() throws Exception {
        given(searchEquipmentApplicationService.search("", "", "AVAILABLE"))
                .willReturn(new SearchEquipmentQueryServiceImpl.Response(List.of(
                        new SearchEquipmentQueryServiceImpl.EquipmentItem(1001L, "EQ-0001", "長机 2台", "長机", "第1倉庫", "貸出可能", true)
                ), equipmentTypeOptions(), true));
        given(i18nMessageResolver.getBusinessMessage("MSG_W_002"))
                .willReturn("[MSG_W_002]検索結果が100件を超えています。先頭100件を表示します。条件を絞ってください。");

        mockMvc.perform(get(RoutePaths.HFP_ELV300_EQUIPMENT_SEARCH)
                        .param("equipmentName", "")
                        .param("equipmentType", "")
                        .param("lendingStatus", "AVAILABLE")
                        .with(userPrincipal("USER02", UserRole.USER)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("[MSG_W_002]検索結果が100件を超えています。先頭100件を表示します。条件を絞ってください。")));
    }

    @Test
    void lendingStartRedirectsToV400WhenSelectedEquipmentIsStillAvailable() throws Exception {
        given(startLendingRequestApplicationService.start(List.of(1001L, 1004L)))
                .willReturn(List.of(1001L, 1004L));

        mockMvc.perform(post(RoutePaths.HFP_ELV300_EQUIPMENT_SEARCH_LENDING_START)
                        .with(csrf())
                        .with(userPrincipal("USER02", UserRole.USER))
                        .param("equipmentIds", "1001", "1004")
                        .param("equipmentName", "長机")
                        .param("equipmentType", "DESK")
                        .param("lendingStatus", "AVAILABLE"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(RoutePaths.HFP_ELV400_USER_LENDING_REQUEST + "?from=V300&equipmentIds=1001&equipmentIds=1004"));
    }

    @Test
    void lendingStartReturnsToSearchWhenEquipmentStatusChanged() throws Exception {
        doThrow(new BusinessException("MSG_E_001"))
                .when(startLendingRequestApplicationService)
                .start(List.of(1001L, 1004L));

        mockMvc.perform(post(RoutePaths.HFP_ELV300_EQUIPMENT_SEARCH_LENDING_START)
                        .with(csrf())
                        .with(userPrincipal("USER02", UserRole.USER))
                        .param("equipmentIds", "1001", "1004")
                        .param("equipmentName", "長机")
                        .param("equipmentType", "DESK")
                        .param("lendingStatus", "AVAILABLE"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(RoutePaths.HFP_ELV300_EQUIPMENT_SEARCH
                        + "?equipmentName=%E9%95%B7%E6%9C%BA&equipmentType=DESK&lendingStatus=AVAILABLE&errorMessageId=MSG_E_001"));
    }

    @Test
    void lendingStartWithoutSelectionRedisplaysSearchWithValidationMessage() throws Exception {
        given(searchEquipmentApplicationService.search("長机", "DESK", "AVAILABLE"))
                .willReturn(new SearchEquipmentQueryServiceImpl.Response(
                        List.of(new SearchEquipmentQueryServiceImpl.EquipmentItem(1001L, "EQ-0001", "長机 2台", "長机", "第1倉庫", "貸出可能", true)),
                        equipmentTypeOptions(),
                        false
                ));

        mockMvc.perform(post(RoutePaths.HFP_ELV300_EQUIPMENT_SEARCH_LENDING_START)
                        .with(csrf())
                        .with(userPrincipal("USER02", UserRole.USER))
                        .param("equipmentName", "長机")
                        .param("equipmentType", "DESK")
                        .param("lendingStatus", "AVAILABLE"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("貸出申請する備品を1件以上選択してください。")))
                .andExpect(content().string(containsString("備品検索画面")));
    }

    private List<SearchEquipmentQueryServiceImpl.Option> equipmentTypeOptions() {
        return List.of(
                new SearchEquipmentQueryServiceImpl.Option("DESK", "長机"),
                new SearchEquipmentQueryServiceImpl.Option("PROJECTOR", "プロジェクター")
        );
    }
}
