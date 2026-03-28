# トランザクション境界

トランザクション境界は **Application Service** に置く。
- 原則として **1ユースケース = 1トランザクション** とする。

## Command / Query の扱い
- **Command Service** および **Query Service** は、
  Application Service のトランザクション境界を引き継いで実行する。
- Command / Query Service において
  独自にトランザクションを開始してはならない。

## 例外時の振る舞い
- Application Service 配下で例外が発生した場合はロールバックする。
- 業務例外・検証例外もロールバック対象とする。
- 例外の変換および UI / API への伝達は Application Service の責務とする。

## 分離レベル（Isolation Level）
- デフォルトの分離レベルは **Read Committed（コミット済み読み取り）** とする。
- 分離レベルを変更する必要が生じた場合は、
  ユースケース単位で検討し、ADR に記録する。

## 禁止事項
- Repository でトランザクション境界を制御しない。