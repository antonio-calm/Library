package Library;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Anton Kalmykov on 27.08.2016.
 */
public class ProfileUglyTwin implements Serializable {
    String firstName;
    String lastName;
    int plan;
    int readPages;
    String photo;
    ArrayList<BookUglyTwin> books;
    ArrayList<LogUglyTwin> progressLog;

    public ProfileUglyTwin(String firstName, String lastName, int plan, int readPages, String photo, ArrayList<BookUglyTwin> books, ArrayList<LogUglyTwin> progressLog) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.plan = plan;
        this.readPages = readPages;
        this.photo = photo;
        this.books = books;
        this.progressLog = progressLog;
    }
}
