package org.leialearns.logic.model;

import org.leialearns.logic.interaction.Symbol;
import org.leialearns.logic.session.Root;
import org.leialearns.logic.utilities.DescriptionLength;
import org.leialearns.utilities.Expression;
import org.leialearns.utilities.Setting;
import org.leialearns.utilities.TypedIterable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import static org.leialearns.utilities.Static.getLoggingClass;

public class ExpectationObject extends BaseDistribution implements Expectation {
    private final Logger logger = LoggerFactory.getLogger(getLoggingClass(this));
    private Setting<Fraction> zero = new Setting<>("Zero", new Expression<Fraction>() {
        @Override
        public Fraction get() {
            return root.createTransientFraction(-1, 0, 1);
        }
    });
    Map<Symbol,Fraction> fractions = new HashMap<>();

    private Root root;

    public ExpectationObject(Root root) {
        this.root = root;
    }

    public void setFractions(Map<Symbol,Fraction> fractions) {
        this.fractions = Collections.unmodifiableMap(fractions);
    }

    public TypedIterable<Symbol> getSymbols() {
        return new TypedIterable<>(fractions.keySet(), Symbol.class);
    }

    @Override
    public void retrieve() {
        Estimate.Iterable estimates = getVersion().findEstimates(getNode());
        fractions.clear();
        for (Estimate estimate : estimates) {
            fractions.put(estimate.getSymbol(), estimate.getFraction());
        }
    }

    public Fraction getFraction(Symbol symbol) {
        Fraction result = null;
        if (fractions.containsKey(symbol)) {
            result = fractions.get(symbol);
        }
        if (result == null) {
            result = zero.get();
        }
        return result;
    }

    public String prefixEncode(Collection<Symbol> symbols) {
        int missing = 0;
        StringBuilder builder = new StringBuilder("{E|");
        builder.append("#sym:");
        builder.append(DescriptionLength.prefixEncode(BigInteger.valueOf(symbols.size())));
        for (Symbol symbol : symbols) {
            builder.append("|s#");
            builder.append(symbol.getOrdinal());
            builder.append(":");
            if (fractions.containsKey(symbol)) {
                Fraction fraction = fractions.get(symbol);
                long index = fraction.getIndex();
                index = index < 1 ? 1 : index + 1;
                builder.append(DescriptionLength.prefixEncode(BigInteger.valueOf(index)));
            } else {
                builder.append("I(0)");
                missing++;
            }
        }
        builder.append("}");
        if (symbols.size() + missing != fractions.size()) {
            logger.warn("#symbols + #missing = {} + {} != {} = #fractions", new Object[]{symbols.size(), missing, fractions.size()});
        }
        return builder.toString();
    }

    public long descriptionLength(Collection<Symbol> symbols) {
        long result = 0L;
        result += DescriptionLength.descriptionLength(BigInteger.valueOf(symbols.size()));
        for (Symbol symbol : symbols) {
            if (fractions.containsKey(symbol)) {
                Fraction fraction = fractions.get(symbol);
                long index = fraction.getIndex();
                index = index < 1 ? 1 : index + 1;
                result += DescriptionLength.descriptionLength(BigInteger.valueOf(index));
            } else {
                result++;
            }
        }
        return result;
    }

    public void log() {
        log(null);
    }

    public void log(String label) {
        if (logger.isDebugEnabled()) {
            Collection<Symbol> symbols = new TreeSet<>();
            for (Symbol symbol : getSymbols()) {
                symbols.add(symbol);
            }
            logger.debug("Expectation: {");
            for (Symbol symbol : symbols) {
                logger.debug("  " + symbol + ": " + getFraction(symbol));
            }
            logger.debug("}");
        }
    }

    public String getTypeLabel() {
        return "Expectation";
    }

}
