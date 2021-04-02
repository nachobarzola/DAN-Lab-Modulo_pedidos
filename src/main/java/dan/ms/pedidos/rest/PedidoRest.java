package dan.ms.pedidos.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import dan.ms.pedidos.domain.DetallePedido;
import dan.ms.pedidos.domain.Pedido;
import dan.ms.pedidos.domain.Obra;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
@RequestMapping("/api/pedido")
@Api(value = "PedidoRest", description = "Permite gestionar los pedidos a la empresa")
public class PedidoRest {
	private List<Pedido> listaPedidos = new ArrayList<>();
	private  Integer ID_GEN = 1;
	
	private static final String SERVER = "//http:127.0.0.1";
	private static final String PUERTO = ":8081";
	private static final String REST_API_CLIENTE = "/api/cliente";
	private static final String REST_API_OBRA = "/api/obra";
   
	
	@PostMapping
	@ApiOperation(value = "Permite agregar un nuevo pedido")
	public ResponseEntity<Pedido> agregarPedido(@RequestBody Pedido pedido){
		
		    pedido.setId(ID_GEN++);
	        listaPedidos.add(pedido);
	        
	        return ResponseEntity.ok(pedido);
	}

	@PostMapping(path = "/{idPedido}/detalle")
	@ApiOperation(value = "Permite agregar un item a un pedido especifico")
	public ResponseEntity<Pedido> agregarItemAPedido(@RequestBody DetallePedido detalle, Integer idPedido){
		
		 
		 OptionalInt indexOpt =   IntStream.range(0, listaPedidos.size())
			        .filter(i -> listaPedidos.get(i).getId().equals(idPedido))
			        .findFirst();

			        if(indexOpt.isPresent()){
			        	Pedido pe = listaPedidos.get(indexOpt.getAsInt());
			        	pe.getDetalle().add(detalle);
			            listaPedidos.set(indexOpt.getAsInt(), pe);
			            return ResponseEntity.ok(pe);
			        } else {
			            return ResponseEntity.notFound().build();
			        }
	
	}
	
	@PutMapping(path = "/{idPedido}")
	@ApiOperation(value = "Permite modificar un pedido existente")
	public ResponseEntity<Pedido> modificarPedido(@RequestBody Pedido pedido, Integer idPedido){
		
		 
		 OptionalInt indexOpt =   IntStream.range(0, listaPedidos.size())
			        .filter(i -> listaPedidos.get(i).getId().equals(idPedido))
			        .findFirst();

			        if(indexOpt.isPresent()){
			        	listaPedidos.set(indexOpt.getAsInt(),pedido);
			            return ResponseEntity.ok(pedido);
			        } else {
			            return ResponseEntity.notFound().build();
			        }
	}
	
	@DeleteMapping(path = "/{idPedido}")
	@ApiOperation(value = "Permite eliminar un pedido existente")
    public ResponseEntity<Pedido> borrar(@PathVariable Integer idPedido){
        OptionalInt indexOpt =   IntStream.range(0, listaPedidos.size())
        .filter(i -> listaPedidos.get(i).getId().equals(idPedido))
        .findFirst();

        if(indexOpt.isPresent()){
            listaPedidos.remove(indexOpt.getAsInt());
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
	@DeleteMapping(path = "/{idPedido}/detalle/{id}")
	@ApiOperation(value = "Permite eliminar un item a un pedido Existente")
	 public ResponseEntity<Pedido> borrarItemPedido(@PathVariable Integer idPedido,Integer id){
        OptionalInt indexOpt =   IntStream.range(0, listaPedidos.size())
        .filter(i -> listaPedidos.get(i).getId().equals(idPedido))
        .findFirst();

        if(indexOpt.isPresent()){
        	Pedido pe = listaPedidos.get(indexOpt.getAsInt());
        	List<DetallePedido> listDetalle = pe.getDetalle();
        	OptionalInt indexOpt2 =   IntStream.range(0, listDetalle.size())
        	        .filter(i -> listDetalle.get(i).getId().equals(id))
        	        .findFirst();
        	
        	if(indexOpt2.isPresent()) {
        		listDetalle.remove(indexOpt2.getAsInt());
        		pe.setDetallePedidoList(listDetalle);
        		listaPedidos.set(indexOpt.getAsInt(),pe);
        		return ResponseEntity.ok(pe);
        	}else {
        		return ResponseEntity.notFound().build();
        	}
 
        } else {
            return ResponseEntity.notFound().build();
        }
    }
	
	@GetMapping(path = "/{idPedido}")
	@ApiOperation(value = "Permite obtener un pedido por id")
	public ResponseEntity<Pedido> obtenerPedido(Integer idPedido){
		 Optional<Pedido> pedido =  listaPedidos
	                .stream()
	                .filter(unPedido -> unPedido.getId().equals(idPedido))
	                .findFirst();
	        return ResponseEntity.of(pedido);
	}

	@GetMapping(path = "/obtener")
    @ApiOperation(value = "Busca pedidos por cuit y/o id cliente")
    public ResponseEntity<List<Pedido>> obtenerObraClienteTipo(@RequestParam(name = "cuit",required=false) String cuit,
    															@RequestParam(name = "idCliente", required=false)Integer idCliente ){
        
    	if(cuit==null && idCliente==null) {
    		return ResponseEntity.badRequest().build();
    	}else { 
    		RestTemplate obra = new RestTemplate();
    		String url="";
    	if(cuit!=null) {
    		       
    		   url = SERVER + PUERTO+ REST_API_OBRA + "/cuit/" + cuit;
    		      
    	} else {
    		
 
		       url = SERVER + PUERTO+ REST_API_OBRA + "?cliente=" + idCliente;
		      
    	 }
    	
    	
    	ResponseEntity<Obra[]> respuesta = obra.getForEntity(url,Obra[].class);
	       Obra[] obras = respuesta.getBody();  
	       
	      List<Integer> obrasTotalesCliente = Arrays.stream(obras) 
	       .map(Obra::getId)
	       .collect(Collectors.toList());
	   
	      List<Pedido> pe = Optional.ofNullable(listaPedidos)
					.orElse(Collections.emptyList())
	                .stream()
	                .filter(unPedido -> obrasTotalesCliente.contains(unPedido.getObra().getId()))
	                .collect(Collectors.toList());
	      
	 
		return ResponseEntity.ok(pe);
    	
    	
    	
    	}
    		       
    			 
    }
	
	
	

	@GetMapping
    @ApiOperation(value = "Busca pedidos por id de obra")
    public ResponseEntity<List<Pedido>> obtenerPedidoIdObra(@RequestParam(name = "idObra",required=true) Integer idObra){

    				List<Pedido> ob = Optional.ofNullable(listaPedidos)
    				.orElse(Collections.emptyList())
                    .stream()
                    .filter(unPedido -> unPedido.getObra().getId().equals(idObra))
                    .collect(Collectors.toList());
    		       
    			 return ResponseEntity.ok(ob);	
    }
	
	@GetMapping(path = "/{idPedido}/detalle/{id}")
    @ApiOperation(value = "Busca Detalle por id pedido y id detalle")
    public ResponseEntity<DetallePedido> obtenerPedidoDetalle(Integer idPedido,Integer id){

		
			Optional<Pedido> pedido =  listaPedidos
                .stream()
                .filter(unPedido -> unPedido.getId().equals(idPedido))
                .findFirst();
			if(pedido.isPresent()) {
				Optional<DetallePedido> detalle = pedido.get().getDetalle()
						.stream()
						.filter(unDetalle -> unDetalle.getId().equals(id))
						.findFirst();
				
				if(detalle.isPresent()) {
					return ResponseEntity.of(detalle);
				}
				
			}
			
			return ResponseEntity.notFound().build();

    		       
    			
    }
	
	
	
}
