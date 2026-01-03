package product;

import java.util.List;

import ovchipkaart.OVChipkaart;
import ovchipkaart.OVChipkaartDAO;

public interface ProductDAO {
    public void setOVChipkaartDAO(OVChipkaartDAO ovChipkaartDAO);
    public boolean save(Product product);
    public boolean update(Product product);
    public boolean delete(Product product);
    public Product findByProductNummer(Integer product_nummer, boolean includeOVChipkaarten);
    public List<Product> findByOVChipkaart(OVChipkaart ovChipkaart, boolean includeOVChipkaarten);
    public List<Product> findAll();
}
