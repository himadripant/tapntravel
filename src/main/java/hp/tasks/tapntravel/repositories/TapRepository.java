package hp.tasks.tapntravel.repositories;

import hp.tasks.tapntravel.entities.Tap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.util.List;

public interface TapRepository extends JpaRepository<Tap, BigInteger> {

    List<Tap> findByPanAndBusIdAndBusCompanyId(
            @Param("pan") String pan, @Param("busId") String busId, @Param("busCompanyId") Integer busCompanyId
    );

    @Query("select t from Tap t where t.beginDateTime > :today and t.beginDateTime < :tomorrow")
    List<Tap> findAllForDay(@Param("today") ZonedDateTime start, @Param("tomorrow") ZonedDateTime end);
}
