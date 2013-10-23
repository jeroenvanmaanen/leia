package org.leialearns.command.api;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.leialearns.enumerations.AccessMode;
import org.leialearns.logic.model.Counted;
import org.leialearns.logic.model.ExpectedModel;
import org.leialearns.enumerations.ModelType;
import org.leialearns.logic.model.Observed;
import org.leialearns.logic.model.Toggled;
import org.leialearns.logic.model.TypedVersionExtension;
import org.leialearns.logic.model.Version;
import org.leialearns.logic.session.Root;
import org.leialearns.logic.session.Session;
import org.leialearns.logic.structure.Node;
import org.leialearns.logic.structure.Structure;
import org.leialearns.utilities.ExceptionWrapper;
import org.leialearns.utilities.ExecutionListener;
import org.leialearns.utilities.Expression;
import org.leialearns.bridge.NearIterable;
import org.leialearns.utilities.GraphDumper;
import org.leialearns.utilities.Setting;
import org.leialearns.utilities.TestUtilities;
import org.leialearns.utilities.TransactionHelper;
import org.leialearns.utilities.TypedIterable;
import org.neo4j.graphdb.GraphDatabaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.leialearns.utilities.Static.getLoggingClass;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/ApplicationContext.xml","/org/leialearns/AppTest-context.xml"})
@TestExecutionListeners(value = {DependencyInjectionTestExecutionListener.class, ExecutionListener.class})
public class ObserverTest {
    private final Logger logger = LoggerFactory.getLogger(getLoggingClass(this));
    private final Fan MISSING_FAN = new Fan(false, new ArrayList<Node>());
    private final Setting<String> interactionContextUri = new Setting<String>("Interaction context URI");
    private final Setting<Session> session = new Setting<Session>("Session", new Expression<Session>() {
        @Override
        public Session get() {
            return createSession();
        }
    });

    @Autowired
    private TransactionHelper transactionHelper;

    @Autowired
    private Observer observer;

    @Autowired
    private Root root;

    @Autowired
    private GraphDumper graphDumper;

    @Autowired
    public void setInteractionContextUri(String interactionContextUri) {
        this.interactionContextUri.set(interactionContextUri);
    }

    public Session createSession() {
        return root.createSession(interactionContextUri.get());
    }

    public Session getSession() {
        return session.get();
    }

    @BeforeClass
    public static void beforeClass() throws IOException {
        TestUtilities.beforeClass(null);
    }

    @Test
    public void testObserver() {
        logger.info("Start test");
        final Setting<Session> session = new Setting<Session>("Session");
        final Counted[] counted = new Counted[3];
        final Toggled[] toggled = new Toggled[3];
        final Collection<TypedVersionExtension> registry = new ArrayList<TypedVersionExtension>();
        try {
            transactionHelper.runInTransaction(new Runnable() {
                @Override
                public void run() {
                    session.set(getSession());
                    // Setup versions
                    // The pattern is to create three new versions such that the first and the last are READABLE,
                    // but the second is only set to readable in the 'finally' section.
                    // Therefore only the versions up and until the first new version can be consolidated
                    // as the second can still change.
                    setupCounted(counted, registry);
                    Node toggleNode = getToggleNode();
                    setupToggled(toggled, registry, toggleNode);

                    // Run the observer
                    observer.command();

                    Observed lastCreated = observer.getLastCreated();
                    assertNotNull(lastCreated);
                    Version lastCreatedVersion = lastCreated.getVersion();
                    assertEquals(ModelType.OBSERVED, lastCreated.getModelType());
                    if (lastCreatedVersion.getAccessMode() != AccessMode.EXCLUDE) {
                        logger.debug("Last created version: [" + lastCreatedVersion + "]");
                        assertEquals(AccessMode.READABLE, lastCreatedVersion.getAccessMode());
                        Toggled lastAttachedToggled = lastCreated.getToggled();
                        logger.debug("Toggled attached to last created: [" + lastAttachedToggled + "]");
                        assertEquals(counted[0], lastCreated.getCounted());
                        assertEquals(lastAttachedToggled, toggled[0]);
                    } else {
                        logger.debug("Not readable: [" + lastCreatedVersion + "]");
                    }
                    Observed observedVersion = lastCreatedVersion.createObservedVersion();
                    assertEquals(ModelType.OBSERVED, observedVersion.getModelType());
                    observedVersion.logCounters();
                    if (toggleNode != null) {
                        observedVersion.logCounters(toggleNode.getParent());
                    }
                    lastCreated.check();
                }
            });
        } catch (Throwable exception) {
            logger.error("Exception in observer command", exception);
            // Save the exception for rethrowing outside the transaction
            Observed lastCreated = observer.getLastCreated();
            if (lastCreated != null) {
                // Mark the version to be excluded instead of a roll back of the transaction
                lastCreated.getVersion().setAccessMode(AccessMode.EXCLUDE, session.get());
            }
            throw ExceptionWrapper.wrap(exception);
        } finally {
            transactionHelper.runInTransaction(new Runnable() {
                @Override
                public void run() {
                    Session theSession = session.get().refresh();
                    for (TypedVersionExtension extension : registry) {
                        Version version = extension.getVersion();
                        logger.debug("Extension: {}: version: {}", extension, version);
                        Version attached = theSession.findVersion(version.getOrdinal());
                        if (attached != null) {
                            attached.setAccessMode(AccessMode.READABLE, theSession);
                        }
                    }
                }
            });
        }
        graphDumper.dumpGraph();
    }

    protected void setupCounted(Counted[] counted, Collection<TypedVersionExtension> registry) {
        // Three new COUNTED versions
        Session session = getSession();
        logger.debug("Session: " + session);
        counted[0] = register(registry, session.createCountedVersion());
        setReadable(counted[0]);
        logger.debug("Counted version 0: [" + counted[0] + "]");

        counted[1] = register(registry, session.createCountedVersion());
        logger.debug("Counted version 1: [" + counted[1] + "]");

        counted[2] = register(registry, session.createCountedVersion());
        setReadable(counted[2]);
        logger.debug("Counted version 2: [" + counted[2] + "]");
    }

    protected int getCovered(ExpectedModel expectedModel, Node node) {
        int result = 0;
        for (Node child : node.findChildren()) {
            boolean childIncluded = expectedModel.isIncluded(child, getSession());
            logger.trace("Is included: " + child + ": " + childIncluded);
            if (childIncluded) {
                result |= 0x06;
            } else {
                if ((result & 0x01) == 0) {
                    result |= 0x01;
                } else {
                    result |= 0x02;
                }
            }
            if (result == 0x07) {
                break;
            }
            result |= getCovered(expectedModel, child);
            if (result == 0x07) {
                break;
            }
        }
        logger.trace("Get covered: " + node + ": 0x0" + Integer.toHexString(result));
        return result;
    }

    protected Node addToStump(Node oldNode, Node newNode, List<Node> padding) {
        Node result;
        if (oldNode == null) {
            result = newNode;
        } else {
            result = oldNode;
            padding.add(newNode);
            logger.trace("Add to padding: " + newNode);
        }
        return result;
    }

    protected boolean addToResult(List<Node> result, Node node, Iterator<Node> padding) {
        boolean flag = true;
        if (node == null) {
            if (padding.hasNext()) {
                result.add(padding.next());
            } else {
                flag = false;
            }
        } else {
            result.add(node);
        }
        return flag;
    }

    protected Fan getStumps(ExpectedModel expectedModel, Node parent) {
        Node excludedBare = null;
        Node excludedCovered = null;
        Node includedBare = null;
        Node includedCovered = null;
        List<Node> padding = new ArrayList<Node>();
        boolean resultComplete = false;
        for (Node child : parent.findChildren()) {
            int covered = getCovered(expectedModel, child);
            if ((covered & 0x03) != 0x03) {
                continue;
            }
            if (expectedModel.isIncluded(child, getSession())) {
                if ((covered & 0x04) == 0x04) {
                    includedCovered = addToStump(includedCovered, child, padding);
                } else {
                    includedBare = addToStump(includedBare, child, padding);
                }
            } else {
                if ((covered & 0x04) == 0x04) {
                    excludedCovered = addToStump(excludedCovered, child, padding);
                } else {
                    excludedBare = addToStump(excludedBare, child, padding);
                }
            }
            resultComplete = includedBare != null && includedCovered != null && excludedBare != null && excludedCovered != null;
            if (resultComplete) {
                break;
            }
        }
        List<Node> result = new ArrayList<Node>();
        logger.trace("Padding size: " + padding.size());
        Iterator<Node> paddingIterator = padding.iterator();
        do {
            if (!addToResult(result, excludedBare, paddingIterator)) break;
            if (!addToResult(result, excludedCovered, paddingIterator)) break;
            if (!addToResult(result, includedBare, paddingIterator)) break;
            if (!addToResult(result, includedCovered, paddingIterator)) break;
        } while (false);
        logger.trace("Result: " + parent + ": " + result.size() + ": " + resultComplete);
        return result.size() == 4 ? new Fan(resultComplete, result) : MISSING_FAN;
    }

    protected static <T> void append(List<T> list, Iterable<T> extra) {
        for (T item : extra) {
            list.add(item);
        }
    }

    protected Node getToggleNode() {
        Structure structure = getSession().getInteractionContext().getStructure();

        // Select a node to toggle
        NearIterable<Node> nodeIterable = structure.findNodes();
        logger.debug("Node iterable is empty?: " + nodeIterable.isEmpty());

        Map<Integer, int[]> stats = new TreeMap<Integer, int[]>();
        Node toggleNode = null;
        for (Node node : nodeIterable) {
            if (toggleNode == null && node.getDepth() > 2) {
                toggleNode = node;
                break;
            }
            int depth = node.getDepth();
            int[] count;
            if (stats.containsKey(depth)) {
                count = stats.get(depth);
            } else {
                count = new int[] {0};
                stats.put(depth, count);
            }
            count[0]++;
        }
        for (Map.Entry<Integer, int[]> entry : stats.entrySet()) {
            logger.debug("Stats: " + entry.getKey() + " -> " + entry.getValue()[0]);
        }

        //assertNotNull(toggleNode);
        logger.debug("Toggle node: [" + toggleNode + "]");
        return toggleNode;
    }

    protected void countChildren(ExpectedModel expectedModel, Node node, int[] counters) {
        for (Node child : node.findChildren()) {
            counters[0] += 1;
            if (expectedModel.isIncluded(child, getSession())) {
                counters[1] += 1;
            }
            countChildren(expectedModel, child, counters);
        }
    }

    protected void setupToggled(Toggled[] toggled, Collection<TypedVersionExtension> registry, Node toggleNode) {
        Session session = getSession();

        Observed observed = observer.getLastCreated();
        Version observedVersion = (observed == null ? null : observed.getVersion());
        Version lastReadableToggled = session.findLastVersion(ModelType.TOGGLED, AccessMode.READABLE);
        Version toggledVersion = (lastReadableToggled == null ? null : lastReadableToggled.findRangeMax(observedVersion, ModelType.TOGGLED, AccessMode.READABLE));
        ExpectedModel expectedModel;
        if (toggledVersion != null) {
            expectedModel = toggledVersion.findToggledVersion();
        } else if (observed != null) {
            expectedModel = observed.getExpectedModel();
        } else {
            logger.info("No last observed");
            expectedModel = ExpectedModel.EMPTY;
        }
        logger.info("Expected model: " + expectedModel);

        if (toggleNode != null) {
            Fan nodeSet = getNodeSet(session, toggleNode, expectedModel);
            createToggledCases(nodeSet, expectedModel, session, registry);

            // Three new toggled versions
            toggled[0] = register(registry, session.createToggledVersion(toggleNode, true));
            setReadable(toggled[0]);
            logger.debug("Toggled node 0: [" + toggled[0].getNode() + "]: [" + toggled[0].getInclude() + "]");
            logger.debug("Toggled version 0: [" + toggled[0] + "]");

            toggled[1] = register(registry, session.createToggledVersion(toggleNode, false));
            logger.debug("Toggled node 1: [" + toggled[0].getNode() + "]: [" + toggled[1].getInclude() + "]");
            logger.debug("Toggled version 1: [" + toggled[1] + "]");

            toggled[2] = register(registry, session.createToggledVersion(toggleNode, false));
            setReadable(toggled[2]);
            logger.debug("Toggled node 2: [" + toggled[0].getNode() + "]: [" + toggled[2].getInclude() + "]");
            logger.debug("Toggled version 2: [" + toggled[2] + "]");

            logNodeSet("after", nodeSet, toggled[2]);
        }
    }

    protected Fan getNodeSet(Session session, Node toggleNode, ExpectedModel expectedModel) {
        Structure structure = session.getInteractionContext().getStructure();

        Fan nodeSet = MISSING_FAN;
        Node topNode = structure.findRootNodes().first();
        for (Node child : topNode.findChildren()) {
            if (child.isPrefixOf(toggleNode)) {
                continue;
            }
            Fan fan = getStumps(expectedModel, child);
            if (nodeSet.isEmpty()) {
                nodeSet = fan;
            }
            if (fan.isComplete()) {
                nodeSet = fan;
                break;
            }
        }
        return nodeSet;
    }

    protected void createToggledCases(Fan nodeSet, ExpectedModel originalExpectedModel, Session session, Collection<TypedVersionExtension> registry) {
        ExpectedModel expectedModel = originalExpectedModel;

        Node common = nodeSet.first();
        if (common != null) {
            common = common.getParent();
        }
        if (common != null && !expectedModel.isIncluded(common, session)) {
            expectedModel = registerReadable(registry, session.createToggledVersion(common, true));
        }

        logNodeSet("before", nodeSet, expectedModel);

        int[] counters;
        int i = 0;
        for (Node node : nodeSet) {
            registerReadable(registry, session.createToggledVersion(node, i < 2));
            if ((i & 0x01) != 0) {
                counters = new int[] { 0, 0 };
                countChildren(expectedModel, node, counters);
                if (counters[1] < 1) {
                    Node child = node.findChildren().first();
                    registerReadable(registry, session.createToggledVersion(child, true));
                }
            }
            i++;
        }
    }

    protected void logNodeSet(String label, Fan nodeSet, ExpectedModel expectedModel) {
        String qualifier = (label == null || label.isEmpty() ? "" : label + ": ");
        logger.info("Node set: " + qualifier + "{");
        for (Node node : nodeSet) {
            StringBuilder builder = new StringBuilder();
            Node n = node;
            while (n != null) {
                builder.append(' ');
                builder.append(expectedModel.isIncluded(n, getSession()) ? '+' : '-');
                n = n.getParent();
            }
            int[] counters = new int[] { 0, 0 };
            countChildren(expectedModel, node, counters);
            builder.append(' ');
            builder.append(counters[0]);
            builder.append(' ');
            builder.append(counters[1]);
            logger.info("  " + (node == null ? "?" : node.toString()) + builder.toString());
        }
        logger.info("}");
    }

    protected <T extends TypedVersionExtension> T register(Collection<TypedVersionExtension> registry, T extension) {
        registry.add(extension);
        return extension;
    }

    protected <T extends TypedVersionExtension> T registerReadable(Collection<TypedVersionExtension> registry, T extension) {
        registry.add(extension);
        Version version = extension.getVersion();
        version.setAccessMode(AccessMode.READABLE, getSession());
        return extension;
    }

    protected void setReadable(TypedVersionExtension versionExtension) {
        Version version;
        if (versionExtension != null && (version = versionExtension.getVersion()) != null && version.getAccessMode() == AccessMode.LOCKED) {
            Session session = getSession();
            logger.debug("Session: " + session);
            version.setAccessMode(AccessMode.READABLE, session);
        }
    }

    protected static class Fan extends TypedIterable<Node> {
        private final boolean complete;

        protected Fan(boolean complete, Iterable<Node> nodes) {
            super(nodes, Node.class);
            this.complete = complete;
        }

        public boolean isComplete() {
            return complete;
        }
    }

}
