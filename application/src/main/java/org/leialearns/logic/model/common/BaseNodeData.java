package org.leialearns.logic.model.common;

import org.leialearns.logic.model.Version;
import org.leialearns.logic.structure.Node;
import org.leialearns.utilities.Setting;

import java.util.function.Supplier;

import static org.leialearns.utilities.Display.displayParts;
import static org.leialearns.utilities.L.literal;

public abstract class BaseNodeData<Type,ItemIterable> implements NodeData<Type,ItemIterable> {
    private final Setting<Version> version = new Setting<>("Version");
    private final Setting<Node> node = new Setting<>("Node");
    private final Setting<Boolean> persistent = new Setting<>("Persistent", version::isFixated);
    private final Setting<String> label = new Setting<>("Label", "?");

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

    public void setLabel(String label) {
        this.label.set(label);
    }

    @Override
    public void set(Version version, Node node) {
        if (persistent.isFixated() && !persistent.get()) {
            throw new IllegalStateException("This histogram is not persistent: " + this);
        }
        this.version.set(version);
        this.node.set(node);
        retrieve(this::getItems);
    }

    public abstract ItemIterable getItems();

    public String getLabel() {
        return label.get();
    }

    public boolean getPersistent() {
        return persistent.get();
    }

    @Override
    public void retrieve(Supplier<ItemIterable> getCounters) {
    }

    protected abstract String getTypeLabel();

    @Override
    public String toString() {
        Version version = (this.version.isFixated() ? this.version.get() : null);
        char versionLabel = (version == null ? '?' : version.getModelType().toChar());
        String versionOrdinal = (version == null ? "" : ":" + version.getOrdinal());
        Object versionLiteral = literal(versionLabel + versionOrdinal);
        Object nodeLiteral = literal(node.isFixated() ? node.get().toString() : "?");
        return displayParts(getTypeLabel(), versionLiteral, nodeLiteral, literal(label.get()));
    }

}
