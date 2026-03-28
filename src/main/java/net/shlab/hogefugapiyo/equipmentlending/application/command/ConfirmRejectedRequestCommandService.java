package net.shlab.hogefugapiyo.equipmentlending.application.command;

import net.shlab.hogefugapiyo.framework.core.service.CommandService;

/**
 * 却下済み申請確認処理の契約を定義する Command Service。
 *
 * <p>設計書:
 * <ul>
 *   <li>{@code docs/03_designs/services/command/HFP-EL-SCS003_confirm-rejected-request_service.md}</li>
 * </ul>
 */
public interface ConfirmRejectedRequestCommandService
        extends CommandService<ConfirmRejectedRequestCommandService.Request> {

    record Request(String userId, long lendingRequestId, int version) {
    }

    record HistoryResponse(String commandId, long lendingRequestId) {
    }
}
