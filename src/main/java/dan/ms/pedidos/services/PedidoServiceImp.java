package dan.ms.pedidos.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import dan.ms.pedidos.domain.DetallePedido;
import dan.ms.pedidos.domain.EstadoPedido;
import dan.ms.pedidos.domain.Obra;
import dan.ms.pedidos.domain.Pedido;
import dan.ms.pedidos.domain.Producto;
import dan.ms.pedidos.excepciones.ExceptionRechazoPedido;
import dan.ms.pedidos.services.dao.DetallePedidoRepository;
import dan.ms.pedidos.services.dao.EstadoPedidoRepository;
import dan.ms.pedidos.services.dao.ObraRepository;
import dan.ms.pedidos.services.dao.PedidoRepository;
import dan.ms.pedidos.services.dao.ProductoRepository;
import dan.ms.pedidos.services.interfaces.DetallePedidoService;
import dan.ms.pedidos.services.interfaces.PedidoService;
import dan.ms.pedidos.services.interfaces.ProductoRestExternoService;
import dan.ms.pedidos.services.interfaces.RiesgoBCRAService;

@Service
public class PedidoServiceImp implements PedidoService {

	/*
	 * @Autowired PedidoRepositoryInMemory pedidoRepo;
	 */
	@Autowired
	PedidoRepository pedidoRepo;

	/*
	 * @Autowired DetallePedidoRepository detalleRepo;
	 */

	@Autowired
	EstadoPedidoRepository estadoRepo;

	@Autowired
	RiesgoBCRAService riesgoBCRA;

	@Autowired
	DetallePedidoService detallePedidoService;

	@Autowired
	DetallePedidoRepository detalleRepo;

	@Autowired
	ObraRepository obraRepo;

	@Autowired
	ProductoRepository productoRepo;

	@Autowired
	ProductoRestExternoService productoExtService;

	@Autowired
	JmsTemplate jms; // jms: java message service

	@Transactional
	@Override
	public Optional<Pedido> guardarPedido(Pedido ped) {
		Pedido pedidoAGuardar = new Pedido();
		if (ped.getId() != null) {
			pedidoAGuardar.setId(ped.getId());
		}
		pedidoAGuardar.setEstado(ped.getEstado());
		pedidoAGuardar.setFechaPedido(ped.getFechaPedido());

		// Primero Guardo detalle pedido

		Optional<List<DetallePedido>> optionalDetalle = detallePedidoService.guardarDetallePedido(ped.getDetalle());
		Optional<Obra> ob = obraRepo.findById(ped.getObra().getId());

		if (ob.isEmpty() || optionalDetalle.isEmpty()) {
			return Optional.empty();
		}
		pedidoAGuardar.setObra(ob.get());

		// Seteo la lista de detalles al pedido a guardar

		for (DetallePedido deta : optionalDetalle.get()) {

			Optional<Producto> p = productoRepo.findById(deta.getProducto().getId());
			if (p.isEmpty()) {
				return Optional.empty();
			}
			deta.setProducto(p.get());

		}

		pedidoAGuardar.setDetalle(optionalDetalle.get());

		try {
			// Guardamos el pedido
			pedidoAGuardar = pedidoRepo.saveAndFlush(pedidoAGuardar);

			return Optional.of(pedidoAGuardar);
		} catch (Exception e) {
			// e.printStackTrace();
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return Optional.empty();
		}
	}

	@Override
	public Optional<Pedido> buscarPorId(Integer id) {
		Optional<Pedido> pedido = this.pedidoRepo.findById(id);
		if (pedido.isPresent()) {
			List<DetallePedido> detp = detalleRepo.findByIdPedido(id);
			if (!detp.isEmpty()) {
				pedido.get().setDetalle(detp);
			}
		}
		return pedido;
	}

	@Transactional
	@Override
	public Optional<Pedido> borrarPedido(Pedido ped) {
		try {
			// Borramos el pedido
			this.pedidoRepo.delete(ped);

			if (this.detallePedidoService.BorrarDetallePedido(ped.getDetalle()).isEmpty()) {
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				return Optional.empty();
			}
			return Optional.of(ped);
		} catch (Exception e) {
			return Optional.empty();
		}

	}

	@Override
	public Optional<Pedido> actualizarPedido(Pedido ped) {
		Optional<Pedido> pedido = guardarPedido(ped);

		return pedido;
	}

	@Override
	public Boolean stockDisponiblePedido(Pedido ped) {

		return productoExtService.hayStockDisponible(ped.getDetalle());
	}

	@Override
	public double saldoDeudor(List<DetallePedido> list) {
		// TODO Auto-generated method generaSaldoDeudor
		return 0.0;
	}

	@Override
	public double saldoDescubierto() {
		// TODO Auto-generated method saldoDescubierto
		return 0.0;
	}

	@Override
	public Boolean situacionCrediticiaBajoRiesgoBCRA() {
		if (riesgoBCRA.estadoCrediticio() != (1 | 2)) {
			return false;
		}

		return true;
	}

	@Override
	public Optional<Pedido> agregarDetallePedido(Pedido ped) {
		return guardarPedido(ped);
	}

	@Override
	public List<Pedido> buscarPorIdObra(Integer idObra) {
		Obra ob = new Obra();
		Optional<Obra> obra = obraRepo.findById(idObra);
		if (obra.isPresent()) {
			ob = obra.get();
			return pedidoRepo.findByObra(ob).get();
		}
		return null;
	}

	@Override
	public Optional<DetallePedido> borrarDetallePedido(DetallePedido det) {

		return detallePedidoService.BorrarDetallePedido(det);

	}

	@Override
	public Optional<List<Pedido>> buscarPorEstado(EstadoPedido estado) {

		return pedidoRepo.findByEstado(estado);
	}

	public EstadoPedido obtenerEstadoPedido(String estado) {

		return estadoRepo.findByEstado(estado);

	}

	@Override
	public Optional<Pedido> evaluarEstadoPedido(Pedido ped) throws ExceptionRechazoPedido {

		Boolean stockDisponible = stockDisponiblePedido(ped);

		double saldoDeudor = saldoDeudor(ped.getDetalle());
		Boolean generaSaldoDeudor = saldoDeudor > 0;

		EstadoPedido esp = new EstadoPedido();

		if (stockDisponible) {
			// Se cumple que hay stock - a
			if (!generaSaldoDeudor) {
				// Se cumple que hay stock y se cumple condicion b
				esp.setEstado("ACEPTADO");
				ped.setEstado(esp);
				// Enviar pedido a cola
				enviarPedidoACola(ped);

			} else {
				double saldoDescubierto = saldoDescubierto();
				Boolean SaldoDeudorMenorQueDescubierto = saldoDeudor < saldoDescubierto;

				Boolean situacionCrediticiaBajoRiesgo = situacionCrediticiaBajoRiesgoBCRA();
				if (generaSaldoDeudor && SaldoDeudorMenorQueDescubierto && situacionCrediticiaBajoRiesgo) {
					// Se cumple que hay stock y se cumple condicion c
					esp.setEstado("ACEPTADO");
					ped.setEstado(esp);
					// Enviar pedido a cola
					enviarPedidoACola(ped);

				} else {

					esp.setEstado("RECHAZADO");
					ped.setEstado(esp);
					esp.setId(obtenerEstadoPedido(ped.getEstado().getEstado()).getId());
					ped.setEstado(esp);
					throw new ExceptionRechazoPedido(ped);
				}
			}

		} else {
			// Si no hay stock, el pedido se carga como pendiente
			esp.setEstado("PENDIENTE");
			ped.setEstado(esp);
		}
		esp.setId(obtenerEstadoPedido(ped.getEstado().getEstado()).getId());
		ped.setEstado(esp);
		return Optional.of(ped);
	}

	private void enviarPedidoACola(Pedido p) {
		Map<String, Integer> pedidoMap = new HashMap<>();

		pedidoMap.put("cantidadDetalle", p.getDetalle().size());
		int i = 1;
		for (DetallePedido det : p.getDetalle()) {
			pedidoMap.put("idDetallePedido" + i, det.getId());
			i++;
		}

		jms.convertAndSend("COLA_PEDIDOS", pedidoMap);

	}

}
