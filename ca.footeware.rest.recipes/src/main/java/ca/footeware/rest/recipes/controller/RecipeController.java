/**
 * 
 */
package ca.footeware.rest.recipes.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ca.footeware.rest.recipes.model.PagingDTO;
import ca.footeware.rest.recipes.model.Recipe;
import ca.footeware.rest.recipes.repository.RecipeRepository;

/**
 * @author Footeware.ca
 *
 */
@RestController
@RequestMapping
public class RecipeController {

	private RecipeRepository repo;

	public RecipeController(RecipeRepository repo) {
		this.repo = repo;
	}

	private List<String> cleanTags(List<String> tags) {
		List<String> tagList = new ArrayList<>();
		String tag;
		for (String orig : tags) {
			tag = orig.trim().toLowerCase();
			if (!tag.isEmpty()) {
				tagList.add(tag);
			}
		}
		return tagList;
	}

	@PostMapping(value = "/recipes", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Recipe> createRecipe(@RequestBody Recipe recipe) {
		Recipe savedRecipe = repo.save(recipe);
		return new ResponseEntity<>(savedRecipe, HttpStatus.OK);
	}

	@DeleteMapping("/recipes/{id}")
	public ResponseEntity<?> deleteRecipe(@PathVariable String id) {
		repo.deleteById(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping(value = "/recipes", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<PagingDTO> getAllRecipes(@RequestParam int pageNumber, @RequestParam int pageSize) {
		Page<Recipe> page = repo.findAll(PageRequest.of(pageNumber, pageSize, Sort.by("name").ascending()));
		PagingDTO dto = new PagingDTO(page.getTotalElements(), page.getContent());
		return new ResponseEntity<>(dto, HttpStatus.OK);
	}

	@GetMapping(value = "/recipes/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Recipe> getRecipeById(@PathVariable String id) {
		Optional<Recipe> optional = repo.findById(id);
		if (optional.isPresent()) {
			return new ResponseEntity<>(optional.get(), HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	@PostMapping(value = "/recipes/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Recipe> updateRecipe(@PathVariable String id, @RequestBody Recipe recipe) {
		Optional<Recipe> optional = repo.findById(id);
		if (optional.isPresent()) {
			Recipe existingRecipe = optional.get();
			existingRecipe.setName(recipe.getName().trim());
			existingRecipe.setBody(recipe.getBody().trim());
			existingRecipe.setTags(cleanTags(recipe.getTags()));
			existingRecipe.setImages(recipe.getImages());
			Recipe savedRecipe = repo.save(existingRecipe);
			return new ResponseEntity<>(savedRecipe, HttpStatus.OK);
		}
		return new ResponseEntity<Recipe>(HttpStatus.BAD_REQUEST);
	}

	@GetMapping(value = "/recipes/search", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<PagingDTO> search(@RequestParam String term, @RequestParam int pageNumber,
			@RequestParam int pageSize) {
		String trimmed = term.trim();
		Page<Recipe> result = repo
				.findByNameContainingIgnoreCaseOrBodyContainingIgnoreCaseOrTagsContainingIgnoreCaseOrderByNameAsc(
						trimmed, trimmed, trimmed, PageRequest.of(pageNumber, pageSize));

		PagingDTO dto = new PagingDTO(result.getTotalElements(), result.getContent());
		return new ResponseEntity<PagingDTO>(dto, HttpStatus.OK);
	}

	@GetMapping(value = "/recipes/search/tags", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<PagingDTO> searchByTag(@RequestParam String tag, @RequestParam int pageNumber,
			@RequestParam int pageSize) {
		Page<Recipe> page = repo.findByTags(tag, PageRequest.of(pageNumber, pageSize));
		PagingDTO dto = new PagingDTO(page.getTotalElements(), page.getContent());
		return new ResponseEntity<>(dto, HttpStatus.OK);
	}

	@GetMapping(value = "/recipes/tags", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<String>> getAllTags() {
		Set<String> allTags = new HashSet<>();
		long rawCount = repo.count();
		Long l = Long.valueOf(rawCount);
		int count = l.intValue();
		Page<Recipe> page;
		List<Recipe> content;
		for (int i = 0; i < count; i++) {
			page = repo.findAll(PageRequest.of(i, 10));
			content = page.getContent();
			for (Recipe recipe : content) {
				allTags.addAll(recipe.getTags());
			}
		}
		List<String> allTagsList = new ArrayList<>(allTags);
		allTagsList.sort((o1, o2) -> o1.compareTo(o2));
		return new ResponseEntity<>(allTagsList, HttpStatus.OK);
	}

	public List<Recipe> getAllRecipes() {
		return repo.findAll();
	}

}
