package dan.ms.pedidos.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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
import dan.ms.pedidos.domain.Obra;
import dan.ms.pedidos.domain.Pedido;
import dan.ms.pedidos.excepciones.ExceptionRechazoPedido;
import dan.ms.pedidos.services.interfaces.PedidoService;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api/pedido")
public class PedidoRest {
	private static String API_REST_USUARIO = "http://localhost:8081/api";
	private static String ENDPOINT_OBRA = "/obra";

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

		if (pedido.getObra() != null && ldp != null && ldp.size() > 0) {
			List<DetallePedido> detallePedido = ldp.stream().filter(unDetalle -> unDetalle.getCantidad() != null)
					.filter(unDetalle -> unDetalle.getProducto() != null).collect(Collectors.toList());
			if (detallePedido.size() == ldp.size()) {

				try {
					if (pedidoService.guardarPedido(pedido) != null) {
						return ResponseEntity.ok(pedido);
					}
				} catch (ExceptionRechazoPedido e) {
					return ResponseEntity.badRequest().build();

				}

			}

		}

		return ResponseEntity.badRequest().build();

	}

	@PutMapping(path = "/{idPedido}")
	@ApiOperation(value = "Actualiza pedido dado un id")
	public ResponseEntity<Pedido> actualizar(@RequestBody Pedido pedido, @PathVariable Integer idPedido) {

		//TODO: Al actualizar el pedido, se reemplaza todo, tambien si tenia varios detalles y aca pongo uno
		//entonces, los demas quedan en null. Esto esta bien??
		Optional<Pedido> ped = pedidoService.buscarPorId(idPedido);
		if (ped.isPresent()) {
			Pedido p = ped.get();
			p.setDetalle(pedido.getDetalle());
			p.setEstado(pedido.getEstado());
			p.setFechaPedido(pedido.getFechaPedido());
			p.setObra(pedido.getObra());
			try {
				return ResponseEntity.ok(pedidoService.actualizarPedido(p));
			} catch (ExceptionRechazoPedido e) {
				return ResponseEntity.badRequest().build();
			}
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

	@GetMapping(path = "/{idPedido}")
	@ApiOperation(value = "Obtener pedido por su ID")
	public ResponseEntity<Pedido> getPorId(@PathVariable Integer idPedido) {
		Optional<Pedido> pedido = pedidoService.buscarPorId(idPedido);
		return ResponseEntity.of(pedido);
	}

	@GetMapping(path = "/obra/{idObra}")
	@ApiOperation(value = "Obtener pedido dado el id de la obra")
	public ResponseEntity<Pedido> getPorIdObra(@PathVariable Integer idObra) {

		return ResponseEntity.of(pedidoService.buscarPorIdObra(idObra));

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
			
			List<Pedido> resultado = new ArrayList<Pedido>();
			Optional<Pedido> pedido = Optional.empty();
			
				for(Obra obra: obrasRespuestaLista) {
					pedido = pedidoService.buscarPorIdObra(obra.getId());
				 	if(pedido.isPresent()) {
				 	 resultado.add(pedido.get());	
				 	}
				 	pedido = Optional.empty();
				}
			
			System.out
					.print("Cantidad de pedidos que coinciden con las obras del cliente:(" + resultado.size() + ") \n");

			return ResponseEntity.ok(resultado);

		}

	}

}
