/**
 *
 */
package ca.footeware.rest.galleries;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;
import java.util.Map;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.jayway.jsonpath.JsonPath;

import ca.footeware.rest.galleries.controllers.ImageController;

/**
 * Test {@link ImageController}.
 *
 * @author Footeware.ca
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@EnableWebMvc
class ImageControllerITTests {

	@Autowired
	private MockMvc mvc;

	/**
	 * Test method for
	 * {@link ca.footeware.web.controllers.ImageController#getGalleries(org.springframework.ui.Model)}.
	 * 
	 * @throws Exception
	 */
	@Test
	void testGetGalleries() throws Exception {
		MvcResult result = mvc.perform(get("/galleries")).andExpect(status().isOk()).andExpect(jsonPath("$").exists())
				.andExpect(jsonPath("$").isArray()).andExpect(jsonPath("$.length()", is(1))).andReturn();
		result.getResponse().getContentAsString().contains(
				"{\"folder\":\"/src/test/resources/images/gallery1\",\"name\":\"gallery1\",\"secret\":false}");
	}

	/**
	 * Test method for
	 * {@link ca.footeware.web.controllers.ImageController#getGallery(java.lang.String, org.springframework.ui.Model)}.
	 *
	 * @throws Exception if shit goes south
	 */
	@Test
	void testGetGallery() throws Exception {
		MvcResult mvcResult = mvc.perform(get("/galleries/gallery1")).andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$").isArray()).andExpect(jsonPath("$.length()", is(5)))
				.andExpect(result -> result.getResponse()).andReturn();
		String json = mvcResult.getResponse().getContentAsString();
		Assertions.assertTrue(json.contains("David in Ferryland-1_modified.jpg"));

		JsonPath path = JsonPath.compile("$[1]");
		Map<String, String> map = path.read(json);
		Assertions.assertEquals("David in Ferryland-1_modified.jpg", map.get("filename"));
		String encoded = map.get("thumb");
		byte[] bytes = Base64.getDecoder().decode(encoded);
		InputStream is = new ByteArrayInputStream(bytes);
		BufferedImage image = ImageIO.read(is);
		Assertions.assertEquals(150, image.getWidth(), "Image wrong width.");
		Assertions.assertEquals(84, image.getHeight(), "Image wrong height.");
	}

	@Test
	void testGetImage() throws Exception {
		MvcResult mvcResult = mvc.perform(get("/galleries/gallery1/test-image-horizontal.png"))
				.andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.IMAGE_JPEG))
				.andExpect(result -> result.getResponse()).andReturn();
		byte[] bytes = mvcResult.getResponse().getContentAsByteArray();
		InputStream is = new ByteArrayInputStream(bytes);
		BufferedImage image = ImageIO.read(is);
		Assertions.assertEquals(1920, image.getWidth(), "Image wrong width.");
		Assertions.assertEquals(1241, image.getHeight(), "Image wrong height.");
	}
}
