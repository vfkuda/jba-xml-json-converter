package converter.labs12;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

@Deprecated
public class ConverterJSON2XML implements Converter {
    StringBuilder sb;
    int indent = 0;
    private BufferedReader reader;
    private Queue<String> terms = new LinkedList<>();

    private boolean isSpacer(char ch) {
        return (ch == ' ' || ch == '\n');
    }

    private void skipSpaces() throws IOException {
        char ch;
        do {
            reader.mark(1);
            ch = (char) reader.read();
        } while (isSpacer(ch));
        reader.reset();
    }

    private void readNextQuotedTerm(StringBuilder sb) throws IOException {
        while (true) {
            char ch = (char) reader.read();
            if (ch == '\"') {
                break;
            } else {
                sb.append(ch);
            }
        }
    }

    private String readNextTerm() throws IOException {
        skipSpaces();
        char ch;
        StringBuilder sb = new StringBuilder();
        while (true) {
            reader.mark(1);
            ch = (char) reader.read();
            if (ch == '\"') {
//                reader.reset();
                readNextQuotedTerm(sb);
            } else if (Character.isLetterOrDigit(ch)) {
                sb.append(ch);
            } else {
                reader.reset();
                break;
            }
        }
        String term = sb.toString();
        if (Objects.equals(term, "null")) {
            term = null;
        }
        return term;
    }

    private Token readNextToken() throws IOException {
        skipSpaces();
        reader.mark(1);
        char ch = (char) reader.read();
        switch (ch) {
            case '{':
                return Token.BLOCK_OPEN;
            case '}':
                return Token.BLOCK_CLOSE;
            case ':':
                return Token.NAMEVAL_SEPARATOR;
            case ',':
                return Token.FIELDS_SEPARATOR;
            default:
                reader.reset();
                terms.offer(readNextTerm());
                return Token.TERM;
        }
    }

    private boolean skipExpectedToken(Token expectedToken) throws IOException {
        Token tk = readNextToken();
        if (tk != expectedToken) {
            System.out.println("unexpected token " + tk + " instead of " + expectedToken);
        }
        return tk == expectedToken;
    }

    private boolean skipExpectedNotRequiredToken(Token expectedToken) throws IOException {
        reader.mark(20);
        Token tk = readNextToken();
        reader.reset();
        if (tk == expectedToken) {
            readNextToken();
        }
        return tk == expectedToken;
    }

    public void appendElement(XMLElem xmlEl) {
        sb.append("<").append(xmlEl.name);

        for (String attr : xmlEl.attrs.keySet()) {
            sb.append(" ").
                    append(attr).append("=").
                    append("\"").append(xmlEl.attrs.get(attr)).append("\"");
        }

        if (xmlEl.body != null) {
            sb.append(">").append(xmlEl.body).append("</").append(xmlEl.name).append(">");
        } else {
            sb.append("/>");
        }

    }

    public void readNextBlock(String elName, XMLElem parent) throws IOException {
        Token tk = Token.NOP;
        while (true) {
            tk = readNextToken();
            if (tk == Token.BLOCK_CLOSE) {
                break;
            }
            String name = terms.poll();
            skipExpectedToken(Token.NAMEVAL_SEPARATOR);
            String val = readNextTerm();
            switch (name.charAt(0)) {
                case '@':
                    parent.attrs.put(name.substring(1), val);
                    break;
                case '#':
                    parent.body = val;
                    break;
                default:
//
            }
            skipExpectedNotRequiredToken(Token.FIELDS_SEPARATOR);
        }
    }

    @Override
    public String convert(BufferedReader reader) throws IOException {
        this.reader = reader;
        sb = new StringBuilder();

        skipExpectedToken(Token.BLOCK_OPEN);
        boolean doneFlag = false;
        Token tk;
        while (!doneFlag) {
            tk = readNextToken();
            switch (tk) {
                case BLOCK_CLOSE:
                    doneFlag = true;
                    continue;
            }

            String elName = terms.poll();

            XMLElem xmlEl = new XMLElem(elName);
            skipExpectedToken(Token.NAMEVAL_SEPARATOR);
            tk = readNextToken();
            switch (tk) {
                case BLOCK_OPEN:
                    readNextBlock(elName, xmlEl);
                    appendElement(xmlEl);
                    break;
                case TERM:
                    xmlEl.body = terms.poll();
                    appendElement(xmlEl);
                    break;
                default:
                    System.out.println("unexpected");
            }
        }
        return sb.toString();
    }

    enum Token {
        NOP, TERM, BLOCK_OPEN, BLOCK_CLOSE, FIELDS_SEPARATOR, NAMEVAL_SEPARATOR;
    }

    private class XMLElem {
        String name;
        Map<String, String> attrs = new LinkedHashMap<>();
        String body;

        public XMLElem(String elName) {
            this.name = elName;
        }
    }


}
