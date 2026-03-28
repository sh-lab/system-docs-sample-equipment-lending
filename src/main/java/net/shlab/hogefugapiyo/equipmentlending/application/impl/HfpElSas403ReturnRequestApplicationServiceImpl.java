package net.shlab.hogefugapiyo.equipmentlending.application.impl;

import net.shlab.hogefugapiyo.equipmentlending.application.HfpElSas403ReturnRequestApplicationService;
import net.shlab.hogefugapiyo.equipmentlending.application.command.RegisterReturnRequestCommandService;
import net.shlab.hogefugapiyo.equipmentlending.application.command.RegisterReturnRequestCommandService.Request;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * {@inheritDoc}
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class HfpElSas403ReturnRequestApplicationServiceImpl implements HfpElSas403ReturnRequestApplicationService {

    private final RegisterReturnRequestCommandService registerReturnRequestCommandService;

    public HfpElSas403ReturnRequestApplicationServiceImpl(RegisterReturnRequestCommandService registerReturnRequestCommandService) {
        this.registerReturnRequestCommandService = registerReturnRequestCommandService;
    }

    @Override
    public void register(String userId, long lendingRequestId, String returnRequestComment, int version) {
        registerReturnRequestCommandService.execute(
                new Request(userId, lendingRequestId, returnRequestComment, version)
        );
    }
}
