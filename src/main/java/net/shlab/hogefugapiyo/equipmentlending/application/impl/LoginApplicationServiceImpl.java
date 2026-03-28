package net.shlab.hogefugapiyo.equipmentlending.application.impl;

import java.util.Optional;
import net.shlab.hogefugapiyo.equipmentlending.application.LoginApplicationService;
import net.shlab.hogefugapiyo.equipmentlending.application.query.FindLoginUserQueryService;
import net.shlab.hogefugapiyo.equipmentlending.application.query.FindLoginUserQueryService.Request;
import net.shlab.hogefugapiyo.equipmentlending.application.query.FindLoginUserQueryService.Response;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * {@inheritDoc}
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class LoginApplicationServiceImpl implements LoginApplicationService {

    private final FindLoginUserQueryService findLoginUserQueryService;

    public LoginApplicationServiceImpl(FindLoginUserQueryService findLoginUserQueryService) {
        this.findLoginUserQueryService = findLoginUserQueryService;
    }

    @Override
    public Optional<Response> findLoginUser(String userId) {
        return findLoginUserQueryService.execute(new Request(userId));
    }
}
