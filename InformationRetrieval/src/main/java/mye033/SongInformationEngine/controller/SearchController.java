package mye033.SongInformationEngine.controller;

import mye033.SongInformationEngine.Indexer;
import mye033.SongInformationEngine.QueryHandler;
import mye033.SongInformationEngine.Searcher;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.swing.text.BadLocationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class SearchController {

    String indexPath = "C:\\Users\\kostas\\Documents\\Sxoli\\4oEtos\\8th_Semester\\InformationRetrieval\\Index";
    String csv = "C:\\Users\\kostas\\Documents\\Sxoli\\4oEtos\\8th_Semester\\InformationRetrieval\\Data\\spotify_millsongdata.csv";

    List<String> standardQuery = new ArrayList<String>();

    private Searcher searcher;

    public SearchController() throws IOException {
        Indexer indexer = new Indexer(indexPath,csv);
        indexer.loadFromCSV(indexer);
        Analyzer analyzer = indexer.getAnalyzer();
        QueryParser parser = new QueryParser("lyrics", analyzer);
        QueryHandler handler = new QueryHandler(parser);
        this.searcher = new Searcher(handler,indexPath,csv);
        this.standardQuery.add("lyrics:");
        this.standardQuery.add("title:");
        this.standardQuery.add("artist:");
    }

    @RequestMapping("/")
    private String redirectToHome(){
        return "redirect:/home";
    }
    @RequestMapping("/home")
    private String loadHomePage(){
        return "/home-page";
    }

    @RequestMapping("/search-results")
    private String loadSearchResults(@RequestParam("query") String userQuery, Model theModel) throws InvalidTokenOffsetsException, IOException, ParseException, BadLocationException {
        boolean res = false;
        for (String s : this.standardQuery) {
            if (userQuery.contains(s)) {
                res = true;
                break;
            }
        }
        if (!res){
            String errorMessage = "Your search didn't return any results. Are you sure you typed it correctly? \n" +
                    "The correct search should be either lyrics, title or artist followed by ':' ";
            theModel.addAttribute("errorMessage", errorMessage);
            return "error-page";
        }
        String finalQuery = userQuery.toLowerCase();
        List<String> highlightedResults = searcher.search(finalQuery);

        theModel.addAttribute("results", highlightedResults);
        return "result-page";

    }
}
