package dan.ms.pedidos;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import dan.ms.pedidos.domain.DetallePedido;
import dan.ms.pedidos.domain.Obra;
import dan.ms.pedidos.domain.Pedido;
import dan.ms.pedidos.domain.Producto;
import dan.ms.pedidos.services.dao.DetallePedidoRepository;
import dan.ms.pedidos.services.dao.EstadoPedidoRepository;
import dan.ms.pedidos.services.dao.PedidoRepository;
import dan.ms.pedidos.services.interfaces.PedidoService;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class DetallePedidoRestTest {

	private String ENDPOINT_DETALLE_PEDIDO = "/api/detallePedido";

	private String ENDPOINT_PEDIDO = "/api/pedido";

	@Autowired
	PedidoRepository pedidoRepo;

	@Autowired
	DetallePedidoRepository detalleRepo;

	@Autowired
	TestRestTemplate testRestTemplate;

	@Autowired
	private PedidoService pedidoService;

	@LocalServerPort
	String puerto;

	@BeforeEach
	void borrar_repositorio() {
		pedidoRepo.deleteAll();
		detalleRepo.deleteAll();
	}

	@Test
	void agregarDetallePedido() {

		// Obtengo la lista de detalles del pedido que se creó

		Pedido p = crearPedido();

		String server = "http://localhost:" + puerto + ENDPOINT_DETALLE_PEDIDO + "/pedido/" + p.getId();

		// Seteo los productos ya guardados en la base de datos con el .sql

		Producto p1 = new Producto();
		p1.setId(4);
		p1.setPrecio(4.5);
		p1.setDescripcion("El cuarto producto que creo");

		// seteo el producto en el detalle y demas parametros del detalle

		DetallePedido detalle = new DetallePedido();
		detalle.setCantidad(8);
		detalle.setPrecio(36.0);
		detalle.setProducto(p1);

		// Agrego el detalle con el api rest

		HttpEntity<DetallePedido> requestPedido = new HttpEntity<>(detalle);
		ResponseEntity<Pedido> respuesta = testRestTemplate.exchange(server, HttpMethod.POST, requestPedido,
				Pedido.class);

		assertTrue(respuesta.getStatusCode().equals(HttpStatus.OK));

		// Busco en la bdd el pedido y obtengo los detalles
		List<DetallePedido> dpDB = pedidoService.buscarPorId(p.getId()).get().getDetalle();

		List<DetallePedido> dp = p.getDetalle();

		// Controlo que no sean iguales
		assertNotEquals(dp, dpDB);
		// Agrego el objeto a mi lista de detalles local
		dp.add(respuesta.getBody().getDetalle().get(3));
		assertEquals(dp.size(), dpDB.size());

		// Comparo objeto por objeto que sean iguales
		for (int i = 0; i < dp.size(); i++) {
			assertEquals(dp.get(i).getProducto().getDescripcion(), dpDB.get(i).getProducto().getDescripcion());
			assertEquals(dp.get(i).getProducto().getId(), dpDB.get(i).getProducto().getId());
			assertEquals(dp.get(i).getProducto().getPrecio(), dpDB.get(i).getProducto().getPrecio());
			assertEquals(dp.get(i).getCantidad(), dpDB.get(i).getCantidad());
			assertEquals(dp.get(i).getPrecio(), dpDB.get(i).getPrecio());
			assertEquals(dp.get(i).getId(), dpDB.get(i).getId());
		}
	}

	@Test
	void actualizarDetallePedido() {


		Pedido ped = crearPedido();
		
		String server = "http://localhost:" + puerto + ENDPOINT_DETALLE_PEDIDO + "/pedido/" + ped.getId()
				+ "/detalle/"+ped.getDetalle().get(1).getId();

		Producto p4 = new Producto();
		p4.setId(4);
		p4.setPrecio(4.5);
		p4.setDescripcion("El cuarto producto que creo");

		DetallePedido dp2 = new DetallePedido();
		dp2.setCantidad(3);
		dp2.setPrecio(13.5);
		dp2.setProducto(p4);
		dp2.setId(ped.getDetalle().get(1).getId());

		/*
		 * DetallePedidoViejo (Producto2) dp2.setId(2); dp2.setCantidad(1);
		 * dp2.setPrecio(2.5);
		 * 
		 * dp2.setProducto(pd2);
		 */

		HttpEntity<DetallePedido> requestPedido = new HttpEntity<>(dp2);
		ResponseEntity<DetallePedido> respuesta = testRestTemplate.exchange(server, HttpMethod.PUT, requestPedido,
				DetallePedido.class);

		assertTrue(respuesta.getStatusCode().equals(HttpStatus.OK));

		List<DetallePedido> dpDB = pedidoService.buscarPorId(ped.getId()).get().getDetalle();

		List<DetallePedido> dp = ped.getDetalle();

		assertNotEquals(dp, dpDB);
		dp.set(1, dp2);
		assertEquals(dp.size(), dpDB.size());
		for (int i = 0; i < dp.size(); i++) {
			assertEquals(dp.get(i).getProducto().getDescripcion(), dpDB.get(i).getProducto().getDescripcion());
			assertEquals(dp.get(i).getProducto().getId(), dpDB.get(i).getProducto().getId());
			assertEquals(dp.get(i).getProducto().getPrecio(), dpDB.get(i).getProducto().getPrecio());
			assertEquals(dp.get(i).getCantidad(), dpDB.get(i).getCantidad());
			assertEquals(dp.get(i).getPrecio(), dpDB.get(i).getPrecio());
			assertEquals(dp.get(i).getId(), dpDB.get(i).getId());
		}
	}

	@Test
	void borrarDetallePedido() {


		Pedido ped = crearPedido();
		
		String server = "http://localhost:" + puerto + ENDPOINT_DETALLE_PEDIDO + "/pedido/" + ped.getId()
				+ "/detalle/"+ped.getDetalle().get(1).getId();

		
		ResponseEntity <List<DetallePedido>> respuesta = testRestTemplate.exchange(server, HttpMethod.DELETE, null,
				new ParameterizedTypeReference<List<DetallePedido>>() {});

		assertTrue(respuesta.getStatusCode().equals(HttpStatus.OK));

		List<DetallePedido> dpDB = pedidoService.buscarPorId(ped.getId()).get().getDetalle();

		List<DetallePedido> dp = ped.getDetalle();

		
		assertNotEquals(dp, dpDB);
		
		dp.remove(1);
		
		assertEquals(dp.size(), dpDB.size());
		for (int i = 0; i < dp.size(); i++) {
			assertEquals(dp.get(i).getProducto().getDescripcion(), dpDB.get(i).getProducto().getDescripcion());
			assertEquals(dp.get(i).getProducto().getId(), dpDB.get(i).getProducto().getId());
			assertEquals(dp.get(i).getProducto().getPrecio(), dpDB.get(i).getProducto().getPrecio());
			assertEquals(dp.get(i).getCantidad(), dpDB.get(i).getCantidad());
			assertEquals(dp.get(i).getPrecio(), dpDB.get(i).getPrecio());
			assertEquals(dp.get(i).getId(), dpDB.get(i).getId());
		}
	}
	
	private Pedido crearPedido() {
		String server = "http://localhost:" + puerto + ENDPOINT_PEDIDO;
		/*
		 * Un cliente puede realizar un pedido de una lista de productos, para ello
		 * indica la cantidad de productos y la obra a la que se deben enviar.
		 */

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
		dp1.setPrecio(3.0);
		dp2.setId(2);
		dp2.setCantidad(1);
		dp2.setPrecio(2.5);
		dp3.setId(3);
		dp3.setCantidad(3);
		dp3.setPrecio(10.5);

		// Le asigno una obra

		Obra ob = new Obra();
		ob.setId(1);
		ob.setDescripcion("La famosa obra chiquitita");

		p1.addDetalle(dp1);
		p1.addDetalle(dp2);
		p1.addDetalle(dp3);
		p1.setObra(ob);
		p1.setId(1);
		HttpEntity<Pedido> requestPedido = new HttpEntity<>(p1);
		ResponseEntity<Pedido> respuesta = testRestTemplate.exchange(server, HttpMethod.POST, requestPedido,
				Pedido.class);

		return respuesta.getBody();
	}

}
