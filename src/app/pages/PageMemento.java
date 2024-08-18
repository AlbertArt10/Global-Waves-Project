package app.pages;

import java.util.ArrayList;
import java.util.List;

/**
 * Clasa PageMemento gestionează starea istoricului de navigare al utilizatorului,
 *       permițând navigarea înapoi și înainte între paginile vizitate.
 * Aceasta implementează Memento Pattern pentru a păstra stările anterioare și ulterioare
 *        ale navigării.
 */
public class PageMemento {
    private List<Page> pageHistory = new ArrayList<>();
    private int currentPageIndex = -1;

    /**
     * Adaugă o nouă pagină la istoric și actualizează indexul paginii curente.
     * @param page Pagina de adăugat în istoric.
     */
    public void addPage(final Page page) {
        // Incrementarea indexului paginii curente
        currentPageIndex++;

        // Limitarea istoricului la paginile până la pagina curentă
        pageHistory = pageHistory.subList(0, currentPageIndex);

        // Adăugarea noii pagini la istoric
        pageHistory.add(page);
    }

    /**
     * Returnează pagina anterioară din istoric dacă este posibil.
     * @return Pagina anterioară sau null dacă nu este disponibilă.
     */
    public Page getPreviousPage() {
        // Verifică dacă există o pagină anterioară în istoric
        if (currentPageIndex > 0) {
            // Decrementarea indexului pentru a naviga înapoi în istoric
            currentPageIndex--;

            // Returnează pagina anterioară din istoric
            return pageHistory.get(currentPageIndex);
        }
        // Utilizatorul se află pe prima pagină din istoric
        return null;
    }

    /**
     * Returnează pagina următoare din istoric dacă este posibil.
     * @return Pagina următoare sau null dacă nu este disponibilă.
     */
    public Page getNextPage() {
        // Verifică dacă există o pagină următoare în istoric
        if (currentPageIndex < pageHistory.size() - 1) {
            // Incrementarea indexului pentru a naviga înainte în istoric
            currentPageIndex++;

            // Returnează pagina următoare din istoric
            return pageHistory.get(currentPageIndex);
        }
        // Utilizatorul se află pe ultima pagină din istoric
        return null;
    }

    /**
     * Verifică dacă este posibilă navigarea la pagina anterioară.
     * @return true dacă există o pagină anterioară, altfel false.
     */
    public boolean canGoToPreviousPage() {
        // Verifică dacă există o pagină anterioară disponibilă în istoric
        if (currentPageIndex > 0) {
            return true;
        }
        // Utilizatorul se află pe prima pagină din istoric
        return false;
    }

    /**
     * Verifică dacă este posibilă navigarea la pagina următoare.
     * @return true dacă există o pagină următoare, altfel false.
     */
    public boolean canGoToNextPage() {
        // Verifică dacă există o pagină următoare disponibilă în istoric
        if (currentPageIndex < pageHistory.size() - 1) {
            return true;
        }
        // Utilizatorul se află pe ultima pagină din istoric
        return false;
    }

    /**
     * Resetează istoricul de pagini foward de pagina curentă.
     */
    public void resetForwardHistory() {
        // Verifică dacă există pagini foward (după pagina curentă) în istoric
        if (currentPageIndex < pageHistory.size() - 1) {
            // Resetează istoricul 'foward' de pagina curentă
            pageHistory = pageHistory.subList(0, currentPageIndex + 1);
        }
    }
}
