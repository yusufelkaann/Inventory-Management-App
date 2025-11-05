package com.yusufelkaan.jwt_auth.inventory.services;

import com.yusufelkaan.jwt_auth.shared.exceptions.ResourceNotFoundException;
import com.yusufelkaan.jwt_auth.inventory.dtos.CreateItemRequest;
import com.yusufelkaan.jwt_auth.inventory.dtos.ItemDto;
import com.yusufelkaan.jwt_auth.inventory.dtos.UpdateItemRequest;
import com.yusufelkaan.jwt_auth.inventory.entities.Item;
import com.yusufelkaan.jwt_auth.inventory.repository.ItemRepository;
import com.yusufelkaan.jwt_auth.shared.dtos.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InventoryServiceImplementation implements InventoryService {
    private final ItemRepository itemRepository;

    @Autowired
    public InventoryServiceImplementation(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    public ApiResponse<ItemDto> createItem(CreateItemRequest createItemRequest) {
        // DTO to entity mapping
        Item newItem = Item.builder()
                .name(createItemRequest.name())
                .quantity(createItemRequest.quantity())
                .price(createItemRequest.price())
                .location(createItemRequest.location())
                .build();

        // Save item
        Item savedItem = itemRepository.save(newItem);

        // Entity to DTO mapping for response
        ItemDto itemDto = ItemDto.builder()
                .id(savedItem.getId())
                .name(savedItem.getName())
                .quantity(savedItem.getQuantity())
                .price(savedItem.getPrice())
                .location(savedItem.getLocation())
                .build();

        // Return response
        return ApiResponse.<ItemDto>builder()
                .success(true)
                .message("Item created successfully")
                .data(itemDto)
                .build();

    }

    @Override
    public ApiResponse<ItemDto> getItemById(Long id) {
        // Fetch Item using the repository
        Optional<Item> optionalItem = itemRepository.findById(id);

        // Handle resource not found
        Item item = optionalItem.orElseThrow(() ->
                new ResourceNotFoundException("Item not found with id: " + id)
        );

        // Entity to DTO
        ItemDto itemDto = ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .quantity(item.getQuantity())
                .price(item.getPrice())
                .location(item.getLocation())
                .build();

        // Return response
        return ApiResponse.<ItemDto>builder()
                .success(true)
                .message("Item retrieved successfully")
                .data(itemDto)
                .build();

    }

    @Override
    public ApiResponse<List<ItemDto>> getAllItems() {
        // Fetch all items entities
        List<Item> items = itemRepository.findAll();

        // Entity to DTO mapping
        List<ItemDto> itemDtos = items.stream()
                .map(this::mapItemToDto)
                .toList();

        // Return response
        return ApiResponse.<List<ItemDto>>builder()
                .success(true)
                .message("Items retrieved successfully")
                .data(itemDtos)
                .build();
    }

    // Helper method for mapping Item entity to ItemDto
    private ItemDto mapItemToDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .quantity(item.getQuantity())
                .price(item.getPrice())
                .location(item.getLocation())
                .build();
    }

    @Override
    public ApiResponse<ItemDto> updateItem(Long id, UpdateItemRequest request) {
        // Fetch the item
        Item existingItem = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + id));

        // Update fields
        if (request.name() != null) existingItem.setName(request.name());
        if (request.quantity() != null) existingItem.setQuantity(request.quantity());
        if (request.price() != null) existingItem.setPrice(request.price());
        if (request.location() != null) existingItem.setLocation(request.location());

        // Persistence
        Item updatedItem = itemRepository.save(existingItem);

        // Entity to DTO mapping
        ItemDto itemDto = mapItemToDto(updatedItem);

        // Return response
        return ApiResponse.<ItemDto>builder()
                .success(true)
                .message("Item updated successfully")
                .data(itemDto)
                .build();

    }

    @Override
    public ApiResponse<Void> deleteItem(Long id) {

        // Check if the item exists
        if (!itemRepository.existsById(id)) {
            throw new ResourceNotFoundException("Item not found with id: " + id);
        }

        // Delete the item
        itemRepository.deleteById(id);

        // Return response
        return ApiResponse.<Void>builder()
                .success(true)
                .message("Item deleted successfully")
                .data(null)
                .build();
    }
}
