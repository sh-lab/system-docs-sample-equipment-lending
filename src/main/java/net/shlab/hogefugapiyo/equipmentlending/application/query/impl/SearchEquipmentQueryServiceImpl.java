package net.shlab.hogefugapiyo.equipmentlending.application.query.impl;

import java.util.List;
import net.shlab.hogefugapiyo.equipmentlending.application.query.SearchEquipmentQueryService;
import net.shlab.hogefugapiyo.equipmentlending.infrastructure.repository.query.EquipmentSearchQueryRepository;
import net.shlab.hogefugapiyo.framework.i18n.I18nMessageResolver;
import org.springframework.stereotype.Service;

/**
 * {@inheritDoc}
 */
@Service
public class SearchEquipmentQueryServiceImpl implements SearchEquipmentQueryService {

    private static final int DISPLAY_LIMIT = 100;

    private final EquipmentSearchQueryRepository equipmentSearchQueryRepository;
    private final I18nMessageResolver i18nMessageResolver;

    public SearchEquipmentQueryServiceImpl(
            EquipmentSearchQueryRepository equipmentSearchQueryRepository,
            I18nMessageResolver i18nMessageResolver
    ) {
        this.equipmentSearchQueryRepository = equipmentSearchQueryRepository;
        this.i18nMessageResolver = i18nMessageResolver;
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
                toEquipmentTypeLabel(equipmentRow.equipmentTypeCode()),
                equipmentRow.storageLocation(),
                toStatusLabel(equipmentRow.statusCode()),
                equipmentRow.selectable()
        );
    }

    private SearchEquipmentQueryService.Option toOption(EquipmentSearchQueryRepository.EquipmentTypeOptionRow optionRow) {
        return new SearchEquipmentQueryService.Option(
                optionRow.equipmentTypeCode(),
                toEquipmentTypeLabel(optionRow.equipmentTypeCode())
        );
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
            case "AVAILABLE" -> i18nMessageResolver.get("label.status.available");
            case "PENDING_LENDING", "LENT", "UNAVAILABLE", "DISPOSED" ->
                    i18nMessageResolver.get("label.status.unavailable");
            default -> statusCode;
        };
    }
}
