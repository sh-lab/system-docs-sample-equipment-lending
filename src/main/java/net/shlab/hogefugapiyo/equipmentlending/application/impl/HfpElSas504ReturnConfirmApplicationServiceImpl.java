package net.shlab.hogefugapiyo.equipmentlending.application.impl;

import net.shlab.hogefugapiyo.equipmentlending.application.HfpElSas504ReturnConfirmApplicationService;
import net.shlab.hogefugapiyo.equipmentlending.application.command.ReturnConfirmCommandService;
import net.shlab.hogefugapiyo.equipmentlending.application.command.ReturnConfirmCommandService.Request;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * {@inheritDoc}
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class HfpElSas504ReturnConfirmApplicationServiceImpl implements HfpElSas504ReturnConfirmApplicationService {

    private final ReturnConfirmCommandService returnConfirmCommandService;

    public HfpElSas504ReturnConfirmApplicationServiceImpl(ReturnConfirmCommandService returnConfirmCommandService) {
        this.returnConfirmCommandService = returnConfirmCommandService;
    }

    @Override
    public void confirm(String adminUserId, long lendingRequestId, String returnConfirmComment, int version) {
        returnConfirmCommandService.execute(
                new Request(adminUserId, lendingRequestId, returnConfirmComment, version)
        );
    }
}
