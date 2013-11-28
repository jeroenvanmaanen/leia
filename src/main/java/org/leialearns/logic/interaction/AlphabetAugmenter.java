package org.leialearns.logic.interaction;

import org.leialearns.bridge.BaseBridgeFacet;
import org.leialearns.bridge.BridgeOverride;
import org.leialearns.utilities.Setting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;

import static org.leialearns.utilities.Static.getLoggingClass;

/**
 * Implements the logic of alphabet objects insofar as it can be oblivious of the ORM framework.
 */
public class AlphabetAugmenter extends BaseBridgeFacet {
    private final Logger logger = LoggerFactory.getLogger(getLoggingClass(this));
    private Setting<Long> fixatedDescriptionLength = new Setting<>("Fixated description length");

    /**
     * @see org.leialearns.logic.interaction.Alphabet#getFixatedDescriptionLength()
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

}
