/**
 *
 */
package ca.footeware.rest.galleries.controllers;

import java.io.File;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import ca.footeware.rest.galleries.exceptions.ImageException;
import ca.footeware.rest.galleries.models.Gallery;
import ca.footeware.rest.galleries.models.Thumbnail;
import ca.footeware.rest.galleries.services.ImageService;

/**
 * Exposes image-related endpoints.
 *
 * @author Footeware.ca
 */
@RestController
public class ImageController {

	private ImageService service;

	/**
	 * Constructor.
	 *
	 * @param service {@link ImageService} injected
	 */
	public ImageController(ImageService service) {
		this.service = service;
	}

	/**
	 * Restrict the #name to letters, digits and dashes only.
	 *
	 * @return boolean true if name passes test
	 * @throws ImageException when an image-related exception occurs
	 */
	private void checkName(String name) throws ImageException {
		if (!name.matches("[\\sa-zA-Z0-9_-]++")) {
			throw new ImageException(
					"Invalid gallery name:" + name + ". Must be spaces, a-z, A-Z, 0-9, underscore or dashes.");
		}
	}

	/**
	 * Create a line-feed-delimited String of specific EXIF item labels and values.
	 *
	 * @param name
	 * @param exif
	 * @return {@link String}
	 */
	@SuppressWarnings("unused")
	private String compileExifString(String name, Map<String, String> exif) {
		var b = new StringBuilder();
		b.append("Name: " + name + "\n");
		b.append("Model: " + exif.get("Model") + "\n");
		b.append("ProcessingSoftware: " + exif.get("ProcessingSoftware") + "\n");
		b.append("DateTime: " + exif.get("DateTime") + "\n");
		b.append("ExposureTime: " + exif.get("ExposureTime") + "\n");
		b.append("FNumber: " + exif.get("FNumber") + "\n");
		b.append("PhotographicSensitivity: " + exif.get("PhotographicSensitivity") + "\n");
		b.append("ExposureCompensation: " + exif.get("ExposureCompensation") + "\n");
		b.append("FocalLength: " + exif.get("FocalLength") + "\n");
		b.append("FocalLengthIn35mmFormat: " + exif.get("FocalLengthIn35mmFormat"));
		return b.toString();
	}

	/**
	 * Get the gallery page with names of the images to be dynamically obtained from
	 * images in images.path.
	 *
	 * @return {@link List} of {@link Gallery}
	 * @throws ImageException if an image-related exception occurs.
	 */
	@GetMapping(value = "/galleries", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Gallery> getGalleries() throws ImageException {
		return service.getGalleries();
	}

	/**
	 * Get the gallery page by name with names of the images to be dynamically
	 * obtained from images in images.path.
	 *
	 * @param galleryName {@link String}
	 * @return {@link List} of {@link Thumbnail}
	 * @throws ImageException if an image-related exception occurs.
	 */
	@Cacheable(value = "galleries", key = "#galleryName")
	@GetMapping(value = "/galleries/{galleryName}", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Thumbnail> getGallery(@PathVariable String galleryName) throws ImageException {
		checkName(galleryName);
		List<Thumbnail> dtos = new ArrayList<>();
		for (File file : service.getFiles(galleryName)) {
			String filename = file.getName();
			if ("secret".equals(filename)) {
				break;
			} else {
//				Map<String, String> exif = service.getExif(file);
//				String exifString = compileExifString(filename, exif);
				String exifString = "";
				byte[] thumbnailAsBytes = service.getThumbnailAsBytes(galleryName, filename);
				String encodedThumb = Base64.getEncoder().encodeToString(thumbnailAsBytes);
				dtos.add(new Thumbnail(filename, exifString, encodedThumb));
			}
		}
		return dtos;
	}

	/**
	 * Get the base64-encoded String of the requested image.
	 * 
	 * @param galleryName {@link String}
	 * @param imageName   {@link String}
	 * @return {@link String}
	 * @throws ImageException
	 */
	@Cacheable(value = "images", key = "#imageName")
	@GetMapping(value = "/galleries/{galleryName}/{imageName}", produces = MediaType.IMAGE_JPEG_VALUE)
	public byte[] getImage(@PathVariable String galleryName, @PathVariable String imageName) throws ImageException {
		checkName(galleryName);
		return service.getImageAsBytes(galleryName, imageName);
	}
}
