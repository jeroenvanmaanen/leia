package org.leialearns.command.encounter;

import org.leialearns.api.enumerations.AccessMode;
import org.leialearns.api.enumerations.Direction;
import org.leialearns.api.enumerations.ModelType;
import org.leialearns.api.interaction.Alphabet;
import org.leialearns.api.interaction.DirectedSymbol;
import org.leialearns.api.interaction.InteractionContext;
import org.leialearns.api.interaction.Symbol;
import org.leialearns.api.model.Counted;
import org.leialearns.api.model.Version;
import org.leialearns.api.model.histogram.Counter;
import org.leialearns.api.session.Root;
import org.leialearns.api.session.Session;
import org.leialearns.api.structure.Node;
import org.leialearns.api.structure.Structure;
import org.leialearns.common.ExceptionWrapper;
import org.leialearns.common.Setting;
import org.leialearns.common.TypedIterable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;

import static org.leialearns.common.Display.display;
import static org.leialearns.common.Display.show;
import static org.leialearns.common.Static.getLoggingClass;

/**
 * Connects the <b>LEIA</b> system to its environment by feeding it tokens from a file. This encounter
 * uses a trivial implementation of the active part of <b>LEIA</b> with a single symbol: '.', that means
 * 'no action'.
 */
public class Encounter implements org.leialearns.api.command.Encounter {
    private final Logger logger = LoggerFactory.getLogger(getLoggingClass(this));
    private final Setting<Boolean> limitFlag = new Setting<>("Limit flag", false);
    private final Setting<StreamAdapter> streamAdapter = new Setting<>("Stream adapter");
    private final Setting<String> interactionContextUri = new Setting<>("Interaction context URI");
    private final Setting<Integer> autoExtendLimit = new Setting<>("Auto extend limit", -1);
    private Version lastVersion = null;

    @Autowired
    private Root root;

    /**
     * Sets the flag that indicates whether the encounter should stop after a fixed number of symbols.
     * @param limitFlag The limit flag
     */
    public void setLimitFlag(boolean limitFlag) {
        this.limitFlag.set(limitFlag);
    }

    /**
     * Sets the stream adapter that provides the symbols for this encounter.
     * @param streamAdapter The stream adapter
     */
    public void setStreamAdapter(StreamAdapter streamAdapter) {
        this.streamAdapter.set(streamAdapter);
    }

    /**
     * Sets the URI to use for the interaction context. If there is already an interaction context with this URI
     * in the data source, then is will be re-used and extended. If not, a new interaction context will be created.
     * @param interactionContextUri The URI of the interaction context to use
     */
    public void setInteractionContextUri(String interactionContextUri) {
        this.interactionContextUri.set(interactionContextUri);
    }

    /**
     * Creates a new session for this encounter.
     * @return A new session for this encounter
     */
    protected Session createSession() {
        return root.createSession(interactionContextUri.get());
    }

    /**
     * Sets the maximum depth of nodes that will be marked extensible.
     * (see {@link org.leialearns.api.structure.Node#getExtensible()}) by the encounter (rather than relying on the
     * optimizer to manage the extensibility of nodes).
     * @param autoExtendLimit The maximum depth of nodes that will be marked extensible
     */
    public void setAutoExtendLimit(int autoExtendLimit) {
        this.autoExtendLimit.set(autoExtendLimit);
    }

    /**
     * Returns the last {@link org.leialearns.api.enumerations.ModelType#COUNTED} version that was created by this encounter.
     * @return the last version that was created by this encounter
     */
    @Override
    public Version getLastVersion() {
        return lastVersion;
    }

    /**
     * Feeds the given files to the <b>LEIA</b> system.
     * @param args The paths of the files to feed
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void command(String... args) {
        StringBuilder builder = new StringBuilder("Args");
        for (String arg : args) {
            builder.append(": ");
            builder.append(arg);
        }
        logger.info(builder.toString());

        Session session = createSession();
        InteractionContext interactionContext = session.getInteractionContext();
        Structure structure = interactionContext.getStructure();
        logger.debug("Structure: {}", structure);
        logger.debug("Interaction context: {}", interactionContext);
        Alphabet actions = interactionContext.getActions();
        logger.debug("Actions: {}", actions);
        Symbol nonAction = actions.internalize(".");
        logger.debug("Non-action: {}", nonAction);

        StreamAdapter adapter = streamAdapter.get();
        int a = 0;
        if (a + 1 < args.length && "-s".equals(args[a])) {
            adapter.setSkip(Long.parseLong(args[a + 1]));
            a += 2;
        }
        if (a + 1 < args.length && "-l".equals(args[a])) {
            adapter.setLimit(Long.parseLong(args[a + 1]));
            a += 2;
        }
        if (a < args.length && "--".equals(args[a])) {
            a++;
        }
        if (a > 0) {
            int l = args.length - a;
            String[] newArgs = new String[l];
            System.arraycopy(args, a, newArgs, 0, l);
            args = newArgs;
        }

        for (String sourceLocation : args) {
            Version version = session.createVersion(ModelType.COUNTED);
            version.setAccessMode(AccessMode.WRITABLE, session);
            Counted countedVersion = version.createCountedVersion();
            Deque<DirectedSymbol> state = new ArrayDeque<>();

            try {
                adapter.setInputStream(new URL(sourceLocation).openStream());
            } catch (IOException exception) {
                throw ExceptionWrapper.wrap(exception);
            }
            int i = 0;
            Alphabet responses = interactionContext.getResponses();
            while (adapter.hasNext()) {
                String token = adapter.next();
                logger.info("Token: " + show(token));
                Symbol symbol = responses.internalize(token);
                logger.debug("Symbol: " + symbol.toString(Direction.RESPONSE) + ": " + symbol.descriptionLength());
                DirectedSymbol directedSymbol = symbol.createDirectedSymbol(Direction.RESPONSE);
                logger.debug("Directed symbol: [" + directedSymbol + "]");

                logger.trace("State: " + display(state));
                Node node = structure.findOrCreateNode(nonAction, new TypedIterable<>(state, DirectedSymbol.class));
                if (node.getDepth() < autoExtendLimit.get()) {
                    logger.debug("Node structure: [" + node.getStructure() + "]");
                    structure.markExtensible(node);
                    logger.debug("Marked extensible: {}", node);
                }
                logger.debug("Node: [" + node + "]");
                Counter counter = countedVersion.getCounter(node, symbol);
                counter.increment();

                state.offerFirst(directedSymbol);
                logger.debug("Structure max depth: " + structure.getMaxDepth());
                while (state.size() > structure.getMaxDepth() + 1) {
                    state.pollLast();
                }
                if (logger.isDebugEnabled()) {
                    logger.debug("State: [" + stateToString(state) + "]");
                }
                if (limitFlag.get() && ++i > 1000) {
                    break;
                }
            }
            logger.info("Finished source location: " + sourceLocation);

            try {
                version.waitForLock(session);
            } catch (InterruptedException exception) {
                throw ExceptionWrapper.wrap(exception);
            }
            version.setAccessMode(AccessMode.READABLE, session);

            lastVersion = version;
        }
    }

    /**
     * Returns a string representation of the given state
     * @param state The state to represent
     * @return The representation of the given state
     */
    public String stateToString(Collection<DirectedSymbol> state) {
        StringBuilder builder = new StringBuilder("{");
        boolean first = true;
        for (DirectedSymbol symbol : state) {
            if (first) {
                first = false;
            } else {
                builder.append(",");
            }
            builder.append(' ');
            builder.append(symbol.toString());
        }
        if (!first) {
            builder.append(' ');
        }
        builder.append("}");
        return builder.toString();
    }

}
