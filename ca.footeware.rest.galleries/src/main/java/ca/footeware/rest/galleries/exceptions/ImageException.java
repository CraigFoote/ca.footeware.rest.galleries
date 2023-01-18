/**
 *
 */
package ca.footeware.rest.galleries.exceptions;

/**
 * Indicates an exception occurred while working with images.
 *
 * @author Footeware.ca
 */
public class ImageException extends Exception {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * An image-related exception.
	 * 
	 * @param message {@link String}
	 */
	public ImageException(String message) {
		super(message);
	}
}
