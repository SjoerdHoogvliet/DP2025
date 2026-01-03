package product;

import java.util.List;

import ovchipkaart.OVChipkaart;

public interface ProductDAO {
    public boolean save(Product product);
    public boolean update(Product product);
    public boolean delete(Product product);
    public Product findByProductNummer(Integer product_nummer);
    public List<Product> findByOVChipkaart(OVChipkaart ovChipkaart);
    public List<Product> findAll();
}
