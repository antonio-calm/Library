package Library;

import java.io.Serializable;

/**
 * Created by Anton Kalmykov on 27.08.2016.
 */
public class LogUglyTwin implements Serializable {
    String date;
    int pagesRead;

    public LogUglyTwin(String date, int pagesRead) {
        this.date = date;
        this.pagesRead = pagesRead;
    }
}
