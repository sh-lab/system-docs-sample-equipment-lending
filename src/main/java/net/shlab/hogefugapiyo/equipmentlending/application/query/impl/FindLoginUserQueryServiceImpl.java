package net.shlab.hogefugapiyo.equipmentlending.application.query.impl;

import java.util.Optional;
import net.shlab.hogefugapiyo.equipmentlending.application.query.FindLoginUserQueryService;
import net.shlab.hogefugapiyo.equipmentlending.infrastructure.repository.query.LoginUserQueryRepository;
import org.springframework.stereotype.Service;

/**
 * {@inheritDoc}
 */
@Service
public class FindLoginUserQueryServiceImpl implements FindLoginUserQueryService {

    private final LoginUserQueryRepository loginUserQueryRepository;

    public FindLoginUserQueryServiceImpl(LoginUserQueryRepository loginUserQueryRepository) {
        this.loginUserQueryRepository = loginUserQueryRepository;
    }

    @Override
    public Optional<FindLoginUserQueryService.Response> execute(FindLoginUserQueryService.Request request) {
        return loginUserQueryRepository.findByUserId(request.userId());
    }
}
