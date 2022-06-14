package com.github.longdt.vertxorm.repository.query;

import com.github.longdt.vertxorm.repository.postgresql.query.RawQuery;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RawQueryTest {
    @Test
    void buildSQL() {
        var query = new RawQuery<Object>("abc = ? and age > ?", 1, 2);
        StringBuilder builder = new StringBuilder();
        var index = query.appendQuerySql(builder, 100);
        assertEquals(index, 102);
    }
}
