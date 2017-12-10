import com.google.common.collect.HashMultiset;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.tdb.base.file.Location;
import com.google.common.collect.Multiset;
import java.util.*;


/**
 * Created by clair on 12/5/2016.
 */
public class ReadTransaction extends TreeMap<String, Integer> {
    private static ReadTransaction transaction;
    private Location location;
    private Dataset dataset;
    private Model model;
   // private ArrayList<String> conferenceArray;

    private final String prefixDC = "PREFIX dc: <http://purl.org/dc/elements/1.1/>";
    private final String prefixSWRC = "PREFIX swrc: <http://swrc.ontoware.org/ontology#>";
    private final String prefixRDFS = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>";
    private final String prefixPURL = "PREFIX purl: <http://purl.org/dc/terms/>";
    //private final String prefixPURLElem = "PREFIX purlelem: <http://purl.org/dc/elements/1.1/>";
    private final String prefixXSD = "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>";
    //private final String prefixRDF = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>";

    public TreeMap<String, Integer> map_year_numOfinPro;
    public ArrayList<String> years;

    public ReadTransaction() {
        location = Location.create("F:\\3rd_Project\\FacetedDBLP\\Jena\\target\\DBLP_NEW");
        dataset = TDBFactory.createDataset(location);
    }


    public void startTranscation() {
        dataset.begin(ReadWrite.READ); // changed from READ to WRITE
        model = dataset.getDefaultModel();
    }

    public void close() {
        if (dataset != null) {
            dataset.close();
        }
    }

    public void executeProceedingQuery(String proceeding) throws Exception {

        //String[] years = new String[]{"2008","2009","2010","2011","2012","2013","2014","2015","2016"};

        map_year_numOfinPro = new TreeMap<String, Integer>();
        String proceedingQuery = prefixDC + prefixSWRC + prefixRDFS + prefixPURL + prefixXSD +
                "SELECT (COUNT(?inproceeding) AS ?no_inproceedings) ?year WHERE{" +
                "?inproceeding  a swrc:InProceedings; swrc:series ?conf; purl:issued ?year ." +
                "?conf rdfs:label \""+proceeding+"\".}" +
                "GROUP BY ?year";

        Query query = QueryFactory.create(proceedingQuery);
        QueryExecution qexec = QueryExecutionFactory.create(query, model);
        int numOfInProceedings = 0;
        String year="";
        try {

            ResultSet results = qexec.execSelect();
            // System.out.println(ResultSetFormatter.asText(results));
            while (results.hasNext()) {
                QuerySolution soln = results.nextSolution();
                String tempString = soln.get("no_inproceedings").toString();
                System.out.println("tempStringp :"+tempString);
                String no_inproceedings =tempString.substring(0,tempString.indexOf("^"));
                numOfInProceedings =  Integer.parseInt(no_inproceedings);
                year = soln.get("year").asLiteral().getString();
                System.out.println("year :"+year);
                //System.out.println("The year is:"+year);
                //System.out.println("The number of inproceedings is:"+numOfInProceedings);
                map_year_numOfinPro.put(year, numOfInProceedings);
            }
            System.out.println("The map :"+map_year_numOfinPro);
        } finally {
            qexec.close();
        }

        dataset.end();
        System.out.println("Start to generate the graph");
    }

    public TreeMap<String, Integer> getYearNumberOfInProc() {
        return map_year_numOfinPro;
    }

    public ArrayList<String> getYears(String yearsDuration){
        years = new ArrayList<String>();
        String[] temp = yearsDuration.split("-");
        for (int year = Integer.parseInt(temp[0]); year <= Integer.parseInt(temp[1]); year++) {
            String temYear = Integer.toString(year);
            System.out.println("temYear=========" + temYear);
            years.add(temYear);
        }

        return years;
    }
}


