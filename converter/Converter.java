package converter;

import java.io.BufferedReader;
import java.io.IOException;

public interface Converter {
    String convert(BufferedReader reader) throws IOException;
}
