package com.github.rmannibucau.sample;

import com.github.rmannibucau.rules.api.dbunit.ArquillianPersistenceDbUnitRule;
import com.github.rmannibucau.rules.api.dbunit.DbUnitInstance;
import org.apache.openejb.junit.ApplicationComposerRule;
import org.apache.openejb.testing.Classes;
import org.apache.openejb.testing.ContainerProperties;
import org.apache.openejb.testing.Default;
import org.apache.openejb.testing.Descriptor;
import org.apache.openejb.testing.Descriptors;
import org.jboss.arquillian.persistence.Cleanup;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.sql.DataSource;

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
@Cleanup(phase = AFTER)
@FixMethodOrder(NAME_ASCENDING) // show that find2 got the cleanup in between, useless out of that "demo" context
public class UserRepositoryTest {
    @Rule
    public final TestRule rules = outerRule(new ApplicationComposerRule(this))
            .around(new ArquillianPersistenceDbUnitRule().resourcesHolder(this));

    @Resource
    @DbUnitInstance
    private DataSource dataSource;

    @Inject
    private UserRepository repository;

    @Test
    @UsingDataSet("datasets/users.yml")
    public void find1() {
        assertEquals("John Smith", repository.find(1L).getName());
        assertEquals("Clark Kent", repository.find(2L).getName());
    }

    @Test
    public void find2() { // ensure we didn't leak previous dataset
        assertNull(repository.find(1L));
    }
}
