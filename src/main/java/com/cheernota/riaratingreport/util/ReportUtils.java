package com.cheernota.riaratingreport.util;

import java.util.Arrays;
import java.util.Optional;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;

@UtilityClass
public class ReportUtils {

    public static CellStyle createHeaderStyle(Workbook workbook, Short colorIndex) {
        var headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(colorIndex);
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderBottom(BorderStyle.MEDIUM);
        headerStyle.setBorderLeft(BorderStyle.MEDIUM);
        headerStyle.setBorderRight(BorderStyle.MEDIUM);
        headerStyle.setBorderTop(BorderStyle.MEDIUM);
        headerStyle.setWrapText(true);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        return headerStyle;
    }

    public static CellStyle createDataStyle(Workbook workbook) {
        var dataStyle = workbook.createCellStyle();
        dataStyle.setBorderBottom(BorderStyle.THIN);
        dataStyle.setBorderLeft(BorderStyle.THIN);
        dataStyle.setBorderRight(BorderStyle.THIN);
        dataStyle.setBorderTop(BorderStyle.THIN);
        dataStyle.setWrapText(true);
        dataStyle.setVerticalAlignment(VerticalAlignment.BOTTOM);
        return dataStyle;
    }

    public static CellStyle createCommentStyle(Workbook workbook) {
        var commentStyle = workbook.createCellStyle();
        commentStyle.setWrapText(true);
        commentStyle.setVerticalAlignment(VerticalAlignment.BOTTOM);
        commentStyle.setAlignment(HorizontalAlignment.LEFT);
        return commentStyle;
    }

    public static CellStyle createSourceStyle(Workbook workbook) {
        var sourceStyle = createCommentStyle(workbook);
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 11);
        font.setFontName("Arial");
        font.setColor(IndexedColors.BLACK.getIndex());
        font.setBold(true);
        sourceStyle.setFont(font);
        return sourceStyle;
    }

    public static Short getColorIndex(String color) {
        return Arrays.stream(IndexedColors.values())
                .filter(col -> StringUtils.containsIgnoreCase(col.name(), color))
                .map(IndexedColors::getIndex)
                .findFirst()
                .orElse(IndexedColors.GREY_25_PERCENT.getIndex());
    }

    public static String cleanHeaderText(String header) {
        return Optional.ofNullable(header)
                .map(str -> str.replace("&shy;", "").replace("&nbsp;", " "))
                .orElse(StringUtils.EMPTY);
    }
}
