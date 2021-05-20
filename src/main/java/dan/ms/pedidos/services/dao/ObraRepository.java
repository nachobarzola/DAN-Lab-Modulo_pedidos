package dan.ms.pedidos.services.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import dan.ms.pedidos.domain.Obra;

@Repository
@Transactional(readOnly = true)
public interface ObraRepository extends JpaRepository<Obra,Integer> {

}
