# ANTONESCU ALBERT
## 324CD
## Global Waves - Analytics & Recommendations

## Descriere:
Acest proiect reprezintă o aplicație asemănătoare ca 
funcționalități cu Spotify, simulând diferite acțiuni făcute de utilizatori și artiști. 
Aceste acțiuni vor fi simulate folosind niște comenzi primite în fișierele de input. 
Astfel, perspectiva din care rezolvați tema este aceea a unui admin, ce percepe toate 
acțiunile realizate de utilizatori și poate genera diferite rapoarte legate de toți 
utilizatorii sau artiștii.

## Cerința completă:
https://ocw.cs.pub.ro/courses/poo-ca-cd/teme/proiect/etapa1
https://ocw.cs.pub.ro/courses/poo-ca-cd/teme/proiect/etapa2
https://ocw.cs.pub.ro/courses/poo-ca-cd/teme/proiect/etapa3

## Comanda "wrapped":

Pentru a implementa această comandă, am folosit design pattern-ul
Template Method Pattern astfel:

    
  * Am creat clasa abstractă `StatsTemplate` care definește template-ul
    pentru calculul statisticilor. Include metoda `calculateStats()` care este
    template method-ul și alte metode abstracte care trebuie implementate
    de subclase.


  * Clasele `UserStats`, `ArtistStats`, `HostStats` extind `StatsTemplate` 
    și furnizează implementarea specifică pentru calculul statisticilor 
    user-ului, artist-ului sau host-ului respectiv.


  * În clasa `Admin` am creat metoda `wrapped()` ce utilizează `StatsTemplate` 
    pentru a returna statisticile corespunzătoare. Aici clasa `StatFactory` 
    va decide strategia de calcul a statisticilor în funcție de tipului de 
    utilizator.


  * Întreaga logică a statisticilor se bazează pe un contor `listenCount`,
o structură Map<String, Integer> `userListenCounts` și o structură
Set<String> `uniqueListeners`, unde:
    + `listenCount` reprezintă nr. de ascultări (în total) al
melodiei/episodului
    + `userListenCounts` reprezintă o Hartă cu numărul de ascultări ale
melodiei/episodului pentru fiecare user
    + `uniqueListeners` reprezintă un Set cu utilizatorii unici care au
ascultat melodia/episodul


  * Acestea sunt contorizate la fiecare Play al unei melodii, oricât de
puțin timp a trecut din aceasta. Logica pentru acest lucru este implementată
în metodele `User.load()` si în `Player.next()`


## Sistemul de Monetizare:

  * Pentru a implementa Sistemul de Monetizare am centralizat toată logica de
    calcul si distribuie a venitului într-o singură clasă `RevenueService`,
    separând-o de alte funcționalități ale programului.


  * La fiecare comandă de `cancelPremium()` se va apela metoda specifică de distribuire a
    venitului Premium `revenueFromPremiumListens()`. De asemenea, aceasta este apelată la finalul programului
    și se obține statisticile de monetizare pentru fiecare artist prin metoda
    `calculateArtistRevenues()` din clasa `Admin`.


  * Pentru distribuirea venitului din reclame, în clasa `Player` se verifică
    dacă melodia de începe a fi redată este o reclamă (Ad Braker) și în caz
    afirmativ se va apela metoda specifică de distribuire a venitului din reclame
    (Ad-uri) `revenueFromFreeListens()` aflată în clasa `RevenueService`.


  * De fiecare dată când un user cumpără un merch, se apelează metoda
    `addMerchRevenue()` din clasa `Artist`, adăugând astfel prețul merch-ului la
    venitul total al artistului obținut din vânzarea de merch `merchRevenue`.


  * Logica de a urmări care este `mostProfitableSong` al artistului se bazează
pe o structură Map<String, Double> `artistSongsRevenue` care urmărește venitul
generat de fiecare melodie a artistului.


  * Structurile List<.Song.> `songsListenedPremium` și `songsListenedFree` sunt
utilizate pentru a urmări melodiile ascultate de user pe modul Premium sau pe modul
"sărac". Logica pentru acestea este implementată în `User.load()`
și în `Player.next()`.


## Sistemul de Notificări:

  * **Descriere:**
    Pentru a implementa Sistenul de Notificări am folosit
    Observer Pattern care permite unui subiect (ContentCreator) să notifice
    observatorii (Subscriberi - utilizatori) despre diverse evenimente.


  * **Implementare:**
    + **Subscriber Interface:** Utilizatorii implementează această
      interfață pentru a primi notificări.
    + **Notification Class:** Reprezintă structura unei notificări, având
      un nume și descriere.
    + **ContentCreator Class:** Subiectul care trimite notificări. Include
      metode pentru adăugarea, eliminarea abonaților și trimiterea notificărilor.
    + **User Class:** Utilizatorii primesc și gestionează notificările.
      Implementează metoda `update()` din `Subscriber` pentru a adăuga notificările
      în lista lor.


  * **Flow-ul Sistemului:**
    + Utilizatorii se abonează la un `ContentCreator`. Starea de abonare
      se schimbă folosind metoda `toggleSubscription()` din clasa `ContentCreator`
    + Când se produce un eveniment (ex: adăugare album nou),
      `ContentCreator` generează o notificare.
    + `ContentCreator` folosește metoda `notifySubscribers()` pentru
      a trimite notificarea tuturor abonaților.
    + Fiecare utilizator abonat primește notificarea prin metoda
      `update()` și o stochează.
    + Utilizatorii pot accesa notificările prin metoda `getNotifications()`
      care returnează și șterge notificările.


## Sistemul de PageNavigation:

  * **Descriere:** Pentru a implementa Sistemul de PageNavigation am implementat
    Memento Pattern pentru a gestiona starea istoricului de navigare în clasa
    `PageMemento`, permițând utilizatorului să navigheze prin istoric fără a
    pierde contextul. Utilizatorul interacționează cu acest istoric prin
    metodele `goToPreviousPage()` și `goToNextPage()`, iar istoricul este actualizat
    la fiecare schimbare de pagină prin `changePage()`.


  * **Flow-ul Sistemului:**
    + Utilizatorul accesează o pagină, care este adăugată în istoric.
    + Utilizatorul poate naviga înapoi și înainte folosind metodele
      `goToPreviousPage()` și `goToNextPage()`.
    + Când se accesează o pagină nouă, istoricul "forward" este resetat
      pentru a asigura un istoric de navigare liniar.
  

## Sistemul de Recommendations:

* **Descriere:** Pentru implementarea Sistemului de Recommendations, am utilizat 
două Design Patterns principale: Factory Pattern și Strategy Pattern. Factory Pattern 
este folosit pentru a crea dinamic instanțe ale claselor care implementează interfața 
`RecommendationStrategy`, iar Strategy Pattern permite schimbarea flexibilă a algoritmilor 
de generare a recomandărilor.


* **Implementare:** 
  + **RecommendationStrategy Interface:** Defineste interfața pentru strategiile de recomandare 
  în sistem.
  + **RecommendationFactory Class:** Factory pentru crearea de strategii de recomandare, bazate 
  pe un tip specificat. Returnează instanțe ale claselor care implementează `RecommendationStrategy`.
  + **RandomSongRecommendation Class:** Implementează interfața `RecommendationStrategy` și generează 
  recomandări de melodii în mod aleatoriu, bazându-se pe genul melodiei curent ascultate de utilizator.
  + **RandomPlaylistRecommendation Class:** Implementează interfața `RecommendationStrategy` și generează 
  recomandări de playlist-uri în mod aleatoriu, bazându-se pe genurile muzicale preferate de utilizator.
  + **FansPlaylistRecommendation Class:** Implementează interfața `RecommendationStrategy` și generează 
  recomandări de playlist-uri pe baza preferințelor fanilor unui anumit artist.


## Design Patterns folosite:
- **SINGLETON:** Exista o singura clasa `Admin` care contine toate datele aplicatiei: useri, melodii, 
playlisturi etc
- **FACTORY:** folosit la crearea de strategii de calcul a statisticilor în funcție de tipului de
    utilizator și la creare de strategii de recomandare, bazate
    pe un tip specificat.
- **TEMPLATE METOD Pattern:**  folosit pentru comanda `wrapped`
- **OBSERVER:** folosit la *Sistemul de Notifications*
- **MEMENTO Pattern:** folosit la *Sistemul de PageNavigation*
- **STRATEGY:** folosit la *Sistemul de Recommendations*

