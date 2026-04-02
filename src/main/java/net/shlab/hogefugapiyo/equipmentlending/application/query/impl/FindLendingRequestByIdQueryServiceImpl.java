package net.shlab.hogefugapiyo.equipmentlending.application.query.impl;

import java.util.List;
import net.shlab.hogefugapiyo.equipmentlending.application.BusinessException;
import net.shlab.hogefugapiyo.equipmentlending.application.BusinessMessageIds;
import net.shlab.hogefugapiyo.equipmentlending.application.query.FindLendingRequestByIdQueryService;
import net.shlab.hogefugapiyo.equipmentlending.infrastructure.repository.entity.LendingRequestRepository;
import net.shlab.hogefugapiyo.equipmentlending.model.entity.LendingRequest;
import org.springframework.stereotype.Service;

/**
 * {@inheritDoc}
 */
@Service
public class FindLendingRequestByIdQueryServiceImpl implements FindLendingRequestByIdQueryService {

    private final LendingRequestRepository lendingRequestRepository;

    public FindLendingRequestByIdQueryServiceImpl(LendingRequestRepository lendingRequestRepository) {
        this.lendingRequestRepository = lendingRequestRepository;
    }

    @Override
    public FindLendingRequestByIdQueryService.Response execute(FindLendingRequestByIdQueryService.Request request) {
        LendingRequest entity = lendingRequestRepository.findById(request.lendingRequestId())
                .orElseThrow(() -> new BusinessException(BusinessMessageIds.REQUEST_DISPLAY_INVALID));
        List<Long> equipmentIds = lendingRequestRepository
                .findEquipmentIdsByLendingRequestId(request.lendingRequestId());
        return new FindLendingRequestByIdQueryService.Response(
                entity.lendingRequestId(),
                entity.applicantUserId(),
                entity.statusCode(),
                entity.requestedAt(),
                entity.reviewedAt(),
                entity.returnRequestedAt(),
                entity.requestComment(),
                entity.returnRequestComment(),
                entity.reviewComment(),
                entity.version(),
                equipmentIds
        );
    }
}
