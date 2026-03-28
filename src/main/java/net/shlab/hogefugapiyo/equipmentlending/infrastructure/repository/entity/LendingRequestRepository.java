package net.shlab.hogefugapiyo.equipmentlending.infrastructure.repository.entity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import net.shlab.hogefugapiyo.equipmentlending.model.entity.LendingRequest;
import net.shlab.hogefugapiyo.framework.core.repository.EntityRepository;

/**
 * 貸出申請 Entity の永続化を担当する Entity Repository。
 *
 * <p>設計書:
 * <ul>
 *   <li>{@code docs/03_designs/entity/HFP-EL-E003_lending-request.md}</li>
 *   <li>{@code docs/03_designs/data/T_LENDING_REQUEST.md}</li>
 *   <li>{@code docs/03_designs/data/T_LENDING_REQUEST_DETAIL.md}</li>
 * </ul>
 */
public interface LendingRequestRepository extends EntityRepository<LendingRequest, Long> {

    long nextId();

    void saveDetails(long lendingRequestId, List<Long> equipmentIds, String createdBy, LocalDateTime createdAt);

    List<Long> findEquipmentIdsByLendingRequestId(long lendingRequestId);
}
