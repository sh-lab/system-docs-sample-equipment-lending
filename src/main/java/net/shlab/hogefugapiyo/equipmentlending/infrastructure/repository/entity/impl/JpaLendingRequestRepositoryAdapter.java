package net.shlab.hogefugapiyo.equipmentlending.infrastructure.repository.entity.impl;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import net.shlab.hogefugapiyo.equipmentlending.infrastructure.repository.entity.LendingRequestRepository;
import net.shlab.hogefugapiyo.equipmentlending.infrastructure.repository.entity.jpa.LendingRequestSpringDataJpaRepository;
import net.shlab.hogefugapiyo.equipmentlending.model.entity.LendingRequest;
import net.shlab.hogefugapiyo.equipmentlending.model.entity.LendingRequestDetail;
import org.springframework.stereotype.Repository;

/**
 * {@inheritDoc}
 */
@Repository
public class JpaLendingRequestRepositoryAdapter implements LendingRequestRepository {

    private final LendingRequestSpringDataJpaRepository lendingRequestSpringDataJpaRepository;

    public JpaLendingRequestRepositoryAdapter(
            LendingRequestSpringDataJpaRepository lendingRequestSpringDataJpaRepository
    ) {
        this.lendingRequestSpringDataJpaRepository = lendingRequestSpringDataJpaRepository;
    }

    @Override
    public long nextId() {
        Long nextId = lendingRequestSpringDataJpaRepository.nextId();
        if (nextId == null) {
            throw new IllegalStateException("Failed to allocate lending request id from sequence.");
        }
        return nextId;
    }

    @Override
    public void saveDetails(long lendingRequestId, List<Long> equipmentIds, String createdBy, LocalDateTime createdAt) {
        LendingRequest request = lendingRequestSpringDataJpaRepository.findById(lendingRequestId)
                .orElseThrow(() -> new IllegalStateException("Failed to find lending request: " + lendingRequestId));
        var auditAt = createdAt.toInstant(ZoneOffset.UTC);
        request.replaceDetails(
                equipmentIds.stream()
                        .map(equipmentId -> new LendingRequestDetail(
                                equipmentId,
                                auditAt,
                                createdBy,
                                auditAt,
                                createdBy
                        ))
                        .toList()
        );
        lendingRequestSpringDataJpaRepository.save(request);
    }

    @Override
    public Optional<LendingRequest> findById(Long lendingRequestId) {
        return lendingRequestSpringDataJpaRepository.findById(lendingRequestId);
    }

    @Override
    public List<Long> findEquipmentIdsByLendingRequestId(long lendingRequestId) {
        return lendingRequestSpringDataJpaRepository.findEquipmentIdsByLendingRequestId(lendingRequestId);
    }

    @Override
    public LendingRequest save(LendingRequest entity) {
        return lendingRequestSpringDataJpaRepository.save(entity);
    }
}
