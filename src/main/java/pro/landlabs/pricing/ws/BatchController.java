package pro.landlabs.pricing.ws;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pricing")
public class BatchController {

    @PostMapping
    @RequestMapping("/batch")
    public ResponseEntity<Integer> createBatch() {
        return ResponseEntity.ok(0);
    }

    @PostMapping
    @RequestMapping("/batch/{id}")
    public ResponseEntity<Object> postData(@PathVariable("id") long batchId) {
        return ResponseEntity.ok().build();
    }

}
