/**
 * Created by clair on 12/24/2016.
 */
import com.google.common.collect.HashMultiset;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.tdb.base.file.Location;
import com.google.common.collect.Multiset;
import com.google.gson.Gson;
import java.io.*;
import java.util.*;
public class SimilarProceeding
{
    private static SimilarProceeding similarProceeding;
    private Location location;
    private Dataset dataset;
    private Model model;
    private ArrayList<String> conferenceArray;

    private final String prefixDC = "PREFIX dc: <http://purl.org/dc/elements/1.1/>";
    private final String prefixSWRC = "PREFIX swrc: <http://swrc.ontoware.org/ontology#>";
    private final String prefixRDFS = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>";
    private final String prefixPURL = "PREFIX purl: <http://purl.org/dc/terms/>";
    private final String prefixPURLElem = "PREFIX purlelem: <http://purl.org/dc/elements/1.1/>";
    private final String prefixXSD = "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>";
    private final String prefixRDF = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>";

    public SimilarProceeding() {
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
//    // write the array to a file
//    public  void writer(ArrayList<String> datArrayList) throws IOException {
//        System.out.println("the size"+ datArrayList.size());
//        File file = null;
//        FileWriter fw;
//        BufferedWriter bw;
//        try {
//                int datList = datArrayList.size();
//                //int datList = 460;
//                int iterateTime = datList/100;
//                for (int i = 0; i < iterateTime; i++) {
//                    String filename = "output_"+i+"_.txt";
//                    fw = new FileWriter(new File(filename));
//                    bw = new BufferedWriter(fw);
//                    for(int j = 100*i; j< 100*(i+1);j++) {
//                        bw.write(datArrayList.get(j) + "\n");
//
//                    }
//                    bw.close();
//            }
//                String filename = "output_"+iterateTime+"_.txt";
//                fw = new FileWriter(new File(filename));
//                bw = new BufferedWriter(fw);
//                for(int i=iterateTime*100;i<datList;i++){
//                    bw.write(datArrayList.get(i) + "\n");
//                }
//                bw.close();
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//    }

    public  ArrayList<String> readConferences(int i){
        ArrayList<String> tempArray = new ArrayList<>();
        String FILENAME = "output_"+i+"_.txt";
        try (BufferedReader br = new BufferedReader(new FileReader(FILENAME))) {
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {
                tempArray.add(sCurrentLine);
            }
            //System.out.println(tempArray);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tempArray;
    }

    public void getAllConferences(){
        startTranscation();
        //Set containing all the conference
        conferenceArray =new ArrayList<>();
        // Compute all the conferences
        String conferenceQuery =  prefixSWRC+prefixRDFS +
                "SELECT DISTINCT ?name WHERE{" +
                "?conference a swrc:Conference; rdfs:label ?name.}";

        Query query = QueryFactory.create(conferenceQuery);
        QueryExecution qexec = QueryExecutionFactory.create(query,model);
        System.out.println("the  conferences:");

        try{
            ResultSet resultSet = qexec.execSelect();
            //ResultSetFormatter.out(resultSet) ;
            while(resultSet.hasNext()){
                QuerySolution soln = resultSet.nextSolution();
                String confereceName = soln.get("name").toString();
                conferenceArray.add(confereceName);
            }
        }finally{
            qexec.close();
        }
        dataset.end();
        System.out.println("the size is conferenceset :"+conferenceArray.size());
    }

    //Compute all the authors who attended
    public void confAuthor() throws Exception{
        System.out.println("Start to compute.....");
        //getAllConferences();
        //countthrougth 1-86
       // int numOfFile = 11;
        for(int numOfFile = 85;numOfFile<87;numOfFile++) {
            conferenceArray = readConferences(numOfFile);
            Map<String, TreeMap> distributonMutisetsMap = new HashMap<>();
            Map<String, Multiset> allMultisetMap = new HashMap<>();
            for (int i = 0; i < conferenceArray.size(); i++) {
                System.out.println("count....." + i);
                startTranscation();
                // multiset containing all the authors
                Multiset<String> authorSet = HashMultiset.create();
                // Compute all the authors
                String authorsQuery = prefixSWRC + prefixRDFS + prefixPURLElem +
                        "SELECT ?creator WHERE{" +
                        "?inproceeding  a swrc:InProceedings; swrc:series ?conf; purlelem:creator ?creator ." +
                        "?conf rdfs:label \"" + conferenceArray.get(i) + "\".}";
                //Query query = QueryFactory.create(authorsQuery);
                QueryExecution qexec1 = QueryExecutionFactory.create(authorsQuery, model);
                //System.out.println("the authors who attended "+conferenceArray.get(i)+"conference:");

                // compute the ditribution
                TreeMap<String, Float> tempDistriMap = new TreeMap<String, Float>();
                try {
                    ResultSet resultSet = qexec1.execSelect();
                    //ResultSetFormatter.out(resultSet) ;
                    while (resultSet.hasNext()) {
                        QuerySolution soln = resultSet.nextSolution();
                        Resource creatorUrl = soln.get("creator").asResource();
                        String localName = creatorUrl.getLocalName();
                        if (!localName.equals(null) && !localName.equals("")) {
                            authorSet.add(localName);
                        }
                    }
                    int sizeOfAuthorSet = authorSet.size();

                    //compute the distribution map for each multiset
                    for (Multiset.Entry<String> entryMultset : authorSet.entrySet()) {
                        float temp = (float) entryMultset.getCount() / sizeOfAuthorSet;
                        // System.out.println("the element account is :"+entryMultset.getCount()+"the size is :"+sizeOfAuthorSet);
                        //System.out.println("Element: " + entryMultset.getElement() + ", distribution(s): " +temp);
                        tempDistriMap.put(entryMultset.getElement(), temp);
                    }
                } finally {
                    qexec1.close();
                    dataset.end();
                }
                // System.out.println("the author are :"+authorSet.toString());
                distributonMutisetsMap.put(conferenceArray.get(i), tempDistriMap);
                allMultisetMap.put(conferenceArray.get(i), authorSet);
                // System.out.println("the distribution is  :"+tempDistriMap);
            }
            System.out.println("Finish computing.....");
            String filename1 = "Distribution_" + numOfFile + ".txt";
            String filename2 = "ConfAuthorMultiset_" + numOfFile + ".txt";
            outPutJson(distributonMutisetsMap, filename1);
            outPutJson(allMultisetMap, filename2);
        }
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

    public static void main(String[] args){
        similarProceeding = new SimilarProceeding();
        try {
            similarProceeding.confAuthor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
