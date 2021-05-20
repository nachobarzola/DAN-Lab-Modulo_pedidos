package dan.ms.pedidos.services.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import dan.ms.pedidos.domain.Producto;

@Repository
@Transactional(readOnly = true)
public interface ProductoRepository extends JpaRepository<Producto,Integer> {

}
