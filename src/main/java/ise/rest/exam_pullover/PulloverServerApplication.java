package ise.rest.exam_pullover;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Collections;

/**
 * Run this to start the exam Pullover server on http://localhost:8081
 * (or: gradlew.bat runPulloverServer). Port 8081 so it can run alongside the
 * product server on 8080.
 */
@SpringBootApplication
public class PulloverServerApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(PulloverServerApplication.class);
        application.setDefaultProperties(Collections.singletonMap("server.port", "8081"));
        application.run(args);
    }
}
