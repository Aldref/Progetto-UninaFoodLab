package com.progetto.utils;

import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;

public class ImageClipUtils {
    public static void setCircularClip(ImageView imageView, double radius) {
        if (imageView == null) return;
        double width = imageView.getFitWidth() > 0 ? imageView.getFitWidth() : (imageView.getImage() != null ? imageView.getImage().getWidth() : 0);
        double height = imageView.getFitHeight() > 0 ? imageView.getFitHeight() : (imageView.getImage() != null ? imageView.getImage().getHeight() : 0);
        if (width <= 0 || height <= 0) return;
        double r = radius > 0 ? radius : Math.min(width, height) / 2;
        Circle clip = new Circle(width / 2, height / 2, r);
        imageView.setClip(clip);
    }
}
