package agile.angine.com.selectors;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Optional;

public interface Selector {

    Optional<Element> selectById(String id, Document from);

    Optional<Elements> selectByQuery(String query, Document from);

}
