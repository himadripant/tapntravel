package hp.tasks.tapntravel.repositories;

import hp.tasks.tapntravel.entities.Stop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StopRepository extends JpaRepository<Stop, Integer> {

}
