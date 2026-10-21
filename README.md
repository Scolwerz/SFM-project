# Autószerviz időpontfoglaló és munkakezelő rendszer
| [Github](https://github.com/Scolwerz/SFM-project.git)  |  [Trello](https://trello.com/b/ZeqjQMMu/sfm-projekt) |


## Információk
**Név:** Autószerviz időpontfoglaló és munkakezelő rendszer

**Frontend:** HTML, CSS, JS, NGINX

**Backend:** Java, Spring Boot

**Database:** MySQL, Spring Data JPA, Hibernate

**Java version:** 17

**Build:** Maven

**Deploy:** Docker

## Leírás
Egy olyan webalkalmazás, amely egyszerűbbé és átláthatóbbá teszi az autószervizek időpontfoglalási és munkaszervezési folyamatait.
Kezelőfelületet nyújt az ügyfelek számára online időpontot foglalására, valamint nyomon követhetik a javítás állapotát.
Az autószerelők egy ticketkezelő rendszeren keresztül kezelhetik a beérkező munkákat, megtekinthetik a javításokhoz kapcsolódó információkat.
Az autószervítben lévő alkatrészek leltározásában is segítséget nyújt.

## Fő Funkciók

### Ügyfelek számára
- Regisztráció és bejelentkezés.
- Online időpont foglalás.
- Lefoglalt időpontok megtekintése és kezelése.
- A javítási munkák állapotának nyomon követése.

### Autószerelők számára
- Beérkező munkák és feladatok megtekintése.
- Ticketek kezelése és állapotuk módosítása.
- A folyamatban lévő és lezárt munkák áttekintése.
- Elvégzett munkák nyilvántartása.

### Adminisztráció
- Ügyfelek és járművek adatainak kezelése.
- Időpontok és szervizmunkák kezelése.
- Felhasználók és jogosultságok kezelése.
- A szerviz munkafolyamatainak áttekintése.


## Adatbázis-struktúra
**MÉG NICNS IMPLEMENTÁLVA**

## Alkalmazás futtatása
A gyökér könyvtárból:
   ```bash
   docker compose up --build
   ```
A frontend a `http://localhost:3000`, a backend a `http://localhost:8080`, a MySQL adatbázis pedig a `localhost:3306` érhető el.

### Leállítás:
```bash
docker compose down
```

The `mysql_data` named volume retains database data. To remove it too, run:
```bash
docker compose down -v
```


### Portok:
|   Container   |   Port    |
| ------------- | --------- |
|   frontend    |   3000    |
|   backend     |   8080    |
|   mysql-db    |   3306    |


## Jövőbeli fejlesztési lehetőségek
- Automatikus e-mailes vagy egyéb értesítések küldése az időpontokról és a munkák állapotáról.
- Online számlázás és fizetés integrálása.
- Alkatrész- és készletnyilvántartás kialakítása.
- Szervizelőzmények és javítási statisztikák megjelenítése.
- Több autószerviz telephelyének támogatása.
- Szerelői munkaterhelés és szervizkapacitás kezelése.
- Naptárnézet és automatikus időpontütközés-ellenőrzés.
