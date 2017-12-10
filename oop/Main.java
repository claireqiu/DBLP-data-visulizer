package oop;

import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

/**
 * Created by yin on 08/01/2017.
 */
public class Main {

    public static void main(String[] args) {

        String input = "ICASSP";

//        Scanner scanner = new Scanner(System.in);
//
//        System.out.println("Please input:");

//        String input = scanner.nextLine();

        List<Conference> conferences = new ArrayList<>();

        int numberOfFiles = 1;

        for (int i = 0; i < numberOfFiles; i++) {
            try {
                conferences.addAll(ConferenceUtils.getConferences("Distribution_" + i + ".txt"));
            } catch (IOException | ParseException e) {
                e.printStackTrace();
                // TODO: Do what you should do when there is error

            }
        }

        try {
            Conference target = ConferenceUtils.findConference(input);
            if (target != null) {
                System.out.println("Found target");
                List<Double> similarities = new ArrayList<>();
                for (Conference conference : conferences) {
                    similarities.add(ConferenceUtils.getSimilarity(target, conference));
                }
                System.out.println("Max similarity: " + Collections.max(similarities));
            } else {
                System.out.println("target not found");
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

    }

}
