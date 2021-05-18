package dan.ms.pedidos.domain;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Pedido {
	private Integer id;
	private Instant fechaPedido;
	private List<DetallePedido> detalle =new ArrayList<>();
	private EstadoPedido estado;
	private Obra obra;
	
	
	public Pedido(Integer id, Instant fechaPedido, List<DetallePedido> detalle, EstadoPedido estado, Obra obra) {
		super();
		this.id = id;
		this.fechaPedido = fechaPedido;
		this.detalle = detalle;
		this.estado = estado;
		this.obra = obra;
	}
	public Pedido() {
		super();
		this.detalle = new ArrayList<>();
	}
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
	public List<DetallePedido> getDetalle() {
		return detalle;
	}
	public void setDetalle(List<DetallePedido> detalle) {
		this.detalle = detalle; 
	}
	public void addDetalle(DetallePedido detalle) {
		this.detalle.add(detalle);
	}
	public EstadoPedido getEstado() {
		return estado;
	}
	public void setEstado(EstadoPedido estado) {
		this.estado = estado;
	}
	public Obra getObra() {
		return obra;
	}
	public void setObra(Obra obra) {
		this.obra = obra;
	}
	@Override
	public String toString() {
		return "Pedido [id=" + id + ", fechaPedido=" + fechaPedido + ", detalle=" + detalle + ", estado=" + estado
				+ ", obra=" + obra + "]";
	}
	
	
}
