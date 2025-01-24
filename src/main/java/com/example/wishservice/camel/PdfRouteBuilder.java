package com.example.wishservice.camel;

import com.example.wishservice.model.Wish;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;

@Component
public class PdfRouteBuilder extends RouteBuilder {

    @Override
    public void configure() {
        // Überwachen des Eingangsverzeichnisses auf PDF-Dateien
        from("file:/src/main/resources/inbox?noop=true&include=.*\\.pdf")
                .routeId("pdfRoute")
                .process(exchange -> {
                    File file = exchange.getIn().getBody(File.class);
                    try (PDDocument document = PDDocument.load(file)) {
                        PDFTextStripper pdfStripper = new PDFTextStripper();
                        String text = pdfStripper.getText(document);
                        exchange.getIn().setBody(text);
                        System.out.println("Extracted text: " + text); // Ausgabe des extrahierten Texts
                    }
                })
                .to("direct:processText");

        // Weiterverarbeitung des extrahierten Texts
        from("direct:processText")
                .process(exchange -> {
                    String text = exchange.getIn().getBody(String.class);
                    Wish wish = extractWishFromText(text);
                    wish.setStatus("nur formuliert"); // Setzt den Status automatisch auf "nur formuliert"
                    exchange.getIn().setBody(wish);
                    System.out.println("Created Wish object: " + wish); // Ausgabe des erstellten Wish-Objekts
                })
                .to("direct:processWish");

        // Verarbeitung der Wünsche
        from("direct:processWish")
                .setHeader("Content-Type", constant("application/json"))
                .marshal().json() // Marshalling des Wish-Objekts in JSON
                .to("http://localhost:8080/wishes"); // Senden des Wunsches an das XmasWishes-System
    }

    // Methode zum Extrahieren eines Wish-Objekts aus dem Text
    private Wish extractWishFromText(String text) {
        Wish wish = new Wish();
        // Annahme: der Text enthält Zeilen im Format "Feld: Wert"
        String[] lines = text.split("\\r?\\n");
        for (String line : lines) {
            if (line.startsWith("Name:")) {
                wish.setName(line.substring(5).trim());
            } else if (line.startsWith("Description:")) {
                wish.setDescription(line.substring(12).trim());
            }
        }
        wish.setStatus("nur formuliert"); // Setzt den Status automatisch auf "nur formuliert"
        return wish;
    }
}
