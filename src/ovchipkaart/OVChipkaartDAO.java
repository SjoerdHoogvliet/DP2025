package ovchipkaart;

import java.util.List;

import product.Product;
import product.ProductDAO;
import reiziger.Reiziger;
import reiziger.ReizigerDAO;

public interface OVChipkaartDAO {
    public void setReizigerDAO(ReizigerDAO reizigerDAO);
    public void setProductDAO(ProductDAO productDAO);
    public boolean save(OVChipkaart ovchipkaart);
    public boolean update(OVChipkaart ovchipkaart);
    public boolean delete(OVChipkaart ovchipkaart);
    public OVChipkaart findByKaartNummer(Integer kaartNummer);
    public List<OVChipkaart> findByProduct(Product product, boolean includeProducten);
    public List<OVChipkaart> findByReiziger(Reiziger reiziger);
    public List<OVChipkaart> findAll();
}
