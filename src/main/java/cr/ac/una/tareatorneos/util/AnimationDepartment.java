package cr.ac.una.tareatorneos.util;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import javafx.scene.control.Label;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.awt.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

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


    public static void slideUpWithEpicBounceClean(Node node, Duration delay, double sceneHeight) {
        // ⛔ Reset antes de animar
        node.setTranslateY(0); // 💥 Esto evita acumulación de desplazamiento

        // ✅ Empezamos fuera de pantalla
        double startY = sceneHeight + 100;
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

    // ✨ Mostrar candado con animación de zoom explosivo y fade
    public static void animateUnlockExplosion(Node lockNode, Runnable onFinished) {
        System.out.println("🔒 Mostrando candado...");

        // ⏳ Esperar que el GIF se vea durante 1.5 segundos (ajústalo según el GIF)
        PauseTransition waitBeforeZoom = new PauseTransition(Duration.seconds(1.5));

        waitBeforeZoom.setOnFinished(e -> {
            // 🎬 Zoom explosivo + desvanecimiento
            ScaleTransition scale = new ScaleTransition(Duration.seconds(1.2), lockNode);
            scale.setFromX(1.0);
            scale.setFromY(1.0);
            scale.setToX(4.0);
            scale.setToY(4.0);
            scale.setInterpolator(Interpolator.EASE_OUT);

            FadeTransition fade = new FadeTransition(Duration.seconds(1.2), lockNode);
            fade.setFromValue(1.0);
            fade.setToValue(0.0);
            fade.setInterpolator(Interpolator.EASE_IN);

            ParallelTransition transition = new ParallelTransition(scale, fade);
            transition.setOnFinished(ev -> {
                lockNode.setVisible(false);
                if (onFinished != null) Platform.runLater(onFinished);
            });

            transition.play();
        });

        waitBeforeZoom.play(); // ⏱️ Inicia espera antes del efecto
    }



    public static void revealAchievementImage(Node imageNode, Duration delay) {
        imageNode.setVisible(true);
        imageNode.setOpacity(0);
        imageNode.setLayoutY(0);
        imageNode.setTranslateY(0);

        FadeTransition fade = new FadeTransition(Duration.seconds(1), imageNode);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.setDelay(delay);

//        glowBorder(imageNode);
        subtleBounce(imageNode);

        fade.play();
    }

    public static void goldenBurstExplosion(Pane container, int count, Duration duration) {
        Random random = new Random();

        // 🧠 Obtener centro real del contenedor
        double centerX = container.getWidth() / 2.0;
        double centerY = container.getHeight() / 2.0;

        for (int i = 0; i < count; i++) {
            Circle spark = new Circle(4, Color.GOLD);

            // 📌 Posición absoluta en el centro
            spark.setLayoutX(centerX);
            spark.setLayoutY(centerY);

            // 🎯 Calcular dirección y distancia aleatoria
            double angle = 2 * Math.PI * random.nextDouble();
            double distance = 150 + random.nextDouble() * 200; // 150–250 px

            double deltaX = distance * Math.cos(angle);
            double deltaY = distance * Math.sin(angle);

            // 🚀 Movimiento relativo desde el centro
            TranslateTransition move = new TranslateTransition(duration, spark);
            move.setFromX(0);
            move.setFromY(0);
            move.setToX(deltaX);
            move.setToY(deltaY);
            move.setInterpolator(Interpolator.EASE_OUT);

            // 💨 Desvanecer mientras se mueve
            FadeTransition fade = new FadeTransition(duration, spark);
            fade.setFromValue(1.0);
            fade.setToValue(0.0);

            // 🎇 Ejecutar y remover al terminar
            ParallelTransition animation = new ParallelTransition(move, fade);
            animation.setOnFinished(e -> container.getChildren().remove(spark));
            animation.play();

            container.getChildren().add(spark); // 👈 Agregar al final
            spark.toBack();
        }
    }

    private static Timeline rainingLoop;
    public static void startInfiniteRainingAchievements(Pane container, Image image, int particlesPerWave, Duration interval, Duration fallDuration) {


        PauseTransition delay = new PauseTransition(Duration.seconds(0.2)); // ⚠️ espera a layout
        delay.setOnFinished(event -> {
            rainingLoop = new Timeline(
                    new KeyFrame(Duration.ZERO, e -> {
                        double containerWidth = container.getWidth();
                        double containerHeight = container.getHeight();

                        System.out.println("🌧 Width: " + containerWidth + " | Height: " + containerHeight);

                        if (containerWidth < 10 || containerHeight < 10) return; // Seguridad

                        for (int i = 0; i < particlesPerWave; i++) {
                            ImageView rain = new ImageView(image);
                            rain.setFitWidth(60);
                            rain.setFitHeight(60);
                            rain.setOpacity(0.25 + Math.random() * 0.4);

                            // ⬇️ Posición inicial arriba y aleatoria horizontal
                            double startX = Math.random() * (containerWidth - 60); // evitar que se salga del borde
                            double startY = Math.random() * 100;

                            // ⬅️ ¡Usar translate en lugar de layout!
                            rain.setTranslateX(startX);
                            rain.setTranslateY(startY);

                            container.getChildren().add(rain);
                            rain.toBack();

                            // ⬇️ Animación de caída
                            TranslateTransition fall = new TranslateTransition(fallDuration, rain);
                            fall.setFromY(startY);
                            fall.setToY(containerHeight); // más allá del fondo
                            fall.setInterpolator(Interpolator.LINEAR);

                            FadeTransition fade = new FadeTransition(fallDuration, rain);
                            fade.setFromValue(rain.getOpacity());
                            fade.setToValue(0.0);

                            ParallelTransition anim = new ParallelTransition(fall, fade);
                            anim.setOnFinished(evt -> container.getChildren().remove(rain));
                            anim.play();
                        }
                    }),
                    new KeyFrame(interval)
            );

            rainingLoop.setCycleCount(Animation.INDEFINITE);
            rainingLoop.play();
        });

        delay.play(); // ⏳ Esperamos antes de iniciar la lluvia
    }


    public static void stopRainingAchievements(Pane container) {
        if (rainingLoop != null) {
            rainingLoop.stop();
            rainingLoop = null;
        }

        // 🔥 Eliminar todos los nodos que fueron agregados como lluvia (trofeos)
        container.getChildren().removeIf(node ->
                node instanceof ImageView &&
                        ((ImageView) node).getFitWidth() == 60 &&
                        ((ImageView) node).getFitHeight() == 60 &&
                        ((ImageView) node).getOpacity() < 1.0
        );
    }


}
