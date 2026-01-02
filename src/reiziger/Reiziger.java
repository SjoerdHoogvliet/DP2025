package reiziger;

import java.time.LocalDate;

import adres.Adres;

public class Reiziger {
    private Integer reiziger_id;
    private String voorletters;
    private String tussenvoegsel;
    private String achternaam;
    private LocalDate geboortedatum;
    private Adres adres;

    // Adres not in constructor as it is a 0..1 relationship, meaning there are reizigers without an adres
    public Reiziger(Integer reiziger_id, String voorletters, String tussenvoegsel, String achternaam, LocalDate geboortedatum) {
        this.reiziger_id = reiziger_id;
        this.voorletters = voorletters;
        this.tussenvoegsel = tussenvoegsel;
        this.achternaam = achternaam;
        this.geboortedatum = geboortedatum;
    }

    public String getNaam() {
        String naam = "";
        
        for (String letter : voorletters.split("")) {
            naam += letter + ".";
        }
        
        naam += " ";
        if (tussenvoegsel != null) {
            naam += tussenvoegsel + " ";
        }
        naam += achternaam;
        return naam;
    }

    public String toString() {
        String returnString = "Reiziger #%d: %s (%s)".formatted(
            reiziger_id,
            this.getNaam(),
            geboortedatum
        );

        if(this.getAdres() != null) {
            returnString += "; " + this.getAdres();
        }

        return returnString;
    }

    //*** Get/Set ***//

    public Integer getReiziger_id() {
        return reiziger_id;
    }

    public void setReiziger_id(Integer reiziger_id) {
        this.reiziger_id = reiziger_id;
    }

    public String getVoorletters() {
        return voorletters;
    }

    public void setVoorletters(String voorletters) {
        this.voorletters = voorletters;
    }

    public String getTussenvoegsel() {
        return tussenvoegsel;
    }

    public void setTussenvoegsel(String tussenvoegsel) {
        this.tussenvoegsel = tussenvoegsel;
    }

    public String getAchternaam() {
        return achternaam;
    }

    public void setAchternaam(String achternaam) {
        this.achternaam = achternaam;
    }

    public LocalDate getGeboortedatum() {
        return geboortedatum;
    }

    public void setGeboortedatum(LocalDate geboortedatum) {
        this.geboortedatum = geboortedatum;
    }

    public Adres getAdres() {
        return adres;
    }

    public void setAdres(Adres adres) {
        this.adres = adres;
    }
}
