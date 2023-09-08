package com.example.account.config.typehandler;

import com.example.account.entity.Currency;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.Array;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static org.hibernate.validator.internal.util.CollectionHelper.newArrayList;


public class ListTypeHandler extends BaseTypeHandler<List<Currency>> {
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<Currency> parameter, JdbcType jdbcType) throws SQLException {
        Array array = ps.getConnection().createArrayOf("text", parameter.toArray());
        ps.setArray(i, array);
    }

    @Override
    public List<Currency> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return toList(rs.getArray(columnName));
    }

    @Override
    public List<Currency> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return toList(rs.getArray(columnIndex));
    }

    @Override
    public List<Currency> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return toList(cs.getArray(columnIndex));
    }

    private List<Currency> toList(Array pgArray) throws SQLException {
        if (pgArray == null) return newArrayList();

        Currency[] currencies = (Currency[]) pgArray.getArray();
        return Arrays.stream(currencies).toList();
    }
}
