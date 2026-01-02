import reiziger.Reiziger;
import reiziger.ReizigerDAO;
import reiziger.ReizigerDAOPsql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import adres.Adres;
import adres.AdresDAO;
import adres.AdresDAOPsql;
import ovchipkaart.OVChipkaart;
import ovchipkaart.OVChipkaartDAO;
import ovchipkaart.OVChipkaartDAOPsql;

public class Main {
    public static void main(String[] args) {
        try {
            Connection connection = getConnection();

            // Creating the Data Access Objects
            ReizigerDAO reizigerDAO = new ReizigerDAOPsql(connection);
            AdresDAO adresDAO = new AdresDAOPsql(connection);
            OVChipkaartDAO ovchipkaartDAO = new OVChipkaartDAOPsql(connection);

            // Assigning related DAOs to each other
            reizigerDAO.setAdresDAO(adresDAO);
            reizigerDAO.setOVChipkaartDAO(ovchipkaartDAO);
            adresDAO.setReizigerDAO(reizigerDAO);
            ovchipkaartDAO.setReizigerDAO(reizigerDAO);

            // Testing the DAOs
            testReizigerDAO(reizigerDAO);
            testAdresDAO(adresDAO, reizigerDAO);
            testOVChipkaartDAO(ovchipkaartDAO, reizigerDAO);

            closeConnection(connection);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private static Connection getConnection() {
        try {
            return DriverManager.getConnection("jdbc:postgresql://localhost:5434/ovchip25", "postgres", "padmin");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private static void closeConnection(Connection connection) {
        try {
            connection.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

     /**
     * P2. Reiziger DAO: persistentie van een klasse
     *
     * Deze methode test de CRUD-functionaliteit van de Reiziger DAO
     *
     * @throws SQLException
     */
    private static void testReizigerDAO(ReizigerDAO rdao) throws SQLException {
        System.out.println("\n---------- Test ReizigerDAO -------------");

        // Haal alle reizigers op uit de database
        List<Reiziger> reizigers = rdao.findAll();
        System.out.println("[Test] ReizigerDAO.findAll() geeft de volgende reizigers:");
        for (Reiziger r : reizigers) {
            System.out.println(r);
        }
        System.out.println();

        // Maak een nieuwe reiziger aan en persisteer deze in de database
        String gbdatum = "1981-03-14";
        // Slightly changed test code because I use LocalDate instead of java.sql.Date
        Reiziger sietske = new Reiziger(77, "S", "", "Boers", LocalDate.parse(gbdatum));
        System.out.print("[Test] Eerst " + reizigers.size() + " reizigers, na ReizigerDAO.save() ");
        rdao.save(sietske);
        reizigers = rdao.findAll();
        System.out.println(reizigers.size() + " reizigers\n");

        // Update test door het sietske object aan te passen en opnieuw te persisteren.
        sietske.setTussenvoegsel("gewijzigd");
        System.out.println("[Test] Oud Sietske object: " + rdao.findById(77));
        rdao.update(sietske);
        System.out.println("[Test] Nieuw Sietske object: " + rdao.findById(77));

        // Haal alle reizigers op uit de database die geboren zijn op 1981-03-14
        List<Reiziger> reizigersFromGbdatum = rdao.findByGbdatum(LocalDate.parse(gbdatum));
        System.out.println("[Test] ReizigerDAO.findByGbdatum() geeft de volgende reizigers:");
        for (Reiziger r : reizigersFromGbdatum) {
            System.out.println(r);
        }
        System.out.println();

        // Verwijder sietske uit de database
        System.out.println("[Test] Alle reizigers voor delete: ");
        reizigers = rdao.findAll();
        for (Reiziger r : reizigers) {
            System.out.println(r);
        }
        System.out.println();
        rdao.delete(sietske);
        System.out.println("[Test] Alle reizigers na delete: ");
        reizigers = rdao.findAll();
        for (Reiziger r : reizigers) {
            System.out.println(r);
        }
    }

    /**
     * P3. Adres DAO: persistentie van een klasse
     *
     * Deze methode test de CRUD-functionaliteit van de Adres DAO
     *
     * @throws SQLException
     */
    private static void testAdresDAO(AdresDAO adresDAO, ReizigerDAO reizigerDAO) throws SQLException {
        System.out.println("\n---------- Test AdresDAO -------------");

        // Haal alle adressen op uit de database
        List<Adres> adressen = adresDAO.findAll();
        System.out.println("[Test] AdresDAO.findAll() geeft de volgende adressen:");
        for (Adres a : adressen) {
            System.out.println(a);
        }
        System.out.println();

        // Breng Sietske terug in leven
        String gbdatum = "1981-03-14";
        Reiziger sietske = new Reiziger(77, "S", "", "Boers", LocalDate.parse(gbdatum));
        reizigerDAO.save(sietske);

        // Maak een nieuwe adres aan en persisteer deze in de database
        Adres sietskeAdres = new Adres(77, "3332KS", "7", "Volgerland", "Zwijndrecht", sietske);
        sietske.setAdres(sietskeAdres);
        System.out.print("[Test] Eerst " + adressen.size() + " adressen, na AdresDAO.save() ");
        adresDAO.save(sietskeAdres);
        adressen = adresDAO.findAll();
        System.out.println(adressen.size() + " adressen");

        // Update test door het sietske object aan te passen en opnieuw te persisteren.
        sietskeAdres.setStraat("Nieuwstraat");
        System.out.println("[Test] Oud adres #77: " + adresDAO.findById(77));
        adresDAO.update(sietskeAdres);
        System.out.println("[Test] Nieuw adres #77: " + adresDAO.findById(77));

        // Haal het adres van sietske op
        Adres adresFromSietske = adresDAO.findByReiziger(sietske);
        System.out.println("[Test] Het adres van Sietske: " + adresFromSietske);
        System.out.println();

        // Verwijder sietske uit de database
        System.out.println("[Test] Aantal adressen voor delete: " + adresDAO.findAll().size());
        adresDAO.delete(sietskeAdres);
        System.out.println("[Test] Aantal adressen na delete: " + adresDAO.findAll().size());

        // Maak de database weer schoon
        reizigerDAO.delete(sietske);
    }

    /**
     * P4. OV Chipkaart DAO: persistentie van een klasse
     *
     * Deze methode test de CRUD-functionaliteit van de OV Chipkaart DAO
     *
     * @throws SQLException
     */
    public static void testOVChipkaartDAO(OVChipkaartDAO ovchipkaartDAO, ReizigerDAO reizigerDAO) throws SQLException {
        System.out.println("\n---------- Test OVChipkaartDAO -------------");

        // Haal alle ovchipkaarten op uit de database
        List<OVChipkaart> ovchipkaarten = ovchipkaartDAO.findAll();
        System.out.println("[Test] OVChipkaartDAO.findAll() geeft de volgende ovchipkaarten:");
        for (OVChipkaart o : ovchipkaarten) {
            System.out.println(o);
        }
        System.out.println();

        // Breng Sietske terug in leven
        String gbdatum = "1981-03-14";
        Reiziger sietske = new Reiziger(77, "S", "", "Boers", LocalDate.parse(gbdatum));
        reizigerDAO.save(sietske);

        // Maak een nieuwe ovchipkaart aan en persisteer deze in de database
        OVChipkaart sietskeOVChipkaart = new OVChipkaart(77, LocalDate.parse(gbdatum), 2, 100, sietske);
        sietske.addOVChipkaart(sietskeOVChipkaart);
        System.out.print("[Test] Eerst " + ovchipkaarten.size() + " ovchipkaarten, na OVChipkaartDAO.save() ");
        ovchipkaartDAO.save(sietskeOVChipkaart);
        ovchipkaarten = ovchipkaartDAO.findAll();
        System.out.println(ovchipkaarten.size() + " ovchipkaarten\n");

        // Update test door sietskes OV Chipkaart aan te passen en opnieuw te persisteren.
        sietskeOVChipkaart.setSaldo(200);
        System.out.println("[Test] Oude OV Chipkaart: " + ovchipkaartDAO.findByKaartNummer(77));
        ovchipkaartDAO.update(sietskeOVChipkaart);
        System.out.println("[Test] Nieuwe OV Chipkaart: " + ovchipkaartDAO.findByKaartNummer(77));

        // Haal alle ovchipkaarten van Sietske uit de database
        List<OVChipkaart> ovchipkaartenFromGbdatum = ovchipkaartDAO.findByReiziger(sietske);
        System.out.println("[Test] Alle OV Chipkaarten van Sietske:");
        for (OVChipkaart o : ovchipkaartenFromGbdatum) {
            System.out.println(o);
        }
        System.out.println();

        // Verwijder de OV Chipkaart van Sietske uit de database
        System.out.println("[Test] Aantal ovchipkaarten voor delete: " + ovchipkaartDAO.findAll().size());

        ovchipkaartDAO.delete(sietskeOVChipkaart);
        System.out.println("[Test] Aantal ovchipkaarten na delete: " + ovchipkaartDAO.findAll().size());

        // Maak de database weer schoon
        reizigerDAO.delete(sietske);
    }
}