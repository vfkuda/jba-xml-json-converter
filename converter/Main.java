package converter;

import java.io.IOException;
import java.util.Scanner;

public class Main {


    public static void main(String[] args) {
        ConverterApp app = new ConverterApp();

//        Scanner sc = new Scanner(System.in);
//        String content = sc.next();

        try {
            System.out.println(app.convert(System.in));
        } catch (IOException e) {

            e.printStackTrace();
        }
    }
}
