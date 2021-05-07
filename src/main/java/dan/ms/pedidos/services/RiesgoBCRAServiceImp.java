package dan.ms.pedidos.services;

import org.springframework.stereotype.Service;

import dan.ms.pedidos.services.interfaces.RiesgoBCRAService;

@Service
public class RiesgoBCRAServiceImp implements RiesgoBCRAService {

	@Override
	public Integer estadoCrediticio() {
		return estadoAceptado();
	}
	
	private Integer estadoAceptado() {

		return NORMAL;

	}

	private Integer estadoRechazado() {

		return MEDIO;
	} 

}
