package com.cheernota.riaratingreport.repository;

import com.cheernota.riaratingreport.entity.Region;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RegionRepository extends JpaRepository<Region, Long> {

    @Query("select distinct r.regionName from Region r")
    Set<String> findDistinctRegionNames();

    @Query("select distinct r.regionId from Region r where r.regionName = ?1")
    Optional<Integer> findIdByRegionName(String regionName);
}
