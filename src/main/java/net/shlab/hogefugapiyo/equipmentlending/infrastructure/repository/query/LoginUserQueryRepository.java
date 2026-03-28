package net.shlab.hogefugapiyo.equipmentlending.infrastructure.repository.query;

import java.util.Optional;
import net.shlab.hogefugapiyo.equipmentlending.application.query.FindLoginUserQueryService;
import net.shlab.hogefugapiyo.framework.core.repository.QueryRepository;

public interface LoginUserQueryRepository extends QueryRepository {
    Optional<FindLoginUserQueryService.Response> findByUserId(String userId);
}
