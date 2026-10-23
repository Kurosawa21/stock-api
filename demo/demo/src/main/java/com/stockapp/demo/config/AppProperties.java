package com.stockapp.demo.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@ConfigurationProperties(prefix = "app")
@Validated
public record AppProperties(
        @NotBlank String name
        //@NotBlank String version,
        //@Valid Stock stock,
        //@Valid Contact contact
) {

/*public record Stock(
        @Min(0) @DefaultValue("5") int seuilAlerte,
        @NotBlank @DefaultValue("CAD") String devise
) {}

public record Contact(
        @NotBlank @Email String email,
        List<String> langues
) {}*/
}