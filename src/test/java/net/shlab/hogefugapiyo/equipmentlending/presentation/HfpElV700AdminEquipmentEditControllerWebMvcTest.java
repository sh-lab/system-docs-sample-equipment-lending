package net.shlab.hogefugapiyo.equipmentlending.presentation;

import java.time.LocalDate;
import java.util.List;
import net.shlab.hogefugapiyo.equipmentlending.application.HfpElSas701AdminEquipmentEditInitApplicationService;
import net.shlab.hogefugapiyo.equipmentlending.application.HfpElSas702RegisterEquipmentApplicationService;
import net.shlab.hogefugapiyo.equipmentlending.application.HfpElSas703UpdateEquipmentInfoApplicationService;
import net.shlab.hogefugapiyo.equipmentlending.application.query.FindAdminEquipmentEditQueryService;
import net.shlab.hogefugapiyo.equipmentlending.model.value.UserRole;
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
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HfpElV700AdminEquipmentEditController.class)
@Import(SecurityConfiguration.class)
class HfpElV700AdminEquipmentEditControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HfpElSas701AdminEquipmentEditInitApplicationService initApplicationService;

    @MockitoBean
    private HfpElSas702RegisterEquipmentApplicationService registerApplicationService;

    @MockitoBean
    private HfpElSas703UpdateEquipmentInfoApplicationService updateApplicationService;

    @MockitoBean
    private I18nMessageResolver i18nMessageResolver;

    @Test
    void showCreateModeRendersRegistrationForm() throws Exception {
        given(initApplicationService.initialize("create", null)).willReturn(createResponse());

        mockMvc.perform(get(RoutePaths.HFP_ELV700_ADMIN_EQUIPMENT_EDIT)
                        .param("mode", "create")
                        .with(userPrincipal("ADMIN1", UserRole.ADMIN)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("管理者備品編集画面")))
                .andExpect(content().string(containsString("登録する")));
    }

    @Test
    void showEditModeRendersStatusUpdateForm() throws Exception {
        given(initApplicationService.initialize("edit", 1001L)).willReturn(editResponse());

        mockMvc.perform(get(RoutePaths.HFP_ELV700_ADMIN_EQUIPMENT_EDIT)
                        .param("mode", "edit")
                        .param("equipmentId", "1001")
                        .with(userPrincipal("ADMIN1", UserRole.ADMIN)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("EQ-0001")))
                .andExpect(content().string(containsString("更新する")));
    }

    @Test
    void registerRedirectsToV600OnSuccess() throws Exception {
        doNothing().when(registerApplicationService)
                .register("ADMIN1", "ライト", "DESK", "第3倉庫", "AVAILABLE", "新規備品");

        mockMvc.perform(post(RoutePaths.HFP_ELV700_ADMIN_EQUIPMENT_EDIT_REGISTER)
                        .with(csrf())
                        .with(userPrincipal("ADMIN1", UserRole.ADMIN))
                        .param("equipmentName", "ライト")
                        .param("equipmentType", "DESK")
                        .param("storageLocation", "第3倉庫")
                        .param("statusCode", "AVAILABLE")
                        .param("remarks", "新規備品"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(RoutePaths.HFP_ELV600_ADMIN_EQUIPMENT_SEARCH + "?messageId=MSG_I_008"));
    }

    @Test
    void updateRedirectsToV600OnSuccess() throws Exception {
        doNothing().when(updateApplicationService).update("ADMIN1", 1001L, "更新後の長机", "UNAVAILABLE", "保守点検中", 0);

        mockMvc.perform(post(RoutePaths.HFP_ELV700_ADMIN_EQUIPMENT_EDIT_UPDATE)
                        .with(csrf())
                        .with(userPrincipal("ADMIN1", UserRole.ADMIN))
                        .param("equipmentId", "1001")
                        .param("equipmentName", "更新後の長机")
                        .param("statusCode", "UNAVAILABLE")
                        .param("remarks", "保守点検中")
                        .param("version", "0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(RoutePaths.HFP_ELV600_ADMIN_EQUIPMENT_SEARCH + "?messageId=MSG_I_009"));
    }

    @Test
    void registerReturnsInputErrorWhenEquipmentNameTooLong() throws Exception {
        given(initApplicationService.initialize("create", null)).willReturn(createResponse());

        mockMvc.perform(post(RoutePaths.HFP_ELV700_ADMIN_EQUIPMENT_EDIT_REGISTER)
                        .with(csrf())
                        .with(userPrincipal("ADMIN1", UserRole.ADMIN))
                        .param("equipmentName", "a".repeat(101))
                        .param("equipmentType", "DESK")
                        .param("storageLocation", "第3倉庫")
                        .param("statusCode", "AVAILABLE")
                        .param("remarks", "新規備品"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("備品名は100文字以内で入力してください。")));
    }

    @Test
    void registerReturnsInputErrorWhenEquipmentNameIsMissing() throws Exception {
        given(initApplicationService.initialize("create", null)).willReturn(createResponse());

        mockMvc.perform(post(RoutePaths.HFP_ELV700_ADMIN_EQUIPMENT_EDIT_REGISTER)
                        .with(csrf())
                        .with(userPrincipal("ADMIN1", UserRole.ADMIN))
                        .param("equipmentType", "DESK")
                        .param("storageLocation", "第3倉庫")
                        .param("statusCode", "AVAILABLE")
                        .param("remarks", "新規備品"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("備品名を入力してください。")));
    }

    @Test
    void updateReturnsInputErrorWhenEquipmentNameTooLong() throws Exception {
        given(initApplicationService.initialize("edit", 1001L)).willReturn(editResponse());

        mockMvc.perform(post(RoutePaths.HFP_ELV700_ADMIN_EQUIPMENT_EDIT_UPDATE)
                        .with(csrf())
                        .with(userPrincipal("ADMIN1", UserRole.ADMIN))
                        .param("equipmentId", "1001")
                        .param("equipmentName", "a".repeat(101))
                        .param("statusCode", "UNAVAILABLE")
                        .param("remarks", "保守点検中")
                        .param("version", "0"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("備品名は100文字以内で入力してください。")));
    }

    @Test
    void updateReturnsInputErrorWhenEquipmentNameIsMissing() throws Exception {
        given(initApplicationService.initialize("edit", 1001L)).willReturn(editResponse());

        mockMvc.perform(post(RoutePaths.HFP_ELV700_ADMIN_EQUIPMENT_EDIT_UPDATE)
                        .with(csrf())
                        .with(userPrincipal("ADMIN1", UserRole.ADMIN))
                        .param("equipmentId", "1001")
                        .param("statusCode", "UNAVAILABLE")
                        .param("remarks", "保守点検中")
                        .param("version", "0"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("備品名を入力してください。")));
    }

    private FindAdminEquipmentEditQueryService.Response createResponse() {
        return new FindAdminEquipmentEditQueryService.Response(
                "create",
                LocalDate.of(2026, 4, 1),
                null,
                List.of(new FindAdminEquipmentEditQueryService.Option("DESK", "長机")),
                List.of(new FindAdminEquipmentEditQueryService.Option("AVAILABLE", "貸出可能"))
        );
    }

    private FindAdminEquipmentEditQueryService.Response editResponse() {
        return new FindAdminEquipmentEditQueryService.Response(
                "edit",
                LocalDate.of(2026, 1, 1),
                new FindAdminEquipmentEditQueryService.EquipmentDetail(
                        1001L,
                        "EQ-0001",
                        "長机 2台",
                        "DESK",
                        "長机",
                        "第1倉庫",
                        LocalDate.of(2026, 1, 1),
                        "初期備考",
                        "AVAILABLE",
                        "貸出可能",
                        0
                ),
                List.of(new FindAdminEquipmentEditQueryService.Option("DESK", "長机")),
                List.of(new FindAdminEquipmentEditQueryService.Option("AVAILABLE", "貸出可能"), new FindAdminEquipmentEditQueryService.Option("UNAVAILABLE", "貸出不可"))
        );
    }
}
