package reiziger;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import adres.AdresDAO;

public class ReizigerDAOPsql implements ReizigerDAO {
    private Connection connection;
    private AdresDAO adresDAO;

    public ReizigerDAOPsql(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void setAdresDAO(AdresDAO adresDAO) {
        this.adresDAO = adresDAO;
    }

    @Override
    public boolean save(Reiziger reiziger) {
        try {
            String query = "INSERT INTO reiziger (reiziger_id, voorletters, tussenvoegsel, achternaam, geboortedatum, adres_id) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, reiziger.getReiziger_id());
            statement.setString(2, reiziger.getVoorletters());
            statement.setString(3, reiziger.getTussenvoegsel());
            statement.setString(4, reiziger.getAchternaam());
            statement.setDate(5, Date.valueOf(reiziger.getGeboortedatum()));
            // If the reiziger has no adres, set the adres_id to null, otherwise set it to the adres_id of the adres
            statement.setInt(6, reiziger.getAdres() == null ? null : reiziger.getAdres().getAdres_id());
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
        try {
            String query = "UPDATE reiziger SET voorletters = ?, tussenvoegsel = ?, achternaam = ?, geboortedatum = ?, adres_id = ? WHERE reiziger_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, reiziger.getVoorletters());
            statement.setString(2, reiziger.getTussenvoegsel());
            statement.setString(3, reiziger.getAchternaam());
            statement.setDate(4, Date.valueOf(reiziger.getGeboortedatum()));
            statement.setInt(5, reiziger.getReiziger_id());
            // If the reiziger has no adres, set the adres_id to null, otherwise set it to the adres_id of the adres
            statement.setInt(6, reiziger.getAdres() == null ? null : reiziger.getAdres().getAdres_id());
            statement.executeUpdate();
            statement.close();
            return true;
        } catch (Exception e) {
            System.err.println("[ReizigerDAOPsql.update] " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(Reiziger reiziger) {
        try {
            String query = "DELETE FROM reiziger WHERE reiziger_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, reiziger.getReiziger_id());
            statement.executeUpdate();
            statement.close();
            return true;
        } catch (Exception e) {
            System.err.println("[ReizigerDAOPsql.delete] " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Reiziger findById(Integer reiziger_id) {
        try {
            String query = "SELECT * FROM reiziger WHERE reiziger_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, reiziger_id);
            ResultSet results = statement.executeQuery();

            if(results.next()) {
                Reiziger reiziger = new Reiziger(
                        results.getInt("reiziger_id"),
                        results.getString("voorletters"),
                        results.getString("tussenvoegsel"),
                        results.getString("achternaam"),
                        LocalDate.parse(results.getString("geboortedatum"))
                );

                // If the reiziger has an adres, get the adres_id and set it to the adres
                if (results.getInt("adres_id") != 0) {
                    reiziger.setAdres(adresDAO.findById(results.getInt("adres_id")));
                }

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
    public List<Reiziger> findByGbdatum(LocalDate geboortedatum) {
        try {
            String query = "SELECT * FROM reiziger WHERE geboortedatum = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setDate(1, Date.valueOf(geboortedatum));
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
                
                // If the reiziger has an adres, get the adres_id and set it to the adres
                if (results.getInt("adres_id") != 0) {
                    reiziger.setAdres(adresDAO.findById(results.getInt("adres_id")));
                }

                reizigers.add(reiziger);
            }
            results.close();
            statement.close();

            return reizigers;
        } catch (Exception e) {
            System.err.println("[ReizigerDAOPsql.findByGbdatum] " + e.getMessage());
            e.printStackTrace();
            return null;
        }
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

                // If the reiziger has an adres, get the adres_id and set it to the adres
                if (results.getInt("adres_id") != 0) {
                    reiziger.setAdres(adresDAO.findById(results.getInt("adres_id")));
                }

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
