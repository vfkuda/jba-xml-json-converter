package converter;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConverterXML2JSON implements Converter {

    public String convert(BufferedReader reader) throws IOException {
        StringBuilder sb = new StringBuilder();
        while (reader.ready()) {
            sb.append(convertXML2JSON(reader.readLine()));
        }
        return sb.toString();
    }

    private String convertXML2JSON(String docContent) {
        Pattern pat;
        Matcher m;

        // element with attributes and body
        pat = Pattern.compile("^<(\\w+)\\s((\\w+\\s*=\\s*\"\\w+\\s*\")+)>(.*)</(.*)>$");
        m = pat.matcher(docContent);
        if (m.find()) {
            System.out.println("m1");
            StringBuilder sb = new StringBuilder();
            String elName = m.group(1);
            String elAtts = m.group(2);
            String elBody = m.group(4);

            Map<String, String> attrs = retreiveXMLAttributes(elAtts);
            attrs.put("#" + elName, elBody);

            sb.append("{");
            sb.append("\"").append(elName).append("\":");
            appendAttributes(sb, attrs);
            sb.append("}");

            return sb.toString();
        }

        // element with attributes no body
        pat = Pattern.compile("^<(\\w+)\\s*((\\w+\\s*=\\s*\"\\w+\"\\s*)+)\\s*/>$");
        m = pat.matcher(docContent);
        if (m.find()) {
            System.out.println("m2");
            StringBuilder sb = new StringBuilder();
            String elName = m.group(1);
            String elAtts = m.group(2);
            System.out.println(m.groupCount() + elAtts);

            Map<String, String> attrs = retreiveXMLAttributes(elAtts);
            attrs.put("#" + elName, null);

            sb.append("{");
            sb.append("\"").append(elName).append("\":");
            appendAttributes(sb, attrs);
            sb.append("}");

            return sb.toString();
        }


        pat = Pattern.compile("<(.*)>(.*)</(.*)>");
        m = pat.matcher(docContent);
        if (m.find()) {
            StringBuilder sb = new StringBuilder();
            sb.append("{");
            sb.
                    append("\"").append(m.group(1)).append("\":").
                    append("\"").append(m.group(2)).append("\"");
            sb.append("}");
            return sb.toString();
        }

        pat = Pattern.compile("^<(.*)/>$");
        m = pat.matcher(docContent);
        if (m.find()) {
            StringBuilder sb = new StringBuilder();
            sb.append("{");
            sb.
                    append("\"").append(m.group(1)).append("\": ").
                    append("null");
            sb.append("}");
            return sb.toString();
        }
        return null;
    }

    private Map<String, String> retreiveXMLAttributes(String attributesString) {
        LinkedHashMap<String, String> retVal = new LinkedHashMap<>();
        Pattern patAtt = Pattern.compile("(\\w+)\\s*=\\s*\"(\\w+)\"");
        Matcher m = patAtt.matcher(attributesString);
        while (m.find()) {
            retVal.put("@" + m.group(1), m.group(2));
        }
        return retVal;
    }

    private void appendAttributes(StringBuilder sb, Map<String, String> attrs) {
        sb.append("{");
        for (String name : attrs.keySet()) {
            if (attrs.get(name) != null) {
                sb.append("\"").append(name).append("\" : \"").append(attrs.get(name)).append("\",");
            } else {
                sb.append("\"").append(name).append("\" : null").append(",");
            }
        }
        sb.setLength(sb.length() - 1);
        sb.append("}");
    }

}
