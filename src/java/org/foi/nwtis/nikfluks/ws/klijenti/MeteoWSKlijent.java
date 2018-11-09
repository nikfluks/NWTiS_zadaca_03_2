package org.foi.nwtis.nikfluks.ws.klijenti;

/**
 * Klasa je web servis klijent za web servis GeoMeteoWS i poziva metode iz projekta nikfluks_zadaca_3_1
 *
 * @author Nikola
 * @version 1
 */
public class MeteoWSKlijent {

    /**
     * Poziv operacije dajSvaParkiralista na web servisu GeoMeteoWS
     *
     * @return lista parkirališta
     */
    public static java.util.List<org.foi.nwtis.nikfluks.ws.serveri.Parkiraliste> dajSvaParkiralista() {
        org.foi.nwtis.nikfluks.ws.serveri.GeoMeteoWS_Service service = new org.foi.nwtis.nikfluks.ws.serveri.GeoMeteoWS_Service();
        org.foi.nwtis.nikfluks.ws.serveri.GeoMeteoWS port = service.getGeoMeteoWSPort();
        return port.dajSvaParkiralista();
    }

    /**
     * Poziv operacije dodajParkiraliste na web servisu GeoMeteoWS
     *
     * @param arg0 parkiralište koje se želi dodati u bazu podataka
     * @return true ako je uspješno dodano, false inače
     */
    public static boolean dodajParkiraliste(org.foi.nwtis.nikfluks.ws.serveri.Parkiraliste arg0) {
        org.foi.nwtis.nikfluks.ws.serveri.GeoMeteoWS_Service service = new org.foi.nwtis.nikfluks.ws.serveri.GeoMeteoWS_Service();
        org.foi.nwtis.nikfluks.ws.serveri.GeoMeteoWS port = service.getGeoMeteoWSPort();
        return port.dodajParkiraliste(arg0);
    }

    /**
     * Poziv operacije dajSveMeteoPodatkeUIntervalu na web servisu GeoMeteoWS
     *
     * @param arg0 id parkirališta za kojeg se traže meteo podaci
     * @param arg1 timestamp od kojeg se traže podaci
     * @param arg2 timestamp do kojeg se traže podaci
     * @return lista meteo podataka
     */
    public static java.util.List<org.foi.nwtis.nikfluks.ws.serveri.MeteoPodaci> dajSveMeteoPodatkeUIntervalu(int arg0, long arg1, long arg2) {
        org.foi.nwtis.nikfluks.ws.serveri.GeoMeteoWS_Service service = new org.foi.nwtis.nikfluks.ws.serveri.GeoMeteoWS_Service();
        org.foi.nwtis.nikfluks.ws.serveri.GeoMeteoWS port = service.getGeoMeteoWSPort();
        return port.dajSveMeteoPodatkeUIntervalu(arg0, arg1, arg2);
    }

}
