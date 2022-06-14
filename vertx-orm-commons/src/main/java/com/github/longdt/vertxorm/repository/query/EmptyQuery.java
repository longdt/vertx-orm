package com.github.longdt.vertxorm.repository.query;

import io.vertx.sqlclient.impl.ArrayTuple;

public class EmptyQuery<E> extends AbstractQuery<E> {
    public EmptyQuery() {
        super(ArrayTuple.EMPTY);
    }

    @Override
    public int appendQuerySql(StringBuilder sqlBuilder, int index) {
        return index;
    }

    @Override
    public boolean isConditional() {
        return false;
    }
}
