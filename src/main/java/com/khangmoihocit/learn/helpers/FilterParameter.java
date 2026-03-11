package com.khangmoihocit.learn.helpers;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class FilterParameter {
    //phân loại theo keyword
    public static String extractKeyword(Map<String, String[]> parameters) {
        return parameters.containsKey("keyword") ? parameters.get("keyword")[0] : null;
    }

    //phân loại theo filter đơn giản: không có page, perpage, keyword, [, sort
    //trả về map các key cần lọc, ?publish=2&&name=a
    public static Map<String, String> filterSimple(Map<String, String[]> parameters) {
        return parameters.entrySet().stream()
                .filter(entry ->
                                !entry.getKey().contains("[") &&
                                !entry.getKey().contains("keyword") &&
                                !entry.getKey().contains("page") &&
                                !entry.getKey().contains("perpage") &&
                                !entry.getKey().contains("sort"))
                .collect(Collectors.toMap(Map.Entry::getKey,
                        stringEntry -> stringEntry.getValue()[0]));
    }

    //phân loại theo filter phức tạp
    public static Map<String, Map<String, String>> filterComplex(Map<String, String[]> parameters) {
        return parameters.entrySet().stream()
                .filter(entry -> entry.getKey().contains("["))
                .collect(Collectors.groupingBy(
                        stringEntry -> stringEntry.getKey().split("\\[")[0],
                        Collectors.toMap(
                                entry -> entry.getKey().split("\\[")[1].replace("]", ""),
                                stringEntry -> stringEntry.getValue()[0])
                ));
    }

    //phân loại theo filter kiểu ngày tháng {start_date: ..., end_date: ...}
    public static Map<String, String> filterDataRange(Map<String, String[]> parameters){
        Map<String, String> dataRangeFilters = new HashMap<>();

        if(parameters.containsKey("start_date")){
            dataRangeFilters.put("start_date", parameters.get("start_date")[0]);
        }

        if(parameters.containsKey("end_date")){
            dataRangeFilters.put("end_date", parameters.get("end_date")[0]);
        }

        return dataRangeFilters;
    }
}
