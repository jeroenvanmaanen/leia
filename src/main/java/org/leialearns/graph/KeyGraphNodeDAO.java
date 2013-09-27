package org.leialearns.graph;

import org.leialearns.bridge.FarObject;
import org.leialearns.utilities.Expression;
import org.leialearns.utilities.Setting;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.UniqueFactory;

import java.util.Map;

public class KeyGraphNodeDAO<DTO extends HasId & FarObject<?>> extends IdDaoSupport<DTO> {
    private final String keyProperty;
    private final Setting<UniqueFactory<Node>> uniqueFactory;

    public KeyGraphNodeDAO(final String indexName, String keyPropertyName) {
        this.keyProperty = keyPropertyName;
        uniqueFactory = new Setting<UniqueFactory<Node>>("Unique factory for " + indexName, new Expression<UniqueFactory<Node>>() {
            public UniqueFactory<Node> get() {
                return new UniqueFactory.UniqueNodeFactory(graphDatabaseService, indexName) {
                    protected void initialize(Node created, Map<String, Object> properties) {
                        created.setProperty(keyProperty, properties.get(keyProperty));
                    }
                };
            }
        });
    }

    public Node getOrCreate(Object keyValue) {
        return uniqueFactory.get().getOrCreate(keyProperty, keyValue);
    }

}
