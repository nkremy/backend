package com.profileapp.backend.controller;

import com.profileapp.backend.dto.request.ServiceRequestDTO;
import com.profileapp.backend.dto.response.detail.ServiceDetailDTO;
import com.profileapp.backend.dto.response.summary.ServiceSummaryDTO;
import com.profileapp.backend.service.ServiceCatalogueService;
import com.profileapp.backend.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/services")
@RequiredArgsConstructor
public class ServiceController {

    private final ServiceCatalogueService serviceCatalogueService;

    @PostMapping
    public ResponseEntity<ApiResponse<ServiceDetailDTO>> createService(
            @Valid @RequestBody ServiceRequestDTO requestDTO) {
        ServiceDetailDTO created = serviceCatalogueService.createService(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Service créé avec succès", created));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ServiceSummaryDTO>>> getAllServices() {
        return ResponseEntity.ok(ApiResponse.success(
                "Liste des services récupérée avec succès",
                serviceCatalogueService.getAllServices()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ServiceDetailDTO>> getServiceById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(
                "Service récupéré avec succès", serviceCatalogueService.getServiceById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ServiceDetailDTO>> updateService(
            @PathVariable Long id,
            @Valid @RequestBody ServiceRequestDTO requestDTO) {
        return ResponseEntity.ok(ApiResponse.success(
                "Service mis à jour avec succès",
                serviceCatalogueService.updateService(id, requestDTO)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteService(@PathVariable Long id) {
        serviceCatalogueService.deleteService(id);
        return ResponseEntity.ok(ApiResponse.success("Service supprimé avec succès"));
    }
}
