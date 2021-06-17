package dan.ms.pedidos.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@Entity
public class DetallePedido {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) //valor autonumerico
	@Column(name="ID_DETALLE_PEDIDO")
	private Integer id;
	
	private Integer cantidad;
	private Double precio;
	
	@OneToOne
	@JoinColumn(name="ID_PRODUCTO")
	private Producto producto;
	
	
	
	public DetallePedido(Integer cantidad, Double precio, Producto producto) {
		super();
		this.cantidad = cantidad;
		this.precio = precio;
		this.producto = producto;
	}

	public DetallePedido() {
		super();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getCantidad() {
		return cantidad;
	}

	public void setCantidad(Integer cantidad) {
		this.cantidad = cantidad;
	}

	public Double getPrecio() {
		return precio;
	}

	public void setPrecio(Double precio) {
		this.precio = precio;
	}

	public Producto getProducto() {
		return producto;
	}

	public void setProducto(Producto producto) {
		this.producto = producto;
	}

	@Override
	public String toString() {
		return "DetallePedido [id=" + id + ", cantidad=" + cantidad + ", precio=" + precio + ", producto=" + producto.toString()
				+ "]";
	}	
	
	
}
