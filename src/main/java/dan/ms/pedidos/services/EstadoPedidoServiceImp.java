package dan.ms.pedidos.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dan.ms.pedidos.domain.EstadoPedido;
import dan.ms.pedidos.services.dao.EstadoPedidoRepository;
import dan.ms.pedidos.services.interfaces.EstadoPedidoService;

@Service
public class EstadoPedidoServiceImp implements EstadoPedidoService {
	
	@Autowired
	EstadoPedidoRepository estadoRepo;
	
	@Override
	public EstadoPedido obtenerEstadoPedidoPorDescripcion(String estado) {
		return estadoRepo.findByEstado(estado);
	}

}
