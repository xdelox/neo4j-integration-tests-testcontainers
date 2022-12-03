package com.graphaware.example;

import org.junit.jupiter.api.*;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;
import org.neo4j.harness.Neo4j;
import org.neo4j.harness.Neo4jBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.anyOf;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ChuckNorrisHarnessIT {

    private Neo4j neo4jEmbedded;

    @BeforeAll
    void initializeNeo4j() {
        this.neo4jEmbedded =
            Neo4jBuilders.newInProcessBuilder()
                .withDisabledServer()
                .withFunction(ChuckNorris.class)
                .build();
    }

    @AfterAll
    void closeNeo4j() {
        this.neo4jEmbedded.close();
    }

    @Test
    void quote_chuck_norris() {
        try (Driver driver = GraphDatabase.driver(neo4jEmbedded.boltURI());
             Session session = driver.session();
        ) {
            // When
            String result = session.run("RETURN com.graphaware.example.chuckNorris() AS result").single().get("result").asString();

            // Then
            assertThat(result).containsIgnoringCase("Chuck");
        }
    }
}
