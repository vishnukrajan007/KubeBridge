package com.kubebridge.controller;

import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/items")
public class ItemController {

    private final DynamoDbClient dynamoDbClient;
    private final String tableName = "ItemsTable"; // must exist in DynamoDB

    public ItemController() {
        this.dynamoDbClient = DynamoDbClient.builder()
                .region(Region.AP_SOUTH_1) // your region
                .build();
    }

    @PostMapping
    public String putItem(@RequestParam String id, @RequestParam String data) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("id", AttributeValue.builder().s(id).build());
        item.put("data", AttributeValue.builder().s(data).build());

        PutItemRequest request = PutItemRequest.builder()
                .tableName(tableName)
                .item(item)
                .build();

        dynamoDbClient.putItem(request);
        return "Item stored with ID: " + id;
    }

    @GetMapping("/{id}")
    public Map<String, AttributeValue> getItem(@PathVariable String id) {
        Map<String, AttributeValue> key = new HashMap<>();
        key.put("id", AttributeValue.builder().s(id).build());

        GetItemRequest request = GetItemRequest.builder()
                .tableName(tableName)
                .key(key)
                .build();

        return dynamoDbClient.getItem(request).item();
    }
}
