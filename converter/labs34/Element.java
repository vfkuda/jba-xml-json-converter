package converter.labs34;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class Element {
    String name;
    Map<String, String> attrs = new LinkedHashMap<>();
    String body;
    Element parent;
    List<Element> children = new ArrayList<>();

    public Element(String elName, Element parent) {
        this.name = elName;
        this.parent = parent;
    }

    public List<Element> getChildren() {
        return children;
    }

    public String getName() {
        return name;
    }

    public char namePrefix() {
        return name.charAt(0);
    }

    public boolean hasName() {
        return name.length() > 0;
    }

    public boolean hasChild(Predicate<Element> p) {
        return null != findChild(p);
    }

    public Element findChild(Predicate<Element> p) {
        for (Element el : children) {
            if (p.test(el)) {
                return el;
            }
        }
        return null;
    }

    public void traverse(Consumer<Element> consumer) {
        consumer.accept(this);
        for (Element el : children) {
            el.traverse(consumer);
        }
    }

    public List<Element> getUplink() {
        LinkedList<Element> path = new LinkedList<>();
        Element node = this;
        do {
            path.addFirst(node);
            node = node.parent;
        } while (node != null);
        return path;
    }

}
