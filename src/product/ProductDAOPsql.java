package product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import ovchipkaart.OVChipkaart;
import ovchipkaart.OVChipkaartDAO;

public class ProductDAOPsql implements ProductDAO {
    private Connection connection;
    private OVChipkaartDAO ovChipkaartDAO;

    public ProductDAOPsql(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void setOVChipkaartDAO(OVChipkaartDAO ovChipkaartDAO) {
        this.ovChipkaartDAO = ovChipkaartDAO;
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
            if (product.getOVChipkaarten() != null) {
                ovChipkaartDAO.saveRelationsForProduct(product);
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

            // Let the owning side persist all relations ()
            ovChipkaartDAO.updateRelationsForProduct(product);

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
            ovChipkaartDAO.deleteRelationsForProduct(product);

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
    public Product findByProductNummer(Integer product_nummer, boolean includeOVChipkaarten) {
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

                if(includeOVChipkaarten) {
                    product.setOVChipkaarten(ovChipkaartDAO.findByProduct(product, false));
                }

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
    public List<Product> findByOVChipkaart(OVChipkaart ovChipkaart, boolean includeOVChipkaarten) {
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

                if(includeOVChipkaarten) {
                    product.setOVChipkaarten(ovChipkaartDAO.findByProduct(product, false));
                }
                
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

                product.setOVChipkaarten(ovChipkaartDAO.findByProduct(product, false));

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
