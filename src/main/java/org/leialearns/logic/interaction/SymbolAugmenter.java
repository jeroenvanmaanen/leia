package org.leialearns.logic.interaction;

import org.leialearns.bridge.BaseBridgeFacet;
import org.leialearns.logic.utilities.DescriptionLength;

import java.math.BigInteger;

/**
 * Implements the logic of symbol objects insofar as it can be oblivious of the ORM framework.
 */
public class SymbolAugmenter extends BaseBridgeFacet {

    /**
     * @see org.leialearns.logic.interaction.Symbol#descriptionLength()
     */
    public long descriptionLength() {
        Symbol symbol = (Symbol) getBridgeFacets().getNearObject();
        Alphabet alphabet = symbol.getAlphabet();
        boolean fixatedAlphabet = alphabet.isFixated();
        long result;
        if (fixatedAlphabet) {
            result = alphabet.getFixatedDescriptionLength();
        } else {
            result = DescriptionLength.descriptionLength(BigInteger.valueOf(symbol.getOrdinal()));
        }
        return result;
    }

}
