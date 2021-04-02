package dan.ms.pedidos.domain;

import java.time.Instant;
import java.util.List;

public class Pedido {
	private Integer id;
	private Instant fechaPedido;
	private Obra obra;
	private EstadoPedido estado;
	private List<DetallePedido> detalle;
	
	public Pedido(Integer id, Instant fechaPedido, Obra obra, EstadoPedido estado,
			List<DetallePedido> detalle) {
		super();
		this.id = id;
		this.fechaPedido = fechaPedido;
		this.obra = obra;
		this.estado = estado;
		this.detalle = detalle;
	}
	
	public Pedido() {}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Instant getFechaPedido() {
		return fechaPedido;
	}
	public void setFechaPedido(Instant fechaPedido) {
		this.fechaPedido = fechaPedido;
	}
	public Obra getObra() {
		return obra;
	}
	public void setObra(Obra obra) {
		this.obra = obra;
	}
	public EstadoPedido getEstado() {
		return estado;
	}
	public void setEstado(EstadoPedido estado) {
		this.estado = estado;
	}
	public List<DetallePedido> getDetalle() {
		return detalle;
	}
	public void setDetallePedidoList(List<DetallePedido> detalle) {
		this.detalle = detalle;
	}
	
	

}
