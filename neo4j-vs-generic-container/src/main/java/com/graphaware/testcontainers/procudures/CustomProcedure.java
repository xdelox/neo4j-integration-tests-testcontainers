package com.graphaware.testcontainers.procudures;

import org.neo4j.procedure.UserFunction;

/**
 */
public class CustomProcedure {

    @UserFunction(name = "example.test")
    public Long test() {
        return 42L;
    }

}
