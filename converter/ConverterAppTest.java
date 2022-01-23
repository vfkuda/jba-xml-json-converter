package converter;

import converter.ConverterApp;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.stream.Collectors;

public class ConverterAppTest {
//    public static void main(String[] args) {
//        ConverterAppTest t = new ConverterAppTest();
//        try {
//            t.test0();
////            t.test1();
////            t.test2();
//            t.test3();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    private void test0() throws IOException {
        XMLParser p = new XMLParser(new ByteArrayInputStream("abcd   xyz19 \"asfsa\" abba".getBytes()));
        assert 'a' == p.reader.read();
        assert 'b' == p.reader.lookAhead();
        assert 'b' == p.reader.read();

        p.reader.unread('b');
        assert 'b' == p.reader.read();

        assert "cd".equals(p.reader.readUntill(' '));

        p.reader.skipSpaces();
        assert "xyz19".equals(p.reader.readAlfaNumeric());

        p.reader.unread("z19");
        assert 'z' == p.reader.read();
        assert '1' == p.reader.read();
        assert '9' == p.reader.read();
        assert ' ' == p.reader.read();

        assert "asfsa".equals(p.reader.readQuoted());

        p.reader.skipSpaces();
        assert 'a' == p.reader.read();
        assert "bba".equals(p.reader.readAlfaNumeric());

        System.out.println("test 0 passed");


    }

    public String strim(String s) {
        String ss;
        ss = s.replaceAll("\\s*", "");
        ss = ss.replaceAll("\n", "");
        return ss;
    }

    public boolean testStrings(String str1, String str2) {
        System.out.println(str1 + " == " + str2);
        return Objects.equals(str1, str2);
    }

    public boolean testStrings2(String str1, String str2) {
        System.out.println("1: " + str1 + "==");
        System.out.println("2: " + strim(str2));
        return Objects.equals(strim(str1), strim(str2));
    }

    public void test1() throws IOException {
        ConverterApp app = new ConverterApp();

        assert testStrings(app.convert("<host>127.0.0.1</host>"), "{\"host\":\"127.0.0.1\"}");
        assert testStrings(app.convert("<success/>"), "{\"success\": null}");

        assert testStrings(app.convert("{\"jdk\" : \"1.8.9\"}"), "<jdk>1.8.9</jdk>");
        assert testStrings(app.convert("{\"storage\" : null}"), "<storage/>");
        System.out.println("test1 done");

    }

    public void test2() throws IOException {
        ConverterApp app = new ConverterApp();

        assert testStrings2(app.convert("<employee department = \"manager\">Garry Smith</employee>"), "" +
                "{\n" +
                "    \"employee\" : {\n" +
                "        \"@department\" : \"manager\",\n" +
                "        \"#employee\" : \"Garry Smith\"\n" +
                "    }\n" +
                "}");
        assert testStrings2(app.convert("" +
                        "{\n" +
                        "    \"employee\" : {\n" +
                        "        \"@department\" : \"manager\",\n" +
                        "        \"#employee\" : \"Garry Smith\"\n" +
                        "    }\n" +
                        "}" +
                        ""),
                "<employee department = \"manager\">Garry Smith</employee>"
        );
        assert testStrings2(app.convert("" +
                        "{\n" +
                        "    \"person\" : {\n" +
                        "        \"@rate\" : 1,\n" +
                        "        \"@name\" : \"Torvalds\",\n" +
                        "        \"#person\" : null\n" +
                        "    }\n" +
                        "}" +
                        ""),
                "<person rate = \"1\" name = \"Torvalds\" />"
        );

        System.out.println("test2 done");
    }


    public void test3() throws IOException {
        String xml = "" +
                "<transaction>\n" +
                "    <id>6753322</id>\n" +
                "    <number region=\"Russia\">8-900-000-00-00</number>\n" +
                "    <nonattr />\n" +
                "    <nonattr></nonattr>\n" +
                "    <nonattr>text</nonattr>\n" +
                "    <attr id=\"1\" />\n" +
                "    <attr id=\"2\"></attr>\n" +
                "    <attr id=\"3\">text</attr>\n" +
                "    <email>\n" +
                "        <to>to_example@gmail.com</to>\n" +
                "        <from>from_example@gmail.com</from>\n" +
                "        <subject>Project discussion</subject>\n" +
                "        <body font=\"Verdana\">Body message</body>\n" +
                "        <date day=\"12\" month=\"12\" year=\"2018\"/>\n" +
                "    </email>\n" +
                "</transaction>";

        XMLParser p = new XMLParser(new ByteArrayInputStream(xml.getBytes()));
        XMLParser.XMLElement root = p.parse();
        root.traverse(new DefaultXMLPrinterConsumer());
    }

}
