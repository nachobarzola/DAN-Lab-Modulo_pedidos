package dan.ms.pedidos.domain;

import javax.persistence.Entity;
import javax.persistence.Id;


@Entity
public class Producto {
	@Id
	private Integer id;
	private String descripcion;
	private Double precio;

	public Producto(Integer id, String descripcion, Double precio) {
		super();
		this.id = id;
		this.descripcion = descripcion;
		this.precio = precio;
	}

	public Producto() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public Double getPrecio() {
		return precio;
	}

	public void setPrecio(Double precio) {
		this.precio = precio;
	}

}
