package agile.angine.com.weight;

import agile.angine.com.models.Range;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;

import java.util.StringJoiner;
import java.util.regex.Pattern;

public class ElementSimilarityWeight {

    private Pattern elementMatcher;

    private ElementSimilarityWeight(Element originalElement) {
        StringJoiner searchElementPattern = getSearchElementPattern(originalElement);
        elementMatcher = Pattern.compile(searchElementPattern.toString());
    }

    private StringJoiner getSearchElementPattern(Element originalMeasure) {
        StringJoiner attributesPattern = new StringJoiner("|");
        originalMeasure.attributes().forEach(attribute -> attributesPattern.add(attribute.getValue()));
        attributesPattern.add(originalMeasure.text());
        attributesPattern.add(originalMeasure.tag().toString());
        attributesPattern.add(originalMeasure.id());
        return attributesPattern;
    }

    public Range calculateWeight(Element e) {
        int weight = 0;
        if (elementMatcher.matcher(e.id()).find()) {
            weight += 40;
        }

        if (elementMatcher.matcher(e.text()).find()) {
            weight += 30;
        }

        Attributes attributes = e.attributes();

        for (Attribute attribute : attributes) {
            if (elementMatcher.matcher(attribute.getKey()).find()) {
                weight += 5;
            }
            if (elementMatcher.matcher(attribute.getValue()).find()) {
                weight += 20;
            }
        }

        return new Range(e, weight);
    }

    public static WeightBuilder builder() {
        return new WeightBuilder();
    }

    public static class WeightBuilder {

        private Element measure;

        public WeightBuilder withMeasure(Element measure) {
            this.measure = measure;
            return this;
        }

        public ElementSimilarityWeight build() {
            return new ElementSimilarityWeight(measure);
        }
    }

}
