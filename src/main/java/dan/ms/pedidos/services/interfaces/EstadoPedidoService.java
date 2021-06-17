package dan.ms.pedidos.services.interfaces;

import dan.ms.pedidos.domain.EstadoPedido;

public interface EstadoPedidoService {
	
	public EstadoPedido obtenerEstadoPedidoPorDescripcion(String detalle);

}
