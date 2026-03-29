package com.profileapp.backend.controller;

import com.profileapp.backend.dto.request.AnalyseRequestDTO;
import com.profileapp.backend.dto.response.detail.AnalyseDetailDTO;
import com.profileapp.backend.dto.response.summary.AnalyseSummaryDTO;
import com.profileapp.backend.service.AnalyseService;
import com.profileapp.backend.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/analyses")
@RequiredArgsConstructor
public class AnalyseController {

    private final AnalyseService analyseService;

    @PostMapping
    public ResponseEntity<ApiResponse<AnalyseDetailDTO>> createAnalyse(
            @Valid @RequestBody AnalyseRequestDTO requestDTO) {
        AnalyseDetailDTO created = analyseService.createAnalyse(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Analyse créée avec succès", created));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AnalyseSummaryDTO>>> getAllAnalyses() {
        return ResponseEntity.ok(ApiResponse.success(
                "Liste des analyses récupérée avec succès",
                analyseService.getAllAnalyses()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AnalyseDetailDTO>> getAnalyseById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(
                "Analyse récupérée avec succès", analyseService.getAnalyseById(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAnalyse(@PathVariable Long id) {
        analyseService.deleteAnalyse(id);
        return ResponseEntity.ok(ApiResponse.success("Analyse supprimée avec succès"));
    }
}
