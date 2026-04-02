package net.shlab.hogefugapiyo.equipmentlending.presentation;

import jakarta.validation.Valid;
import net.shlab.hogefugapiyo.equipmentlending.application.BusinessException;
import net.shlab.hogefugapiyo.equipmentlending.application.BusinessMessageIds;
import net.shlab.hogefugapiyo.equipmentlending.application.HfpElSas701AdminEquipmentEditInitApplicationService;
import net.shlab.hogefugapiyo.equipmentlending.application.HfpElSas702RegisterEquipmentApplicationService;
import net.shlab.hogefugapiyo.equipmentlending.application.HfpElSas703UpdateEquipmentInfoApplicationService;
import net.shlab.hogefugapiyo.equipmentlending.application.query.FindAdminEquipmentEditQueryService;
import net.shlab.hogefugapiyo.equipmentlending.presentation.form.AdminEquipmentRegisterForm;
import net.shlab.hogefugapiyo.equipmentlending.presentation.form.AdminEquipmentUpdateForm;
import net.shlab.hogefugapiyo.equipmentlending.presentation.route.RoutePaths;
import net.shlab.hogefugapiyo.equipmentlending.presentation.views.Views;
import net.shlab.hogefugapiyo.equipmentlending.presentation.controller.AbstractBaseController;
import net.shlab.hogefugapiyo.framework.i18n.I18nMessageResolver;
import net.shlab.hogefugapiyo.equipmentlending.infrastructure.security.UserPrincipal;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@PreAuthorize("hasRole('ADMIN')")
public class HfpElV700AdminEquipmentEditController extends AbstractBaseController {

    private final HfpElSas701AdminEquipmentEditInitApplicationService initApplicationService;
    private final HfpElSas702RegisterEquipmentApplicationService registerApplicationService;
    private final HfpElSas703UpdateEquipmentInfoApplicationService updateApplicationService;

    public HfpElV700AdminEquipmentEditController(
            HfpElSas701AdminEquipmentEditInitApplicationService initApplicationService,
            HfpElSas702RegisterEquipmentApplicationService registerApplicationService,
            HfpElSas703UpdateEquipmentInfoApplicationService updateApplicationService,
            I18nMessageResolver i18nMessageResolver
    ) {
        super(i18nMessageResolver);
        this.initApplicationService = initApplicationService;
        this.registerApplicationService = registerApplicationService;
        this.updateApplicationService = updateApplicationService;
    }

    @GetMapping(RoutePaths.HFP_ELV700_ADMIN_EQUIPMENT_EDIT)
    public String show(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            Model model,
            @RequestParam(value = "mode", required = false, defaultValue = "edit") String mode,
            @RequestParam(value = "equipmentId", required = false) Long equipmentId,
            @RequestParam(value = "errorMessageId", required = false) String errorMessageId
    ) {
        try {
            populateModel(model, initApplicationService.initialize(normalizeMode(mode), equipmentId), null, resolveMessage(errorMessageId));
            return Views.HFP_ELV700_ADMIN_EQUIPMENT_EDIT;
        } catch (BusinessException ex) {
            return redirectWithError(RoutePaths.HFP_ELV600_ADMIN_EQUIPMENT_SEARCH, ex.messageId());
        }
    }

    @PostMapping(RoutePaths.HFP_ELV700_ADMIN_EQUIPMENT_EDIT_REGISTER)
    public String register(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            Model model,
            @Valid @ModelAttribute("createForm") AdminEquipmentRegisterForm form,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            FindAdminEquipmentEditQueryService.Response viewData = initApplicationService.initialize("create", null);
            populateModel(model, viewData, null, null);
            ValidationErrorSupport.populate(model, bindingResult);
            applyCreateInput(model, form);
            return Views.HFP_ELV700_ADMIN_EQUIPMENT_EDIT;
        }
        try {
            registerApplicationService.register(
                    userPrincipal.userId(),
                    form.getEquipmentName(),
                    form.getEquipmentType(),
                    form.getStorageLocation(),
                    form.getStatusCode(),
                    form.getRemarks()
            );
            return redirectWithMessage(RoutePaths.HFP_ELV600_ADMIN_EQUIPMENT_SEARCH, BusinessMessageIds.EQUIPMENT_REGISTER_COMPLETED);
        } catch (BusinessException ex) {
            FindAdminEquipmentEditQueryService.Response viewData = initApplicationService.initialize("create", null);
            populateModel(model, viewData, null, resolveMessage(ex.messageId()));
            applyCreateInput(model, form);
            return Views.HFP_ELV700_ADMIN_EQUIPMENT_EDIT;
        }
    }

    @PostMapping(RoutePaths.HFP_ELV700_ADMIN_EQUIPMENT_EDIT_UPDATE)
    public String update(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            Model model,
            @Valid @ModelAttribute("updateForm") AdminEquipmentUpdateForm form,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            if (form.getEquipmentId() == null) {
                return redirectWithError(RoutePaths.HFP_ELV600_ADMIN_EQUIPMENT_SEARCH, BusinessMessageIds.EQUIPMENT_DISPLAY_INVALID);
            }
            try {
                FindAdminEquipmentEditQueryService.Response viewData = initApplicationService.initialize("edit", form.getEquipmentId());
                populateModel(model, viewData, null, null);
                ValidationErrorSupport.populate(model, bindingResult);
                applyUpdateInput(model, form);
                return Views.HFP_ELV700_ADMIN_EQUIPMENT_EDIT;
            } catch (BusinessException ex) {
                return redirectWithError(RoutePaths.HFP_ELV600_ADMIN_EQUIPMENT_SEARCH, ex.messageId());
            }
        }
        try {
            updateApplicationService.update(
                    userPrincipal.userId(),
                    form.getEquipmentId(),
                    form.getEquipmentName(),
                    form.getStatusCode(),
                    form.getRemarks(),
                    form.getVersion()
            );
            return redirectWithMessage(RoutePaths.HFP_ELV600_ADMIN_EQUIPMENT_SEARCH, BusinessMessageIds.EQUIPMENT_STATUS_UPDATE_COMPLETED);
        } catch (BusinessException ex) {
            try {
                FindAdminEquipmentEditQueryService.Response viewData = initApplicationService.initialize("edit", form.getEquipmentId());
                populateModel(model, viewData, null, resolveMessage(ex.messageId()));
                applyUpdateInput(model, form);
                return Views.HFP_ELV700_ADMIN_EQUIPMENT_EDIT;
            } catch (BusinessException nested) {
                return redirectWithError(RoutePaths.HFP_ELV600_ADMIN_EQUIPMENT_SEARCH, nested.messageId());
            }
        }
    }

    private void populateModel(Model model, FindAdminEquipmentEditQueryService.Response viewData, String infoMessage, String errorMessage) {
        model.addAttribute("viewData", viewData);
        model.addAttribute("isCreateMode", "create".equals(viewData.mode()));
        model.addAttribute("isEditMode", "edit".equals(viewData.mode()));
        model.addAttribute("equipmentTypeOptions", viewData.equipmentTypeOptions());
        model.addAttribute("statusOptions", viewData.statusOptions());
        model.addAttribute("infoMessage", infoMessage);
        model.addAttribute("errorMessage", errorMessage);
    }

    private String normalizeMode(String mode) {
        return "create".equals(mode) ? "create" : "edit";
    }

    private void applyCreateInput(Model model, AdminEquipmentRegisterForm form) {
        model.addAttribute("inputEquipmentName", form.getEquipmentName());
        model.addAttribute("inputEquipmentType", form.getEquipmentType());
        model.addAttribute("inputStorageLocation", form.getStorageLocation());
        model.addAttribute("inputStatusCode", form.getStatusCode());
        model.addAttribute("inputRemarks", form.getRemarks() == null ? "" : form.getRemarks());
    }

    private void applyUpdateInput(Model model, AdminEquipmentUpdateForm form) {
        model.addAttribute("inputEquipmentName", form.getEquipmentName());
        model.addAttribute("inputStatusCode", form.getStatusCode());
        model.addAttribute("inputRemarks", form.getRemarks() == null ? "" : form.getRemarks());
    }
}
