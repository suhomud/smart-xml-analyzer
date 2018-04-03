package agile.angine.com.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class DocumentParser {

    private static final String CHARSET_NAME = "utf8";

    public static Optional<Document> toDocument(File file) throws IOException {
        Document doc = Jsoup.parse(
                file,
                CHARSET_NAME,
                file.getAbsolutePath());
        return Optional.of(doc);
    }

}
