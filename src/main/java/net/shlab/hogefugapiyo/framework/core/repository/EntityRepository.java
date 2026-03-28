package   net.shlab.hogefugapiyo.framework.core.repository;

import java.util.Optional;

/**
 * エンティティの永続化を担当するリポジトリの共通基底インターフェース。
 * <p>
 * すべてのエンティティリポジトリはこのインターフェースを実装し、
 * フレームワーク側でAOP等による横断的な処理（ログ・統計等）が可能です。
 * @param <E> エンティティ型
 */
public interface EntityRepository<E, ID> {
 
     /**
     * エンティティをIDで検索します。
     *  <p>存在しない場合は {@code Optional.empty()} を返し、{@code null} を返してはならない。</p>
     * @param id エンティティのID
     * @return 検索されたエンティティ（存在しない場合は {@code Optional.empty()}）
     */
    Optional<E> findById(ID id);
 
    /**
     * エンティティを保存します。
     * @param entity 保存するエンティティ
     * @return 保存されたエンティティ（非null）
     */
    E save(E entity);


}