package ca.footeware.rest.recipes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ca.footeware.rest.recipes.controller.RecipeController;
import ca.footeware.rest.recipes.model.PagingDTO;
import ca.footeware.rest.recipes.model.Recipe;
import ca.footeware.rest.recipes.repository.RecipeRepository;

@SpringBootTest
class ApplicationTests {

	@Autowired
	private RecipeRepository repo;
	private RecipeController controller;

	@BeforeEach
	void beforeEach() {
		controller = new RecipeController(repo);
		clearDB();
	}

	@AfterEach
	void aterEach() {
		clearDB();
	}

	private void clearDB() {
		List<Recipe> recipes = controller.getAllRecipes();
		for (Recipe recipe : recipes) {
			controller.deleteRecipe(recipe.getId());
		}
	}

	@Test
	void testSearch() {
		Recipe recipe = new Recipe("name1", "body1", List.of("tag1"), null);
		controller.createRecipe(recipe);
		PagingDTO result = controller.search("name1", 0, 10).getBody();
		assertEquals(1, result.recipes().size());
		result = controller.search("Bod", 0, 10).getBody();
		assertEquals(1, result.recipes().size());
		result = controller.search(" tag ", 0, 10).getBody();
		assertEquals(1, result.recipes().size());
		List<Recipe> recipes = result.recipes();
		String id = recipes.get(0).getId();
		Recipe recipeById = controller.getRecipeById(id).getBody();
		assertNotNull(recipeById);
		recipeById.setBody("new");
		Recipe updateReciped = controller.updateRecipe(recipe.getId(), recipeById).getBody();
		assertEquals("new", updateReciped.getBody());
		recipe = new Recipe("name2", "body2", List.of("tag2"), null);
		controller.createRecipe(recipe).getBody();
		result = controller.search("name", 0, 10).getBody();
		assertEquals(2, result.recipes().size());
		assertEquals("name1", result.recipes().get(0).getName());
	}

	@Test
	void testGetAllTags() {
		Recipe recipe = new Recipe("name1", "body1", List.of("tag1"), null);
		controller.createRecipe(recipe);
		recipe = new Recipe("name2", "body2", List.of("tag1", "tag2"), null);
		controller.createRecipe(recipe);
		recipe = new Recipe("name3", "body3", List.of("tag1", "tag2"), null);
		controller.createRecipe(recipe);
		recipe = new Recipe("name4", "body4", List.of("tag3"), null);
		controller.createRecipe(recipe);
		List<String> tags = controller.getAllTags().getBody();
		assertEquals(3, tags.size());
	}
	
	@Test
	void searchByTags() {
		Recipe recipe = new Recipe("name1", "body1", List.of("tag1"), null);
		controller.createRecipe(recipe);
		recipe = new Recipe("name2", "body2", List.of("tag1", "tag2"), null);
		controller.createRecipe(recipe);
		PagingDTO dto = controller.searchByTag("tag1", 0, 10).getBody();
		List<Recipe> recipes = dto.recipes();
		assertEquals(2, recipes.size());
	}

	@Test
	void contextLoads() {
	}
}
