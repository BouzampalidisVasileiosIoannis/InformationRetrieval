package mye033.InformationRetrieval;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.StoredFields;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.search.highlight.Highlighter;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import java.awt.*;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;


public class Searcher {
    private final IndexSearcher myIndexSearcher;
    private final QueryHandler myQueryHandler;
    private final Analyzer myAnalyzer;

    protected final Indexer myIndexer;
    private int numberOfHits = 10;  //isws na to kanoume na pairnei input ap to xrhsth

    public Searcher (String indexDirectoryPath)  throws IOException {
        this.myIndexer = new Indexer(indexDirectoryPath);
        this.myIndexSearcher = new IndexSearcher((myIndexer.getReader()));
        this.myAnalyzer = myIndexer.getAnalyzer();
        this.myQueryHandler = new QueryHandler(new QueryParser("lyrics", myAnalyzer));
    }
    public String search(String query, HighlightedTextPane textPane) throws IOException, ParseException, InvalidTokenOffsetsException, BadLocationException {
        Query myQuery = myQueryHandler.getQuery(query);
        TopDocs myTopDocs = myIndexSearcher.search(myQuery,numberOfHits);
        System.out.println("The total hits of your query were : " + myTopDocs.totalHits);
        StoredFields storedFields = myIndexSearcher.storedFields();
        int maxFragments = 100000000;

        StringBuilder highlightedResults = new StringBuilder();

        for (ScoreDoc hit : myTopDocs.scoreDocs) {
            Document doc = myIndexSearcher.doc(hit.doc);
            String title = doc.get("title");
            String artist = doc.get("artist");
            String lyrics = doc.get("lyrics");

            QueryScorer scorer = new QueryScorer(myQuery);
            SimpleHTMLFormatter formatter = new SimpleHTMLFormatter("<b>", "</b>");

            Highlighter highlighter = new Highlighter(formatter, scorer);
            //highlighter.setTextFragmenter(new SimpleFragmenter(100));
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
            highlightedResults.append(result);
        }
        return highlightedResults.toString();
    }


    public static void main(String[] args) throws IOException, ParseException, InvalidTokenOffsetsException {
        Searcher userSearch = new Searcher("G:\\8th_semester\\Information_Retrieval");
        userSearch.myIndexer.loadFromCSV("G:\\8th_semester\\Information_Retrieval\\Data\\spotify_millsongdata.csv",userSearch.myIndexer);
        //userSearch.search();
    }
}
