package net.shlab.hogefugapiyo.equipmentlending.application.command;

import net.shlab.hogefugapiyo.framework.core.service.CommandService;

/**
 * 返却申請登録処理の契約を定義する Command Service。
 *
 * <p>設計書:
 * <ul>
 *   <li>{@code docs/03_designs/services/command/HFP-EL-SCS002_register-return-request_service.md}</li>
 * </ul>
 */
public interface RegisterReturnRequestCommandService
        extends CommandService<RegisterReturnRequestCommandService.Request> {

    record Request(String userId, long lendingRequestId, String returnRequestComment, int version) {
    }

    record HistoryResponse(String commandId, long lendingRequestId) {
    }
}
