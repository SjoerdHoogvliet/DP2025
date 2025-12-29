import reiziger.Reiziger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        try {
            Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5434/ovchip25", "postgres", "padmin");

            Statement statement = connection.createStatement();
            
            ResultSet results = statement.executeQuery("SELECT * FROM reiziger");

            System.out.println("Alle reizigers:");
            while(results.next()) {
                Reiziger reiziger = new Reiziger(
                    results.getInt("reiziger_id"),
                    results.getString("voorletters"),
                    results.getString("tussenvoegsel"),
                    results.getString("achternaam"),
                    LocalDate.parse(results.getString("geboortedatum"))
                );
                System.out.println(reiziger);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}