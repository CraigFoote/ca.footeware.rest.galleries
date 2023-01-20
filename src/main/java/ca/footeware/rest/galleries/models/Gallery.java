package ca.footeware.rest.galleries.models;

/**
 * A named folder with optional 'secret' file.
 *
 * @author Footeware.ca
 */
public class Gallery {

	private String name;
	private boolean secret;

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the secret
	 */
	public boolean isSecret() {
		return secret;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param secret the secret to set
	 */
	public void setSecret(boolean secret) {
		this.secret = secret;
	}

}
