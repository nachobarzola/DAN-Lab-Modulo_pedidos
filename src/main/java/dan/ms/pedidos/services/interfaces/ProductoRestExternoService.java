package dan.ms.pedidos.services.interfaces;

import java.util.List;

import dan.ms.pedidos.domain.DetallePedido;

public interface ProductoRestExternoService {
	
	public Boolean hayStockDisponible(List<DetallePedido> detP);

}
