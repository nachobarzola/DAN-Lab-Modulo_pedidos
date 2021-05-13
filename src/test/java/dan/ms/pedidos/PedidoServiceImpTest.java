package dan.ms.pedidos;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import dan.ms.pedidos.domain.DetallePedido;
import dan.ms.pedidos.domain.Obra;
import dan.ms.pedidos.domain.Pedido;
import dan.ms.pedidos.domain.Producto;
import dan.ms.pedidos.excepciones.ExceptionRechazoPedido;
import dan.ms.pedidos.services.interfaces.PedidoService;
import dan.ms.persistence.repositories.PedidoRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class PedidoServiceImpTest {

	@Autowired
	private PedidoService pedidoService;

	@Autowired
	private PedidoRepository pedidoRepo;

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
		p1.setFechaPedido(Instant.now());
		p1.setId(1);

		// Persisto el pedido
		Pedido pReturn = pedidoService.guardarPedido(p1);
		
		assertEquals(p1, pReturn);
		
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
		p1.setFechaPedido(Instant.now());
		p1.setId(1);

		// Persisto el pedido
		pedidoService.guardarPedido(p1);

		// Lo busco a donde se persistio
		Optional<Pedido> optP = pedidoService.buscarPorId(p1.getId());
		
		assertEquals(p1, optP.get());

	}

}
