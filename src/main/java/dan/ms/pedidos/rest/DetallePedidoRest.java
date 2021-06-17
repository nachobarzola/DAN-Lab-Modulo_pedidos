package dan.ms.pedidos.rest;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

import java.util.stream.IntStream;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dan.ms.pedidos.domain.DetallePedido;
import dan.ms.pedidos.domain.Pedido;
import dan.ms.pedidos.services.interfaces.PedidoService;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api/detallePedido")
public class DetallePedidoRest {

	@Autowired
	PedidoService pedidoService;

	@PostMapping("/pedido/{idPedido}")
	@ApiOperation(value = "Agrega  detalle pedido al pedido recibido como id")
	public ResponseEntity<Pedido> agregarItemPedido(@PathVariable Integer idPedido,
			@RequestBody DetallePedido detalle) {

		Optional<Pedido> ped = pedidoService.buscarPorId(idPedido);

		if (ped.isPresent()) {
			detalle.setId(null);
			ped.get().addDetalle(detalle);

			return ResponseEntity.ok(pedidoService.actualizarPedido(ped.get()).get());

		}
		return ResponseEntity.notFound().build();

	}

	@PutMapping(path = "/pedido/{idPedido}/detalle/{idDetalle}")
	@ApiOperation(value = "Permite actualizar el detalle de un pedido")
	public ResponseEntity<DetallePedido> actualizar(@PathVariable Integer idPedido, @PathVariable Integer idDetalle,
			@RequestBody DetallePedido detalle) {


		Optional<Pedido> ped = pedidoService.buscarPorId(idPedido);

		if (ped.isPresent()) {
			Pedido pedido = ped.get();
			List<DetallePedido> det = pedido.getDetalle();
			OptionalInt index = IntStream.range(0, det.size()).filter(i -> det.get(i).getId().equals(idDetalle))
					.findFirst();
			if (index.isPresent()) {
				det.get(index.getAsInt()).setCantidad(detalle.getCantidad());
				det.get(index.getAsInt()).setPrecio(detalle.getPrecio());
				det.get(index.getAsInt()).setProducto(detalle.getProducto());
				pedido.setDetalle(det);

				pedidoService.actualizarPedido(pedido);
				return ResponseEntity.ok(det.get(index.getAsInt()));

			}

		}

		return ResponseEntity.notFound().build();
	}

	@DeleteMapping(path = "/pedido/{idPedido}/detalle/{idDetalle}")
	@ApiOperation(value = "Borra el detalle de un pedido dado el id del pedido y el id del detalle")
	public ResponseEntity<List<DetallePedido>> borrar(@PathVariable Integer idPedido, @PathVariable Integer idDetalle) {

		Optional<Pedido> ped = pedidoService.buscarPorId(idPedido);
		if (ped.isPresent()) {
			Pedido pedido = ped.get();
			List<DetallePedido> det = pedido.getDetalle();
			OptionalInt index = IntStream.range(0, det.size())
					.filter(i -> det.get(i).getId().equals(idDetalle))
					.findFirst();
					
			if (index.isPresent()) {
				DetallePedido detalle = det.get(index.getAsInt());
				det.remove(index.getAsInt());
				pedido.setDetalle(det);

				pedidoService.actualizarPedido(pedido);
				if(pedidoService.borrarDetallePedido(detalle).isEmpty()) {
					return ResponseEntity.notFound().build();
				}
				
				return ResponseEntity.ok(det);

			}

		}
		return ResponseEntity.notFound().build();
	}
}
