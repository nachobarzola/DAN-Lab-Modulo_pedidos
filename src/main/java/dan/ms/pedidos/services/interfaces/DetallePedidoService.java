package dan.ms.pedidos.services.interfaces;

import java.util.List;
import java.util.Optional;

import dan.ms.pedidos.domain.DetallePedido;

public interface DetallePedidoService {

	public Optional<List<DetallePedido>> guardarDetallePedido(List<DetallePedido> detP);

	public Optional<List<DetallePedido>> BorrarDetallePedido(List<DetallePedido> listaDetalle);
	
	public Optional<DetallePedido> BorrarDetallePedido(DetallePedido listaDetalle);
}
