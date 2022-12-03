package com.graphaware.testcontainers.plugins;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.neo4j.driver.*;
import org.neo4j.driver.Record;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

@Testcontainers
public class GenericContainerPluginsIT implements PluginsTest {

    @Container
    private static final GenericContainer genericContainer = new GenericContainer<>(DockerImageName.parse("neo4j:5.2"))
        .withEnv("NEO4J_AUTH", "neo4j/password")
        .withEnv("NEO4JLABS_PLUGINS", "[\"apoc\"]")
        .withFileSystemBind("target/neo4j-vs-generic-container-all.jar", "/plugins/neo4j-vs-generic-container-all.jar", BindMode.READ_WRITE)
        .withExposedPorts(7687);

    @Test
    @Override
    public void load_neo4jlabs_plugins() {
        AuthToken authToken = AuthTokens.basic("neo4j", "password");
        String boltUrl = getBoltUrl();
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
        String boltUrl = getBoltUrl();
        System.out.println(boltUrl);
        try (
            Driver driver = GraphDatabase.driver(boltUrl, authToken);
            Session session = driver.session();
        ) {
            final Result result = session.run("RETURN example.test() AS value");
            assertThat(result.single().get("value").asInt()).isEqualTo(42);
        }
    }

    @Test
    @Override
    public void load_generic_plugin_without_dependencies() {
        AuthToken authToken = AuthTokens.basic("neo4j", "password");
        String boltUrl = getBoltUrl();
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

    @NotNull
    private String getBoltUrl() {
        return "bolt://" + genericContainer.getHost() + ":" + genericContainer.getMappedPort(7687);
    }

}
