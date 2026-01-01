package reiziger;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReizigerDAOPsql implements ReizigerDAO {
    private Connection connection;

    public ReizigerDAOPsql(Connection connection) {
        this.connection = connection;
    }

    @Override
    public boolean save(Reiziger reiziger) {
        try {
            String query = "INSERT INTO reiziger (voorletters, tussenvoegsel, achternaam, geboortedatum) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, reiziger.getVoorletters());
            statement.setString(2, reiziger.getTussenvoegsel());
            statement.setString(3, reiziger.getAchternaam());
            statement.setString(4, reiziger.getGeboortedatum().toString());
            statement.executeUpdate();
            statement.close();
            return true;
        } catch (Exception e) {
            System.err.println("[ReizigerDAOPsql.save] " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(Reiziger reiziger) {
        return false;
    }

    @Override
    public boolean delete(Reiziger reiziger) {
        return false;
    }

    @Override
    public Reiziger findById(Integer reiziger_id) {
        try {
            String query = "SELECT * FROM reiziger WHERE reiziger_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, reiziger_id);
            ResultSet results = statement.executeQuery();

            // TODO: Somehow this returns a warning/error that i should call next
            if(results.next()) {
                Reiziger reiziger = new Reiziger(
                        results.getInt("reiziger_id"),
                        results.getString("voorletters"),
                        results.getString("tussenvoegsel"),
                        results.getString("achternaam"),
                        LocalDate.parse(results.getString("geboortedatum"))
                );
                results.close();
                statement.close();
                return reiziger;
            }
            return null;
        } catch (Exception e) {
            System.err.println("[ReizigerDAOPsql.findById] " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Reiziger> findAll() {
        try {
            String query = "SELECT * FROM reiziger";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet results = statement.executeQuery();

            List<Reiziger> reizigers = new ArrayList<>();
            while (results.next()) {
                Reiziger reiziger = new Reiziger(
                        results.getInt("reiziger_id"),
                        results.getString("voorletters"),
                        results.getString("tussenvoegsel"),
                        results.getString("achternaam"),
                        LocalDate.parse(results.getString("geboortedatum"))
                );
                reizigers.add(reiziger);
            }
            results.close();
            statement.close();

            return reizigers;
        } catch (Exception e) {
            System.err.println("[ReizigerDAOPsql.findAll] " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
