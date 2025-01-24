package com.example.wishservice.camel;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class FileMoveRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("file:src/main/resources/inbox?noop=false")
                .log("Processing file: ${header.CamelFileName}") // Protokollieren des Dateinamens
                .to("file:src/main/resources/done?fileName=${file:name.noext}.txt"); // Verschieben der Datei nach done und Beibehalten des Dateinamens
    }
}
