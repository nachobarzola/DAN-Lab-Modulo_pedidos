package dan.ms.pedidos;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import dan.ms.pedidos.domain.DetallePedido;
import dan.ms.pedidos.domain.Obra;
import dan.ms.pedidos.domain.Pedido;
import dan.ms.pedidos.domain.Producto;
import dan.ms.pedidos.services.dao.EstadoPedidoRepository;
import dan.ms.pedidos.services.dao.PedidoRepository;
import dan.ms.pedidos.services.interfaces.PedidoService;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class PedidoRestTest {

	private String ENDPOINT_PEDIDO = "/api/pedido";

	/*
	 * @Autowired PedidoRepositoryInMemory pedidoRepo;
	 */
	@Autowired
	PedidoRepository pedidoRepo;

	@Autowired
	EstadoPedidoRepository estadoRepo;

	@Autowired
	TestRestTemplate testRestTemplate;

	@Autowired
	private PedidoService pedidoService;

	@LocalServerPort
	String puerto;

	@BeforeEach
	void borrar_repositorio() {
		pedidoRepo.deleteAll();

	}

	@Test
	void crear_pedidoCompleto() {
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
		pd1.setId(1);
		Producto pd2 = new Producto();
		pd2.setId(2);
		Producto pd3 = new Producto();
		pd3.setId(3);

		// seteo cada producto a un detalle
		dp1.setProducto(pd1);
		dp2.setProducto(pd2);
		dp3.setProducto(pd3);

		// seteo cantidades y precios totales de producto
		dp1.setCantidad(2);
		dp1.setPrecio(301.0);
		dp2.setCantidad(1);
		dp2.setPrecio(108.5);
		dp3.setCantidad(3);
		dp3.setPrecio(181.5);

		// Le asigno una obra

		Obra ob = new Obra();
		ob.setId(1);
		ob.setDescripcion("La famosa obra chiquitita");

		p1.addDetalle(dp1);
		p1.addDetalle(dp2);
		p1.addDetalle(dp3);
		p1.setObra(ob);
		p1.setFechaPedido(Date.from(Instant.now()));
		p1.setId(1);

		HttpEntity<Pedido> requestPedido = new HttpEntity<>(p1);
		ResponseEntity<Pedido> respuesta = testRestTemplate.exchange(server, HttpMethod.POST, requestPedido,
				Pedido.class);

		assertTrue(respuesta.getStatusCode().equals(HttpStatus.OK));

		// Chequeo que este persistido
		Optional<Pedido> cli = pedidoService.buscarPorId(respuesta.getBody().getId());
		// assertEquals(p1, cli.get()); Nunca van a ser iguales porque se agrega un
		// estado
		assertNotEquals(Optional.empty(), cli);

		pedidoService.borrarPedido(respuesta.getBody());

	}

	@Test
	void crear_pedidoIncompleto_faltaObra() {
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
		pd1.setDescripcion("Un productito bien falso");
		pd1.setPrecio(150.5);
		pd1.setId(1);
		Producto pd2 = new Producto();
		pd2.setDescripcion("Un producto bien mentiroso");
		pd2.setPrecio(108.5);
		pd2.setId(2);
		Producto pd3 = new Producto();
		pd3.setDescripcion("Falsolin");
		pd3.setPrecio(60.5);
		pd3.setId(3);

		// seteo cada producto a un detalle
		dp1.setProducto(pd1);
		dp2.setProducto(pd2);
		dp3.setProducto(pd3);

		// seteo cantidades y precios totales de producto
		dp1.setCantidad(2);
		dp1.setPrecio(301.0);
		dp2.setCantidad(1);
		dp2.setPrecio(108.5);
		dp3.setCantidad(3);
		dp3.setPrecio(181.5);

		// Agrego los detalles de pedido a la lista de pedidos

		p1.addDetalle(dp1);
		p1.addDetalle(dp2);
		p1.addDetalle(dp3);
		p1.setFechaPedido(Date.from(Instant.now()));

		HttpEntity<Pedido> requestPedido = new HttpEntity<>(p1);
		ResponseEntity<Pedido> respuesta = testRestTemplate.exchange(server, HttpMethod.POST, requestPedido,
				Pedido.class);

		assertTrue(respuesta.getStatusCode().equals(HttpStatus.BAD_REQUEST));

	}

	@Test
	void crear_pedidoIncompleto_faltaDetallePedido() {
		String server = "http://localhost:" + puerto + ENDPOINT_PEDIDO;
		/*
		 * Crear un pedido sin detalles
		 */

		// Creo un pedido
		Pedido p1 = new Pedido();

		// Creo una obra
		Obra ob = new Obra();
		ob.setId(1);
		ob.setDescripcion("La famosa obra chiquitita");

		p1.setFechaPedido(Date.from(Instant.now()));
		p1.setObra(ob);
		p1.setId(1);

		HttpEntity<Pedido> requestPedido = new HttpEntity<>(p1);
		ResponseEntity<Pedido> respuesta = testRestTemplate.exchange(server, HttpMethod.POST, requestPedido,
				Pedido.class);

		assertTrue(respuesta.getStatusCode().equals(HttpStatus.BAD_REQUEST));

	}

	@Test
	void crear_pedidoIncompleto_faltaAlgunaCantidaDeProducto() {
		String server = "http://localhost:" + puerto + ENDPOINT_PEDIDO;
		/*
		 * Crear un pedido sin detalles
		 */

		// Creo un pedido
		Pedido p1 = new Pedido();

		// Creo detalles de pedido para la lista del pedido
		DetallePedido dp1 = new DetallePedido();
		DetallePedido dp2 = new DetallePedido();
		DetallePedido dp3 = new DetallePedido();

		// Creo un producto por cada detalle

		Producto pd1 = new Producto();
		pd1.setDescripcion("Un productito bien falso");
		pd1.setPrecio(150.5);
		pd1.setId(1);
		Producto pd2 = new Producto();
		pd2.setDescripcion("Un producto bien mentiroso");
		pd2.setPrecio(108.5);
		pd2.setId(2);
		Producto pd3 = new Producto();
		pd3.setDescripcion("Falsolin");
		pd3.setPrecio(60.5);
		pd3.setId(3);

		// seteo cada producto a un detalle
		dp1.setProducto(pd1);
		dp2.setProducto(pd2);
		dp3.setProducto(pd3);

		// seteo cantidades y precios totales de producto menos cantidad de producto 1

		dp1.setPrecio(301.0);
		dp2.setCantidad(1);
		dp2.setPrecio(108.5);
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
		p1.setFechaPedido(Date.from(Instant.now()));
		p1.setId(1);

		HttpEntity<Pedido> requestPedido = new HttpEntity<>(p1);
		ResponseEntity<Pedido> respuesta = testRestTemplate.exchange(server, HttpMethod.POST, requestPedido,
				Pedido.class);

		assertTrue(respuesta.getStatusCode().equals(HttpStatus.BAD_REQUEST));

	}
}
