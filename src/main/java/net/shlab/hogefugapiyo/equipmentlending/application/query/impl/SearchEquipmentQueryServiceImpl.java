package net.shlab.hogefugapiyo.equipmentlending.application.query.impl;

import java.util.List;
import net.shlab.hogefugapiyo.equipmentlending.application.query.SearchEquipmentQueryService;
import net.shlab.hogefugapiyo.equipmentlending.infrastructure.repository.query.EquipmentSearchQueryRepository;
import org.springframework.stereotype.Service;

/**
 * {@inheritDoc}
 */
@Service
public class SearchEquipmentQueryServiceImpl implements SearchEquipmentQueryService {

    private static final int DISPLAY_LIMIT = 100;

    private final EquipmentSearchQueryRepository equipmentSearchQueryRepository;
    public SearchEquipmentQueryServiceImpl(
            EquipmentSearchQueryRepository equipmentSearchQueryRepository
    ) {
        this.equipmentSearchQueryRepository = equipmentSearchQueryRepository;
    }

    @Override
    public SearchEquipmentQueryService.Response execute(SearchEquipmentQueryService.Request request) {
        EquipmentSearchQueryRepository.Criteria criteria = new EquipmentSearchQueryRepository.Criteria(
                request.equipmentName(),
                request.equipmentType(),
                request.lendingStatus()
        );
        List<SearchEquipmentQueryService.EquipmentItem> queriedItems = equipmentSearchQueryRepository.findEquipmentByCriteria(criteria)
                .stream()
                .map(this::toEquipmentItem)
                .toList();
        List<SearchEquipmentQueryService.Option> equipmentTypeOptions = equipmentSearchQueryRepository.findEquipmentTypeOptions()
                .stream()
                .map(this::toOption)
                .toList();
        boolean hasMoreThanLimit = queriedItems.size() > DISPLAY_LIMIT;
        List<SearchEquipmentQueryService.EquipmentItem> visibleItems = hasMoreThanLimit
                ? queriedItems.subList(0, DISPLAY_LIMIT)
                : queriedItems;
        return new SearchEquipmentQueryService.Response(visibleItems, equipmentTypeOptions, hasMoreThanLimit);
    }

    private SearchEquipmentQueryService.EquipmentItem toEquipmentItem(EquipmentSearchQueryRepository.EquipmentRow equipmentRow) {
        return new SearchEquipmentQueryService.EquipmentItem(
                equipmentRow.equipmentId(),
                equipmentRow.equipmentCode(),
                equipmentRow.equipmentName(),
                equipmentRow.equipmentTypeName(),
                equipmentRow.storageLocation(),
                toStatusLabel(equipmentRow.statusCode()),
                equipmentRow.selectable()
        );
    }

    private SearchEquipmentQueryService.Option toOption(EquipmentSearchQueryRepository.EquipmentTypeOptionRow optionRow) {
        return new SearchEquipmentQueryService.Option(
                optionRow.equipmentTypeCode(),
                optionRow.equipmentTypeName()
        );
    }

    private String toStatusLabel(String statusCode) {
        return switch (statusCode) {
            case "AVAILABLE" -> "貸出可能";
            case "PENDING_LENDING", "LENT", "UNAVAILABLE", "DISPOSED" -> "貸出不可";
            default -> statusCode;
        };
    }
}
