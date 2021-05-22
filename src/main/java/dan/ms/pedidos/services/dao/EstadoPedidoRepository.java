package dan.ms.pedidos.services.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dan.ms.pedidos.domain.EstadoPedido;

@Repository
public interface EstadoPedidoRepository extends JpaRepository<EstadoPedido,Integer> {

}
