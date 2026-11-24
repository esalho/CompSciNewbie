import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Opintorekisteri – esimerkkiohjelma Python cheat sheetin rinnalle.
 */
public class Opintorekisteri {

    // Vakio: static final = arvo ei muutu koskaan. Vakiot kirjoitetaan ISOILLA_KIRJAIMILLA.
    // Python: TIEDOSTONIMI = "opiskelijat.csv" (Pythonissa vakio on vain sopimus)
    private static final String TIEDOSTONIMI = "opiskelijat.csv";
    private static final int VALMISTUMISEN_RAJA = 180;

    // Ohjelman aloituskohta.
    // Python: if __name__ == "__main__": main()
    public static void main(String[] args) {
        Scanner lukija = new Scanner(System.in);

        // ArrayList on Javan vastine Pythonin listalle.
        // <Opiskelija> kertoo, minkä tyyppisiä alkioita listassa saa olla.
        // Python: opiskelijat = []
        ArrayList<Opiskelija> opiskelijat = new ArrayList<>();

        System.out.println("Tervetuloa opintorekisteriin!");
        tulostaOhjeet();

        // while (true) + break, kuten Pythonissa.
        while (true) {
            System.out.print("\nKomento: ");
            String komento = lukija.nextLine().trim().toLowerCase();  // Python: input().strip().lower()

            // Merkkijonot verrataan AINA equals-metodilla, ei ==-operaattorilla!
            if (komento.equals("lopeta")) {
                break;
            } else if (komento.isEmpty()) {          // Python: if komento == "":
                continue;
            } else if (komento.equals("lisaa")) {
                lisaaOpiskelija(lukija, opiskelijat);
            } else if (komento.equals("kurssi")) {
                lisaaKurssi(lukija, opiskelijat);
            } else if (komento.equals("listaa")) {
                listaa(opiskelijat);
            } else if (komento.equals("tilasto")) {
                tulostaTilasto(opiskelijat);
            } else if (komento.equals("tallenna")) {
                tallenna(opiskelijat);
            } else if (komento.equals("lataa")) {
                lataa(opiskelijat);
            } else {
                System.out.println("Tuntematon komento.");
                tulostaOhjeet();
            }
        }
        lukija.close();
        System.out.println("Näkemiin!");
    }

    // void = metodi ei palauta mitään. Python: funktio ilman return-lausetta (palauttaa None)
    private static void tulostaOhjeet() {
        System.out.println("Komennot: lisaa, kurssi, listaa, tilasto, tallenna, lataa, lopeta");
    }

    // Syötteen lukeminen

    /**
     * Kysyy käyttäjältä kokonaislukua niin kauan, kunnes syöte on kelvollinen.
     * Älä koskaan luota käyttäjän syötteeseen sellaisenaan.
     */
    private static int lueKokonaisluku(Scanner lukija, String kysymys, int min, int max) {
        while (true) {
            System.out.print(kysymys);
            String syote = lukija.nextLine();

            // Python: try: ... except ValueError:
            try {
                int luku = Integer.parseInt(syote.trim());   // Python: int(syote)
                if (luku >= min && luku <= max) {             // Python: if min <= luku <= max:
                    return luku;
                }
                System.out.println("Luvun pitää olla välillä " + min + "–" + max + ".");
            } catch (NumberFormatException e) {               // ≈ Pythonin ValueError
                System.out.println("Syötä kokonaisluku!");
            }
        }
    }

    private static String lueTeksti(Scanner lukija, String kysymys) {
        while (true) {
            System.out.print(kysymys);
            String teksti = lukija.nextLine().trim();
            // ;-merkki on kielletty, koska käytämme sitä tiedostossa erottimena.
            if (!teksti.isEmpty() && !teksti.contains(";")) {   // Python: not ... and ";" not in teksti
                return teksti;
            }
            System.out.println("Teksti ei saa olla tyhjä eikä sisältää ;-merkkiä.");
        }
    }

    // Komennot

    private static void lisaaOpiskelija(Scanner lukija, ArrayList<Opiskelija> opiskelijat) {
        String nimi = lueTeksti(lukija, "Nimi: ");

        if (etsi(opiskelijat, nimi) != null) {
            System.out.println("Opiskelija " + nimi + " on jo rekisterissä.");
            return;   // return lopettaa metodin heti, kuten Pythonissa
        }

        // Olio luodaan new-avainsanalla. Python: Opiskelija(nimi)
        Opiskelija uusi = new Opiskelija(nimi);
        opiskelijat.add(uusi);   // Python: opiskelijat.append(uusi)
        System.out.println("Lisätty: " + uusi);
    }

    private static void lisaaKurssi(Scanner lukija, ArrayList<Opiskelija> opiskelijat) {
        String nimi = lueTeksti(lukija, "Opiskelijan nimi: ");
        Opiskelija opiskelija = etsi(opiskelijat, nimi);

        // null ≈ Pythonin None. Python: if opiskelija is None:
        if (opiskelija == null) {
            System.out.println("Opiskelijaa ei löytynyt.");
            return;
        }

        String kurssi = lueTeksti(lukija, "Kurssin nimi: ");
        int arvosana = lueKokonaisluku(lukija, "Arvosana (0–5): ", 0, 5);
        int laajuus = lueKokonaisluku(lukija, "Laajuus (op): ", 1, 30);

        opiskelija.lisaaSuoritus(kurssi, arvosana, laajuus);
        System.out.println("Suoritus lisätty: " + opiskelija);
    }

    /**
     * Etsii opiskelijan nimellä. Palauttaa null, jos ei löydy.
     * Huomaa: paluutyyppi (Opiskelija) on kirjoitettu metodin eteen.
     * Python: def etsi(opiskelijat: list[Opiskelija], nimi: str) -> Opiskelija | None:
     */
    private static Opiskelija etsi(ArrayList<Opiskelija> opiskelijat, String nimi) {
        // for-each-silmukka. Python: for opiskelija in opiskelijat:
        for (Opiskelija opiskelija : opiskelijat) {
            if (opiskelija.getNimi().equalsIgnoreCase(nimi)) {
                return opiskelija;
            }
        }
        return null;
    }

    private static void listaa(ArrayList<Opiskelija> opiskelijat) {
        if (opiskelijat.isEmpty()) {   // Python: if not opiskelijat:  tai  len(opiskelijat) == 0
            System.out.println("Rekisteri on tyhjä.");
            return;
        }

        // "Perinteinen" for-silmukka indeksin kanssa.
        // Python: for i in range(len(opiskelijat)):   (tai enumerate)
        for (int i = 0; i < opiskelijat.size(); i++) {
            Opiskelija opiskelija = opiskelijat.get(i);   // Python: opiskelijat[i]
            // printf ≈ f-merkkijono muotoiluineen. [§ 3]
            // Python: print(f"{i + 1}. {opiskelija.nimi:<15} {pisteet:>4} op")
            System.out.printf("%d. %-15s %4d op   ka. %.2f%n",
                    i + 1,
                    opiskelija.getNimi(),
                    opiskelija.getOpintopisteet(),
                    opiskelija.keskiarvo());
        }
    }

    private static void tulostaTilasto(ArrayList<Opiskelija> opiskelijat) {
        if (opiskelijat.isEmpty()) {
            System.out.println("Ei tilastoitavaa.");
            return;
        }

        int pisteetYhteensa = 0;
        Opiskelija eniten = opiskelijat.get(0);

        for (Opiskelija opiskelija : opiskelijat) {
            pisteetYhteensa += opiskelija.getOpintopisteet();
            if (opiskelija.getOpintopisteet() > eniten.getOpintopisteet()) {
                eniten = opiskelija;
            }
        }

        // ⚠️ Kokonaislukujako! Kahden int-luvun jakolasku katkaisee desimaalit.
        int katkaistu = pisteetYhteensa / opiskelijat.size();             // Python: //
        double tarkka = (double) pisteetYhteensa / opiskelijat.size();    // Python: /

        System.out.println("Opiskelijoita: " + opiskelijat.size());
        System.out.println("Opintopisteitä keskimäärin: " + tarkka
                + " (kokonaislukujaolla olisi tullut " + katkaistu + ")");
        System.out.println("Eniten opintopisteitä: " + eniten);

        // HashMap on Javan vastine Pythonin sanakirjalle.
        // Lasketaan, montako kertaa kukin arvosana esiintyy.
        // Python: maarat = {}
        HashMap<Integer, Integer> arvosanojenMaarat = new HashMap<>();
        for (Opiskelija opiskelija : opiskelijat) {
            for (int arvosana : opiskelija.getArvosanat()) {
                // Python: maarat[arvosana] = maarat.get(arvosana, 0) + 1
                arvosanojenMaarat.put(arvosana, arvosanojenMaarat.getOrDefault(arvosana, 0) + 1);
            }
        }

        System.out.println("Arvosanajakauma:");
        for (int arvosana = 5; arvosana >= 0; arvosana--) {   // Python: for arvosana in range(5, -1, -1):
            int maara = arvosanojenMaarat.getOrDefault(arvosana, 0);
            // String.repeat ≈ Pythonin "*" * maara
            System.out.println("  " + arvosana + ": " + "*".repeat(maara));
        }

        // Viittaukset: getValmistuneet palauttaa UUDEN listan,
        // joten sen muokkaaminen ei vaikuta alkuperäiseen rekisteriin.
        ArrayList<Opiskelija> valmistuneet = getValmistuneet(opiskelijat);
        System.out.println("Valmistumisen raja (" + VALMISTUMISEN_RAJA + " op) ylittyy: "
                + valmistuneet.size() + " opiskelijalla");
    }

    private static ArrayList<Opiskelija> getValmistuneet(ArrayList<Opiskelija> opiskelijat) {
        ArrayList<Opiskelija> tulos = new ArrayList<>();
        for (Opiskelija opiskelija : opiskelijat) {
            if (opiskelija.getOpintopisteet() >= VALMISTUMISEN_RAJA) {
                tulos.add(opiskelija);
            }
        }
        return tulos;
    }

    // Tiedostot
    // Tiedostomuoto (yksi suoritus per rivi): nimi;kurssi;arvosana;laajuus
    //
    // Tunnettu rajoitus: opiskelija, jolla ei ole yhtään suoritusta, ei
    // tallennu tiedostoon. (Hyvä harjoitustehtävä: korjaa tämä!)

    private static void tallenna(ArrayList<Opiskelija> opiskelijat) {
        Path polku = Paths.get(TIEDOSTONIMI);

        // try-with-resources sulkee tiedoston automaattisesti.
        // Python: with open(TIEDOSTONIMI, "w", encoding="utf-8") as tiedosto:
        try (BufferedWriter kirjoittaja = Files.newBufferedWriter(polku, StandardCharsets.UTF_8)) {
            for (Opiskelija opiskelija : opiskelijat) {
                for (Suoritus suoritus : opiskelija.getSuoritukset()) {
                    kirjoittaja.write(opiskelija.getNimi() + ";" + suoritus.toCsv());
                    kirjoittaja.newLine();
                }
            }
            System.out.println("Tallennettu tiedostoon " + TIEDOSTONIMI);
        } catch (IOException e) {
            // Otetaan kiinni vain IOException – ei kaikkia mahdollisia virheitä.
            System.out.println("Tallennus epäonnistui: " + e.getMessage());
        }
    }

    private static void lataa(ArrayList<Opiskelija> opiskelijat) {
        Path polku = Paths.get(TIEDOSTONIMI);

        if (!Files.exists(polku)) {
            System.out.println("Tiedostoa " + TIEDOSTONIMI + " ei ole vielä olemassa.");
            return;
        }

        // Rakennetaan uusi lista ensin. Jos lukeminen epäonnistuu kesken,
        // vanhat tiedot säilyvät ennallaan.
        ArrayList<Opiskelija> ladatut = new ArrayList<>();
        int ohitetut = 0;

        // Python: with open(TIEDOSTONIMI, encoding="utf-8") as tiedosto:
        try (Scanner tiedosto = new Scanner(polku, StandardCharsets.UTF_8)) {
            while (tiedosto.hasNextLine()) {                  // Python: for rivi in tiedosto:
                String rivi = tiedosto.nextLine().trim();
                String[] osat = rivi.split(";");              // Python: osat = rivi.split(";")

                // Viallinen rivi ohitetaan eikä se kaada ohjelmaa.
                if (osat.length != 4) {                       // Huom: taulukolla .length ilman sulkeita
                    ohitetut++;
                    continue;
                }

                try {
                    String nimi = osat[0];
                    String kurssi = osat[1];
                    int arvosana = Integer.parseInt(osat[2]);
                    int laajuus = Integer.parseInt(osat[3]);

                    Opiskelija opiskelija = etsi(ladatut, nimi);
                    if (opiskelija == null) {
                        opiskelija = new Opiskelija(nimi);
                        ladatut.add(opiskelija);
                    }
                    opiskelija.lisaaSuoritus(kurssi, arvosana, laajuus);
                } catch (IllegalArgumentException e) {
                    // NumberFormatException on IllegalArgumentExceptionin aliluokka,
                    // joten tämä ottaa kiinni sekä virheelliset luvut että
                    // lisaaSuoritus-metodin heittämät virheet.
                    ohitetut++;
                }
            }
        } catch (IOException e) {
            System.out.println("Lataus epäonnistui: " + e.getMessage());
            return;
        }

        // Viittaukset: clear() ja addAll() muuttavat SAMAA listaa,
        // jonka main-metodi omistaa. Siksi muutos näkyy myös mainissa.
        // Jos kirjoittaisimme tähän "opiskelijat = ladatut;", muutos EI näkyisi
        // mainissa – vaihtaisimme vain tämän metodin oman nimilapun kohdetta.
        opiskelijat.clear();
        opiskelijat.addAll(ladatut);

        System.out.println("Ladattu " + opiskelijat.size() + " opiskelijaa.");
        if (ohitetut > 0) {
            System.out.println("Ohitettiin " + ohitetut + " virheellistä riviä.");
        }
    }
}

// Luokat
// Samaan tiedostoon voi kirjoittaa useita luokkia, mutta vain yksi saa
// olla public, ja sen nimen on vastattava tiedoston nimeä.
// Isommissa ohjelmissa jokainen luokka on omassa tiedostossaan.

/**
 * Yksi opiskelija ja hänen suorituksensa.
 */
class Opiskelija {

    // Attribuutit ovat private: niihin pääsee käsiksi vain tämän luokan metodien kautta.
    // Python: self.nimi = nimi (Pythonissa kaikki attribuutit ovat julkisia)
    private String nimi;
    private ArrayList<Suoritus> suoritukset;

    // Konstruktori. Python: def __init__(self, nimi: str):
    public Opiskelija(String nimi) {
        this.nimi = nimi;                      // this ≈ self
        this.suoritukset = new ArrayList<>();
    }

    // Getter-metodi. Javassa attribuutit luetaan tavallisesti näin.
    public String getNimi() {
        return this.nimi;
    }

    public ArrayList<Suoritus> getSuoritukset() {
        // Palautetaan KOPIO, jotta ulkopuolinen koodi ei voi muokata
        // opiskelijan suorituksia ohi lisaaSuoritus-metodin.
        // Python: return self.suoritukset[:]
        return new ArrayList<>(this.suoritukset);
    }

    public void lisaaSuoritus(String kurssi, int arvosana, int laajuus) {
        // Olio vartioi omaa tilaansa: virheellistä tietoa ei päästetä sisään.
        // Python: raise ValueError("...")
        if (arvosana < 0 || arvosana > 5) {
            throw new IllegalArgumentException("Arvosanan pitää olla 0–5, oli " + arvosana);
        }
        if (laajuus <= 0) {
            throw new IllegalArgumentException("Laajuuden pitää olla positiivinen, oli " + laajuus);
        }
        this.suoritukset.add(new Suoritus(kurssi, arvosana, laajuus));
    }

    public int getOpintopisteet() {
        int summa = 0;
        for (Suoritus suoritus : this.suoritukset) {
            // Hylätystä (0) suorituksesta ei saa opintopisteitä.
            if (suoritus.getArvosana() > 0) {
                summa += suoritus.getLaajuus();
            }
        }
        return summa;
    }

    public ArrayList<Integer> getArvosanat() {
        // Huom: listaan tallennetaan Integer, ei int. ArrayList ei voi sisältää
        // alkeistyyppejä, joten Java muuntaa int → Integer automaattisesti.
        ArrayList<Integer> arvosanat = new ArrayList<>();
        for (Suoritus suoritus : this.suoritukset) {
            arvosanat.add(suoritus.getArvosana());
        }
        return arvosanat;
    }

    /**
     * Hyväksyttyjen suoritusten keskiarvo. Palauttaa 0.0, jos suorituksia ei ole.
     */
    public double keskiarvo() {
        int summa = 0;
        int maara = 0;
        for (Suoritus suoritus : this.suoritukset) {
            if (suoritus.getArvosana() > 0) {
                summa += suoritus.getArvosana();
                maara++;                          // Python: maara += 1
            }
        }

        if (maara == 0) {
            return 0.0;   // vältetään nollalla jakaminen
        }
        return (double) summa / maara;            // (double) estää kokonaislukujaon
    }

    // toString ≈ Pythonin __str__. Kutsutaan automaattisesti, kun olio tulostetaan.
    @Override
    public String toString() {
        return this.nimi + " (" + getOpintopisteet() + " op)";
    }
}

/**
 * Yksi kurssisuoritus.
 * Javassa ei ole tuplea, joten toisiinsa kuuluvat arvot kootaan omaksi luokakseen.
 * Python: suoritus = ("Ohjelmoinnin perusteet", 5, 5)
 */
class Suoritus {

    // final = arvoa ei voi muuttaa konstruktorin jälkeen (vrt. tuple on muuttumaton)
    private final String kurssi;
    private final int arvosana;
    private final int laajuus;

    public Suoritus(String kurssi, int arvosana, int laajuus) {
        this.kurssi = kurssi;
        this.arvosana = arvosana;
        this.laajuus = laajuus;
    }

    public String getKurssi() {
        return this.kurssi;
    }

    public int getArvosana() {
        return this.arvosana;
    }

    public int getLaajuus() {
        return this.laajuus;
    }

    public String toCsv() {
        return this.kurssi + ";" + this.arvosana + ";" + this.laajuus;
    }

    @Override
    public String toString() {
        return this.kurssi + ": " + this.arvosana + " (" + this.laajuus + " op)";
    }
}