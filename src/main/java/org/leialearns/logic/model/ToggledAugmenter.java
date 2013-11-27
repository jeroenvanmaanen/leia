package org.leialearns.logic.model;

import org.leialearns.bridge.BaseBridgeFacet;
import org.leialearns.enumerations.AccessMode;
import org.leialearns.enumerations.ModelType;
import org.leialearns.logic.interaction.InteractionContext;
import org.leialearns.logic.session.Session;
import org.leialearns.logic.structure.Node;
import org.leialearns.utilities.Expression;
import org.leialearns.utilities.Setting;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ToggledAugmenter extends BaseBridgeFacet {
    private Setting<Toggled> toggled = new Setting<>("Toggled", new Expression<Toggled>() {
        @Override
        public Toggled get() {
            Toggled toggled = (Toggled) getBridgeFacets().getNearObject();
            return toggled;
        }
    });
    private Setting<Map<Node, Boolean>> includedFlags = new Setting<>("Included flags", new Expression<Map<Node, Boolean>>() {
        @Override
        public Map<Node, Boolean> get() {
            Toggled thisToggled = toggled.get();
            Version thisToggledVersion = thisToggled.getVersion();
            long minToggled;
            Expected expected = thisToggled.getExpected();
            if (expected == null) {
                minToggled = 0L;
            } else {
                Toggled previousToggled = expected.getToggled();
                minToggled = previousToggled.getVersion().getOrdinal();
            }
            InteractionContext context = thisToggledVersion.getInteractionContext();
            Version.Iterable versions = context.findVersionsInRange(minToggled, thisToggledVersion.getOrdinal(), ModelType.TOGGLED, AccessMode.READABLE);
            Map<Node,Boolean> result = new HashMap<>();
            for (Version toggledVersion : versions) {
                Toggled toggledItem = toggledVersion.findToggledVersion();
                result.put(toggledItem.getNode(), toggledItem.getInclude());
            }
            return Collections.unmodifiableMap(result);
        }
    });

    public boolean isIncluded(Node node) {
        Boolean result = includedFlags.get().get(node);
        if (result == null) {
            Expected expected = toggled.get().getExpected();
            if (expected != null) {
                result = expected.isIncluded(node);
            }
        }
        return result == null ? false : result;
    }

}
