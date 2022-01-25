package converter;

import converter.labs12.ConverterApp;
import converter.labs34.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Objects;

public class MainTest {
//    public static void main(String[] args) {
//        MainTest t = new MainTest();
//        try {
////            t.test0();
////            t.test1();
////            t.test2();
////            t.test3();
//            t.test4();
////            t.test41();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    private void test0() throws IOException {
//        testing token reader
        TokenReader r = new TokenReader(new ByteArrayInputStream("abcd   xyz19 \"asfsa\" abba".getBytes()));
        assert 'a' == r.read();
        assert 'b' == r.lookAhead();
        assert 'b' == r.read();

        r.unread('b');
        assert 'b' == r.read();

        assert "cd".equals(r.readWhile(' '));

        r.skipSpaces();
        assert "xyz19".equals(r.readAlfaNumeric());

        r.unread("z19");
        assert 'z' == r.read();
        assert '1' == r.read();
        assert '9' == r.read();
        assert ' ' == r.read();

        assert "asfsa".equals(r.readQuoted());

        r.skipSpaces();
        assert 'a' == r.read();
        assert "bba".equals(r.readAlfaNumeric());

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
        Element root = p.parse();
        root.traverse(new DefaultPrinterConsumer());
    }

    private void test4() throws IOException {
        String json = "" +
                "{\n" +
                "    \"transaction\": {\n" +
                "        \"id\": \"6753322\",\n" +
                "        \"number\": {\n" +
                "            \"@region\": \"Russia\",\n" +
                "            \"#number\": \"8-900-000-000\"\n" +
                "        },\n" +
                "        \"empty1\": null,\n" +
                "        \"empty2\": { },\n" +
                "        \"empty3\": \"\",\n" +
                "        \"inner1\": {\n" +
                "            \"inner2\": {\n" +
                "                \"inner3\": {\n" +
                "                    \"key1\": \"value1\",\n" +
                "                    \"key2\": \"value2\"\n" +
                "                }\n" +
                "            }\n" +
                "        },\n" +
                "        \"inner4\": {\n" +
                "            \"@\": 123,\n" +
                "            \"#inner4\": \"value3\"\n" +
                "        },\n" +
                "        \"inner4.2\": {\n" +
                "            \"\": 123,\n" +
                "            \"#inner4.2\": \"value3\"\n" +
                "        },\n" +
                "        \"inner5\": {\n" +
                "            \"@attr1\": 123.456,\n" +
                "            \"#inner4\": \"value4\"\n" +
                "        },\n" +
                "        \"inner6\": {\n" +
                "            \"@attr2\": 789.321,\n" +
                "            \"#inner6\": \"value5\"\n" +
                "        },\n" +
                "        \"inner7\": {\n" +
                "            \"#inner7\": \"value6\"\n" +
                "        },\n" +
                "        \"inner8\": {\n" +
                "            \"@attr3\": \"value7\"\n" +
                "        },\n" +
                "        \"inner9\": {\n" +
                "            \"@attr4\": \"value8\",\n" +
                "            \"#inner9\": \"value9\",\n" +
                "            \"something\": \"value10\"\n" +
                "        },\n" +
                "        \"inner10\": {\n" +
                "            \"@attr5\": null,\n" +
                "            \"#inner10\": null\n" +
                "        },\n" +
                "        \"inner11\": {\n" +
                "            \"@\": null,\n" +
                "            \"#\": null\n" +
                "        },\n" +
                "        \"inner12\": {\n" +
                "            \"@somekey\": \"attrvalue\",\n" +
                "            \"#inner12\": null,\n" +
                "            \"somekey\": \"keyvalue\",\n" +
                "            \"inner12\": \"notnull\"\n" +
                "        },\n" +
                "        \"inner13\": {\n" +
                "            \"@invalid_attr\": {\n" +
                "                \"some_key\": \"some value\"\n" +
                "            },\n" +
                "            \"#inner13\": {\n" +
                "                \"key\": \"value\"\n" +
                "            }\n" +
                "        },\n" +
                "        \"\": {\n" +
                "            \"#\": null,\n" +
                "            \"secret\": \"this won't be converted\"\n" +
                "        }\n" +
                "    },\n" +
                "    \"meta\": {\n" +
                "        \"version\": 0.01\n" +
                "    }\n" +
                "}";

        AlaJSONParser p = new AlaJSONParser(new ByteArrayInputStream(json.getBytes()));
//        Element root = p.parse();
        Element root = p.parseWierdStructureWithAttribitesMagic();
        for (Element el : root.getChildren()) {
            el.traverse(new DefaultPrinterConsumer(true, true));
        }


    }

    private void test41() throws IOException {
        String json = "" +
                "{\n" +
                "    \"root1\": {\n" +
                "        \"@attr1\": \"val1\",\n" +
                "        \"@attr2\": \"val2\",\n" +
                "        \"#root1\": {\n" +
                "            \"elem1\": {\n" +
                "                \"@attr3\": \"val3\",\n" +
                "                \"@attr4\": \"val4\",\n" +
                "                \"#elem1\": \"Value1\"\n" +
                "            },\n" +
                "            \"elem2\": {\n" +
                "                \"@attr5\": \"val5\",\n" +
                "                \"@attr6\": \"val6\",\n" +
                "                \"#elem2\": \"Value2\"\n" +
                "            }\n" +
                "        }\n" +
                "    },\n" +
                "    \"root2\": {\n" +
                "        \"@attr1\": null,\n" +
                "        \"@attr2\": \"\",\n" +
                "        \"#root2\": null\n" +
                "    },\n" +
                "    \"root3\": {\n" +
                "        \"@attr1\": \"val2\",\n" +
                "        \"@attr2\": \"val1\",\n" +
                "        \"#root3\": \"\"\n" +
                "    },\n" +
                "    \"root4\": \"Value4\"\n" +
                "}" +
                "";

        AlaJSONParser p = new AlaJSONParser(new ByteArrayInputStream(json.getBytes()));
//        Element root = p.parse();
        Element root = p.parseWierdStructureWithAttribitesMagic();
        for (Element el : root.getChildren()) {
            el.traverse(new DefaultPrinterConsumer(true, true));
        }

    }

    private void test5() {
    }

}
