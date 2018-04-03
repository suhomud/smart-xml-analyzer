package agile.angine.com;

import agile.angine.com.display.ConsoleDisplay;
import agile.angine.com.display.Display;
import agile.angine.com.models.Range;
import agile.angine.com.selectors.DocumentSelector;
import agile.angine.com.selectors.Selector;
import agile.angine.com.util.DocumentParser;
import agile.angine.com.weight.ElementSimilarityWeight;
import org.apache.commons.cli.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class App {

    private static Logger LOGGER = LoggerFactory.getLogger(App.class);

    private static final String OPTION_ORIGIN_FILE_PATH = "ip";
    private static final String OPTION_OTHER_SAMPLE_FILE_PATH = "op";
    private static final String OPTION_SEARCHING_ID = "id";

    private static int DEFAULT_MINIMUM_WEIGHT = 50;

    private static String INPUT_ORIGIN_FILE_PATH = "";
    private static String INPUT_OTHER_SAMPLE_FILE_PATH = "";
    private static String SEARCHING_ELEMENT_ID = "";

    private static Selector selector = new DocumentSelector();
    private static Display display = new ConsoleDisplay();
    private static Element originalElementById;

    public static void main(String[] args) {
        try {
            setupParrams(args);

            Document originalDocument = getDocument(INPUT_ORIGIN_FILE_PATH, "could not load origin document");

            originalElementById = getSearchElementById(originalDocument);

            Document searchDocument = getDocument(INPUT_OTHER_SAMPLE_FILE_PATH, "could not load other document");

            Elements possibleSimilarElements = getPossibleElements(searchDocument);

            List<Range> elementsRange = getWeightedRange(possibleSimilarElements);
            Collections.sort(elementsRange);

            Optional<Element> searchItem = getElementWithRelevantWeight(elementsRange);

            display.display(searchItem.orElseThrow(() -> new NoSuchElementException("Could not find provided element")));

        } catch (RuntimeException pe) {
            LOGGER.info(pe.getMessage());
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());
        }
    }

    private static List<Range> getWeightedRange(Elements possibleSimilarElements) {
        ElementSimilarityWeight similarityWeight = ElementSimilarityWeight.builder()
                .withMeasure(originalElementById)
                .build();

        List<Range> elementsRange = new ArrayList<>(possibleSimilarElements.size());

        possibleSimilarElements.forEach(element -> {
            elementsRange.add(similarityWeight.calculateWeight(element));
        });

        return elementsRange;
    }

    private static Element getSearchElementById(Document originalDocument) {
        return selector.selectById(SEARCHING_ELEMENT_ID, originalDocument)
                .orElseThrow(() -> new NoSuchElementException("could not find element by id"));
    }

    private static Elements getPossibleElements(Document otherDocument) {
        return selector.selectByQuery(originalElementById.tag().toString(), otherDocument)
                .orElseThrow(() -> new NoSuchElementException("could not find elements with provided arguments"));
    }

    private static Document getDocument(String inputOriginFilePath, String s) throws IOException {
        File originFile = new File(inputOriginFilePath);
        return DocumentParser.toDocument(originFile)
                .orElseThrow(() -> new IllegalArgumentException(s));
    }

    private static Optional<Element> getElementWithRelevantWeight(List<Range> elementsRange) {
        return elementsRange.stream()
                .filter(range -> range.weight >= DEFAULT_MINIMUM_WEIGHT)
                .map(range -> range.element)
                .findFirst();
    }

    private static void setupParrams(String[] args) throws ParseException {
        Options opts = createOptions();

        CommandLine cli = parseOptions(opts, args);
        INPUT_ORIGIN_FILE_PATH = cli.getOptionValue(OPTION_ORIGIN_FILE_PATH);
        INPUT_OTHER_SAMPLE_FILE_PATH = cli.getOptionValue(OPTION_OTHER_SAMPLE_FILE_PATH);
        SEARCHING_ELEMENT_ID = cli.getOptionValue(OPTION_SEARCHING_ID);
    }

    private static Options createOptions() {
        Options options = new Options();

        options.addOption(Option.builder(OPTION_ORIGIN_FILE_PATH)
                .required()
                .hasArg()
                .build());

        options.addOption(Option.builder(OPTION_OTHER_SAMPLE_FILE_PATH)
                .required()
                .hasArg()
                .build());

        options.addOption(Option.builder(OPTION_SEARCHING_ID)
                .required()
                .hasArg()
                .build());

        return options;
    }

    private static CommandLine parseOptions(Options opts, String[] args) throws ParseException {
        CommandLineParser parser = new DefaultParser();
        return parser.parse(opts, args);
    }

}
