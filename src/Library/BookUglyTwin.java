package Library;

import java.io.Serializable;

/**
 * Created by Anton Kalmykov on 27.08.2016.
 */
class BookUglyTwin implements Serializable {
    String title;
    String author;
    String subject;
    int pages;
    int readPages;

    public BookUglyTwin(String title, String author, String subject, int pages, int readPages) {
        this.title = title;
        this.author = author;
        this.subject = subject;
        this.pages = pages;
        this.readPages = readPages;
    }
}
