package converter.labs12;

import java.io.*;

public class ConverterApp {

    protected DocType detectDocType(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        switch (reader.read()) {
            case '<':
                return DocType.XML;
            case '{':
                return DocType.JSON;
        }
        return DocType.UNKNOWN;
    }

    public String convert(InputStream is) throws IOException {
        is.mark(1);
        DocType kind = detectDocType(is);
        is.reset();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        Converter c = createConverter(kind);
        return c.convert(reader);
    }

    public String convert(String str) throws IOException {
        InputStream is = new ByteArrayInputStream(str.getBytes());
        return convert(is);
    }

    public Converter createConverter(DocType kind) {
        Converter c;
        switch (kind) {
            case JSON:
                c = new ConverterJSON2XML();
                break;
            case XML:
                c = new ConverterXML2JSON();
                break;
            default:
                return null;
        }
        return c;

    }

    public static enum DocType {
        JSON, XML, UNKNOWN;
    }


}
