package net.shlab.hogefugapiyo.equipmentlending.application.query.impl;

import java.util.List;
import net.shlab.hogefugapiyo.equipmentlending.application.query.SearchAdminEquipmentQueryService;
import net.shlab.hogefugapiyo.equipmentlending.infrastructure.repository.query.AdminEquipmentSearchQueryRepository;
import org.springframework.stereotype.Service;

@Service
public class SearchAdminEquipmentQueryServiceImpl implements SearchAdminEquipmentQueryService {

    private static final int DISPLAY_LIMIT = 100;

    private final AdminEquipmentSearchQueryRepository repository;

    public SearchAdminEquipmentQueryServiceImpl(AdminEquipmentSearchQueryRepository repository) {
        this.repository = repository;
    }

    @Override
    public Response execute(Request request) {
        List<EquipmentItem> queriedItems = repository.findByCriteria(new AdminEquipmentSearchQueryRepository.Criteria(
                        request.equipmentName(),
                        request.equipmentType(),
                        request.statusCode(),
                        request.systemRegisteredDate()))
                .stream()
                .map(this::toItem)
                .toList();
        List<Option> equipmentTypeOptions = repository.findEquipmentTypeOptions().stream()
                .map(row -> new Option(row.equipmentTypeCode(), row.equipmentTypeName()))
                .toList();
        List<Option> statusOptions = List.of(
                new Option("ALL", "すべて"),
                new Option("AVAILABLE", "貸出可能"),
                new Option("PENDING_LENDING", "貸出申請中"),
                new Option("LENT", "貸出中"),
                new Option("UNAVAILABLE", "貸出不可"),
                new Option("DISPOSED", "廃棄")
        );
        boolean hasMoreThanLimit = queriedItems.size() > DISPLAY_LIMIT;
        List<EquipmentItem> visibleItems = hasMoreThanLimit ? queriedItems.subList(0, DISPLAY_LIMIT) : queriedItems;
        return new Response(visibleItems, equipmentTypeOptions, statusOptions, hasMoreThanLimit);
    }

    private EquipmentItem toItem(AdminEquipmentSearchQueryRepository.EquipmentRow row) {
        return new EquipmentItem(
                row.equipmentId(),
                row.equipmentCode(),
                row.equipmentName(),
                row.equipmentTypeCode(),
                row.equipmentTypeName(),
                row.systemRegisteredDate(),
                row.storageLocation(),
                row.statusCode(),
                toStatusLabel(row.statusCode()),
                row.version()
        );
    }

    private String toStatusLabel(String statusCode) {
        return switch (statusCode) {
            case "AVAILABLE" -> "貸出可能";
            case "PENDING_LENDING" -> "貸出申請中";
            case "LENT" -> "貸出中";
            case "UNAVAILABLE" -> "貸出不可";
            case "DISPOSED" -> "廃棄";
            default -> statusCode;
        };
    }
}
