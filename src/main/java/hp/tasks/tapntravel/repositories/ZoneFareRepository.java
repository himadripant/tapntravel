package hp.tasks.tapntravel.repositories;

import hp.tasks.tapntravel.entities.ZoneFare;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ZoneFareRepository extends JpaRepository<ZoneFare, Integer> {

}
