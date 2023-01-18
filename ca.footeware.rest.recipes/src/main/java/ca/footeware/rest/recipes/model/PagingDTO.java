/**
 * 
 */
package ca.footeware.rest.recipes.model;

import java.util.List;

/**
 * @author Footeware.ca
 *
 */
public record PagingDTO(long total, List<Recipe> recipes) {
	public PagingDTO(long total, List<Recipe> recipes) {
		this.total = total;
		this.recipes = recipes;
	}
}
