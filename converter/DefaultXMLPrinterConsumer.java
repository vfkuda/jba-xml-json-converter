package converter;

import java.util.function.Consumer;
import java.util.stream.Collectors;

public class DefaultXMLPrinterConsumer implements Consumer<XMLParser.XMLElement> {

    @Override
    public void accept(XMLParser.XMLElement el) {
        System.out.println("Element:");
        System.out.println("path = " +
                el.getUpNodes().stream().map(XMLParser.XMLElement::getName).collect(Collectors.joining(", ")));
        if (el.body == null) {
            System.out.printf("value = null\n");
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
