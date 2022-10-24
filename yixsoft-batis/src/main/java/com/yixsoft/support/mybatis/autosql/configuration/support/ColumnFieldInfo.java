package com.yixsoft.support.mybatis.autosql.configuration.support;

import com.google.common.base.CaseFormat;
import com.yixsoft.support.mybatis.autosql.annotations.Column;
import com.yixsoft.support.mybatis.support.typedef.ClassFieldsDescription;
import com.yixsoft.support.mybatis.support.typedef.FieldDescription;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Create by davep at 2020-02-27 16:53
 */
public class ColumnFieldInfo implements Comparable<ColumnFieldInfo> {
    private final FieldDescription description;
    private final int matchRank;
    private final List<String> possibleNames;
    private String matchingColumn;

    public ColumnFieldInfo(FieldDescription description) {
        this.description = description;
        Column columnAnnotation = description.findAnnotation(Column.class);
        String annotationName = Optional.ofNullable(columnAnnotation).map(Column::value).orElse(null);
        if (annotationName != null) {
            this.matchRank = 0;
            this.possibleNames = new ArrayList<>(Collections.singletonList(annotationName));
        } else {
            this.possibleNames = Arrays.stream(CaseFormat.values())
                    .flatMap(sFormat -> Arrays.stream(CaseFormat.values())
                            .map(tFormat -> sFormat.to(tFormat, this.description.getFieldName())))
                    .distinct()
                    .collect(Collectors.toList());
            this.matchRank = 1;
        }
    }

    public static Map<String, ColumnFieldInfo> mapFields(List<ColumnFieldInfo> fields, Set<String> columnNames) {
        return columnNames.stream()
                .map(column -> new ClassFieldsDescription.KeyValuePair<>(column, findMatchField(fields, column)))
                .filter(ClassFieldsDescription.KeyValuePair::valueNotNull)
                .collect(HashMap::new, (map, pair) -> map.put(pair.getKey(), pair.getValue()), HashMap::putAll);
    }

    public static ColumnFieldInfo findMatchField(List<ColumnFieldInfo> fields, String name) {
        return fields.stream().filter(desc -> desc.matchName(name))
                .min(Comparator.comparing(ColumnFieldInfo::getMatchRank)).orElse(null);
    }

    public int getMatchRank() {
        return matchRank;
    }

    public List<String> getPossibleNames() {
        return possibleNames;
    }

    public boolean matchName(String searchColumnName) {
        boolean match = possibleNames.stream().anyMatch(name -> name.equals(searchColumnName));
        if (match) {
            matchingColumn = searchColumnName;
        }
        return match;
    }

    @Override
    public int compareTo(ColumnFieldInfo o) {
        return Integer.compare(this.matchRank, o.matchRank);
    }

    public String getMatchingColumn() {
        return matchingColumn;
    }

    public String getFieldName() {
        return description.getFieldName();
    }
}
