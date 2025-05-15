package com.example.demo3;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;


public class LesothoTourView {
    // declarations
    private BorderPane root;
    private Timeline scrollTimeline;
    private HBox scrollingImagesBox;
    private VBox contentBox;
    private StackPane layeredPane;
    private ImageView backgroundImageView;
    private List<Place> places;
    private int currentImageIndex = 0;
    private MediaPlayer backgroundVideoPlayer;
    private StackPane videoBackgroundContainer;

    // Constructor to initialize the view
    public LesothoTourView() {
        root = new BorderPane();
        setupBackgroundLayered();
        createTitleAndNavigation();
        setupVideoBackground();
        createContentLayout();
        startAutoScroll();
    }

    // Getter method for the view
    public BorderPane getView() {
        return root;
    }

    // Method to set up video background
    private void setupVideoBackground() {
        try {
            // Load the background video
            URL videoUrl = getClass().getResource("/videos/GN.mp4");
            if (videoUrl != null) {
                Media backgroundMedia = new Media(videoUrl.toString());
                backgroundVideoPlayer = new MediaPlayer(backgroundMedia);
                MediaView backgroundMediaView = new MediaView(backgroundVideoPlayer);

                // Set video properties - full width
                backgroundMediaView.setPreserveRatio(false);
                backgroundMediaView.setFitWidth(1200);
                backgroundMediaView.setFitHeight(300);

                // Create a container for the video background
                videoBackgroundContainer = new StackPane(backgroundMediaView);
                videoBackgroundContainer.setAlignment(Pos.CENTER);
                videoBackgroundContainer.setStyle("-fx-background-color: transparent;");
                videoBackgroundContainer.setPrefHeight(300);

                // Start playing the video in loop
                backgroundVideoPlayer.setCycleCount(MediaPlayer.INDEFINITE);
                backgroundVideoPlayer.play();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Could not load background video");
        }
    }

    // Method to set up layered background
    private void setupBackgroundLayered() {
        layeredPane = new StackPane();

        backgroundImageView = new ImageView();
        InputStream bgStream = getClass().getResourceAsStream("/images/kome.jpg");
        if (bgStream != null) {
            backgroundImageView.setImage(new Image(bgStream));
        } else {
            backgroundImageView.setImage(new Image("https://via.placeholder.com/1200x800"));
        }
        backgroundImageView.setFitWidth(1200);
        backgroundImageView.setFitHeight(800);
        backgroundImageView.setPreserveRatio(false);
        backgroundImageView.setOpacity(0.25);

        layeredPane.getChildren().add(backgroundImageView);

        contentBox = new VBox(10);
        contentBox.setAlignment(Pos.TOP_CENTER);
        contentBox.setPadding(new Insets(10));

        ScrollPane scrollPane = new ScrollPane(contentBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        layeredPane.getChildren().add(scrollPane);

        root.setCenter(layeredPane);
    }

    // Method to create title and navigation buttons
    private void createTitleAndNavigation() {
        Label titleLabel = new Label("THE LESOTHO TOUR");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        titleLabel.setTextFill(Color.WHITE);
        titleLabel.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0, 0, 0);");

        Button homeButton = createNavButton("Home", "#FF5722");
        Button quizButton = createNavButton("Take Quiz", "#4CAF50");
        Button mapButton = createNavButton("Explore Map", "#2196F3");
        Button aboutButton = createNavButton("About Lesotho", "#9C27B0");

        homeButton.setOnAction(e -> {
            stopBackgroundVideo();
            showHomeContent();
        });
        quizButton.setOnAction(e -> {
            stopBackgroundVideo();
            showQuizContent();
        });
        mapButton.setOnAction(e -> {
            stopBackgroundVideo();
            showMapContent();
        });
        aboutButton.setOnAction(e -> {
            stopBackgroundVideo();
            showAboutLesothoContent();
        });

        HBox buttonBox = new HBox(15, homeButton, quizButton, mapButton, aboutButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10));

        VBox navContainer = new VBox(titleLabel, buttonBox);
        navContainer.setAlignment(Pos.CENTER);
        navContainer.setPadding(new Insets(20));
        navContainer.setBackground(new Background(new BackgroundFill(Color.rgb(139, 69, 19, 0.9), CornerRadii.EMPTY, Insets.EMPTY)));

        root.setTop(navContainer);
    }

    // Method to show About Lesotho content
    private void showAboutLesothoContent() {
        contentBox.getChildren().clear();
        backgroundImageView.setVisible(false);
        if (videoBackgroundContainer != null) {
            videoBackgroundContainer.setVisible(false);
        }
        AboutLesothoDashboard aboutDashboard = new AboutLesothoDashboard(this::showHomeContent);
        contentBox.getChildren().add(aboutDashboard.getView());
    }

    // Method to show home content (empty implementation)
    private void showHomeContent(Void unused) {
    }

    // Method to stop background video
    private void stopBackgroundVideo() {
        if (backgroundVideoPlayer != null) {
            backgroundVideoPlayer.stop();
        }
        if (videoBackgroundContainer != null) {
            videoBackgroundContainer.setVisible(false);
        }
    }

    // Method to start background video
    private void startBackgroundVideo() {
        if (backgroundVideoPlayer != null) {
            backgroundVideoPlayer.play();
            if (videoBackgroundContainer != null) {
                videoBackgroundContainer.setVisible(true);
            }
        }
    }

    // Method to show home content
    private void showHomeContent() {
        contentBox.getChildren().clear();
        backgroundImageView.setVisible(true);
        contentBox.setStyle("");
        createContentLayout();
        startBackgroundVideo();
    }

    // Method to show quiz content
    private void showQuizContent() {
        contentBox.getChildren().clear();
        backgroundImageView.setVisible(false);
        contentBox.setStyle("-fx-background-color: #333333; -fx-text-fill: white;");
        QuizDashboard quizDashboard = new QuizDashboard();
        StackPane quizView = quizDashboard.getView();
        quizView.setStyle("-fx-text-fill: white;");
        contentBox.getChildren().add(quizView);
    }

    // Method to show map content
    private void showMapContent() {
        contentBox.getChildren().clear();
        backgroundImageView.setVisible(false);
        contentBox.setStyle("");
        MapDashboard mapDashboard = new MapDashboard();
        contentBox.getChildren().add(mapDashboard.getView());
    }

    // Helper method to create navigation buttons
    private Button createNavButton(String text, String color) {
        Button button = new Button(text);
        button.setStyle("-fx-font-size: 14px; -fx-background-color: " + color + "; -fx-text-fill: white; -fx-padding: 8 16;");
        return button;
    }

    // Method to create content layout
    private void createContentLayout() {
        contentBox.getChildren().clear();

        if (videoBackgroundContainer != null) {
            contentBox.getChildren().add(videoBackgroundContainer);
        }

        createScrollingImages();
    }

    // Method to create scrolling images gallery
    private void createScrollingImages() {
        VBox imageGallery = new VBox(10);
        imageGallery.setAlignment(Pos.TOP_CENTER);

        Label viewImagesLabel = new Label("VIEW PICTURES");
        viewImagesLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        viewImagesLabel.setTextFill(Color.WHITE);
        viewImagesLabel.setStyle("-fx-effect: dropshadow(gaussian, black, 5, 0.5, 0, 0);");

        scrollingImagesBox = new HBox(30);
        scrollingImagesBox.setAlignment(Pos.CENTER_LEFT);
        scrollingImagesBox.setPadding(new Insets(20));

        places = loadPlacesFromImageFolder();
        for (int i = 0; i < places.size(); i++) {
            final int index = i;
            ImageView imageView = new ImageView(places.get(i).getImage());
            imageView.setFitHeight(180);
            imageView.setPreserveRatio(true);
            imageView.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0, 0, 0);");

            imageView.setOnMouseClicked(event -> {
                stopBackgroundVideo();
                currentImageIndex = index;
                showImageDashboard();
            });

            scrollingImagesBox.getChildren().add(imageView);
        }

        ScrollPane imageScrollPane = new ScrollPane(scrollingImagesBox);
        imageScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        imageScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        imageScrollPane.setPrefHeight(250);
        imageScrollPane.setMaxWidth(1000);
        imageScrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        imageGallery.getChildren().addAll(viewImagesLabel, imageScrollPane);
        contentBox.getChildren().add(imageGallery);
    }

    // Method to show image dashboard
    private void showImageDashboard() {
        contentBox.getChildren().clear();
        backgroundImageView.setVisible(false);
        if (videoBackgroundContainer != null) {
            videoBackgroundContainer.setVisible(false);
        }

        // Main container with padding
        VBox imageDashboard = new VBox(20);
        imageDashboard.setAlignment(Pos.CENTER);
        imageDashboard.setPadding(new Insets(40));
        imageDashboard.setStyle("-fx-background-color: #222222;");

        // Image view
        ImageView fullImageView = new ImageView(places.get(currentImageIndex).getImage());
        fullImageView.setPreserveRatio(true);
        fullImageView.setFitWidth(800);
        fullImageView.setFitHeight(500);

        // Navigation buttons
        HBox navButtons = new HBox(20);
        navButtons.setAlignment(Pos.CENTER);

        Button prevButton = new Button("Previous");
        prevButton.setStyle("-fx-font-size: 14px; -fx-padding: 8 16;");
        prevButton.setOnAction(e -> {
            currentImageIndex = (currentImageIndex - 1 + places.size()) % places.size();
            fullImageView.setImage(places.get(currentImageIndex).getImage());
        });

        Button nextButton = new Button("Next");
        nextButton.setStyle("-fx-font-size: 14px; -fx-padding: 8 16;");
        nextButton.setOnAction(e -> {
            currentImageIndex = (currentImageIndex + 1) % places.size();
            fullImageView.setImage(places.get(currentImageIndex).getImage());
        });

        Button backButton = new Button("Back to Home");
        backButton.setStyle("-fx-font-size: 14px; -fx-padding: 8 16;");
        backButton.setOnAction(e -> showHomeContent());

        navButtons.getChildren().addAll(prevButton, backButton, nextButton);

        imageDashboard.getChildren().addAll(fullImageView, navButtons);
        contentBox.getChildren().add(imageDashboard);
    }

    // Method to load places from image folder
    private List<Place> loadPlacesFromImageFolder() {
        List<Place> places = new ArrayList<>();
        try {
            URL dirURL = getClass().getResource("/images");
            if (dirURL != null) {
                Path path = Paths.get(dirURL.toURI());
                DirectoryStream<Path> stream = Files.newDirectoryStream(path, "*.{png,jpg,jpeg}");
                for (Path imagePath : stream) {
                    Image image = new Image(imagePath.toUri().toString());
                    places.add(new Place(image));
                }
            } else {
                System.err.println("Image folder not found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return places;
    }

    // Method to start auto-scrolling of images
    private void startAutoScroll() {
        scrollTimeline = new Timeline(new KeyFrame(Duration.seconds(0.1), e -> {
            scrollingImagesBox.setTranslateX(scrollingImagesBox.getTranslateX() - 1);
            if (Math.abs(scrollingImagesBox.getTranslateX()) > scrollingImagesBox.getWidth()) {
                scrollingImagesBox.setTranslateX(0);
            }
        }));
        scrollTimeline.setCycleCount(Timeline.INDEFINITE);
        scrollTimeline.play();
    }
}