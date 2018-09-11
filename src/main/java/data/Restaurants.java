package data;

public class Restaurants {

    int id;
    String name;
    boolean foodora;
    String city;
    String address;
    Address address_components;
    int star_rank;
    boolean now_open;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isFoodora() {
        return foodora;
    }

    public String getCity() {
        return city;
    }

    public String getAddress() {
        return address;
    }

    public Address getAddress_components() {
        return address_components;
    }

    public int getStar_rank() {
        return star_rank;
    }

    public boolean isNow_open() {
        return now_open;
    }
}
