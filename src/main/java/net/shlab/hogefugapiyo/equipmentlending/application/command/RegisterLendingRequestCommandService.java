package net.shlab.hogefugapiyo.equipmentlending.application.command;

import java.util.List;
import net.shlab.hogefugapiyo.framework.core.service.CommandService;

/**
 * 貸出申請登録処理の契約を定義する Command Service。
 *
 * <p>設計書:
 * <ul>
 *   <li>{@code docs/03_designs/services/command/HFP-EL-SCS001_register-lending-request_service.md}</li>
 * </ul>
 */
public interface RegisterLendingRequestCommandService
        extends CommandService<RegisterLendingRequestCommandService.Request> {

    record Request(String userId, List<Long> equipmentIds, String requestComment) {
    }

    record HistoryResponse(String commandId, long lendingRequestId, List<Long> equipmentIds) {
    }
}
