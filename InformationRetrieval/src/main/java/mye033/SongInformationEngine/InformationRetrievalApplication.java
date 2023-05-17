package mye033.SongInformationEngine;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class InformationRetrievalApplication {

	public static void main(String[] args) throws IOException { SpringApplication.run(InformationRetrievalApplication.class, args);
	}

}
