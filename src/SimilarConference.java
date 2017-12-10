/**
 * Created by clair on 12/20/2016.
 */

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;


import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.common.reflect.TypeToken;
import java.io.*;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import com.google.common.collect.Sets.SetView;
public class SimilarConference {
    private String confernce;

    public SimilarConference(String conf) {
        confernce = conf;
    }

    private int getSimilarConfFileIndex() {
        File file = new File("output_all_conferences.txt");
        int lineNr = 0;
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                lineNr++;
                if (line.contains(confernce)) {
                    break;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Found the line number: " + lineNr);

        int index = 0;
        index = (int) Math.ceil(lineNr / 100);
        System.out.println("The index is  " + index);
        return index;
    }

    private static Map getAllConfeneceAsMultiset(String filename) {
//        String mystring = "";
//        try {
//            JSONParser parser = new JSONParser();
//
//            Object obj = parser.parse(new FileReader(filename));
//            JSONObject jObject = (JSONObject) obj;
//            mystring = jObject.toString();
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        // String jsonString = mystring;
//        HashMap<String, HashMap> map = new Gson().fromJson(mystring, new TypeToken<HashMap<String, HashMap>>() {
//        }.getType());
//       // return map;

        Gson gson = new Gson();

        Map<String, Map<String, Double>> outerMap = new HashMap<>();

        Map<String, Double> innerMap = new HashMap<>();

        String json = "";

        Path path = FileSystems.getDefault().getPath("", filename);
        try {
            json = new String(Files.readAllBytes(path));
        } catch (IOException e) {
            e.printStackTrace();
        }

        outerMap = gson.fromJson(json, new TypeToken<Map<String, Map<String, Double>>>() {
        }.getType());
        return outerMap;
    }

    public Map caculateSimilarConf() {
        long startTime = System.currentTimeMillis();
        // get the index of the file which contains the target conference
        int index = getSimilarConfFileIndex();
        String fileName = "F:\\3rd_Project\\FacetedDBLP\\conferenceDistribution_original\\Distribution_" + index + ".txt";
        Map<String, Map<String, Double>> disMapOfConf = getAllConfeneceAsMultiset(fileName);
        System.out.println("disMapOfConf:"+disMapOfConf);
        //get the distribution of authors of target conference
        Map<String, Double> distributionTargetConf = disMapOfConf.get(confernce);
        // get the set of author of target conference
        Set<String> setOfTargetConf = distributionTargetConf.keySet();

        System.out.println("The target distruion of " + confernce + " is " + distributionTargetConf);

        // TreeMap contains the max value for each of the files(87).
        TreeMap<String, Double> similarMapFile = new TreeMap<>();

        // Loop throught all the files
        for (int i = 0; i < 87; i++) {
            fileName = "F:\\3rd_Project\\FacetedDBLP\\conferenceDistribution_original\\Distribution_" + i + ".txt";
            System.out.println("The file name is:" + fileName);
            Map<String, HashMap> distributionMap = getAllConfeneceAsMultiset(fileName);
            Map<String, Double> allSimilarMap = new HashMap<>();
            for (Map.Entry<String, HashMap> entry : distributionMap.entrySet()) {
                // Map of a conference
                Map<String, Double> tempDistriMap = entry.getValue();
                // the Set of all the conference of one file
                //Set of authors who attended this conference
                Set<String> tempSet = tempDistriMap.keySet();
                // intersection of two sets of conferences
                SetView<String> intersection = Sets.intersection(setOfTargetConf, tempSet);
                double temmSum = 0;
                // compute the similarity value
                for (String s : intersection) {
                    double aValue = distributionTargetConf.get(s);
                    double otherValue = tempDistriMap.get(s);

                    if (aValue > otherValue) {
                        temmSum += otherValue;
                    } else {
                        temmSum += aValue;
                    }
                }
                if (temmSum < 1) {
                    allSimilarMap.put(entry.getKey(), temmSum);
                }
            }
            // The maxium value of one file
            String maxSimilarConf = Collections.max(allSimilarMap.entrySet(), Map.Entry.comparingByValue()).getKey();
            //System.out.println("allSimilarMap"+allSimilarMap);
            //System.out.println("the max value of this file is:" + maxSimilarConf + "and the value is:" + allSimilarMap.get(maxSimilarConf));
            similarMapFile.put(maxSimilarConf, allSimilarMap.get(maxSimilarConf));
        }
        // Calling the method sortByvalues
        Map sortedMap = sortByValues(similarMapFile);

        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println("elapsedTime:" + elapsedTime);
        return sortedMap;

    }

    //Method for sorting the TreeMap based on values
    public static <K, V extends Comparable<V>> Map<K, V> sortByValues ( final Map<K, V> map){
        Comparator<K> valueComparator =
                new Comparator<K>() {
                    public int compare(K k1, K k2) {
                        int compare =
                                map.get(k1).compareTo(map.get(k2));
                        if (compare == 0)
                            return 1;
                        else
                            return compare;
                    }
                };

        Map<K, V> sortedByValues =
                new TreeMap<K, V>(Collections.reverseOrder(valueComparator));
        sortedByValues.putAll(map);
        return sortedByValues;
    }
}
