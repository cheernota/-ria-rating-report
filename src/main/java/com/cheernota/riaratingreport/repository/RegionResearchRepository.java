package com.cheernota.riaratingreport.repository;

import com.cheernota.riaratingreport.entity.RegionResearch;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RegionResearchRepository extends JpaRepository<RegionResearch, RegionResearch.RegionResearchId> {

    @Query("select r from RegionResearch r where r.id.researchId = ?1")
    List<RegionResearch> findAllByResearchId(Integer researchId);
}