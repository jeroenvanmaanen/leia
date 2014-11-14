package org.leialearns.logic.interaction;

import org.leialearns.api.common.PrefixDecoder;
import org.leialearns.api.interaction.Alphabet;
import org.leialearns.bridge.BaseBridgeFacet;
import org.leialearns.bridge.BridgeOverride;
import org.leialearns.utilities.Setting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import static org.leialearns.utilities.Static.getLoggingClass;

/**
 * Implements the logic of alphabet objects insofar as it can be oblivious of the ORM framework.
 */
public class AlphabetAugmenter extends BaseBridgeFacet {
    private final Logger logger = LoggerFactory.getLogger(getLoggingClass(this));
    private final Map<Long,Long> ordinalTranslation = new HashMap<>();
    private Setting<Long> fixatedDescriptionLength = new Setting<>("Fixated description length");

    /*
     * @see org.leialearns.api.interaction.Alphabet#getFixatedDescriptionLength()
     */
    @BridgeOverride
    public long getFixatedDescriptionLength() {
        Alphabet alphabet = getAlphabet();
        if (!alphabet.isFixated()) {
            throw new IllegalStateException("Alphabet is not fixated: " + alphabet);
        }
        long result;
        if (fixatedDescriptionLength.isFixated()) {
            result = fixatedDescriptionLength.get();
        } else {
            long largestOrdinal = alphabet.findLargestSymbolOrdinal();
            logger.debug("Largest symbol ordinal: " + largestOrdinal);
            result = (long) BigInteger.valueOf(largestOrdinal).bitLength();
            logger.debug("Fixed description length: " + result);
            fixatedDescriptionLength.set(result);
        }
        return result;
    }

    protected Alphabet getAlphabet() {
        return (Alphabet) getBridgeFacets().getNearObject();
    }

    @BridgeOverride
    public void prefixDecode(PrefixDecoder decoder) {
        decoder.nextString();
        Alphabet alphabet = getAlphabet();
        long numberOfSymbols = decoder.nextLong();
        ordinalTranslation.clear();
        for (long i = 0; i < numberOfSymbols; i++) {
            String denotation = decoder.nextString();
            long newIndex = alphabet.internalize(denotation).getOrdinal();
            if (newIndex != i) {
                ordinalTranslation.put(i, newIndex);
            }
        }
    }

    @BridgeOverride
    public long translateOrdinal(Long i) {
        return ordinalTranslation.containsKey(i) ? ordinalTranslation.get(i) : i;
    }
}
