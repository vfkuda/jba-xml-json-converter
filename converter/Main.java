package converter;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;

public class Main {


    public static void main(String[] args) {
        ConverterApp app = new ConverterApp();

        try {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(new File("test.txt")));
            System.out.println(app.convert(bis));
        } catch (IOException e) {

            e.printStackTrace();
        }
    }
}
