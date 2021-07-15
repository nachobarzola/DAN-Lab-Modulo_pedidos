package dan.ms.pedidos;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import dan.ms.pedidos.domain.DetallePedido;
import dan.ms.pedidos.domain.EstadoPedido;
import dan.ms.pedidos.domain.Obra;
import dan.ms.pedidos.domain.Pedido;
import dan.ms.pedidos.domain.Producto;
import dan.ms.pedidos.excepciones.ExceptionRechazoPedido;
import dan.ms.pedidos.services.dao.ObraRepository;
import dan.ms.pedidos.services.interfaces.EstadoPedidoService;
import dan.ms.pedidos.services.interfaces.PedidoService;
import dan.ms.persistence.repositories.PedidoRepositoryInMemory;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("testing")
public class PedidoServiceImpTest {

	@Autowired
	private PedidoService pedidoService;

	@Autowired
	private PedidoRepositoryInMemory pedidoRepo;

	@Autowired
	EstadoPedidoService estadoPedidoService;

	@Autowired
	ObraRepository obraRepo;
	
	@LocalServerPort
	String puerto;

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

		// Creo una obra

		Obra ob = new Obra();
		ob.setId(1);

		p1.addDetalle(dp1);
		p1.addDetalle(dp2);
		p1.addDetalle(dp3);
		p1.setObra(ob);

		// Persisto el pedido
		Pedido pReturn = pedidoService.guardarPedido(p1).get();

		p1.setId(pReturn.getId());

		p1.setObra(obraRepo.findById(p1.getObra().getId()).get());

		pReturn.setFechaPedido(null);
		p1.setFechaPedido(null);
		ObjectMapper mapper = new ObjectMapper();
		try {
			String valueP1 = mapper.writeValueAsString(p1);
			String valueOptP = mapper.writeValueAsString(pReturn);
			assertEquals(valueP1, valueOptP);
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
		pd1.setId(1);
		Producto pd2 = new Producto();
		pd2.setId(2);
		Producto pd3 = new Producto();
		pd3.setId(3);
		Producto pd4 = new Producto();
		pd4.setId(4);

		// seteo cada producto a un detalle
		dp1.setProducto(pd1);
		dp2.setProducto(pd2);
		dp3.setProducto(pd3);
		dp4.setProducto(pd4);

		// seteo cantidades y precios totales de producto
		dp1.setCantidad(2);
		dp1.setPrecio(301.0);
		dp2.setCantidad(1);
		dp2.setPrecio(108.5);
		dp3.setCantidad(3);
		dp3.setPrecio(181.5);
		dp4.setCantidad(50);
		dp4.setPrecio(50.55);

		// Le asigno una obra

		Obra ob = new Obra();
		ob.setId(1);

		p1.addDetalle(dp1);
		p1.addDetalle(dp2);
		p1.addDetalle(dp3);
		p1.setObra(ob);

		p1.setFechaPedido(Date.from(Instant.now()));

		EstadoPedido estP = estadoPedidoService.obtenerEstadoPedidoPorDescripcion("NUEVO");
		p1.setEstado(estP);

		// Persisto el pedido
		Optional<Pedido> p = pedidoService.guardarPedido(p1);
		p1.setId(p.get().getId());
		// TODO: problema con time stamp. Comparar todos los campos menos ese
		// Lo busco a donde se persistio
		Optional<Pedido> optP = pedidoService.buscarPorId(p1.getId());
		optP.get().setFechaPedido(null);
		p.get().setFechaPedido(null);

		ObjectMapper mapper = new ObjectMapper();
		try {
			String valueP = mapper.writeValueAsString(p.get());
			String valueOptP = mapper.writeValueAsString(optP.get());
			assertEquals(valueP, valueOptP);
		} catch (JsonProcessingException e) {

		}
	}

}
