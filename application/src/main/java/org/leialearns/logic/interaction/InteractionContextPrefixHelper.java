package org.leialearns.logic.interaction;

import org.leialearns.api.common.PrefixDecoder;
import org.leialearns.api.common.PrefixEncoder;
import org.leialearns.api.interaction.InteractionContext;
import org.leialearns.bridge.BridgeOverride;
import org.springframework.stereotype.Component;

@Component
public class InteractionContextPrefixHelper {

    @BridgeOverride
    public void prefixEncode(InteractionContext interactionContext, PrefixEncoder encoder) {
        encoder.appendComment("{InteractionContext{\n");
        encoder.append(interactionContext.getURI());
        encoder.appendComment("|Actions|\n");
        interactionContext.getActions().prefixEncode(encoder);
        encoder.appendComment("|Responses|\n");
        interactionContext.getResponses().prefixEncode(encoder);
        encoder.appendComment("|Structure|\n");
        interactionContext.getStructure().prefixEncode(encoder);
        encoder.appendComment("}InteractionContext}\n");
    }

    @BridgeOverride
    public void prefixDecode(InteractionContext interactionContext, PrefixDecoder decoder) {
        decoder.nextString(); // URI
        interactionContext.getActions().prefixDecode(decoder);
        interactionContext.getResponses().prefixDecode(decoder);
        decoder.addHelper(interactionContext, InteractionContext.class);
        interactionContext.getStructure().prefixDecode(decoder);
    }
}
