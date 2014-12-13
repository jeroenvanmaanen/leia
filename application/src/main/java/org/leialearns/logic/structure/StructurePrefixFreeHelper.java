package org.leialearns.logic.structure;

import org.leialearns.api.common.PrefixDecoder;
import org.leialearns.api.common.PrefixEncoder;
import org.leialearns.api.enumerations.Direction;
import org.leialearns.api.interaction.Alphabet;
import org.leialearns.api.interaction.InteractionContext;
import org.leialearns.api.interaction.Symbol;
import org.leialearns.api.structure.Node;
import org.leialearns.api.structure.Structure;
import org.leialearns.bridge.BridgeOverride;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.BiFunction;

import static org.leialearns.common.Static.asList;

@Component
public class StructurePrefixFreeHelper {
    private static final Logger logger = LoggerFactory.getLogger(new Object(){}.getClass().getEnclosingClass());

    @BridgeOverride
    public void prefixEncode(Structure structure, PrefixEncoder encoder) {
        encoder.appendComment("{Structure{\n");
        encoder.append(structure.getURI());
        List<Node> rootNodes = asList(structure.findRootNodes());
        encoder.append(rootNodes.size());
        for (Node rootNode : rootNodes) {
            prefixEncode(rootNode, encoder);
        }
        encoder.appendComment("}Structure}\n");
    }

    protected void prefixEncode(Node node, PrefixEncoder encoder) {
        encoder.appendComment("{Node{\n");
        Symbol symbol = node.getSymbol();
        encoder.append(symbol.getOrdinal());
        encoder.appendOriginal(symbol.getDenotation());
        encoder.appendComment("\n");
        encoder.append(node.getDirection());

        encoder.append(node.getExtensible());

        List<Node> children = asList(node.findChildren());
        encoder.appendComment("|Children|\n");
        encoder.append(children.size());
        for (Node child : children) {
            prefixEncode(child, encoder);
        }
        encoder.appendComment("}Node}\n");
    }

    // Make sure to set the interaction context as a helper of the decoder first.
    @BridgeOverride
    public void prefixDecode(Structure structure, PrefixDecoder decoder) {
        decoder.nextString(); // URI
        int numberOfRootNodes = decoder.nextInt();
        InteractionContext context = decoder.getHelper(InteractionContext.class);
        if (context == null) {
            throw new IllegalStateException("No interaction context helper found");
        }
        Alphabet actions = context.getActions();
        Alphabet responses = context.getResponses();
        for (int i = 0; i < numberOfRootNodes; i++) {
            prefixDecodeNode(structure::findOrCreateNode, decoder, structure, actions, responses);
        }
    }

    protected void prefixDecodeNode(BiFunction<Symbol,Direction,Node> nodeCreator, PrefixDecoder decoder, Structure structure, Alphabet actions, Alphabet responses) {
        long ordinal = decoder.nextLong();
        Direction direction = decoder.nextEnum(Direction.class);
        Symbol symbol = getSymbol(ordinal, direction, actions, responses);
        Node node = nodeCreator.apply(symbol, direction);

        if (decoder.nextBoolean()) {
            structure.markExtensible(node);
        }

        int numberOfChildren = decoder.nextInt();
        for ( ; numberOfChildren > 0; numberOfChildren--) {
            prefixDecodeNode(node::findOrCreate, decoder, structure, actions, responses);
        }
    }

    protected Symbol getSymbol(long ordinal, Direction direction, Alphabet actions, Alphabet responses) {
        Symbol symbol;
        switch (direction) {
            case ACTION:
                symbol = actions.getSymbol(ordinal);
                break;
            case RESPONSE:
                symbol = responses.getSymbol(ordinal);
                break;
            default:
                throw new RuntimeException("Unknown direction: " + direction);
        }
        if (logger.isTraceEnabled()) {
            logger.trace(String.format("Get symbol: %s: %s: %s", ordinal, direction, symbol));
        }
        return symbol;
    }
}
