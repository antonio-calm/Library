package Library;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Created by Anton Kalmykov on 27.08.2016.
 */
public class Book {
    private final SimpleStringProperty title;
    private final SimpleStringProperty author;
    private final SimpleStringProperty subject;
    private final SimpleIntegerProperty pages;
    private SimpleIntegerProperty pagesRead;

    public Book(SimpleStringProperty title, SimpleStringProperty author, SimpleStringProperty subject, SimpleIntegerProperty pages, SimpleIntegerProperty readPages) {
        this.title = title;
        this.author = author;
        this.subject = subject;
        this.pages = pages;
        this.pagesRead = readPages;
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public void setAuthor(String author) {
        this.author.set(author);
    }

    public void setSubject(String subject) {
        this.subject.set(subject);
    }

    public void setPages(int pages) {
        this.pages.set(pages);
    }

    public void setPagesRead(int pagesRead) {
        this.pagesRead.set(pagesRead);
    }

    public String getTitle() {

        return title.get();
    }

    public SimpleStringProperty titleProperty() {
        return title;
    }

    public String getAuthor() {
        return author.get();
    }

    public SimpleStringProperty authorProperty() {
        return author;
    }

    public String getSubject() {
        return subject.get();
    }

    public SimpleStringProperty subjectProperty() {
        return subject;
    }

    public int getPages() {
        return pages.get();
    }

    public IntegerProperty pagesProperty() {
        return pages;
    }

    public int getPagesRead() {
        return pagesRead.get();
    }

    public IntegerProperty pagesReadProperty() {
        return pagesRead;
    }

    public void addPagesRead(int pagesRead) {
        setPagesRead(getPagesRead() + pagesRead);
    }

    @Override
    public String toString() {
        return title.getValue();
    }

}
