package com.store.observability.controller;

import com.store.observability.service.MetricProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class StoreController {

    private final MetricProducerService metricProducerService;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/api/metrics/action")
    @ResponseBody
    public ResponseEntity<Map<String, String>> triggerAction(@RequestBody Map<String, Object> payload) {
        String action = (String) payload.getOrDefault("action", "view_product");
        Double value = 0.0;
        if (payload.containsKey("value")) {
            try {
                value = Double.valueOf(payload.get("value").toString());
            } catch (Exception e) {
                value = 0.0;
            }
        }

        metricProducerService.sendMetric(action, value);
        return ResponseEntity.ok(Map.of("status", "success", "action", action));
    }
}
