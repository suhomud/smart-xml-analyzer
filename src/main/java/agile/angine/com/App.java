package agile.angine.com;

import org.apache.commons.cli.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class App {
    private static Logger LOGGER = LoggerFactory.getLogger(App.class);

    private static final String OPTION_ORIGIN_FILE_PATH = "ip";
    private static final String OPTION_OTHER_SAMPLE_FILE_PATH = "op";
    private static final String OPTION_SEARCHING_ID = "id";
    private static final String CHARSET_NAME = "utf8";

    private static int DEFAULT_MINIMUM_WEIGHT = 20;

    private static String INPUT_ORIGIN_FILE_PATH = "";
    private static String INPUT_OTHER_SAMPLE_FILE_PATH = "";
    private static String SEARCHING_ELEMENT_ID = "";

    public static void main(String[] args) {
        try {
            // set up
            setupParrams(args);

            // collecting searchingData
            File originFile = new File(INPUT_ORIGIN_FILE_PATH);
            Document originalDocument = parseFile(originFile)
                    .orElseThrow(() -> new IllegalArgumentException("could not load origin document"));
            Optional<Element> originElementById = findElementById(originalDocument, SEARCHING_ELEMENT_ID);
            Element originElement = originElementById
                    .orElseThrow(() -> new NoSuchElementException("could not find element by id"));
            Attributes originAttributes = originElement.attributes();

            // collecting attributes from other file
            File otherFile = new File(INPUT_OTHER_SAMPLE_FILE_PATH);
            Document otherDocument = parseFile(otherFile)
                    .orElseThrow(() -> new IllegalArgumentException("could not load other document"));
            Optional<Elements> otherElementsByTagOpt = findElementsByQuery(otherDocument, originElement.tag().toString());
            Map<Element, Attributes> otherElementAttrs = getElementAttributes(otherElementsByTagOpt.
                    orElseThrow(() -> new NoSuchElementException("could not find elements with provided arguments")));

            // weighting similarity
            Map<Element, Integer> elementSimilarityWeight = weightSimilarity(originAttributes, otherElementAttrs);

            // getting result
            Optional<Element> searchItem = getElementWithRelevantWeight(elementSimilarityWeight);

            printResult(searchItem.orElseThrow(() -> new NoSuchElementException("Could not find provided element ")));
        } catch (RuntimeException pe) {
            LOGGER.info(pe.getMessage());
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());
        }
    }

    private static Optional<Element> getElementWithRelevantWeight(Map<Element, Integer> elementSimilarityWeight) {
        return elementSimilarityWeight.entrySet().stream()
                .filter(map -> map.getValue() >= DEFAULT_MINIMUM_WEIGHT)
                .map(Map.Entry::getKey)
                .findFirst();
    }

    private static void setupParrams(String[] args) throws ParseException {
        Options opts = createOptions();

        CommandLine cli = parseOptions(opts, args);
        INPUT_ORIGIN_FILE_PATH = cli.getOptionValue(OPTION_ORIGIN_FILE_PATH);
        INPUT_OTHER_SAMPLE_FILE_PATH = cli.getOptionValue(OPTION_OTHER_SAMPLE_FILE_PATH);
        SEARCHING_ELEMENT_ID = cli.getOptionValue(OPTION_SEARCHING_ID);
    }

    private static void printResult(Element searchItem) {
        StringJoiner resultText = new StringJoiner(" > ");
        new ArrayDeque<>(searchItem.parents())
                .descendingIterator()
                .forEachRemaining(parent -> resultText.add(parent.tag().toString()));
        resultText.add(searchItem.tag().toString());
        LOGGER.info(resultText.toString());
    }

    private static Map<Element, Attributes> getElementAttributes(Elements elements) {
        return elements.stream()
                .distinct()
                .collect(Collectors.toMap(element -> element, Node::attributes));
    }

    private static Map<Element, Integer> weightSimilarity(Attributes originAttributes, Map<Element, Attributes> otherElementAttr) {
        Map<Element, Integer> elementSimilarityWeight = new HashMap<>();
        otherElementAttr.forEach(((element, attributes) -> {
            elementSimilarityWeight.put(element, weightAttrSimilarity(originAttributes, attributes));
        }));
        return elementSimilarityWeight;
    }

    private static Integer weightAttrSimilarity(Attributes originAttrs, Attributes otherAttr) {
        AtomicInteger weightSimilarity = new AtomicInteger(0);
        originAttrs.iterator()
                .forEachRemaining(originAttr -> {
                            String otherAttrValues = otherAttr.get(originAttr.getKey());
                            if (otherAttrValues.isEmpty()) {
                                return;
                            }
                            weightSimilarity.set(Arrays.stream(originAttr.getValue().split(" "))
                                    .filter(originAttrValue -> Arrays.asList(otherAttrValues.split(" ")).contains(originAttrValue))
                                    .flatMapToInt(matchedAttr -> IntStream.of(10))
                                    .sum());

                        }
                );
        return weightSimilarity.get();
    }

    private static Optional<Document> parseFile(File htmlFile) throws IOException {
        Document doc = Jsoup.parse(
                htmlFile,
                CHARSET_NAME,
                htmlFile.getAbsolutePath());
        return Optional.of(doc);
    }

    private static Optional<Element> findElementById(Document htmlDoc, String targetElementId) {
        return Optional.ofNullable(htmlDoc.getElementById(targetElementId));
    }

    private static Optional<Elements> findElementsByQuery(Document htmlDoc, String cssQuery) {
        return Optional.ofNullable(htmlDoc.select(cssQuery));
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
