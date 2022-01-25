package converter.labs34;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class AlaJSONParser {
    private final TokenReader reader;
    private final Queue<String> terms = new LinkedList<>();

    public AlaJSONParser(InputStream is) {
        this.reader = new TokenReader(is);
    }

    private String readAlfaNumericDotUnderscoreTermOrNull() throws IOException {
        String str = reader.readWhile(ch -> (Character.isLetterOrDigit(ch) || ch == '_' || ch == '.'));
        return "null".equals(str) ? null : str;
    }

    //    reads token and returns its type. for term puts read term into `terms` collection
    private Token nextToken() throws IOException {
        reader.skipSpaces();
        char ch = reader.read();
        switch (ch) {
            case '{':
                return Token.BLOCK_OPEN;
            case '}':
                return Token.BLOCK_CLOSE;
            case ':':
                return Token.NAME_VALUE_SEPARATOR;
            case ',':
                return Token.ELEMENT_SEPARATOR;
            case '\"':
                reader.unread('\"');
                terms.offer(reader.readQuoted());
                return Token.TERM_QUOTED;
            default:
                reader.unread(ch);
                terms.offer(reader.readAlfaNumeric());
                return Token.TERM;
        }
    }

    private String getLastReadTerm() {
        return terms.poll();
    }

    private void skipExpected(Token expected) throws IOException {
        Token tk = nextToken();
        if (tk != expected) {
            System.out.printf("Unexpected %s Token vs %s\n", tk, expected);
        }
    }

    public void addSubElement(Element parent, Element child) {
        parent.children.add(child);
    }

    private void parseElement(Element parent) throws IOException {
        Token tk;
        Element el = null;

        while (true) {
//            expected '}' or ',' or TERM
            tk = nextToken();

            switch (tk) {
                case BLOCK_CLOSE:
                    if (el != null) {
                        addSubElement(parent, el);
                    }
                    return;
                case ELEMENT_SEPARATOR:
                    addSubElement(parent, el);
                    break;
                case TERM:
                case TERM_QUOTED:
//                    expects structure  `key` `:` `value || block`
                    String name = terms.poll();
                    el = new Element(name, parent);
                    skipExpected(Token.NAME_VALUE_SEPARATOR);
                    tk = nextToken();
                    switch (tk) {
                        case BLOCK_OPEN:
                            parseElement(el);
                            break;
                        case TERM:
                            reader.unread(getLastReadTerm());
                            el.body = readAlfaNumericDotUnderscoreTermOrNull();
                            break;
                        case TERM_QUOTED:
                            el.body = getLastReadTerm();
                            break;
                    }
            }
        }
    }


    private void moveChildren(Element from, Element to) {
        to.children.addAll(from.children);
        for (Element e : to.children) {
            e.parent = to;
        }
    }

    private void postprocessElementMagic(Element el) {

        Element hashedChild = el.findChild(e -> e.name.equals("#" + el.name));

//        there should be #name element
        boolean attributesMagicHolds = hashedChild != null;
//            all children should be prefixed with @
        if (attributesMagicHolds) {
            attributesMagicHolds = !el.hasChild(e -> (e != hashedChild && (e.name.length() == 0 || e.namePrefix() != '@')));
        }
//            all children should have not empty name
        if (attributesMagicHolds) {
            attributesMagicHolds = !el.hasChild(e -> (e != hashedChild && e.name.length() == 1));
        }
//            none should have grandchildren
        if (attributesMagicHolds) {
            attributesMagicHolds = !el.hasChild(e -> (e != hashedChild && e.children.size() > 0));
        }


        if (attributesMagicHolds) {
            //attribute magic holds - convert @children into decent attributes
            Element child;
            for (Iterator<Element> it = el.children.iterator(); it.hasNext(); ) {
                child = it.next();
                if (child.namePrefix() == '@') {
                    el.attrs.put(child.name.substring(1), child.body);
                    it.remove();
                }
            }
//            as magic holds there is only one #-prefixed element.
//            move its subElements or body text to current element
            el.children.clear();
            if (hashedChild.children.size() > 0) {
                moveChildren(hashedChild, el);
            } else {
                el.body = hashedChild.body;
            }

        } else {
            // if attribute magic vanishes - strip all magic prefixes
            Element child;
            for (int i = el.children.size() - 1; i >= 0; i--) {
                child = el.children.get(i);
                if (child.hasName()) {
                    switch (child.namePrefix()) {
                        case '@':
                        case '#':
                            if (child.name.length() == 1) {
                                el.children.remove(i);
                            } else {
                                String strippedName = child.name.substring(1);

//                                if element with name already exists -> remove
//                                otherwise strip magic prefixes
                                if (el.findChild(e -> e.name.equals(strippedName)) != null) {
                                    el.children.remove(i);
                                } else {
                                    child.name = strippedName;
                                }
                                break;
                            }
                    }
                } else {
                    el.children.remove(i);
                }
            }
        }

        // propagate the same down the list
        for (Element child : el.children) {
            postprocessElementMagic(child);
        }

    }

    public Element parse() throws IOException {
        skipExpected(Token.BLOCK_OPEN);

        Element root = new Element("/", null);
        parseElement(root);
        return root;
    }

    public Element parseWierdStructureWithAttribitesMagic() throws IOException {
        Element root = parse();
        for (Element el : root.children) {
            postprocessElementMagic(el);
        }
        return root;
    }


    private enum Token {
        BLOCK_OPEN, BLOCK_CLOSE, TERM, TERM_QUOTED, ELEMENT_SEPARATOR, NAME_VALUE_SEPARATOR,
    }

}
