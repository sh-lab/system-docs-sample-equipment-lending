package net.shlab.hogefugapiyo.framework.core.operation;

import java.util.Objects;

/**
 * 同一スレッド内で業務操作コンテキストを保持する Holder。
 */
public final class OperationContextHolder {

    private static final ThreadLocal<OperationContext> HOLDER = new ThreadLocal<>();

    private OperationContextHolder() {
    }

    public static void set(OperationContext context) {
        HOLDER.set(Objects.requireNonNull(context));
    }

    public static OperationContext get() {
        return HOLDER.get();
    }

    public static void clear() {
        HOLDER.remove();
    }
}
