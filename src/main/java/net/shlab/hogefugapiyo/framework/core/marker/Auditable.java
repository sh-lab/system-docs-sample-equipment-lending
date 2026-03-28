package net.shlab.hogefugapiyo.framework.core.marker;

import java.time.Instant;

/**
 * 監査項目を保持するエンティティが実装するマーカーIFである。
 */
public interface Auditable {
    void setCreatedAt(Instant t);

    void setUpdatedAt(Instant t);

    void setCreatedBy(String u);

    void setUpdatedBy(String u);
}
