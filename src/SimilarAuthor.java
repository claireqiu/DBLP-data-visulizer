import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.gson.Gson;
import com.opencsv.CSVReader;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.tdb.base.file.Location;

import java.io.*;
import java.util.*;

/**
 * Created by clair on 2/14/2017.
 */
public class SimilarAuthor {
    private static SimilarAuthor similarAuthor;
    private Location location;
    private Dataset dataset;
    private Model model;
    private List<QuerySolution> authorList;

    private final String prefixDC = "PREFIX dc: <http://purl.org/dc/elements/1.1/>";
    private final String prefixSWRC = "PREFIX swrc: <http://swrc.ontoware.org/ontology#>";
    private final String prefixRDFS = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>";
    private final String prefixPURL = "PREFIX purl: <http://purl.org/dc/terms/>";
    private final String prefixPURLElem = "PREFIX purlelem: <http://purl.org/dc/elements/1.1/>";
    private final String prefixXSD = "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>";
    private final String prefixRDF = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>";

    public SimilarAuthor() {
        location = Location.create("F:\\3rd_Project\\FacetedDBLP\\Jena\\target\\DBLP_NEW");
        dataset = TDBFactory.createDataset(location);
    }
    private void startTranscation() {
        dataset.begin(ReadWrite.READ); // changed from READ to WRITE
        model = dataset.getDefaultModel();
    }

    public void close() {
        if (dataset != null) {
            dataset.close();
        }
    }

    public void getAllAuthors(){
        long startTime = System.currentTimeMillis();
        startTranscation();
        // Compute all the conferences
        String authoreQuery =  prefixPURLElem +
                "SELECT DISTINCT ?creator WHERE{" +
                "?x purlelem:creator ?creator.}";

        Query query = QueryFactory.create(authoreQuery);
        QueryExecution qexec = QueryExecutionFactory.create(query,model);

        try{
            ResultSet resultSet = qexec.execSelect();
            authorList = ResultSetFormatter.toList(resultSet);
            System.out.println("the size is conferenceset :"+authorList.size());
        }finally{
            qexec.close();
        }
        dataset.end();

        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println("elapsedTime:"+elapsedTime);
    }


    //Compute all the conferences which the author attended
    public void readCSVfile() throws FileNotFoundException {
        long startTime = System.currentTimeMillis();
        System.out.println("Start to compute.....");
        CSVReader reader = new CSVReader(new FileReader("author_conf_pair_new.csv"), ',');
        String [] nextLine;
        Map<String, TreeMap> distributonMutisetsMap = new HashMap<>();
        Map<String, Multiset> allMultisetMap = new HashMap<>();
       // int i = 0;
        try {
            while ((nextLine = reader.readNext()) != null ) {
                // nextLine[] is an array of values from the line
                String localName = nextLine[0];
                // if the author is not contianed in allMultisetMap
                if (!allMultisetMap.containsKey(localName)) {
                    allMultisetMap.put(localName, HashMultiset.create());
                }
                // otherwiase all the values to the existing author
                allMultisetMap.get(localName).add(nextLine[1]);
               // i++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        for (Map.Entry<String, Multiset> entry : allMultisetMap.entrySet())
        {
            String localName = entry.getKey();
            Multiset<String> conferenceSet = entry.getValue();
            int sizeOfConfSet = conferenceSet.size();
            // compute the ditribution
            TreeMap<String, Float> tempDistriMap = new TreeMap<String, Float>();

            //compute the distribution map for each multiset
            for (Multiset.Entry<String> entryMultset : conferenceSet.entrySet()) {
                float temp = (float) entryMultset.getCount() / sizeOfConfSet;
                // System.out.println("the element account is :"+entryMultset.getCount()+"the size is :"+sizeOfConfSet);
                //System.out.println("Element: " + entryMultset.getElement() + ", distribution(s): " +temp);
                tempDistriMap.put(entryMultset.getElement(), temp);
            }
            distributonMutisetsMap.put(localName, tempDistriMap);

        }
        String filename1 = "1503allMultisetMap.txt";
        String filename2 = "1503distributonMutisetsMap .txt";

        outPutJson(distributonMutisetsMap, filename1);
        outPutJson(distributonMutisetsMap, filename2);

        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println("elapsedTime:"+elapsedTime);
        System.out.println("Finish computing.....");

    }

    public void outPutJson(Map map, String filename){
        Gson gson = new Gson();
        String output  = gson.toJson(map);

        BufferedWriter outputstream = null;
        try {
            File file = new File(filename);
            outputstream = new BufferedWriter(new FileWriter(file));
            outputstream.write(output);
        } catch ( IOException e ) {
            e.printStackTrace();
        } finally {
            if ( outputstream != null ) {
                try {
                    outputstream.close();
                }catch ( IOException e ) {
                    e.printStackTrace();
                }
            }
        }
    }



    public static void main(String[]args){
        SimilarAuthor test = new SimilarAuthor();
        try {
            //test.getAllAuthors();
           // test.authorConf();
            test.readCSVfile();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
