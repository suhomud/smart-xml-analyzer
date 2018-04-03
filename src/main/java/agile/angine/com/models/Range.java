package agile.angine.com.models;

import org.jsoup.nodes.Element;

public class Range implements Comparable<Range> {

    public Element element;
    public int weight;

    public Range(Element element, int weight) {
        this.element = element;
        this.weight = weight;
    }

    @Override
    public int compareTo(Range o) {
        return Integer.compare(this.weight, o.weight);
    }

}
