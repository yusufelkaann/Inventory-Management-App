package com.yusufelkaan.jwt_auth.inventory.repository;

import com.yusufelkaan.jwt_auth.inventory.entities.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item,Long> {
    Optional<Item> findById(Long id);

}
