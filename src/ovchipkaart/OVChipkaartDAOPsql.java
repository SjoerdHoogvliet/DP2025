package ovchipkaart;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import product.Product;
import product.ProductDAO;
import reiziger.Reiziger;
import reiziger.ReizigerDAO;

public class OVChipkaartDAOPsql implements OVChipkaartDAO {
    private Connection connection;
    private ReizigerDAO reizigerDAO;
    private ProductDAO productDAO;

    public OVChipkaartDAOPsql(Connection connection) {
        this.connection = connection;
    }

    
    @Override
    public void setReizigerDAO(ReizigerDAO reizigerDAO) {
        this.reizigerDAO = reizigerDAO;
    }

    @Override
    public void setProductDAO(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }

    @Override
    public boolean save(OVChipkaart ovChipkaart) {
        try {
            String query = "INSERT INTO ov_chipkaart (kaart_nummer, geldig_tot, klasse, saldo, reiziger_id) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, ovChipkaart.getKaartNummer());
            statement.setDate(2, Date.valueOf(ovChipkaart.getGeldigTot()));
            statement.setInt(3, ovChipkaart.getKlasse());
            statement.setFloat(4, ovChipkaart.getSaldo());
            statement.setInt(5, ovChipkaart.getReiziger().getReiziger_id());
            statement.executeUpdate();

            Reiziger reizigerToUpdate = ovChipkaart.getReiziger();
            // Usually the OV Chipkaart is already set, however by doing this we are certain
            reizigerToUpdate.addOVChipkaart(ovChipkaart);
            reizigerDAO.update(reizigerToUpdate);

            if (ovChipkaart.getProducten() != null) {
                for (Product product : ovChipkaart.getProducten()) {
                    // We are certain there is no conflict here as we just created the product, 
                    // NOTE: status is nullable and we have no logic that will check whether the bought product is active or not therefore no status insert is done
                    String relationQuery = "INSERT INTO ov_chipkaart_product (kaart_nummer,product_nummer last_update) VALUES (?, ?, ?)";
                    PreparedStatement relationStatement = connection.prepareStatement(relationQuery);
                    relationStatement.setInt(1, ovChipkaart.getKaartNummer());
                    relationStatement.setInt(2, product.getProductNummer());
                    relationStatement.setDate(3, Date.valueOf(LocalDate.now()));
                    relationStatement.executeUpdate();
                    relationStatement.close();
                }
            }

            statement.close();
            return true;
        } catch (Exception e) {
            System.err.println("[OVChipkaartDAOPsql.save] " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(OVChipkaart ovChipkaart) {
        try {
            String query = "UPDATE ov_chipkaart SET geldig_tot = ?, klasse = ?, saldo = ? WHERE kaart_nummer = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setDate(1, Date.valueOf(ovChipkaart.getGeldigTot()));
            statement.setInt(2, ovChipkaart.getKlasse());
            statement.setFloat(3, ovChipkaart.getSaldo());
            statement.setInt(4, ovChipkaart.getKaartNummer());
            statement.executeUpdate();

            if (ovChipkaart.getProducten() != null) {
                for (Product product : ovChipkaart.getProducten()) {
                    // If we already inserted this relation it will conflict on the two primary keys, then we can update the last_update
                    String relationQuery = "INSERT INTO ov_chipkaart_product (kaart_nummer,product_nummer, last_update) VALUES (?, ?, ?) ON CONFLICT(kaart_nummer,product_nummer) DO UPDATE SET last_update = ?";
                    PreparedStatement relationStatement = connection.prepareStatement(relationQuery);
                    relationStatement.setInt(1, ovChipkaart.getKaartNummer());
                    relationStatement.setInt(2, product.getProductNummer());
                    relationStatement.setDate(3, Date.valueOf(LocalDate.now()));
                    relationStatement.setDate(4, Date.valueOf(LocalDate.now()));
                    relationStatement.executeUpdate();
                    relationStatement.close();
                }
            }

            statement.close();
            return true;
        } catch (Exception e) {
            System.err.println("[OVChipkaartDAOPsql.update] " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(OVChipkaart ovChipkaart) {
        try {
            // Delete the Product relations first
            String relationQuery = "DELETE FROM ov_chipkaart_product WHERE kaart_nummer = ?";
            PreparedStatement relationStatement = connection.prepareStatement(relationQuery);
            relationStatement.setInt(1, ovChipkaart.getKaartNummer());
            relationStatement.executeUpdate();
            relationStatement.close();

            String query = "DELETE FROM ov_chipkaart WHERE kaart_nummer = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, ovChipkaart.getKaartNummer());
            statement.executeUpdate();
            
            Reiziger reizigerToUpdate = ovChipkaart.getReiziger();
            // Make sure the OV Chipkaart is removed from reiziger
            reizigerToUpdate.removeOVChipkaart(ovChipkaart);
            reizigerDAO.update(reizigerToUpdate);
            
            statement.close();
            return true;
        } catch (Exception e) {
            System.err.println("[OVChipkaartDAOPsql.delete] " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public OVChipkaart findByKaartNummer(Integer kaartNummer) {
        try {
            String query = "SELECT * FROM ov_chipkaart WHERE kaart_nummer = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, kaartNummer);
            ResultSet results = statement.executeQuery();

            if(results.next()) {
                OVChipkaart ovChipkaart = new OVChipkaart(
                        results.getInt("kaart_nummer"),
                        LocalDate.parse(results.getString("geldig_tot")),
                        results.getInt("klasse"),
                        results.getFloat("saldo"),
                        reizigerDAO.findById(results.getInt("reiziger_id"))
                );

                ovChipkaart.setProducten(productDAO.findByOVChipkaart(ovChipkaart, false));
                
                results.close();
                statement.close();
                return ovChipkaart;
            }
            return null;
        } catch (Exception e) {
            System.err.println("[OVChipkaartDAOPsql.findById] " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<OVChipkaart> findByProduct(Product product, boolean includeProducten) {
        try {
            String query = "SELECT * FROM ov_chipkaart oc LEFT JOIN ov_chipkaart_product ocp ON ocp.kaart_nummer = oc.kaart_nummer WHERE ocp.product_nummer = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, product.getProductNummer());
            ResultSet results = statement.executeQuery();

            List<OVChipkaart> ovChipkaarten = new ArrayList<>();
            while (results.next()) {
                OVChipkaart ovChipkaart = new OVChipkaart(
                        results.getInt("kaart_nummer"),
                        LocalDate.parse(results.getString("geldig_tot")),
                        results.getInt("klasse"),
                        results.getFloat("saldo"),
                        reizigerDAO.findById(results.getInt("reiziger_id"))
                );

                if(includeProducten) {
                    ovChipkaart.setProducten(productDAO.findByOVChipkaart(ovChipkaart, false));
                }

                ovChipkaarten.add(ovChipkaart);
            }

            results.close();
            statement.close();
            return ovChipkaarten;
        } catch (Exception e) {
            System.err.println("[OVChipkaartDAOPsql.findByProduct] " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<OVChipkaart> findByReiziger(Reiziger reiziger) {
        try {
            String query = "SELECT * FROM ov_chipkaart WHERE reiziger_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, reiziger.getReiziger_id());
            ResultSet results = statement.executeQuery();

            List<OVChipkaart> ovChipkaarten = new ArrayList<>();
            while (results.next()) {
                OVChipkaart ovChipkaart = new OVChipkaart(
                        results.getInt("kaart_nummer"),
                        LocalDate.parse(results.getString("geldig_tot")),
                        results.getInt("klasse"),
                        results.getFloat("saldo"),
                        reiziger
                );

                ovChipkaart.setProducten(productDAO.findByOVChipkaart(ovChipkaart, false));
                
                ovChipkaarten.add(ovChipkaart);
            }

            results.close();
            statement.close();
            return ovChipkaarten;
        } catch (Exception e) {
            System.err.println("[OVChipkaartDAOPsql.findByReiziger] " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<OVChipkaart> findAll() {
        try {
            String query = "SELECT * FROM ov_chipkaart";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet results = statement.executeQuery();

            List<OVChipkaart> ovChipkaarten = new ArrayList<>();
            while (results.next()) {
                OVChipkaart ovChipkaart = new OVChipkaart(
                        results.getInt("kaart_nummer"),
                        LocalDate.parse(results.getString("geldig_tot")),
                        results.getInt("klasse"),
                        results.getFloat("saldo"),
                        reizigerDAO.findById(results.getInt("reiziger_id"))
                );

                ovChipkaart.setProducten(productDAO.findByOVChipkaart(ovChipkaart, false));

                ovChipkaarten.add(ovChipkaart);
            }

            results.close();
            statement.close();
            return ovChipkaarten;
        } catch(Exception e) {
            System.err.println("[OVChipkaartDAOPsql.findAll] " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
