package org.leialearns.logic.model.expectation;

import org.leialearns.api.interaction.Symbol;
import org.leialearns.api.model.expectation.Estimate;
import org.leialearns.api.model.expectation.Expectation;
import org.leialearns.api.model.expectation.Fraction;
import org.leialearns.api.session.Root;
import org.leialearns.common.Setting;
import org.leialearns.common.TypedIterable;
import org.leialearns.logic.prefixfree.DescriptionLength;
import org.leialearns.logic.utilities.PrefixFreeBigInt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;
import java.util.function.Supplier;

import static org.leialearns.common.Static.getLoggingClass;

public class ExpectationObject implements Expectation {
    private final Logger logger = LoggerFactory.getLogger(getLoggingClass(this));
    private final Setting<Fraction> zero;
    private Map<Symbol,Fraction> fractions = new HashMap<>();
    private Supplier<String> locationSupplier = null;

    public ExpectationObject(Root root) {
        this.zero = new Setting<>("Zero", () -> root.createTransientFraction(-1, 0, 1));
    }

    public void setFractions(Map<Symbol,Fraction> fractions) {
        this.fractions = Collections.unmodifiableMap(fractions);
    }


    @Override
    public void setLocation(Supplier<String> locationSupplier) {
        this.locationSupplier = locationSupplier;
    }

    public TypedIterable<Symbol> getSymbols() {
        return new TypedIterable<>(fractions.keySet(), Symbol.class);
    }

    @Override
    public void retrieve(Supplier<Iterable<Estimate>> getItems) {
        logger.debug("Retrieve estimates for: {}", locationSupplier.get());
        Iterable<Estimate> estimates = getItems.get();
        Fraction sum = zero.get();
        fractions.clear();
        for (Estimate estimate : estimates) {
            Fraction fraction = estimate.getFraction();
            fractions.put(estimate.getSymbol(), fraction);
            if (logger.isTraceEnabled()) {
                sum = sum.add(fraction);
            }
        }
        if (logger.isTraceEnabled()) {
            logger.trace("Sum: {}", sum);
        }
    }

    @Override
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

    @Override
    public String prefixEncode(Collection<Symbol> symbols) {
        int missing = 0;
        StringBuilder builder = new StringBuilder("{E|");
        builder.append("#sym:");
        builder.append(PrefixFreeBigInt.prefixEncode(BigInteger.valueOf(symbols.size())));
        for (Symbol symbol : symbols) {
            builder.append("|s#");
            builder.append(symbol.getOrdinal());
            builder.append(":");
            if (fractions.containsKey(symbol)) {
                Fraction fraction = fractions.get(symbol);
                long index = fraction.getIndex();
                index = index < 1 ? 1 : index + 1;
                builder.append(PrefixFreeBigInt.prefixEncode(BigInteger.valueOf(index)));
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

    @Override
    public long descriptionLength(Iterable<Symbol> symbols) {
        long result = 0L;
        int size = 0;
        for (Symbol symbol : symbols) {
            size++;
            if (fractions.containsKey(symbol)) {
                Fraction fraction = fractions.get(symbol);
                long index = fraction.getIndex();
                index = index < 1 ? 1 : index + 1;
                result += DescriptionLength.descriptionLength(BigInteger.valueOf(index));
            } else {
                result++;
            }
        }
        result += DescriptionLength.descriptionLength(BigInteger.valueOf(size));
        return result;
    }

    @Override
    public void log() {
        log(null);
    }

    @Override
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

    @Override
    public String getTypeLabel() {
        return "Expectation";
    }

}
