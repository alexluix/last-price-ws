package pro.landlabs.pricing.ws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pro.landlabs.pricing.model.PriceDataChunk;

@RestController
@RequestMapping("/pricing")
public class BatchController {

    private static final Logger logger = LoggerFactory.getLogger(BatchController.class);

    @PostMapping
    @RequestMapping("/batch")
    public ResponseEntity<Integer> createBatch() {
        return ResponseEntity.ok(1);
    }

    @PostMapping
    @RequestMapping("/batch/{id}")
    public ResponseEntity<Object> postData(
            @PathVariable("id") long batchId, @RequestBody PriceDataChunk priceDataChunk) {

        if (batchId == 0) throw new IllegalArgumentException();

        logger.info("Received price data chunk: {}", priceDataChunk);

        return ResponseEntity.ok().build();
    }

}
