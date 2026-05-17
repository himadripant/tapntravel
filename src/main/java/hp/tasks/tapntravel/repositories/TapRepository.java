package hp.tasks.tapntravel.repositories;

import hp.tasks.tapntravel.entities.Tap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigInteger;
import java.util.List;

public interface TapRepository extends JpaRepository<Tap, BigInteger> {

    @Query("SELECT t FROM Tap t JOIN FETCH t.beginStop LEFT JOIN FETCH t.endStop WHERE t.pan = :pan AND t.busId = :busId AND t.busCompanyId = :busCompanyId")
    List<Tap> findByPanAndBusIdAndBusCompanyId(
            @Param("pan") String pan, @Param("busId") String busId, @Param("busCompanyId") Integer busCompanyId
    );
}
