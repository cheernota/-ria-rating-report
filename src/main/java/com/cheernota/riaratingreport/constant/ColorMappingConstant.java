package com.cheernota.riaratingreport.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.IndexedColors;

@RequiredArgsConstructor
@Getter
public enum ColorMappingConstant {

    MAROON("Бордовый", IndexedColors.MAROON.getIndex()),
    LIGHT_TURQUOISE("Бирюзовый", IndexedColors.LIGHT_TURQUOISE.getIndex()),
    LIGHT_ORANGE("Оранжевый", IndexedColors.LIGHT_ORANGE.getIndex()),
    SEA_GREEN("Зеленый", IndexedColors.SEA_GREEN.getIndex());


    private final String value;
    private final short index;

    public static ColorMappingConstant convert(Object obj) {
        if (obj instanceof String) {
            String str = (String) obj;
            for (ColorMappingConstant constant : values()) {
                if (constant.getValue().equalsIgnoreCase(str.trim())) {
                    return constant;
                }
            }
        }
        return null;
    }
}
