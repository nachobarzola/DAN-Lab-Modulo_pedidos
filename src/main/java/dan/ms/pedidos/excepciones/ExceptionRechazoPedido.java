package dan.ms.pedidos.excepciones;

import dan.ms.pedidos.domain.Pedido;

public class ExceptionRechazoPedido extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String msjException;

	public ExceptionRechazoPedido(Pedido ped) {

		this.msjException = "Se rechazo el pedido: " + ped.toString();
	}

	public String getMessage() {
		return msjException;
	}

}
