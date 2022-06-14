package com.github.longdt.vertxorm.repository.mysql.query;

import com.github.longdt.vertxorm.repository.query.AbstractQuery;
import io.vertx.sqlclient.Tuple;

/**
 * <p>RawQuery class.</p>
 *
 * @author Long Dinh
 * @version $Id: $Id
 */
public class RawQuery<E> extends AbstractQuery<E> {
    protected String querySql;

    /**
     * <p>Constructor for RawQuery.</p>
     *
     * @param querySql a {@link java.lang.String} object.
     */
    public RawQuery(String querySql) {
        this(querySql, QueryFactory.EMPTY_PARAMS);
    }

    /**
     * <p>Constructor for RawQuery.</p>
     *
     * @param querySql a {@link java.lang.String} object.
     * @param params a {@link java.lang.Object} object.
     */
    public RawQuery(String querySql, Object... params) {
        this(querySql, Tuple.wrap(params));
    }

    /**
     * <p>Constructor for RawQuery.</p>
     *
     * @param querySql a {@link java.lang.String} object.
     * @param params a {@link io.vertx.sqlclient.Tuple} object.
     */
    public RawQuery(String querySql, Tuple params) {
        super(params);
        this.querySql = querySql;
    }

    /** {@inheritDoc} */
    @Override
    public int appendQuerySql(StringBuilder sqlBuilder, int index) {
	sqlBuilder.append(querySql);
        return index + params.size();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isConditional() {
        return querySql != null;
    }
}
