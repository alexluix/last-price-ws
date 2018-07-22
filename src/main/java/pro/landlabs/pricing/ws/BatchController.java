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

    @PostMapping("/batches")
    public ResponseEntity<Long> createBatch() {
        long batchId = priceRegistryService.createBatch();

        return ResponseEntity.ok(batchId);
    }

    @PostMapping("/batches/{id}")
    public ResponseEntity<Object> postData(
            @PathVariable("id") long batchId, @RequestBody PriceDataChunk priceDataChunk) {

        logger.info("Received price data chunk: {}", priceDataChunk);

        for (Price<JsonNode> price : priceDataChunk.getPrices()) {
            priceRegistryService.addData(batchId, price);
        }

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/batches/{id}")
    public ResponseEntity<Object> cancelBatch(@PathVariable("id") long batchId) {
        priceRegistryService.cancelBatch(batchId);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/batches/{id}/complete")
    public ResponseEntity<Object> completeBatch(@PathVariable("id") long batchId) {
        priceRegistryService.completeBatch(batchId);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/instruments/{id}/price")
    public ResponseEntity<Object> getPrice(@PathVariable("id") long refId) {
        Price<JsonNode> price = priceRegistryService.getPrice(refId);

        if (price == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(price);
        }
    }

}
