package adres;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import reiziger.Reiziger;
import reiziger.ReizigerDAO;

public class AdresDAOPsql implements AdresDAO {
    private Connection connection;
    private ReizigerDAO reizigerDAO;

    public AdresDAOPsql(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void setReizigerDAO(ReizigerDAO reizigerDAO) {
        this.reizigerDAO = reizigerDAO;
    }

    @Override
    public boolean save(Adres adres) {
        try {
            String sql = "INSERT INTO adres (adres_id, postcode, huisnummer, straat, woonplaats, reiziger_id) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, adres.getAdres_id());
            statement.setString(2, adres.getPostcode());
            statement.setString(3, adres.getHuisnummer());
            statement.setString(4, adres.getStraat());
            statement.setString(5, adres.getWoonplaats());
            statement.setInt(6, adres.getReiziger().getReiziger_id());
            statement.executeUpdate();

            // TODO: Should reiziger also get an update here where the adres_id is set?

            statement.close();
            return true;
        } catch (Exception e) {
            System.err.println("[AdresDAOPsql.save] " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(Adres adres) {
        try {
            String query = "UPDATE adres SET postcode = ?, huisnummer = ?, straat = ?, woonplaats = ?, reiziger_id = ? WHERE adres_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, adres.getPostcode());
            statement.setString(2, adres.getHuisnummer());
            statement.setString(3, adres.getStraat());
            statement.setString(4, adres.getWoonplaats());
            statement.setInt(5, adres.getReiziger().getReiziger_id());
            statement.setInt(6, adres.getAdres_id());
            statement.executeUpdate();

            statement.close();
            return true;
        } catch (Exception e) {
            System.err.println("[AdresDAOPsql.update] " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(Adres adres) {
        try {
            String query = "DELETE FROM adres WHERE adres_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, adres.getAdres_id());
            statement.executeUpdate();

            statement.close();
            return true;
        } catch (Exception e) {
            System.err.println("[AdresDAOPsql.delete] " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override 
    public Adres findById(Integer adres_id) {
        try {
            String query = "SELECT * FROM adres WHERE adres_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, adres_id);
            ResultSet results = statement.executeQuery();

            if(results.next()) {
                Adres adres = new Adres(
                        results.getInt("adres_id"),
                        results.getString("postcode"),
                        results.getString("huisnummer"),
                        results.getString("straat"),
                        results.getString("woonplaats"),
                        reizigerDAO.findById(results.getInt("reiziger_id"))
                );
                results.close();
                statement.close();
                return adres;
            }

            results.close();
            statement.close();
            return null;
        } catch (Exception e) {
            System.err.println("[AdresDAOPsql.findById] " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Adres findByReiziger(Reiziger reiziger) {
        try {
            String query = "SELECT * FROM adres WHERE reiziger_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, reiziger.getReiziger_id());
            ResultSet results = statement.executeQuery();

            if(results.next()) {
                Adres adres = new Adres(
                        results.getInt("adres_id"),
                        results.getString("postcode"),
                        results.getString("huisnummer"),
                        results.getString("straat"),
                        results.getString("woonplaats"),
                        reiziger
                );
                results.close();
                statement.close();
                return adres;
            }
            
            results.close();
            statement.close();
            return null;
        } catch (Exception e) {
            System.err.println("[AdresDAOPsql.findByReiziger] " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Adres> findAll() {
        try {
            String query = "SELECT * FROM adres";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet results = statement.executeQuery();

            List<Adres> adressen = new ArrayList<>();
            while (results.next()) {
                Adres adres = new Adres(
                        results.getInt("adres_id"),
                        results.getString("postcode"),
                        results.getString("huisnummer"),
                        results.getString("straat"),
                        results.getString("woonplaats"),
                        reizigerDAO.findById(results.getInt("reiziger_id"))
                );
                adressen.add(adres);
            }
            results.close();
            statement.close();

            return adressen;
        } catch(Exception e) {
            System.err.println("[AdresDAOPsql.findAll] " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
