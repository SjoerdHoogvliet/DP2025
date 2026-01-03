package product;

import java.util.ArrayList;
import java.util.List;

import ovchipkaart.OVChipkaart;

public class Product {
    private Integer productNummer;
    private String naam;
    private String beschrijving;
    private float prijs;
    private List<OVChipkaart> ovChipkaarten = new ArrayList<>();

    public Product(Integer productNummer, String naam, String beschrijving, float prijs) {
        this.productNummer = productNummer;
        this.naam = naam;
        this.beschrijving = beschrijving;
        this.prijs = prijs;
    }

    public String toString() {
        String returnString = "Product #%d: %s (%s)".formatted(
            this.productNummer,
            this.naam,
            this.beschrijving
        );

        if(this.ovChipkaarten.size() > 0) {
            returnString += " en heeft de volgende OV Chipkaarten: \n";
            for (OVChipkaart o : this.ovChipkaarten) {
                returnString += o + "\n";
            }
        }

        return returnString;
    }

    // As we can have multiple OV Chipkaarten, add these functions to add and remove OV Chipkaarten without getting and setting the whole list
    public void addOVChipkaart(OVChipkaart ovChipkaart) {
        if(!this.ovChipkaarten.contains(ovChipkaart)) {
            this.ovChipkaarten.add(ovChipkaart);
        }
    }

    public void removeOVChipkaart(OVChipkaart ovChipkaart) {
        this.ovChipkaarten.remove(ovChipkaart);
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

    public List<OVChipkaart> getOVChipkaarten() {
        return ovChipkaarten;
    }

    public void setOVChipkaarten(List<OVChipkaart> ovChipkaarten) {
        this.ovChipkaarten = ovChipkaarten;
    }
}
