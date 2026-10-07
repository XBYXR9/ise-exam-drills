package ise.rest.exam_ticket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Collections;

/**
 * Run this to start the ISE HN 2026 Ticket Manager on http://localhost:8083
 * (or: gradlew.bat runTicketServer).
 */
@SpringBootApplication
public class TicketServerApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(TicketServerApplication.class);
        application.setDefaultProperties(Collections.singletonMap("server.port", "8083"));
        application.run(args);
    }
}
