import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import java.io.IOException;

public class Searcher {
    private final IndexSearcher myIndexSearcher;
    private final QueryHandler myQueryHandler;
    private final Analyzer myAnalyzer;
    private int numberOfHits = 10;

    public Searcher (String indexDirectoryPath)  throws IOException {
        Indexer myIndexer = new Indexer(indexDirectoryPath);
        this.myIndexSearcher = new IndexSearcher((myIndexer.getReader()));
        this.myAnalyzer = myIndexer.getAnalyzer();
        this.myQueryHandler = new QueryHandler(new QueryParser("lyrics", myAnalyzer));
    }
    public void search() throws IOException, ParseException {
        Query myQuery = myQueryHandler.getQuery();
        TopDocs myTopDocs = myIndexSearcher.search(myQuery,numberOfHits);
        System.out.println("The total hits of your query where : " + myTopDocs.totalHits);
        ScoreDoc[] hits = myTopDocs.scoreDocs;
        for (ScoreDoc hit : hits) {
            Document doc = myIndexSearcher.doc(hit.doc);
            System.out.println("Title: " + doc.get("title"));
            System.out.println("Artist: " + doc.get("artist"));
            System.out.println("Lyrics: " + doc.get("lyrics"));
            System.out.println("Score: " + hit.score);
            System.out.println();
        }
    }

}
