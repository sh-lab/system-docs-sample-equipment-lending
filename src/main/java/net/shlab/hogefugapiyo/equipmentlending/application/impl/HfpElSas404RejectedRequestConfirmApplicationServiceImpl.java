package net.shlab.hogefugapiyo.equipmentlending.application.impl;

import net.shlab.hogefugapiyo.equipmentlending.application.HfpElSas404RejectedRequestConfirmApplicationService;
import net.shlab.hogefugapiyo.equipmentlending.application.command.ConfirmRejectedRequestCommandService;
import net.shlab.hogefugapiyo.equipmentlending.application.command.ConfirmRejectedRequestCommandService.Request;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * {@inheritDoc}
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class HfpElSas404RejectedRequestConfirmApplicationServiceImpl implements HfpElSas404RejectedRequestConfirmApplicationService {

    private final ConfirmRejectedRequestCommandService confirmRejectedRequestCommandService;

    public HfpElSas404RejectedRequestConfirmApplicationServiceImpl(
            ConfirmRejectedRequestCommandService confirmRejectedRequestCommandService
    ) {
        this.confirmRejectedRequestCommandService = confirmRejectedRequestCommandService;
    }

    @Override
    public void confirm(String userId, long lendingRequestId, int version) {
        confirmRejectedRequestCommandService.execute(new Request(userId, lendingRequestId, version));
    }
}
