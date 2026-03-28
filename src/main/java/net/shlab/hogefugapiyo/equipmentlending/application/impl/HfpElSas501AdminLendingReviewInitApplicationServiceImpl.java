package net.shlab.hogefugapiyo.equipmentlending.application.impl;

import net.shlab.hogefugapiyo.equipmentlending.application.HfpElSas501AdminLendingReviewInitApplicationService;
import net.shlab.hogefugapiyo.equipmentlending.application.query.FindAdminLendingReviewQueryService;
import net.shlab.hogefugapiyo.equipmentlending.application.query.FindAdminLendingReviewQueryService.Request;
import net.shlab.hogefugapiyo.equipmentlending.application.query.FindAdminLendingReviewQueryService.Response;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * {@inheritDoc}
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class HfpElSas501AdminLendingReviewInitApplicationServiceImpl
        implements HfpElSas501AdminLendingReviewInitApplicationService {

    private final FindAdminLendingReviewQueryService findAdminLendingReviewQueryService;

    public HfpElSas501AdminLendingReviewInitApplicationServiceImpl(
            FindAdminLendingReviewQueryService findAdminLendingReviewQueryService
    ) {
        this.findAdminLendingReviewQueryService = findAdminLendingReviewQueryService;
    }

    @Override
    public Response initialize(String adminUserId, Long lendingRequestId) {
        return findAdminLendingReviewQueryService.execute(new Request(adminUserId, lendingRequestId));
    }
}
