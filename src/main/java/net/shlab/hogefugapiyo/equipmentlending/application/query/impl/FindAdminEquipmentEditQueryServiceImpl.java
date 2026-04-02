package net.shlab.hogefugapiyo.equipmentlending.application.query.impl;

import java.time.LocalDate;
import java.util.List;
import net.shlab.hogefugapiyo.equipmentlending.application.BusinessException;
import net.shlab.hogefugapiyo.equipmentlending.application.BusinessMessageIds;
import net.shlab.hogefugapiyo.equipmentlending.application.query.FindAdminEquipmentEditQueryService;
import net.shlab.hogefugapiyo.equipmentlending.infrastructure.repository.query.AdminEquipmentEditQueryRepository;
import org.springframework.stereotype.Service;

@Service
public class FindAdminEquipmentEditQueryServiceImpl implements FindAdminEquipmentEditQueryService {

    private final AdminEquipmentEditQueryRepository repository;

    public FindAdminEquipmentEditQueryServiceImpl(AdminEquipmentEditQueryRepository repository) {
        this.repository = repository;
    }

    @Override
    public Response execute(Request request) {
        List<Option> equipmentTypeOptions = repository.findEquipmentTypeOptions().stream()
                .map(row -> new Option(row.equipmentTypeCode(), row.equipmentTypeName()))
                .toList();
        List<Option> statusOptions = List.of(
                new Option("AVAILABLE", "貸出可能"),
                new Option("UNAVAILABLE", "貸出不可"),
                new Option("DISPOSED", "廃棄")
        );
        if ("create".equals(request.mode())) {
            return new Response("create", LocalDate.now(), null, equipmentTypeOptions, statusOptions);
        }
        AdminEquipmentEditQueryRepository.EquipmentDetailRow row = repository.findEquipmentDetail(request.equipmentId() == null ? -1L : request.equipmentId())
                .orElseThrow(() -> new BusinessException(BusinessMessageIds.EQUIPMENT_DISPLAY_INVALID));
        return new Response(
                "edit",
                row.systemRegisteredDate(),
                new EquipmentDetail(
                        row.equipmentId(),
                        row.equipmentCode(),
                        row.equipmentName(),
                        row.equipmentTypeCode(),
                        row.equipmentTypeName(),
                        row.storageLocation(),
                        row.systemRegisteredDate(),
                        row.remarks(),
                        row.statusCode(),
                        toStatusLabel(row.statusCode()),
                        row.version()
                ),
                equipmentTypeOptions,
                statusOptions
        );
    }

    private String toStatusLabel(String statusCode) {
        return switch (statusCode) {
            case "AVAILABLE" -> "貸出可能";
            case "UNAVAILABLE" -> "貸出不可";
            case "DISPOSED" -> "廃棄";
            case "PENDING_LENDING" -> "貸出申請中";
            case "LENT" -> "貸出中";
            default -> statusCode;
        };
    }
}
