package org.leialearns.graph.util;

import com.google.common.base.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.SortedSet;
import java.util.TreeSet;

import static org.leialearns.utilities.Static.getLoggingClass;

public class LogNode<T extends Comparable<T>> implements Comparable<LogNode<T>> {
    private final Logger logger = LoggerFactory.getLogger(getLoggingClass(this));
    private final SortedSet<LogNode> children = new TreeSet<>();
    private final T object;
    private final String label;

    public LogNode(T object, Function<T,String> getLabel) {
        if (object == null) {
            throw new IllegalArgumentException("The object should not be null");
        }
        this.object = object;
        label = getLabel.apply(object);
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
    public int compareTo(LogNode<T> other) {
        return this.object.compareTo(other.object);
    }
}
