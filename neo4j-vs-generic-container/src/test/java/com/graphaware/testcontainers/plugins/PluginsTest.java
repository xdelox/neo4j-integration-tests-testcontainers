package com.graphaware.testcontainers.plugins;

public interface PluginsTest {
    void load_neo4jlabs_plugins();
    void load_generic_plugin_fat_jar();
    void load_generic_plugin_without_dependencies();
}
