package mye033.SongInformationEngine;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.StoredFields;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.search.highlight.Highlighter;
import javax.swing.text.BadLocationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class Searcher {
    private final IndexSearcher myIndexSearcher;
    private final QueryHandler myQueryHandler;
    private final Analyzer myAnalyzer;

    public final Indexer myIndexer;
    private int numberOfHits = 10;  //isws na to kanoume na pairnei input ap to xrhsth

    //@Autowired
    public Searcher (QueryHandler handler, String indexPath, String csvPath) throws IOException {
        this.myIndexer = new Indexer(indexPath,csvPath);
        this.myIndexSearcher = new IndexSearcher((myIndexer.getReader()));
        this.myAnalyzer = myIndexer.getAnalyzer();
        //this.myQueryHandler = new QueryHandler(new QueryParser("lyrics", myAnalyzer));
        this.myQueryHandler = handler;
    }

    public List<String> search(String query) throws IOException, ParseException, InvalidTokenOffsetsException, BadLocationException {
        Query myQuery = myQueryHandler.getQuery(query);
        TopDocs myTopDocs = myIndexSearcher.search(myQuery,numberOfHits);
        StoredFields storedFields = myIndexSearcher.storedFields();
        int maxFragments = 100000000;

        List<String> highlightedResults = new ArrayList<>();
        Set<String> processedDocs = new HashSet<>();

        for (ScoreDoc hit : myTopDocs.scoreDocs) {
            Document doc = myIndexSearcher.doc(hit.doc);
            String docId = doc.get("id");
            if (processedDocs.contains(docId)) {
                continue; // Skip already processed document
            }
            processedDocs.add(docId);
            String title = doc.get("title");
            String artist = doc.get("artist");
            String lyrics = doc.get("lyrics");

            QueryScorer scorer = new QueryScorer(myQuery);
            SimpleHTMLFormatter formatter = new SimpleHTMLFormatter("<b>", "</b>");

            Highlighter highlighter = new Highlighter(formatter, scorer);
            Fragmenter fragmenter = new SimpleSpanFragmenter(scorer, maxFragments);
            highlighter.setTextFragmenter(fragmenter);

            TokenStream lyricsStream = TokenSources.getTokenStream("lyrics", lyrics, myAnalyzer);
            TextFragment[] lyricsFragments = highlighter.getBestTextFragments(lyricsStream, lyrics, true, maxFragments);

            StringBuilder result = new StringBuilder();
            result.append("<html><body>");
            if (title != null) {
                result.append("<b>Title:</b> ").append(title).append("<br>");
            }
            if (artist != null) {
                result.append("<b>Artist:</b> ").append(artist).append("<br>");
            }
            if (lyricsFragments != null && lyricsFragments.length > 0) {
                result.append("<b>Lyrics:</b> ");
                for (TextFragment fragment : lyricsFragments) {
                    if (fragment != null && fragment.getScore() > 0) {
                        result.append(fragment);
                    }
                }
                result.append("<br>");
            }
            result.append("</body></html>");
            highlightedResults.add(result.toString());
        }
        return highlightedResults;

    }
}
