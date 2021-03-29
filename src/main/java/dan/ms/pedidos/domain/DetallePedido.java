package dan.ms.pedidos.domain;

public class DetallePedido {
	private Integer id;
	private Integer cantidad;
	private Double precio;
	private Producto producto;
	
	public DetallePedido(Integer id, Integer cantidad, Double precio, Producto producto) {
		super();
		this.id = id;
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
		return "DetallePedido [id=" + id + ", cantidad=" + cantidad + ", precio=" + precio + ", producto=" + producto
				+ "]";
	}	
	
	
}
