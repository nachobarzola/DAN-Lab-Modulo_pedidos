package dan.ms.pedidos.services.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import dan.ms.pedidos.domain.DetallePedido;

@Repository
public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Integer> {
	
  @Query(nativeQuery = true, value="select * from detalle_pedido dp where dp.id_pedido = ?1")
  List<DetallePedido> findByIdPedido(Integer id);

}
