package cr.ac.una.tareatorneos.util;

import javafx.animation.*;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import javafx.scene.control.Label;


import java.awt.*;
import java.util.Random;

public class AnimationDepartment {

    public static void fadeIn(Node node, Duration delay) {
        node.setOpacity(0);
        FadeTransition fade = new FadeTransition(Duration.millis(800), node);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.setDelay(delay);
        fade.play();
    }

    public static void dropCrown(Node node, Duration delay) {
        node.setVisible(true);
        node.setOpacity(0);

        // 💡 Posición inicial fuera de pantalla (hacia arriba)
        node.setTranslateY(-200);

        // 🔽 Animación de bajada
        TranslateTransition drop = new TranslateTransition(Duration.seconds(1), node);
        drop.setToY(0); // Volver a la posición original
        drop.setInterpolator(Interpolator.EASE_OUT);
        drop.setDelay(delay);

        // 🌫 Fade-in
        FadeTransition fade = new FadeTransition(Duration.seconds(1), node);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.setDelay(delay);

        // 🔁 Rebote sutil
        ScaleTransition bounce = new ScaleTransition(Duration.seconds(0.5), node);
        bounce.setFromX(1.2);
        bounce.setFromY(1.2);
        bounce.setToX(1.0);
        bounce.setToY(1.0);
        bounce.setCycleCount(2);
        bounce.setAutoReverse(true);
        bounce.setDelay(delay.add(Duration.seconds(1)));

        // 🧩 Ejecutar animaciones en secuencia
        SequentialTransition crownDrop = new SequentialTransition(
                new ParallelTransition(drop, fade),
                bounce
        );


        crownDrop.play();
    }

    public static void launchGoldenConfetti(Pane container, int count, Duration duration) {
        Random random = new Random();

        double margin = 5; // Margen para no tocar bordes

        for (int i = 0; i < count; i++) {
            Circle confetti = new Circle(5, Color.GOLD);
            double containerWidth = container.getWidth();
            double containerHeight = container.getHeight();

            // ❗ Restringimos el rango de aparición en X y caída en Y
            double startX = margin + random.nextDouble() * (containerWidth - 2 * margin);
            double endY = containerHeight - margin; // no más allá del fondo visible

            confetti.setTranslateX(startX);
            confetti.setLayoutY(-50);

            container.getChildren().add(confetti);

            TranslateTransition fall = new TranslateTransition(duration, confetti);
            fall.setFromY(0);
            fall.setToY(endY);
            fall.setInterpolator(Interpolator.LINEAR);

            fall.setOnFinished(e -> container.getChildren().remove(confetti));
            fall.play();
        }
    }

    public static void neonGlowLoop(Node node) {
        DropShadow neonGlow = new DropShadow();
        neonGlow.setColor(Color.web("#FFD700")); // Dorado brillante
        neonGlow.setRadius(10);
        neonGlow.setSpread(0.7);
        neonGlow.setOffsetX(0);
        neonGlow.setOffsetY(0);
        node.setEffect(neonGlow);

        Timeline glowLoop = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(neonGlow.radiusProperty(), 10),
                        new KeyValue(neonGlow.spreadProperty(), 0.4)
                ),
                new KeyFrame(Duration.seconds(0.7),
                        new KeyValue(neonGlow.radiusProperty(), 20),
                        new KeyValue(neonGlow.spreadProperty(), 0.7)
                ),
                new KeyFrame(Duration.seconds(1.4),
                        new KeyValue(neonGlow.radiusProperty(), 10),
                        new KeyValue(neonGlow.spreadProperty(), 0.4)
                )
        );

        glowLoop.setCycleCount(Animation.INDEFINITE);
        glowLoop.setAutoReverse(true);
        glowLoop.play();
    }

    private static Timeline lightSweepTimeline; // 🌟 Referencia global

    public static void animatedLightSweep(Label label) {
        stopAnimatedLightSweep(); // 🔁 Detener si ya estaba corriendo

        lightSweepTimeline = new Timeline();
        final int[] step = {0};

        KeyFrame frame = new KeyFrame(Duration.millis(100), e -> {
            int offset = step[0] % 100;
            String gradient = String.format(
                    "-fx-text-fill: linear-gradient(from %d%% 0%% to %d%% 0%%, #ffffff33, #FFD700, #ffffff33);",
                    offset, offset + 20
            );
            label.setStyle(gradient);
            step[0] += 5;
        });

        lightSweepTimeline.getKeyFrames().add(frame);
        lightSweepTimeline.setCycleCount(Animation.INDEFINITE);
        lightSweepTimeline.play();
    }

    public static void stopAnimatedLightSweep() {
        if (lightSweepTimeline != null) {
            lightSweepTimeline.stop();
            lightSweepTimeline = null;
        }
    }




    private static Timeline confettiLoop;

    public static void startConfettiLoop(Pane container, int particlesPerBatch, Duration interval) {
        stopConfetti(); // Limpia si ya estaba corriendo

        confettiLoop = new Timeline(
                new KeyFrame(Duration.ZERO, e -> {
                    launchGoldenConfetti(container, particlesPerBatch, Duration.seconds(3));
                }),
                new KeyFrame(interval) // intervalo entre "disparos"
        );
        confettiLoop.setCycleCount(Animation.INDEFINITE);
        confettiLoop.play();
    }

    public static void startConfettiLoopWithDelay(Pane container, int particlesPerBatch, Duration interval, Duration initialDelay) {
        stopConfetti(); // Detiene cualquier loop anterior si ya está corriendo

        // ⏳ Esperar el delay antes de lanzar el confeti
        PauseTransition delay = new PauseTransition(initialDelay);
        delay.setOnFinished(e -> startConfettiLoop(container, particlesPerBatch, interval));
        delay.play();
    }


    public static void stopConfetti() {
        if (confettiLoop != null) {
            confettiLoop.stop();
            confettiLoop = null;
        }
    }


    public static void spotlightEffect(Node lightNode, Duration delay) {
        lightNode.setOpacity(0);
        lightNode.setVisible(true);

        // Fade in
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.5), lightNode);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.setDelay(delay);

        // Mantener la luz durante la caída
        PauseTransition hold = new PauseTransition(Duration.seconds(1.2));

        // Fade out
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.5), lightNode);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);

        SequentialTransition spotlight = new SequentialTransition(fadeIn, hold, fadeOut);
        spotlight.play();
    }


    public static void sparkleEffect(Node node, Duration delay) {
        node.setOpacity(0);
        node.setVisible(true);

        // ☑️ Impedir cualquier evento permanentemente (sin interferir)
        node.setMouseTransparent(true);

        // 💥 Explosión visual
        ScaleTransition scale = new ScaleTransition(Duration.seconds(0.9), node);
        scale.setFromX(0.1);
        scale.setFromY(0.1);
        scale.setToX(2.5);
        scale.setToY(2.5);
        scale.setInterpolator(Interpolator.EASE_OUT);

        FadeTransition fade = new FadeTransition(Duration.seconds(0.9), node);
        fade.setFromValue(0.9);
        fade.setToValue(0.0);
        fade.setInterpolator(Interpolator.EASE_IN);

        ParallelTransition sparkle = new ParallelTransition(scale, fade);
        sparkle.setDelay(delay);

        // 🚫 REMOVER: esto hace que vuelva a bloquear tras la animación
        // sparkle.setOnFinished(e -> node.setMouseTransparent(false));

        sparkle.play();
    }

    public static void slideFromTop(Node node, Duration delay) {
        node.setOpacity(0);
        TranslateTransition slide = new TranslateTransition(Duration.millis(800), node);
        slide.setFromY(-50);
        slide.setToY(0);
        slide.setDelay(delay);
        slide.setInterpolator(Interpolator.EASE_OUT);

        FadeTransition fade = new FadeTransition(Duration.millis(800), node);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.setDelay(delay);

        ParallelTransition anim = new ParallelTransition(slide, fade);
        anim.play();
    }

    public static void slideUpWithEpicBounce(Node node, Duration delay) {
        node.setOpacity(0);

        node.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                double sceneHeight = newScene.getHeight();

                // Fase 1: Subida desde abajo
                TranslateTransition slideIn = new TranslateTransition(Duration.millis(1500), node);
                slideIn.setFromY(sceneHeight);
                slideIn.setToY(-20); // sube ligeramente más arriba para dar rebote
                slideIn.setInterpolator(Interpolator.EASE_OUT);
                slideIn.setDelay(delay);

                // Fase 2: Rebote hacia la posición final
                TranslateTransition settle = new TranslateTransition(Duration.millis(300), node);
                settle.setFromY(-20);
                settle.setToY(0);
                settle.setInterpolator(Interpolator.EASE_IN);

                // Fade mientras sube
                FadeTransition fade = new FadeTransition(Duration.millis(1000), node);
                fade.setFromValue(0);
                fade.setToValue(1);
                fade.setDelay(delay);

                // Encadenar las fases
                SequentialTransition sequence = new SequentialTransition(
                        new ParallelTransition(slideIn, fade),
                        settle
                );

                sequence.play();
            }
        });
    }

    public static void slideUpWithEpicBounceClean(Node node, Duration delay, double startY) {
        node.setTranslateY(startY); // ❗ posicionalo debajo de la vista
        node.setOpacity(0);

        TranslateTransition slideIn = new TranslateTransition(Duration.millis(1500), node);
        slideIn.setFromY(startY);
        slideIn.setToY(-20);
        slideIn.setInterpolator(Interpolator.EASE_OUT);
        slideIn.setDelay(delay);

        TranslateTransition settle = new TranslateTransition(Duration.millis(300), node);
        settle.setFromY(-20);
        settle.setToY(0);
        settle.setInterpolator(Interpolator.EASE_IN);

        FadeTransition fade = new FadeTransition(Duration.millis(1000), node);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.setDelay(delay);

        new SequentialTransition(
                new ParallelTransition(slideIn, fade),
                settle
        ).play();
    }








    public static void subtleBounce(Node node) {
        TranslateTransition bounce = new TranslateTransition(Duration.seconds(2), node);
        bounce.setByY(5);
        bounce.setAutoReverse(true);
        bounce.setCycleCount(javafx.animation.Animation.INDEFINITE);
        bounce.setInterpolator(Interpolator.EASE_BOTH);
        bounce.play();
    }

    public static void glowBorder(Node node) {
        DropShadow glow = new DropShadow();
        glow.setColor(Color.web("#FFD700"));
        glow.setRadius(10);
        glow.setSpread(0.6);
        node.setEffect(glow);

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(glow.radiusProperty(), 10)),
                new KeyFrame(Duration.seconds(1), new KeyValue(glow.radiusProperty(), 20)),
                new KeyFrame(Duration.seconds(2), new KeyValue(glow.radiusProperty(), 10))
        );
        timeline.setCycleCount(javafx.animation.Animation.INDEFINITE);
        timeline.setAutoReverse(true);
        timeline.play();
    }
}
