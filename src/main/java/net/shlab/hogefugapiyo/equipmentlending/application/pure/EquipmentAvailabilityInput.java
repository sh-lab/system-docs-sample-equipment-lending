package net.shlab.hogefugapiyo.equipmentlending.application.pure;

/**
 * 備品の貸出可否判定に必要な入力データを表す DTO。
 *
 * <p>PureService ({@link CheckLendingRequestAvailabilityService}) が
 * JPA エンティティに依存しないよう、必要最小限のフィールドのみを保持する。
 *
 * @param equipmentId 備品ID
 * @param statusCode  備品ステータスコード（例: "AVAILABLE", "LENT"）
 */
public record EquipmentAvailabilityInput(long equipmentId, String statusCode) {
}
