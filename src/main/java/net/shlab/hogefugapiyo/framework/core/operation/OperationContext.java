package net.shlab.hogefugapiyo.framework.core.operation;

import java.util.Objects;
import java.util.UUID;

/**
 * 業務操作単位の相関 ID を保持するコンテキスト。
 */
public final class OperationContext {

    private final UUID operationId;

    private OperationContext(UUID operationId) {
        this.operationId = Objects.requireNonNull(operationId);
    }

    public static OperationContext create() {
        return new OperationContext(UUID.randomUUID());
    }

    public UUID operationId() {
        return operationId;
    }
}
