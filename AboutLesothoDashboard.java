package com.example.demo3;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class AboutLesothoDashboard {
    private VBox view;
    private Consumer<Void> returnToHomeCallback;
    private List<MediaPlayer> musicPlayers = new ArrayList<>();
    private List<MediaPlayer> videoPlayers = new ArrayList<>();
    private List<Image> cultureImages = new ArrayList<>();
    private List<Image> foodImages = new ArrayList<>();
    private int currentMusicIndex = 0;
    private int currentVideoIndex = 0;
    private int currentCultureImageIndex = 0;
    private int currentFoodImageIndex = 0;
    private MediaPlayer currentMusicPlayer;
    private MediaPlayer currentVideoPlayer;
    private MediaPlayer historyPlayer;
    private ImageView backgroundImageView;
    private static final String HISTORY_AUDIO_FILE = "HST.mp3";
    private VBox historyAudioControls;

    public AboutLesothoDashboard(Consumer<Void> returnToHomeCallback) {
        this.returnToHomeCallback = returnToHomeCallback;
        createView();
    }

    public VBox getView() {
        return view;
    }

    private void createView() {
        stopAllMedia();

        backgroundImageView = new ImageView();
        try {
            InputStream bgStream = getClass().getResourceAsStream("/images/sehlabathebe.jpg");
            if (bgStream != null) {
                backgroundImageView.setImage(new Image(bgStream));
            } else {
                backgroundImageView.setImage(new Image("https://images.unsplash.com/photo-1605000797499-95a51c5269ae?ixlib=rb-1.2.1&auto=format&fit=crop&w=1350&q=80"));
            }
        } catch (Exception e) {
            System.err.println("Could not load background image: " + e.getMessage());
            backgroundImageView = null;
        }

        if (backgroundImageView != null) {
            backgroundImageView.setFitWidth(1200);
            backgroundImageView.setFitHeight(800);
            backgroundImageView.setPreserveRatio(false);
            backgroundImageView.setOpacity(0.5);
        }

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        VBox aboutContainer = new VBox(20);
        aboutContainer.setPadding(new Insets(20));
        aboutContainer.setStyle("-fx-background-color: rgba(60, 60, 60, 0.7);");
        aboutContainer.setAlignment(Pos.CENTER);

        Label titleLabel = createSectionLabel("About Lesotho", 28);
        titleLabel.setStyle("-fx-text-fill: #FFD700; -fx-font-weight: bold; -fx-padding: 10; " +
                "-fx-border-color: #FFD700; -fx-border-width: 2; -fx-border-radius: 5;");

        VBox historySection = new VBox(10);
        historySection.setStyle("-fx-background-color: rgba(80, 80, 80, 0.8); -fx-padding: 15; -fx-border-radius: 5;");
        historySection.setAlignment(Pos.CENTER);
        Label historyTitle = createSectionLabel("History", 24);
        historyTitle.setStyle("-fx-text-fill: black;");
        TextArea historyText = new TextArea("Lesotho, officially the Kingdom of Lesotho, is a landlocked country completely surrounded by South Africa.\n\n" +
                "Originally known as Basutoland, it was founded by King Moshoeshoe I in the early 19th century. The nation gained independence from Britain on October 4, 1966, and is now a constitutional monarchy with a population of about 2.2 million people.\n\n" +
                "Key Historical Facts:\n" +
                "• 1822: Moshoeshoe I unites various clans to form Basotho nation\n" +
                "• 1868: Becomes British protectorate to avoid Boer encroachment\n" +
                "• 1966: Gains independence as Kingdom of Lesotho\n" +
                "• 1993: Returns to democratic rule after military government\n\n" +
                "Unique Features of Lesotho:\n" +
                "• Known as 'The Kingdom in the Sky' - entire country above 1000m elevation\n" +
                "• Major water exporter - supplies water to South Africa through the Lesotho Highlands Water Project\n" +
                "• Diamond producer - home to the Letseng diamond mine (highest dollar-per-carat mine in the world)\n" +
                "• Only country with all its land above 1000m elevation\n" +
                "• Rich cultural heritage with unique traditions and customs");
        styleTextArea(historyText);

        historyAudioControls = createHistoryAudioControls();

        historySection.getChildren().addAll(historyTitle, historyText, historyAudioControls);

        Label musicTitle = createSectionLabel("Basotho Music", 24);
        VBox musicSection = createMusicSection();

        Label cultureTitle = createSectionLabel("Culture and Traditional Attire", 24);
        VBox cultureSection = createCultureSection();

        Label foodTitle = createSectionLabel("Traditional Cuisine", 24);
        VBox foodSection = createFoodSection();

        Label videosTitle = createSectionLabel("Places of Attraction Videos", 24);
        VBox videosSection = createVideosSection();

        Label districtsTitle = createSectionLabel("Districts of Lesotho Details", 24);
        GridPane districtsGrid = createDistrictsGrid();

        aboutContainer.getChildren().addAll(
                titleLabel,
                historySection,
                musicTitle, musicSection,
                cultureTitle, cultureSection,
                foodTitle, foodSection,
                videosTitle, videosSection,
                districtsTitle, districtsGrid
        );

        scrollPane.setContent(aboutContainer);

        StackPane layeredPane = new StackPane();
        if (backgroundImageView != null) {
            layeredPane.getChildren().add(backgroundImageView);
        } else {
            layeredPane.setStyle("-fx-background-color: linear-gradient(to bottom, #2e4b7c, #3a5b8c);");
        }
        layeredPane.getChildren().add(scrollPane);

        view = new VBox(layeredPane);
        view.setStyle("-fx-background-color: transparent;");
    }

    private VBox createHistoryAudioControls() {
        VBox controlsBox = new VBox(10);
        controlsBox.setAlignment(Pos.CENTER);
        controlsBox.setPadding(new Insets(10));
        controlsBox.setStyle("-fx-background-color: rgba(88, 88, 88, 0.7); -fx-border-radius: 5;");

        Label audioLabel = new Label("History Narration");
        audioLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px;");

        HBox buttonsBox = new HBox(10);
        buttonsBox.setAlignment(Pos.CENTER);

        Button playButton = new Button("Play");
        playButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-min-width: 80px;");
        playButton.setOnAction(e -> {
            if (historyPlayer == null) {
                initializeHistoryPlayer();
            }
            if (historyPlayer != null) {
                historyPlayer.play();
            }
        });

        Button pauseButton = new Button("Pause");
        pauseButton.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-min-width: 80px;");
        pauseButton.setOnAction(e -> {
            if (historyPlayer != null) {
                historyPlayer.pause();
            }
        });

        Button stopButton = new Button("Stop");
        stopButton.setStyle("-fx-background-color: #F44336; -fx-text-fill: white; -fx-min-width: 80px;");
        stopButton.setOnAction(e -> {
            if (historyPlayer != null) {
                historyPlayer.stop();
            }
        });

        buttonsBox.getChildren().addAll(playButton, pauseButton, stopButton);
        controlsBox.getChildren().addAll(audioLabel, buttonsBox);

        return controlsBox;
    }

    private void initializeHistoryPlayer() {
        try {
            URL audioUrl = getClass().getResource("/voice/" + HISTORY_AUDIO_FILE);
            if (audioUrl != null) {
                Media historyMedia = new Media(audioUrl.toString());
                historyPlayer = new MediaPlayer(historyMedia);
                historyPlayer.setOnEndOfMedia(() -> historyPlayer.stop());
                historyPlayer.setOnError(() -> {
                    System.err.println("History audio error occurred");
                    historyPlayer.stop();
                });
            } else {
                System.err.println("Could not find history audio file: " + HISTORY_AUDIO_FILE);
            }
        } catch (Exception e) {
            System.err.println("Could not load history audio: " + e.getMessage());
        }
    }

    private Label createSectionLabel(String text, int fontSize) {
        Label label = new Label(text);
        label.setFont(Font.font("Arial", FontWeight.BOLD, fontSize));
        label.setTextFill(Color.WHITE);
        label.setAlignment(Pos.CENTER);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setStyle("-fx-padding: 10; -fx-border-color: #777777; -fx-border-width: 0 0 1 0;");
        return label;
    }

    private void styleTextArea(TextArea textArea) {
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setStyle("-fx-font-size: 16px; -fx-text-fill: white; " +
                "-fx-background-color: rgba(95, 95, 95, 0.7); " +
                "-fx-border-color: #777777; -fx-border-width: 2; " +
                "-fx-border-radius: 5; -fx-control-inner-background: #444444;");
        textArea.setPrefHeight(400);
    }

    private VBox createMusicSection() {
        VBox musicContainer = new VBox(10);
        musicContainer.setAlignment(Pos.CENTER);
        musicContainer.setStyle("-fx-padding: 15; -fx-background-color: rgba(88, 88, 88, 0.7); -fx-border-radius: 5;");

        try {
            URL musicDir = getClass().getResource("/music");
            if (musicDir != null) {
                Path musicPath = Paths.get(musicDir.toURI());
                try (var stream = Files.newDirectoryStream(musicPath, "*.mp3")) {
                    stream.forEach(file -> {
                        if (file.getFileName().toString().equals(HISTORY_AUDIO_FILE)) {
                            return;
                        }

                        try {
                            Media sound = new Media(file.toUri().toString());
                            MediaPlayer player = new MediaPlayer(sound);
                            musicPlayers.add(player);

                            Label musicLabel = new Label(file.getFileName().toString().replace(".mp3", ""));
                            musicLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-cursor: hand; " +
                                    "-fx-padding: 8; -fx-background-color: #666666; -fx-background-radius: 5;"); 
                            musicLabel.setOnMouseEntered(e -> musicLabel.setStyle("-fx-text-fill: #FFD700; -fx-font-size: 16px; -fx-cursor: hand; " +
                                    "-fx-padding: 8; -fx-background-color: #777777; -fx-background-radius: 5;")); 
                            musicLabel.setOnMouseExited(e -> musicLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-cursor: hand; " +
                                    "-fx-padding: 8; -fx-background-color: #666666; -fx-background-radius: 5;"));
                            musicLabel.setOnMouseClicked(e -> {
                                stopAllMediaExceptHistory();
                                currentMusicIndex = musicPlayers.indexOf(player);
                                showFullscreenMusic();
                                player.play();
                            });

                            HBox musicItem = new HBox(musicLabel);
                            musicItem.setAlignment(Pos.CENTER);
                            musicItem.setPadding(new Insets(5));
                            musicContainer.getChildren().add(musicItem);
                        } catch (Exception e) {
                            System.err.println("Error loading music file: " + file.getFileName());
                        }
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (musicPlayers.isEmpty()) {
            Label noMusicLabel = new Label("No traditional music samples available");
            noMusicLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
            musicContainer.getChildren().add(noMusicLabel);
        }

        return musicContainer;
    }

    private VBox createCultureSection() {
        VBox cultureContainer = new VBox(10);
        cultureContainer.setStyle("-fx-padding: 15; -fx-background-color: rgba(88, 88, 88, 0.7); -fx-border-radius: 5;");
        cultureContainer.setAlignment(Pos.CENTER);

        HBox imagesBox = new HBox(30);
        imagesBox.setAlignment(Pos.CENTER);

        String[][] cultureImageData = {
                {"/images/Basotho_Hat.jpg", "Basotho Hat"},
                {"/images/seanamarena.jpg", "Seanamarena Blanket"},
                {"/images/seshoeshoe.jpeg", "Seshoeshoe Dress"},
                {"/images/Thethana.jpg", "Thethana Attire"}
        };

        for (String[] imageData : cultureImageData) {
            try {
                InputStream stream = getClass().getResourceAsStream(imageData[0]);
                if (stream != null) {
                    Image image = new Image(stream);
                    cultureImages.add(image);

                    VBox imageContainer = new VBox(5);
                    imageContainer.setAlignment(Pos.CENTER);
                    imageContainer.setPrefWidth(200);

                    ImageView imageView = new ImageView(image);
                    imageView.setFitHeight(150);
                    imageView.setFitWidth(180);
                    imageView.setPreserveRatio(true);
                    imageView.setStyle("-fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 5, 0, 0, 0);");
                    imageView.setOnMouseEntered(e -> imageView.setStyle("-fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(255,215,0,0.8), 10, 0, 0, 0);"));
                    imageView.setOnMouseExited(e -> imageView.setStyle("-fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 5, 0, 0, 0);"));
                    imageView.setOnMouseClicked(e -> {
                        currentCultureImageIndex = cultureImages.indexOf(image);
                        showFullscreenImage(cultureImages, currentCultureImageIndex, imageData[1]);
                    });

                    Label nameLabel = new Label(imageData[1]);
                    nameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
                    nameLabel.setAlignment(Pos.CENTER);
                    nameLabel.setMaxWidth(180);
                    nameLabel.setWrapText(true);

                    imageContainer.getChildren().addAll(imageView, nameLabel);
                    imagesBox.getChildren().add(imageContainer);
                }
            } catch (Exception e) {
                System.err.println("Could not load image: " + imageData[0]);
            }
        }

        if (cultureImages.isEmpty()) {
            Label noImagesLabel = new Label("No cultural images available");
            noImagesLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
            cultureContainer.getChildren().add(noImagesLabel);
        } else {
            cultureContainer.getChildren().add(imagesBox);
        }

        return cultureContainer;
    }

    private VBox createFoodSection() {
        VBox foodContainer = new VBox(10);
        foodContainer.setStyle("-fx-padding: 15; -fx-background-color: rgba(88, 88, 88, 0.7); -fx-border-radius: 5;");
        foodContainer.setAlignment(Pos.CENTER);

        HBox imagesBox = new HBox(30);
        imagesBox.setAlignment(Pos.CENTER);

        String[][] foodImageData = {
                {"/images/p&m.jpeg", "Papa & Meat"},
                {"/images/motoho.jpeg", "Motoho"},
                {"/images/pone.jpg", "Pone"},
                {"/images/pot.jpeg", "Traditional Pot"}
        };

        for (String[] imageData : foodImageData) {
            try {
                InputStream stream = getClass().getResourceAsStream(imageData[0]);
                if (stream != null) {
                    Image image = new Image(stream);
                    foodImages.add(image);

                    VBox imageContainer = new VBox(5);
                    imageContainer.setAlignment(Pos.CENTER);
                    imageContainer.setPrefWidth(200);

                    ImageView imageView = new ImageView(image);
                    imageView.setFitHeight(150);
                    imageView.setFitWidth(180);
                    imageView.setPreserveRatio(true);
                    imageView.setStyle("-fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 5, 0, 0, 0);");
                    imageView.setOnMouseEntered(e -> imageView.setStyle("-fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(255,215,0,0.8), 10, 0, 0, 0);"));
                    imageView.setOnMouseExited(e -> imageView.setStyle("-fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 5, 0, 0, 0);"));
                    imageView.setOnMouseClicked(e -> {
                        currentFoodImageIndex = foodImages.indexOf(image);
                        showFullscreenImage(foodImages, currentFoodImageIndex, imageData[1]);
                    });

                    Label nameLabel = new Label(imageData[1]);
                    nameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
                    nameLabel.setAlignment(Pos.CENTER);
                    nameLabel.setMaxWidth(180);
                    nameLabel.setWrapText(true);

                    imageContainer.getChildren().addAll(imageView, nameLabel);
                    imagesBox.getChildren().add(imageContainer);
                }
            } catch (Exception e) {
                System.err.println("Could not load image: " + imageData[0]);
            }
        }

        if (foodImages.isEmpty()) {
            Label noImagesLabel = new Label("No food images available");
            noImagesLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
            foodContainer.getChildren().add(noImagesLabel);
        } else {
            foodContainer.getChildren().add(imagesBox);
        }

        return foodContainer;
    }

    private VBox createVideosSection() {
        VBox videosContainer = new VBox(10);
        videosContainer.setStyle("-fx-padding: 15; -fx-background-color: rgba(88, 88, 88, 0.7); -fx-border-radius: 5;");
        videosContainer.setAlignment(Pos.CENTER);

        HBox videosBox = new HBox(30);
        videosBox.setAlignment(Pos.CENTER);

        try {
            URL videosDir = getClass().getResource("/videos");
            if (videosDir != null) {
                Path videosPath = Paths.get(videosDir.toURI());
                try (var stream = Files.newDirectoryStream(videosPath, "*.mp4")) {
                    stream.forEach(file -> {
                        try {
                            Media media = new Media(file.toUri().toString());
                            MediaPlayer player = new MediaPlayer(media);
                            videoPlayers.add(player);

                            VBox videoContainer = new VBox(5);
                            videoContainer.setAlignment(Pos.CENTER);
                            videoContainer.setPrefWidth(300);

                            MediaView mediaView = new MediaView(player);
                            mediaView.setFitWidth(280);
                            mediaView.setFitHeight(180);
                            mediaView.setStyle("-fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 5, 0, 0, 0);");
                            mediaView.setOnMouseEntered(e -> mediaView.setStyle("-fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(255,215,0,0.8), 10, 0, 0, 0);"));
                            mediaView.setOnMouseExited(e -> mediaView.setStyle("-fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 5, 0, 0, 0);"));

                            
                            mediaView.setOnMouseClicked(e -> {
                                stopAllMediaExceptHistory();
                                currentVideoIndex = videoPlayers.indexOf(player);
                                showFullscreenVideo();
                            });

                            Label nameLabel = new Label(file.getFileName().toString().replace(".mp4", ""));
                            nameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
                            nameLabel.setAlignment(Pos.CENTER);
                            nameLabel.setMaxWidth(280);
                            nameLabel.setWrapText(true);

                            StackPane videoPane = new StackPane(mediaView);
                            videoPane.setStyle("-fx-background-color: black; -fx-padding: 2;");

                            videoContainer.getChildren().addAll(videoPane, nameLabel);
                            videosBox.getChildren().add(videoContainer);
                        } catch (Exception e) {
                            System.err.println("Error loading video: " + file.getFileName());
                        }
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (videoPlayers.isEmpty()) {
            Label noVideosLabel = new Label("No cultural videos available");
            noVideosLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
            videosContainer.getChildren().add(noVideosLabel);
        } else {
            videosContainer.getChildren().add(videosBox);
        }

        return videosContainer;
    }

    private GridPane createDistrictsGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(20);
        grid.setPadding(new Insets(15));
        grid.setStyle("-fx-background-color: rgba(88, 88, 88, 0.7); -fx-border-radius: 5;");
        grid.setAlignment(Pos.CENTER);

        addDistrictInfo(grid, 0, 0, "Berea", "Lowlands", "Morija Museum, Setsoto Gallery, Kome Caves");
        addDistrictInfo(grid, 1, 0, "Butha-Buthe", "Highlands", "Maletsunyane Falls, Oxbow Lodge");
        addDistrictInfo(grid, 2, 0, "Leribe", "Lowlands", "Liphofung Cave, Katse Dam");
        addDistrictInfo(grid, 0, 1, "Mafeteng", "Lowlands", "Mafeteng Museum, Makhaleng River");
        addDistrictInfo(grid, 1, 1, "Maseru", "Lowlands", "Royal Palace, Thaba-Bosiu, Pioneer Mall");
        addDistrictInfo(grid, 2, 1, "Mohale's Hoek", "Lowlands", "Sehlabathebe National Park");
        addDistrictInfo(grid, 0, 2, "Mokhotlong", "Highlands", "Sani Pass, Thabana Ntlenyana");
        addDistrictInfo(grid, 1, 2, "Qacha's Nek", "Highlands", "Qacha's Nek Border Post");
        addDistrictInfo(grid, 2, 2, "Quthing", "Lowlands", "Cave Dwellings");
        addDistrictInfo(grid, 0, 3, "Thaba-Tseka", "Highlands", "Katse Dam, Bokong Nature Reserve");

        return grid;
    }

    private void addDistrictInfo(GridPane grid, int col, int row, String name, String region, String attractions) {
        VBox districtBox = new VBox(8);
        districtBox.setStyle("-fx-background-color: #666666; -fx-padding: 15; -fx-border-radius: 5; " +
                "-fx-border-color: #888888; -fx-border-width: 1;");
        districtBox.setPrefWidth(250);
        districtBox.setAlignment(Pos.CENTER);

        Label nameLabel = new Label(name);
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        nameLabel.setTextFill(Color.GOLD);
        nameLabel.setAlignment(Pos.CENTER);
        nameLabel.setMaxWidth(Double.MAX_VALUE);

        Label regionLabel = new Label("Region: " + region);
        regionLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        regionLabel.setTextFill(Color.LIGHTGRAY);
        regionLabel.setAlignment(Pos.CENTER);

        Label attractionsLabel = new Label("Attractions:\n" + attractions);
        attractionsLabel.setFont(Font.font("Arial", 14));
        attractionsLabel.setTextFill(Color.WHITE);
        attractionsLabel.setWrapText(true);
        attractionsLabel.setAlignment(Pos.CENTER);

        districtBox.getChildren().addAll(nameLabel, regionLabel, attractionsLabel);
        grid.add(districtBox, col, row);
    }

    private void showFullscreenMusic() {
        if (musicPlayers.isEmpty()) return;

        currentMusicPlayer = musicPlayers.get(currentMusicIndex);

        VBox musicView = new VBox(30);
        musicView.setAlignment(Pos.CENTER);
        musicView.setStyle("-fx-background-color: rgba(54, 54, 54, 0.85); -fx-padding: 40;");
        musicView.setPrefHeight(400);

        String songName = currentMusicPlayer.getMedia().getSource()
                .replace(".mp3", "")
                .replace("%20", " ")
                .substring(currentMusicPlayer.getMedia().getSource().lastIndexOf("/") + 1);

        Label titleLabel = new Label("Now Playing: " + songName);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        titleLabel.setTextFill(Color.GOLD);

        HBox controls = new HBox(20);
        controls.setAlignment(Pos.CENTER);

        Button prevButton = createMediaButton("Previous", "#2196F3");
        prevButton.setOnAction(e -> {
            if (!musicPlayers.isEmpty()) {
                currentMusicPlayer.stop();
                currentMusicIndex = (currentMusicIndex - 1 + musicPlayers.size()) % musicPlayers.size();
                showFullscreenMusic();
                musicPlayers.get(currentMusicIndex).play();
            }
        });

        Button playButton = createMediaButton("Play", "#4CAF50");
        playButton.setOnAction(e -> currentMusicPlayer.play());

        Button pauseButton = createMediaButton("Pause", "#FF9800");
        pauseButton.setOnAction(e -> currentMusicPlayer.pause());

        Button stopButton = createMediaButton("Stop", "#F44336");
        stopButton.setOnAction(e -> currentMusicPlayer.stop());

        Button nextButton = createMediaButton("Next", "#2196F3");
        nextButton.setOnAction(e -> {
            if (!musicPlayers.isEmpty()) {
                currentMusicPlayer.stop();
                currentMusicIndex = (currentMusicIndex + 1) % musicPlayers.size();
                showFullscreenMusic();
                musicPlayers.get(currentMusicIndex).play();
            }
        });

        controls.getChildren().addAll(prevButton, playButton, pauseButton, stopButton, nextButton);
        VBox.setMargin(controls, new Insets(20, 0, 20, 0));

        VBox container = new VBox(30, titleLabel, controls);
        container.setAlignment(Pos.CENTER);

        musicView.getChildren().add(container);

        view.getChildren().clear();
        view.getChildren().add(musicView);

        currentMusicPlayer.play();
    }

    private void showFullscreenImage(List<Image> images, int index, String titleText) {
        VBox imageView = new VBox(30);
        imageView.setAlignment(Pos.CENTER);
        imageView.setStyle("-fx-background-color: rgba(54, 54, 54, 0.85); -fx-padding: 40;");
        imageView.setPrefHeight(500);

        Label titleLabel = new Label(titleText);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.GOLD);

        ImageView fullImageView = new ImageView(images.get(index));
        fullImageView.setPreserveRatio(true);
        fullImageView.setFitWidth(600);
        fullImageView.setFitHeight(400);
        fullImageView.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 20, 0, 0, 0);");

        HBox navButtons = new HBox(20);
        navButtons.setAlignment(Pos.CENTER);

        Button prevButton = createMediaButton("Previous", "#2196F3");
        prevButton.setOnAction(e -> {
            int newIndex = (index - 1 + images.size()) % images.size();
            showFullscreenImage(images, newIndex, titleText);
        });

        Button nextButton = createMediaButton("Next", "#2196F3");
        nextButton.setOnAction(e -> {
            int newIndex = (index + 1) % images.size();
            showFullscreenImage(images, newIndex, titleText);
        });

        navButtons.getChildren().addAll(prevButton, nextButton);
        VBox.setMargin(navButtons, new Insets(20, 0, 20, 0));

        VBox container = new VBox(30, titleLabel, fullImageView, navButtons);
        container.setAlignment(Pos.CENTER);

        imageView.getChildren().add(container);

        view.getChildren().clear();
        view.getChildren().add(imageView);
    }

    private void showFullscreenVideo() {
        if (videoPlayers.isEmpty()) return;

        currentVideoPlayer = videoPlayers.get(currentVideoIndex);

        VBox videoView = new VBox(30);
        videoView.setAlignment(Pos.CENTER);
        videoView.setStyle("-fx-background-color: rgba(54, 54, 54, 0.85); -fx-padding: 40;");
        videoView.setPrefHeight(700); 

        String videoName = currentVideoPlayer.getMedia().getSource()
                .replace(".mp4", "")
                .replace("%20", " ")
                .substring(currentVideoPlayer.getMedia().getSource().lastIndexOf("/") + 1);

        Label titleLabel = new Label("Now Playing: " + videoName);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        titleLabel.setTextFill(Color.GOLD);

        MediaView mediaView = new MediaView(currentVideoPlayer);
        mediaView.setFitWidth(800); 
        mediaView.setFitHeight(450);
        mediaView.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 20, 0, 0, 0);");

        
        mediaView.setOnMouseClicked(e -> {
            if (currentVideoPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                currentVideoPlayer.pause();
            } else {
                currentVideoPlayer.play();
            }
        });

        HBox controls = new HBox(20);
        controls.setAlignment(Pos.CENTER);

        Button prevButton = createMediaButton("Previous", "#2196F3");
        prevButton.setOnAction(e -> {
            if (!videoPlayers.isEmpty()) {
                currentVideoPlayer.stop();
                currentVideoIndex = (currentVideoIndex - 1 + videoPlayers.size()) % videoPlayers.size();
                showFullscreenVideo();
                videoPlayers.get(currentVideoIndex).play();
            }
        });

        Button playButton = createMediaButton("Play", "#4CAF50");
        playButton.setOnAction(e -> currentVideoPlayer.play());

        Button pauseButton = createMediaButton("Pause", "#FF9800");
        pauseButton.setOnAction(e -> currentVideoPlayer.pause());

        Button stopButton = createMediaButton("Stop", "#F44336");
        stopButton.setOnAction(e -> {
            currentVideoPlayer.stop();
            view.getChildren().clear();
            createView(); 
        });

        Button nextButton = createMediaButton("Next", "#2196F3");
        nextButton.setOnAction(e -> {
            if (!videoPlayers.isEmpty()) {
                currentVideoPlayer.stop();
                currentVideoIndex = (currentVideoIndex + 1) % videoPlayers.size();
                showFullscreenVideo();
                videoPlayers.get(currentVideoIndex).play();
            }
        });

        controls.getChildren().addAll(prevButton, playButton, pauseButton, stopButton, nextButton);
        VBox.setMargin(controls, new Insets(20, 0, 20, 0));

        VBox container = new VBox(20, titleLabel, mediaView, controls); // Reduced spacing from 30 to 20
        container.setAlignment(Pos.CENTER);

        videoView.getChildren().add(container);

        view.getChildren().clear();
        view.getChildren().add(videoView);

        currentVideoPlayer.play();
    }

    private Button createMediaButton(String text, String color) {
        Button button = new Button(text);
        button.setStyle("-fx-font-size: 16px; -fx-padding: 10 20; -fx-background-color: " + color +
                "; -fx-text-fill: white; -fx-background-radius: 5;");
        button.setOnMouseEntered(e -> button.setStyle("-fx-font-size: 16px; -fx-padding: 10 20; " +
                "-fx-background-color: derive(" + color + ", 20%); -fx-text-fill: white; -fx-background-radius: 5;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-font-size: 16px; -fx-padding: 10 20; " +
                "-fx-background-color: " + color + "; -fx-text-fill: white; -fx-background-radius: 5;"));
        return button;
    }

    private void stopAllMediaExceptHistory() {
        if (currentMusicPlayer != null) {
            currentMusicPlayer.stop();
        }
        if (currentVideoPlayer != null) {
            currentVideoPlayer.stop();
        }
        musicPlayers.forEach(MediaPlayer::stop);
        videoPlayers.forEach(MediaPlayer::stop);
    }

    private void stopAllMedia() {
        if (historyPlayer != null) {
            historyPlayer.stop();
        }
        stopAllMediaExceptHistory();
    }
}
