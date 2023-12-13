package com.cheernota.riaratingreport.service;

import com.cheernota.riaratingreport.dto.ResearchJsonDto;
import javax.validation.constraints.NotEmpty;
import org.apache.commons.lang3.StringUtils;

public interface ResearchHandler<T extends ResearchJsonDto> {
    void handleResearch(T researchDto, String researchCode);

    /* change some latin symbols to cyrillic and replace special symbols */
    default String handleRegionName(@NotEmpty String regionName) {
        regionName = StringUtils.replaceChars(regionName, "CH", "СН");
        regionName = regionName.replace("&nbsp;", " ")
                .replace('\u00A0', ' ')
                .replace("&mdash;", "—")
                .replace("\r\n", " ")
                .replace("  ", " ")
                .replace(" – ", " — ")
                .replace(" ⎯ ", " — ")
                .trim();
        if (regionName.equals("Ханты-Мансийский автономный округ")) {
            regionName = regionName.concat(" — Югра");
        }
        return regionName.replace("республика", "Республика")
                .replace("Осетия-Алания", "Осетия — Алания");
    }
}
