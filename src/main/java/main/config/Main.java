package main.config;

import main.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ComponentScan(basePackages = {"main.config", "main.controller", "main.model", "main.repository", "main.service", "main.utils"})
@EnableJpaRepositories("main.repository")
@EnableScheduling
@EnableJpaAuditing
@EntityScan("main.model")
public class Main implements CommandLineRunner {

    @Autowired
    private DocumentService documentService;

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        //documentService.exportTranslationToExcel();
        documentService.importTranslationsFromExcel("translations.xlsx");
    }
}