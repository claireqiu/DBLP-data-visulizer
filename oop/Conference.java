package oop;

import java.util.List;
import java.util.Map;

/**
 * Created by yin on 08/01/2017.
 */
public class Conference {

    private String name;

    private List<String> authors;

    private Map<String, Double> distributions;

    public Conference(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }

    public Map<String, Double> getDistributions() {
        return distributions;
    }

    public void setDistributions(Map<String, Double> distributions) {
        this.distributions = distributions;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Conference) {
            return ((Conference) obj).name.equals(name);
        }
        return super.equals(obj);
    }
}
