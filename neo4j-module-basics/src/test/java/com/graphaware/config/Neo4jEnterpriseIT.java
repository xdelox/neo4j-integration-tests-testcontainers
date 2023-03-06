package com.graphaware.config;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.neo4j.driver.*;
import org.testcontainers.containers.Neo4jContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@Tag("neo4j-module")
class Neo4jEnterpriseIT {

    @Container
    private final Neo4jContainer neo4jContainer = (Neo4jContainer) new Neo4jContainer(DockerImageName.parse("docker.graphaware.com/mirror/neo4j:4.4.14-enterprise")
        .asCompatibleSubstituteFor("neo4j"))
        .withEnv("NEO4J_ACCEPT_LICENSE_AGREEMENT", "yes");


    @Test
    void start_neo4j_enterprise() {

        try (Driver driver = GraphDatabase.driver(neo4jContainer.getBoltUrl(), AuthTokens.basic("neo4j", "password"));
             Session session = driver.session()
        ) {
            String edition = session
                .run("CALL dbms.components() YIELD edition RETURN edition", Collections.emptyMap())
                .next()
                .get(0)
                .asString();
            assertThat(edition).isEqualTo("enterprise");
        }
    }
}
