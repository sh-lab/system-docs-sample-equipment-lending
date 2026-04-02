package net.shlab.hogefugapiyo.equipmentlending.application.pure;

import java.util.List;
import net.shlab.hogefugapiyo.equipmentlending.application.command.LendingRequestAvailabilityResult;
import net.shlab.hogefugapiyo.framework.core.service.PureService;

/**
 * 貸出申請可能かを判定する Pure Service。
 *
 * <p>設計書:
 * <ul>
 *   <li>{@code docs/03_designs/services/pure/HFP-EL-SPS001_check-lending-request-availability_service.md}</li>
 * </ul>
 */
public interface CheckLendingRequestAvailabilityService extends PureService {

    LendingRequestAvailabilityResult check(List<EquipmentAvailabilityInput> equipments);
}
