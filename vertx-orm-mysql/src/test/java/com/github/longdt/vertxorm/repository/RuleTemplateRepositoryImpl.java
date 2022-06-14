package com.github.longdt.vertxorm.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.longdt.vertxorm.model.RuleTemplate;
import com.github.longdt.vertxorm.repository.mysql.AbstractCrudRepository;
import io.vertx.core.json.Json;
import io.vertx.core.json.jackson.DatabindCodec;
import io.vertx.sqlclient.Pool;

import java.util.List;

public class RuleTemplateRepositoryImpl extends AbstractCrudRepository<Integer, RuleTemplate> implements RuleTemplateRepository {
    public RuleTemplateRepositoryImpl(Pool pool) {
        var conf = new Configuration<Integer, RuleTemplate>()
                .setTableName("rule_template")
                .setColumnNames(List.of("id", "name", "arguments", "flink_job", "active", "created_at", "updated_at"))
                .setIdAccessor(new IdAccessor<>() {
                    @Override
                    public Integer getId(RuleTemplate entity) {
                        return entity.getId();
                    }

                    @Override
                    public void setId(RuleTemplate entity, Integer integer) {
                        entity.setId(integer);
                    }

                    @Override
                    public Integer db2IdValue(Object value) {
                        return ((Number) value).intValue();
                    }
                })
                .setRowMapper(row -> new RuleTemplate().setId(row.getInteger(0))
                        .setName(row.getString(1))
                        .setArguments(row.getString(2) != null ? ((DatabindCodec) Json.CODEC).fromString(row.getString(2), new TypeReference<>() {
                        }) : null)
                        .setFlinkJob(row.getString(3))
                        .setActive(row.getBoolean(4))
                        .setCreatedAt(row.getLocalDateTime(5))
                        .setUpdatedAt(row.getLocalDateTime(6)))
                .setParametersMapper(ruleTemplate -> {
                    Object[] data = new Object[7];
                    data[0] = ruleTemplate.getId();
                    data[1] = ruleTemplate.getName();
                    data[2] = ruleTemplate.getArguments() != null ? Json.encode(ruleTemplate.getArguments()) : null;
                    data[3] = ruleTemplate.getFlinkJob();
                    data[4] = ruleTemplate.getActive();
                    data[5] = ruleTemplate.getCreatedAt();
                    data[6] = ruleTemplate.getUpdatedAt();
                    return data;
                });

        init(pool, conf);
    }
}
