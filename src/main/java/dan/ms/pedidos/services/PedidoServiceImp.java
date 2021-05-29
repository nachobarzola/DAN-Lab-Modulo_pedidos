package dan.ms.pedidos.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dan.ms.pedidos.domain.DetallePedido;
import dan.ms.pedidos.domain.EstadoPedido;
import dan.ms.pedidos.domain.Obra;
import dan.ms.pedidos.domain.Pedido;
import dan.ms.pedidos.excepciones.ExceptionRechazoPedido;
import dan.ms.pedidos.services.dao.DetallePedidoRepository;
import dan.ms.pedidos.services.dao.PedidoRepository;
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

	@Autowired
	DetallePedidoRepository detalleRepo;

	@Autowired
	RiesgoBCRAService riesgoBCRA;
	
	@Autowired
	ProductoRestExternoService productoExtService;

	@Override
	public Optional<Pedido> guardarPedido(Pedido ped){

		return Optional.of(this.pedidoRepo.saveAndFlush(ped));

	}

	@Override
	public Optional<Pedido> buscarPorId(Integer id) {

		return this.pedidoRepo.findById(id);
	}

	@Override
	public void borrarPedido(Pedido ped) {
		this.pedidoRepo.delete(ped);

	}

	@Override
	public Optional<Pedido> actualizarPedido(Pedido ped){
		Optional<Pedido> pedido = guardarPedido(ped);

		return pedido;
	}

	@Override
	public Boolean stockDisponiblePedido(Pedido ped) {

		return productoExtService.hayStockDisponible(ped.getDetalle()) ;
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
	public Optional<Pedido> agregarDetallePedido(Pedido ped){
		return guardarPedido(ped);
	}

	@Override
	public Optional<Pedido> buscarPorIdObra(Integer idObra) {
		Obra ob = new Obra();
		ob.setId(idObra);
		return pedidoRepo.findByObra(ob);
	}

	@Override
	public void borrarDetallePedido(DetallePedido det) {

		detalleRepo.delete(det);

	}

	@Override
	public Optional<List<Pedido>> buscarPorEstado(EstadoPedido estado) {

		return pedidoRepo.findByEstado(estado);
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
				esp.setId(1);
				ped.setEstado(esp);

			} else {
				double saldoDescubierto = saldoDescubierto();
				Boolean SaldoDeudorMenorQueDescubierto = saldoDeudor < saldoDescubierto;

				Boolean situacionCrediticiaBajoRiesgo = situacionCrediticiaBajoRiesgoBCRA();
				if (generaSaldoDeudor && SaldoDeudorMenorQueDescubierto && situacionCrediticiaBajoRiesgo) {
					// Se cumple que hay stock y se cumple condicion c
					esp.setEstado("ACEPTADO");
					// esp.setId(1);
					ped.setEstado(esp);

				} else {

					esp.setEstado("RECHAZADO");
					// esp.setId(2);
					ped.setEstado(esp);
					throw new ExceptionRechazoPedido(ped);
				}
			}

		} else {
			// Si no hay stock, el pedido se carga como pendiente
			esp.setEstado("PENDIENTE");
			// esp.setId(3);
			ped.setEstado(esp);
		}
		return Optional.of(ped);
	}

}
