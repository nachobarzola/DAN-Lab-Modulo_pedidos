package dan.ms.pedidos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import dan.ms.persistence.repositories.PedidoRepository;

@Configuration
public class MiConfig {

	@Bean
	public PedidoRepository pedidoRepository() {

		return new PedidoRepository();

	}

}
