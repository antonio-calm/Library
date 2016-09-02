package Library;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.image.Image;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Anton Kalmykov on 27.08.2016.
 */
public class Profile {
    private SimpleStringProperty firstName;
    private SimpleStringProperty lastName;
    private SimpleIntegerProperty plan;
    private SimpleIntegerProperty readPages;
    ObservableList<Book> books;
    ObservableList<XYChart.Data<String, Number>> progressLog;
    private Image photo;
    private String photoFileName;

    public Profile(SimpleStringProperty firstName, SimpleStringProperty lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
        plan = new SimpleIntegerProperty(20);
        readPages = new SimpleIntegerProperty(0);
        books = FXCollections.observableArrayList();
        progressLog = FXCollections.observableArrayList();
        photo = new Image("no photo.png");
        photoFileName = "";
    }

    public Image getPhoto() {
        return photo;
    }

    public void setPhoto(Image photo) {
        this.photo = photo;
    }

    public String getFirstName() {
        return firstName.get();
    }

    public SimpleStringProperty firstNameProperty() {
        return firstName;
    }

    public String getLastName() {
        return lastName.get();
    }

    public SimpleStringProperty lastNameProperty() {
        return lastName;
    }

    public void setFirstName(String firstName) {
        this.firstName.set(firstName);
    }

    public void setLastName(String lastName) {
        this.lastName.set(lastName);
    }

    public int getPlan() {
        return plan.get();
    }

    public void setPlan(int plan) {
        this.plan.set(plan);
    }

    public SimpleIntegerProperty planProperty() {
        return plan;
    }

    public int getReadPages() {
        return readPages.get();
    }

    public SimpleIntegerProperty readPagesProperty() {
        return readPages;
    }

    public void setReadPages(int readPages) {
        this.readPages.set(readPages);
    }

    public String getPhotoFileName() {
        return photoFileName;
    }

    public void setPhotoFileName(String photoFileName) {
        this.photoFileName = photoFileName;
    }

    public ObservableList<PieChart.Data> generateSubjectReadData() {
        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
        ObservableList<String> subjects = getSubjects();
        for (String subject : subjects) {
            data.add(new PieChart.Data(subject, 0));
        }

        for (Book book : books) {
            for (PieChart.Data element : data) {
                if (book.getSubject().equals(element.getName())) {
                    element.setPieValue(element.getPieValue() + book.getPagesRead());
                    break;
                }
            }
        }
        return data;
    }

    public ObservableList<String> getSubjects() {
        ObservableList<String> subjects = FXCollections.observableArrayList();
        for (Book book : books) {
            if (!subjects.contains(book.getSubject())) {
                subjects.add(book.getSubject());
            }
        }
        return subjects;
    }

    public ObservableList<XYChart.Data<String, Number>> generateLastTwoWeeksChartActualData() {
        ObservableList<XYChart.Data<String, Number>> chartData = FXCollections.observableArrayList();
        Date today = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM.dd");

        for (int i = 13; i >= 0; i--) {
            String dateString = dateFormat.format(new Date(today.getTime() - i * (1000 * 3600 * 24)));
            chartData.add(new XYChart.Data(dateString, 0));
        }

        for (XYChart.Data day : chartData) {
            for (XYChart.Data log : progressLog) {
                if (log.getXValue().equals(day.getXValue())) {
                    day.setYValue((int) day.getYValue() + (int) log.getYValue());
                }
            }
        }
        return chartData;
    }

    public ObservableList<XYChart.Data<String, Number>> generateLastTwoWeeksChartPlanData() {
        ObservableList<XYChart.Data<String, Number>> chartData = FXCollections.observableArrayList();
        Date today = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM.dd");

        for (int i = 13; i >= 0; i--) {
            String dateString = dateFormat.format(new Date(today.getTime() - i * (1000 * 3600 * 24)));
            chartData.add(new XYChart.Data(dateString, getPlan()));
        }
        return chartData;
    }


    public int getTwoWeeksAverage() {
        ObservableList<XYChart.Data<String, Number>> twoWeeksData = generateLastTwoWeeksChartActualData();
        int sum = 0;
        for (XYChart.Data day : twoWeeksData) {
            sum += (int) day.getYValue();
        }
        return sum / 14;
    }
}

