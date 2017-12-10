import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MergerFiles {

    public static void main(String[] args) {
        ArrayList<File> files = new ArrayList<>();
        String mergedFilePath = "F:\\3rd_Project\\FacetedDBLP\\conferenceDistribution\\allcreator.txt";
        File mergedFile = new File(mergedFilePath);
        for(int i = 0;i<172;i++) {
            String sourceFile1Path = "F:\\3rd_Project\\FacetedDBLP\\dblp_Jena\\output_author"+i+"_.txt";
           // String sourceFile2Path = "F:\\3rd_Project\\FacetedDBLP\\conferenceDistribution\\Distribution_1.txt";
            files.add(new File(sourceFile1Path));
        }
        mergeFiles(files, mergedFile);
       // Gson gson = new Gson();

//        Map<String, Map<String, Double>> outerMap = new HashMap<>();
//
//        Map<String, Double> innerMap = new HashMap<>();
//
//        String json = "";
//
//        Path path = FileSystems.getDefault().getPath("", "F:\\3rd_Project\\FacetedDBLP\\dblp_Jena\\1602distributonMutisetsMap .txt");
//        try {
//            json = new String(Files.readAllBytes(path));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        outerMap = gson.fromJson(json, new TypeToken<Map<String, Map<String, Double>>>() {
//        }.getType());
//
//        System.out.println("outerMap: " + outerMap.size());

    }

    public static void mergeFiles(ArrayList<File> files, File mergedFile) {

        FileWriter fstream = null;
        BufferedWriter out = null;
        try {
            fstream = new FileWriter(mergedFile, true);
            out = new BufferedWriter(fstream);
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        for (File f : files) {
            System.out.println("merging: " + f.getName());
            FileInputStream fis;
            try {
                fis = new FileInputStream(f);
                BufferedReader in = new BufferedReader(new InputStreamReader(fis));

                String aLine;
                while ((aLine = in.readLine()) != null) {
                    out.write(aLine);
                    out.newLine();
                }
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}