package pro.landlabs.pricing.ws;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pro.landlabs.pricing.model.Price;
import pro.landlabs.pricing.model.PriceDataChunk;
import pro.landlabs.pricing.service.PriceRegistryService;

@RestController
@RequestMapping("/pricing")
public class BatchController {

    private static final Logger logger = LoggerFactory.getLogger(BatchController.class);

    private final PriceRegistryService priceRegistryService;

    public BatchController(PriceRegistryService priceRegistryService) {
        this.priceRegistryService = priceRegistryService;
    }

    @PostMapping
    @RequestMapping("/batch")
    public ResponseEntity<Long> createBatch() {
        long batchId = priceRegistryService.createBatch();

        return ResponseEntity.ok(batchId);
    }

    @PostMapping
    @RequestMapping("/batch/{id}")
    public ResponseEntity<Object> postData(
            @PathVariable("id") long batchId, @RequestBody PriceDataChunk priceDataChunk) {

        if (batchId == 0) throw new IllegalArgumentException();

        logger.info("Received price data chunk: {}", priceDataChunk);

        for (Price<JsonNode> price : priceDataChunk.getPrices()) {
            priceRegistryService.addData(batchId, price);
        }

        return ResponseEntity.ok().build();
    }

}
