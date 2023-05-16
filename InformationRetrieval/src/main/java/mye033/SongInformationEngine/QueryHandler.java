package mye033.SongInformationEngine;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class QueryHandler {
    private final QueryParser parser;

    public QueryHandler(QueryParser parser)
    {
        this.parser = parser;
    }

    public Query getQuery(String userIn) throws IOException, ParseException
    {
        System.out.print("After the specification of the field, the ':' is required. \nEnter your query:");
        String[] inParts = userIn.split(":");
        String fieldType = inParts[0];
        String phrase = inParts[1].trim();
        String[] tempPhrase = phrase.split(" ");
        //testing
        System.out.println("user input: " + userIn);

        if (tempPhrase.length == 1)
        {
            TermQuery singleQuery = new TermQuery(new Term(fieldType,tempPhrase[0]));
            //testing
            System.out.println("Single query: " + singleQuery);
            return singleQuery;
        }
        else
        {
            PhraseQuery.Builder queryBuilder = new PhraseQuery.Builder();
            for (String s : tempPhrase) {
                queryBuilder.add(new Term(fieldType, s));
            }
            PhraseQuery userPhraseQuery = queryBuilder.build();
            //testing
            System.out.println("Phrase query: " + userPhraseQuery);
            return userPhraseQuery;
        }
    }

    /*
    public static void main(String[] args) throws IOException, ParseException {
        //System.out.println("After the specification of the field the ':' is required. \nEnter your query:");
        Indexer myIndexer = new Indexer("G:\\8th_semester\\Information_Retrieval");
        myIndexer.loadFromCSV("G:\\8th_semester\\Information_Retrieval\\Data\\spotify_millsongdata.csv", myIndexer);
        QueryParser userParser = new QueryParser("lyrics",myIndexer.getAnalyzer());
        QueryHandler userQuery = new QueryHandler(userParser);
        //userQuery.getQuery();
    }
    */
}
