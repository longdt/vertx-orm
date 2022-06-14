package com.github.longdt.vertxorm.util;

import io.vertx.sqlclient.Tuple;
import io.vertx.sqlclient.impl.TupleInternal;

/**
 * <p>RotatedArrayTuple class.</p>
 *
 * @author Long Dinh
 * @version $Id: $Id
 */
public class RotatedArrayTuple implements TupleInternal {
    private final Object[] values;
    private final int offset;
    private final int size;

    /**
     * <p>Constructor for RotatedArrayTuple.</p>
     *
     * @param values an array of {@link Object} objects.
     * @param offset a int.
     */
    public RotatedArrayTuple(Object[] values, int offset) {
        this.values = values;
        this.offset = offset;
        this.size = values.length;
    }

    /** {@inheritDoc} */
    @Override
    public void setValue(int pos, Object value) {
        values[resolveIndex(pos)] = value;
    }

    private int resolveIndex(int pos) {
        pos += offset;
        if (pos >= size) {
            return pos - size;
        }
        return pos;
    }

    @Override
    public Object getValueInternal(int pos) {
        return values[resolveIndex(pos)];
    }

    /** {@inheritDoc} */
    @Override
    public Tuple addValue(Object value) {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    @Override
    public int size() {
        return values.length;
    }

    /** {@inheritDoc} */
    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }
}
