package agile.angine.com.weight;

import agile.angine.com.models.Range;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.junit.Assert;
import org.junit.Test;

public class ElementSimilarityWeightTest {

    private static final int INITIAL_WEIGHT = 0;

    @Test
    public void calculateWeightIdMatch() {
        // before
        Element originalElement = new Element(Tag.valueOf("a"), "")
                .attr("id", "testId");
        Element otherElement = new Element(Tag.valueOf("a"), "")
                .attr("id", "testId");
        ElementSimilarityWeight weight = ElementSimilarityWeight.builder()
                .withMeasure(originalElement)
                .build();
        // when
        Range range = weight.calculateWeight(otherElement);
        // than
        Assert.assertNotNull(range.element);
        Assert.assertTrue(range.element.equals(otherElement));
        Assert.assertTrue(range.weight > INITIAL_WEIGHT);
    }

    @Test
    public void calculateWeightTextMatch() {
        // before
        Element originalElement = new Element(Tag.valueOf("a"), "")
                .text("testText");
        Element otherElement = new Element(Tag.valueOf("a"), "")
                .text("testText");
        ElementSimilarityWeight weight = ElementSimilarityWeight.builder()
                .withMeasure(originalElement)
                .build();
        // when
        Range range = weight.calculateWeight(otherElement);
        // than
        Assert.assertNotNull(range.element);
        Assert.assertTrue(range.element.equals(otherElement));
        Assert.assertTrue(range.weight > INITIAL_WEIGHT);
    }

    @Test
    public void calculateWeightAttributeMatch() {
        // before
        Element originalElement = new Element(Tag.valueOf("a"), "")
                .attr("ref", "")
                .attr("class", "");
        Element otherElement = new Element(Tag.valueOf("a"), "")
                .attr("ref", "");
        ElementSimilarityWeight weight = ElementSimilarityWeight.builder()
                .withMeasure(originalElement)
                .build();
        // when
        Range rangeOneAttrMatch = weight.calculateWeight(otherElement);
        int weightOneAttrMatch = rangeOneAttrMatch.weight;
        // than
        Assert.assertNotNull(rangeOneAttrMatch.element);
        Assert.assertTrue(rangeOneAttrMatch.element.equals(otherElement));
        Assert.assertTrue(weightOneAttrMatch > INITIAL_WEIGHT);
        // when add attr
        otherElement = otherElement.attr("class", "");
        Range rangeTwoAttrMatch = weight.calculateWeight(otherElement);
        // than
        Assert.assertTrue("weight should increase after adding second attr",
                rangeTwoAttrMatch.weight > weightOneAttrMatch);
    }

    @Test
    public void calculateWeightAttributeValueMatch() {
        // before
        Element originalElement = new Element(Tag.valueOf("a"), "")
                .attr("class", "btn success")
                .attr("type", "button");
        Element otherElement = new Element(Tag.valueOf("a"), "")
                .attr("placeholder", "button");
        ElementSimilarityWeight weight = ElementSimilarityWeight.builder()
                .withMeasure(originalElement)
                .build();
        // when
        Range rangeOneAttrValueMatch = weight.calculateWeight(otherElement);
        int weightOneAttrValueMatch = rangeOneAttrValueMatch.weight;
        // than
        Assert.assertNotNull(rangeOneAttrValueMatch.element);
        Assert.assertTrue(rangeOneAttrValueMatch.element.equals(otherElement));
        Assert.assertTrue(weightOneAttrValueMatch > INITIAL_WEIGHT);
        // when add attr
        otherElement = otherElement.attr("role", "success");
        Range rangeTwoAttrValueMatch = weight.calculateWeight(otherElement);
        // than
        Assert.assertTrue("weight should increase after adding second attr value",
                rangeTwoAttrValueMatch.weight > weightOneAttrValueMatch);
    }

}