package converter;

import java.io.*;

public class Main {

    public static void lab2() {
        ConverterApp app = new ConverterApp();
        try {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(new File("test.txt")));
            System.out.println(app.convert(bis));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
//        lab2();
        lab3();
    }

    private static void lab3() {
        XMLParser p = null;
        try {

            p = new XMLParser(new BufferedInputStream(new FileInputStream(new File("test.txt"))));
            XMLParser.XMLElement root = p.parse();
            root.traverse(new DefaultXMLPrinterConsumer());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
