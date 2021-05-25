package dan.ms.pedidos.services.dao;




import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dan.ms.pedidos.domain.Obra;
import dan.ms.pedidos.domain.Pedido;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Integer> {
	
	Optional<Pedido> findByObra(Obra ob);

}
