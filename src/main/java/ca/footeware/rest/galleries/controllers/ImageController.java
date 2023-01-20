/**
 *
 */
package ca.footeware.rest.galleries.controllers;

import java.io.File;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

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
	 * Create a line-feed-delimited String of specific EXIF item labels and values.
	 *
	 * @param name
	 * @param exif
	 * @return {@link String}
	 */
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
	@GetMapping("/galleries")
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
	@GetMapping(value = "/galleries/{galleryName}", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Thumbnail> getGallery(@PathVariable String galleryName) throws ImageException {
		checkName(galleryName);
		List<Thumbnail> thumbs = new ArrayList<>();
		for (File file : service.getFiles(galleryName)) {
			Map<String, String> exif = service.getExif(file);
			String name = file.getName();
			if ("secret".equals(name)) {
				break;
			} else {
				String exifString = compileExifString(name, exif);
				byte[] bytes = service.getThumbnailAsBytes(galleryName, name);
				String encodedBytes = Base64.getEncoder().encodeToString(bytes);
				thumbs.add(new Thumbnail(name, exifString, encodedBytes));
			}
		}
		return thumbs;
	}

	/**
	 * Get the full-size version from the received gallery and image name.
	 *
	 * @param galleryName {@link String} gallery name
	 * @param imageName   {@link String} image file name
	 * @return byte[] the 'produces' attribute dictates how the browser will handle
	 *         the bytes, i.e as jpegs
	 * @throws ImageException when an image-related exception occurs
	 */
	@GetMapping(value = "/galleries/{galleryName}/{imageName}", produces = MediaType.TEXT_PLAIN_VALUE)
	public String getImage(@PathVariable String galleryName, @PathVariable String imageName) throws ImageException {
		checkName(galleryName);
		byte[] bytes = service.getImageAsBytes(galleryName, imageName);
		return Base64.getEncoder().encodeToString(bytes);
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
}
