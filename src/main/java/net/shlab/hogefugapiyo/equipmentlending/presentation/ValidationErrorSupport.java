package net.shlab.hogefugapiyo.equipmentlending.presentation;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

final class ValidationErrorSupport {

    private ValidationErrorSupport() {
    }

    static void populate(Model model, BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            model.addAttribute("validationErrors", List.of());
            model.addAttribute("fieldErrors", Map.of());
            return;
        }
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            fieldErrors.putIfAbsent(fieldError.getField(), fieldError.getDefaultMessage());
        }
        model.addAttribute("validationErrors", List.copyOf(fieldErrors.values()));
        model.addAttribute("fieldErrors", fieldErrors);
    }
}
