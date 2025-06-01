package com.progetto.controller;

import javafx.fxml.FXML;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.Node;

public class HomepageUtenteController {

    @FXML
    private FlowPane cardcontainer;

    public void addCard(Node card) {
        cardcontainer.getChildren().add(card);
    }
}
