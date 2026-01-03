package product;

import java.util.ArrayList;
import java.util.List;

public class Product {
    private Integer productNummer;
    private String naam;
    private String beschrijving;
    private float prijs;
    private List<Integer> ovChipkaartenNummers = new ArrayList<>();

    public Product(Integer productNummer, String naam, String beschrijving, float prijs) {
        this.productNummer = productNummer;
        this.naam = naam;
        this.beschrijving = beschrijving;
        this.prijs = prijs;
    }

    public String toString() {
        return "Product #%d: %s (%s)".formatted(
            this.productNummer,
            this.naam,
            this.beschrijving
        );
    }

    // As we can have multiple OV Chipkaarten, add these functions to add and remove OV Chipkaarten without getting and setting the whole list
    public void addOVChipkaartNummer(Integer ovChipkaartNummer) {
        if(!this.ovChipkaartenNummers.contains(ovChipkaartNummer)) {
            this.ovChipkaartenNummers.add(ovChipkaartNummer);
        }
    }

    public void removeOVChipkaartNummer(Integer ovChipkaartNummer) {
        this.ovChipkaartenNummers.remove(ovChipkaartNummer);
    }

    //*** Get/Set ***//
    public Integer getProductNummer() {
        return productNummer;
    }

    public void setProductNummer(Integer productNummer) {
        this.productNummer = productNummer;
    }

    public String getNaam() {
        return naam;
    }

    public void setNaam(String naam) {
        this.naam = naam;
    }

    public String getBeschrijving() {
        return beschrijving;
    }

    public void setBeschrijving(String beschrijving) {
        this.beschrijving = beschrijving;
    }

    public float getPrijs() {
        return prijs;
    }

    public void setPrijs(float prijs) {
        this.prijs = prijs;
    }

    public List<Integer> getOVChipkaartenNummers() {
        return ovChipkaartenNummers;
    }

    public void setOVChipkaartenNummers(List<Integer> ovChipkaartenNummers) {
        this.ovChipkaartenNummers = ovChipkaartenNummers;
    }
}
