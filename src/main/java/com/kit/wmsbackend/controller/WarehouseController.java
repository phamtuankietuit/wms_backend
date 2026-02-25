package com.kit.wmsbackend.controller;

import com.kit.wmsbackend.entity.Warehouse;
import com.kit.wmsbackend.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/warehouses")
@RequiredArgsConstructor
public class WarehouseController {
	private final WarehouseService warehouseService;

	@GetMapping
	public ResponseEntity<List<Warehouse>> findAll() {
		return ResponseEntity.ok(warehouseService.findAll());
	}

	@GetMapping("/{id}")
	public ResponseEntity<Warehouse> findById(@PathVariable String id) {
		return ResponseEntity.ok(warehouseService.findById(id));
	}

	@PostMapping
	public ResponseEntity<Warehouse> create(@RequestBody Warehouse warehouse) {
		return ResponseEntity.status(HttpStatus.CREATED).body(warehouseService.create(warehouse));
	}

	@PutMapping("/{id}")
	public ResponseEntity<Warehouse> update(@PathVariable String id, @RequestBody Warehouse warehouse) {
		return ResponseEntity.ok(warehouseService.update(id, warehouse));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable String id) {
		warehouseService.delete(id);
		return ResponseEntity.noContent().build();
	}
}
