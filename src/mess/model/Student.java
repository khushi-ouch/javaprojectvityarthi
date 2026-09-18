package mess.model;

public class Student extends Person {

    private String regNo;
    private String hostelBlock;

    public Student(int id, String name, String regNo, String hostelBlock) {
        super(id, name);
        this.regNo = regNo;
        this.hostelBlock = hostelBlock;
    }

    public String getRegNo() {
        return regNo;
    }

    public String getHostelBlock() {
        return hostelBlock;
    }

    @Override
    public String getRole() {
        return "STUDENT";
    }

    @Override
    public String toString() {
        return super.toString() + " regNo=" + regNo + " block=" + hostelBlock;
    }
}
