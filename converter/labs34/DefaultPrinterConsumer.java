package converter.labs34;

import java.util.function.Consumer;
import java.util.stream.Collectors;

public class DefaultPrinterConsumer implements Consumer<Element> {
    boolean skipRootElement;
    boolean dontPrintNulls;

    public DefaultPrinterConsumer() {
        this(false, false);
    }

    public DefaultPrinterConsumer(boolean skipRootAtPath, boolean dontPrintNulls) {
        this.skipRootElement = skipRootAtPath;
        this.dontPrintNulls = dontPrintNulls;
    }

    public String getElementPath(Element el) {
        String path;
        if (skipRootElement) {
            path = el.getUplink().stream().skip(1).map(Element::getName).collect(Collectors.joining(", "));
        } else {
            path = el.getUplink().stream().map(Element::getName).collect(Collectors.joining(", "));
        }
        return path;
    }

    @Override
    public void accept(Element el) {
        System.out.println("Element:");
        System.out.println("path = " + getElementPath(el));
        if (el.body == null) {
            if (!dontPrintNulls) {
                System.out.println("value = null");
            }
        } else {
            System.out.printf("value = \"%s\"\n", el.body);
        }
        if (el.attrs.size() > 0) {
            System.out.println("attributes:");
            for (String attrName : el.attrs.keySet()) {
                System.out.printf("%s = \"%s\"\n", attrName, el.attrs.get(attrName));
            }
        }
        System.out.println();

    }

}
