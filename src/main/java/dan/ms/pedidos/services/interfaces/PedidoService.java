package dan.ms.pedidos.services.interfaces;

import java.util.List;
import java.util.Optional;

import dan.ms.pedidos.domain.DetallePedido;
import dan.ms.pedidos.domain.EstadoPedido;
import dan.ms.pedidos.domain.Pedido;
import dan.ms.pedidos.excepciones.ExceptionRechazoPedido;


public interface PedidoService {

	public Optional<Pedido> guardarPedido(Pedido ped);
	
	public Optional<Pedido> evaluarEstadoPedido(Pedido ped) throws ExceptionRechazoPedido;

	public Optional<Pedido> buscarPorId(Integer id);

	public Optional<Pedido> borrarPedido(Pedido ped);

	public Optional<Pedido> actualizarPedido(Pedido ped);
	
	public Boolean stockDisponiblePedido(Pedido ped);
	
	public double saldoDeudor(List<DetallePedido> list);

	public double saldoDescubierto();
	
	public Optional<Pedido> agregarDetallePedido(Pedido ped);
	
	public Boolean situacionCrediticiaBajoRiesgoBCRA();

	public List<Pedido> buscarPorIdObra(Integer idObra);
	
	public Optional<List<Pedido>> buscarPorEstado(EstadoPedido estado);
	
	public Optional<DetallePedido> borrarDetallePedido(DetallePedido det);
	
}
