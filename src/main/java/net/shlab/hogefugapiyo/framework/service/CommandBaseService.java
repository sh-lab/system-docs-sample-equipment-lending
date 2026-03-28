package net.shlab.hogefugapiyo.framework.service;

import java.util.UUID;
import net.shlab.hogefugapiyo.framework.core.operation.OperationContext;
import net.shlab.hogefugapiyo.framework.core.operation.OperationContextHolder;
import net.shlab.hogefugapiyo.framework.core.service.CommandService;

/**
 * コマンド（書き込み）処理用サービスの抽象クラス。
 * <p>
 * doExecuteメソッドでコマンドの実装を行い、正常終了時のみrecordHistoryメソッドで履歴記録を行う。
 * 
 * @param <REQ> リクエスト型
 * @param <RES> レスポンス型 履歴登録用のデータ

 */
public abstract class CommandBaseService<REQ, RES> implements CommandService<REQ> {

    @Override
    public void execute(REQ request) {
        currentOperationId();
        RES result = doExecute(request);
        recordHistory(result);
    }

    protected abstract String commandId();

    protected abstract RES doExecute(REQ request);

    protected abstract void recordHistory(RES result);

    protected final UUID currentOperationId() {
        OperationContext operationContext = OperationContextHolder.get();
        if (operationContext == null) {
            throw new IllegalStateException("Operation context is not available.");
        }
        return operationContext.operationId();
    }

}
