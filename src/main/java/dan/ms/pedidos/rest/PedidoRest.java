package dan.ms.pedidos.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.IntStream;

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

import dan.ms.pedidos.domain.DetallePedido;
import dan.ms.pedidos.domain.Pedido;
import io.swagger.annotations.ApiOperation;



@RestController
@RequestMapping("/api/pedido")
public class PedidoRest {
	private static final List<Pedido> listaPedido=new ArrayList<>();
	private static Integer ID_GEN = 1;
	
	@PostMapping
	@ApiOperation(value= "Crea un pedido")
	public ResponseEntity<Pedido> crear(@RequestBody Pedido pedido){
		pedido.setId(ID_GEN++);
		System.out.print("Se creo un nuevo pedido: "+ pedido.toString()+"\n");
		listaPedido.add(pedido);
		return ResponseEntity.ok(pedido);
	}
	
	@PostMapping("/{idPedido}/detalle")
	@ApiOperation(value= "Agrega detalle pedido al pedido recibido como id")
	public ResponseEntity<Pedido> agregarItemPedido(@PathVariable Integer idPedido, @RequestBody DetallePedido detalle){
		detalle.setId(ID_GEN++);
		OptionalInt index = IntStream.range(0, listaPedido.size())
				.filter(i -> listaPedido.get(i).getId().equals(idPedido))
				.findFirst();
		listaPedido.get(index.getAsInt()).setDetalle(detalle);
		return ResponseEntity.ok(listaPedido.get(index.getAsInt()));
		
	}
	
	@PutMapping(path= "/{idPedido}")
	@ApiOperation(value="Actualiza pedido dado un id")
	public ResponseEntity<Pedido> actualizar(@RequestBody Pedido pedido, @PathVariable Integer idPedido){
		OptionalInt index = IntStream.range(0, listaPedido.size())
				.filter(i -> listaPedido.get(i).getId().equals(idPedido))
				.findFirst();
		if(index.isPresent()) {
			listaPedido.set(index.getAsInt(), pedido);
			return ResponseEntity.ok(pedido);
		}
		else {
			return ResponseEntity.notFound().build();
		}
	}
	
	@DeleteMapping(path= "/{idPedido}")
	@ApiOperation(value= "Borra un pedido dado un id")
	public ResponseEntity<Pedido> borrar(@PathVariable Integer idPedido){
		OptionalInt index = IntStream.range(0, listaPedido.size())
				.filter(i -> listaPedido.get(i).getId().equals(idPedido))
				.findFirst();
		if(index.isPresent()) {
			listaPedido.remove(index.getAsInt());
			return ResponseEntity.ok().build();
		}
		else {
			return ResponseEntity.notFound().build();
		}
	}
	//TODO: REFACTORIZAR CODIGO: ver si se puede hacer mas simple!
	@DeleteMapping(path= "/{idPedido}/detalle/{idDetalle}")
	@ApiOperation(value= "Borra el detalle de un pedido dado el id del pedido y el id del detalle")
	public ResponseEntity<DetallePedido> borrar(@PathVariable Integer idPedido,@PathVariable Integer idDetalle){

		 OptionalInt indexPedido = IntStream.range(0, listaPedido.size()).
				filter(i -> listaPedido.get(i).getId().equals(idPedido))
				.findFirst();
		//Verificamos si existe ese pedido con esa id.
		if(indexPedido.isPresent()) {
			Pedido ped = listaPedido.get(indexPedido.getAsInt());
			OptionalInt indexDetalle = IntStream.range(0, ped.getDetalle().size())
					.filter(j -> ped.getDetalle().get(j).getId().equals(idDetalle))
					.findFirst();
			
			if(indexDetalle.isPresent()) {
				DetallePedido detallePedido = listaPedido.get(indexPedido.getAsInt()).getDetalle().get(indexDetalle.getAsInt());
				listaPedido.get(indexPedido.getAsInt()).getDetalle().remove(indexDetalle.getAsInt());
				return ResponseEntity.ok(detallePedido);
			}
			else {
				return ResponseEntity.notFound().build();
			}
		
		}
		else {
			return ResponseEntity.notFound().build();
		}
		
	}
	
	@GetMapping(path= "/{idPedido}")
	@ApiOperation(value= "Obtener pedido por su ID")
	public ResponseEntity<Pedido> getPorId(@PathVariable Integer idPedido){
		Optional<Pedido> pedido = listaPedido.stream()
				.filter(unPedido -> unPedido.getId().equals(idPedido))
				.findFirst();
		return ResponseEntity.of(pedido);
	}
	
	@GetMapping(path= "/obra/{idObra}")
	@ApiOperation(value= "Obtener pedido dado el id de la obra")
	public ResponseEntity<Pedido> getPorIdObra(@PathVariable Integer idObra){
		Optional<Pedido> pedido = listaPedido.stream()
				.filter(unPedido -> unPedido.getObra().getId().equals(idObra))
				.findFirst();
		return ResponseEntity.of(pedido);
	}
	
	

	
	
	
}
