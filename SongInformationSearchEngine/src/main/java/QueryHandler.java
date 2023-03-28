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

    public Query getQuery() throws IOException, ParseException
    {
        System.out.println("After the specification of the field, the ':' is required. \nEnter your query:");
        String userIn = System.console().readLine();
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
            queryBuilder.add(new Term(fieldType , phrase));
            PhraseQuery userPhraseQuery = queryBuilder.build();
            //testing
            System.out.println("Phrase query: " + userPhraseQuery);
            return userPhraseQuery;
        }
    }

    public static void main(String[] args) {
        System.out.println("After the specification of the field the ':' is required. \nEnter your query:");
    }
}
