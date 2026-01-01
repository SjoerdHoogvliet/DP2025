package adres;

import java.util.List;

import reiziger.Reiziger;
import reiziger.ReizigerDAO;

public interface AdresDAO {
    public void setReizigerDAO(ReizigerDAO reizigerDAO);
    public boolean save(Adres adres);
    public boolean update(Adres adres);
    public boolean delete(Adres adres);
    public Adres findById(Integer adres_id);
    public Adres findByReiziger(Reiziger reiziger);
    public List<Adres> findAll();
}
