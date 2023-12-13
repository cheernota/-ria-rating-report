package com.cheernota.riaratingreport.repository;

import com.cheernota.riaratingreport.entity.Research;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ResearchRepository extends JpaRepository<Research, Long>, JpaSpecificationExecutor<Research> {

    Optional<Research> findFirstByOrderByResearchDateDesc();

    @Query("select distinct r.researchCode from Research r")
    Set<String> findDistinctResearchCodes();

    @Query("select distinct r.researchId from Research r where r.researchCode = ?1")
    Optional<Integer> findIdByResearchCode(String researchCode);
}
