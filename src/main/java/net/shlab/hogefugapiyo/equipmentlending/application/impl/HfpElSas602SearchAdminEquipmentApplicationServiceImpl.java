package net.shlab.hogefugapiyo.equipmentlending.application.impl;

import java.time.LocalDate;
import net.shlab.hogefugapiyo.equipmentlending.application.HfpElSas602SearchAdminEquipmentApplicationService;
import net.shlab.hogefugapiyo.equipmentlending.application.query.SearchAdminEquipmentQueryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class HfpElSas602SearchAdminEquipmentApplicationServiceImpl implements HfpElSas602SearchAdminEquipmentApplicationService {

    private final SearchAdminEquipmentQueryService searchAdminEquipmentQueryService;

    public HfpElSas602SearchAdminEquipmentApplicationServiceImpl(SearchAdminEquipmentQueryService searchAdminEquipmentQueryService) {
        this.searchAdminEquipmentQueryService = searchAdminEquipmentQueryService;
    }

    @Override
    public SearchAdminEquipmentQueryService.Response search(
            String equipmentName,
            String equipmentType,
            String statusCode,
            LocalDate systemRegisteredDateFrom,
            LocalDate systemRegisteredDateTo
    ) {
        return searchAdminEquipmentQueryService.execute(new SearchAdminEquipmentQueryService.Request(
                normalize(equipmentName),
                normalize(equipmentType),
                normalizeStatus(statusCode),
                systemRegisteredDateFrom,
                systemRegisteredDateTo
        ));
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }

    private String normalizeStatus(String statusCode) {
        String normalized = normalize(statusCode);
        return normalized.isBlank() ? "ALL" : normalized;
    }
}
