package net.shlab.hogefugapiyo.framework.service;

import java.util.UUID;
import net.shlab.hogefugapiyo.framework.core.operation.OperationContext;
import net.shlab.hogefugapiyo.framework.core.operation.OperationContextHolder;
import net.shlab.hogefugapiyo.framework.core.service.CommandService;

/**
 * コマンド（書き込み）処理用サービスの抽象クラス。
 * <p>
 * doExecuteメソッドでコマンドの実装を行い、正常終了時のみrecordHistoryメソッドで履歴記録を行う。
 * <p>
 * 型パラメータ {@code RES} は {@link #doExecute(Object)} から {@link #recordHistory(Object)} へ
 * 渡すための内部中間データ型であり、呼び出し元（Application Service）には公開されない。
 * {@link net.shlab.hogefugapiyo.framework.core.service.CommandService#execute(Object)} の
 * 戻り値は {@code void} であり、「業務データを戻り値として返却しない」ルールを満たす。
 *
 * @param <REQ> リクエスト型
 * @param <RES> 内部中間データ型（履歴登録等の後処理に使用、外部非公開）
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
