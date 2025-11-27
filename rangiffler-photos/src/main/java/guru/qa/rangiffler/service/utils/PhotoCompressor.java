package guru.qa.rangiffler.service.utils;

import lombok.SneakyThrows;
import net.coobird.thumbnailator.Thumbnails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

@Component
public class PhotoCompressor {

  private static final Logger LOG = LoggerFactory.getLogger(PhotoCompressor.class);

  private static final int MIN_WIDTH = 242;
  private static final int MIN_HEIGHT = 240;
  private static final int[] DIMENSIONS = {1200, 800, 600, 400};
  private static final float[] QUALITIES = {0.8f, 0.7f, 0.6f};
  private static final long MAX_SIZE = 150 * 1024; // 150KB

  @SneakyThrows
  public byte[] compressImage(byte[] originalImage) {
    if (shouldSkipCompression(originalImage)) {
      return originalImage;
    }

    LOG.info("Starting compression: {} bytes", originalImage.length);

    try {
      BufferedImage image = ImageIO.read(new ByteArrayInputStream(originalImage));
      if (image != null && (image.getWidth() < MIN_WIDTH || image.getHeight() < MIN_HEIGHT)) {
        LOG.warn("Image too small: {}x{}px, skipping compression",
          image.getWidth(), image.getHeight());
        return originalImage;
      }
    } catch (Exception e) {
      LOG.warn("Could not read image dimensions", e);
    }

    for (int dimension : DIMENSIONS) {
      if (dimension < MIN_WIDTH && dimension < MIN_HEIGHT) {
        continue;
      }

      for (float quality : QUALITIES) {
        try {
          byte[] compressed = compressWithSettings(originalImage, dimension, quality);

          if (compressed.length <= MAX_SIZE) {
            LOG.info("Successfully compressed to {} bytes ({}px, quality: {})",
              compressed.length, dimension, quality);
            return compressed;
          }

          LOG.debug("Compression {}px quality {} -> {} bytes (still too large)",
            dimension, quality, compressed.length);

        } catch (Exception e) {
          LOG.warn("Compression failed for {}px quality {}", dimension, quality, e);
        }
      }
    }

    LOG.warn("All compression attempts failed, using minimum dimensions");
    return compressWithSettings(originalImage, Math.max(MIN_WIDTH, MIN_HEIGHT), 0.5f);
  }

  private byte[] compressWithSettings(byte[] originalImage, int maxDimension, float quality) throws Exception {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    int targetSize = Math.max(maxDimension, Math.max(MIN_WIDTH, MIN_HEIGHT));

    Thumbnails.of(new ByteArrayInputStream(originalImage))
      .size(targetSize, targetSize)
      .outputFormat("JPEG")
      .outputQuality(quality)
      .toOutputStream(outputStream);

    return outputStream.toByteArray();
  }

  private boolean shouldSkipCompression(byte[] image) {
    return image == null || image.length == 0 || image.length <= 80 * 1024;
  }
}
