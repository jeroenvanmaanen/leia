package org.leialearns.api.common;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.leialearns.spring.test.ExecutionListener;
import org.leialearns.spring.test.TestUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigInteger;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/ApplicationContext.xml","/org/leialearns/AppTest-context.xml"})
@TestExecutionListeners(value = {DependencyInjectionTestExecutionListener.class, ExecutionListener.class})
public class PrefixFreeBigIntTest {
    private static final Logger logger = LoggerFactory.getLogger(new Object(){}.getClass().getEnclosingClass());
    private static final BigInteger MAX_LONG = BigInteger.valueOf(Long.MAX_VALUE);
    private static final BigInteger LARGE = MAX_LONG.multiply(MAX_LONG);
    private static final int WIDTH = 70;
    private static final int ALIGN = 3;
    private static final String STRING = "Hello,\tWorld!\r\n\u3375";

    @Autowired
    PrefixEncoderFactory prefixEncoderFactory;

    @Autowired
    PrefixDecoderFactory prefixDecoderFactory;

    @BeforeClass
    public static void beforeClass() throws IOException {
        TestUtilities.beforeClass(null);
    }

    @Test
    public void testReadable() throws IOException {
        StringWriter writer = new StringWriter();
        PrefixEncoder prefixEncoder = prefixEncoderFactory.createReadablePrefixEncoder(writer);
        save(prefixEncoder);
        prefixEncoder.close();

        String encoding = writer.toString();
        logger.debug("Encoding: <![CDATA[\n" + encoding + "]]>");

        StringReader reader = new StringReader(encoding);
        PrefixDecoder prefixDecoder = prefixDecoderFactory.createReadablePrefixDecoder(reader);
        verify(prefixDecoder);
    }

    @Test
    public void testBinary() throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrefixEncoder prefixEncoder = prefixEncoderFactory.createBinaryPrefixEncoder(output);
        save(prefixEncoder);
        prefixEncoder.close();

        byte[] encoding = output.toByteArray();
        if (logger.isDebugEnabled()) {
            int i = 0;
            while (i < encoding.length) {
                String label = Integer.toHexString(i).toUpperCase();
                if (label.length() < 2) {
                    label = " " + label;
                }
                StringBuilder hex = new StringBuilder();
                StringBuilder text = new StringBuilder();
                for (int j = 0; j < 8; j++, i++) {
                    if (i < encoding.length) {
                        byte b = encoding[i];
                        hex.append(' ');
                        hex.append(Integer.toHexString((b & 0xFF) | 0x100).substring(1));
                        if (b >= ' ' && b <= '~') {
                            text.append("  ");
                            text.append((char) b);
                        } else {
                            switch (b) {
                                case '\r':
                                    text.append(" \\r");
                                    break;
                                case '\n':
                                    text.append(" \\n");
                                    break;
                                case '\t':
                                    text.append(" \\t");
                                    break;
                                case '.':
                                    text.append(" \\.");
                                    break;
                                default:
                                    text.append("  .");
                            }
                        }
                    } else {
                        hex.append("   ");
                        text.append("   ");
                    }
                }
                logger.debug(String.format("%s -%s %s", label, hex, text));
            }
        }

        ByteArrayInputStream input = new ByteArrayInputStream(encoding);
        PrefixDecoder prefixDecoder = prefixDecoderFactory.createBinaryPrefixDecoder(input);
        verify(prefixDecoder);
        Assert.assertEquals(0, prefixDecoder.nextInt()); // A padding bit to get a full byte
    }

    private void save(PrefixEncoder prefixEncoder) {
        prefixEncoder.append(0);
        prefixEncoder.append(LARGE);
        prefixEncoder.append(Byte.MAX_VALUE, WIDTH);
        prefixEncoder.append(0, ALIGN);
        prefixEncoder.append(STRING);
        prefixEncoder.append(2);
        prefixEncoder.append(LARGE, LARGE.bitLength() + 16);
    }

    private void verify(PrefixDecoder prefixDecoder) {
        Assert.assertEquals(0, prefixDecoder.nextInt());
        Assert.assertEquals(LARGE, prefixDecoder.nextBigInteger());
        Assert.assertEquals(Byte.MAX_VALUE, prefixDecoder.nextInt(WIDTH));
        Assert.assertEquals(0, prefixDecoder.nextLong(ALIGN));
        Assert.assertEquals(STRING, prefixDecoder.nextString());
        Assert.assertEquals(2, prefixDecoder.nextLong());
        Assert.assertEquals(LARGE, prefixDecoder.nextBigInteger(LARGE.bitLength() + 16));
    }
}
