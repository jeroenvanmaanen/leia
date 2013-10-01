package org.leialearns.logic.utilities;

import org.leialearns.logic.interaction.Symbol;
import org.leialearns.logic.model.Counter;
import org.leialearns.logic.model.Expectation;
import org.leialearns.logic.model.Fraction;
import org.leialearns.logic.model.Histogram;
import org.leialearns.logic.session.Root;
import org.leialearns.logic.session.Session;
import org.leialearns.utilities.Expression;
import org.leialearns.utilities.Pair;
import org.leialearns.utilities.Setting;
import org.leialearns.utilities.TypedIterable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// import static org.leialearns.utilities.Display.display;
import static org.leialearns.utilities.Static.getLoggingClass;

public class Oracle {
    private static final Pattern WHITE_SPACE_RE = Pattern.compile("\\s+");
    private static final Pattern ENTRY_RE = Pattern.compile("^[(]([0-9]*),([0-9]*)%([0-9]*)[)]$");
    private final Logger logger = LoggerFactory.getLogger(getLoggingClass(this));
    private final Setting<Approximation> half = new Setting<Approximation>("Fraction half", new Expression<Approximation>() {
        @Override
        public Approximation get() {
            return createApproximation(-1, 1, 2);
        }
    });
    private Setting<Approximation> approximations = new Setting<Approximation>("Approximations", new Expression<Approximation>() {
        @Override
        public Approximation get() {
            return self.getApproximations(null);
        }
    });
    private long largestIndex = 0;
    private String dataUrl = null;

    @Resource(name = "oracle")
    private Oracle self;

    @Autowired
    private Root root;

    public void setData(String dataUrl) {
        this.dataUrl = dataUrl;
    }

    public void reset() {
        Approximation approximations = this.approximations.get();
        approximations.clear();
        self.getApproximations(approximations);
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public Approximation getApproximations(Approximation oldApproximations) {
        Approximation approximations = (oldApproximations == null ? createApproximation(0, 0, 1) : oldApproximations);
        InputStream input;

        Fraction.Iterable fractions = root.findFractions();
        for (Fraction fraction : fractions) {
            logger.trace("Reusing fraction: " + fraction);
            Approximation approximation = createApproximation(fraction);
            // logger.trace("-- Approximation from DB: [" + approximation + "]");
            approximations.insert(approximation);
            largestIndex = Math.max(largestIndex, approximation.getIndex());
        }
        Session session = root.createSession("http://leia.org/oracle");
        session.flush();

        try {
            input = getClass().getResourceAsStream(dataUrl);
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            // Discard header
            reader.readLine();

            String line;
            while ((line = reader.readLine()) != null) {
                // logger.trace("== Line: [" + display(line) + "]");
                line = WHITE_SPACE_RE.matcher(line).replaceAll("");
                Matcher matcher = ENTRY_RE.matcher(line);
                if (matcher.matches()) {
                    long index = Long.parseLong(matcher.group(1));
                    int numerator = Integer.parseInt(matcher.group(2));
                    int denominator = Integer.parseInt(matcher.group(3));
                    Approximation approximation = createApproximation(index, numerator, denominator);
                    // logger.trace("-- Approximation from stream: [" + approximation + "]");
                    approximations.insert(approximation);
                    largestIndex = Math.max(largestIndex, approximation.getIndex());
                } else {
                    logger.warn("Line does not match oracle entry: [" + line + "]");
                }
            }
            if (logger.isTraceEnabled()) {
                logger.trace("Entries in ascending order: {");
                for (Approximation entry : approximations) {
                    logger.trace("-- Approximation: [" + entry + "]");
                }
                logger.trace("}");
            }
        } catch (Throwable throwable) {
            logger.warn("Unable to load oracle data from stream: [" + dataUrl + "]", throwable);
        }
        Approximation a = approximations.getRight();
        Approximation x;
        while (a != null && ((x = a.getLeft()) != null || (x = a.getRight()) != null)) {
            a = x;
        }
        logger.info("Smallest approximation: " + a);
        logger.info("Largest index: " + largestIndex);
        return approximations;
    }

    public Fraction find(long maxIndex, long numerator, long denominator) {
        Approximation target = createApproximation(-1, numerator, denominator);
        Approximation approximation = approximations.get();
        if (maxIndex > largestIndex) {
            throw new IllegalStateException("Max index larger than largest index: " + maxIndex + ": " + largestIndex);
        }
        Approximation left = null;
        Approximation right = null;
        int order = 0;
        while (approximation != null && approximation.getIndex() <= maxIndex) {
            order = approximation.compareTo(target);
            if (order < 0) {
                left = approximation;
                approximation = approximation.getRight();
            } else if (order > 0) {
                right = approximation;
                approximation = approximation.getLeft();
            } else {
                left = approximation;
                right = approximation;
                break;
            }
        }
        if (left == null || right == null) {
            Approximation range = approximations.get();
            throw new IllegalArgumentException("Target is outside range: " + range + (range == null ? "" : ": " + range.getRight()));
        }
        if (order != 0) {
            Approximation middle = left.add(right).multiply(half.get());
            if (middle.compareTo(target) > 0) {
                approximation = left;
            } else {
                approximation = right;
            }
        }
        return approximation.getFraction();
    }

    public Expectation minimize(Histogram histogram) {
        TypedIterable<Counter> counters = histogram.getCounters();
        long maxIndex = 0L;
        for (Counter counter : counters) {
            maxIndex += counter.getValue();
        }

        Expectation result;
        do {
            logger.debug("Max index: " + maxIndex);
            result = minimize(histogram, maxIndex);
            maxIndex = Math.max(maxIndex + 1L, maxIndex * 3L / 2L);
        } while (result == null);
        return result;
    }

    protected Expectation minimize(Histogram histogram, long maxIndex) {
        TypedIterable<Counter> counters = histogram.getCounters();
        long weight = 0L;
        for (Counter counter : counters) {
            weight += counter.getValue();
        }

        // Associate the probability values with the symbols, and sort the resulting set of pairs on ascending
        // probability values.
        Set<Pair<Fraction,Symbol>> pairs = new TreeSet<Pair<Fraction, Symbol>>();
        for (Counter counter : counters) {
            pairs.add(new Pair<Fraction, Symbol>(root.createTransientFraction(-1, counter.getValue(), weight), counter.getSymbol()));
        }

        // Match, for each pair, the sum of the probability values up to that pair with the set of admissible
        // probability values for this weight.
        // Assign the difference between this admissible value and the previous admissible value as the probability
        // value for this symbol.
        Map<Symbol,Fraction> fractions = new HashMap<Symbol, Fraction>();
        try {
            Fraction last = root.createTransientFraction(-1, 0, 1);
            Fraction lastEstimate = last;
            for (Pair<Fraction,Symbol> pair : pairs) {
                last = add(last, pair.getLeft());
                Fraction estimate = find(maxIndex, last.getNumerator(), last.getDenominator());
                if (lastEstimate != null && estimate.compareTo(lastEstimate) <= 0) {
                    estimate = getSuccessor(maxIndex, lastEstimate);
                    if (estimate == null) {
                        logger.warn("Can't find successor for last estimate: " + maxIndex + ": " + lastEstimate);
                        fractions = null;
                        break;
                    }
                }
                Fraction probability = subtract(estimate, lastEstimate);
                fractions.put(pair.getRight(), root.createTransientFraction(estimate.getIndex(), probability.getNumerator(), probability.getDenominator()));
                lastEstimate = estimate;
            }
        } catch (IllegalStateException exception) {
            logger.debug("Pairs: {");
            for (Pair<Fraction,Symbol> pair : pairs) {
                logger.debug("  (" + pair.getLeft() + ", " + pair.getRight() + ")");
            }
            logger.debug("}");
            throw exception;
        }

        Expectation expectation;
        if (fractions == null) {
            expectation = getUniformEstimate(counters, weight);
        } else {
            expectation = root.createExpectation();
            expectation.setFractions(fractions);
        }
        return expectation;
    }

    protected Expectation getUniformEstimate(TypedIterable<Counter> counters, long weight) {
        Map<Symbol,Fraction> fractions = new HashMap<Symbol, Fraction>();
        for (Counter counter : counters) {
            fractions.put(counter.getSymbol(), root.createTransientFraction(-1L, 1L, weight));
        }
        Expectation expectation = root.createExpectation();
        expectation.setFractions(fractions);
        return expectation;
    }

    public Expectation prefixDecode(Reader code, Collection<Symbol> symbols) {
        int domainSize = DescriptionLength.prefixDecode(code).intValue();
        if (symbols.size() < domainSize) {
            throw new IllegalArgumentException("Not enough symbols: " + symbols.size() + " < " + domainSize);
        }
        if (symbols.size() > domainSize) {
            logger.warn("Too many symbols: " + symbols.size() + " > " + domainSize);
        }

        // Decode the index values, lookup the associated fractions, pair them up with the symbols
        // and sort the resulting set of pairs on ascending factional value.
        Set<Pair<Fraction,Symbol>> pairs = new TreeSet<Pair<Fraction, Symbol>>();
        Iterator<Symbol> symbolIterator = symbols.iterator();
        for (int i = 0; i < domainSize; i++) {
            long index = DescriptionLength.prefixDecode(code).longValue();
            if (index <= 1) {
                continue;
            }
            Fraction cumulative = root.findFraction(index - 1);
            if (cumulative == null) {
                throw new IllegalStateException("No fraction found for: " + index);
            }
            pairs.add(new Pair<Fraction, Symbol>(cumulative, symbolIterator.next()));
        }

        // Assign the difference between successive fractions to the probabilities of the associated symbols.
        Map<Symbol,Fraction> fractions = new HashMap<Symbol, Fraction>();
        Fraction last = root.createTransientFraction(-1, 0, 1);
        for (Pair<Fraction,Symbol> pair : pairs) {
            Fraction cumulative = pair.getLeft();
            Fraction probability = subtract(cumulative, last);
            probability = root.createTransientFraction(cumulative.getIndex(), probability.getNumerator(), probability.getDenominator());
            fractions.put(pair.getRight(), probability);
            last = cumulative;
        }
        if (last.getNumerator() != last.getDenominator()) {
            logger.warn("Probabilities do not add up to unity: " + last);
        }

        Expectation expectation = root.createExpectation();
        expectation.setFractions(fractions);
        return expectation;
    }

    public Fraction add(Fraction f, Fraction g) {
        long fd = f.getDenominator();
        long gd = g.getDenominator();
        long n = (f.getNumerator() * gd) + (g.getNumerator() * fd);
        long d = fd * gd;
        return root.createTransientFraction(-1, n, d);
    }

    public Fraction subtract(Fraction f, Fraction g) {
        long fd = f.getDenominator();
        long gd = g.getDenominator();
        long n = (f.getNumerator() * gd) - (g.getNumerator() * fd);
        long d = fd * gd;
        return root.createTransientFraction(-1, n, d);
    }

    protected Fraction getSuccessor(long maxIndex, Fraction lowerBound) {
        Fraction result = getSuccessor(maxIndex, lowerBound, approximations.get());
        if (logger.isTraceEnabled()) {
            logger.trace("Successor: " + maxIndex + ": " + lowerBound + " -> " + result);
        }
        return result;
    }

    protected Fraction getSuccessor(long maxIndex, Fraction lowerBound, Approximation approximation) {
        Fraction result = null;
        if (approximation.getIndex() <= maxIndex) {
            // if (logger.isTraceEnabled()) {
            //     logger.trace("Search: " + approximation.getFraction());
            // }
            if (lowerBound.compareTo(approximation.getFraction()) < 0) {
                Approximation leftTree = approximation.getLeft();
                if (leftTree != null) {
                    result = getSuccessor(maxIndex, lowerBound, leftTree);
                }
                if (result == null) {
                    result = approximation.getFraction();
                }
            }
            if (result == null) {
                Approximation rightTree = approximation.getRight();
                if (rightTree != null) {
                    result = getSuccessor(maxIndex, lowerBound, rightTree);
                }
            }
        // } else if (logger.isTraceEnabled()) {
        //     logger.trace("Break: " + approximation.getFraction());
        }
        return result;
    }

    protected Approximation createApproximation(long index, long numerator, long denominator) {
        Fraction fraction;
        if (index < 0) {
            fraction = root.createTransientFraction(index, numerator, denominator);
            logger.trace("Created new transient fraction: " + fraction);
        } else {
            fraction = root.findFraction(index);
            if (fraction == null) {
                fraction = root.createFraction(index, numerator, denominator, true);
                logger.trace("Created new persistent fraction: " + fraction);
            } else if (fraction.getNumerator() != numerator || fraction.getDenominator() != denominator) {
                throw new IllegalStateException("Fraction mismatch: (" + index + ", " + numerator + ", " + denominator + "): " + fraction);
            } else {
                logger.trace("Reusing fraction: " + fraction);
            }
        }
        return createApproximation(fraction);
    }

    protected Approximation createApproximation(Fraction fraction) {
        return new Approximation(fraction);
    }

}
