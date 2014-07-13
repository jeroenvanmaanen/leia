package org.leialearns.command.generator;

import org.leialearns.utilities.ExceptionWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigInteger;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.leialearns.utilities.Static.getLoggingClass;

public class Generator implements org.leialearns.command.api.Generator {
    private final Logger logger = LoggerFactory.getLogger(getLoggingClass(this));
    private static final long DEFAULT_LENGTH = 1000L;

    @Override
    public void command(String... args) {
        long length = DEFAULT_LENGTH;
        int a = 0;
        if (a + 1 < args.length && "-l".equals(args[a])) {
            length = Long.parseLong(args[a + 1]);
            a += 2;
        }
        if (a < args.length && "--".equals(args[a])) {
            a++;
        }
        if (a > 0) {
            int l = args.length - a;
            String[] newArgs = new String[l];
            System.arraycopy(args, a, newArgs, 0, l);
            args = newArgs;
        }
        if (args.length < 0) {
            throw new IllegalArgumentException("Missing source file name");
        }
        for (String sourceLocation : args) {
            try {
                InputStream source = new URL(sourceLocation).openStream();
                Reader reader = new InputStreamReader(source, Charset.forName("UTF-8"));
                generate(reader, length);
            } catch (IOException exception) {
                throw ExceptionWrapper.wrap(exception);
            }
        }
    }

    protected void generate(Reader reader, long length) throws IOException {
        Random random = new Random();
        MarkovNode node = parse(reader);
        while (length > 0) {
            length--;
            MarkovEdge edge = node.generate(random);
            logger.debug("Generate: {}", edge.getSymbol());
            System.out.print(edge.getSymbol());
            node = edge.getTo();
        }
    }

    protected MarkovNode parse(Reader input) throws IOException {
        Map<String,MarkovNode> nodes = new HashMap<>();
        MarkovNode root = new MarkovNode();
        nodes.put("root", root);
        BufferedReader reader = new BufferedReader(input);
        String line;
        String identifier_re = "[a-zA-Z_]+";
        String edge_label_re = "([0-9]+)\\|((?:[^]\\\\]|\\\\[]\\\\nr])*)";
        Pattern pattern = Pattern.compile(String.format("^\\s*(%s)\\s*-\\[%s\\]->\\s*(%s)\\s*$", identifier_re, edge_label_re, identifier_re));
        while ((line = reader.readLine()) != null) {
            Matcher matcher = pattern.matcher(line);
            if (matcher.matches()) {
                String fromName = matcher.group(1);
                String toName = matcher.group(4);
                BigInteger weight = new BigInteger(matcher.group(2));
                String symbol = matcher.group(3);
                logger.debug("New edge: {} -([{}|{}]-> {})", new Object[] {fromName, weight, symbol, toName});
                symbol = symbol.replaceAll("\\\\n", "\n").replaceAll("\\\\r", "\r").replace("\\\\", "\\");
                MarkovNode from = getNode(nodes, fromName);
                MarkovNode to = getNode(nodes, toName);
                MarkovEdge edge = new MarkovEdge(weight, symbol, to);
                from.add(edge);
            } else {
                logger.debug("Skip:[{}]", line);
            }
        }
        return root;
    }

    protected MarkovNode getNode(Map<String,MarkovNode> nodes, String name) {
        MarkovNode node;
        if (nodes.containsKey(name)) {
            node = nodes.get(name);
        } else {
            node = new MarkovNode();
            nodes.put(name, node);
        }
        return node;
    }
}
