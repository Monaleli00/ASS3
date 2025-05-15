package com.example.demo3;

import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import javafx.stage.Stage;

import java.util.*;

public class MapDashboard {

    private WebView webView;
    private final Map<String, DistrictInfo> districts = new HashMap<>();
    private final Map<String, TouristAttraction> attractions = new HashMap<>();
    private final Map<String, List<TouristAttraction>> districtAttractions = new HashMap<>();

    public MapDashboard() {
        initializeDistricts();
        initializeAttractions();
        linkAttractionsToDistricts();
    }

    public VBox getView() {
        VBox mainContainer = new VBox(10);
        mainContainer.setStyle("-fx-padding: 10px; -fx-alignment: center;");

        Label titleLabel = new Label("Interactive Map of Lesotho");
        titleLabel.setFont(Font.font(20));
        titleLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: black;");

        webView = new WebView();
        webView.setPrefSize(800, 650);
        webView.getEngine().setJavaScriptEnabled(true);

        String htmlContent = createHtmlMapContent();
        webView.getEngine().loadContent(htmlContent);

        // JavaScript-Java bridge
        webView.getEngine().getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                JSObject window = (JSObject) webView.getEngine().executeScript("window");
                window.setMember("javaConnector", new JavaConnector());
            }
        });

        HBox controls = createNavigationControls();

        mainContainer.getChildren().addAll(titleLabel, controls, webView);
        return mainContainer;
    }

    private String createHtmlMapContent() {
        return """
        <!DOCTYPE html>
        <html>
        <head>
            <title>Lesotho Tourist Map</title>
            <meta charset="utf-8" />
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <link rel="stylesheet" href="https://unpkg.com/leaflet@1.7.1/dist/leaflet.css" />
            <style>
                html, body, #map { height: 100%; margin: 0; padding: 0; }
                
                /* Simplified circular marker styling */
                .district-hotspot {
                    background-color: red;
                    width: 12px;
                    height: 12px;
                    border-radius: 50%;
                    border: 2px solid white;
                    box-shadow: 0 0 5px rgba(0,0,0,0.5);
                    cursor: pointer;
                }
                
                .hotspot-label {
                    position: absolute;
                    background-color: white;
                    padding: 3px 6px;
                    border-radius: 3px;
                    font-size: 12px;
                    font-family: Arial, sans-serif;
                    pointer-events: none;
                    white-space: nowrap;
                    box-shadow: 0 1px 3px rgba(0,0,0,0.2);
                    transform: translateX(-50%) translateY(10px);
                    display: none;
                }
                
                .district-marker:hover .hotspot-label {
                    display: block;
                }
                
                #legend-container {
                    position: absolute;
                    bottom: 10px;
                    right: 10px;
                    z-index: 1000;
                    background: white;
                    border-radius: 5px;
                    box-shadow: 0 0 10px rgba(0,0,0,0.2);
                    padding: 10px;
                    font-family: Arial, sans-serif;
                    font-size: 12px;
                }
                
                .legend-item {
                    display: flex;
                    align-items: center;
                    margin: 5px 0;
                }
                
                .legend-icon {
                    width: 12px;
                    height: 12px;
                    margin-right: 8px;
                    background-color: red;
                    border-radius: 50%;
                    border: 2px solid white;
                }
            </style>
        </head>
        <body>
            <div id="map"></div>
            <div id="legend-container">
                <div class="legend-item">
                    <div class="legend-icon"></div>
                    <span>District Hotspot</span>
                </div>
            </div>

            <script src="https://unpkg.com/leaflet@1.7.1/dist/leaflet.js"></script>
            <script>
                var map = L.map('map').setView([-29.5, 28.0], 8);

                L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                    attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
                }).addTo(map);

                // Precise district coordinates adjusted for better visibility
                var districts = {
                    "Berea": {lat: -29.148, lng: 27.899},
                    "Butha-Buthe": {lat: -28.767, lng: 28.233},
                    "Leribe": {lat: -28.9, lng: 28.05},
                    "Mafeteng": {lat: -29.8, lng: 27.25},
                    "Maseru": {lat: -29.3167, lng: 27.4833},
                    "Mohale's Hoek": {lat: -30.15, lng: 27.7},
                    "Mokhotlong": {lat: -29.2833, lng: 29.0667},
                    "Qacha's Nek": {lat: -30.1167, lng: 28.6833},
                    "Quthing": {lat: -30.4, lng: 27.7},
                    "Thaba-Tseka": {lat: -29.5167, lng: 28.6}
                };

                for (let district in districts) {
                    let pos = districts[district];
                    let marker = L.marker([pos.lat, pos.lng], {
                        icon: L.divIcon({
                            className: 'district-marker',
                            html: '<div class="district-hotspot"></div><div class="hotspot-label">' + district + '</div>',
                            iconSize: [12, 12],
                            iconAnchor: [6, 6]
                        })
                    }).addTo(map);

                    marker.on('click', function() {
                        if (window.javaConnector && typeof window.javaConnector.showDistrictInfo === 'function') {
                            window.javaConnector.showDistrictInfo(district);
                        }
                    });
                    
                    marker.on('mouseover', function() {
                        this.getElement().querySelector('.hotspot-label').style.display = 'block';
                    });
                    
                    marker.on('mouseout', function() {
                        this.getElement().querySelector('.hotspot-label').style.display = 'none';
                    });
                }
            </script>
        </body>
        </html>
        """;
    }

    private HBox createNavigationControls() {
        HBox controls = new HBox(15);
        controls.setAlignment(Pos.CENTER);

        Button zoomInBtn = new Button("Zoom In");
        zoomInBtn.setOnAction(e -> webView.getEngine().executeScript("map.zoomIn()"));

        Button zoomOutBtn = new Button("Zoom Out");
        zoomOutBtn.setOnAction(e -> webView.getEngine().executeScript("map.zoomOut()"));

        Button resetBtn = new Button("Reset View");
        resetBtn.setOnAction(e -> webView.getEngine().executeScript("map.setView([-29.5, 28.0], 8)"));

        controls.getChildren().addAll(zoomInBtn, zoomOutBtn, resetBtn);
        return controls;
    }

    private void initializeDistricts() {
        districts.put("Berea", new DistrictInfo("Berea", "Lowlands"));
        districts.put("Butha-Buthe", new DistrictInfo("Butha-Buthe", "Highlands"));
        districts.put("Leribe", new DistrictInfo("Leribe", "Lowlands"));
        districts.put("Mafeteng", new DistrictInfo("Mafeteng", "Lowlands"));
        districts.put("Maseru", new DistrictInfo("Maseru", "Lowlands"));
        districts.put("Mohale's Hoek", new DistrictInfo("Mohale's Hoek", "Lowlands"));
        districts.put("Mokhotlong", new DistrictInfo("Mokhotlong", "Highlands"));
        districts.put("Qacha's Nek", new DistrictInfo("Qacha's Nek", "Highlands"));
        districts.put("Quthing", new DistrictInfo("Quthing", "Lowlands"));
        districts.put("Thaba-Tseka", new DistrictInfo("Thaba-Tseka", "Highlands"));
    }

    private void initializeAttractions() {
        // Berea
        attractions.put("Morija Museum", new TouristAttraction("Morija Museum", "Historical museum showcasing Basotho culture and history."));
        attractions.put("Thaba Bosiu", new TouristAttraction("Thaba Bosiu", "Historic mountain fortress of King Moshoeshoe I."));
        attractions.put("Kome Caves", new TouristAttraction("Kome Caves", "Ancient cave dwellings still inhabited today."));

        // Butha-Buthe
        attractions.put("Maletsunyane Falls", new TouristAttraction("Maletsunyane Falls", "One of the highest single-drop waterfalls in Africa."));
        attractions.put("AfriSki Resort", new TouristAttraction("AfriSki Resort", "Southern Africa's only ski resort."));

        // Leribe
        attractions.put("Liphofung Cave", new TouristAttraction("Liphofung Cave", "Historic cave with San rock paintings."));
        attractions.put("Hlotse Hot Springs", new TouristAttraction("Hlotse Hot Springs", "Natural hot springs with therapeutic properties."));

        // Mafeteng
        attractions.put("Malealea Lodge", new TouristAttraction("Malealea Lodge", "Popular lodge offering pony treks and cultural experiences."));
        attractions.put("Makhaleng River", new TouristAttraction("Makhaleng River", "Scenic river valley with hiking trails."));

        // Maseru
        attractions.put("Royal Palace", new TouristAttraction("Royal Palace", "Official residence of the King of Lesotho."));
        attractions.put("Basotho Hat Shop", new TouristAttraction("Basotho Hat Shop", "Iconic shop selling traditional Basotho crafts."));
        attractions.put("Pioneer Mall", new TouristAttraction("Pioneer Mall", "Largest shopping mall in Lesotho."));

        // Mohale's Hoek
        attractions.put("Sehlabathebe National Park", new TouristAttraction("Sehlabathebe National Park", "Remote park with unique flora and fauna."));

        // Mokhotlong
        attractions.put("Sani Pass", new TouristAttraction("Sani Pass", "Mountain pass connecting Lesotho to South Africa."));
        attractions.put("Thabana Ntlenyana", new TouristAttraction("Thabana Ntlenyana", "Highest peak in Southern Africa at 3,482m."));

        // Qacha's Nek
        attractions.put("Qacha's Nek Border Post", new TouristAttraction("Qacha's Nek Border Post", "Scenic border crossing with South Africa."));

        // Quthing
        attractions.put("Ha Kome Cave Dwellings", new TouristAttraction("Ha Kome Cave Dwellings", "Ancient cave homes built into sandstone cliffs."));

        // Thaba-Tseka
        attractions.put("Katse Dam", new TouristAttraction("Katse Dam", "Massive dam with impressive engineering and views."));
        attractions.put("Bokong Nature Reserve", new TouristAttraction("Bokong Nature Reserve", "High-altitude reserve with waterfalls and wildlife."));
    }

    private void linkAttractionsToDistricts() {
        // Berea
        districtAttractions.put("Berea", Arrays.asList(
                attractions.get("Morija Museum"),
                attractions.get("Kome Caves")
        ));

        // Butha-Buthe
        districtAttractions.put("Butha-Buthe", Arrays.asList(
                attractions.get("Maletsunyane Falls"),
                attractions.get("AfriSki Resort")
        ));

        // Leribe
        districtAttractions.put("Leribe", Arrays.asList(
                attractions.get("Liphofung Cave"),
                attractions.get("Hlotse Hot Springs")
        ));

        // Mafeteng
        districtAttractions.put("Mafeteng", Arrays.asList(
                attractions.get("Malealea Lodge"),
                attractions.get("Makhaleng River")
        ));

        // Maseru
        districtAttractions.put("Maseru", Arrays.asList(
                attractions.get("Royal Palace"),
                attractions.get("Basotho Hat Shop"),
                attractions.get("Pioneer Mall")
        ));

        // Mohale's Hoek
        districtAttractions.put("Mohale's Hoek", Arrays.asList(
                attractions.get("Sehlabathebe National Park")
        ));

        // Mokhotlong
        districtAttractions.put("Mokhotlong", Arrays.asList(
                attractions.get("Sani Pass"),
                attractions.get("Thabana Ntlenyana")
        ));

        // Qacha's Nek
        districtAttractions.put("Qacha's Nek", Arrays.asList(
                attractions.get("Qacha's Nek Border Post")
        ));

        // Quthing
        districtAttractions.put("Quthing", Arrays.asList(
                attractions.get("Ha Kome Cave Dwellings")
        ));

        // Thaba-Tseka
        districtAttractions.put("Thaba-Tseka", Arrays.asList(
                attractions.get("Katse Dam"),
                attractions.get("Bokong Nature Reserve")
        ));
    }

    private void showDistrictPopup(DistrictInfo district) {
        Stage popupStage = new Stage();
        popupStage.setTitle(district.getName() + " District - Places of Interest");

        VBox root = new VBox(15);
        root.setStyle("-fx-padding: 20; -fx-background-color: #f5f5f5;");
        root.setPrefSize(500, 500);

        Label title = new Label(district.getName() + " District");
        title.setFont(Font.font(22));
        title.setStyle("-fx-font-weight: bold; -fx-text-fill: #333;");

        Label region = new Label("Region: " + district.getRegion());
        region.setFont(Font.font(16));
        region.setStyle("-fx-text-fill: #555;");

        Separator separator = new Separator();

        Label attractionsLabel = new Label("Places of Interest:");
        attractionsLabel.setFont(Font.font(18));
        attractionsLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #333;");

        List<TouristAttraction> attractions = districtAttractions.getOrDefault(district.getName(), new ArrayList<>());
        VBox attractionsBox = new VBox(10);

        if (attractions.isEmpty()) {
            Label noAttractions = new Label("No attractions listed for this district.");
            noAttractions.setStyle("-fx-font-style: italic; -fx-text-fill: #777;");
            attractionsBox.getChildren().add(noAttractions);
        } else {
            for (TouristAttraction attraction : attractions) {
                VBox attractionBox = new VBox(5);
                attractionBox.setStyle("-fx-padding: 10; -fx-background-color: white; -fx-border-radius: 5; -fx-background-radius: 5;");

                Label name = new Label(attraction.getName());
                name.setFont(Font.font(16));
                name.setStyle("-fx-font-weight: bold; -fx-text-fill: #0066cc;");

                Label description = new Label(attraction.getDescription());
                description.setFont(Font.font(14));
                description.setStyle("-fx-text-fill: #444;");
                description.setWrapText(true);

                attractionBox.getChildren().addAll(name, description);
                attractionsBox.getChildren().add(attractionBox);
            }
        }

        ScrollPane scrollPane = new ScrollPane(attractionsBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #f5f5f5; -fx-border-color: transparent;");

        root.getChildren().addAll(title, region, separator, attractionsLabel, scrollPane);

        Scene scene = new Scene(root);
        popupStage.setScene(scene);
        popupStage.show();
    }

    // Java-JavaScript bridge class
    public class JavaConnector {
        public void showDistrictInfo(String districtName) {
            Platform.runLater(() -> {
                DistrictInfo district = districts.get(districtName);
                if (district != null) {
                    showDistrictPopup(district);
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.setContentText("District information not found for: " + districtName);
                    alert.showAndWait();
                }
            });
        }
    }

    // Supporting data classes
    private static class DistrictInfo {
        private final String name;
        private final String region;

        public DistrictInfo(String name, String region) {
            this.name = name;
            this.region = region;
        }

        public String getName() { return name; }
        public String getRegion() { return region; }
    }

    private static class TouristAttraction {
        private final String name;
        private final String description;

        public TouristAttraction(String name, String description) {
            this.name = name;
            this.description = description;
        }

        public String getName() { return name; }
        public String getDescription() { return description; }
    }
}