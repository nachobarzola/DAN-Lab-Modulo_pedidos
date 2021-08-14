package dan.ms.pedidos.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import dan.ms.persistence.repositories.PedidoRepositoryInMemory;

@Configuration
public class MiConfig {

	@Bean
	public PedidoRepositoryInMemory pedidoRepositoryInMenory() {

		return new PedidoRepositoryInMemory();

	}
	
	@Bean
	@LoadBalanced
	public RestTemplate restTemplate(){
		return new RestTemplate();
	}

}
