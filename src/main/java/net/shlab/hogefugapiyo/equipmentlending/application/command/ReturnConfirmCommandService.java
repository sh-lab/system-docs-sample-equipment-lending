package net.shlab.hogefugapiyo.equipmentlending.application.command;

import java.util.List;
import net.shlab.hogefugapiyo.framework.core.service.CommandService;

/**
 * 返却確認処理の契約を定義する Command Service。
 *
 * <p>設計書:
 * <ul>
 *   <li>{@code docs/03_designs/services/command/HFP-EL-SCS012_return-confirm_service.md}</li>
 * </ul>
 */
public interface ReturnConfirmCommandService
        extends CommandService<ReturnConfirmCommandService.Request> {

    record Request(String adminUserId, long lendingRequestId, String returnConfirmComment, int version) {
    }

    record HistoryResponse(String commandId, long lendingRequestId, List<Long> equipmentIds) {
    }
}
