package dan.ms.pedidos.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dan.ms.pedidos.domain.DetallePedido;
import dan.ms.pedidos.domain.EstadoPedido;
import dan.ms.pedidos.domain.Pedido;
import dan.ms.pedidos.excepciones.ExceptionRechazoPedido;
import dan.ms.pedidos.services.interfaces.PedidoService;
import dan.ms.persistence.repositories.PedidoRepository;

@Service
public class PedidoServiceImp implements PedidoService {

	@Autowired
	PedidoRepository pedidoRepo;

	// TODO : como obtengo el estado de BCRA de un cliente determinado?

	@Override
	public Pedido guardarPedido(Pedido ped) throws ExceptionRechazoPedido {

		Boolean stockDisponible = stockDisponiblePedido(ped);

		double saldoDeudor = saldoDeudor(ped.getDetalle());
		Boolean generaSaldoDeudor = saldoDeudor > 0;

		EstadoPedido esp = ped.getEstado();
		

	
		if (stockDisponible) {
			//Se cumple que hay stock - a
			if (!generaSaldoDeudor) {
				// Se cumple que hay stock y se cumple condicion b
				esp.setEstado("ACEPTADO");
				ped.setEstado(esp);
				return this.pedidoRepo.save(ped);
			}
			double saldoDescubierto = saldoDescubierto();
			Boolean SaldoDeudorMenorQueDescubierto = saldoDeudor < saldoDescubierto;
			// TODO: Como hacer el chequeo de riesgo BCRA
			Boolean situacionCrediticiaBajoRiesgo = situacionCrediticiaBajoRiesgoBCRA();
			if (generaSaldoDeudor && SaldoDeudorMenorQueDescubierto && situacionCrediticiaBajoRiesgo) {
				// Se cumple que hay stock y se cumple condicion c
				esp.setEstado("ACEPTADO");
				ped.setEstado(esp);
				return this.pedidoRepo.save(ped);

			}
			
			
			esp.setEstado("RECHAZADO");
			ped.setEstado(esp);
			this.pedidoRepo.save(ped);
			throw new ExceptionRechazoPedido(ped);

		}
		// Si no hay stock, el pedido se caga como pendiente
		esp.setEstado("PENDIENTE");
		ped.setEstado(esp);
		return this.pedidoRepo.save(ped);

	}

	@Override
	public Optional<Pedido> buscarPorId(Integer id) {

		return this.pedidoRepo.findById(id);
	}

	@Override
	public void borrarPedido(Pedido ped) {
		// TODO: Falta implementar borrarPedido

	}

	@Override
	public Pedido actualizarPedido(Pedido ped) {
		// TODO Falta implementar actualizarPedido
		return null;
	}

	@Override
	public Boolean stockDisponiblePedido(Pedido ped) {
		// TODO Auto-generated method StockDisponiblePedido
		return true;
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
		// TODO Auto-generated method stub
		return true;
	}

}
