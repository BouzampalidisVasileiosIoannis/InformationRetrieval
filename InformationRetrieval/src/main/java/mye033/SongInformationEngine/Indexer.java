package mye033.SongInformationEngine;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import au.com.bytecode.opencsv.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;

@Component
public class Indexer {
    private final Directory indexDirectory;
    private final Analyzer analyzer;
    private final IndexWriterConfig indexConfig;
    private IndexWriter writer;
    private final String INDEXDIRECTORYPATH = "G:\\8th_semester\\Information_Retrieval";
    private final String CSVPATH = "G:\\8th_semester\\Information_Retrieval\\Data\\spotify_millsongdata.csv";

    //@Autowired
    public Indexer() throws IOException {
        this.indexDirectory = FSDirectory.open(Paths.get(INDEXDIRECTORYPATH));
        this.analyzer = new StandardAnalyzer();
        this.indexConfig = new IndexWriterConfig(analyzer);
        this.writer = new IndexWriter(indexDirectory, indexConfig);
    }

    public Analyzer getAnalyzer() {
        return analyzer;
    }

    public void addDoc(Document doc) throws IOException {
        //System.out.println("The added doc: " + doc);
        writer.addDocument(doc);
    }

    public String deleteDoc(String id) throws IOException {
        writer.deleteDocuments(new org.apache.lucene.index.Term("id", id));
        System.out.println("The deleted id: " + id);
        return id;
    }
    public void indexerCommit() throws IOException {
        writer.commit();
    }

    public void indexerClose() throws IOException {
        writer.close();
    }

    public IndexWriter getWriter() {
        return writer;
    }

    public DirectoryReader getReader() throws IOException {
        return DirectoryReader.open(indexDirectory);
    }

    public void loadFromCSV(String CSVPATH, Indexer myIndexer) throws IOException {
        try (CSVReader csvReader = new CSVReader(new FileReader(CSVPATH))) {
            String[] line;
            System.out.println("Data import has begun...");
            while ((line = csvReader.readNext()) != null) {
                String artist = line[0];
                String title = line[1];
                String lyrics = line[2];
                Document newDoc = new Document();
                newDoc.add(new TextField("artist", artist, Field.Store.YES));
                newDoc.add(new TextField("title", title, Field.Store.YES));
                newDoc.add(new TextField("lyrics", lyrics, Field.Store.YES));
                myIndexer.addDoc(newDoc);
            }
        }
        myIndexer.indexerCommit();
        myIndexer.indexerClose();
        System.out.println("Data imported successfully!");
    }


    public static void main(String[] args) throws IOException {
        Indexer myIndexer = new Indexer();
        myIndexer.loadFromCSV("G:\\8th_semester\\Information_Retrieval\\Data\\spotify_millsongdata.csv", myIndexer);

    }
}
