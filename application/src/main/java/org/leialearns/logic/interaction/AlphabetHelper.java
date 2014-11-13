package org.leialearns.logic.interaction;

import org.leialearns.api.common.PrefixEncoder;
import org.leialearns.api.interaction.Alphabet;
import org.leialearns.api.interaction.Symbol;
import org.leialearns.bridge.BridgeOverride;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class AlphabetHelper {
    private static final Logger logger = LoggerFactory.getLogger(new Object(){}.getClass().getEnclosingClass());

    @BridgeOverride
    public void prefixEncode(Alphabet alphabet, PrefixEncoder encoder) {
        Long largestOrdinal = alphabet.findLargestSymbolOrdinal();
        logger.debug("Largest ordinal: {}", largestOrdinal);
        if (largestOrdinal == null) {
            encoder.append(0);
        } else {
            encoder.append(largestOrdinal + 1);
            for (long i = 0; i <= largestOrdinal; i++) {
                Symbol symbol = alphabet.getSymbol(i);
                logger.debug("Encode symbol: {}", symbol);
                encoder.append(symbol.getDenotation());
            }
        }
    }
}
