package net.shlab.hogefugapiyo.equipmentlending.presentation;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import net.shlab.hogefugapiyo.equipmentlending.application.HfpElSas601AdminEquipmentSearchInitApplicationService;
import net.shlab.hogefugapiyo.equipmentlending.application.HfpElSas602SearchAdminEquipmentApplicationService;
import net.shlab.hogefugapiyo.equipmentlending.application.query.SearchAdminEquipmentQueryService;
import net.shlab.hogefugapiyo.equipmentlending.presentation.route.RoutePaths;
import net.shlab.hogefugapiyo.equipmentlending.presentation.views.Views;
import net.shlab.hogefugapiyo.equipmentlending.presentation.controller.AbstractBaseController;
import net.shlab.hogefugapiyo.framework.i18n.I18nMessageResolver;
import net.shlab.hogefugapiyo.equipmentlending.infrastructure.security.UserPrincipal;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@PreAuthorize("hasRole('ADMIN')")
public class HfpElV600AdminEquipmentSearchController extends AbstractBaseController {

    private static final String TOO_MANY_RESULTS_MESSAGE_ID = "MSG_W_002";
    private static final String NO_RESULTS_MESSAGE_ID = "MSG_I_001";
    private static final String INVALID_DISPLAY_MESSAGE_ID = "MSG_E_009";

    private final HfpElSas601AdminEquipmentSearchInitApplicationService initApplicationService;
    private final HfpElSas602SearchAdminEquipmentApplicationService searchApplicationService;

    public HfpElV600AdminEquipmentSearchController(
            HfpElSas601AdminEquipmentSearchInitApplicationService initApplicationService,
            HfpElSas602SearchAdminEquipmentApplicationService searchApplicationService,
            I18nMessageResolver i18nMessageResolver
    ) {
        super(i18nMessageResolver);
        this.initApplicationService = initApplicationService;
        this.searchApplicationService = searchApplicationService;
    }

    @GetMapping(RoutePaths.HFP_ELV600_ADMIN_EQUIPMENT_SEARCH)
    public String show(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            Model model,
            @RequestParam(value = "equipmentName", required = false) String equipmentName,
            @RequestParam(value = "equipmentType", required = false) String equipmentType,
            @RequestParam(value = "statusCode", required = false) String statusCode,
            @RequestParam(value = "systemRegisteredDate", required = false) String systemRegisteredDate,
            @RequestParam(value = "messageId", required = false) String messageId,
            @RequestParam(value = "errorMessageId", required = false) String errorMessageId
    ) {
        boolean initialDisplay = equipmentName == null && equipmentType == null && statusCode == null && systemRegisteredDate == null;
        LocalDate parsedDate = parseDate(systemRegisteredDate);
        String resolvedErrorMessageId = parsedDate == null && systemRegisteredDate != null && !systemRegisteredDate.isBlank()
                ? INVALID_DISPLAY_MESSAGE_ID
                : errorMessageId;
        SearchAdminEquipmentQueryService.Response result = initialDisplay
                ? initApplicationService.initialize()
                : searchApplicationService.search(equipmentName, equipmentType, statusCode, parsedDate);
        model.addAttribute("equipmentItems", result.equipmentItems());
        model.addAttribute("equipmentTypeOptions", result.equipmentTypeOptions());
        model.addAttribute("statusOptions", result.statusOptions());
        model.addAttribute("equipmentName", normalize(equipmentName));
        model.addAttribute("equipmentType", normalize(equipmentType));
        model.addAttribute("statusCode", normalizeStatus(statusCode));
        model.addAttribute("systemRegisteredDate", systemRegisteredDate == null ? "" : systemRegisteredDate);
        model.addAttribute("warningMessage", result.hasMoreThanLimit() ? i18nMessageResolver().getBusinessMessage(TOO_MANY_RESULTS_MESSAGE_ID) : null);
        model.addAttribute("infoMessage", resolveInfoMessage(result, messageId));
        model.addAttribute("errorMessage", resolveMessage(resolvedErrorMessageId));
        return Views.HFP_ELV600_ADMIN_EQUIPMENT_SEARCH;
    }

    private String resolveInfoMessage(SearchAdminEquipmentQueryService.Response result, String messageId) {
        if (!result.hasMoreThanLimit() && result.equipmentItems().isEmpty()) {
            return i18nMessageResolver().getBusinessMessage(NO_RESULTS_MESSAGE_ID);
        }
        return resolveMessage(messageId);
    }

    private LocalDate parseDate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException ex) {
            return null;
        }
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }

    private String normalizeStatus(String value) {
        String normalized = normalize(value);
        return normalized.isBlank() ? "ALL" : normalized;
    }
}
