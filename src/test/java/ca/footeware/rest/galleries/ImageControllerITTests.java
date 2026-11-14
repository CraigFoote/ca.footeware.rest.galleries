/**
 *
 */
package ca.footeware.rest.galleries;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;
import java.util.Map;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.jayway.jsonpath.JsonPath;

import ca.footeware.rest.galleries.controllers.ImageController;
import ca.footeware.rest.galleries.exceptions.ImageException;
import ca.footeware.rest.galleries.services.ImageService;

/**
 * Test {@link ImageController}.
 *
 * @author Footeware.ca
 */
@SpringBootTest
class ImageControllerITTests {

	private MockMvc mockMvc;

	@Value("${images.path}")
	private String imagesPath;

	@Value("${images.max.dimension}")
	private int maxImgDim;

	@Value("${images.thumbnails.max.dimension}")
	private int maxTnDim;

	@BeforeEach
	void setup() {
		try {
			mockMvc = standaloneSetup(new ImageController(new ImageService(imagesPath, maxImgDim, maxTnDim))).build();
		} catch (ImageException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Test method for
	 * {@link ca.footeware.web.controllers.ImageController#getGalleries(org.springframework.ui.Model)}.
	 * 
	 * @throws Exception
	 */
	@Test
	void testGetGalleries() throws Exception {
		mockMvc.perform(get("/galleries")).andExpect(status().isOk())
				.andExpect(content().contentType("application/json")).andExpect(content().encoding(UTF_8))
				.andExpect(content().string(containsString("[{\"name\":\"gallery1\",\"secret\":false}]")));
	}

	/**
	 * Test method for
	 * {@link ca.footeware.web.controllers.ImageController#getGallery(java.lang.String, org.springframework.ui.Model)}.
	 *
	 * @throws Exception if shit goes south
	 */
	@Test
	void testGetGallery() throws Exception {
		MvcResult mvcResult = mockMvc.perform(get("/galleries/gallery1")).andExpect(status().isOk())
				.andExpect(content().contentType("application/json")).andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$.length()", is(5))).andExpect(result -> result.getResponse()).andReturn();

		String json = mvcResult.getResponse().getContentAsString();
		assertTrue(json.contains("David in Ferryland-1_modified.jpg"));

		JsonPath path = JsonPath.compile("$[1]");
		Map<String, String> map = path.read(json);
		assertEquals("David in Ferryland-1_modified.jpg", map.get("filename"));
		String encoded = map.get("thumb");
		byte[] bytes = Base64.getDecoder().decode(encoded);
		InputStream is = new ByteArrayInputStream(bytes);
		BufferedImage image = ImageIO.read(is);
		assertEquals(150, image.getWidth(), "Image wrong width.");
		assertEquals(84, image.getHeight(), "Image wrong height.");
	}

	@Test
	void testGetImage() throws Exception {
		MvcResult mvcResult = mockMvc.perform(get("/galleries/gallery1/test-image-horizontal.png"))
				.andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.IMAGE_JPEG))
				.andExpect(result -> result.getResponse()).andReturn();

		byte[] bytes = mvcResult.getResponse().getContentAsByteArray();
		InputStream is = new ByteArrayInputStream(bytes);
		BufferedImage image = ImageIO.read(is);
		assertEquals(1920, image.getWidth(), "Image wrong width.");
		assertEquals(1241, image.getHeight(), "Image wrong height.");
	}
}
