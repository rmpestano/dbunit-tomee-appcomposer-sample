package com.github.rmannibucau.sample;

import com.github.dbunit.rules.DBUnitRule;
import com.github.dbunit.rules.api.dataset.DataSet;
import com.github.rmannibucau.rules.api.dbunit.DbUnitInstance;
import org.apache.openejb.junit.ApplicationComposerRule;
import org.apache.openejb.testing.*;
import org.jboss.arquillian.persistence.Cleanup;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import java.sql.SQLException;

import static org.jboss.arquillian.persistence.TestExecutionPhase.AFTER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.rules.RuleChain.outerRule;
import static org.junit.runners.MethodSorters.NAME_ASCENDING;

@Default
@Classes(cdi = true)
@Descriptors(@Descriptor(name = "persistence.xml", path = "META-INF/persistence.xml"))
@ContainerProperties({
        @ContainerProperties.Property(name = "jdbc/user", value = "new://Resource?type=DataSource"),
        @ContainerProperties.Property(name = "jdbc/user.LogSql", value = "true")
})
@DataSet(cleanBefore = true)
public class UserRepositoryTest {

    @Resource
    @DbUnitInstance
    private DataSource dataSource;

    @Rule
    public final TestRule rules = outerRule(new ApplicationComposerRule(this))
            .around(DBUnitRule.instance(() -> {
                try {
                    return dataSource.getConnection();
                } catch (SQLException e) {
                    e.printStackTrace();
                    return null;
                }
            }));


    @Inject
    private UserRepository repository;


    @Test
    @DataSet("datasets/users.yml")
    public void find1() {
        assertEquals("John Smith", repository.find(1L).getName());
        assertEquals("Clark Kent", repository.find(2L).getName());
    }

    @Test
    public void find2() { // ensure we didn't leak previous dataset
        assertNull(repository.find(1L));
    }
}
