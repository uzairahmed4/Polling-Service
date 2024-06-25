// Main application class, generated by Spring Initializr.
// Must live in the top package so that @SpringBootApplication annotation
// can find all the components.

package polling;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import polling.model.*;
import polling.service.*;

@SpringBootApplication
public class PollingApplication {

    public static void main(String[] args) {
        SpringApplication.run(PollingApplication.class, args);
    }

    // The initDB() @Bean is run automatically on startup, before the
    // @RestController is started.
    // The body could access command line argument args (but doesn't).
    // The fact that initDB() requires a WelcomeService argument tells Spring
    // to auto-configure and pass a WelcomeService instance, which initDB()
    // amends by adding more Welcome instances.
    @Bean
    public CommandLineRunner initDB(PollingService ps) {
        return (args) -> {
        	ps.addModel(new Model(1, "ChatGPT", "I like machines"));
        	ps.addModel(new Model(2, "Gemini", "I like machines"));
        	ps.addModel(new Model(3, "Copilot", "I like fruits"));
        };
    }
}