package dan.ms.pedidos.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import dan.ms.pedidos.domain.DetallePedido;
import dan.ms.pedidos.services.dao.DetallePedidoRepository;
import dan.ms.pedidos.services.interfaces.DetallePedidoService;

@Service
public class DetallePedidoServiceImp implements DetallePedidoService {

	@Autowired
	DetallePedidoRepository detalleRepo;

	@Transactional
	@Override
	public Optional<List<DetallePedido>> guardarDetallePedido(List<DetallePedido> detP) {
		Optional<List<DetallePedido>> resultado;
		try {
			// Guardamos el DetallePedido
			resultado = Optional.of(detalleRepo.saveAll(detP));
			return resultado;
		} catch (Exception e) {
			// e.printStackTrace();
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return Optional.empty();
		}
	}

	@Override
	public Optional<List<DetallePedido>> BorrarDetallePedido(List<DetallePedido> listaDetalle) {
		try {
			// Borramos la lista de detalles

			detalleRepo.deleteAll(listaDetalle);
			return Optional.of(listaDetalle);
		} catch (Exception e) {
			// e.printStackTrace();
			return Optional.empty();
		}
	}

	@Override
	public Optional<DetallePedido> BorrarDetallePedido(DetallePedido detalle) {

		try {
			// Borramos el detalle

			detalleRepo.delete(detalle);

			return Optional.of(detalle);
		} catch (Exception e) {
			// e.printStackTrace();
			return Optional.empty();
		}

	}

}
