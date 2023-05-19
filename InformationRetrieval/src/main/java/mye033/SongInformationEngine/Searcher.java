package mye033.SongInformationEngine;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.StoredFields;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
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
    private int numberOfHits = 15;

    //@Autowired
    public Searcher (QueryHandler handler, String indexPath, String csvPath) throws IOException {
        this.myIndexer = new Indexer(indexPath,csvPath);
        this.myIndexSearcher = new IndexSearcher((myIndexer.getReader()));
        this.myAnalyzer = myIndexer.getAnalyzer();
        this.myQueryHandler = handler;
    }

    public List<String> search(String query) throws IOException, ParseException, InvalidTokenOffsetsException, BadLocationException {
        Query myQuery = myQueryHandler.getQuery(query);
        TopDocs myTopDocs = myIndexSearcher.search(myQuery, numberOfHits);
        System.out.println("The total hits of your query were : " + myTopDocs.totalHits);
        StoredFields storedFields = myIndexSearcher.storedFields();
        int maxFragmentsLyrics = 1000000000;
        int maxFragmentsRest = 10000;

        List<String> highlightedResults = new ArrayList<>();

        for (ScoreDoc hit : myTopDocs.scoreDocs) {
            Document doc = myIndexSearcher.doc(hit.doc);
            String title = doc.get("title");
            String artist = doc.get("artist");
            String lyrics = doc.get("lyrics");

            QueryScorer scorer = new QueryScorer(myQuery);
            SimpleHTMLFormatter formatter = new SimpleHTMLFormatter("<b>", "</b>");

            Highlighter highlighter = new Highlighter(formatter, scorer);
            Fragmenter fragmenter = new SimpleSpanFragmenter(scorer, maxFragmentsLyrics);
            highlighter.setTextFragmenter(fragmenter);

            Highlighter highlighterRest = new Highlighter(formatter, scorer);
            Fragmenter fragmenterRest = new SimpleSpanFragmenter(scorer, maxFragmentsRest);
            highlighterRest.setTextFragmenter(fragmenterRest);


            TokenStream artistStream = TokenSources.getTokenStream("artist", artist, myAnalyzer);
            TextFragment[] artistFragments = highlighterRest.getBestTextFragments(artistStream, artist, true, maxFragmentsRest);


            TokenStream titleStream = TokenSources.getTokenStream("title", title, myAnalyzer);
            TextFragment[] titleFragments = highlighterRest.getBestTextFragments(titleStream, title, true, maxFragmentsRest);


            TokenStream lyricsStream = TokenSources.getTokenStream("lyrics", lyrics, myAnalyzer);
            TextFragment[] lyricsFragments = highlighter.getBestTextFragments(lyricsStream, lyrics, true, maxFragmentsLyrics);

            StringBuilder result = new StringBuilder();
            result.append("<html><body>");
            if (myQuery.toString().startsWith("title:")) {
                if (titleFragments != null && titleFragments.length > 0) {
                    result.append("<b>Title:</b> ");
                    for (TextFragment fragment : titleFragments) {
                        if (fragment != null && fragment.getScore() > 0) {
                            result.append(fragment);
                        }
                    }
                    result.append("<br>");
                    result.append("<b>Artist:</b> ").append(artist).append("<br>");
                    result.append("<b>Lyrics:</b> ").append(lyrics).append("<br>");
                    result.append("<br>");
                }
            }
            else if (myQuery.toString().startsWith("artist:")) {
                if (artistFragments != null && artistFragments.length > 0) {
                    result.append("<b>Artist:</b> ");
                    for (TextFragment fragment : artistFragments) {
                        if (fragment != null && fragment.getScore() > 0) {
                            result.append(fragment);
                        }
                    }
                    result.append("<br>");
                    result.append("<b>Title:</b> ").append(title).append("<br>");
                    result.append("<b>Lyrics:</b> ").append(lyrics).append("<br>");
                    result.append("<br>");
                }
            }
            else if (myQuery.toString().startsWith("lyrics:")){
                if (lyricsFragments != null && lyricsFragments.length > 0) {

                    result.append("<b>Lyrics:</b> ");
                    for (TextFragment fragment : lyricsFragments) {
                        if (fragment != null && fragment.getScore() > 0) {
                            result.append(fragment);
                        }
                    }
                    result.append("<br>");
                    result.append("<b>Artist:</b> ").append(artist).append("<br>");
                    result.append("<b>Title:</b> ").append(title).append("<br>");
                    result.append("<br>");
                }
            }
            result.append("</body></html>");
            highlightedResults.add(result.toString());
        }
        Set<String> uniqueResults = new HashSet<>(highlightedResults);
        highlightedResults.clear();
        highlightedResults.addAll(uniqueResults);
        return highlightedResults;

    }
}
