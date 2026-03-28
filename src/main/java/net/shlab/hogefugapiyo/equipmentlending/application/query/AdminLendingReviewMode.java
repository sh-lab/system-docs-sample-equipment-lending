package net.shlab.hogefugapiyo.equipmentlending.application.query;

/**
 * 管理者承認・却下・返却確認画面の表示モードを表す列挙型。
 *
 * <p>設計書:
 * <ul>
 *   <li>{@code docs/03_designs/ui/HFP-EL-V500_admin-lending-review.md}</li>
 * </ul>
 */
public enum AdminLendingReviewMode {
    APPROVAL_REVIEW,
    RETURN_CONFIRM
}
