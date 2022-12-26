package dgu.edu.dnaapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class DnaApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(DnaApiApplication.class, args);
	}

}
