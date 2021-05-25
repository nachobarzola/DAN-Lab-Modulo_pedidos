package dan.ms.pedidos.services.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dan.ms.pedidos.domain.DetallePedido;

@Repository
public interface DetallePedidoRepository extends JpaRepository<DetallePedido,Integer>{
	
	//List<DetallePedido> findByPedidos(Integer id);

}