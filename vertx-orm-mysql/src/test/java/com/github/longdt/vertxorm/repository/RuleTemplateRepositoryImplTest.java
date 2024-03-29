package com.github.longdt.vertxorm.repository;

import com.github.longdt.vertxorm.model.ArgumentDescription;
import com.github.longdt.vertxorm.model.RuleTemplate;
import com.github.longdt.vertxorm.repository.mysql.query.QueryFactory;
import com.github.longdt.vertxorm.util.DatabaseTestCase;
import com.github.longdt.vertxorm.util.Futures;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class RuleTemplateRepositoryImplTest extends DatabaseTestCase {
    private final RuleTemplateRepository repository = new RuleTemplateRepositoryImpl(pool);
    private static final String DEFAULT_RULE_TEMPLATE_NAME = "Sample Rule Template";

    @Test
    void insert(Vertx vertx, VertxTestContext testContext) {
        var arguements = new HashMap<String, ArgumentDescription>();
        arguements.put("max_txn_cnt", new ArgumentDescription().setName("max_txn_cnt").setType(ArgumentDescription.ValueType.INTEGER));
        arguements.put("max_txn_amount", new ArgumentDescription().setName("max_txn_amount").setType(ArgumentDescription.ValueType.INTEGER));
        var now = LocalDateTime.now();
        var template = new RuleTemplate()
                .setActive(true)
                .setName(DEFAULT_RULE_TEMPLATE_NAME)
                .setFlinkJob("Flink Job")
                .setArguments(arguements)
                .setCreatedAt(now)
                .setUpdatedAt(now);
        repository.insert(template)
                .onComplete(testContext.succeeding(entity -> testContext.verify(() -> {
                    assertNotNull(entity);
                    assertEquals(entity.getId(), 1);
                    assertEquals(entity.getFlinkJob(), template.getFlinkJob());
                    assertTrue(entity.getActive());
                    testContext.completeNow();
                })));
    }

    @Test
    void save(Vertx vertx, VertxTestContext testContext) {
        var arguements = new HashMap<String, ArgumentDescription>();
        arguements.put("max_txn_cnt", new ArgumentDescription().setName("max_txn_cnt").setType(ArgumentDescription.ValueType.INTEGER));
        arguements.put("max_txn_amount", new ArgumentDescription().setName("max_txn_amount").setType(ArgumentDescription.ValueType.INTEGER));
        var now = LocalDateTime.now();
        var template = new RuleTemplate()
                .setActive(true)
                .setName(DEFAULT_RULE_TEMPLATE_NAME)
                .setFlinkJob("Flink Job")
                .setArguments(arguements)
                .setCreatedAt(now)
                .setUpdatedAt(now);
        template = Futures.join(repository.insert(template));
        assertTrue(template.getActive());
        assertEquals("Flink Job", template.getFlinkJob());

        template.setActive(false)
                .setFlinkJob("Some Job");
        repository.save(template)
                .onComplete(testContext.succeeding(entity -> testContext.verify(() -> {
                    assertNotNull(entity);
                    assertEquals(entity.getId(), 1);
                    assertEquals(entity.getFlinkJob(), "Some Job");
                    assertFalse(entity.getActive());
                    testContext.completeNow();
                })));
    }

    @Test
    void update_Success(Vertx vertx, VertxTestContext testContext) {
        awaitCompletion(this::insert, vertx);
        var now = LocalDateTime.now();
        var template = new RuleTemplate()
                .setActive(true)
                .setName("Sample Rule Template")
                .setFlinkJob("Updated Flink Job")
                .setArguments(Collections.emptyMap())
                .setUpdatedAt(now)
                .setId(1);
        repository.update(template)
                .onComplete(testContext.succeeding(entity -> testContext.verify(() -> {
                    assertNotNull(entity);
                    assertEquals(entity.getId(), 1);
                    assertEquals(entity.getFlinkJob(), template.getFlinkJob());
                    assertTrue(entity.getArguments().isEmpty());
                    assertTrue(entity.getActive());
                    testContext.completeNow();
                })));
    }

    @Test
    void update_Fail(Vertx vertx, VertxTestContext testContext) {
        awaitCompletion(this::insert, vertx);
        var now = LocalDateTime.now();
        var template = new RuleTemplate()
                .setActive(true)
                .setName("Sample Rule Template")
                .setFlinkJob("Updated Flink Job")
                .setArguments(Collections.emptyMap())
                .setUpdatedAt(now)
                .setId(2);
        repository.update(template)
                .onComplete(testContext.failing(throwable -> testContext.verify(() -> {
                    assertEquals(throwable.getClass(), EntityNotFoundException.class);
                    testContext.completeNow();
                })));
    }

    @Test
    void updateQuery_Success(Vertx vertx, VertxTestContext testContext) {
        awaitCompletion(this::insert, vertx);
        var now = LocalDateTime.now();
        var template = new RuleTemplate()
                .setActive(true)
                .setName("Sample Rule Template")
                .setFlinkJob("Updated Flink Job")
                .setArguments(Collections.emptyMap())
                .setUpdatedAt(now)
                .setId(1);
        repository.update(template, QueryFactory.equal("active", 1))
                .onComplete(testContext.succeeding(entity -> testContext.verify(() -> {
                    assertNotNull(entity);
                    assertEquals(entity.getId(), 1);
                    assertEquals(entity.getFlinkJob(), template.getFlinkJob());
                    assertTrue(entity.getArguments().isEmpty());
                    assertTrue(entity.getActive());
                    testContext.completeNow();
                })));
    }

    @Test
    void updateQuery_Fail(Vertx vertx, VertxTestContext testContext) {
        awaitCompletion(this::insert, vertx);
        var now = LocalDateTime.now();
        var template = new RuleTemplate()
                .setActive(true)
                .setName("Sample Rule Template")
                .setFlinkJob("Updated Flink Job")
                .setArguments(Collections.emptyMap())
                .setUpdatedAt(now)
                .setId(1);
        repository.update(template, QueryFactory.equal("active", 0))
                .onComplete(testContext.failing(throwable -> testContext.verify(() -> {
                    assertEquals(throwable.getClass(), EntityNotFoundException.class);
                    testContext.completeNow();
                })));
    }

    @Test
    void updateDynamic_Success(Vertx vertx, VertxTestContext testContext) {
        awaitCompletion(this::insert, vertx);
        var template = new RuleTemplate()
                .setName("A Random Name")
                .setId(1);
        repository.updateDynamic(template)
                .compose(r -> repository.find(1))
                .map(Optional::orElseThrow)
                .onComplete(testContext.succeeding(entity -> testContext.verify(() -> {
                    assertNotNull(entity);
                    assertEquals(entity.getId(), 1);
                    assertEquals(entity.getName(), template.getName());
                    assertNotNull(entity.getArguments());
                    assertFalse(entity.getActive());
                    testContext.completeNow();
                })));
    }

    @Test
    void updateDynamic_Fail(Vertx vertx, VertxTestContext testContext) {
        awaitCompletion(this::insert, vertx);
        var template = new RuleTemplate()
                .setName("A Random Name")
                .setId(2);
        repository.updateDynamic(template)
                .onComplete(testContext.failing(throwable -> testContext.verify(() -> {
                    assertEquals(throwable.getClass(), EntityNotFoundException.class);
                    testContext.completeNow();
                })));
    }

    @Test
    void updateDynamicQuery_Success(Vertx vertx, VertxTestContext testContext) {
        awaitCompletion(this::insert, vertx);
        var template = new RuleTemplate()
                .setName("A Random Name")
                .setId(1);
        repository.updateDynamic(template, QueryFactory.equal("active", 1))
                .compose(r -> repository.find(1))
                .map(Optional::orElseThrow)
                .onComplete(testContext.succeeding(entity -> testContext.verify(() -> {
                    assertNotNull(entity);
                    assertEquals(entity.getId(), 1);
                    assertEquals(entity.getName(), template.getName());
                    assertNotNull(entity.getArguments());
                    assertFalse(entity.getActive());
                    testContext.completeNow();
                })));
    }

    @Test
    void updateDynamicQuery_Fail(Vertx vertx, VertxTestContext testContext) {
        awaitCompletion(this::insert, vertx);
        var template = new RuleTemplate()
                .setName("A Random Name")
                .setId(1);
        repository.updateDynamic(template, QueryFactory.equal("active", 0))
                .onComplete(testContext.failing(throwable -> testContext.verify(() -> {
                    assertEquals(throwable.getClass(), EntityNotFoundException.class);
                    testContext.completeNow();
                })));
    }

    @Test
    void find(Vertx vertx, VertxTestContext testContext) {
        awaitCompletion(this::insert, vertx);
        repository.find(1)
                .onComplete(testContext.succeeding(rs -> testContext.verify(() -> {
                    var entity = rs.orElseThrow();
                    assertNotNull(entity);
                    assertEquals(entity.getId(), 1);
                    assertTrue(entity.getActive());
                    testContext.completeNow();
                })));
    }

    @Test
    void findAll(Vertx vertx, VertxTestContext testContext) {
        awaitCompletion(this::insert, vertx);
        repository.findAll()
                .onComplete(testContext.succeeding(rs -> testContext.verify(() -> {
                    assertEquals(rs.size(), 1);
                    var entity = rs.get(0);
                    assertNotNull(entity);
                    assertEquals(entity.getId(), 1);
                    assertTrue(entity.getActive());
                    testContext.completeNow();
                })));
    }
}
