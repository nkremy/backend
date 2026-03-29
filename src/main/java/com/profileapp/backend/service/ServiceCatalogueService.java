package com.profileapp.backend.service;

import com.profileapp.backend.dto.request.ServiceRequestDTO;
import com.profileapp.backend.dto.response.detail.ServiceDetailDTO;
import com.profileapp.backend.dto.response.summary.ServiceSummaryDTO;
import java.util.List;

public interface ServiceCatalogueService {
    ServiceDetailDTO createService(ServiceRequestDTO requestDTO);
    List<ServiceSummaryDTO> getAllServices();
    ServiceDetailDTO getServiceById(Long id);
    ServiceDetailDTO updateService(Long id, ServiceRequestDTO requestDTO);
    void deleteService(Long id);
}
