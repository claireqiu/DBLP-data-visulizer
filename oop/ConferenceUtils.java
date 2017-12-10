package oop;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by yin on 08/01/2017.
 */
public class ConferenceUtils {

    /**
     * Get a list of conferences from distribution file, which means the authors list of each
     * conference is <b>not</b> multiset.
     *
     * @param fromFile distribution file name
     * @return a list of conferences
     */
    public static List<Conference> getConferences(String fromFile) throws IOException, ParseException {

        System.out.println("Reading conferences from: " + fromFile);

        List<Conference> conferenceList = new ArrayList<>();

        JSONParser parser = new JSONParser();

        // Read the file into json string
        Object obj = parser.parse(new FileReader(fromFile));
        JSONObject jObject = (JSONObject) obj;
        String jsonString = jObject.toString();

        // Read the json string into map
        HashMap<String, TreeMap<String, Double>> map = new Gson().fromJson(jsonString, new TypeToken<HashMap<String, TreeMap<String, Double>>>() {
        }.getType());

        // add conferences one by one
        for (String conferenceName : map.keySet()) {
            Conference conference = new Conference(conferenceName);

            conference.setDistributions(map.get(conferenceName));
            conference.setAuthors(new ArrayList<>(conference.getDistributions().keySet()));

            conferenceList.add(conference);
        }

        return conferenceList;
    }

    public static double getSimilarity(Conference conf1, Conference conf2) {

        double result = 0;

        Map<String, Double> distribution1 = conf1.getDistributions();
        Map<String, Double> distribution2 = conf2.getDistributions();

        for (String author : conf1.getAuthors()) {

            result += conf2.getAuthors().contains(author) ?
                    Math.min(distribution1.get(author), distribution2.get(author)) : 0;

        }

        return result;
    }

    /**
     * Find the conference instance with given conference name
     * @param conferenceName name of the conference to be found
     * @return the instance of conference, null if not found
     */
    public static Conference findConference(String conferenceName) throws IOException, ParseException {

        File file = new File("output_all_conferences.txt");
        boolean found = false;
        int lineNr = 0;
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                lineNr ++;
                if (line.contains(conferenceName)) {
                    found = true;
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (found) {
            System.out.println("Found the line number: " + lineNr);

            int index = 0;
            index = (int)Math.ceil(lineNr/100);
            System.out.println("The index is  " + index);

            List<Conference> conferences = ConferenceUtils.getConferences("Distribution_" + index + ".txt");

            return conferences.get(conferences.indexOf(new Conference(conferenceName)));
        } else {
            return null;
        }
    }
}
