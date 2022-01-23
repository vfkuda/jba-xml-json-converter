package converter;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class XMLParser {
    TokenReader reader;
    Queue<String> terms = new LinkedList<>();

    public XMLParser(InputStream is) {
        this.reader = new TokenReader(is);
    }

    private Token nextToken() throws IOException {
        reader.skipSpaces();
        char ch = reader.read();
        switch (ch) {
            case '>':
                return Token.ELEMENT_CLOSE_MORE;
            case '<':
                if (reader.checkAhead('/')) {
                    reader.read();
                    return Token.ELEMENT_OPEN_LESS_SLASH;
                }
                return Token.ELEMENT_OPEN_LESS;
            case '/':
                if (reader.checkAhead('>')) {
                    reader.read();
                    return Token.ELEMENT_CLOSE_SLASH_MORE;
                }
                System.out.println("unexpected '/'");
                break;
            case '=':
                return Token.SIGN_EQU;
            case '\"':
                reader.unread('\"');
                terms.offer(reader.readQuoted());
                return Token.TERM_QUOTED;
            default:
                reader.unread(ch);
                terms.offer(reader.readAlfaNumeric());
                return Token.TERM;
        }
        return null;
    }

    private String nextTermToken() throws IOException {
        Token tk = nextToken();
        if (!(tk == Token.TERM || tk == Token.TERM_QUOTED)) {
            System.out.println("Error: token was expected");
        }
        return terms.poll();
    }

    public XMLElement readElement(XMLElement parent) throws IOException {
        skipExpected(Token.ELEMENT_OPEN_LESS);
        String name = nextTermToken();
        XMLElement el = new XMLElement(name, parent);
        boolean betweenTags = false;

        while (true) {
            Token tk = nextToken();
            switch (tk) {
                case ELEMENT_CLOSE_SLASH_MORE:
//                    nextTermToken();
//                    skipExpected(Token.ELEMENT_CLOSE_MORE);
                    return el;
                case ELEMENT_CLOSE_MORE:
                    betweenTags = true;
                    break;
                case SIGN_EQU:
                    el.attrs.put(terms.poll(), nextTermToken());
                    break;
                case ELEMENT_OPEN_LESS_SLASH:
                    nextTermToken();
                    skipExpected(Token.ELEMENT_CLOSE_MORE);
                    return el;
                case ELEMENT_OPEN_LESS:
                    reader.unread('<');
                    XMLElement child = readElement(el);
                    el.children.add(child);
                    break;
                case TERM:
                case TERM_QUOTED:
                    if (betweenTags) {
                        reader.unread(terms.poll());
                        el.body = reader.readUntill('<');
                    }
                    break;
            }
        }
    }

    private void skipExpected(Token expected) throws IOException {
        Token tk = nextToken();
        if (tk != expected) {
            System.out.printf("Unexpected %s Token vs %s\n", tk, expected);
        }
    }

    public XMLElement parse() throws IOException {
        return readElement(null);
    }

    private enum Token {
        ELEMENT_OPEN_LESS, ELEMENT_OPEN_LESS_SLASH,
        ELEMENT_CLOSE_MORE, ELEMENT_CLOSE_SLASH_MORE, TERM, TERM_QUOTED, SIGN_EQU

    }

    class XMLElement {
        String name;
        Map<String, String> attrs = new LinkedHashMap<>();
        String body;
        XMLElement parent = null;
        List<XMLElement> children = new ArrayList<>();

        public XMLElement(String elName, XMLElement parent) {
            this.name = elName;
            this.parent = parent;
        }

        public String getName() {
            return name;
        }

        public void traverse(Consumer<XMLElement> consumer) {
            consumer.accept(this);
            for (XMLElement el : children) {
                el.traverse(consumer);
            }
        }

        public List<XMLElement> getUpNodes() {
            LinkedList<XMLElement> path = new LinkedList<>();
            XMLElement node = this;
            do {
                path.addFirst(node);
                node = node.parent;
            } while (node != null);
            return path;
        }
    }


}
