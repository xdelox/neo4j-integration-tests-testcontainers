package com.graphaware.config.adminpassword;


import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.neo4j.driver.*;
import org.testcontainers.containers.Neo4jContainer;
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy;
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;
import org.testcontainers.containers.wait.strategy.WaitStrategy;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.net.HttpURLConnection;

import static org.assertj.core.api.Assertions.assertThat;


@Testcontainers
@Tag("neo4j-module")
class Neo4jContainerChangeAdminPasswordIT {

    //  private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(AdminPasswordConfigIT.class);

    public static final WaitStrategy WAIT_FOR_BOLT = new LogMessageWaitStrategy()
        .withRegEx(String.format(".*Bolt enabled on .*:%d\\.\n", 7687));

    private static final WaitStrategy WAIT_FOR_HTTP = new HttpWaitStrategy()
        .forPort(7474)
        .forStatusCodeMatching(response -> response == HttpURLConnection.HTTP_OK);

    private static final String ANOTHER_PASSWORD = "QuickSilver";

    @Container
    private final Neo4jContainer neo4jContainer = new Neo4jContainer("neo4j:5.2")
        .withAdminPassword(ANOTHER_PASSWORD);


    @Test
    void change_admin_password() {
        neo4jContainer.start();
        AuthToken authToken = AuthTokens.basic("neo4j", ANOTHER_PASSWORD);
        try (
            Driver driver = GraphDatabase.driver(neo4jContainer.getBoltUrl(), authToken);
            Session session = driver.session();
        ) {
            var result = session.run("RETURN 1 as test").list();

            assertThat(result).hasSize(1);
        }
    }
}
