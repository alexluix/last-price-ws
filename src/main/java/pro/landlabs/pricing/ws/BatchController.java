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
        return ResponseEntity.ok(1);
    }

    @PostMapping
    @RequestMapping("/batch/{id}")
    public ResponseEntity<Object> postData(@PathVariable("id") long batchId) {

        if (batchId == 0) throw new IllegalArgumentException();

        return ResponseEntity.ok().build();
    }

}
