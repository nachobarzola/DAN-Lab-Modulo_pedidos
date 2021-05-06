package dan.ms.pedidos.services.interfaces;

import java.util.List;
import java.util.Optional;

import dan.ms.pedidos.domain.DetallePedido;
import dan.ms.pedidos.domain.Pedido;
import dan.ms.pedidos.excepciones.ExceptionRechazoPedido;


public interface PedidoService {

	public Pedido guardarPedido(Pedido ped) throws ExceptionRechazoPedido;

	public Optional<Pedido> buscarPorId(Integer id);

	public void borrarPedido(Pedido ped);

	public Pedido actualizarPedido(Pedido ped);
	
	public Boolean stockDisponiblePedido(Pedido ped);
	
	public double saldoDeudor(List<DetallePedido> list);

	public double saldoDescubierto();
	
	public Boolean situacionCrediticiaBajoRiesgoBCRA();
}
