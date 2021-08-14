package dan.ms.pedidos.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import dan.ms.pedidos.domain.DetallePedido;
import dan.ms.pedidos.services.interfaces.ProductoRestExternoService;



@Service
public class ProductoRestExternoServiceImp implements ProductoRestExternoService {
	@SuppressWarnings("rawtypes")
	@Autowired
    CircuitBreakerFactory circuitBreakerFactory;
	
	@Autowired
	RestTemplate restProducto;
	
	private static String API_REST_PRODUCTO = "http://modulo-productos/";
	private static String ENDPOINT_PRODUCTO = "api/producto";

	@Override
	public Boolean hayStockDisponible(List<DetallePedido> detP) {
		CircuitBreaker circuitBreaker = circuitBreakerFactory.create("circuitbreaker");

		String uri = API_REST_PRODUCTO + ENDPOINT_PRODUCTO +"/detallePedido";
		//
		Boolean respuesta;

		//


		HttpEntity<List<DetallePedido>> requestDetallePedido = new HttpEntity<>(detP);
		respuesta = circuitBreaker.run(
				() -> restProducto.postForObject(uri,requestDetallePedido, Boolean.class) ,
				throwable -> defaultResponse()
				);

		return respuesta;
	}
	
	public Boolean defaultResponse() {
		System.out.println("Se activo el cricuit breaker");
		return false;
		}


}
