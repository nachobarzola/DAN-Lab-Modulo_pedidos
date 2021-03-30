package dan.ms.pedidos.domain;

public class Obra {
	private Integer id;
	private String descripcion;
	
	public Obra(Integer id, String descripcion) {
		super();
		this.id = id;
		this.descripcion = descripcion;
	}

	public Obra() {
		super();
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
	
	
}
