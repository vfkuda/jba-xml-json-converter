package converter;

import converter.ConverterApp;

import java.io.IOException;
import java.util.Objects;

public class ConverterAppTest {
    public static void main(String[] args) {
        ConverterAppTest t = new ConverterAppTest();
        try {
            t.test1();
            t.test2();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        System.out.println("1: " + strim(str1) + "==");
        System.out.println("2: " + strim(str2));
//        System.out.println(strim(str1) + " == " + strim(str2));
        return Objects.equals(strim(str1), strim(str2));
    }

    public void test1() throws IOException {
        ConverterApp app = new ConverterApp();

        assert testStrings(app.convert("<host>127.0.0.1</host>"), "{\"host\":\"127.0.0.1\"}");
        assert testStrings(app.convert("<success/>"), "{\"success\": null}");

        assert testStrings(app.convert("{\"jdk\" : \"1.8.9\"}"), "<jdk>1.8.9</jdk>");
        assert testStrings(app.convert("{\"storage\" : null}"), "<storage/>");
        System.out.println("test2 done");

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
        assert testStrings2(app.convert("<person rate = \"1\" name = \"Torvalds\" />"), "" +
                "{\n" +
                "    \"person\" : {\n" +
                "        \"@rate\" : \"1\",\n" +
                "        \"@name\" : \"Torvalds\",\n" +
                "        \"#person\" : null\n" +
                "    }\n" +
                "}");


        System.out.println("test2 done");
    }
}
