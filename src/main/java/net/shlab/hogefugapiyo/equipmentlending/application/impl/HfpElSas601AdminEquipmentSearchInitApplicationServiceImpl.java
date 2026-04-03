package net.shlab.hogefugapiyo.equipmentlending.application.impl;

import net.shlab.hogefugapiyo.equipmentlending.application.HfpElSas601AdminEquipmentSearchInitApplicationService;
import net.shlab.hogefugapiyo.equipmentlending.application.query.SearchAdminEquipmentQueryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class HfpElSas601AdminEquipmentSearchInitApplicationServiceImpl implements HfpElSas601AdminEquipmentSearchInitApplicationService {

    private final SearchAdminEquipmentQueryService searchAdminEquipmentQueryService;

    public HfpElSas601AdminEquipmentSearchInitApplicationServiceImpl(SearchAdminEquipmentQueryService searchAdminEquipmentQueryService) {
        this.searchAdminEquipmentQueryService = searchAdminEquipmentQueryService;
    }

    @Override
    public SearchAdminEquipmentQueryService.Response initialize() {
        return searchAdminEquipmentQueryService.execute(new SearchAdminEquipmentQueryService.Request("", "", "ALL", null, null));
    }
}
