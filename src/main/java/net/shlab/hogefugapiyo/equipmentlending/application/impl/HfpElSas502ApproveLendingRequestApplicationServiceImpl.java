package net.shlab.hogefugapiyo.equipmentlending.application.impl;

import net.shlab.hogefugapiyo.equipmentlending.application.HfpElSas502ApproveLendingRequestApplicationService;
import net.shlab.hogefugapiyo.equipmentlending.application.command.ApproveLendingRequestCommandService;
import net.shlab.hogefugapiyo.equipmentlending.application.command.ApproveLendingRequestCommandService.Request;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * {@inheritDoc}
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class HfpElSas502ApproveLendingRequestApplicationServiceImpl
        implements HfpElSas502ApproveLendingRequestApplicationService {

    private final ApproveLendingRequestCommandService approveLendingRequestCommandService;

    public HfpElSas502ApproveLendingRequestApplicationServiceImpl(
            ApproveLendingRequestCommandService approveLendingRequestCommandService
    ) {
        this.approveLendingRequestCommandService = approveLendingRequestCommandService;
    }

    @Override
    public void approve(String adminUserId, long lendingRequestId, String reviewComment, int version) {
        approveLendingRequestCommandService.execute(
                new Request(adminUserId, lendingRequestId, reviewComment, version)
        );
    }
}
