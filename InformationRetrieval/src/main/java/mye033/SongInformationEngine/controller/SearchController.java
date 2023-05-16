package mye033.SongInformationEngine.controller;

import mye033.SongInformationEngine.Searcher;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.swing.text.BadLocationException;
import java.io.IOException;
import java.util.List;

@Controller
public class SearchController {

    @Autowired
    private Searcher searcher;


    public SearchController(Searcher searcher) throws IOException {
        this.searcher = searcher;
    }
    @RequestMapping("/home")
    private String loadHomePage(){
        return "/home-page";
    }

    @RequestMapping("/search-results")
    private String loadSearchResults(@RequestParam("query") String userQuery, Model theModel) throws InvalidTokenOffsetsException, IOException, ParseException, BadLocationException {

        List<String> highlightedResults = searcher.search(userQuery);
        theModel.addAttribute("results", highlightedResults);
        return "result-page";
    }
}
