package converter;

import converter.labs12.ConverterApp;
import converter.labs34.DefaultPrinterConsumer;
import converter.labs34.Element;
import converter.labs34.AlaJSONParser;
import converter.labs34.XMLParser;

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
//        lab3();
        lab4();
    }

    private static void lab3() {
        XMLParser p = null;
        try {
            p = new XMLParser(new BufferedInputStream(new FileInputStream(new File("test.txt"))));
            Element root = p.parse();
            root.traverse(new DefaultPrinterConsumer());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void lab4() {
        AlaJSONParser p = null;
        try {
            p = new AlaJSONParser(new BufferedInputStream(new FileInputStream(new File("test.txt"))));
//            Element root = p.parse();
            Element root = p.parseWierdStructureWithAttribitesMagic();
            for (Element el : root.getChildren()) {
                el.traverse(new DefaultPrinterConsumer(true, true));
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
