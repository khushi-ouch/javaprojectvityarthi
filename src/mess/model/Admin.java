package mess.model;

public class Admin extends Person {

    public Admin(int id, String name) {
        super(id, name);
    }

    @Override
    public String getRole() {
        return "ADMIN";
    }
}
