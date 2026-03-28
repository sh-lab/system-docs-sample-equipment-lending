package net.shlab.hogefugapiyo.equipmentlending.presentation;

import net.shlab.hogefugapiyo.equipmentlending.application.query.SearchEquipmentQueryService;
import net.shlab.hogefugapiyo.equipmentlending.application.HfpElSas301EquipmentSearchInitApplicationService;
import net.shlab.hogefugapiyo.equipmentlending.application.HfpElSas302SearchEquipmentApplicationService;
import net.shlab.hogefugapiyo.equipmentlending.application.HfpElSas303StartLendingRequestApplicationService;
import net.shlab.hogefugapiyo.equipmentlending.presentation.route.RoutePaths;
import net.shlab.hogefugapiyo.equipmentlending.presentation.views.Views;
import net.shlab.hogefugapiyo.framework.core.controller.AbstractBaseController;
import net.shlab.hogefugapiyo.framework.i18n.I18nMessageResolver;
import net.shlab.hogefugapiyo.framework.security.UserPrincipal;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 備品検索画面の表示と検索条件受付を担当する Controller。
 *
 * <p>主に HFP-EL-SAS301、HFP-EL-SAS302 および HFP-EL-SAS303 の画面用アプリケーションサービスへ処理を委譲する。
 *
 * <p>設計書:
 * <ul>
 *   <li>{@code docs/03_designs/ui/HFP-EL-V300_equipment-search.md}</li>
 * </ul>
 */
@Controller
@PreAuthorize("hasRole('USER')")
public class HfpElV300EquipmentSearchController extends AbstractBaseController {

    private static final String DEFAULT_LENDING_STATUS = "AVAILABLE";
    private static final String ALL_LENDING_STATUS = "ALL";
    private static final String UNAVAILABLE_LENDING_STATUS = "UNAVAILABLE";
    private static final String TOO_MANY_RESULTS_MESSAGE_ID = "MSG_W_002";
    private static final String NO_RESULTS_MESSAGE_ID = "MSG_I_001";
    private static final String FROM_EQUIPMENT_SEARCH = "V300";

    private final HfpElSas301EquipmentSearchInitApplicationService equipmentSearchInitApplicationService;
    private final HfpElSas302SearchEquipmentApplicationService searchEquipmentApplicationService;
    private final HfpElSas303StartLendingRequestApplicationService startLendingRequestApplicationService;
    private final I18nMessageResolver i18nMessageResolver;

    public HfpElV300EquipmentSearchController(
            HfpElSas301EquipmentSearchInitApplicationService equipmentSearchInitApplicationService,
            HfpElSas302SearchEquipmentApplicationService searchEquipmentApplicationService,
            HfpElSas303StartLendingRequestApplicationService startLendingRequestApplicationService,
            I18nMessageResolver i18nMessageResolver
    ) {
        this.equipmentSearchInitApplicationService = equipmentSearchInitApplicationService;
        this.searchEquipmentApplicationService = searchEquipmentApplicationService;
        this.startLendingRequestApplicationService = startLendingRequestApplicationService;
        this.i18nMessageResolver = i18nMessageResolver;
    }

    @GetMapping(RoutePaths.HFP_ELV300_EQUIPMENT_SEARCH)
    public String show(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            Model model,
            @RequestParam(value = "equipmentName", required = false) String equipmentName,
            @RequestParam(value = "equipmentType", required = false) String equipmentType,
            @RequestParam(value = "lendingStatus", required = false) String lendingStatus,
            @RequestParam(value = "errorMessageId", required = false) String errorMessageId
    ) {
        boolean initialDisplay = equipmentName == null && equipmentType == null && lendingStatus == null;
        SearchEquipmentQueryService.Response result = initialDisplay
                ? equipmentSearchInitApplicationService.initialize()
                : searchEquipmentApplicationService.search(equipmentName, equipmentType, lendingStatus);

        String normalizedEquipmentName = equipmentName == null ? "" : equipmentName.trim();
        String normalizedEquipmentType = equipmentType == null ? "" : equipmentType.trim();
        String normalizedLendingStatus = normalizeLendingStatus(lendingStatus, initialDisplay);

        model.addAttribute("equipmentItems", result.equipmentItems());
        model.addAttribute("equipmentTypeOptions", result.equipmentTypeOptions());
        model.addAttribute("equipmentName", normalizedEquipmentName);
        model.addAttribute("equipmentType", normalizedEquipmentType);
        model.addAttribute("lendingStatus", normalizedLendingStatus);
        model.addAttribute(
                "warningMessage",
                result.hasMoreThanLimit() ? i18nMessageResolver.getBusinessMessage(TOO_MANY_RESULTS_MESSAGE_ID) : null
        );
        model.addAttribute(
                "infoMessage",
                !result.hasMoreThanLimit() && result.equipmentItems().isEmpty()
                        ? i18nMessageResolver.getBusinessMessage(NO_RESULTS_MESSAGE_ID)
                        : null
        );
        model.addAttribute(
                "errorMessage",
                errorMessageId == null || errorMessageId.isBlank() ? null : i18nMessageResolver.getBusinessMessage(errorMessageId)
        );
        return Views.HFP_ELV300_EQUIPMENT_SEARCH;
    }

    @PostMapping(RoutePaths.HFP_ELV300_EQUIPMENT_SEARCH_LENDING_START)
    public String startLendingRequest(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam("equipmentIds") java.util.List<Long> equipmentIds,
            @RequestParam(value = "equipmentName", required = false) String equipmentName,
            @RequestParam(value = "equipmentType", required = false) String equipmentType,
            @RequestParam(value = "lendingStatus", required = false) String lendingStatus
    ) {
        try {
            java.util.List<Long> selectedEquipmentIds = startLendingRequestApplicationService.start(equipmentIds);
            UriComponentsBuilder builder = UriComponentsBuilder.fromPath(RoutePaths.HFP_ELV400_USER_LENDING_REQUEST)
                    .queryParam("from", FROM_EQUIPMENT_SEARCH);
            selectedEquipmentIds.forEach(equipmentId -> builder.queryParam("equipmentIds", equipmentId));
            return "redirect:" + builder.toUriString();
        } catch (net.shlab.hogefugapiyo.equipmentlending.application.BusinessException ex) {
            return "redirect:" + UriComponentsBuilder.fromPath(RoutePaths.HFP_ELV300_EQUIPMENT_SEARCH)
                    .queryParam("equipmentName", normalizeSearchValue(equipmentName))
                    .queryParam("equipmentType", normalizeSearchValue(equipmentType))
                    .queryParam("lendingStatus", normalizeLendingStatus(lendingStatus, false))
                    .queryParam("errorMessageId", ex.messageId())
                    .toUriString();
        }
    }

    private String normalizeLendingStatus(String lendingStatus, boolean initialDisplay) {
        if (initialDisplay || lendingStatus == null || lendingStatus.isBlank()) {
            return DEFAULT_LENDING_STATUS;
        }
        if (ALL_LENDING_STATUS.equals(lendingStatus)
                || UNAVAILABLE_LENDING_STATUS.equals(lendingStatus)
                || DEFAULT_LENDING_STATUS.equals(lendingStatus)) {
            return lendingStatus;
        }
        return DEFAULT_LENDING_STATUS;
    }

    private String normalizeSearchValue(String value) {
        return value == null ? "" : value.trim();
    }
}
