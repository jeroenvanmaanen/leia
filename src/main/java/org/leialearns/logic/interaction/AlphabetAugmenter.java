package org.leialearns.logic.interaction;

import org.leialearns.bridge.BaseBridgeFacet;
import org.leialearns.utilities.Setting;

import java.math.BigInteger;

/**
 * Implements the logic of alphabet objects insofar as it can be oblivious of the ORM framework.
 */
public class AlphabetAugmenter extends BaseBridgeFacet {
    private Setting<Long> fixatedDescriptionLength = new Setting<Long>("Fixated description length");

    /**
     * @see org.leialearns.logic.interaction.Alphabet#getFixatedDescriptionLength()
     */
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
            result = (long) BigInteger.valueOf(largestOrdinal).bitLength();
            fixatedDescriptionLength.set(result);
        }
        return result;
    }

    protected Alphabet getAlphabet() {
        return (Alphabet) getBridgeFacets().getNearObject();
    }

}
