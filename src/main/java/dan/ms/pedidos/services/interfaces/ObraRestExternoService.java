package dan.ms.pedidos.services.interfaces;

import java.util.List;

import dan.ms.pedidos.domain.Obra;

public interface ObraRestExternoService {
	public List<Obra> obtenerObrasDeUnCliente(String clientParams);
}
