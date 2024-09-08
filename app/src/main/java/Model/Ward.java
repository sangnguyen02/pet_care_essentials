package Model;

public class Ward {
    private int ward_id;
    private String name;
    private int district_id;

    public Ward() {
    }

    public Ward(int ward_id, String name, int district_id) {
        this.ward_id = ward_id;
        this.name = name;
        this.district_id = district_id;
    }

    public int getWard_id() {
        return ward_id;
    }

    public void setWard_id(int ward_id) {
        this.ward_id = ward_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDistrict_id() {
        return district_id;
    }

    public void setDistrict_id(int district_id) {
        this.district_id = district_id;
    }
}
