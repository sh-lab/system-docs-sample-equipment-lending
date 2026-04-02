package net.shlab.hogefugapiyo.equipmentlending.application.command.impl;

import java.util.List;
import java.util.Set;
import net.shlab.hogefugapiyo.equipmentlending.application.BusinessException;
import net.shlab.hogefugapiyo.equipmentlending.application.BusinessMessageIds;
import net.shlab.hogefugapiyo.equipmentlending.application.command.RegisterLendingRequestCommandService;
import net.shlab.hogefugapiyo.equipmentlending.application.pure.CheckLendingRequestAvailabilityService;
import net.shlab.hogefugapiyo.equipmentlending.application.pure.EquipmentAvailabilityInput;
import net.shlab.hogefugapiyo.equipmentlending.infrastructure.repository.entity.EquipmentRepository;
import net.shlab.hogefugapiyo.equipmentlending.infrastructure.repository.history.HistoryRepository;
import net.shlab.hogefugapiyo.equipmentlending.infrastructure.repository.entity.LendingRequestRepository;
import net.shlab.hogefugapiyo.equipmentlending.model.entity.Equipment;
import net.shlab.hogefugapiyo.equipmentlending.model.entity.LendingRequest;
import net.shlab.hogefugapiyo.equipmentlending.model.history.EquipmentHistory;
import net.shlab.hogefugapiyo.equipmentlending.model.history.LendingRequestDetailHistory;
import net.shlab.hogefugapiyo.equipmentlending.model.history.LendingRequestHistory;
import net.shlab.hogefugapiyo.equipmentlending.model.value.EquipmentStatus;
import net.shlab.hogefugapiyo.equipmentlending.model.value.LendingRequestStatus;
import net.shlab.hogefugapiyo.framework.core.time.CurrentTimeProvider;
import net.shlab.hogefugapiyo.framework.service.CommandBaseService;
import org.springframework.stereotype.Service;

/**
 * {@inheritDoc}
 *
 * <p>貸出申請の登録と備品状態更新を一連の業務処理として実行する。
 */
@Service
public class RegisterLendingRequestCommandServiceImpl
        extends CommandBaseService<RegisterLendingRequestCommandService.Request, RegisterLendingRequestCommandService.HistoryResponse>
        implements RegisterLendingRequestCommandService {

    private static final String COMMAND_ID = "HFP-EL-SCS001_register-lending-request_service";

    private final LendingRequestRepository lendingRequestRepository;
    private final EquipmentRepository equipmentRepository;
    private final CheckLendingRequestAvailabilityService checkLendingRequestAvailabilityService;
    private final CurrentTimeProvider currentTimeProvider;
    private final HistoryRepository historyRepository;

    public RegisterLendingRequestCommandServiceImpl(
            LendingRequestRepository lendingRequestRepository,
            EquipmentRepository equipmentRepository,
            CheckLendingRequestAvailabilityService checkLendingRequestAvailabilityService,
            CurrentTimeProvider currentTimeProvider,
            HistoryRepository historyRepository
    ) {
        this.lendingRequestRepository = lendingRequestRepository;
        this.equipmentRepository = equipmentRepository;
        this.checkLendingRequestAvailabilityService = checkLendingRequestAvailabilityService;
        this.currentTimeProvider = currentTimeProvider;
        this.historyRepository = historyRepository;
    }

    @Override
    protected String commandId() {
        return COMMAND_ID;
    }

    @Override
    protected RegisterLendingRequestCommandService.HistoryResponse doExecute(RegisterLendingRequestCommandService.Request request) {
        validateEquipmentIds(request.equipmentIds());
        List<Equipment> equipments = equipmentRepository.findByIds(request.equipmentIds());
        if (equipments.size() != request.equipmentIds().size()) {
            throw new BusinessException(BusinessMessageIds.EQUIPMENT_SELECTION_INVALID);
        }
        validateEquipments(equipments);
        var availability = checkLendingRequestAvailabilityService.check(
                equipments.stream()
                        .map(e -> new EquipmentAvailabilityInput(e.equipmentId(), e.statusCode()))
                        .toList());
        if (!availability.valid()) {
            throw new BusinessException(BusinessMessageIds.EQUIPMENT_SELECTION_INVALID);
        }
        List<Long> equipmentIds = equipments.stream().map(Equipment::equipmentId).toList();
        long lendingRequestId = lendingRequestRepository.nextId();
        var now = currentTimeProvider.currentDateTime();
        LendingRequest lendingRequest = new LendingRequest();
        lendingRequest.setLendingRequestId(lendingRequestId);
        lendingRequest.setApplicantUserId(request.userId());
        lendingRequest.setStatus(LendingRequestStatus.PENDING_APPROVAL);
        lendingRequest.setRequestComment(normalizeComment(request.requestComment()));
        lendingRequest.setRequestedAt(now);
        lendingRequest.setCreatedAt(now.toInstant(java.time.ZoneOffset.UTC));
        lendingRequest.setUpdatedAt(now.toInstant(java.time.ZoneOffset.UTC));
        lendingRequest.setCreatedBy(request.userId());
        lendingRequest.setUpdatedBy(request.userId());
        lendingRequestRepository.save(lendingRequest);
        lendingRequestRepository.saveDetails(lendingRequestId, equipmentIds, request.userId(), now);
        equipments.forEach(equipment -> {
            equipment.setStatus(EquipmentStatus.PENDING_LENDING);
            equipment.setUpdatedAt(now.toInstant(java.time.ZoneOffset.UTC));
            equipment.setUpdatedBy(request.userId());
        });
        equipmentRepository.saveAll(equipments);
        return new RegisterLendingRequestCommandService.HistoryResponse(commandId(), lendingRequestId, equipmentIds);
    }

    @Override
    protected void recordHistory(RegisterLendingRequestCommandService.HistoryResponse result) {
        var operationId = currentOperationId();
        var operatedAt = currentTimeProvider.currentDateTime();
        historyRepository.insertLendingRequestHistory(
                new LendingRequestHistory(operationId, result.lendingRequestId(), result.commandId(), operatedAt)
        );
        historyRepository.insertLendingRequestDetailHistories(
                result.equipmentIds().stream()
                        .map(equipmentId -> new LendingRequestDetailHistory(
                                operationId,
                                result.lendingRequestId(),
                                equipmentId,
                                result.commandId(),
                                operatedAt
                        ))
                        .toList()
        );
        historyRepository.insertEquipmentHistories(
                result.equipmentIds().stream()
                        .map(equipmentId -> new EquipmentHistory(
                                operationId,
                                equipmentId,
                                result.commandId(),
                                operatedAt
                        ))
                        .toList()
        );
    }

    private void validateEquipments(List<Equipment> equipments) {
        if (equipments == null || equipments.isEmpty()) {
            throw new BusinessException(BusinessMessageIds.EQUIPMENT_SELECTION_INVALID);
        }
        Set<Long> ids = equipments.stream().map(Equipment::equipmentId).collect(java.util.stream.Collectors.toSet());
        if (ids.size() != equipments.size()) {
            throw new BusinessException(BusinessMessageIds.EQUIPMENT_SELECTION_INVALID);
        }
    }

    private void validateEquipmentIds(List<Long> equipmentIds) {
        if (equipmentIds == null || equipmentIds.isEmpty()) {
            throw new BusinessException(BusinessMessageIds.EQUIPMENT_SELECTION_INVALID);
        }
        if (equipmentIds.stream().distinct().count() != equipmentIds.size()) {
            throw new BusinessException(BusinessMessageIds.EQUIPMENT_SELECTION_INVALID);
        }
    }

    private String normalizeComment(String requestComment) {
        return requestComment == null || requestComment.isBlank() ? null : requestComment.trim();
    }
}
