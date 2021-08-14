package dan.ms.pedidos.services;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import dan.ms.pedidos.domain.Obra;
import dan.ms.pedidos.services.interfaces.ObraRestExternoService;

@Service
public class ObraRestExternoServiceImp implements ObraRestExternoService {
	@SuppressWarnings("rawtypes")
	@Autowired
    CircuitBreakerFactory circuitBreakerFactory;
	private static String API_REST_USUARIO = "http://localhost:9000/api";
	private static String ENDPOINT_OBRA = "/obra";
	
	String uri = API_REST_USUARIO + ENDPOINT_OBRA;
	
	
	@Override
	public List<Obra> obtenerObrasDeUnCliente(String clientParams) {
		CircuitBreaker circuitBreaker = circuitBreakerFactory.create("circuitbreaker");
		RestTemplate restUsuario = new RestTemplate();
		uri = uri + clientParams;
		Obra[] respuesta = circuitBreaker.run(() -> 
		restUsuario.getForObject(uri,Obra[].class),
		throwable -> defaultResponse());

		//ResponseEntity<Obra[]> respuesta = restUsuario.exchange(clientParam, HttpMethod.GET, null, Obra[].class);
		if(respuesta == null) {
			return null;
		}
		List<Obra> obrasRespuestaLista = Arrays.asList(respuesta);
		
		return obrasRespuestaLista;
	}

	public Obra[] defaultResponse() {
		return null;
			
	}
}
