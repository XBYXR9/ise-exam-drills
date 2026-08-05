package ise.rest.practice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Collections;

/**
 * Starts the practice server on http://localhost:8082
 * (or: gradlew.bat runPracticeServer).
 *
 * This package is deliberately outside the scan path of the other two applications,
 * so an unfinished practice resource can never break the real servers or their tests.
 */
@SpringBootApplication
public class PracticeServerApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(PracticeServerApplication.class);
        application.setDefaultProperties(Collections.singletonMap("server.port", "8082"));
        application.run(args);
    }
}
