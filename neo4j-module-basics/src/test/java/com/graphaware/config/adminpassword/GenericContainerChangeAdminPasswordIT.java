package com.graphaware.config.adminpassword;


import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.neo4j.driver.*;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy;
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;
import org.testcontainers.containers.wait.strategy.WaitStrategy;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.net.HttpURLConnection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@Testcontainers
@Tag("generic")
class GenericContainerChangeAdminPasswordIT {

  //  private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(AdminPasswordConfigIT.class);

    public static final WaitStrategy WAIT_FOR_BOLT = new LogMessageWaitStrategy()
        .withRegEx(String.format(".*Bolt enabled on .*:%d\\.\n", 7687));

    private static final WaitStrategy WAIT_FOR_HTTP = new HttpWaitStrategy()
        .forPort(7474)
        .forStatusCodeMatching(response -> response == HttpURLConnection.HTTP_OK);
    private static final String ANOTHER_PASSWORD = "QuickSilver";

    @Container
    private final GenericContainer genericContainer = new GenericContainer<>(DockerImageName.parse("neo4j:5.1"))
        .withEnv("NEO4J_AUTH","neo4j/"+ANOTHER_PASSWORD)
        .withExposedPorts(7687);

    @Test
    void change_admin_password() {
        genericContainer.start();

        AuthToken authToken = AuthTokens.basic("neo4j", ANOTHER_PASSWORD);
        String boltUrl = "bolt://" + genericContainer.getHost() + ":" + genericContainer.getMappedPort(7687);
        System.out.println(boltUrl);
        try (
            Driver driver = GraphDatabase.driver(boltUrl, authToken);
            Session session = driver.session();
        ) {
            final List<org.neo4j.driver.Record> result = session.run("RETURN 1 as test").list();
            assertThat(result).hasSize(1);
        }
    }


}
