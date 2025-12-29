import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class Main {
    public static void main(String[] args) {
        try {
            Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5434/ovchip25", "postgres", "padmin");

            Statement statement = connection.createStatement();
            
            ResultSet results = statement.executeQuery("SELECT * FROM reiziger");

            System.out.println("Alle reizigers:");
            while(results.next()) {
                String naam = "";
                naam += results.getString("voorletters") + ". ";
                if (results.getString("tussenvoegsel") != null) {
                    naam += results.getString("tussenvoegsel") + " ";
                }
                naam += results.getString("achternaam");

                String returnString = "#%d %s (%s)".formatted(
                    results.getInt("reiziger_id"),
                    naam,
                    results.getString("geboortedatum")
                );
                System.out.println(returnString);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}