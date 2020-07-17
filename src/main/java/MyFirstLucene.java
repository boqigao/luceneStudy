import jdk.internal.org.objectweb.asm.tree.analysis.Analyzer;
import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.queries.function.valuesource.LongFieldSource;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;
import sun.net.www.protocol.file.FileURLConnection;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MyFirstLucene {

    @Test
    public void testIndex() throws Exception {

        Directory directory = FSDirectory.open(Paths.get("/Users/boqgao/luceneindex"));
        StandardAnalyzer analyzer = new StandardAnalyzer(); //
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter indexWriter = new IndexWriter(directory, config);

        Document document = new Document();

        File f = new File("/Users/boqgao/lucenesource");
        File[] listFiles = f.listFiles();

        for (File file : listFiles) {
            String file_name = file.getName();
            Field fileNameField = new TextField("fileName", file_name, Field.Store.YES);
            long file_size = FileUtils.sizeOf(file);

            Field fileSizeField = new LongPoint("fileSize", file_size);

            String file_path = file.getPath();
            Field filePathField = new StoredField("filePath", file_path);

            String file_content = FileUtils.readFileToString(file);
            Field fileContentField = new TextField("fileContent", file_content, Field.Store.YES);

            document.add(fileNameField);
            document.add(fileSizeField);
            document.add(filePathField);
            document.add(fileContentField);
            indexWriter.addDocument(document);
        }
        indexWriter.close();
    }

    @Test
    public void testSearch() throws Exception {
        Directory directory = FSDirectory.open(Paths.get("/Users/boqgao/luceneindex"));
        IndexReader indexReader = DirectoryReader.open(directory);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        Query query = new TermQuery(new Term("fileContent", "java"));
        TopDocs topDocs = indexSearcher.search(query, 1);
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;

        for (ScoreDoc scoreDoc : scoreDocs) {
            int doc = scoreDoc.doc;
            Document document = indexReader.document(doc);

            String fileName = document.get("fileName");
            System.out.println(fileName);
        }

        indexReader.close();
    }
}
