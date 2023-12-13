package com.cheernota.riaratingreport.entity;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "REGION")
public class Region {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REGION_ID", nullable = false)
    private Integer regionId;

    @Column(name = "REGION_NAME", nullable = false)
    private String regionName;

    @Getter
    @OneToMany(mappedBy = "region", fetch = FetchType.LAZY)
    private Set<RegionResearch> regionResearchSet = new HashSet<>();
}
