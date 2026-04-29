package Project;

public class Project{

    public static void main(String[] args) {

        Cattle cow = new Cattle("C001", "Brahman", "Male", "Healthy", "Meat");
        Sheep sheep = new Sheep("S001", "Merino", "Female", "Healthy");

        System.out.println(cow);
        System.out.println(sheep);
    }
}