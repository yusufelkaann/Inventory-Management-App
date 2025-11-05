package com.yusufelkaan.jwt_auth.inventory.services;

import com.yusufelkaan.jwt_auth.inventory.dtos.CreateItemRequest;
import com.yusufelkaan.jwt_auth.inventory.dtos.ItemDto;
import com.yusufelkaan.jwt_auth.inventory.dtos.UpdateItemRequest;
import com.yusufelkaan.jwt_auth.shared.dtos.ApiResponse;

import java.util.List;


public interface InventoryService {
    ApiResponse<ItemDto> createItem(CreateItemRequest createItemRequest);

    ApiResponse<ItemDto> getItemById(Long id);

    ApiResponse<List<ItemDto>> getAllItems();

    ApiResponse<ItemDto> updateItem(Long id, UpdateItemRequest updateItemRequest);

    ApiResponse<Void> deleteItem(Long id);
}
