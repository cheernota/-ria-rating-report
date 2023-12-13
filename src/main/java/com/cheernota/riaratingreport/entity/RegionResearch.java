package com.cheernota.riaratingreport.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "REGION_RESEARCH")
public class RegionResearch {

    @EmbeddedId
    private RegionResearchId id;

    @ManyToOne
    @JoinColumn(name = "RESEARCH_ID", insertable = false, updatable = false)
    private Research research;

    @ManyToOne
    @JoinColumn(name = "REGION_ID", insertable = false, updatable = false)
    private Region region;

    @Column(name = "REGION_PLACE")
    private Integer regionPlace;

    @Embeddable
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class RegionResearchId implements Serializable {

        @Column(name = "RESEARCH_ID")
        private Integer researchId;

        @Column(name = "REGION_ID")
        private Integer regionId;
    }
}
