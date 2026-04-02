# 画面一覧

## 1. 文書の目的
本書は、対象システムに含まれる画面を俯瞰的に整理するための一覧である。
個別画面の詳細仕様を定義するものではなく、画面の識別・役割・関係を把握することを目的とする。

---

## 2. 記載方針
- 1行を1画面、または1つの利用単位として記載する。
- 詳細な項目仕様、レイアウト仕様、実装方式は記載しない。
- 具体的な技術要素や実装依存の表現は避ける。
- 必要に応じて、個別の画面設計書へ参照をつなぐ。

---

## 3. 画面一覧
| 識別子 | 名称 | 目的 | 主な利用者 | 関連機能・業務 | 関連画面・前後関係 | 関連設計 | 備考 |
|------|------|------|-----------|----------------|-------------------|----------|------|
| `login` | ログイン画面 | 利用者および管理者がログインし、ロールに応じたマイページへ遷移するための入口とする。学習用途に限り、試験用利用者としてあらかじめ定義された利用者一覧を固定表示する。 | 利用者、管理者 | ログイン、利用開始 | 遷移元：`/`、遷移先：`利用者マイページ(V100)`、`管理者マイページ(V200)` | `docs/03_designs/ui/login.md` | 基盤画面であり、画面ID体系の適用対象外である。 |
| `HFP-EL-V100_user-mypage` | 利用者マイページ | 利用者が自身の貸出申請状況を確認し、関連画面へ遷移する起点とする。 | 利用者 | 申請状況確認、備品検索開始、申請詳細参照 | 遷移元：`login`、関連先：`備品検索画面(V300)`、`利用者貸出申請・返却画面(V400)` | `docs/03_designs/ui/HFP-EL-V100_user-mypage.md` | 実装済み画面である。 |
| `HFP-EL-V200_admin-mypage` | 管理者マイページ | 管理者が承認待ち申請一覧および返却確認待ち申請一覧を確認し、関連画面へ遷移する起点とする。 | 管理者 | 管理業務起点、申請状況確認、備品管理開始 | 遷移元：`login`、関連先：`管理者承認・却下・返却確認画面(V500)`、`管理者備品検索画面(V600)` | `docs/03_designs/ui/HFP-EL-V200_admin-mypage.md` | 設計済み画面である。 |
| `HFP-EL-V300_equipment-search` | 備品検索画面 | 利用者が貸出対象となる備品を検索し、申請対象を選定する。 | 利用者 | 備品検索、貸出候補確認 | 遷移元：`利用者マイページ(V100)`、遷移先：`利用者貸出申請・返却画面(V400)` | `docs/03_designs/ui/HFP-EL-V300_equipment-search.md` | 設計済み画面である。 |
| `HFP-EL-V400_user-lending-request` | 利用者貸出申請・返却画面 | 利用者が貸出申請を行い、貸出中申請に対する返却申請、および却下された申請内容の確認を行う。 | 利用者 | 貸出申請、返却申請、却下申請確認 | 遷移元：`利用者マイページ(V100)`、`備品検索画面(V300)`、関連先：`利用者マイページ(V100)`、`備品検索画面(V300)` | `docs/03_designs/ui/HFP-EL-V400_user-lending-request.md` | 設計済み画面である。 |
| `HFP-EL-V500_admin-lending-review` | 管理者承認・却下・返却確認画面 | 管理者が貸出申請の承認・却下、および返却確認を行う。 | 管理者 | 承認、却下、返却確認 | 遷移元：`管理者マイページ(V200)`、関連先：`管理者マイページ(V200)` | `docs/03_designs/ui/HFP-EL-V500_admin-lending-review.md` | 設計済み画面である。 |
| `HFP-EL-V600_admin-equipment-search` | 管理者備品検索画面 | 管理者が備品を検索し、新規登録および情報更新対象の選定を行う。 | 管理者 | 備品検索、備品登録開始、備品情報更新開始 | 遷移元：`管理者マイページ(V200)`、関連先：`管理者マイページ(V200)`、`管理者備品編集画面(V700)` | `docs/03_designs/ui/HFP-EL-V600_admin-equipment-search.md` | 設計済み画面である。 |
| `HFP-EL-V700_admin-equipment-edit` | 管理者備品編集画面 | 管理者が新規備品登録または登録済み備品の情報更新を行う。 | 管理者 | 備品登録、備品情報更新 | 遷移元：`管理者備品検索画面(V600)`、関連先：`管理者備品検索画面(V600)` | `docs/03_designs/ui/HFP-EL-V700_admin-equipment-edit.md` | 単一画面で新規登録モードと編集モードを切り替える。 |

---

## 4. 利用上の補足
- `login` は基盤画面であり、業務画面とは別枠で扱う。
- `login` を除く業務画面は認証済み利用者のみ利用対象とする。
- 利用者向け画面は `USER` 権限、管理者向け画面は `ADMIN` 権限を前提とする。
- 共通の認証・認可の具体設計は `docs/03_designs/security-design.md` を参照する。
- 画面の追加・統合・廃止が生じた場合は、本一覧を起点に関連資料との整合を確認する。

---

## 5. 関連資料
- 個別画面設計：`docs/03_designs/ui/HFP-EL-V100_user-mypage.md`
- 個別画面設計：`docs/03_designs/ui/HFP-EL-V200_admin-mypage.md`
- 個別画面設計：`docs/03_designs/ui/HFP-EL-V300_equipment-search.md`
- 個別画面設計：`docs/03_designs/ui/HFP-EL-V400_user-lending-request.md`
- 個別画面設計：`docs/03_designs/ui/HFP-EL-V500_admin-lending-review.md`
- 個別画面設計：`docs/03_designs/ui/HFP-EL-V600_admin-equipment-search.md`
- 個別画面設計：`docs/03_designs/ui/HFP-EL-V700_admin-equipment-edit.md`
- 個別画面設計：`docs/03_designs/ui/login.md`
- 共通セキュリティ設計：`docs/03_designs/security-design.md`
- データ設計：`docs/03_designs/data/M_USER.md`
- データ設計：`docs/03_designs/data/M_EQUIPMENT.md`
- データ設計：`docs/03_designs/data/M_EQUIPMENT_TYPE.md`
- データ設計：`docs/03_designs/data/T_LENDING_REQUEST.md`
- データ設計：`docs/03_designs/data/T_LENDING_REQUEST_DETAIL.md`
