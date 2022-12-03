package com.graphaware.testcontainers.procudures;


import org.neo4j.procedure.UserFunction;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 */
public class CustomProcedureWithDependency {

    @UserFunction(name = "example.testWithDependency")
    public List<Long> testWithDependency() {
        return newArrayList(1L, 42L);
    }
}
