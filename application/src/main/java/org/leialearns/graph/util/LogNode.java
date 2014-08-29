package org.leialearns.graph.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.SortedSet;
import java.util.TreeSet;

import static org.leialearns.utilities.Static.getLoggingClass;

public class LogNode implements Comparable<LogNode> {
    private final Logger logger = LoggerFactory.getLogger(getLoggingClass(this));
    private final SortedSet<LogNode> children = new TreeSet<>();
    private final String label;

    public LogNode(String label) {
        if (label == null) {
            throw new IllegalArgumentException("The label should not be null");
        }
        this.label = label;
    }

    public void add(LogNode child) {
        children.add(child);
    }

    public void log(String indent) {
        if (logger.isDebugEnabled()) {
            logger.debug("{}{}", indent, label);
            String subIndent = indent + ". ";
            for (LogNode child : children) {
                child.log(subIndent);
            }
        }
    }

    @Override
    public int compareTo(LogNode other) {
        return this.label.compareTo(other.label);
    }
}
