package com.example.demo3;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;

import java.io.InputStream;
import java.util.List;

public class ImageDetailViewer {
    private final BorderPane root;
    private final List<Place> places;
    private final ImageView imageView;
    private int currentIndex = 0;
    private Timeline slideshow;

    public ImageDetailViewer(List<Place> places, int startIndex) {
        this.places = places;
        this.currentIndex = startIndex;

        // Initialize ImageView
        imageView = new ImageView();
        imageView.setFitWidth(600);
        imageView.setFitHeight(400);
        imageView.setPreserveRatio(true);

        // Initialize layout and add ImageView to center
        root = new BorderPane();
        root.setCenter(imageView);

        // Start slideshow after layout is fully set
        startSlideshow();
    }

    private void startSlideshow() {
        if (places == null || places.isEmpty()) {
            System.err.println("No places provided for the slideshow.");
            return;
        }

        // Set first image before slideshow starts
        updateImage();

        slideshow = new Timeline(new KeyFrame(Duration.seconds(3), e -> {
            currentIndex = (currentIndex + 1) % places.size();
            updateImage();
        }));
        slideshow.setCycleCount(Timeline.INDEFINITE);
        slideshow.play();
    }

    private void updateImage() {
        if (places == null || places.isEmpty()) return;

        String path = String.valueOf(places.get(currentIndex).imagePath);
        InputStream stream = getClass().getResourceAsStream(path);

        // Use placeholder if image path is invalid
        if (stream == null) {
            System.err.println("Image not found: " + path + " — loading placeholder.");
            stream = getClass().getResourceAsStream("/images/placeholder.png");
        }

        if (stream != null) {
            imageView.setImage(new Image(stream));
        } else {
            System.err.println("Placeholder image also not found.");
        }
    }

    public BorderPane getView() {
        return root;
    }
}
