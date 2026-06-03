package com.maozi.db.handler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 逗号分隔字符串 TypeHandler。
 * <p>
 * 将数据库中以逗号分隔的字符串（如 "a,b,c"）与 Java 的 {@link Set}{@code <String>} 或 {@link List}{@code <String>} 双向转换。
 * 自动根据字段类型返回对应的集合实现：字段声明为 Set 则返回 {@link LinkedHashSet}，声明为 List 则返回 {@link ArrayList}。
 * </p>
 * <p>
 * 使用方式：
 * </p>
 * <pre>
 * &#64;TableField(typeHandler = CommaSeparatedTypeHandler.class)
 * private Set&lt;String&gt; tags;
 *
 * &#64;TableField(typeHandler = CommaSeparatedTypeHandler.class)
 * private List&lt;String&gt; names;
 * </pre>
 *
 * @author maozi
 */
@MappedTypes({Collection.class, List.class, Set.class})
@MappedJdbcTypes(JdbcType.VARCHAR)
public class CommaSeparatedTypeHandler extends BaseTypeHandler<Collection<String>> {

    private static final String DELIMITER = ",";

    private final Class<?> declaredType;

    public CommaSeparatedTypeHandler(Class<?> type) {
        this.declaredType = type;
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Collection<String> parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, String.join(DELIMITER, parameter));
    }

    @Override
    public Collection<String> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return toCollection(rs.getString(columnName));
    }

    @Override
    public Collection<String> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return toCollection(rs.getString(columnIndex));
    }

    @Override
    public Collection<String> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return toCollection(cs.getString(columnIndex));
    }

    private Collection<String> toCollection(String value) {
        if (value == null || value.isBlank()) {
            return isSetType() ? Set.of() : List.of();
        }
        String[] parts = value.split(DELIMITER);
        List<String> list = new ArrayList<>(parts.length);
        for (String part : parts) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                list.add(trimmed);
            }
        }
        return isSetType() ? new LinkedHashSet<>(list) : list;
    }

    private boolean isSetType() {
        return declaredType != null && Set.class.isAssignableFrom(declaredType);
    }

}
