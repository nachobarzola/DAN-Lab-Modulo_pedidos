package dan.ms.pedidos.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.springframework.transaction.annotation.Transactional;


@Entity
@Transactional(readOnly = true)
public class Producto {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // valor autonumerico
	@Column(name = "ID_PRODUCTO")
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
