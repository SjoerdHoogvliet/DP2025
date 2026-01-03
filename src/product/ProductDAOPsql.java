package product;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import ovchipkaart.OVChipkaart;

public class ProductDAOPsql implements ProductDAO {
    private Connection connection;

    public ProductDAOPsql(Connection connection) {
        this.connection = connection;
    }

    // Method for getting all OV Chipkaart nummers for a product
    public List<Integer> getAllOVChipkaartNummersForProduct(Integer product_nummer) {
        try {
            String query = "SELECT kaart_nummer FROM ov_chipkaart_product WHERE product_nummer = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, product_nummer);
            ResultSet results = statement.executeQuery();

            List<Integer> ids = new ArrayList<>();
            while (results.next()) {
                ids.add(results.getInt("kaart_nummer"));
            }
            results.close();
            statement.close();

            return ids;
        } catch (Exception e) {
            System.err.println("[ProductDAOPsql.getAllOVChipkaartIds] " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean save(Product product) {
        try {
            String query = "INSERT INTO product (product_nummer, naam, beschrijving, prijs) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, product.getProductNummer());
            statement.setString(2, product.getNaam());
            statement.setString(3, product.getBeschrijving());
            statement.setFloat(4, product.getPrijs());
            statement.executeUpdate();

            // Persist the relation with the OV Chipkaart
            if (product.getOVChipkaartenNummers() != null) {
                for (Integer ovChipkaartNummer : product.getOVChipkaartenNummers()) {
                    // We are certain there is no conflict here as we just created the product, 
                    // NOTE: status is nullable and we have no logic that will check whether the bought product is active or not therefore no status insert is done
                    String relationQuery = "INSERT INTO ov_chipkaart_product (kaart_nummer,product_nummer, last_update) VALUES (?, ?, ?)";
                    PreparedStatement relationStatement = connection.prepareStatement(relationQuery);
                    relationStatement.setInt(1, ovChipkaartNummer);
                    relationStatement.setInt(2, product.getProductNummer());
                    relationStatement.setDate(3, Date.valueOf(LocalDate.now()));
                    relationStatement.executeUpdate();
                    relationStatement.close();
                }
            }

            statement.close();
            return true;
        } catch (Exception e) {
            System.err.println("[ProductDAOPsql.save] " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(Product product) {
        try {
            String query = "UPDATE product SET naam = ?, beschrijving = ?, prijs = ? WHERE product_nummer = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, product.getNaam());
            statement.setString(2, product.getBeschrijving());
            statement.setFloat(3, product.getPrijs());
            statement.setInt(4, product.getProductNummer());
            statement.executeUpdate();

            // Persist the relation with the OV Chipkaart
            if (product.getOVChipkaartenNummers() != null) {
                // First delete all existing relations with this product (as some relations may have been deleted)
                String removeRelationsQuery = "DELETE FROM ov_chipkaart_product WHERE product_nummer = ?";
                PreparedStatement removeRelationsStatement = connection.prepareStatement(removeRelationsQuery);
                removeRelationsStatement.setInt(1, product.getProductNummer());
                removeRelationsStatement.executeUpdate();
                removeRelationsStatement.close();

                // Then insert the new relations
                for (Integer ovChipkaartNummer : product.getOVChipkaartenNummers()) {
                    String relationQuery = "INSERT INTO ov_chipkaart_product (kaart_nummer,product_nummer, last_update) VALUES (?, ?, ?)";
                    PreparedStatement relationStatement = connection.prepareStatement(relationQuery);
                    relationStatement.setInt(1, ovChipkaartNummer);
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
            System.err.println("[ProductDAOPsql.update] " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(Product product) {
        try {
            // Delete the OV Chipkaart relations first
            String relationQuery = "DELETE FROM ov_chipkaart_product WHERE product_nummer = ?";
            PreparedStatement relationStatement = connection.prepareStatement(relationQuery);
            relationStatement.setInt(1, product.getProductNummer());
            relationStatement.executeUpdate();
            relationStatement.close();

            // Delete the product
            String query = "DELETE FROM product WHERE product_nummer = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, product.getProductNummer());
            statement.executeUpdate();

            statement.close();
            return true;
        } catch (Exception e) {
            System.err.println("[ProductDAOPsql.delete] " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Product findByProductNummer(Integer product_nummer) {
        try {
            String query = "SELECT * FROM product WHERE product_nummer = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, product_nummer);
            ResultSet results = statement.executeQuery();

            if(results.next()) {
                Product product = new Product(
                        results.getInt("product_nummer"),
                        results.getString("naam"),
                        results.getString("beschrijving"),
                        results.getFloat("prijs")
                );

                results.close();
                statement.close();
                return product;
            }
            return null;
        } catch (Exception e) {
            System.err.println("[ProductDAOPsql.findByProductNummer] " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Product> findByOVChipkaart(OVChipkaart ovChipkaart) {
        try {
            String query = "SELECT * FROM product p LEFT JOIN ov_chipkaart_product ocp ON p.product_nummer = ocp.product_nummer WHERE ocp.kaart_nummer = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, ovChipkaart.getKaartNummer());
            ResultSet results = statement.executeQuery();

            List<Product> products = new ArrayList<>();
            while (results.next()) {
                Product product = new Product(
                        results.getInt("product_nummer"),
                        results.getString("naam"),
                        results.getString("beschrijving"),
                        results.getFloat("prijs")
                );

                product.setOVChipkaartenNummers(getAllOVChipkaartNummersForProduct(product.getProductNummer()));

                products.add(product);
            }
            results.close();
            statement.close();

            return products;
        } catch (Exception e) {
            System.err.println("[ProductDAOPsql.findByOVChipkaart] " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Product> findAll() {
        try {
            String query = "SELECT * FROM product";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet results = statement.executeQuery();

            List<Product> products = new ArrayList<>();
            while (results.next()) {
                Product product = new Product(
                        results.getInt("product_nummer"),
                        results.getString("naam"),
                        results.getString("beschrijving"),
                        results.getFloat("prijs")
                );
                product.setOVChipkaartenNummers(getAllOVChipkaartNummersForProduct(product.getProductNummer()));

                products.add(product);
            }
            results.close();
            statement.close();

            return products;
        } catch (Exception e) {
            System.err.println("[ProductDAOPsql.findAll] " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
