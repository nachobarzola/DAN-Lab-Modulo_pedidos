package dan.ms.pedidos.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import dan.ms.pedidos.domain.DetallePedido;
import dan.ms.pedidos.domain.EstadoPedido;
import dan.ms.pedidos.domain.Obra;
import dan.ms.pedidos.domain.Pedido;
import dan.ms.pedidos.excepciones.ExceptionRechazoPedido;
import dan.ms.pedidos.services.interfaces.EstadoPedidoService;
import dan.ms.pedidos.services.interfaces.PedidoService;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api/pedido")
public class PedidoRest {
	private static String API_REST_USUARIO = "http://localhost:9000/api";
	private static String ENDPOINT_OBRA = "/obra";

	@Autowired
	EstadoPedidoService estadoPedidoService;

	@Autowired
	PedidoService pedidoService;

	@PostMapping
	@ApiOperation(value = "Crea un pedido")
	public ResponseEntity<Pedido> crear(@RequestBody Pedido pedido) {

		/*
		 * . . El servicio REST verifique que cuando un Pedido es recibido para ser
		 * CREADO posee información de la obra, posee al menos un detalle y el detalle
		 * posee datos de producto y cantidad
		 */

		List<DetallePedido> ldp = pedido.getDetalle();

		if (pedido.getObra() != null && ldp != null && ldp.size() > 0 && pedido.getFechaPedido() != null) {
			List<DetallePedido> detallePedido = ldp.stream().filter(unDetalle -> unDetalle.getCantidad() != null)
					.filter(unDetalle -> unDetalle.getProducto() != null).collect(Collectors.toList());
			if (detallePedido.size() == ldp.size()) {

				EstadoPedido estP = estadoPedidoService.obtenerEstadoPedidoPorDescripcion("NUEVO");
				pedido.setEstado(estP);
				pedido.setId(null);
				for (DetallePedido dp : pedido.getDetalle()) {
					dp.setId(null);
				}
				return ResponseEntity.ok(pedidoService.guardarPedido(pedido).get());

			}

		}

		return ResponseEntity.badRequest().build();

	}

	@PutMapping(path = "/estado/{idPedido}")
	@ApiOperation(value = "Actualizar estado de pedido")
	public ResponseEntity<Pedido> actualizarEstadoPedido(@RequestBody EstadoPedido estadoPedido,
			@PathVariable Integer idPedido) {
		Optional<Pedido> ped = pedidoService.buscarPorId(idPedido);

		// Se puede cambiar cualquier estado a nuevo.

		if (ped.isPresent()) {

			switch (estadoPedido.getEstado()) {
			case ("NUEVO"): {

				ped.get().setEstado(estadoPedido);

				break;
			}

			// Si se cambia el estado a “CONFIRMADO” el servicio que lo reciba deberá luego
			// guardarlo como: ACEPTADO, PENDIENTE, o RECHAZADO de acuerdo a las
			// siguientes regla
			case ("CONFIRMADO"): {
				try {
					// Ponemos el pedido en la cola
					ped = pedidoService.evaluarEstadoPedido(ped.get());

				} catch (ExceptionRechazoPedido e) {
					return ResponseEntity.badRequest().build();
				}

				break;
			}
			default: {
				return ResponseEntity.notFound().build();
			}

			}

			return ResponseEntity.ok(pedidoService.actualizarPedido(ped.get()).get());

		}

		return ResponseEntity.notFound().build();

	}

	@PutMapping(path = "/{idPedido}")
	@ApiOperation(value = "Actualiza pedido dado un id")
	public ResponseEntity<Pedido> actualizar(@RequestBody Pedido pedido, @PathVariable Integer idPedido) {

		// TODO: Al actualizar el pedido, se reemplaza todo, tambien si tenia varios
		// detalles y aca pongo uno
		// entonces, los demas quedan en null. Esto esta bien??
		Optional<Pedido> ped = pedidoService.buscarPorId(idPedido);
		if (ped.isPresent()) {
			Pedido p = ped.get();
			p.setDetalle(pedido.getDetalle());
			p.setEstado(pedido.getEstado());
			p.setFechaPedido(pedido.getFechaPedido());
			p.setObra(pedido.getObra());

			return ResponseEntity.ok(pedidoService.actualizarPedido(p).get());

		}
		return ResponseEntity.notFound().build();

	}

	@DeleteMapping(path = "/{idPedido}")
	@ApiOperation(value = "Borra un pedido dado un id")
	public ResponseEntity<Pedido> borrar(@PathVariable Integer idPedido) {

		Optional<Pedido> pe = pedidoService.buscarPorId(idPedido);

		if (pe.isPresent()) {
			pedidoService.borrarPedido(pe.get());
			return ResponseEntity.ok().build();
		}
		return ResponseEntity.notFound().build();

	}

	@GetMapping("/estado/{idEstado}")
	@ApiOperation(value = "Obtiene los pedidos por id de estadoPedido")

	public ResponseEntity<List<Pedido>> getPorEstado(@PathVariable Integer idEstado) {

		EstadoPedido e = new EstadoPedido();
		e.setId(idEstado);
		Optional<List<Pedido>> listPe = pedidoService.buscarPorEstado(e);

		if (listPe.isPresent()) {
			return ResponseEntity.ok(listPe.get());
		}
		return ResponseEntity.notFound().build();

	}

	@GetMapping(path = "/{idPedido}")
	@ApiOperation(value = "Obtener pedido por su ID")
	public ResponseEntity<Pedido> getPorId(@PathVariable Integer idPedido) {
		Optional<Pedido> pedido = pedidoService.buscarPorId(idPedido);
		return ResponseEntity.of(pedido);
	}

	@GetMapping(path = "/obra/{idObra}")
	@ApiOperation(value = "Obtener pedido dado el id de la obra")
	public ResponseEntity<List<Pedido>> getPorIdObra(@PathVariable Integer idObra) {

		return ResponseEntity.ok(pedidoService.buscarPorIdObra(idObra));

	}

	@GetMapping
	@ApiOperation(value = "Obtiene los pedidos asociados al id y/o cuit de un cliente")
	public ResponseEntity<List<Pedido>> getPor_Cuit_o_Id(@RequestParam(required = false) String cuitCliente,
			@RequestParam(required = false) Integer idCliente) {
		RestTemplate restUsuario = new RestTemplate();
		String uri = API_REST_USUARIO + ENDPOINT_OBRA;

		if (cuitCliente == null && idCliente == null) {
			return ResponseEntity.badRequest().build();
		} else {
			if (cuitCliente != null && idCliente == null) {
				uri = uri + "?cuitCliente=" + cuitCliente;
			}
			if (idCliente != null && cuitCliente == null) {
				uri = uri + "?idCliente=" + idCliente;
			}
			if (idCliente != null && cuitCliente != null) {
				uri = uri + "?idCliente=" + idCliente + "&cuitCliente=" + cuitCliente;
			}
			ResponseEntity<Obra[]> respuesta = restUsuario.exchange(uri, HttpMethod.GET, null, Obra[].class);
			Obra[] obrasRespuesta = respuesta.getBody();

			// Una vez que obtengo las obras, debo buscar los pedidos asosiados a las obras
			List<Obra> obrasRespuestaLista = Arrays.asList(obrasRespuesta);
			System.out.print("obrasRespuestaLista:(" + obrasRespuestaLista.size() + ") \n");

			// Buscamos los pedidos asoaciados a las obras recibidas del API usuario

			List<Pedido> listaResultado = new ArrayList<>();
			

			for (Obra obra : obrasRespuestaLista) {
				List<Pedido> listaPedido = new ArrayList<>();
				listaPedido = pedidoService.buscarPorIdObra(obra.getId());
				if (listaPedido != null) {
					listaResultado.addAll(listaPedido);
				}
			}

			System.out
					.print("Cantidad de pedidos que coinciden con las obras del cliente:(" + listaResultado.size() + ") \n");

			return ResponseEntity.ok(listaResultado);

		}

	}

}
