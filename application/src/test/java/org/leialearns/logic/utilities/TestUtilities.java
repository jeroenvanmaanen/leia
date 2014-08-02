package org.leialearns.logic.utilities;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.leialearns.logic.model.Fraction;
import org.leialearns.utilities.ExceptionWrapper;
import org.leialearns.utilities.ExecutionListener;
import org.leialearns.utilities.TransactionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.leialearns.logic.utilities.DescriptionLength.descriptionLength;
import static org.leialearns.logic.utilities.DescriptionLength.toBinary;
import static org.leialearns.logic.utilities.PrefixFree.prefixDecode;
import static org.leialearns.logic.utilities.PrefixFree.prefixEncode;
import static org.leialearns.utilities.Static.getLoggingClass;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/ApplicationContext.xml","/org/leialearns/AppTest-context.xml"})
@TestExecutionListeners(value = {DependencyInjectionTestExecutionListener.class, ExecutionListener.class})
public class TestUtilities {
    private final Logger logger = LoggerFactory.getLogger(getLoggingClass(this));

    @Autowired
    private Oracle oracle;

    @Autowired
    private TransactionHelper transactionHelper;

    @BeforeClass
    public static void beforeClass() throws IOException {
        org.leialearns.utilities.TestUtilities.beforeClass(null);
    }

    @Test
    public void testBigIntegerToBinary() {
        logger.info("Start test");
        for (int i = 0; i < 20; i++) {
            logger.info("Binary: " + i + ": [" + toBinary(BigInteger.valueOf(i)) + "]");
        }
        BigInteger n = BigInteger.valueOf(Long.MAX_VALUE - 10);
        BigInteger end = n.add(BigInteger.valueOf(20));
        while (n.compareTo(end) < 0) {
            logger.info("Binary: " + n + ": [" + toBinary(n) + "]");
            n = n.add(BigInteger.ONE);
        }
    }

    @Test
    public void testPrefixEncodeBigInteger() {
        logger.info("Start test");
        for (int i = 0; i < 40; i++) {
            singlePrefixEncodeBigInteger(BigInteger.valueOf(i));
        }
        for (int i = 1; i < 17; i++) {
            BigInteger n = BigInteger.valueOf(2).pow(i).subtract(BigInteger.ONE);
            BigInteger m = n.subtract(BigInteger.ONE);
            singlePrefixEncodeBigInteger(m);
            singlePrefixEncodeBigInteger(n);
        }
    }

    private void singlePrefixEncodeBigInteger(BigInteger n) {
        String encoded = prefixEncode(n);
        logger.info("Prefix-free: " + n + ": [" + encoded + "]");
        Reader encodedReader = new StringReader(encoded);
        BigInteger decoded = prefixDecode(encodedReader).bigInteger();
        assertAtEnd(encodedReader);
        assertEquals(n, decoded);
    }

    public static void assertAtEnd(Reader reader) {
        try {
            int ch;
            while ((ch = reader.read()) != -1) {
                assertTrue(ch != 'O' && ch != 'I');
            }
            reader.close();
        } catch (IOException exception) {
            throw ExceptionWrapper.wrap(exception);
        }
    }

    @Test
    public void testDescriptionLengthBigInteger() {
        logger.info("Start test");
        for (int i = 0; i < 40; i++) {
            singleDescriptionLengthBigInteger(BigInteger.valueOf(i));
        }
        BigInteger p = BigInteger.valueOf(16);
        for (int i = 1; i < 12; i++) {
            p = p.multiply(p);
            BigInteger n = p.subtract(BigInteger.ONE);
            BigInteger m = n.subtract(BigInteger.ONE);
            singleDescriptionLengthBigInteger(m);
            singleDescriptionLengthBigInteger(n);
        }
    }

    private void singleDescriptionLengthBigInteger(BigInteger n) {
        String encoded = prefixEncode(n);
        int parts = encoded.replaceAll("[^/]+", "").length();
        encoded = encoded.replaceAll("[^OI]+", "");
        long descriptionLength = descriptionLength(n);
        logger.info("Description length: " + n + ": [" + descriptionLength + "]: ((" + encoded.length() + ":" + parts + "))");
        assertEquals("description length == length of prefix-free encoding", descriptionLength, encoded.length());
    }

    @Ignore
    @Test
    public void testOracle() {
        try {
            assertNotNull(oracle);
            transactionHelper.runInTransaction(new Runnable() {
                @Override
                public void run() {
                    oracle.reset();
                }
            });
            compare(2, 7, 3, 7, -1);
            compare(3, 7, 2, 7, 1);
            compare(2, 7, 2, 7, 1);
            compare(2, 7, 2, 7, -1);
            part(11);
            part(12);
            part(195);
            part(196);
        } catch (Throwable throwable) {
            logger.error("Exception", throwable);
            throw ExceptionWrapper.wrap(throwable);
        }
    }

    protected void compare(long n1, long d1, long n2, long d2, int s) {
        Approximation first = oracle.createApproximation(-1, n1, d1);
        Approximation second = oracle.createApproximation(-1, n2, d2);
        assertTrue(first.compareTo(second) * s >= 0);
    }

    protected void part(int maxIndex) {
        logger.info("");
        int d = 70;
        for (int n = 15; n <= 22; n++) {
            Fraction fraction = oracle.find(maxIndex, n, d);
            logger.info("Approximation: " + maxIndex + " " + n + "/" + d + " -> " + fraction);
        }
    }

}
