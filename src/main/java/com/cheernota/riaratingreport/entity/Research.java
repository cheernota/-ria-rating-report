package com.cheernota.riaratingreport.entity;

import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
@Table(name = "RESEARCH")
public class Research {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RESEARCH_ID", nullable = false)
    private Long researchId;

    @Column(name = "RESEARCH_CODE", nullable = false, unique = true)
    private String researchCode;

    @Column(name = "RESEARCH_NAME")
    private String researchName;

    @Column(name = "RESEARCH_JSON")
    private String researchJson;

    @Column(name = "json_version")
    private Integer jsonVersion;

    @Column(name = "RESEARCH_DATE")
    private LocalDate researchDate;

}
