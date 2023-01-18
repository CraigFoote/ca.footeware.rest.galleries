/**
 * 
 */
package ca.footeware.rest.recipes.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import ca.footeware.rest.recipes.model.Recipe;

/**
 * @author Footeware.ca
 *
 */
public interface RecipeRepository extends MongoRepository<Recipe, String> {

	Page<Recipe> findByNameContainingIgnoreCaseOrBodyContainingIgnoreCaseOrTagsContainingIgnoreCaseOrderByNameAsc(
			String name, String body, String tags, Pageable pageable);

	Page<Recipe> findAll(Pageable pageable);

	Page<Recipe> findByTags(String tags, Pageable pageable);
}
