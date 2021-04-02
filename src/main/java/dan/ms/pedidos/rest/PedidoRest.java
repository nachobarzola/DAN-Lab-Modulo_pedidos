package dan.ms.pedidos.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
import io.swagger.annotations.ApiOperation;



@RestController
@RequestMapping("/api/pedido")
public class PedidoRest {
	private static final List<Pedido> listaPedido=new ArrayList<>();
	private static Integer ID_GEN = 1;
	private static String API_REST_USUARIO = "http://localhost:50000/api";
	private static String ENDPOINT_OBRA = "/obra";
	
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

		 OptionalInt indexPedido = getIndexIdPedido(idPedido);
		//Verificamos si existe ese pedido con esa id.
		if(indexPedido.isPresent()) {
			
			Pedido ped = listaPedido.get(indexPedido.getAsInt());
			OptionalInt indexDetalle = getIndexIdDetallePedido(ped,idDetalle);
			
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
	
	@GetMapping
	@ApiOperation(value="Obtiene los pedidos asociados al id y/o cuit de un cliente")
	public ResponseEntity<List<Pedido>> getPor_Cuit_o_Id(@RequestParam(required=false) Integer cuit, @RequestParam(required=false) Integer idCliente){
		RestTemplate restUsuario = new RestTemplate();
		String uri="";
		
		if(cuit==null && idCliente==null) {
			return ResponseEntity.badRequest().build();
		}
		else if(cuit!=null && idCliente ==null) {
			//TODO: NO HAY METODO EN EL API DE USUARIO para obtener obra por cuit.
			//uri = API_REST_USUARIO + ENDPOINT_OBRA + "?cuit="+;
		}
		else if(cuit==null && idCliente != null) {
			uri = API_REST_USUARIO + ENDPOINT_OBRA + "?id_cliente="+idCliente;
		}
		//(cuit!=null && idCliente != null)
		else {
			
		}
		ResponseEntity<Obra[]> respuesta = restUsuario.exchange(uri, HttpMethod.GET,null,Obra[].class );
		Obra[] obrasRespuesta = respuesta.getBody();
		//Una vez que obtengo las obras, debo buscar los pedidos asosiados a las obras
		List<Obra> obrasRespuestaLista = Arrays.asList(obrasRespuesta);
		
		//Buscamos los pedidos asoaciados a las obras recibidas del API usuario
		List<Pedido> resultado = listaPedido.stream()
				.filter(unPed -> obrasRespuestaLista.contains(unPed.getObra()))
				.collect(Collectors.toList());
		
		System.out.print("Cantidad de pedidos que coinciden con las obras del cliente:("+resultado.size() +") \n");
		
		
		return ResponseEntity.ok(resultado);
		
		
		
		
		
	}
	
	
	
	@GetMapping(path= "/{idPedido}/detalle/{idDetalle}")
	@ApiOperation(value= "Obtener pedido dado una id y un detalleId")
	public ResponseEntity<DetallePedido> getPorIdDetalle(@PathVariable Integer idPedido, @PathVariable Integer idDetalle){
		 OptionalInt indexPedido = getIndexIdPedido(idPedido);
			//Verificamos si existe ese pedido con esa id.
			if(indexPedido.isPresent()) {
				
				Pedido ped = listaPedido.get(indexPedido.getAsInt());
				OptionalInt indexDetalle = getIndexIdDetallePedido(ped,idDetalle);
				
				//Verificamos si existe el detalle con idDetalle.
				if(indexDetalle.isPresent()) {
					DetallePedido detallePedido = listaPedido.get(indexPedido.getAsInt()).getDetalle().get(indexDetalle.getAsInt());
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
	
	//METODOS AUXILIARES
	private OptionalInt getIndexIdPedido(Integer idPedido) {
		return IntStream.range(0, listaPedido.size()).
				filter(i -> listaPedido.get(i).getId().equals(idPedido))
				.findFirst();
	}
	private OptionalInt getIndexIdDetallePedido(Pedido pedido,Integer idDetalle) {
		return IntStream.range(0, pedido.getDetalle().size())
				.filter(j -> pedido.getDetalle().get(j).getId().equals(idDetalle))
				.findFirst();
	}
	

	
	
	
}
