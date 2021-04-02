package dan.ms.pedidos.domain;

public class EstadoPedido {
	Integer id;
	String estado;
	
	public EstadoPedido(Integer id, String estado) {
		super();
		this.id = id;
		this.estado = estado;
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}
	
	

}
