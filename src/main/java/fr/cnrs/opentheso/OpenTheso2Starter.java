package fr.cnrs.opentheso;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication
@EnableJpaRepositories
@ServletComponentScan
public class OpenTheso2Starter {

    public static void main(String[] args) {
        SpringApplication.run(OpenTheso2Starter.class, args);
    }

}
