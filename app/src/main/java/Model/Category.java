package Model;

public class Category {
    private String category_id;
    private String name;
    private int status;

    public Category() {
    }

    public Category(String category_id, String name, int status) {
        this.category_id = category_id;
        this.name = name;
        this.status = status;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
