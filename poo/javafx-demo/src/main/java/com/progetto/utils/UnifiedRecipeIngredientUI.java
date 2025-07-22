package com.progetto.utils;

import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import com.progetto.Entity.EntityDto.Ricetta;
import com.progetto.Entity.EntityDto.Ingredienti;
import java.util.ArrayList;
import java.util.List;

public class UnifiedRecipeIngredientUI {
    public static VBox createUnifiedRecipeBox(Ricetta ricetta, List<Ricetta> recipesList, VBox container, boolean isHybrid, Runnable notifyControllerOfChange, List<String> unitaDiMisuraList) {
        VBox recipeBox = new VBox(10);
        recipeBox.getStyleClass().add("recipe-box");

        HBox nameBox = new HBox(10);
        Label nameLabel = new Label("Nome ricetta:");
        TextField nameField = new TextField(ricetta.getNome());
        nameField.setPromptText("es. Pasta alla carbonara");
        nameField.getStyleClass().add("ingredient-name-field");
        nameField.textProperty().addListener((obs, oldVal, newVal) -> {
            ricetta.setNome(newVal);
            notifyControllerOfChange.run();
        });

        Button removeBtn = new Button("Rimuovi");
        removeBtn.getStyleClass().add("remove-button");
        removeBtn.setOnAction(e -> {
            recipesList.remove(ricetta);
            container.getChildren().remove(recipeBox);
            notifyControllerOfChange.run();
        });
        nameBox.getChildren().addAll(nameLabel, nameField, removeBtn);

        VBox ingredientsContainer = new VBox(5);
        Label ingredientsLabel = new Label("Ingredienti:");
        ingredientsLabel.getStyleClass().add("ingredients-title");

        if (ricetta.getIngredientiRicetta() == null || ricetta.getIngredientiRicetta().isEmpty()) {
            ricetta.setIngredientiRicetta(new ArrayList<>());
            ricetta.getIngredientiRicetta().add(new Ingredienti("", 0, ""));
        }

        for (Ingredienti ingrediente : ricetta.getIngredientiRicetta()) {
            HBox ingredientBox = createUnifiedIngredientBox(ingrediente, ricetta, ingredientsContainer, isHybrid, notifyControllerOfChange, unitaDiMisuraList);
            ingredientsContainer.getChildren().add(ingredientBox);
        }

        Button addIngredientBtn = new Button("+ Ingrediente");
        addIngredientBtn.getStyleClass().add("add-ingredient-button");
        addIngredientBtn.setOnAction(e -> {
            Ingredienti nuovo = new Ingredienti("", 0, "");
            ricetta.getIngredientiRicetta().add(nuovo);
            HBox ingredientBox = createUnifiedIngredientBox(nuovo, ricetta, ingredientsContainer, isHybrid, notifyControllerOfChange, unitaDiMisuraList);
            if (ingredientsContainer.getChildren().contains(addIngredientBtn)) {
                ingredientsContainer.getChildren().add(ingredientsContainer.getChildren().size() - 1, ingredientBox);
            } else {
                ingredientsContainer.getChildren().add(ingredientBox);
            }
            notifyControllerOfChange.run();
        });
        ingredientsContainer.getChildren().add(addIngredientBtn);

        recipeBox.getChildren().addAll(nameBox, ingredientsLabel, ingredientsContainer);
        return recipeBox;
    }

    public static HBox createUnifiedIngredientBox(Ingredienti ingrediente, Ricetta ricetta, VBox container, boolean isHybrid, Runnable notifyControllerOfChange, List<String> unitaDiMisuraList) {
        HBox ingredientBox = new HBox(15);
        ingredientBox.getStyleClass().addAll("ingredient-box", "ingredient-row");

        TextField nomeField = new TextField(ingrediente.getNome());
        nomeField.setPromptText("Nome ingrediente");
        nomeField.getStyleClass().add("ingredient-name-field");
        nomeField.textProperty().addListener((obs, oldVal, newVal) -> {
            ingrediente.setNome(newVal);
            notifyControllerOfChange.run();
        });

        TextField quantitaField = new TextField(ingrediente.getQuantita() == 0 ? "" : String.valueOf(ingrediente.getQuantita()));
        quantitaField.setPromptText("Quantità");
        quantitaField.getStyleClass().add("ingredient-quantity-field");
        quantitaField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*(\\.\\d*)?")) {
                quantitaField.setText(oldVal);
            } else {
                try {
                    if (!newVal.isEmpty() && !newVal.equals(".")) {
                        float value = Float.parseFloat(newVal);
                        if (value >= 0) {
                            ingrediente.setQuantita(value);
                            notifyControllerOfChange.run();
                        }
                    } else {
                        ingrediente.setQuantita(0);
                    }
                } catch (NumberFormatException e) {
                }
            }
        });

        ComboBox<String> unitaCombo = new ComboBox<>();
        if (unitaDiMisuraList != null && !unitaDiMisuraList.isEmpty()) {
            unitaCombo.getItems().addAll(unitaDiMisuraList);
        } else {
            unitaCombo.getItems().addAll("g", "kg", "ml", "l", "cucchiaino", "cucchiaio", "tazza", "pz", "spicchio", "foglia", "rametto");
        }
        if (ingrediente.getUnitaMisura() != null && !ingrediente.getUnitaMisura().isEmpty()) {
            unitaCombo.setValue(ingrediente.getUnitaMisura());
        } else if (!unitaCombo.getItems().isEmpty()) {
            unitaCombo.setValue(unitaCombo.getItems().get(0));
            ingrediente.setUnitaMisura(unitaCombo.getItems().get(0));
        }
        unitaCombo.setEditable(false);
        unitaCombo.setPromptText("Unità");
        unitaCombo.getStyleClass().add("combo-box");
        unitaCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                ingrediente.setUnitaMisura(newVal);
                notifyControllerOfChange.run();
            }
        });

        Button removeBtn = new Button("✕");
        removeBtn.getStyleClass().add("remove-ingredient-button");
        removeBtn.setOnAction(e -> {
            ricetta.getIngredientiRicetta().remove(ingrediente);
            container.getChildren().remove(ingredientBox);
            notifyControllerOfChange.run();
        });

        ingredientBox.getChildren().addAll(nomeField, quantitaField, unitaCombo, removeBtn);
        return ingredientBox;
    }
}
