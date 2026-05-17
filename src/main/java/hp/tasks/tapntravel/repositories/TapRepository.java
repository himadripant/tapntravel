package hp.tasks.tapntravel.repositories;

import hp.tasks.tapntravel.entities.Tap;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;
import java.util.List;

public interface TapRepository extends JpaRepository<Tap, BigInteger> {
    List<Tap> findByPanAndBusIdAndBusCompanyId(String pan, String busId, Integer busCompanyId);
}
