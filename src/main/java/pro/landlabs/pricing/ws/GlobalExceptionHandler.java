package pro.landlabs.pricing.ws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.AbstractMap;

@ControllerAdvice
@Component
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private static final String BAD_REQUEST_MESSAGE = "Unable to process request";

    @ExceptionHandler
    public ResponseEntity<AbstractMap.SimpleEntry<String, String>> handle(Exception exception) {
        logger.error(BAD_REQUEST_MESSAGE, exception);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new AbstractMap.SimpleEntry<>("message", BAD_REQUEST_MESSAGE));
    }

}
