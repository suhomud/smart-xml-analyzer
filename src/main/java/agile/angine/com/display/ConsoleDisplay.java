package agile.angine.com.display;

import org.jsoup.nodes.Element;

import java.util.ArrayDeque;
import java.util.StringJoiner;

public class ConsoleDisplay implements Display {

    @Override
    public void display(Element element) {
        StringJoiner resultText = new StringJoiner(" > ");
        if (element.parents().isEmpty()) {
            resultText.add(element.tag().toString());
            return;
        }

        new ArrayDeque<>(element.parents())
                .descendingIterator()
                .forEachRemaining(parent -> resultText.add(getNewElementTag(parent) + joinIndex(parent)));
        resultText.add(element.tag().toString());
        System.out.println(resultText.toString());
    }

    private String joinIndex(Element element) {
        Integer index = element.elementSiblingIndex();
        return index == 0 ? "" : "[" + (index) + "]";
    }

    private String getNewElementTag(Element parent) {
        return parent.tag().toString();
    }

}
