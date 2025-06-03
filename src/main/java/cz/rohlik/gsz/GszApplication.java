package cz.rohlik.gsz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class GszApplication {

	public static void main(String[] args) {
		SpringApplication.run(GszApplication.class, args);
	}

}
