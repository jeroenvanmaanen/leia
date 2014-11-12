package org.leialearns.logic.model.common;

import org.leialearns.api.model.Version;
import org.leialearns.api.model.common.NodeData;
import org.leialearns.api.model.common.NodeDataProxy;
import org.leialearns.api.structure.Node;
import org.leialearns.utilities.Setting;

import java.util.function.BiFunction;

import static org.leialearns.utilities.Display.displayParts;
import static org.leialearns.utilities.L.literal;

public class NodeDataProxyImpl<Type extends NodeData<ItemType>,ItemType> implements NodeDataProxy<Type> {
    private final Setting<Version> version = new Setting<>("Version");
    private final Setting<Node> node = new Setting<>("Node");
    private final Setting<Boolean> persistent = new Setting<>("Persistent", version::isFixated);
    private final Setting<Type> data = new Setting<>("Data", () -> { throw new IllegalStateException(); });
    private final Setting<BiFunction<Version,Node,Iterable<ItemType>>> itemsGetter = new Setting<>("Items getter", () -> { throw new IllegalStateException(); });

    @Override
    public void setNode(Node node) {
        if (node != null) {
            this.node.set(node);
        }
    }

    @Override
    public Node getNode() {
        return node.isFixated() ? node.get() : null;
    }

    @Override
    public Version getVersion() {
        return version.isFixated() ? version.get() : null;
    }

    @Override
    public void set(Version version, Node node) {
        if (persistent.isFixated() && !persistent.get()) {
            throw new IllegalStateException("This histogram is not persistent: " + this);
        }
        this.version.set(version);
        this.node.set(node);
        this.data.get().retrieve(this::getItems);
    }

    @Override
    public void setData(Type data) {
        this.data.set(data);
    }

    @Override
    public Type getData() {
        return data.get();
    }

    public void setItemsGetter(BiFunction<Version,Node,Iterable<ItemType>> itemsGetter) {
        this.itemsGetter.set(itemsGetter);
    }

    public Iterable<ItemType> getItems() {
        return itemsGetter.get().apply(getVersion(), getNode());
    }

    public boolean getPersistent() {
        return persistent.get();
    }

    @Override
    public String toString() {
        Type data = (this.data.isFixated() ? getData() : null);
        String typeLabel = (data == null ? "?Type?" : data.getTypeLabel());
        Version version = (this.version.isFixated() ? this.version.get() : null);
        char versionLabel = (version == null ? '?' : version.getModelType().toChar());
        String versionOrdinal = (version == null ? "" : ":" + version.getOrdinal());
        Object versionLiteral = literal(versionLabel + versionOrdinal);
        Object nodeLiteral = literal(node.isFixated() ? node.get().toString() : "?");
        Object dataLiteral = literal(data == null ? "?" : data.toString());
        return displayParts(typeLabel, versionLiteral, nodeLiteral, dataLiteral);
    }

}
