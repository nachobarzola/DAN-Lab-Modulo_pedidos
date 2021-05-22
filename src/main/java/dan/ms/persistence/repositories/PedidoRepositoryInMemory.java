package dan.ms.persistence.repositories;

import org.springframework.stereotype.Repository;

import dan.ms.pedidos.domain.Pedido;
import frsf.isi.dan.InMemoryRepository;

@Repository
public class PedidoRepositoryInMemory extends InMemoryRepository<Pedido> {


	@Override
	public Integer getId(Pedido entity) {
		return entity.getId();
		}

	@Override
	public void setId(Pedido entity, Integer id) {
		entity.setId(id);
		
	}
	

}
