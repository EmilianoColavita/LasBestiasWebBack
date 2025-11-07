package com.backend.LasBestias;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LasBestiasApplication {

	public static void main(String[] args) {
		// ✅ Carga las variables del archivo .env y las inyecta en el entorno del sistema
		Dotenv dotenv = Dotenv.configure()
				.directory("./") // busca el .env en la raíz del proyecto
				.ignoreIfMissing() // no lanza error si falta (útil para producción)
				.load();

		dotenv.entries().forEach(entry ->
				System.setProperty(entry.getKey(), entry.getValue())
		);

		SpringApplication.run(LasBestiasApplication.class, args);
	}
}
