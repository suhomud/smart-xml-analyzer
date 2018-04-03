package agile.angine.com.selectors;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Optional;

public class DocumentSelector implements Selector {

    @Override
    public Optional<Element> selectById(String id, Document from) {
        return Optional.ofNullable(from.getElementById(id));
    }

    @Override
    public Optional<Elements> selectByQuery(String query, Document from) {
        return Optional.ofNullable(from.select(query));
    }

}
