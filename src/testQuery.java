import com.google.common.collect.HashMultiset;
import com.google.common.collect.Interner;
import com.google.common.collect.Multiset;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import org.apache.jena.ext.com.google.common.collect.HashMultimap;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.tdb.base.file.Location;

import java.io.*;
import java.util.*;

/**
 * Created by clair on 12/20/2016.
 */
public class testQuery {

    private Location location;
    private Dataset dataset;
    private Model model;

    private final String prefixDC = "PREFIX dc: <http://purl.org/dc/elements/1.1/>";
    private final String prefixSWRC = "PREFIX swrc: <http://swrc.ontoware.org/ontology#>";
    private final String prefixRDFS = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>";
    private final String prefixPURL = "PREFIX purl: <http://purl.org/dc/terms/>";
    private final String prefixPURLElem = "PREFIX purlelem: <http://purl.org/dc/elements/1.1/>";
    private final String prefixXSD = "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>";

    public testQuery() {
        location = Location.create("F:\\3rd_Project\\FacetedDBLP\\Jena\\target\\DBLP_NEW");
        dataset = TDBFactory.createDataset(location);
    }
    private void startTranscation() {
        dataset.begin(ReadWrite.READ); // changed from READ to WRITE
        model = dataset.getDefaultModel();
    }

    public void executeProceedingQuery() throws Exception {
        long startTime = System.currentTimeMillis();
        startTranscation();

        // query for IJCAI
        String proceedingQuery2 =   prefixSWRC  + prefixPURL +prefixPURLElem+prefixRDFS+
                    "SELECT (COUNT(?inproceeding) AS ?no_inproceedings) ?year \n" +
                    "WHERE{ ?inproceeding  a swrc:InProceedings; \n" +
                    "                        swrc:series ?conf;  \n" +
                    "                        purl:issued ?year .\n" +
                    "        ?conf rdfs:label \"IJCAI\".}\n" +
                    "GROUP BY ?year";

        String authorsQuery = prefixSWRC + prefixRDFS + prefixPURLElem +
                "SELECT distinct ?creator WHERE{" +
                "?inproceeding  a swrc:InProceedings; purlelem:creator ?creator .}";
        String authro = prefixSWRC+
                "SELECT ?author WHERE{" +
                "?author  a swrc:Author .} LIMIT 10";

        String label = prefixRDFS+
                "SELECT ?p ?o  WHERE{" +
                "<http://dblp.l3s.de/d2r/resource/authors/Andr%C3%A9_Seznec> ?p ?o .}" +
                " Limit 10";

        //author name and conference name
        String authorConfQuery =  prefixSWRC + prefixRDFS + prefixPURLElem +
                "SELECT  ?author ?conf  WHERE{" +
                "?x swrc:series ?conf;" +
                "   purlelem:creator ?creator." +
                "?creator rdfs:label ?author." +
                "?conf a swrc:Conference.}" ;

        String authorNumberQuery =  prefixSWRC + prefixRDFS + prefixPURLElem +
                "SELECT (COUNT(distinct ?creator) AS ?numberOfAuthors) WHERE{" +
                "?inproceeding  purlelem:creator  .}";

        // classes
        String types = "SELECT DISTINCT ?class\n" +
                "WHERE { [] a ?class }\n" +
                "ORDER BY ?class\n";
        // Properties
        String properties = "SELECT DISTINCT ?property\n" +
                "WHERE { [] ?property [] }\n" +
                "ORDER BY ?property";


        String swrcseries  =   prefixSWRC  +
                "SELECT DISTINCT ?x ?y " +
                "WHERE{ ?x swrc:series ?y .}";

            Query query = QueryFactory.create(swrcseries);
            QueryExecution qexec = QueryExecutionFactory.create(query, model);

            try {

                ResultSet results = qexec.execSelect();
               //System.out.println(ResultSetFormatter.asText(results));
                File file = new File("swrcseries.csv");
                DataOutputStream stream = new DataOutputStream(new FileOutputStream(file));
                //BufferedWriter outputstream = new BufferedWriter(new FileWriter(file));
                ResultSetFormatter.outputAsCSV(stream,results);
                //List<QuerySolution> templist = ResultSetFormatter.toList(results);
                //System.out.println("The size:"+templist.size());
            } finally {
                qexec.close();
            }
        //}
        dataset.close();
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println("elapsedTime:"+elapsedTime);
    }

    public void excuteAuthor(){
        startTranscation();
        String authorsQuery = prefixSWRC + prefixRDFS + prefixPURLElem  +
                "SELECT distinct ?authorname WHERE{" +
                "?inproceeding  a swrc:InProceedings; purlelem:creator ?creator." +
                "?creator rdfs:label ?authorname}";


        Query query = QueryFactory.create(authorsQuery);
        QueryExecution qexec = QueryExecutionFactory.create(query, model);

        String creator="";
        ArrayList<String> temparray = new ArrayList();
        try {

            ResultSet results = qexec.execSelect();
            // Vector<String> resultsVec = new Vector<String>();
            while (results.hasNext()) {
                QuerySolution soln = results.nextSolution();
                String authorname = soln.get("authorname").toString();
                temparray.add(authorname);
                //resultsVec.add(inproceeding);
            }
            writer(temparray);
            // map_year_numOfinPro.put(temYear, resultsVec.size());
           // System.out.println("The result is:" + temparray);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            qexec.close();
            dataset.end();
        }

    }

        // write the array to a file
    public  void writer(ArrayList<String> datArrayList) throws IOException {
        System.out.println("the size"+ datArrayList.size());
        //File file = null;
        FileWriter fw;
        BufferedWriter bw;
        String filename = "AllAuthorName.txt";
        fw = new FileWriter(new File(filename));
        bw = new BufferedWriter(fw);
        try {
                int datList = datArrayList.size();
                for (int i = 0; i < datList; i++) {
                    bw.write(datArrayList.get(i) + "\n");}
            bw.close();
            }catch (Exception e){
                e.printStackTrace();
            }
    }

    public static void main(String[]args) throws IOException {
        testQuery test = new testQuery();
        try {
            test.executeProceedingQuery();
        } catch (Exception e) {
            e.printStackTrace();
        }
//        long startTime = System.currentTimeMillis();
//        CSVReader reader = new CSVReader(new FileReader("author_conf_pair.csv"), ',', '\"', 1);
//        String[] nextLine;
//        String[] newNextLine = new String[2];
//
//
//        CSVWriter writer =
//                new CSVWriter(new FileWriter("author_conf_pair_new.csv"), ',', CSVWriter.NO_QUOTE_CHARACTER);
//
//
//        int i = 0;
//        try {
//            while ((nextLine = reader.readNext()) != null) {
//                // nextLine[] is an array of values from the line
//                newNextLine[0] = nextLine[0];
//                newNextLine[1]  = nextLine[1].substring(44);
//                writer.writeNext(newNextLine);
//
//            }
//            writer.close();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        long stopTime = System.currentTimeMillis();
//        long elapsedTime = stopTime - startTime;
//        System.out.println("elapsedTime:"+elapsedTime);
//


    }
}

