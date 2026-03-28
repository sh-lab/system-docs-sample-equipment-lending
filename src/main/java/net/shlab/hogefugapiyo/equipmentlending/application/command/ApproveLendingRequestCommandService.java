package net.shlab.hogefugapiyo.equipmentlending.application.command;

import java.util.List;
import net.shlab.hogefugapiyo.framework.core.service.CommandService;

/**
 * 貸出申請承認処理の契約を定義する Command Service。
 *
 * <p>設計書:
 * <ul>
 *   <li>{@code docs/03_designs/services/command/HFP-EL-SCS010_approve-lending-request_service.md}</li>
 * </ul>
 */
public interface ApproveLendingRequestCommandService
        extends CommandService<ApproveLendingRequestCommandService.Request> {

    record Request(String adminUserId, long lendingRequestId, String reviewComment, int version) {
    }

    record HistoryResponse(String commandId, long lendingRequestId, List<Long> equipmentIds) {
    }
}
