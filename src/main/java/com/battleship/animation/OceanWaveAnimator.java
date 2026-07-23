package com.battleship.animation;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * Handles the animated ocean background used by empty sea cells.
 *
 * <p>
 * This class loads one horizontal sprite sheet, cuts it into individual
 * frames using {@link PixelReader} and {@link WritableImage}, creates one
 * {@link ImagePattern} per frame, and updates all registered sea-cell layers
 * using a JavaFX {@link Timeline}.
 * </p>
 *
 * <p>
 * The sprite sheet is expected to have four frames of 64x64 pixels placed
 * horizontally:
 * </p>
 *
 * <pre>
 * frame 0: x = 0
 * frame 1: x = 64
 * frame 2: x = 128
 * frame 3: x = 192
 * </pre>
 *
 * <p>
 * The frames are cut only once when the animator is created. During the
 * animation cycle, only the fill pattern of each registered rectangle is
 * updated.
 * </p>
 */
public final class OceanWaveAnimator {

    private static final int FRAME_COUNT = 4;
    private static final int FRAME_WIDTH = 64;
    private static final int FRAME_HEIGHT = 64;
    private static final int EXPECTED_SPRITESHEET_WIDTH = FRAME_COUNT * FRAME_WIDTH;
    private static final int EXPECTED_SPRITESHEET_HEIGHT = FRAME_HEIGHT;

    private final ImagePattern[] framePatterns;
    private final List<Rectangle> oceanLayers;
    private final Timeline timeline;

    private int currentFrameIndex;

    /**
     * Creates an ocean animator with the default frame duration.
     *
     * @param spriteSheetPath resource path of the ocean sprite sheet.
     */
    public OceanWaveAnimator(String spriteSheetPath) {
        this(spriteSheetPath, Duration.millis(350));
    }

    /**
     * Creates an ocean animator with a custom frame duration.
     *
     * @param spriteSheetPath resource path of the ocean sprite sheet.
     * @param frameDuration   duration between animation frames.
     */
    public OceanWaveAnimator(String spriteSheetPath, Duration frameDuration) {
        Image spriteSheet = loadSpriteSheet(spriteSheetPath);
        validateSpriteSheet(spriteSheet);

        this.framePatterns = cutFramesAsPatterns(spriteSheet);
        this.oceanLayers = new ArrayList<>();
        this.currentFrameIndex = 0;

        this.timeline = new Timeline(
                new KeyFrame(frameDuration, event -> advanceFrame()));
        this.timeline.setCycleCount(Animation.INDEFINITE);
    }

    /**
     * Creates a visual ocean layer for one board cell.
     *
     * <p>
     * The returned rectangle uses the current animation frame as its fill
     * and is automatically registered so future timeline ticks update it.
     * </p>
     *
     * @param width  rectangle width.
     * @param height rectangle height.
     * @return rectangle with animated ocean fill.
     */
    public Rectangle createOceanLayer(double width, double height) {
        Rectangle oceanLayer = new Rectangle(width, height);
        oceanLayer.setMouseTransparent(true);
        oceanLayer.setSmooth(false);
        oceanLayer.setFill(framePatterns[currentFrameIndex]);

        oceanLayers.add(oceanLayer);
        return oceanLayer;
    }

    /**
     * Clears the registered ocean layers.
     *
     * <p>
     * This should be called before rebuilding the board grids. It avoids
     * keeping references to old JavaFX nodes that are no longer displayed.
     * </p>
     */
    public void clearRegisteredCells() {
        oceanLayers.clear();
    }

    /**
     * Starts the ocean animation.
     */
    public void start() {
        if (timeline.getStatus() != Animation.Status.RUNNING) {
            timeline.play();
        }
    }

    /**
     * Stops the ocean animation.
     */
    public void stop() {
        timeline.stop();
    }

    /**
     * Stops the animation and clears all registered cells.
     */
    public void dispose() {
        stop();
        clearRegisteredCells();
    }

    /**
     * Loads the sprite sheet from the resources folder.
     *
     * @param spriteSheetPath resource path.
     * @return loaded image.
     */
    private Image loadSpriteSheet(String spriteSheetPath) {
        InputStream inputStream = OceanWaveAnimator.class.getResourceAsStream(spriteSheetPath);

        if (inputStream == null) {
            throw new IllegalArgumentException("Ocean sprite sheet was not found: " + spriteSheetPath);
        }

        Image spriteSheet = new Image(inputStream);

        if (spriteSheet.isError()) {
            throw new IllegalArgumentException("Ocean sprite sheet could not be loaded: " + spriteSheetPath);
        }

        return spriteSheet;
    }

    /**
     * Validates the expected sprite sheet dimensions.
     *
     * @param spriteSheet loaded sprite sheet.
     */
    private void validateSpriteSheet(Image spriteSheet) {
        int width = (int) Math.round(spriteSheet.getWidth());
        int height = (int) Math.round(spriteSheet.getHeight());

        if (width != EXPECTED_SPRITESHEET_WIDTH || height != EXPECTED_SPRITESHEET_HEIGHT) {
            throw new IllegalArgumentException(
                    "Ocean sprite sheet must be 256x64 px. Current size: " + width + "x" + height);
        }

        if (spriteSheet.getPixelReader() == null) {
            throw new IllegalArgumentException("Ocean sprite sheet does not provide a PixelReader.");
        }
    }

    /**
     * Cuts the sprite sheet into four frames and converts each one to an
     * ImagePattern.
     *
     * @param spriteSheet complete sprite sheet.
     * @return array of image patterns, one per animation frame.
     */
    private ImagePattern[] cutFramesAsPatterns(Image spriteSheet) {
        PixelReader pixelReader = spriteSheet.getPixelReader();
        ImagePattern[] patterns = new ImagePattern[FRAME_COUNT];

        for (int frameIndex = 0; frameIndex < FRAME_COUNT; frameIndex++) {
            int sourceX = frameIndex * FRAME_WIDTH;
            WritableImage frame = new WritableImage(
                    pixelReader,
                    sourceX,
                    0,
                    FRAME_WIDTH,
                    FRAME_HEIGHT);

            patterns[frameIndex] = new ImagePattern(
                    frame,
                    0,
                    0,
                    FRAME_WIDTH,
                    FRAME_HEIGHT,
                    false);
        }

        return patterns;
    }

    /**
     * Advances the animation to the next frame and updates all registered
     * ocean layers at the same time.
     */
    private void advanceFrame() {
        currentFrameIndex = (currentFrameIndex + 1) % FRAME_COUNT;
        ImagePattern currentPattern = framePatterns[currentFrameIndex];

        for (Rectangle oceanLayer : oceanLayers) {
            oceanLayer.setFill(currentPattern);
        }
    }
}