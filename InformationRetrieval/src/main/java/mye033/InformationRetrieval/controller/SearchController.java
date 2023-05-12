package mye033.InformationRetrieval.controller;

import mye033.InformationRetrieval.Searcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SearchController {

    @Autowired
    private Searcher searcher;


    public SearchController(Searcher searcher){
        this.searcher = searcher;
    }
    @RequestMapping("/home")
    private String loadHomePage(){
        searcher.
        return "/home-page";
    }

    @RequestMapping("/search-results")
    private String loadSearchResults(){

        return "/search-results";
    }
}
