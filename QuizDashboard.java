package com.example.demo3;

import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.control.Alert.AlertType;

import java.io.InputStream;

public class QuizDashboard {
    private int currentQuestionIndex = 0;
    private int score = 0;

    private StackPane rootPane;
    private VBox vbox;
    private Label qLabel;
    private ToggleGroup group;
    private VBox questionBox;
    private Button submitBtn;
    private Label feedbackLabel;
    private Button okButton;

    private final String[][] questions = {
            {"What is the capital of Lesotho?", "Maseru", "Maputsoe", "Roma", "Thaba-Tseka", "Maseru"},
            {"Which waterfall is located in Semonkong?", "Tsehlanyane", "Maletsunyane", "Mohale", "Qacha", "Maletsunyane"},
            {"What is the historical mountain fortress?", "Thaba-Bosiu", "Maluti", "Berea", "Roma Hills", "Thaba-Bosiu"},
            {"What are the traditional homes carved in rock?", "Kome Caves", "Ha Baroana", "Thaba-Tseka", "Butha-Buthe", "Kome Caves"},
            {"How many districts are in Lesotho?", "8", "10", "12", "6", "10"}
    };

    public QuizDashboard() {
        initializeUI();
    }

    private void initializeUI() {
        rootPane = new StackPane();

        // Set up background image
        ImageView backgroundImageView = new ImageView();
        InputStream bgStream = getClass().getResourceAsStream("/images/kome.jpg");
        if (bgStream != null) {
            backgroundImageView.setImage(new Image(bgStream));
        } else {
            backgroundImageView.setImage(new Image("https://via.placeholder.com/1200x800"));
        }
        backgroundImageView.setFitWidth(1200);
        backgroundImageView.setFitHeight(800);
        backgroundImageView.setPreserveRatio(false);
        backgroundImageView.setOpacity(0.2);
        rootPane.getChildren().add(backgroundImageView);

        // Create UI layout
        vbox = new VBox(10);
        vbox.setStyle("-fx-padding: 20px; -fx-background-color: rgba(220, 220, 220, 0.8); -fx-text-fill: black;");
        vbox.setMaxWidth(600);

        qLabel = new Label();
        qLabel.setStyle("-fx-text-fill: black; -fx-font-size: 18px; -fx-font-weight: bold;");

        group = new ToggleGroup();
        questionBox = new VBox(5);

        submitBtn = new Button("Submit Answer");

        feedbackLabel = new Label();
        feedbackLabel.setStyle("-fx-text-fill: black; -fx-font-size: 14px;");

        okButton = new Button("OK");
        okButton.setVisible(false);
        okButton.setOnAction(this::handleOkButton);

        vbox.getChildren().addAll(qLabel, questionBox, submitBtn, feedbackLabel, okButton);
        rootPane.getChildren().add(vbox);

        loadQuestion(currentQuestionIndex);

        submitBtn.setDisable(true);
        group.selectedToggleProperty().addListener((obs, oldToggle, newToggle) ->
                submitBtn.setDisable(newToggle == null)
        );

        submitBtn.setOnAction(this::handleSubmit);
    }

    private void loadQuestion(int index) {
        group.getToggles().clear();
        questionBox.getChildren().clear();
        feedbackLabel.setText("");
        okButton.setVisible(false);

        qLabel.setText((index + 1) + ". " + questions[index][0]);

        for (int i = 1; i <= 4; i++) {
            RadioButton rb = new RadioButton(questions[index][i]);
            rb.setToggleGroup(group);
            rb.setStyle("-fx-text-fill: black; -fx-font-size: 14px;");
            questionBox.getChildren().add(rb);
        }
    }

    private void handleSubmit(javafx.event.ActionEvent event) {
        RadioButton selected = (RadioButton) group.getSelectedToggle();
        if (selected != null) {
            String selectedAnswer = selected.getText();
            String correctAnswer = questions[currentQuestionIndex][5];

            if (selectedAnswer.equals(correctAnswer)) {
                score++;
                feedbackLabel.setText("✅ Correct!");
            } else {
                feedbackLabel.setText("❌ Incorrect! Correct answer: " + correctAnswer);
            }

            submitBtn.setDisable(true);
            okButton.setVisible(true);
        }
    }

    private void handleOkButton(javafx.event.ActionEvent event) {
        currentQuestionIndex++;
        if (currentQuestionIndex < questions.length) {
            loadQuestion(currentQuestionIndex);
            submitBtn.setDisable(true);
        } else {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Quiz Completed");
            alert.setHeaderText("You have completed the quiz!");
            alert.setContentText("Your score: " + score + " out of " + questions.length);
            alert.showAndWait();

            qLabel.setText("🎉 Thank you for participating!");
            questionBox.getChildren().clear();
            feedbackLabel.setText("");
            submitBtn.setDisable(true);
            okButton.setVisible(false);
        }
    }

    public StackPane getView() {
        return rootPane;
    }
}
