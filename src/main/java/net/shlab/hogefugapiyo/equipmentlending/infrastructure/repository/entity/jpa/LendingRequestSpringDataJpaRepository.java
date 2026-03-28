package net.shlab.hogefugapiyo.equipmentlending.infrastructure.repository.entity.jpa;

import java.util.List;
import net.shlab.hogefugapiyo.equipmentlending.model.entity.LendingRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LendingRequestSpringDataJpaRepository extends JpaRepository<LendingRequest, Long> {

    @Query(value = "SELECT NEXT VALUE FOR SEQ_T_LENDING_REQUEST_ID", nativeQuery = true)
    Long nextId();

    @Query(
            """
            select detail.equipmentId
            from LendingRequest request
            join request.details detail
            where request.lendingRequestId = :lendingRequestId
            order by detail.equipmentId asc
            """
    )
    List<Long> findEquipmentIdsByLendingRequestId(@Param("lendingRequestId") long lendingRequestId);
}
