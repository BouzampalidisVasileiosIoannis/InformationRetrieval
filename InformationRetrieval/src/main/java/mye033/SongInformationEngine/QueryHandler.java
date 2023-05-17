package mye033.SongInformationEngine;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

import java.io.IOException;


public class QueryHandler {
    private final QueryParser parser;

    public QueryHandler(QueryParser parser)
    {
        this.parser = parser;
    }

    public Query getQuery(String userIn) throws IOException, ParseException
    {
        String[] inParts = userIn.split(":");
        String fieldType = inParts[0];
        String phrase = inParts[1].trim();
        String[] tempPhrase = phrase.split(" ");

        if (tempPhrase.length == 1)
        {
            TermQuery singleQuery = new TermQuery(new Term(fieldType,tempPhrase[0]));
            return singleQuery;
        }
        else
        {
            PhraseQuery.Builder queryBuilder = new PhraseQuery.Builder();
            for (String s : tempPhrase) {
                queryBuilder.add(new Term(fieldType, s));
            }
            PhraseQuery userPhraseQuery = queryBuilder.build();
            return userPhraseQuery;
        }
    }
}
