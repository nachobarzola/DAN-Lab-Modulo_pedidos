package dan.ms.pedidos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import dan.ms.persistence.repositories.PedidoRepositoryInMemory;

@Configuration
public class MiConfig {

	@Bean
	public PedidoRepositoryInMemory pedidoRepositoryInMenory() {

		return new PedidoRepositoryInMemory();

	}

}
