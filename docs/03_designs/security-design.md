# セキュリティ設計

## 1. 文書の目的
本書は、本システムにおける認証・認可の具体設計を定義し、
画面設計および Controller 実装の共通基準を示すことを目的とする。

---

## 2. 適用範囲
- ログイン画面および業務画面の認証条件
- 画面別の認可設計
- 認証コンテキストの表現
- Controller における利用者識別方法
- 未認証および権限不足時の制御方針

---

## 3. 基本設計方針
- 認証・認可基盤には Spring Security を採用する。
- 認可判定は Controller 層で完結させ、業務サービス層では行わない。
- 利用者区分は `USER` / `ADMIN` のロールで表現する。
- 共通の認証・認可制御は横断設計として集約し、個別画面設計書へ重複定義しない。
- 更新系リクエストでは、CSRF 対策に加えて重複送信防止を横断設計として扱う。
- 重複送信防止用ワンタイムトークンの検証・消費は、個別 Controller ではなく `HandlerInterceptor` で横断的に実施する。
- ワンタイムトークン不正時は、元画面を再表示せず、多重送信検知専用画面へ遷移させる。

---

## 4. 認証設計
### 4.1 認証対象
- `login` 画面およびログイン処理は、未認証の利用者が利用できる。
- 本サンプルでは教育用の例外として、`/`、静的リソース、`/h2-console/**` も未認証アクセス可能とする。
- 上記例外は、学習用の導線簡素化、画面描画に必要な静的ファイル配信、および H2 コンソールの学習利用を目的としたものである。
- `login` を除く業務画面は、認証済み利用者のみ利用できる。

### 4.2 認証コンテキスト
- 認証済み利用者情報は `UserPrincipal` として表現する。
- `UserPrincipal` は少なくとも以下を保持する。
  - `userId`
  - `roleCode`
- `roleCode` は `M_USER.ROLE_CODE` の値体系と一致させる。

### 4.3 正とする保持場所
- ログイン成功後の利用者情報は Spring Security の `Authentication` および `SecurityContext` に保持する。
- 独自の `HttpSession` 属性は、認証・認可判定の正として扱わない。

### 4.4 本サンプルにおける認証の簡略化

本サンプルでは、認証処理を以下のとおり簡易化している（判断根拠は [ADR-003](../99_adr/ADR-003_spring-security-csrf-adoption.md) を参照）。

- Spring Security の `AuthenticationProvider` / `UserDetailsService` による認証処理は採用せず、`LoginController` 内で手動認証を行う。
- 全利用者に共通のパスワード（`pass`）を使用し、パスワードのハッシュ化を行わない。
- `M_USER` テーブルにパスワードカラムを持たず、パスワード照合はアプリケーションコード内の固定値比較で行う。
- ログイン画面に試験用利用者一覧および共通パスワードを表示する。
- 認証後の利用者情報の保持、認可制御、CSRF 対策については Spring Security を利用する。
- 重複送信防止用のワンタイムトークンは、認証情報とは分離したセッション上の一時状態として扱う。

認証関連の実装（`LoginApplicationService`、`FindLoginUserQueryService`、`LoginController`）は、業務サービスではなく認証基盤の一部として扱うため、業務サービス設計書（SAS / SQS）の作成対象外とする。

**本サンプルの認証構成を本番環境で使用してはならない。**
本番環境では、以下の対応が必要である。

- `AuthenticationProvider` または `UserDetailsService` を用いた標準的な認証方式の採用
- パスワードのハッシュ化保存（`BCryptPasswordEncoder` 等）
- ユーザーテーブルへの認証情報カラムの追加
- 試験用利用者一覧・共通パスワード表示の削除

---

## 5. 認可設計
### 5.1 画面別認可
| 画面 | 利用対象 | 認可方法 |
|------|----------|----------|
| `login` | 未認証の利用者 | `permitAll` |
| `HFP-EL-V100_user-mypage` | `USER` 権限を持つ認証済み利用者 | `@PreAuthorize("hasRole('USER')")` |
| `HFP-EL-V300_equipment-search` | `USER` 権限を持つ認証済み利用者 | `@PreAuthorize("hasRole('USER')")` |
| `HFP-EL-V400_user-lending-request` | `USER` 権限を持つ認証済み利用者 | `@PreAuthorize("hasRole('USER')")` |
| `HFP-EL-V200_admin-mypage` | `ADMIN` 権限を持つ認証済み利用者 | `@PreAuthorize("hasRole('ADMIN')")` |
| `HFP-EL-V500_admin-lending-review` | `ADMIN` 権限を持つ認証済み利用者 | `@PreAuthorize("hasRole('ADMIN')")` |
| `HFP-EL-V600_admin-equipment-search` | `ADMIN` 権限を持つ認証済み利用者 | `@PreAuthorize("hasRole('ADMIN')")` |
| `HFP-EL-V700_admin-equipment-edit` | `ADMIN` 権限を持つ認証済み利用者 | `@PreAuthorize("hasRole('ADMIN')")` |

### 5.2 Controller 責務
- Controller は画面単位の認可を宣言する。
- Controller は認証コンテキストから利用者 ID を取得し、業務サービスへ引き渡す。
- Controller 内でロール判定や利用者識別の重複実装を行わない。
- Controller 内でワンタイムトークンの個別検証を行わず、表示時のトークン払い出しと画面再表示に必要な入力保持のみを担う。
- 業務サービス層は、Controller から引き渡された利用者 ID を前提に業務処理を行い、認可判定は持ち込まない。

---

## 6. 権限不足時制御
- 未認証で業務画面へアクセスした場合は、ログイン画面へ誘導する。
- 認証済みだが別ロールの画面へアクセスした場合は、認可エラー画面に固定せず、利用者区分に応じて許可された画面へ誘導する。
- 上記制御はセキュリティ設定または専用ハンドラへ集約し、個別 Controller に分散させない。

---

## 7. 個別画面設計との関係
- `docs/03_designs/ui/screen-list.md` では、画面ごとの利用対象と画面間関係のみを記載する。
- 個別画面設計書では、当該画面に固有の利用条件や本人確認要件を記載する。
- `@PreAuthorize`、`UserPrincipal`、権限不足時制御などの共通事項は、本書を正本として参照する。

---

## 8. テスト観点
- Controller 単体テストでは、画面ごとに正常系、未認証、権限不足を検証する。
- 認証済み利用者を用いるテストでは、`UserPrincipal` を保持した認証コンテキストを明示的に設定する。
- 画面固有の業務条件と共通認可条件を混在させず、それぞれ分離して検証する。
- 更新系フォームの Web テストでは、CSRF トークンに加えて `HandlerInterceptor` による重複送信防止用ワンタイムトークンの正常系・不正系を確認する。
- ワンタイムトークン不正系では、専用エラー画面への遷移と、利用者区分に応じたマイページへの戻り導線を確認する。

---

## 9. 関連資料
- `docs/01_requirements/functional-requirements.md`
- `docs/02_architecture/security-authorization-policy.md`
- `docs/03_designs/ui/screen-list.md`
- `docs/03_designs/ui/HFP-EL-V100_user-mypage.md`
- `docs/03_designs/ui/HFP-EL-V200_admin-mypage.md`
- `docs/03_designs/ui/HFP-EL-V300_equipment-search.md`
- `docs/03_designs/ui/HFP-EL-V400_user-lending-request.md`
- `docs/03_designs/ui/HFP-EL-V500_admin-lending-review.md`
- `docs/03_designs/ui/HFP-EL-V600_admin-equipment-search.md`
- `docs/03_designs/ui/HFP-EL-V700_admin-equipment-edit.md`
- `docs/99_adr/ADR-003_spring-security-csrf-adoption.md`
- `docs/99_adr/ADR-006_duplicate-submit-prevention-layering.md`
