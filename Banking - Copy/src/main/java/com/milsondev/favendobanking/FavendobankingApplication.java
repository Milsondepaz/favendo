package com.milsondev.favendobanking;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class FavendobankingApplication {

    public static void main(String[] args) {
        SpringApplication.run(FavendobankingApplication.class, args);
    }

    @Bean
    public OpenAPI openApiConfig() {
        return new OpenAPI().info(apiInfo());
    }

    public Info apiInfo() {
        Info info = new Info();
        info
                .title("favendo - Banking service API")
                .description("Assignment for the position as a Backend developer at Favendo")
                .version("v1.0.0");
        return info;
    }
    
    
   

}
