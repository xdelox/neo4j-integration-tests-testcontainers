package com.graphaware.testcontainers.plugins;

import org.junit.jupiter.api.Test;
import org.neo4j.driver.*;
import org.testcontainers.containers.Neo4jContainer;
import org.testcontainers.containers.Neo4jLabsPlugin;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.MountableFile;

import java.lang.Record;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

@Testcontainers
public class Neo4jLoadPluginsIT implements PluginsTest {

    @Container
    private static final Neo4jContainer neo4jContainer = new Neo4jContainer("neo4j:5.1")
        .withPlugins(MountableFile.forHostPath("target/neo4j-vs-generic-container.jar")) //This first
        .withLabsPlugins(Neo4jLabsPlugin.APOC);

    @Test
    @Override
    public void load_neo4jlabs_plugins() {
        AuthToken authToken = AuthTokens.basic("neo4j", "password");
        String boltUrl = neo4jContainer.getBoltUrl();
        System.out.println(boltUrl);
        try (
            Driver driver = GraphDatabase.driver(boltUrl, authToken);
            Session session = driver.session();
        ) {
            final List<org.neo4j.driver.Record> result = session.run("SHOW PROCEDURES WHERE name STARTS WITH 'apoc'").list();
            assertThat(result).hasSizeGreaterThan(0);
        }
    }

    @Test
    @Override
    public void load_generic_plugin_fat_jar() {
        AuthToken authToken = AuthTokens.basic("neo4j", "password");
        String boltUrl = neo4jContainer.getBoltUrl();
        System.out.println(boltUrl);
        try (
            Driver driver = GraphDatabase.driver(boltUrl, authToken);
            Session session = driver.session();
        ) {
            final Result result = session.run("RETURN example.test() AS value");
            long value = result.single().get("value").asLong();
            assertThat(value).isEqualTo(42L);
        }
    }

    @Test
    @Override
    public void load_generic_plugin_without_dependencies() {
        AuthToken authToken = AuthTokens.basic("neo4j", "password");
        String boltUrl = neo4jContainer.getBoltUrl();
        System.out.println(boltUrl);
        try (
            Driver driver = GraphDatabase.driver(boltUrl, authToken);
            Session session = driver.session();
        ) {
            final Result result = session.run("RETURN example.testWithDependency() AS value");
            var value = result.single().get("value").asList();
            assertThat(value).hasSizeGreaterThan(1);
        }
    }
}
