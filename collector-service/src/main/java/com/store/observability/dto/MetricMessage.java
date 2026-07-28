package com.store.observability.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MetricMessage {
    private String service;
    private String action;
    private Long timestamp;
    private Double value;
}
