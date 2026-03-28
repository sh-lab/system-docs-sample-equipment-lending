package net.shlab.hogefugapiyo.equipmentlending.application.impl;

import java.util.List;
import net.shlab.hogefugapiyo.equipmentlending.application.HfpElSas402LendingRequestApplicationService;
import net.shlab.hogefugapiyo.equipmentlending.application.command.RegisterLendingRequestCommandService;
import net.shlab.hogefugapiyo.equipmentlending.application.command.RegisterLendingRequestCommandService.Request;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * {@inheritDoc}
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class HfpElSas402LendingRequestApplicationServiceImpl implements HfpElSas402LendingRequestApplicationService {

    private final RegisterLendingRequestCommandService registerLendingRequestCommandService;

    public HfpElSas402LendingRequestApplicationServiceImpl(
            RegisterLendingRequestCommandService registerLendingRequestCommandService
    ) {
        this.registerLendingRequestCommandService = registerLendingRequestCommandService;
    }

    @Override
    public void register(String userId, List<Long> equipmentIds, String requestComment) {
        registerLendingRequestCommandService.execute(new Request(userId, equipmentIds, requestComment));
    }
}
