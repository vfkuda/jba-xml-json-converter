package converter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.function.Predicate;

public class TokenReader {
    final int BUF_SIZE = 64;
    final int BUF_CENTER = BUF_SIZE / 2;
    int bufferPointer = BUF_CENTER;
    int bufferEnd = BUF_CENTER;
    BufferedReader reader;
    char[] extraBuf = new char[BUF_SIZE];

    public TokenReader(InputStream is) {
        this.reader = new BufferedReader(new InputStreamReader(is));
    }

    public void skipUntill(Predicate<Character> p) throws IOException {
        char ch;
        do {
            ch = read();
        } while (p.test(ch));
        unread(ch);
    }

    public String readUntill(Predicate<Character> p) throws IOException {
        StringBuilder sb = new StringBuilder();
        char ch;
        do {
            ch = read();
            sb.append(ch);
        } while (p.test(ch));
        unread(ch);
        sb.setLength(sb.length() - 1);
        return sb.toString();
    }

    public void skipSpaces() throws IOException {
        skipUntill(ch -> (ch == ' ' || ch == '\n'));
    }

    public char lookAhead() throws IOException {
        if (bufferPointer >= bufferEnd) {
            extraBuf[bufferEnd++] = (char) reader.read();
        }
        return extraBuf[bufferPointer];
    }

    public boolean checkAhead(char expectedChar) throws IOException {
        return expectedChar == lookAhead();
    }

    public char read() throws IOException {
        if (bufferPointer < bufferEnd) {
            return extraBuf[bufferPointer++];
        } else {
            bufferPointer = BUF_CENTER;
            bufferEnd = BUF_CENTER;
            return (char) reader.read();
        }
    }

    public String readAlfaNumeric() throws IOException {
        return readUntill(ch -> (Character.isLetterOrDigit(ch)));
    }


    public String readUntill(char tillChar) throws IOException {
        return readUntill(ch -> ch != tillChar);
    }

    public String readQuoted() throws IOException {
        read();
        String retVal = readUntill('\"');
        read();
        return retVal;
    }

    public void unread(char ch) {
        extraBuf[--bufferPointer] = ch;
    }

    public void unread(String str) {
        bufferPointer = bufferPointer - str.length();
        for (int i = 0; i < str.length(); i++) {
            extraBuf[bufferPointer + i] = str.charAt(i);
        }
    }

}
