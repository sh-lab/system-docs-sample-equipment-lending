package net.shlab.hogefugapiyo.equipmentlending.application.impl;

import net.shlab.hogefugapiyo.equipmentlending.application.HfpElSas302SearchEquipmentApplicationService;
import net.shlab.hogefugapiyo.equipmentlending.application.query.SearchEquipmentQueryService;
import net.shlab.hogefugapiyo.equipmentlending.application.query.SearchEquipmentQueryService.Request;
import net.shlab.hogefugapiyo.equipmentlending.application.query.SearchEquipmentQueryService.Response;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * {@inheritDoc}
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class HfpElSas302SearchEquipmentApplicationServiceImpl implements HfpElSas302SearchEquipmentApplicationService {

    private static final String DEFAULT_LENDING_STATUS = "AVAILABLE";
    private static final String NOT_AVAILABLE_LENDING_STATUS = "NOT_AVAILABLE";

    private final SearchEquipmentQueryService searchEquipmentQueryService;

    public HfpElSas302SearchEquipmentApplicationServiceImpl(SearchEquipmentQueryService searchEquipmentQueryService) {
        this.searchEquipmentQueryService = searchEquipmentQueryService;
    }

    @Override
    public Response search(String equipmentName, String equipmentType, String lendingStatus) {
        return searchEquipmentQueryService.execute(new Request(
                normalize(equipmentName),
                normalize(equipmentType),
                normalizeStatus(lendingStatus)
        ));
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }

    private String normalizeStatus(String lendingStatus) {
        String normalized = normalize(lendingStatus);
        if ("ALL".equals(normalized)
                || NOT_AVAILABLE_LENDING_STATUS.equals(normalized)
                || DEFAULT_LENDING_STATUS.equals(normalized)) {
            return normalized;
        }
        return DEFAULT_LENDING_STATUS;
    }
}
