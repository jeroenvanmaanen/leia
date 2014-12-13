package org.leialearns.logic.interaction;

import org.leialearns.api.interaction.Alphabet;
import org.leialearns.api.interaction.Symbol;
import org.leialearns.bridge.BaseBridgeFacet;
import org.leialearns.bridge.BridgeOverride;
import org.leialearns.logic.prefixfree.DescriptionLength;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;

import static org.leialearns.common.Display.display;
import static org.leialearns.common.Static.getLoggingClass;

/**
 * Implements the logic of symbol objects insofar as it can be oblivious of the ORM framework.
 */
public class SymbolAugmenter extends BaseBridgeFacet {
    private final Logger logger = LoggerFactory.getLogger(getLoggingClass(this));

    /*
     * @see org.leialearns.api.interaction.Symbol#descriptionLength()
     */
    @BridgeOverride
    public long descriptionLength() {
        Symbol symbol = (Symbol) getBridgeFacets().getNearObject();
        Alphabet alphabet = symbol.getAlphabet();
        logger.trace("Alphabet of symbol: " + display(alphabet));
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
