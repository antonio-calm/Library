package Library;

import javafx.application.Application;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by Anton Kalmykov on 27.08.2016.
 */
public class Library extends Application {


    Profile profile = new Profile(new SimpleStringProperty(""), new SimpleStringProperty(""));
    final String PHOTO_DIR_NAME = ".library.photos";
    final String SAVE_DIR_NAME = "profiles";

    @Override
    public void init() throws Exception {
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        File dirPhoto = new File(PHOTO_DIR_NAME);
        dirPhoto.mkdir();

        TabPane root = new TabPane();
        root.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        primaryStage.setScene(new Scene(root));
        primaryStage.setMinHeight(470);
        primaryStage.setMaxHeight(470);
        primaryStage.setMinWidth(700);
        primaryStage.setMaxWidth(700);
        primaryStage.setTitle("My library");

        //-------------------------------
        //Вкладка с таблицей "My library"
        Tab libraryTab = new Tab();
        libraryTab.setText("My library");

        //Создаем подпись к таблице
        Label tableLabel = new Label("Books I want to read:");
        tableLabel.setFont(new Font(12));

        //Создаем таблицу
        TableView table = new TableView();
        table.setMaxHeight(300);
        table.setMinHeight(300);
        table.setMaxWidth(470);
        table.setMinWidth(470);
        table.setEditable(true);

        TableColumn titleColumn = new TableColumn("Title");
        titleColumn.setMinWidth(148);// почему-то если поставить 149-150, таблица начинает сжиматься
        titleColumn.setMaxWidth(148);// хотя ширина таблицы 470, а столбцы 150, 100, 80, 70 и 70
        titleColumn.setCellValueFactory(new PropertyValueFactory<Book, String>("title"));
        TableColumn authorColumn = new TableColumn("Author");
        authorColumn.setMinWidth(100);
        authorColumn.setMaxWidth(100);
        authorColumn.setCellValueFactory(new PropertyValueFactory<Book, String>("author"));
        TableColumn subjectColumn = new TableColumn("Subject");
        subjectColumn.setMinWidth(80);
        subjectColumn.setMaxWidth(80);
        subjectColumn.setCellValueFactory(new PropertyValueFactory<Book, String>("subject"));
        TableColumn pagesColumn = new TableColumn("Pages");
        TableColumn pagesTotalColumn = new TableColumn("Total");
        pagesTotalColumn.setMinWidth(70);
        pagesTotalColumn.setMaxWidth(70);
        pagesTotalColumn.setCellValueFactory(new PropertyValueFactory<Book, String>("pages"));
        TableColumn pagesReadColumn = new TableColumn("Read");
        pagesReadColumn.setMinWidth(70);
        pagesReadColumn.setMaxWidth(70);
        pagesReadColumn.setCellValueFactory((new PropertyValueFactory<Book, String>("pagesRead")));

        pagesColumn.getColumns().addAll(pagesTotalColumn, pagesReadColumn);

        table.getColumns().addAll(titleColumn, authorColumn, subjectColumn, pagesColumn);
        table.setItems(profile.books);

        //Объединяем подпись к таблице и таблицу в VBox
        VBox tableBox = new VBox();
        tableBox.getChildren().addAll(tableLabel, table);
        tableBox.setPadding(new Insets(10, 0, 5, 10));

        //Создаем поля для ввода
        TextField addTitle = new TextField();
        addTitle.setPromptText("Enter title here");
        TextField addAuthor = new TextField();
        addAuthor.setPromptText("Enter author here");
        TextField addSubject = new TextField();
        addSubject.setPromptText("Enter subject here");
        TextField addPages = new TextField();
        addPages.setPromptText("Enter number of pages here");

        //Создаем кнопки
        Button addBookButton = new Button("Add");

        Button deleteBookButton = new Button("Delete");

        //Объединяем кнопки в HBox
        HBox editingButtonGroup = new HBox();
        editingButtonGroup.getChildren().addAll(addBookButton, deleteBookButton);
        editingButtonGroup.setSpacing(3);

        //Объединяем поля для ввода и групу кнопок в VBox
        VBox inputBox = new VBox();
        inputBox.getChildren().addAll(addTitle, addAuthor, addSubject, addPages, editingButtonGroup);
        inputBox.setMinWidth(200);
        inputBox.setPadding(new Insets(26, 10, 5, 10));
        inputBox.setSpacing(5);

        //Объединяем VBox с таблицей и VBox c полями для ввода и кнопками в HBox
        HBox fullContentGroupMyLibrary = new HBox(tableBox, inputBox);

        //Определяем полученный контент на вкладку "My library"
        libraryTab.setContent(fullContentGroupMyLibrary);

        //------------------------------------------
        //Вкладка с графиком прогресса "My progress"
        Tab progressTab = new Tab();
        progressTab.setText("My progress");

        //Создаем линейный график
        Axis xAxis = new CategoryAxis();
        Axis yAxis = new NumberAxis();
        yAxis.setLabel("Pages per day");

        LineChart<String, Number> lastTwoWeeksChart = new LineChart(xAxis, yAxis);
        lastTwoWeeksChart.setMinSize(430, 400);
        lastTwoWeeksChart.setMaxSize(430, 400);

        XYChart.Series<String, Number> actualSeries = new LineChart.Series(profile.generateLastTwoWeeksChartActualData());
        actualSeries.setName("Actual");
        XYChart.Series<String, Number> planSeries = new LineChart.Series(profile.generateLastTwoWeeksChartPlanData());
        planSeries.setName("Plan");

        lastTwoWeeksChart.legendVisibleProperty().setValue(true);
        lastTwoWeeksChart.setTitle("Last 2 weeks");

        lastTwoWeeksChart.setLegendSide(Side.BOTTOM);
        lastTwoWeeksChart.getData().addAll(actualSeries, planSeries);

        //Создаем круговую диаграмму
        ObservableList<PieChart.Data> pieChartDate = profile.generateSubjectReadData();
        PieChart subjectChart = new PieChart(pieChartDate);
        subjectChart.setLabelsVisible(false);
        subjectChart.setLegendSide(Side.TOP);
        subjectChart.setMaxHeight(308);

        //Создаем поля для ввода прочитанных страниц и соответствующие элементы управления
        TextField addReadPagesQtty = new TextField();
        addReadPagesQtty.setPromptText("Enter number of pages");
        addReadPagesQtty.setMinWidth(140);
        addReadPagesQtty.setMaxWidth(140);

        ChoiceBox chooseBook = new ChoiceBox(profile.books);
        chooseBook.setMinWidth(140);
        chooseBook.setMaxWidth(140);

        DatePicker chooseDate = new DatePicker();
        chooseDate.setMinWidth(100);
        chooseDate.setMaxWidth(100);

        Button addReadPagesButton = new Button("Read");
        addReadPagesButton.setMinWidth(100);
        addReadPagesButton.setMaxWidth(100);

        //Объединяем все в группы
        HBox addPagesGroup1 = new HBox(addReadPagesQtty, addReadPagesButton);
        addPagesGroup1.setSpacing(5);

        HBox addPagesGroup2 = new HBox(chooseBook, chooseDate);
        addPagesGroup2.setSpacing(5);

        VBox rightGroup = new VBox(subjectChart, addPagesGroup1, addPagesGroup2);
        rightGroup.setSpacing(5);
        rightGroup.setPadding(new Insets(0, 0, 10, 0));
        HBox fullContentGroupMyProgress = new HBox(lastTwoWeeksChart, rightGroup);
        progressTab.setContent(fullContentGroupMyProgress);

        //-----------------------------------------
        //Вкладка с детальной информацией "Details"
        Tab detailsTab = new Tab();
        detailsTab.setText("Details");

        Label detailsLabel = new Label("Choose book for details:");
        ChoiceBox chooseBookForDetails = new ChoiceBox(profile.books);
        chooseBookForDetails.setMinWidth(200);

        Button detailsButton = new Button("Show");
        detailsButton.setMinWidth(120);

        Text detailsTextTitle = new Text("Book title:");
        Text detailsTextAuthor = new Text("Author:");
        Text detailsTextSubject = new Text("Subject:");
        Text detailsTextPages = new Text("Total pages:");
        Text detailsTextRead = new Text("Read pages:");
        Text detailsTextPercentage = new Text("Read percentage:");

        Text detailsTextTitleShow = new Text();
        Text detailsTextAuthorShow = new Text();
        Text detailsTextSubjectShow = new Text();
        Text detailsTextPagesShow = new Text();
        Text detailsTextReadShow = new Text();
        Text detailsTextPercentageShow = new Text();

        Separator separator = new Separator();
        separator.setOrientation(Orientation.VERTICAL);

        PieChart detailsChart = new PieChart();
        detailsChart.setLabelsVisible(false);
        detailsChart.setLegendSide(Side.LEFT);

        Text nameText = new Text(profile.getFirstName() + " " + profile.getLastName());
        nameText.setFont(new Font(17));

        Button addPhotoButton = new Button("Change photo");
        addPhotoButton.setMinWidth(100);

        Text bookQttyTextTitle = new Text("Books in library:");
        Text pagesReadTextTitle = new Text("Pages read:");
        Text planTextTitle = new Text("Daily plan:");
        Text actualTextTitle = new Text("2 weeks average:");
        Text percentageTextTitle = new Text("Performance:");

        Text bookQttyTextShow = new Text(profile.books.size() + "");
        Text pagesReadTextShow = new Text(profile.getReadPages() + "");
        Text planTextShow = new Text(profile.getPlan() + "");
        Text actualTextShow = new Text(profile.getTwoWeeksAverage() + "");
        Text percentageTextShow = new Text((profile.getTwoWeeksAverage() * 100) / profile.getPlan() + "%");

        Button saveButton = new Button("Save");
        saveButton.setMinWidth(100);

        Button loadButton = new Button("Load");
        loadButton.setMinWidth(100);

        Button changePlanButton = new Button("Change plan");
        changePlanButton.setMinWidth(100);

        Button exitButton = new Button("Exit");
        exitButton.setMinWidth(100);

        ImageView avatar = new ImageView(profile.getPhoto());
        avatar.setFitHeight(100);
        avatar.setFitWidth(100);

        //Объединяем элементы вкладки в группы:
        //--------------------правая сторона------------------------
        HBox chooseBookDetailsButtonGroup = new HBox(chooseBookForDetails, detailsButton);
        chooseBookDetailsButtonGroup.setSpacing(5);

        VBox chooseBookDetailsGroup = new VBox(detailsLabel, chooseBookDetailsButtonGroup);
        chooseBookDetailsGroup.setSpacing(5);

        VBox textDetailsTitleLeftGroup = new VBox(detailsTextTitle, detailsTextAuthor, detailsTextSubject, detailsTextPages, detailsTextRead, detailsTextPercentage);
        textDetailsTitleLeftGroup.setSpacing(5);
        textDetailsTitleLeftGroup.setPadding(new Insets(0, 0, 0, 10));

        VBox detailsTextShowGroup = new VBox(detailsTextTitleShow, detailsTextAuthorShow, detailsTextSubjectShow, detailsTextPagesShow, detailsTextReadShow, detailsTextPercentageShow);
        detailsTextShowGroup.setSpacing(5);

        HBox detailsTextLeftGroup = new HBox(textDetailsTitleLeftGroup, detailsTextShowGroup);
        detailsTextLeftGroup.setSpacing(15);

        VBox leftSideGroupDetails = new VBox(chooseBookDetailsGroup, detailsTextLeftGroup, detailsChart);
        leftSideGroupDetails.setMaxWidth(340);
        leftSideGroupDetails.setMinWidth(340);
        leftSideGroupDetails.setPadding(new Insets(10, 0, 10, 10));
        leftSideGroupDetails.setSpacing(10);

        //--------------------левая сторона------------------------
        VBox nameAndButtonGroup = new VBox(nameText, addPhotoButton);
        nameAndButtonGroup.setAlignment(Pos.TOP_RIGHT);
        nameAndButtonGroup.setSpacing(5);

        HBox profileGroup = new HBox(nameAndButtonGroup, avatar);
        profileGroup.setAlignment(Pos.TOP_RIGHT);
        profileGroup.setSpacing(15);

        VBox textDetailsTitleRightGroup = new VBox(bookQttyTextTitle, pagesReadTextTitle, planTextTitle, actualTextTitle, percentageTextTitle);
        textDetailsTitleRightGroup.setAlignment(Pos.TOP_RIGHT);
        textDetailsTitleRightGroup.setSpacing(5);

        VBox textDetailsShowGroup = new VBox(bookQttyTextShow, pagesReadTextShow, planTextShow, actualTextShow, percentageTextShow);
        textDetailsShowGroup.setSpacing(5);

        HBox detailsTextRightGroup = new HBox(textDetailsTitleRightGroup, textDetailsShowGroup);
        detailsTextRightGroup.setAlignment(Pos.TOP_RIGHT);
        detailsTextRightGroup.setSpacing(15);
        detailsTextRightGroup.setPadding(new Insets(0, 10, 0, 0));

        HBox saveLoadButtonGroup = new HBox(saveButton, loadButton);
        saveLoadButtonGroup.setAlignment(Pos.TOP_RIGHT);
        saveLoadButtonGroup.setSpacing(15);

        HBox createExitButtonGroup = new HBox(changePlanButton, exitButton);
        createExitButtonGroup.setAlignment(Pos.TOP_RIGHT);
        createExitButtonGroup.setSpacing(15);

        VBox profileButtonsGroup = new VBox(saveLoadButtonGroup, createExitButtonGroup);
        profileButtonsGroup.setSpacing(5);
        profileButtonsGroup.setMinHeight(120);
        profileButtonsGroup.setAlignment(Pos.BOTTOM_RIGHT);

        VBox rightSideGroupDetails = new VBox(profileGroup, detailsTextRightGroup, profileButtonsGroup);
        rightSideGroupDetails.setMinWidth(320);
        rightSideGroupDetails.setSpacing(20);
        rightSideGroupDetails.setPadding(new Insets(20, 0, 0, 0));

        HBox fullContentGroupDetails = new HBox(leftSideGroupDetails, separator, rightSideGroupDetails);

        detailsTab.setContent(fullContentGroupDetails);

        //-----------------------------------------
        //-----------------------------------------
        //Описываем поведение активных элементов

        //Кнопка добавления книги (вкладка My Library)
        addBookButton.setOnAction((ActionEvent event) -> {

            if (isDigit(addPages.getText()) && Integer.parseInt(addPages.getText().trim()) > 0 && !addTitle.getText().equals("") && !addAuthor.getText().equals("") && !addSubject.getText().equals("")) {

                if (!profile.getSubjects().contains(addSubject.getText().trim())) {
                    pieChartDate.add(new PieChart.Data(addSubject.getText().trim(), 0));
                }


                profile.books.add(
                        new Book(
                                new SimpleStringProperty(addTitle.getText().trim()),
                                new SimpleStringProperty(addAuthor.getText().trim()),
                                new SimpleStringProperty(addSubject.getText().trim()),
                                new SimpleIntegerProperty(Integer.parseInt(addPages.getText().trim())),
                                new SimpleIntegerProperty(0)));

                addTitle.clear();
                addAuthor.clear();
                addSubject.clear();
                addPages.clear();
                bookQttyTextShow.setText(profile.books.size() + "");
            }
        });

        //Кнопка удаления книги (вкладка My Library)
        deleteBookButton.setOnAction((ActionEvent) -> {
                    profile.books.remove(table.getSelectionModel().getSelectedItem());
                    bookQttyTextShow.setText(profile.books.size() + "");
                }
        );

        //Кнопка добавления прочитанных страниц (вкладка My Progress)
        addReadPagesButton.setOnAction(event -> {

            //проверяем что все поля заполнены и введенная сумма страниц не превышает непрочитанные в книге страницы
            if (chooseBook.getValue() != null &&
                    chooseDate.getValue() != null &&
                    !addReadPagesQtty.getText().equals("") &&
                    isDigit(addReadPagesQtty.getText().trim()) &&
                    Integer.parseInt(addReadPagesQtty.getText().trim()) > 0 &&
                    Integer.parseInt(addReadPagesQtty.getText().trim()) <=
                            ((Book) chooseBook.getValue()).getPages() - ((Book) chooseBook.getValue()).getPagesRead()) {

                Book chosenBook = (Book) chooseBook.getValue();
                int readPages = Integer.parseInt(addReadPagesQtty.getText());
                String chosenMonth = chooseDate.getValue().getMonthValue() + "";

                if (chooseDate.getValue().getMonthValue() < 10) {
                    chosenMonth = "0" + chosenMonth;
                }
                String chosenDay = chooseDate.getValue().getDayOfMonth() + "";
                if (chooseDate.getValue().getDayOfMonth() < 10) {
                    chosenDay = "0" + chosenDay;
                }

                String chosenDate = chosenMonth + "." + chosenDay;

                for (XYChart.Data item : actualSeries.getData()) {
                    if (item.getXValue().equals(chosenDate)) {
                        item.setYValue((int) item.getYValue() + readPages);
                    }
                }

                //добавляем прочитанные страницы в указанную книгу
                chosenBook.addPagesRead(Integer.parseInt(addReadPagesQtty.getText()));

                //изменяем круговую диаграму
                for (PieChart.Data piePiece : subjectChart.getData()) {
                    if (piePiece.getName().equals(chosenBook.getSubject())) {
                        piePiece.setPieValue(piePiece.getPieValue() + readPages);
                        break;
                    }
                }

                //изменяем общее кол-во прочитанных страниц в профайле
                profile.setReadPages(profile.getReadPages() + readPages);

                //добавляем данные в лог прочитанных страниц профайла
                profile.progressLog.add(new XYChart.Data(chosenDate, readPages));

                //обновляем информацию на вкладке Details
                pagesReadTextShow.setText(profile.getReadPages() + "");
                actualTextShow.setText(profile.getTwoWeeksAverage() + "");
                percentageTextShow.setText((profile.getTwoWeeksAverage() * 100) / profile.getPlan() + "%");

                addReadPagesQtty.clear();
            }
        });

        //кнопка выбора книги (вкладка Details)
        detailsButton.setOnAction(event -> {

            if (chooseBookForDetails.getValue() != null) {
                Book book = (Book) chooseBookForDetails.getValue();

                detailsTextTitleShow.setText(book.getTitle());
                detailsTextAuthorShow.setText(book.getAuthor());
                detailsTextSubjectShow.setText(book.getSubject());
                detailsTextPagesShow.setText(book.getPages() + "");
                detailsTextReadShow.setText(book.getPagesRead() + "");
                detailsTextPercentageShow.setText((book.getPagesRead() * 100) / book.getPages() + "%");

                ObservableList<PieChart.Data> detailsChartData = FXCollections.observableArrayList();

                    detailsChartData.addAll(
                            new PieChart.Data("Read", book.getPagesRead()),
                            new PieChart.Data("Not read", book.getPages() - book.getPagesRead())
                    );


                detailsChart.setData(detailsChartData);
            }
        });

        //кнопка добавления фото (вкладка Details)
        addPhotoButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PNG", "*.png"));

            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                Image newImage = new Image("file:" + file.getAbsolutePath());
                avatar.setImage(newImage);
                profile.setPhoto(newImage);

                File copyFile = new File(dirPhoto + File.separator + profile.getFirstName() + profile.getLastName() + "Photo" + ".png");

                try (FileInputStream fis = new FileInputStream(file);
                     FileOutputStream fos = new FileOutputStream(copyFile);
                     BufferedOutputStream bos = new BufferedOutputStream(fos)) {
                    copyFile.createNewFile();
                    int i;
                    while ((i = fis.read()) != -1) {
                        bos.write(i);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                profile.setPhotoFileName(copyFile.getName());
            }
        });

        //кнопка изменения плана
        changePlanButton.setOnAction(event -> {
            Stage planChangeStage = new Stage();
            planChangeStage.setMinHeight(100);
            planChangeStage.setMaxHeight(100);
            planChangeStage.setMinWidth(300);
            planChangeStage.setMaxWidth(300);
            planChangeStage.setResizable(false);
            planChangeStage.initOwner(primaryStage);
            planChangeStage.initModality(Modality.WINDOW_MODAL);
            planChangeStage.centerOnScreen();
            planChangeStage.setTitle("Enter new plan value");

            TextField enterNewPlanValue = new TextField();
            enterNewPlanValue.setPromptText("Enter new plan here");

            Button changePlanConfirmationButton = new Button("Change");

            HBox changePlanGroup = new HBox(enterNewPlanValue, changePlanConfirmationButton);
            changePlanGroup.setMinWidth(300);
            changePlanGroup.setAlignment(Pos.CENTER);
            changePlanGroup.setSpacing(10);

            planChangeStage.setScene(new Scene(changePlanGroup));

            planChangeStage.show();

            changePlanConfirmationButton.setOnAction(event1 -> {
                profile.setPlan(Integer.parseInt(enterNewPlanValue.getText()));
                planTextShow.setText(profile.getPlan() + "");
                percentageTextShow.setText((profile.getTwoWeeksAverage() * 100) / profile.getPlan() + "%");
                planSeries.setData(profile.generateLastTwoWeeksChartPlanData());
                planChangeStage.close();
            });
        });

        //кнопка сохранения
        saveButton.setOnAction(event -> {
            try {
                save();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Stage saveConfirmationStage = new Stage();
            saveConfirmationStage.initOwner(primaryStage);
            saveConfirmationStage.initModality(Modality.WINDOW_MODAL);
            saveConfirmationStage.centerOnScreen();
            saveConfirmationStage.setMinHeight(100);
            saveConfirmationStage.setMaxHeight(100);
            saveConfirmationStage.setMinWidth(200);
            saveConfirmationStage.setMaxWidth(200);
            saveConfirmationStage.setTitle("Saved!");

            Button saveOKButton = new Button("Ok");
            saveOKButton.setMinWidth(100);

            VBox saveConfirmationGroup = new VBox(saveOKButton);
            saveConfirmationGroup.setSpacing(5);
            saveConfirmationGroup.setAlignment(Pos.CENTER);

            saveConfirmationStage.setScene(new Scene(saveConfirmationGroup));

            saveOKButton.setOnAction(event1 -> saveConfirmationStage.close());

            saveConfirmationStage.show();
        });

        //кнопка загрузки
        loadButton.setOnAction(event -> {


            Profile loadedProfile = null;

            try {
                loadedProfile = load(primaryStage);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (loadedProfile != null) {
                profile = loadedProfile;


                table.setItems(profile.books);
                actualSeries.setData(profile.generateLastTwoWeeksChartActualData());
                planSeries.setData(profile.generateLastTwoWeeksChartPlanData());
                pieChartDate.clear();
                pieChartDate.addAll(profile.generateSubjectReadData());
                avatar.setImage(profile.getPhoto());
                chooseBook.setItems(profile.books);
                chooseBookForDetails.setItems(profile.books);
                nameText.setText(profile.getFirstName() + " " + profile.getLastName());
                bookQttyTextShow.setText(profile.books.size() + "");
                pagesReadTextShow.setText(profile.getReadPages() + "");
                planTextShow.setText(profile.getPlan() + "");
                actualTextShow.setText(profile.getTwoWeeksAverage() + "");
                percentageTextShow.setText((profile.getTwoWeeksAverage() * 100) / profile.getPlan() + "%");
            }
        });

        //кнопка выхода
        exitButton.setOnAction(event -> {
            try {
                save();
            } catch (IOException e) {
                e.printStackTrace();
            }
            primaryStage.close();
        });


        root.getTabs().addAll(libraryTab, progressTab, detailsTab);

        primaryStage.show();

        //-------------------------
        //-------------------------
        //стартовое окно
        //-------------------------
        //-------------------------
        Stage startStage = new Stage();
        startStage.initModality(Modality.WINDOW_MODAL);
        startStage.initOwner(primaryStage);
        startStage.setMinWidth(400);
        startStage.setMaxWidth(400);
        startStage.setMinHeight(200);
        startStage.setMaxHeight(200);
        startStage.setTitle("Welcome to Library!");
        startStage.centerOnScreen();
        startStage.initStyle(StageStyle.UNDECORATED);

        Text alarm = new Text("Enter first and last names!");
        alarm.setFill(Color.RED);
        alarm.setVisible(false);

        TextField firstNameProfile = new TextField();
        firstNameProfile.setMaxWidth(150);
        firstNameProfile.setPromptText("Enter first name");

        TextField lastNameProfile = new TextField();
        lastNameProfile.setMaxWidth(150);
        lastNameProfile.setPromptText("Enter last name");

        Button createProfileButton = new Button("Create Profile");
        createProfileButton.setMaxWidth(150);

        Separator startSeparator = new Separator();
        startSeparator.setOrientation(Orientation.VERTICAL);

        Button loadProfileStartButton = new Button("Load Profile");
        loadProfileStartButton.setMinWidth(150);

        Button closeStartButton = new Button("Close");
        closeStartButton.setMinWidth(150);

        VBox startLeftGroup = new VBox(alarm, firstNameProfile, lastNameProfile, createProfileButton);
        startLeftGroup.setMinWidth(193);
        startLeftGroup.setMinHeight(145);
        startLeftGroup.setSpacing(5);
        startLeftGroup.setAlignment(Pos.CENTER);

        VBox startRightGroup = new VBox(loadProfileStartButton, closeStartButton);
        startRightGroup.setSpacing(5);
        startRightGroup.setMinWidth(192);
        startRightGroup.setMinHeight(145);
        startRightGroup.setAlignment(Pos.TOP_CENTER);           //чтобы выровнять кнопки относительно
        startRightGroup.setPadding(new Insets(41, 0, 0, 0));    //полей в левой части

        HBox startGroup = new HBox(startLeftGroup, startSeparator, startRightGroup);

        //кнопка создания профиля - стартовое окно
        createProfileButton.setOnAction(event -> {
            if (!firstNameProfile.getText().equals("") && !lastNameProfile.getText().equals("")) {
                profile.setFirstName(firstNameProfile.getText());
                profile.setLastName(lastNameProfile.getText());
                nameText.setText(firstNameProfile.getText() + " " + lastNameProfile.getText());
                startStage.close();
            } else {
                alarm.setVisible(true);
            }
        });

        //кнопка выхода - стартовое окно
        closeStartButton.setOnAction(event -> {
            startStage.close();
            primaryStage.close();
        });

        //кнопка загрузки - стартовое окно
        loadProfileStartButton.setOnAction(event -> {

            Profile loadedProfile = null;

            try {
                loadedProfile = load(primaryStage);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (loadedProfile != null) {
                profile = loadedProfile;


                table.setItems(profile.books);
                actualSeries.setData(profile.generateLastTwoWeeksChartActualData());
                planSeries.setData(profile.generateLastTwoWeeksChartPlanData());
                pieChartDate.addAll(profile.generateSubjectReadData());
                avatar.setImage(profile.getPhoto());
                chooseBook.setItems(profile.books);
                chooseBookForDetails.setItems(profile.books);
                nameText.setText(profile.getFirstName() + " " + profile.getLastName());
                bookQttyTextShow.setText(profile.books.size() + "");
                pagesReadTextShow.setText(profile.getReadPages() + "");
                planTextShow.setText(profile.getPlan() + "");
                actualTextShow.setText(profile.getTwoWeeksAverage() + "");
                percentageTextShow.setText((profile.getTwoWeeksAverage() * 100) / profile.getPlan() + "%");

                startStage.close();
            }
        });

        startStage.setScene(new Scene(startGroup));

        startStage.show();
    }

    public void save() throws IOException {
        File dirProfiles = new File(SAVE_DIR_NAME);
        dirProfiles.mkdir();
        File profileFile = new File(dirProfiles + File.separator + profile.getFirstName() + profile.getLastName() + ".libr");

        profileFile.createNewFile();

        ArrayList<BookUglyTwin> booksForUglyTwin = new ArrayList<BookUglyTwin>();
        ArrayList<LogUglyTwin> logsForUglyTwin = new ArrayList<LogUglyTwin>();
        String firstNameForUglyTwin = profile.getFirstName();
        String lastNameForUglyTwin = profile.getLastName();
        int planForUglyTwin = profile.getPlan();
        int readPagesByUglyTwin = profile.getReadPages();
        String photoForUglyTwin = profile.getPhotoFileName();

        for (Book book : profile.books) {
            booksForUglyTwin.add(new BookUglyTwin(book.getTitle(), book.getAuthor(), book.getSubject(), book.getPages(), book.getPagesRead()));
        }

        for (XYChart.Data<String, Number> log : profile.progressLog) {
            logsForUglyTwin.add(new LogUglyTwin(log.getXValue(), log.getYValue().intValue()));
        }

        ProfileUglyTwin uglyTwin = new ProfileUglyTwin(firstNameForUglyTwin, lastNameForUglyTwin, planForUglyTwin, readPagesByUglyTwin, photoForUglyTwin, booksForUglyTwin, logsForUglyTwin);

        try (FileOutputStream fos = new FileOutputStream(profileFile);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(uglyTwin);
        }
    }

    public Profile load(Stage stage) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("LIBR", "*.libr"));
        File dir = new File(SAVE_DIR_NAME);
        dir.mkdir();
        fileChooser.setInitialDirectory(dir);

        File profileFile = fileChooser.showOpenDialog(stage);

        if (profileFile != null) {

            ProfileUglyTwin uglyTwin = null;
            ObservableList<Book> books = FXCollections.observableArrayList();
            ObservableList<XYChart.Data<String, Number>> progressLog = FXCollections.observableArrayList();

            try (FileInputStream fis = new FileInputStream(profileFile);
                 ObjectInputStream ois = new ObjectInputStream(fis)) {
                uglyTwin = (ProfileUglyTwin) ois.readObject();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            SimpleStringProperty firstName = new SimpleStringProperty(uglyTwin.firstName);
            SimpleStringProperty lastName = new SimpleStringProperty(uglyTwin.lastName);

            for (BookUglyTwin book : uglyTwin.books) {
                books.add(new Book(new SimpleStringProperty(book.title), new SimpleStringProperty(book.author), new SimpleStringProperty(book.subject), new SimpleIntegerProperty(book.pages), new SimpleIntegerProperty(book.readPages)));
            }

            for (LogUglyTwin log : uglyTwin.progressLog) {
                progressLog.add(new XYChart.Data<>(log.date, log.pagesRead));
            }

            Profile profile = new Profile(firstName, lastName);
            profile.setPlan(uglyTwin.plan);
            profile.setReadPages(uglyTwin.readPages);
            profile.setPhotoFileName(uglyTwin.photo);
            profile.books = books;
            profile.progressLog = progressLog;
            profile.setPhoto(new Image("file:" + PHOTO_DIR_NAME + File.separator + profile.getPhotoFileName()));

            return profile;

        }
        return null;
    }

    private boolean isDigit(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
