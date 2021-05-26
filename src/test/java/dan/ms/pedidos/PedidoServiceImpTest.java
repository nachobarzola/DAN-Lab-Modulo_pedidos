package dan.ms.pedidos;

import static org.junit.jupiter.api.Assertions.assertEquals;


import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import dan.ms.pedidos.domain.DetallePedido;
import dan.ms.pedidos.domain.Obra;
import dan.ms.pedidos.domain.Pedido;
import dan.ms.pedidos.domain.Producto;
import dan.ms.pedidos.excepciones.ExceptionRechazoPedido;
import dan.ms.pedidos.services.interfaces.PedidoService;
import dan.ms.persistence.repositories.PedidoRepositoryInMemory;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class PedidoServiceImpTest {

	@Autowired
	private PedidoService pedidoService;

	@Autowired
	private PedidoRepositoryInMemory pedidoRepo;

	@BeforeEach
	void borrar_repositorio() {
		pedidoRepo.deleteAll();
	}

	// Test de integracion con DB/repositorio
	@Test
	public void guardarPedido() throws ExceptionRechazoPedido {

		// Creo un pedido
		Pedido p1 = new Pedido();

		// Creo detalles de pedido para la lista del pedido
		DetallePedido dp1 = new DetallePedido();
		DetallePedido dp2 = new DetallePedido();
		DetallePedido dp3 = new DetallePedido();

		// Creo un producto por cada detalle
		Producto pd1 = new Producto();
		pd1.setDescripcion("El primer producto que creo");
		pd1.setPrecio(1.5);
		pd1.setId(1);
		Producto pd2 = new Producto();
		pd2.setDescripcion("El segundo producto que creo");
		pd2.setPrecio(2.5);
		pd2.setId(2);
		Producto pd3 = new Producto();
		pd3.setDescripcion("El tercer producto que creo");
		pd3.setPrecio(3.5);
		pd3.setId(3);
		
				

		// seteo cada producto a un detalle
		dp1.setProducto(pd1);
		dp2.setProducto(pd2);
		dp3.setProducto(pd3);

		// seteo cantidades y precios totales de producto
		dp1.setId(1);
		dp1.setCantidad(2);
		dp1.setPrecio(301.0);
		dp2.setId(2);
		dp2.setCantidad(1);
		dp2.setPrecio(108.5);
		dp3.setId(3);
		dp3.setCantidad(3);
		dp3.setPrecio(181.5);

		// Creo una obra

		Obra ob = new Obra();
		ob.setId(1);
		ob.setDescripcion("La famosa obra chiquitita");

		p1.addDetalle(dp1);
		p1.addDetalle(dp2);
		p1.addDetalle(dp3);
		p1.setObra(ob);
		
		p1.setId(1);

		// Persisto el pedido
		Pedido pReturn = pedidoService.guardarPedido(p1).get();
		
		pReturn.setFechaPedido(null);
		p1.setFechaPedido(null);
		ObjectMapper mapper = new ObjectMapper();
		try {
			String valueP1 = mapper.writeValueAsString(p1);
			String valueOptP = mapper.writeValueAsString(pReturn);
			assertEquals(valueP1,valueOptP);
		} catch (JsonProcessingException e) {
			
		}
		
		
		
		
	}

	// Test de intergracion con DB/repositorio
	@Test
	public void buscarPedido() throws ExceptionRechazoPedido {
		// Creo un pedido
		Pedido p1 = new Pedido();

		// Creo detalles de pedido para la lista del pedido
		DetallePedido dp1 = new DetallePedido();
		DetallePedido dp2 = new DetallePedido();
		DetallePedido dp3 = new DetallePedido();
		DetallePedido dp4 = new DetallePedido();

		// Creo un producto por cada detalle

		Producto pd1 = new Producto();
		pd1.setDescripcion("El primer producto que creo");
		pd1.setPrecio(1.5);
		pd1.setId(1);
		Producto pd2 = new Producto();
		pd2.setDescripcion("El segundo producto que creo");
		pd2.setPrecio(2.5);
		pd2.setId(2);
		Producto pd3 = new Producto();
		pd3.setDescripcion("El tercer producto que creo");
		pd3.setPrecio(3.5);
		pd3.setId(3);
		
		Producto pd4 = new Producto();
		pd4.setDescripcion("El cuarto producto que creo");
		pd4.setPrecio(4.5);
		pd4.setId(4);

		// seteo cada producto a un detalle
		dp1.setProducto(pd1);
		dp2.setProducto(pd2);
		dp3.setProducto(pd3);
		dp4.setProducto(pd4);

		// seteo cantidades y precios totales de producto
		dp1.setId(1);
		dp1.setCantidad(2);
		dp1.setPrecio(3.0);
		dp2.setId(2);
		dp2.setCantidad(1);
		dp2.setPrecio(2.5);
		dp3.setId(3);
		dp3.setCantidad(3);
		dp3.setPrecio(10.5);
		dp4.setId(4);
		dp4.setCantidad(3);
		dp4.setPrecio(13.5);

		// Creo una obra

		Obra ob = new Obra();
		ob.setId(1);
		ob.setDescripcion("La famosa obra chiquitita");

		p1.addDetalle(dp1);
		p1.addDetalle(dp2);
		p1.addDetalle(dp3);
		p1.addDetalle(dp4);
		p1.setObra(ob);
		
		p1.setId(1);

		// Persisto el pedido
		pedidoService.guardarPedido(p1);
 
		//TODO: problema con time stamp. Comparar todos los campos menos ese
		// Lo busco a donde se persistio
		Optional<Pedido> optP = pedidoService.buscarPorId(p1.getId());
		optP.get().setFechaPedido(null);
		p1.setFechaPedido(null);
		
		
		ObjectMapper mapper = new ObjectMapper();
		try {
			String valueP1 = mapper.writeValueAsString(p1);
			String valueOptP = mapper.writeValueAsString(optP.get());
			assertEquals(valueP1,valueOptP);
		} catch (JsonProcessingException e) {
		
		}
	}

}
