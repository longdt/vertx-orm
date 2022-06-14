package com.github.longdt.vertxorm.repository.mysql.query;

import com.github.longdt.vertxorm.repository.query.SingleQuery;

/**
 * <p>LessThan class.</p>
 *
 * @author Long Dinh
 * @version $Id: $Id
 */
public class LessThan<O, A extends Comparable<A>> extends SingleQuery<O> {

    /**
     * <p>Constructor for LessThan.</p>
     *
     * @param fieldName a {@link java.lang.String} object.
     * @param value a A object.
     */
    public LessThan(String fieldName, A value) {
        super(fieldName, value);
    }

    /** {@inheritDoc} */
    @Override
    public int appendQuerySql(StringBuilder sqlBuilder, int index) {
        sqlBuilder.append('`')
                .append(fieldName)
                .append("`<?");
        return index + 1;
    }
}
