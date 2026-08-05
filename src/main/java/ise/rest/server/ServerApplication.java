package ise.rest.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Run this to start the product server on http://localhost:8080
 * (or: gradlew.bat runProductServer).
 *
 * @SpringBootApplication scans THIS package and below, which is why the resource,
 * service and repository all live under ise.rest.server.
 */
@SpringBootApplication
public class ServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }
}
