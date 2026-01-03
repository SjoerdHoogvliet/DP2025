package ovchipkaart;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import product.Product;
import reiziger.Reiziger;

public class OVChipkaart {
    private Integer kaartNummer;
    private LocalDate geldigTot;
    private Integer klasse;
    private float saldo;
    private Reiziger reiziger;
    private List<Product> producten = new ArrayList<>();

    public OVChipkaart(Integer kaartNummer, LocalDate geldigTot, Integer klasse, float saldo, Reiziger reiziger) {
        this.kaartNummer = kaartNummer;
        this.geldigTot = geldigTot;
        this.klasse = klasse;
        this.saldo = saldo;
        this.reiziger = reiziger;
    }

    public String toString() {
        String returnString = "OV Chipkaart #%d: met %.2f euro is geldig tot %s in klasse %d".formatted(
            this.kaartNummer,
            this.saldo,
            this.geldigTot,
            this.klasse
        );

        if(this.producten.size() > 0) {
            returnString += " en heeft de producten: \n";
            for (Product p : this.producten) {
                returnString += p + "\n";
            }
        }

        return returnString;
    }

    // As we can have multiple products, add these functions to add and remove Products without getting and setting the whole list
    public void addProduct(Product product) {
        if(!this.producten.contains(product)) {
            this.producten.add(product);
            product.addOVChipkaart(this);
        }
    }

    public void removeProduct(Product product) {
        this.producten.remove(product);
        product.removeOVChipkaart(this);
    }

    //*** Get/Set ***//
    public Integer getKaartNummer() {
        return kaartNummer;
    }

    public void setKaartNummer(Integer kaartNummer) {
        this.kaartNummer = kaartNummer;
    }

    public LocalDate getGeldigTot() {
        return geldigTot;
    }

    public void setGeldigTot(LocalDate geldigTot) {
        this.geldigTot = geldigTot;
    }

    public Integer getKlasse() {
        return klasse;
    }

    public void setKlasse(Integer klasse) {
        this.klasse = klasse;
    }

    public float getSaldo() {
        return saldo;
    }

    public void setSaldo(float saldo) {
        this.saldo = saldo;
    }

    public Reiziger getReiziger() {
        return reiziger;
    }

    public void setReiziger(Reiziger reiziger) {
        this.reiziger = reiziger;
    }

    public List<Product> getProducten() {
        return producten;
    }

    public void setProducten(List<Product> producten) {
        this.producten = producten;
    }
}
