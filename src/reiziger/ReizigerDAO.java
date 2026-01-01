package reiziger;

import java.util.List;

public interface ReizigerDAO {
    public boolean save(Reiziger reiziger);
    public boolean update(Reiziger reiziger);
    public boolean delete(Reiziger reiziger);
    public Reiziger findById(Integer reiziger_id);
    public List<Reiziger> findAll();
}
