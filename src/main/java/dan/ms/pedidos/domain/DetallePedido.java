package dan.ms.pedidos.domain;

public class DetallePedido {
Integer id;
Integer Cantidad;
Double precio;
Producto producto;

public DetallePedido(Integer id, Integer cantidad, Double precio, Producto producto) {
	super();
	this.id = id;
	Cantidad = cantidad;
	this.precio = precio;
	this.producto = producto;
}

public Integer getId() {
	return id;
}
public void setId(Integer id) {
	this.id = id;
}
public Integer getCantidad() {
	return Cantidad;
}
public void setCantidad(Integer cantidad) {
	Cantidad = cantidad;
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

	

}
