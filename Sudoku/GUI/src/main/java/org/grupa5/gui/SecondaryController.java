package org.grupa5.gui;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javafx.beans.property.*;
import javafx.beans.property.adapter.JavaBeanIntegerPropertyBuilder;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import javafx.util.converter.NumberStringConverter;
import org.grupa5.dao.exception.ReadException;
import org.grupa5.dao.SudokuBoardDaoFactory;
import org.grupa5.dao.exception.WriteException;
import org.grupa5.sudoku.SudokuBoard;
import org.grupa5.sudoku.SudokuField;
import org.grupa5.sudoku.exceptions.GetException;
import org.grupa5.sudoku.exceptions.SetException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SecondaryController implements Initializable {

    @FXML
    private AnchorPane root;

    private final Logger logger = LoggerFactory.getLogger(SecondaryController.class);

    private SudokuBoard sudokuBoard = new SudokuBoard();
    private boolean flag = true;
    private ResourceBundle resourceBundle;
    private final List<IntegerProperty> integerPropertyArrayListForSudokuFieldBinding = new ArrayList<>();

    @FXML
    private ComboBox<Level> boxLevel = new ComboBox<>();

    // TODO: 13.05.2020 nadpisac metode, zapisac nazwy jako klucze  internalizowac tego enuma jakos
    public enum Level {
        Easy(42),
        Medium(54),
        Hard(60),
        Prosty(42),
        Sredni(54),
        Trudny(60);

        private final int number;

        public int getNumber() {
            return number;
        }

        Level(int number) {
            this.number = number;
        }
    }

    // TODO: 06.05.2020 mozna zrobic wlasna
    StringConverter<Number> converter = new NumberStringConverter();

    @FXML
    private GridPane grid1;

    @FXML
    private Button secondaryButton;

    @FXML
    private Button language;

    @FXML
    private Button saveButton;

    @FXML
    private Label level;

    @FXML
    private Button loadButton;

    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("primary");
    }

    private boolean checkNumeric(String value) {
        String number = value.replaceAll("\\s+", "");
        for (int j = 0; j < number.length(); j++) {
            if (!(((int) number.charAt(j) > 47 && (int) number.charAt(j) <= 57))) {
                return false;
            }
        }
        return true;
    }

    private void switchStartAndEndButtons() {
        if (!flag) {
            try {
                this.switchToPrimary();
            } catch (IOException e) {
                e.printStackTrace();
            }
            flag = true;
            return;
        }
        flag = false;
        secondaryButton.setText(resourceBundle.getString("end"));
    }


    // TODO: zrobić żeby grid się centrował po zmianie rozmiaru okna
    private void fillGrid() throws NoSuchMethodException {
        // TODO: 16.05.2020 Testuje tym czy sie internacjonalizuja wyjatki xd
        try {
            sudokuBoard.set(0, 0, 12);
        } catch (SetException e) {
            System.out.println(e.getLocalizedMessage());
        }
        int numRows = grid1.getRowCount();
        int numCols = grid1.getColumnCount();
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                TextField textField = new TextField();
                textField.setAlignment(Pos.CENTER);
                textField.setMaxWidth(45);
                textField.setMaxHeight(45);

                if (i != 0 && j != 0) {

                    textField.setTextFormatter(new TextFormatter<>(c -> {
                        if (c.isContentChange()) {
                            if (c.getText().matches("[0-9] | ^$ ")) {
                                return c;
                            }
                        }
                        return c;
                    }));

                    SudokuField sudokuField = this.sudokuBoard.getField(j - 1, i - 1);
                    IntegerProperty integerProperty = new JavaBeanIntegerPropertyBuilder().bean(sudokuField).name("value").build();

                    this.integerPropertyArrayListForSudokuFieldBinding.add(integerProperty);
                    textField.textProperty().bindBidirectional(integerProperty, converter);

                    textField.addEventFilter(KeyEvent.KEY_TYPED, new EventHandler<KeyEvent>() {
                        @Override
                        public void handle(KeyEvent event) {
                            textField.clear();
                            String character = event.getCharacter();
                            if (!checkNumeric(character)) {
                                textField.setText("0");
                                event.consume();
                            }
                        }
                    });
                    textField.addEventFilter(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
                        @Override
                        public void handle(KeyEvent event) {
                            if (!sudokuBoard.isWholeBoardValid()) {
                                textField.setText("0");
                            }
                        }
                    });

                    int intToAdd = 0;
                    try {
                        intToAdd = sudokuBoard.get(j - 1, i - 1);
                    } catch (GetException e) {
                        System.out.println(e.getLocalizedMessage());
                    }
                    if (intToAdd != 0) {
                        textField.setDisable(true);
                    }
                } else if (i == 0 && j == 0) {
                    textField.setDisable(true);
                    textField.setText("X");
                } else if (i == 0) {
                    textField.setDisable(true);
                    textField.setText((Character.toString((char) (64 + j))));
                } else {
                    textField.setDisable(true);
                    textField.setText(("0" + i));
                }
                grid1.add(textField, i, j);
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Grid Filled");
        }
    }

    public void startGame() throws NoSuchMethodException {
        if (logger.isInfoEnabled()) {
            logger.info("Sudoku Game Started");
        }
        switchStartAndEndButtons();
        int numberOfFields = boxLevel.getSelectionModel().getSelectedItem().getNumber();
        sudokuBoard.solveGame();
        sudokuBoard.removeFields(numberOfFields);
        this.fillGrid();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (logger.isDebugEnabled()) {
            logger.debug("SecondaryController init");
        }
        this.resourceBundle = ResourceBundle.getBundle("Lang", Locale.getDefault());
        if (Locale.getDefault().equals(new Locale("en", "en"))) {
            boxLevel.setItems(FXCollections.observableArrayList(Level.values()[0], Level.values()[1], Level.values()[2]));
            boxLevel.setValue(Level.values()[0]);
        } else {
            boxLevel.setItems(FXCollections.observableArrayList(Level.values()[3], Level.values()[4], Level.values()[5]));
            boxLevel.setValue(Level.values()[3]);
        }
    }

    public void saveSudokuToFile() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);

        File file = fileChooser.showSaveDialog(new Stage());

        if (file != null) {
            try {
//                SudokuBoardDaoFactory.getFileDao(file.getAbsolutePath()).write(this.sudokuBoard);
                // TODO: 18.05.2020 zrobic parametr
                SudokuBoardDaoFactory.getJdbcDao("'Nazwa3'").write(this.sudokuBoard);
            } catch (WriteException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Save Error");
                alert.setHeaderText("Error Saving Game");
                alert.setContentText("There was an error saving your game.\n" +
                        "Please try to save again!");

                alert.showAndWait();

                if (logger.isErrorEnabled()) {
                    logger.error("Sudoku Game Saving Failed");
                }
            }
        }
    }

    public void readSudokuFromFile() {
        // TODO: 05.05.2020 popraw wczytywanie rozpoczetej gry
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(new Stage());
        if (file != null) {
            try {
//                this.sudokuBoard = SudokuBoardDaoFactory.getFileDao(file.getAbsolutePath()).read();
                // TODO: 18.05.2020 poprawic parametr
                this.sudokuBoard = SudokuBoardDaoFactory.getJdbcDao("'Nazwa3'").read();
                System.out.println(this.sudokuBoard);
                switchStartAndEndButtons();
                this.fillGrid();
            } catch (ReadException | NoSuchMethodException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Load Error");
                alert.setHeaderText("Error Loading Game");
                alert.setContentText("There was an error loading your game.\n" +
                        "Please try to load again!");
                alert.showAndWait();

                if (logger.isErrorEnabled()) {
                    logger.error("Sudoku Game Loading Failed");
                }
            }
        }
    }

    public void changeLanguage() {
        if (Locale.getDefault().equals(new Locale("en", "en"))) {
            Locale.setDefault(new Locale("pl", "pl"));
            resourceBundle = ResourceBundle.getBundle("Lang", Locale.getDefault());
            boxLevel.setItems(FXCollections.observableArrayList(Level.values()[3], Level.values()[4], Level.values()[5]));
            boxLevel.setValue(Level.values()[3]);
        } else {
            Locale.setDefault(new Locale("en", "en"));
            resourceBundle = ResourceBundle.getBundle("Lang", Locale.getDefault());
            boxLevel.setItems(FXCollections.observableArrayList(Level.values()[0], Level.values()[1], Level.values()[2]));
            boxLevel.setValue(Level.values()[0]);
        }
        try {
            updateLanguage();
        } catch (IOException e) {
            if (logger.isErrorEnabled()) {
                logger.error("changeLanguage threw ", e);
            }
        }
    }

    // TODO: 16.05.2020 Aktualnie po przeresetowaniu sceny tzn po zmianie jezyka
    //  resetuje nam sie plansza, postep gry, jak to tam chcesz nazwac >.> do fixu
    private void updateLanguage() throws IOException {
        App reload = new App();
        reload.reload("secondary");
    }

}


