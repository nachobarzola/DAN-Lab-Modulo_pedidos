package dan.ms.pedidos.services;

import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import dan.ms.pedidos.domain.DetallePedido;
import dan.ms.pedidos.services.interfaces.ProductoRestExternoService;

@Service
public class ProductoRestExternoServiceImp implements ProductoRestExternoService {

	private static String API_REST_PRODUCTO = "http://localhost:9001/";
	private static String ENDPOINT_PRODUCTO = "api/producto";

    //TODO: hay que testear este metodo
	@Override
	public Boolean hayStockDisponible(List<DetallePedido> detP) {

		RestTemplate restProducto = new RestTemplate();
		String uri = API_REST_PRODUCTO + ENDPOINT_PRODUCTO;
		//
		ResponseEntity<Object[]> respuesta;

		//

		uri = uri + "/detallePedido";

		HttpEntity<List<DetallePedido>> requestDetallePedido = new HttpEntity<>(detP);
		respuesta = restProducto.exchange(uri, HttpMethod.GET, requestDetallePedido, Object[].class);

		if (respuesta.getStatusCode().equals(HttpStatus.OK)) {
			return true;
		} else {
			return false;
		}

	}

}
