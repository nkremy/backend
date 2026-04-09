package com.profileapp.backend.repository;

import com.profileapp.backend.entity.SousServiceImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface SousServiceImageRepository extends JpaRepository<SousServiceImage, Long> {

    List<SousServiceImage> findBySousServiceIdOrderByOrdreAsc(Long sousServiceId);

    @Modifying
    @Transactional
    void deleteBySousServiceId(Long sousServiceId);
}
