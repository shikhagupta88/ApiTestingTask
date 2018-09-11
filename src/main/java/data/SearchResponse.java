package data;

import com.sun.xml.internal.ws.developer.Serialization;

import java.util.List;

// This is just a demo response. I have added few fields only.
public class SearchResponse {

    int total;
    List<Restaurants> restaurants;

    public int getTotal() {
        return total;
    }

    public List<Restaurants> getRestaurants() {
        return restaurants;
    }
}
