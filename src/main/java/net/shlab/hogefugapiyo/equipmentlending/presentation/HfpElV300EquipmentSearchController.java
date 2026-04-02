package net.shlab.hogefugapiyo.equipmentlending.presentation;

import jakarta.validation.Valid;
import net.shlab.hogefugapiyo.equipmentlending.application.query.SearchEquipmentQueryService;
import net.shlab.hogefugapiyo.equipmentlending.application.HfpElSas301EquipmentSearchInitApplicationService;
import net.shlab.hogefugapiyo.equipmentlending.application.HfpElSas302SearchEquipmentApplicationService;
import net.shlab.hogefugapiyo.equipmentlending.application.HfpElSas303StartLendingRequestApplicationService;
import net.shlab.hogefugapiyo.equipmentlending.presentation.form.EquipmentLendingStartForm;
import net.shlab.hogefugapiyo.equipmentlending.presentation.route.RoutePaths;
import net.shlab.hogefugapiyo.equipmentlending.presentation.views.Views;
import net.shlab.hogefugapiyo.equipmentlending.presentation.controller.AbstractBaseController;
import net.shlab.hogefugapiyo.framework.i18n.I18nMessageResolver;
import net.shlab.hogefugapiyo.equipmentlending.infrastructure.security.UserPrincipal;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
    private static final String NOT_AVAILABLE_LENDING_STATUS = "NOT_AVAILABLE";
    private static final String TOO_MANY_RESULTS_MESSAGE_ID = "MSG_W_002";
    private static final String NO_RESULTS_MESSAGE_ID = "MSG_I_001";
    private static final String FROM_EQUIPMENT_SEARCH = "V300";

    private final HfpElSas301EquipmentSearchInitApplicationService equipmentSearchInitApplicationService;
    private final HfpElSas302SearchEquipmentApplicationService searchEquipmentApplicationService;
    private final HfpElSas303StartLendingRequestApplicationService startLendingRequestApplicationService;

    public HfpElV300EquipmentSearchController(
            HfpElSas301EquipmentSearchInitApplicationService equipmentSearchInitApplicationService,
            HfpElSas302SearchEquipmentApplicationService searchEquipmentApplicationService,
            HfpElSas303StartLendingRequestApplicationService startLendingRequestApplicationService,
            I18nMessageResolver i18nMessageResolver
    ) {
        super(i18nMessageResolver);
        this.equipmentSearchInitApplicationService = equipmentSearchInitApplicationService;
        this.searchEquipmentApplicationService = searchEquipmentApplicationService;
        this.startLendingRequestApplicationService = startLendingRequestApplicationService;
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
        renderSearch(model, equipmentName, equipmentType, lendingStatus, resolveMessage(errorMessageId));
        return Views.HFP_ELV300_EQUIPMENT_SEARCH;
    }

    @PostMapping(RoutePaths.HFP_ELV300_EQUIPMENT_SEARCH_LENDING_START)
    public String startLendingRequest(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            Model model,
            @Valid @ModelAttribute("lendingStartForm") EquipmentLendingStartForm form,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            renderSearch(
                    model,
                    form.getEquipmentName(),
                    form.getEquipmentType(),
                    form.getLendingStatus(),
                    null
            );
            ValidationErrorSupport.populate(model, bindingResult);
            return Views.HFP_ELV300_EQUIPMENT_SEARCH;
        }
        try {
            java.util.List<Long> selectedEquipmentIds = startLendingRequestApplicationService.start(form.getEquipmentIds());
            UriComponentsBuilder builder = UriComponentsBuilder.fromPath(RoutePaths.HFP_ELV400_USER_LENDING_REQUEST)
                    .queryParam("from", FROM_EQUIPMENT_SEARCH);
            selectedEquipmentIds.forEach(equipmentId -> builder.queryParam("equipmentIds", equipmentId));
            return "redirect:" + builder.toUriString();
        } catch (net.shlab.hogefugapiyo.equipmentlending.application.BusinessException ex) {
            return "redirect:" + UriComponentsBuilder.fromPath(RoutePaths.HFP_ELV300_EQUIPMENT_SEARCH)
                    .queryParam("equipmentName", normalizeSearchValue(form.getEquipmentName()))
                    .queryParam("equipmentType", normalizeSearchValue(form.getEquipmentType()))
                    .queryParam("lendingStatus", normalizeLendingStatus(form.getLendingStatus(), false))
                    .queryParam("errorMessageId", ex.messageId())
                    .toUriString();
        }
    }

    private void renderSearch(Model model, String equipmentName, String equipmentType, String lendingStatus, String errorMessage) {
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
                result.hasMoreThanLimit() ? i18nMessageResolver().getBusinessMessage(TOO_MANY_RESULTS_MESSAGE_ID) : null
        );
        model.addAttribute(
                "infoMessage",
                !result.hasMoreThanLimit() && result.equipmentItems().isEmpty()
                        ? i18nMessageResolver().getBusinessMessage(NO_RESULTS_MESSAGE_ID)
                        : null
        );
        model.addAttribute("errorMessage", errorMessage);
    }

    private String normalizeLendingStatus(String lendingStatus, boolean initialDisplay) {
        if (initialDisplay || lendingStatus == null || lendingStatus.isBlank()) {
            return DEFAULT_LENDING_STATUS;
        }
        if (ALL_LENDING_STATUS.equals(lendingStatus)
                || NOT_AVAILABLE_LENDING_STATUS.equals(lendingStatus)
                || DEFAULT_LENDING_STATUS.equals(lendingStatus)) {
            return lendingStatus;
        }
        return DEFAULT_LENDING_STATUS;
    }

    private String normalizeSearchValue(String value) {
        return value == null ? "" : value.trim();
    }
}
