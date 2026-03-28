package net.shlab.hogefugapiyo.equipmentlending.application.impl;

import net.shlab.hogefugapiyo.equipmentlending.application.HfpElSas201AdminMypageInitApplicationService;
import net.shlab.hogefugapiyo.equipmentlending.application.query.FindAdminMypageQueryService;
import net.shlab.hogefugapiyo.equipmentlending.application.query.FindAdminMypageQueryService.Request;
import net.shlab.hogefugapiyo.equipmentlending.application.query.FindAdminMypageQueryService.Response;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * {@inheritDoc}
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class HfpElSas201AdminMypageInitApplicationServiceImpl implements HfpElSas201AdminMypageInitApplicationService {

    private final FindAdminMypageQueryService findAdminMypageQueryService;

    public HfpElSas201AdminMypageInitApplicationServiceImpl(FindAdminMypageQueryService findAdminMypageQueryService) {
        this.findAdminMypageQueryService = findAdminMypageQueryService;
    }

    @Override
    public Response initialize(String adminUserId) {
        return findAdminMypageQueryService.execute(new Request(adminUserId));
    }
}
