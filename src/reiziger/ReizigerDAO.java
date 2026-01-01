package reiziger;

import java.time.LocalDate;
import java.util.List;

import adres.AdresDAO;

public interface ReizigerDAO {
    public void setAdresDAO(AdresDAO adresDAO);
    public boolean save(Reiziger reiziger);
    public boolean update(Reiziger reiziger);
    public boolean delete(Reiziger reiziger);
    public Reiziger findById(Integer reiziger_id);
    public List<Reiziger> findByGbdatum(LocalDate geboortedatum);
    public List<Reiziger> findAll();
}
