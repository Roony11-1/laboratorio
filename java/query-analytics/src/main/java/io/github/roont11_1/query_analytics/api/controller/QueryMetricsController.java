package io.github.roont11_1.query_analytics.api.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.roont11_1.query_analytics.core.service.QueryMetricsService;


@RestController
@RequestMapping("/api/v1/metrics")
public class QueryMetricsController
{
    private final QueryMetricsService metricsService;

    public QueryMetricsController(QueryMetricsService metricsService)
    {
        this.metricsService = metricsService;
    }

    @GetMapping("/categories")
    public ResponseEntity<Map<String, Long>> getCategoryCounts()
    {
        return ResponseEntity.ok(metricsService.getCategoryCounts());
    }
}