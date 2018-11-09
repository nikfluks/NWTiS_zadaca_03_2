package org.foi.nwtis.nikfluks.web.zrna;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import org.foi.nwtis.nikfluks.rest.klijenti.MeteoRESTKlijent;
import org.foi.nwtis.nikfluks.rest.klijenti.MeteoRESTKlijentId;
import org.foi.nwtis.nikfluks.ws.klijenti.MeteoWSKlijent;
import org.foi.nwtis.nikfluks.ws.serveri.MeteoPodaci;
import org.foi.nwtis.nikfluks.ws.serveri.Parkiraliste;

/**
 * Klasa je Manage Bean, sesijskog dosega. Služi za obradu korisničkih zahtjeva.
 *
 * @author Nikola
 * @version 1
 */
@Named(value = "operacijaParkiraliste")
@SessionScoped
public class OperacijaParkiraliste implements Serializable {

    private String naziv = "";
    private String adresa = "";
    private List<Parkiraliste> parkiralista;
    private List<String> odabranaParkiralista;
    private List<MeteoPodaci> meteo;
    private String greska = "";

    /**
     * Konstruktor klase OperacijaParkiraliste, inicijalizira 2 liste
     */
    public OperacijaParkiraliste() {
        parkiralista = new ArrayList<>();
        odabranaParkiralista = new ArrayList<>();
        meteo = new ArrayList<>();
    }

    /**
     * Dodaje parkiralište u bazu podataka putem soap web servisa
     *
     * @return prazan string
     */
    public String upisiSOAP() {
        try {
            if (!naziv.isEmpty() && !adresa.isEmpty()) {
                Parkiraliste p = new Parkiraliste();
                p.setNaziv(naziv);
                p.setAdresa(adresa);

                boolean uspjesno = MeteoWSKlijent.dodajParkiraliste(p);
                if (!uspjesno) {
                    proslijediGresku("Dodavanje parkirališta nije uspjelo!");
                } else {
                    naziv = "";
                    adresa = "";
                }
            } else {
                proslijediGresku("Naziv i/ili adresa ne smiju biti prazni!");
            }
        } catch (Exception e) {
            proslijediGresku("Greška pri spajanju na SOAP servis!");
        }
        return "";
    }

    /**
     * Dodaje parkiralište u bazu podataka putem rest web servisa
     *
     * @return prazan string
     */
    public String upisiREST() {
        try {
            if (!naziv.isEmpty() && !adresa.isEmpty()) {
                MeteoRESTKlijent mrsk = new MeteoRESTKlijent();
                String zahtjev = "{\"naziv\": \"" + naziv + "\", \"adresa\": \"" + adresa + "\"}";
                String odgovor = mrsk.postJson(zahtjev, String.class);
                JsonObject jsonSadrzaj = new JsonParser().parse(odgovor).getAsJsonObject();
                String status = jsonSadrzaj.get("status").getAsString();

                if (status.equalsIgnoreCase("err")) {
                    proslijediGresku(jsonSadrzaj.get("poruka").getAsString());
                } else {
                    naziv = "";
                    adresa = "";
                }
            } else {
                proslijediGresku("Naziv i/ili adresa ne smiju biti prazni!");
            }
        } catch (Exception e) {
            proslijediGresku("Greška pri spajanju na REST servis!");
        }
        return "";
    }

    /**
     * Dohvaća podatke, naziv i adresu, o parkiralištu putem rest web servisa i ispisuje iz u input polja
     *
     * @return prazan string
     */
    public String preuzmiRest() {
        if (odabranaParkiralista.size() == 1) {
            try {
                MeteoRESTKlijentId meteoId = new MeteoRESTKlijentId(odabranaParkiralista.get(0));
                String odgovor = meteoId.getJson(String.class);
                JsonObject jsonSadrzaj = new JsonParser().parse(odgovor).getAsJsonObject();
                if (jsonSadrzaj.get("poruka") != null) {
                    proslijediGresku(jsonSadrzaj.get("poruka").getAsString());
                } else {
                    naziv = jsonSadrzaj.getAsJsonArray("odgovor").get(0).getAsJsonObject().get("naziv").getAsString();
                    adresa = jsonSadrzaj.getAsJsonArray("odgovor").get(0).getAsJsonObject().get("adresa").getAsString();
                }
            } catch (Exception e) {
                proslijediGresku("Greška pri spajanju na REST servis!");
            }
        } else {
            proslijediGresku("Mora biti odabrano točno jedno parkiralište!");
        }
        return "";
    }

    /**
     * Ažurira podatke o parkiralištu, naziv i adresu, putem rest web servisa
     *
     * @return
     */
    public String azurirajRest() {
        if (!naziv.isEmpty() && !adresa.isEmpty()) {
            if (odabranaParkiralista.size() == 1) {
                try {
                    MeteoRESTKlijentId meteoId = new MeteoRESTKlijentId(odabranaParkiralista.get(0));
                    String json = "{\"naziv\": \"" + naziv + "\", \"adresa\":\"" + adresa + "\"}";
                    String odgovor = meteoId.putJson(json, String.class);

                    JsonObject jsonSadrzaj = new JsonParser().parse(odgovor).getAsJsonObject();
                    if (jsonSadrzaj.get("poruka") != null) {
                        proslijediGresku(jsonSadrzaj.get("poruka").getAsString());
                    } else {
                        naziv = "";
                        adresa = "";
                    }
                } catch (Exception e) {
                    proslijediGresku("Greška pri spajanju na REST servis!");
                }
            } else {
                proslijediGresku("Mora biti odabrano točno jedno parkiralište!");
            }
        } else {
            proslijediGresku("Naziv i/ili adresa ne smiju biti prazni!");
        }
        return "";
    }

    /**
     * Briše parkiralište temeljem id-a putem rest web servisa
     *
     * @return prazan string
     */
    public String brisiRest() {
        if (odabranaParkiralista.size() == 1) {
            try {
                MeteoRESTKlijentId meteoId = new MeteoRESTKlijentId(odabranaParkiralista.get(0));
                String odgovor = meteoId.deleteJson(String.class);
                JsonObject jsonSadrzaj = new JsonParser().parse(odgovor).getAsJsonObject();
                if (jsonSadrzaj.get("poruka") != null) {
                    proslijediGresku(jsonSadrzaj.get("poruka").getAsString());
                }
            } catch (Exception e) {
                proslijediGresku("Greška pri spajanju na REST servis!");
            }
        } else {
            proslijediGresku("Mora biti odabrano točno jedno parkiralište!");
        }
        return "";
    }

    /**
     * Dohvaća sve meteo podatke za minimalno 2 odabrana parkirališta, putem soap web servisa
     *
     * @return prazan string
     */
    public String preuzmiMeteo() {
        try {
            if (odabranaParkiralista.size() > 1) {
                for (String id : odabranaParkiralista) {
                    meteo.addAll(MeteoWSKlijent.dajSveMeteoPodatkeUIntervalu(Integer.parseInt(id), 0, System.currentTimeMillis()));
                }
            } else {
                proslijediGresku("Mora biti odabrano više od jednog parkirališta!");
            }
        } catch (Exception e) {
            proslijediGresku("Greška pri spajanju na SOAP servis!");
        }
        return "";
    }

    /**
     * Dohvaća podatke, naziv i adresu, o parkiralištu putem soap web servisa i ispisuje iz u input polja
     *
     * @return prazan string
     */
    public String preuzmiSoap() {
        if (odabranaParkiralista.size() == 1) {
            try {
                List<Parkiraliste> listaParkiralista = MeteoWSKlijent.dajSvaParkiralista();
                if (listaParkiralista != null) {
                    for (Parkiraliste p : listaParkiralista) {
                        if (p.getId() == Integer.parseInt(odabranaParkiralista.get(0))) {
                            naziv = p.getNaziv();
                            adresa = p.getAdresa();
                        }
                    }
                }
            } catch (Exception e) {
                proslijediGresku("Greška pri spajanju na SOAP servis!");
            }
        } else {
            proslijediGresku("Mora biti odabrano točno jedno parkiralište!");
        }
        return "";
    }

    /**
     * Proslijeđuje grešku koja se prikazuje na ekranu korisnika.
     *
     * @param greska greška koja se ispisuje
     */
    public void proslijediGresku(String greska) {
        this.greska = greska;
        FacesMessage facesGreska = new FacesMessage(greska);
        FacesContext.getCurrentInstance().addMessage(null, facesGreska);
    }

    /**
     * getter za varijablu naziv
     *
     * @return naziv
     */
    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    /**
     * getter za varijablu adresa
     *
     * @return adresa
     */
    public String getAdresa() {
        return adresa;
    }

    public void setAdresa(String adresa) {
        this.adresa = adresa;
    }

    /**
     * getter za varijablu parkiralista, dohvaća sva parkirališta iz baze podataka putem soap web servisa
     *
     * @return lista parkiralista ili nova prazna lista u slučaju greške
     */
    public List<Parkiraliste> getParkiralista() {
        try {
            parkiralista = MeteoWSKlijent.dajSvaParkiralista();
            if (parkiralista == null) {
                proslijediGresku("Dohvaćanje parkirališta nije uspjelo!");
                return new ArrayList<>();
            }
        } catch (Exception e) {
            proslijediGresku("Greška pri spajanju na SOAP servis!");
            return new ArrayList<>();
        }
        return parkiralista;
    }

    /**
     * setter za varijablu parkiralista
     *
     * @param parkiralista parkiralista koja se postavljaju
     */
    public void setParkiralista(List<Parkiraliste> parkiralista) {
        this.parkiralista = parkiralista;
    }

    /**
     * getter za varijablu odabranaParkiralista
     *
     * @return odabranaParkiralista
     */
    public List<String> getOdabranaParkiralista() {
        return odabranaParkiralista;
    }

    /**
     * setter za varijablu odabranaParkiralista
     *
     * @param odabranaParkiralista odabranaParkiralista koja se postavljaju
     */
    public void setOdabranaParkiralista(List<String> odabranaParkiralista) {
        this.odabranaParkiralista = odabranaParkiralista;
    }

    /**
     * getter za varijablu meteo
     *
     * @return meteo
     */
    public List<MeteoPodaci> getMeteo() {
        return meteo;
    }

    /**
     * setter za varijablu meteo
     *
     * @param meteo meteo podaci koji se postavljaju
     */
    public void setMeteo(List<MeteoPodaci> meteo) {
        this.meteo = meteo;
    }

    /**
     * getter za varijablu greska
     *
     * @return greska
     */
    public String getGreska() {
        return greska;
    }

    /**
     * setter za varijablu greska
     *
     * @param greska greska koja se postavlja
     */
    public void setGreska(String greska) {
        this.greska = greska;
    }
}
