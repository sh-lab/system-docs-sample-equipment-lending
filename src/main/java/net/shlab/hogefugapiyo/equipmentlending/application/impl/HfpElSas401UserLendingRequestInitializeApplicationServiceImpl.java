package net.shlab.hogefugapiyo.equipmentlending.application.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import net.shlab.hogefugapiyo.equipmentlending.application.BusinessException;
import net.shlab.hogefugapiyo.equipmentlending.application.BusinessMessageIds;
import net.shlab.hogefugapiyo.equipmentlending.application.HfpElSas401UserLendingRequestInitializeApplicationService;
import net.shlab.hogefugapiyo.equipmentlending.application.query.FindEquipmentByIdsQueryService;
import net.shlab.hogefugapiyo.equipmentlending.application.query.FindEquipmentByIdsQueryService.Request;
import net.shlab.hogefugapiyo.equipmentlending.application.query.LendingRequestScreenMode;
import net.shlab.hogefugapiyo.equipmentlending.application.query.UserLendingRequestEquipmentDto;
import net.shlab.hogefugapiyo.equipmentlending.application.query.UserLendingRequestViewData;
import net.shlab.hogefugapiyo.equipmentlending.infrastructure.repository.entity.LendingRequestRepository;
import net.shlab.hogefugapiyo.equipmentlending.model.entity.LendingRequest;
import net.shlab.hogefugapiyo.framework.i18n.I18nMessageResolver;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * {@inheritDoc}
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class HfpElSas401UserLendingRequestInitializeApplicationServiceImpl
        implements HfpElSas401UserLendingRequestInitializeApplicationService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final String FROM_EQUIPMENT_SEARCH = "V300";

    private final FindEquipmentByIdsQueryService findEquipmentByIdsQueryService;
    private final LendingRequestRepository lendingRequestRepository;
    private final I18nMessageResolver i18nMessageResolver;

    public HfpElSas401UserLendingRequestInitializeApplicationServiceImpl(
            FindEquipmentByIdsQueryService findEquipmentByIdsQueryService,
            LendingRequestRepository lendingRequestRepository,
            I18nMessageResolver i18nMessageResolver
    ) {
        this.findEquipmentByIdsQueryService = findEquipmentByIdsQueryService;
        this.lendingRequestRepository = lendingRequestRepository;
        this.i18nMessageResolver = i18nMessageResolver;
    }

    @Override
    public UserLendingRequestViewData initialize(String userId, String from, Long requestId, List<Long> equipmentIds) {
        if (FROM_EQUIPMENT_SEARCH.equals(from)) {
            return initializeLendingMode(equipmentIds);
        }
        if (requestId == null) {
            throw new BusinessException(BusinessMessageIds.REQUEST_DISPLAY_INVALID);
        }
        LendingRequest request = lendingRequestRepository.findById(requestId)
                .orElseThrow(() -> new BusinessException(BusinessMessageIds.REQUEST_DISPLAY_INVALID));
        if (!userId.equals(request.applicantUserId())) {
            throw new BusinessException(BusinessMessageIds.REQUEST_DISPLAY_INVALID);
        }
        List<Long> requestEquipmentIds = lendingRequestRepository.findEquipmentIdsByLendingRequestId(requestId);
        var equipmentResponse = findEquipmentByIdsQueryService.execute(new Request(requestEquipmentIds));
        if (equipmentResponse.items().size() != requestEquipmentIds.size()) {
            throw new BusinessException(BusinessMessageIds.REQUEST_DISPLAY_INVALID);
        }
        LendingRequestScreenMode mode = resolveMode(request.statusCode());
        boolean actionEnabled = isActionEnabled(request.statusCode());
        return new UserLendingRequestViewData(
                mode,
                actionEnabled,
                false,
                true,
                request.lendingRequestId(),
                toStatusLabel(request.statusCode()),
                formatDateTime(request.requestedAt()),
                formatDateTime(request.reviewedAt()),
                formatDateTime(request.returnRequestedAt()),
                defaultString(request.requestComment()),
                defaultString(request.returnRequestComment()),
                defaultString(request.reviewComment()),
                request.version(),
                requestEquipmentIds,
                toEquipmentItems(equipmentResponse.items())
        );
    }

    private UserLendingRequestViewData initializeLendingMode(List<Long> equipmentIds) {
        var equipmentResponse = findEquipmentByIdsQueryService.execute(new Request(equipmentIds));
        if (equipmentResponse.items().size() != equipmentIds.size()) {
            throw new BusinessException(BusinessMessageIds.EQUIPMENT_SELECTION_INVALID);
        }
        return new UserLendingRequestViewData(
                LendingRequestScreenMode.LENDING,
                true,
                true,
                false,
                null,
                null,
                null,
                null,
                null,
                "",
                "",
                "",
                null,
                equipmentIds,
                toEquipmentItems(equipmentResponse.items())
        );
    }

    private LendingRequestScreenMode resolveMode(String statusCode) {
        return switch (statusCode) {
            case "REJECTED" -> LendingRequestScreenMode.REJECTED_CONFIRM;
            case "LENT", "PENDING_RETURN_CONFIRMATION" -> LendingRequestScreenMode.RETURN;
            default -> LendingRequestScreenMode.LENDING;
        };
    }

    private boolean isActionEnabled(String statusCode) {
        return "LENT".equals(statusCode) || "REJECTED".equals(statusCode);
    }

    private List<UserLendingRequestEquipmentDto> toEquipmentItems(List<FindEquipmentByIdsQueryService.EquipmentItem> equipments) {
        return equipments.stream()
                .map(equipment -> new UserLendingRequestEquipmentDto(
                        equipment.equipmentId(),
                        equipment.equipmentCode(),
                        equipment.equipmentName(),
                        toEquipmentTypeLabel(equipment.equipmentType()),
                        equipment.storageLocation()
                ))
                .toList();
    }

    private String toEquipmentTypeLabel(String equipmentTypeCode) {
        return switch (equipmentTypeCode) {
            case "DESK" -> i18nMessageResolver.get("label.equipment-type.desk");
            case "PIPE_CHAIR" -> i18nMessageResolver.get("label.equipment-type.pipe-chair");
            case "PROJECTOR" -> i18nMessageResolver.get("label.equipment-type.projector");
            default -> equipmentTypeCode;
        };
    }

    private String toStatusLabel(String statusCode) {
        return switch (statusCode) {
            case "LENT" -> i18nMessageResolver.get("label.status.lent");
            case "PENDING_APPROVAL" -> i18nMessageResolver.get("label.status.pending-approval");
            case "PENDING_RETURN_CONFIRMATION" -> i18nMessageResolver.get("label.status.pending-return-confirmation");
            case "REJECTED" -> i18nMessageResolver.get("label.status.rejected");
            case "COMPLETED" -> i18nMessageResolver.get("label.status.completed");
            default -> statusCode;
        };
    }

    private String formatDateTime(LocalDateTime value) {
        return value == null ? null : value.format(DATE_TIME_FORMATTER);
    }

    private String defaultString(String value) {
        return value == null ? "" : value;
    }
}
