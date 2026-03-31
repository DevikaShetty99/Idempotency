package com.example.inventory.service;

import com.example.inventory.entity.Inventory;

public interface InventoryService {
    Inventory reserve(Inventory inventory);
}