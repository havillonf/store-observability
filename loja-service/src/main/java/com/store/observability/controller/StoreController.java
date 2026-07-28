package com.store.observability.controller;

import com.store.observability.repository.UserInteractionRepository;
import com.store.observability.service.MetricProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class StoreController {

    private final MetricProducerService metricProducerService;
    private final UserInteractionRepository interactionRepository;

    @GetMapping("/")
    public String index(Model model) {
        long views = interactionRepository.countByAction("view_product");
        long carts = interactionRepository.countByAction("add_to_cart");
        long checkouts = interactionRepository.countByAction("checkout");
        double totalRevenue = interactionRepository.sumCheckoutValue();

        model.addAttribute("views", views);
        model.addAttribute("carts", carts);
        model.addAttribute("checkouts", checkouts);
        model.addAttribute("totalRevenue", totalRevenue);

        return "index";
    }

    @PostMapping("/api/metrics/action")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> triggerAction(@RequestBody Map<String, Object> payload) {
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

        long views = interactionRepository.countByAction("view_product");
        long carts = interactionRepository.countByAction("add_to_cart");
        long checkouts = interactionRepository.countByAction("checkout");
        double totalRevenue = interactionRepository.sumCheckoutValue();

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "action", action,
                "views", views,
                "carts", carts,
                "checkouts", checkouts,
                "totalRevenue", totalRevenue
        ));
    }
}
