package com.profileapp.backend.service;

import com.profileapp.backend.dto.request.AnalyseRequestDTO;
import com.profileapp.backend.dto.response.detail.AnalyseDetailDTO;
import com.profileapp.backend.dto.response.summary.AnalyseSummaryDTO;
import java.util.List;

public interface AnalyseService {
    AnalyseDetailDTO createAnalyse(AnalyseRequestDTO requestDTO);
    List<AnalyseSummaryDTO> getAllAnalyses();
    AnalyseDetailDTO getAnalyseById(Long id);
    void deleteAnalyse(Long id);
}
