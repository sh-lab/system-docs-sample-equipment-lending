package net.shlab.hogefugapiyo.equipmentlending.application.impl;

import net.shlab.hogefugapiyo.equipmentlending.application.HfpElSas503RejectLendingRequestApplicationService;
import net.shlab.hogefugapiyo.equipmentlending.application.command.RejectLendingRequestCommandService;
import net.shlab.hogefugapiyo.equipmentlending.application.command.RejectLendingRequestCommandService.Request;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * {@inheritDoc}
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class HfpElSas503RejectLendingRequestApplicationServiceImpl
        implements HfpElSas503RejectLendingRequestApplicationService {

    private final RejectLendingRequestCommandService rejectLendingRequestCommandService;

    public HfpElSas503RejectLendingRequestApplicationServiceImpl(
            RejectLendingRequestCommandService rejectLendingRequestCommandService
    ) {
        this.rejectLendingRequestCommandService = rejectLendingRequestCommandService;
    }

    @Override
    public void reject(String adminUserId, long lendingRequestId, String reviewComment, int version) {
        rejectLendingRequestCommandService.execute(
                new Request(adminUserId, lendingRequestId, reviewComment, version)
        );
    }
}
