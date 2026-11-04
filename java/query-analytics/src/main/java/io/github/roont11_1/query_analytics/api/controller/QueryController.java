package io.github.roont11_1.query_analytics.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.roont11_1.query_analytics.api.dto.QueryRequest;
import io.github.roont11_1.query_analytics.core.service.QueryService;

@RestController
@RequestMapping("api/v1/query")
public class QueryController 
{
    private final QueryService queryService;


    public QueryController(QueryService queryService)
    {
        this.queryService = queryService;
    }

    @PostMapping
    public ResponseEntity<Void> query(@RequestBody QueryRequest request)
    {
        queryService.query(request);

        return ResponseEntity.accepted().build();
    }
}
