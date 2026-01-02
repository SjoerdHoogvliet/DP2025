package ovchipkaart;

import java.time.LocalDate;

import reiziger.Reiziger;

public class OVChipkaart {
    private Integer kaartNummer;
    private LocalDate geldigTot;
    private Integer klasse;
    private float saldo;
    private Reiziger reiziger;

    public OVChipkaart(Integer kaartNummer, LocalDate geldigTot, Integer klasse, float saldo, Reiziger reiziger) {
        this.kaartNummer = kaartNummer;
        this.geldigTot = geldigTot;
        this.klasse = klasse;
        this.saldo = saldo;
        this.reiziger = reiziger;
    }

    public String toString() {
        return "OV Chipkaart #%d: met %.2f euro is geldig tot %s in klasse %d".formatted(
            this.kaartNummer,
            this.saldo,
            this.geldigTot,
            this.klasse
        );
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
}
