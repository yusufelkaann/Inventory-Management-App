package com.yusufelkaan.jwt_auth.inventory.controllers;

import com.yusufelkaan.jwt_auth.inventory.dtos.CreateItemRequest;
import com.yusufelkaan.jwt_auth.inventory.dtos.ItemDto;
import com.yusufelkaan.jwt_auth.inventory.dtos.UpdateItemRequest;
import com.yusufelkaan.jwt_auth.inventory.services.InventoryService;
import com.yusufelkaan.jwt_auth.shared.dtos.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    @Autowired
    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ItemDto>> createItem(@Valid @RequestBody CreateItemRequest request) {
        ApiResponse<ItemDto> response = inventoryService.createItem(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ItemDto>>> getAllItems() {

        ApiResponse<List<ItemDto>> response = inventoryService.getAllItems();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("")
    public ResponseEntity<ApiResponse<ItemDto>> getItemById(@PathVariable Long id) {

        ApiResponse<ItemDto> response = inventoryService.getItemById(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()") // <-- CHECK: User must be logged in
    public ResponseEntity<ApiResponse<ItemDto>> updateItem(
            @PathVariable Long id,
            @Valid @RequestBody UpdateItemRequest request
    ) {
        ApiResponse<ItemDto> response = inventoryService.updateItem(id, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> deleteItem(@PathVariable Long id) {

        ApiResponse<Void> response = inventoryService.deleteItem(id);
        // Returning 200 OK to show the ApiResponse body, though 204 No Content is REST standard for delete
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
