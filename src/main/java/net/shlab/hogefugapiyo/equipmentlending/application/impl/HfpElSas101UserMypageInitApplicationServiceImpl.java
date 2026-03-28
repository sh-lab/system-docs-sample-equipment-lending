package net.shlab.hogefugapiyo.equipmentlending.application.impl;

import net.shlab.hogefugapiyo.equipmentlending.application.HfpElSas101UserMypageInitApplicationService;
import net.shlab.hogefugapiyo.equipmentlending.application.query.FindUserMypageQueryService;
import net.shlab.hogefugapiyo.equipmentlending.application.query.FindUserMypageQueryService.Request;
import net.shlab.hogefugapiyo.equipmentlending.application.query.FindUserMypageQueryService.Response;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * {@inheritDoc}
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class HfpElSas101UserMypageInitApplicationServiceImpl implements HfpElSas101UserMypageInitApplicationService {

    private final FindUserMypageQueryService findUserMypageQueryService;

    public HfpElSas101UserMypageInitApplicationServiceImpl(FindUserMypageQueryService findUserMypageQueryService) {
        this.findUserMypageQueryService = findUserMypageQueryService;
    }

    @Override
    public Response initialize(String userId) {
        return findUserMypageQueryService.execute(new Request(userId));
    }
}
