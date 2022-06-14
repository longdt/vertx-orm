package com.github.longdt.vertxorm.repository.mysql;

import com.github.longdt.vertxorm.repository.SqlSupport;
import com.github.longdt.vertxorm.repository.query.Query;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * <p>SqlSupportImpl class.</p>
 *
 * @author Long Dinh
 * @version $Id: $Id
 */
public class SqlSupportImpl implements SqlSupport {
    private final String tableName;
    private final List<String> columnNames;
    private final String insertSql;
    private final String autoIdInsertSql;
    private final String upsertSql;
    private final String updateSql;
    private final String querySql;
    private final String queryByIdSql;
    private final String countSql;
    private final String existSql;
    private final String existByIdSql;
    private final String deleteSql;
    private final String deleteByIdSql;

    /**
     * <p>Constructor for SqlSupportImpl.</p>
     *
     * @param tableName   a {@link java.lang.String} object.
     * @param columnNames a {@link java.util.List} object.
     */
    public SqlSupportImpl(String tableName, List<String> columnNames) {
        this.tableName = Objects.requireNonNull(tableName);
        this.columnNames = Objects.requireNonNull(columnNames);
        insertSql = "INSERT INTO `" + tableName + "` "
                + columnNames.stream().map(c -> '`' + c + '`').collect(Collectors.joining(",", "(", ")"))
                + " VALUES "
                + IntStream.rangeClosed(1, columnNames.size()).mapToObj(idx -> "?").collect(Collectors.joining(",", "(", ")"));
        autoIdInsertSql = "INSERT INTO `" + tableName + "` "
                + columnNames.stream().skip(1).map(c -> '`' + c + '`').collect(Collectors.joining(",", "(", ")"))
                + " VALUES "
                + IntStream.rangeClosed(1, columnNames.size() - 1).mapToObj(idx -> "?").collect(Collectors.joining(",", "(", ")"));
        upsertSql = insertSql
                + " ON DUPLICATE KEY UPDATE "
                + columnNames.stream().skip(1).map(c -> '`' + c + "` = ?").collect(Collectors.joining(", "));
        updateSql = "UPDATE `" + tableName + "` SET "
                + columnNames.stream().skip(1).map(c -> '`' + c + "` = ?").collect(Collectors.joining(","))
                + " WHERE `" + getIdName() + "` = ?";
        querySql = "SELECT " + columnNames.stream().map(c -> '`' + c + '`').collect(Collectors.joining(","))
                + " FROM `" + tableName + '`';
        queryByIdSql = querySql + " WHERE `" + getIdName() + "` = ?";
        countSql = "SELECT count(*) FROM `" + tableName + "`";
        existSql = "SELECT 1 FROM `" + tableName + "`";
        existByIdSql = existSql + " WHERE `" + getIdName() + "` = ? LIMIT 1";
        deleteSql = "DELETE FROM `" + tableName + "`";
        deleteByIdSql = deleteSql + " WHERE `" + getIdName() + "` = ?";
    }

    /**
     * <p>Getter for the field <code>tableName</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    String getTableName() {
        return tableName;
    }

    String getIdName() {
        return columnNames.get(0);
    }

    @Override
    public List<String> getColumnNames() {
        return columnNames;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getInsertSql() {
        return insertSql;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAutoIdInsertSql() {
        return autoIdInsertSql;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUpsertSql() {
        return upsertSql;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUpdateSql() {
        return updateSql;
    }

    @Override
    public <E> int getUpdateSql(StringBuilder sqlBuilder, Query<E> query) {
        sqlBuilder.append(updateSql).append(" AND ");
        return query.appendQuerySql(sqlBuilder, columnNames.size());
    }

    @Override
    public int getUpdateDynamicSql(StringBuilder sqlBuilder, Object[] params) {
        sqlBuilder.append("UPDATE `").append(tableName).append("` SET ");
        int counter = 0;
        for (int i = 1; i < params.length; ++i) {
            if (params[i] != null) {
                sqlBuilder.append('`').append(columnNames.get(i)).append("`=?,");
                ++counter;
            }
        }
        if (counter > 0) {
            sqlBuilder.setLength(sqlBuilder.length() - 1);
        }
        sqlBuilder.append(" WHERE `").append(getIdName()).append("` = ?");
        return counter + 1;
    }

    @Override
    public <E> int getUpdateDynamicSql(StringBuilder sqlBuilder, Object[] params, Query<E> query) {
        int index = getUpdateDynamicSql(sqlBuilder, params);
        sqlBuilder.append(" AND ");
        return query.appendQuerySql(sqlBuilder, index);
    }

    @Override
    public int getUpdateDynamicAllSql(StringBuilder sqlBuilder, Object[] params) {
        return 0;
    }

    @Override
    public <E> int getUpdateDynamicAllSql(StringBuilder sqlBuilder, Object[] params, Query<E> query) {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getQuerySql() {
        return querySql;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getQueryByIdSql() {
        return queryByIdSql;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <E> String getSql(String sql, Query<E> query) {
        StringBuilder sqlBuilder = new StringBuilder(sql);
        if (query.isConditional()) {
            sqlBuilder.append(" WHERE ");
            query.appendQuerySql(sqlBuilder, 0);
        }
        if (query.orderBy() != null && !query.orderBy().isEmpty()) {
            sqlBuilder.append(" ORDER BY ");
            query.orderBy().forEach(o -> sqlBuilder.append("`").append(o.getFieldName()).append("` ")
                    .append(o.isDescending() ? "DESC," : "ASC,"));
            sqlBuilder.deleteCharAt(sqlBuilder.length() - 1);
        }

        if (query.limit() >= 0) {
            sqlBuilder.append(" LIMIT ?");
        }
        if (query.offset() >= 0) {
            sqlBuilder.append(" OFFSET ?");
        }
        return sqlBuilder.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCountSql() {
        return countSql;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getExistSql() {
        return existSql;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getExistByIdSql() {
        return existByIdSql;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDeleteSql() {
        return deleteSql;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDeleteByIdSql() {
        return deleteByIdSql;
    }
}
