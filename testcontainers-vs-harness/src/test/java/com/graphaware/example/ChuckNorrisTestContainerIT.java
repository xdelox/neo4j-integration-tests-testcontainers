package com.graphaware.example;

import org.junit.jupiter.api.Test;
import org.neo4j.driver.*;
import org.testcontainers.containers.Neo4jContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.MountableFile;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
class ChuckNorrisTestContainerIT {

    @Container
    private static final Neo4jContainer neo4jContainer = new Neo4jContainer("neo4j:5.2")
        .withPlugins(MountableFile.forHostPath("target/testcontainers-vs-harness-all.jar"))
        ;

    @Test
    void quote_chuck_norris() {
        String boltUrl = neo4jContainer.getBoltUrl();
        AuthToken authToken = AuthTokens.basic("neo4j", "password");

        try (
            Driver driver = GraphDatabase.driver(boltUrl, authToken);
            Session session = driver.session();
        ) {
            // When
            String result = session.run("RETURN com.graphaware.example.chuckNorris() AS result").single().get("result").asString();

            // Then
            assertThat(result).containsIgnoringCase("Chuck");
        }
    }
}
