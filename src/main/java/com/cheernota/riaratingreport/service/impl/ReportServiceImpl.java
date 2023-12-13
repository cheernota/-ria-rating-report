package com.cheernota.riaratingreport.service.impl;

import static com.cheernota.riaratingreport.util.ReportUtils.cleanHeaderText;
import static com.cheernota.riaratingreport.util.ReportUtils.createCommentStyle;
import static com.cheernota.riaratingreport.util.ReportUtils.createDataStyle;
import static com.cheernota.riaratingreport.util.ReportUtils.createHeaderStyle;
import static com.cheernota.riaratingreport.util.ReportUtils.createSourceStyle;
import static com.cheernota.riaratingreport.util.ReportUtils.getColorIndex;
import static com.cheernota.riaratingreport.util.ResearchSpecification.buildResearchSpecification;

import com.cheernota.riaratingreport.constant.ColorMappingConstant;
import com.cheernota.riaratingreport.dto.ResearchV1JsonDto;
import com.cheernota.riaratingreport.dto.ResearchV1JsonDto.AppData;
import com.cheernota.riaratingreport.dto.ResearchV1JsonDto.SlideDataHeader;
import com.cheernota.riaratingreport.dto.ResearchV2JsonDto;
import com.cheernota.riaratingreport.dto.request.ResearchFilterRqDto;
import com.cheernota.riaratingreport.entity.Research;
import com.cheernota.riaratingreport.exception.NotFoundException;
import com.cheernota.riaratingreport.exception.WorkbookException;
import com.cheernota.riaratingreport.repository.ResearchRepository;
import com.cheernota.riaratingreport.service.ReportService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private static final int DEFAULT_COLUMN_WIDTH = 25;
    private final ResearchRepository researchRepository;
    private final ObjectMapper objectMapper;

    @Override
    public byte[] getResearchReportByFilter(ResearchFilterRqDto filterRqDto) {
        byte[] bytes;
        Specification<Research> spec = Specification.where(buildResearchSpecification(filterRqDto));
        List<Research> researchList = researchRepository.findAll(spec);
        if (CollectionUtils.isEmpty(researchList)) {
            throw new NotFoundException("No data found to report");
        }

        try (Workbook workbook = new HSSFWorkbook()) {
            for (final var research : researchList) {
                if (research.getJsonVersion() == 1) {
                    fillReportV1(workbook, research);
                } else if (research.getJsonVersion() == 2) {
                    fillReportV2(workbook, research);
                }
            }
            bytes = getByteArray(workbook);
        } catch (IOException e) {
            log.error("Error while filling the workbook: {}", e.getMessage());
            throw new WorkbookException(e.getMessage());
        }
        return bytes;
    }

    private void fillReportV1(Workbook workbook, Research research) throws JsonProcessingException {
        var researchDto = objectMapper.readValue(research.getResearchJson(), ResearchV1JsonDto.class);
        if (CollectionUtils.isEmpty(researchDto.getSlides())) {
            return;
        }

        Sheet sheet = workbook.createSheet();
        sheet.setDefaultColumnWidth(DEFAULT_COLUMN_WIDTH);
        var slideListOpt = researchDto.getSlides().stream()
                .map(ResearchV1JsonDto.Slide::getData)
                .findFirst();
        if (slideListOpt.isEmpty()) {
            // skip if there is no data available
            return;
        }

        var slideList = slideListOpt.get();
        var headerList = slideList.getHeaders();
        var colorIndex = getColorIndex(slideList.getColor());
        var headerStyle = createHeaderStyle(workbook, colorIndex);

        // add report title
        var titleRow = sheet.createRow(0);
        var titleCell = titleRow.createCell(0);
        titleCell.setCellStyle(createSourceStyle(workbook));
        titleCell.setCellValue(research.getResearchName());
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, headerList.size() - 1));

        // add headers and add main data
        var rowNum = 1;
        addReportHeaders(sheet, rowNum++, headerStyle, headerList);
        var dataList = slideList.getAppData();
        var dataStyle = createDataStyle(workbook);
        for (final var data : dataList) {
            fillDataCell(sheet, headerList, rowNum++, dataStyle, data);
        }

        // add summary row
        if (CollectionUtils.isNotEmpty(slideList.getSummary())) {
            var summaryStyle = createDataStyle(workbook);
            summaryStyle.setFillForegroundColor(colorIndex);
            summaryStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            fillDataCell(sheet, headerList, rowNum++, summaryStyle, slideList.getSummary());
        }

        // add comment row if exists
        var sourceComment = Optional.ofNullable(slideList.getText())
                .map(ResearchV1JsonDto.SlideDataText::getSourceComment);
        if (sourceComment.isPresent() && StringUtils.isNotBlank(sourceComment.get())) {
            var commentRow = sheet.createRow(rowNum);
            var commentCell = commentRow.createCell(0);
            commentCell.setCellStyle(createCommentStyle(workbook));
            commentCell.setCellValue(sourceComment.get());
            sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, headerList.size() - 1));
            setHeightForMergedCells(headerList.size(), sourceComment.get(), commentRow);
            rowNum++;
        }

        // fill source row if exists
        var source = Optional.ofNullable(slideListOpt.get().getText())
                .map(ResearchV1JsonDto.SlideDataText::getSource);
        if (source.isPresent() && StringUtils.isNotBlank(source.get())) {
            var sourceRow = sheet.createRow(rowNum);
            var sourceCell = sourceRow.createCell(0);
            sourceCell.setCellStyle(createSourceStyle(workbook));
            sourceCell.setCellValue(source.get());
            sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, 1));
        }
    }

    private void fillReportV2(Workbook workbook, Research research) throws JsonProcessingException {
        Sheet sheet = workbook.createSheet();
        sheet.setDefaultColumnWidth(DEFAULT_COLUMN_WIDTH);

        var researchDto = objectMapper.readValue(research.getResearchJson(), ResearchV2JsonDto.class);
        var colorIndex = Optional.ofNullable(ColorMappingConstant.convert(researchDto.getStyle()))
                .map(ColorMappingConstant::getIndex)
                .orElse(IndexedColors.GREY_25_PERCENT.getIndex());
        var headerStyle = createHeaderStyle(workbook, colorIndex);
        var dataStyle = createDataStyle(workbook);

        // add report title
        var titleRow = sheet.createRow(0);
        var titleCell = titleRow.createCell(0);
        titleCell.setCellStyle(createSourceStyle(workbook));
        titleCell.setCellValue(research.getResearchName());
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, researchDto.getData().size() - 1));


        // add headers
        var rowNum = 1;
        var headerRow = sheet.createRow(rowNum++);
        var dataList = researchDto.getData();
        for (var i = 0; i < dataList.size(); i++) {
            var infoData = dataList.get(i);
            var headerCell = headerRow.createCell(i);
            headerCell.setCellStyle(headerStyle);
            headerCell.setCellValue(cleanHeaderText(infoData.getHeader()));
        }

        // add data
        var valueList = dataList.get(0).getData();
        for (var i = 0; i < valueList.size(); i++) {
            var dataRow = sheet.createRow(rowNum++);
            for (var k = 0; k < dataList.size(); k++) {
                var dataCell = dataRow.createCell(k);
                dataCell.setCellStyle(dataStyle);
                dataCell.setCellValue(dataList.get(k).getData().get(i));
            }
        }

        // fill footer
        var footer = researchDto.getFooter();
        if (StringUtils.isNotBlank(footer.getMethods())) {
            var methodsRow = sheet.createRow(rowNum);
            var methodsCell = methodsRow.createCell(0);
            methodsCell.setCellStyle(createCommentStyle(workbook));
            methodsCell.setCellValue(footer.getMethods());
            sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, dataList.size() - 1));
            setHeightForMergedCells(dataList.size(), footer.getMethods(), methodsRow);
            rowNum++;
        }
        if (StringUtils.isNotBlank(footer.getSource())) {
            var sourceRow = sheet.createRow(rowNum);
            var sourceCell = sourceRow.createCell(0);
            sourceCell.setCellStyle(createSourceStyle(workbook));
            sourceCell.setCellValue(footer.getSource());
            sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, 1));
        }
    }

    /**
     * Excel couldn`t auto-fit rows to content for all rows that have merged cells in them.
     */
    private static void setHeightForMergedCells(int colNum,
                                                @NonNull String text,
                                                @NonNull Row targetRow) {
        var lineCnt = (int) WordUtils.wrap(text, DEFAULT_COLUMN_WIDTH * colNum).lines().count();
        targetRow.setHeight((short) (targetRow.getHeight() * lineCnt));
    }

    private static void addReportHeaders(Sheet sheet, int rowNumber, CellStyle cellStyle,
                                         List<SlideDataHeader> headerList) {
        var headerRow = sheet.createRow(rowNumber);
        for (var i = 0; i < headerList.size(); i++) {
            var cell = headerRow.createCell(i);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(cleanHeaderText(headerList.get(i).getHeaderText()));
        }
    }

    private static void fillDataCell(Sheet sheet, List<SlideDataHeader> headerList, int rowNumber,
                                     CellStyle dataStyle, List<AppData> summaryList) {
        var dataRow = sheet.createRow(rowNumber);
        for (var i = 0; i < headerList.size(); i++) {
            var cell = dataRow.createCell(i);
            cell.setCellStyle(dataStyle);
            cell.setCellValue(summaryList.get(i).getDataText());
        }
    }

    private static byte[] getByteArray(Workbook workbook) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            workbook.write(bos);
            return bos.toByteArray();
        } catch (IOException e) {
            log.error("Error while writing to workbook: {}", e.getMessage());
            throw new WorkbookException(e.getMessage());
        }
    }
}
