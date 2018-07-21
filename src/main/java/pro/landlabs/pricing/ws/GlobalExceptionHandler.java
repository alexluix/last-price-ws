package pro.landlabs.pricing.ws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pro.landlabs.pricing.service.BatchNotFoundException;

import java.util.AbstractMap;

@ControllerAdvice
@Component
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private static final String BAD_REQUEST_MESSAGE = "Unable to process request";
    public static final String BATCH_NOT_FOUND_MESSAGE = "Batch not found";

    @ExceptionHandler
    public ResponseEntity<AbstractMap.SimpleEntry<String, String>> handle(BatchNotFoundException exception) {
        logger.error(BATCH_NOT_FOUND_MESSAGE, exception);

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new AbstractMap.SimpleEntry<>("message", BATCH_NOT_FOUND_MESSAGE));
    }

    @ExceptionHandler
    public ResponseEntity<AbstractMap.SimpleEntry<String, String>> handle(Exception exception) {
        logger.error(BAD_REQUEST_MESSAGE, exception);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new AbstractMap.SimpleEntry<>("message", BAD_REQUEST_MESSAGE));
    }

}
