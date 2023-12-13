package com.cheernota.riaratingreport.service;

import com.cheernota.riaratingreport.dto.request.ResearchFilterRqDto;

public interface ReportService {

    byte[] getResearchReportByFilter(ResearchFilterRqDto filterRqDto);
}
