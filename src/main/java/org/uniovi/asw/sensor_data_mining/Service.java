package org.uniovi.asw.sensor_data_mining;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;

/**
 * Hello world!
 *
 */
@EnableCircuitBreaker
@SpringBootApplication
public class Service {
	public static void main( String[] args ) {
		SpringApplication.run(Service.class, args);
	}
}
