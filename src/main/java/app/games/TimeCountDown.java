package app.games;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

class TimeCountDown {
    private static Timeline timeline;
    private static int timeLeft;
    private static Runnable onTimeUpdate;
    private static Runnable onTimeUp;
    
    public static void initialize(int initialTimeSeconds, Runnable onTimeUpdateRunnable, Runnable onTimeUpRunnable) {
        timeLeft = initialTimeSeconds;
        onTimeUpdate = onTimeUpdateRunnable;
        onTimeUp = onTimeUpRunnable;

        if (timeline != null) {
            timeline.stop();
        }

        timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);

        KeyFrame keyFrame = new KeyFrame(Duration.seconds(1), event -> {
            timeLeft--;
            if (onTimeUpdate != null) {
                onTimeUpdate.run();
            }
            if (timeLeft <= 0) {
                timeline.stop();
                if (onTimeUp != null) {
                    onTimeUp.run();
                }
            }
        });

        timeline.getKeyFrames().add(keyFrame);
    }

    public static void start() {
        if (timeline != null) {
            timeline.play();
        }
    }

    public static void stop() {
        if (timeline != null) {
            timeline.stop();
        }
    }

    public static String getFormattedTime() {
        int minutes = timeLeft / 60;
        int seconds = timeLeft % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}
