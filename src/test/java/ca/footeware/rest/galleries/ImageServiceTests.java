/**
 *
 */
package ca.footeware.rest.galleries;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import static org.junit.jupiter.api.Assertions.*;

import org.apache.commons.io.FilenameUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import ca.footeware.rest.galleries.exceptions.ImageException;
import ca.footeware.rest.galleries.models.Gallery;
import ca.footeware.rest.galleries.services.ImageService;

/**
 * Tests {@link ImageService}.
 *
 * @author Footeware.ca
 */
@SpringBootTest
class ImageServiceTests {

	private static final String GALLERY_NAME = "gallery1";
	private static final String IMAGE_HORIZONTAL = "test-image-horizontal.png";
	private static final String IMAGE_SQUARE = "test-image-square.png";
	private static final String IMAGE_VERTICAL = "test-image-vertical.png";
	private static final String IMAGE_WEBP = "DSC_0074.webp";

	@Value("${images.path}")
	String imagesPath;

	@Value("${images.max.dimension}")
	private Integer maxImgDim;

	@Value("${images.thumbnails.max.dimension}")
	private Integer maxTnDim;

	@Autowired
	private ImageService service;

	/**
	 * Test method for
	 * {@link ca.footeware.rest.galleries.services.ImageService#getExif(File)}
	 *
	 * @throws ImageException when shit goes south
	 * @throws IOException    when shit goes south
	 */
	@Disabled
	void testExif() throws ImageException, IOException {
		for (Gallery gallery : service.getGalleries()) {
			File[] imageFiles = service.getFiles(gallery.getName());
			for (File file : imageFiles) {
				Map<String, String> exif = service.getExif(file);
				if ("webp".equalsIgnoreCase(FilenameUtils.getExtension(file.getName()))
						|| "png".equalsIgnoreCase(FilenameUtils.getExtension(file.getName()))) {
					assertTrue(exif != null);
				} else {
					assertTrue(exif.containsKey("Model"), "Missing 'Model' entry for file:" + file);
				}
			}
		}
	}

	/**
	 * Test method for
	 * {@link ca.footeware.rest.galleries.services.ImageService#getFiles()}.
	 */
	@Test
	void testGetFiles() {
		File[] files = service.getFiles();
		assertEquals(1, files.length, "Wrong number of files found.");
	}

	/**
	 * Test method for
	 * {@link ca.footeware.rest.galleries.services.ImageService#getGalleries()}.
	 *
	 * @throws ImageException when shit goes south
	 */
	@Test
	void testGetGalleries() throws ImageException {
		List<Gallery> galleries = service.getGalleries();
		assertEquals(1, galleries.size(), "Should have been one gallery.");
	}

	/**
	 * Test method for
	 * {@link ca.footeware.rest.galleries.services.ImageService#getImageAsBytes(java.lang.String, java.lang.String)}.
	 *
	 * @throws IOException    when shit goes south
	 * @throws ImageException when shit goes south
	 */
	@Test
	void testGetImageAsBytes() throws IOException, ImageException {
		byte[] bytes = service.getImageAsBytes(GALLERY_NAME, IMAGE_HORIZONTAL);
		BufferedImage image = ImageIO.read(new ByteArrayInputStream(bytes));
		assertEquals(1241, image.getHeight(), "Image wrong height.");
		assertEquals(1920, image.getWidth(), "Image wrong width.");

		bytes = service.getImageAsBytes(GALLERY_NAME, IMAGE_VERTICAL);
		image = ImageIO.read(new ByteArrayInputStream(bytes));
		assertEquals(1920, image.getHeight(), "Image wrong height.");
		assertEquals(1241, image.getWidth(), "Image wrong width.");

		bytes = service.getImageAsBytes(GALLERY_NAME, IMAGE_SQUARE);
		image = ImageIO.read(new ByteArrayInputStream(bytes));
		assertEquals(1920, image.getHeight(), "Image wrong height.");
		assertEquals(1920, image.getWidth(), "Image wrong width.");
		
		bytes = service.getImageAsBytes(GALLERY_NAME, IMAGE_WEBP);
		image = ImageIO.read(new ByteArrayInputStream(bytes));
		assertEquals(1272, image.getHeight(), "Image wrong height.");
		assertEquals(1920, image.getWidth(), "Image wrong width.");
	}

	/**
	 * Test method for
	 * {@link ca.footeware.rest.galleries.services.ImageService#getThumbnailAsBytes(java.lang.String, java.lang.String)}.
	 *
	 * @throws IOException    when shit goes south
	 * @throws ImageException when shit goes south
	 */
	@Test
	void testGetThumbnailAsBytes() throws IOException, ImageException {
		byte[] bytes = service.getThumbnailAsBytes(GALLERY_NAME, IMAGE_HORIZONTAL);
		BufferedImage image = ImageIO.read(new ByteArrayInputStream(bytes));
		assertEquals(97, image.getHeight(), "Image wrong height.");
		assertEquals(150, image.getWidth(), "Image wrong width.");

		bytes = service.getThumbnailAsBytes(GALLERY_NAME, IMAGE_VERTICAL);
		image = ImageIO.read(new ByteArrayInputStream(bytes));
		assertEquals(150, image.getHeight(), "Image wrong height.");
		assertEquals(97, image.getWidth(), "Image wrong width.");

		bytes = service.getThumbnailAsBytes(GALLERY_NAME, IMAGE_SQUARE);
		image = ImageIO.read(new ByteArrayInputStream(bytes));
		assertEquals(150, image.getHeight(), "Image wrong height.");
		assertEquals(150, image.getWidth(), "Image wrong width.");
		
		bytes = service.getThumbnailAsBytes(GALLERY_NAME, IMAGE_WEBP);
		image = ImageIO.read(new ByteArrayInputStream(bytes));
		assertEquals(99, image.getHeight(), "Image wrong height.");
		assertEquals(150, image.getWidth(), "Image wrong width.");
	}

	/**
	 * Test method for
	 * {@link ca.footeware.rest.galleries.services.ImageService#ImageService}.
	 *
	 * @throws ImageException when shit goes south
	 */
	@Test
	void testImageService() throws ImageException {
		ImageService service = new ImageService(imagesPath);
		Assertions.assertNotNull(service);
	}

	/**
	 * Test method for
	 * {@link ca.footeware.rest.galleries.services.ImageService#resize(BufferedImage, int, java.awt.Dimension)}
	 *
	 * @throws IOException    when shit goes south
	 * @throws ImageException when shit goes south
	 */
	@Test
	void testResizeImage() throws IOException, ImageException {
		byte[] bytes = service.getImageAsBytes(GALLERY_NAME, IMAGE_HORIZONTAL);
		BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(bytes));
		int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
		BufferedImage image = service.resize(originalImage, type, service.getDimensions(originalImage, maxTnDim));
		assertNotNull(image);
		int width = image.getWidth();
		int height = image.getHeight();
		assertEquals(width, maxTnDim.intValue(), "Thumbnail was not the correct width.");
		assertTrue(height <= maxTnDim, "Thumbnail was too tall.");

		bytes = service.getImageAsBytes(GALLERY_NAME, IMAGE_VERTICAL);
		originalImage = ImageIO.read(new ByteArrayInputStream(bytes));
		type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
		image = service.resize(originalImage, type, service.getDimensions(originalImage, maxTnDim));
		assertNotNull(image);
		width = image.getWidth();
		height = image.getHeight();
		assertTrue(width <= maxTnDim, "Thumbnail was too wide.");
		assertEquals(height, maxTnDim.intValue(), "Thumbnail was not the correct height.");

		bytes = service.getImageAsBytes(GALLERY_NAME, IMAGE_SQUARE);
		originalImage = ImageIO.read(new ByteArrayInputStream(bytes));
		type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
		image = service.resize(originalImage, type, service.getDimensions(originalImage, maxTnDim));
		assertNotNull(image);
		width = image.getWidth();
		height = image.getHeight();
		assertEquals(width, maxTnDim.intValue(), "Thumbnail was not the correct width.");
		assertEquals(height, maxTnDim.intValue(), "Thumbnail was not the correct height.");
	}

}
