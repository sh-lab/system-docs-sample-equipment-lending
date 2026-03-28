package net.shlab.hogefugapiyo.equipmentlending.application.impl;

import net.shlab.hogefugapiyo.equipmentlending.application.HfpElSas301EquipmentSearchInitApplicationService;
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
public class HfpElSas301EquipmentSearchInitApplicationServiceImpl implements HfpElSas301EquipmentSearchInitApplicationService {

    private static final String DEFAULT_LENDING_STATUS = "AVAILABLE";

    private final SearchEquipmentQueryService searchEquipmentQueryService;

    public HfpElSas301EquipmentSearchInitApplicationServiceImpl(SearchEquipmentQueryService searchEquipmentQueryService) {
        this.searchEquipmentQueryService = searchEquipmentQueryService;
    }

    @Override
    public Response initialize() {
        return searchEquipmentQueryService.execute(new Request("", "", DEFAULT_LENDING_STATUS));
    }
}
