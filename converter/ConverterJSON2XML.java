package converter;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class ConverterJSON2XML implements Converter {
    StringBuilder sb;
    int indent = 0;
    private BufferedReader reader;
    private Queue<String> terms = new LinkedList<>();
    //        String line = reader.readLine();
//
//        String retVal = "";
//        do {
//            switch (kind) {
//                case JSON:
//                    retVal = retVal + convertJSON2XML(line);
//                    break;
//                case XML:
//                    retVal = retVal + convertXML2JSON(line);
//                    break;
//                default:
//                    return "unknown format";
//            }
//
//        } while (reader.ready());
//
//        return retVal;
//}

    //    public String convert(BufferedReader reader) {
//
//        DocType kind = detectDocType(line);
//
//        switch (kind) {
//            case JSON:
//                return convertJSON2XML(line);
//            case XML:
//                return convertXML2JSON(line);
//        }
//
//        return "unknown format";
//    }
    private boolean isSpacer(char ch) {
        return (ch == ' ' || ch == '\n');
    }

    private void trimReader() throws IOException {
        char ch;
        do {
            ch = (char) reader.read();
        } while (isSpacer(ch));
    }

    private String readNextTokenValue() throws IOException {
        char ch;
        StringBuilder sb = new StringBuilder();
        do {
            ch = (char) reader.read();
            if (ch != '\"') {
                sb.append(ch);
            }
        } while (!isSpacer(ch));
        sb.setLength(sb.length() - 1);
        return sb.toString();
    }

    private Token readNextToken() throws IOException {
        trimReader();
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
                terms.offer( readNextTokenValue());
                return Token.TERM;
        }
    }

//    private String convertJSON2XML(String docContent) {


    //        Pattern pat;
//        Matcher m;

//        pat = Pattern.compile("\\{\"(.*)\"\\s*:\\s*\"(.*)\"\\}");
//        m = pat.matcher(docContent);
//        if (m.find()) {
//            StringBuilder sb = new StringBuilder();
//            sb.
//                    append("<").append(m.group(1)).append(">").
//                    append(m.group(2)).
//                    append("</").append(m.group(1)).append(">");
//            return sb.toString();
//        }
//
//        pat = Pattern.compile("\\{\"(.*)\"\\s*:\\s*null}");
//        m = pat.matcher(docContent);
//        if (m.find()) {
//            StringBuilder sb = new StringBuilder();
//            sb.
//                    append("<").append(m.group(1)).append("/>");
//            return sb.toString();
//        }
//        return null;
//    }

    public void appendElement(XMLElem xmlEl) {
        sb.append("<").append(xmlEl.name).append(" ");

        for (String attr : xmlEl.attrs.keySet()) {
            sb.append(attr).append("=").
                    append("\"").append(xmlEl.attrs.get(attr)).append("\"").append(" ");
        }

        if (xmlEl.body != null) {
            sb.append(">").append(xmlEl.body).append("</").append(xmlEl.name).append(">");
        } else {
            sb.append("/>");
        }

    }

    public void readNextBlock(String name, XMLElem parent) throws IOException {
        XMLElem xmlEl = new XMLElem(name);
        while (true) {
            Token tk = readNextToken();
            switch (tk) {
                case BLOCK_CLOSE:
                    appendElement(xmlEl);
                    indent--;
                    return;
                case BLOCK_OPEN:
                    indent++;
                    readNextBlock(terms.poll(), xmlEl);
                    break;
                case FIELDS_SEPARATOR:
                    if (indent > 1) {
                        if lastTermTokenValue.charAt(0) == '@' {

                        }
                    }

            }
        }


    }

    @Override
    public String convert(BufferedReader reader) throws IOException {
        this.reader = reader;
        sb = new StringBuilder();

        Token tk = readNextToken();
        readNextBlock(null, null);
        return null;
    }

    enum Token {
        TERM, BLOCK_OPEN, BLOCK_CLOSE, FIELDS_SEPARATOR, NAMEVAL_SEPARATOR;
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
