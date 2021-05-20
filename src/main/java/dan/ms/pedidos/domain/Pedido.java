package dan.ms.pedidos.domain;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

@Entity
public class Pedido {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // valor autonumerico
	@Column(name = "ID_PEDIDO")
	private Integer id;
	private Instant fechaPedido;

	/*
	 * @OneToMany(cascade = CascadeType.PERSIST,targetEntity = DetallePedido.class)
	 */
	@OneToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
	@JoinColumn(name = "ID_PEDIDO")
	private List<DetallePedido> detalle = new ArrayList<>();

	@OneToOne
	@JoinColumn(name = "ID_ESTADO_PEDIDO")
	private EstadoPedido estado;

	@OneToOne
	@JoinColumn(name = "ID_OBRA")
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
