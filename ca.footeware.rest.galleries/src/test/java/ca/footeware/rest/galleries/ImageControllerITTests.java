/**
 *
 */
package ca.footeware.rest.galleries;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import ca.footeware.rest.galleries.controllers.ImageController;
import jakarta.servlet.ServletException;

/**
 * Test {@link ImageController}.
 *
 * @author Footeware.ca
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class ImageControllerITTests {

	@Autowired
	private MockMvc mvc;
	@LocalServerPort
	private int port;

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
	 *
	 */
	@Test
	void testGetGallery() throws Exception {
		MvcResult mvcResult = mvc.perform(get("/galleries/gallery1")).andExpect(status().isOk())
				.andExpect(result -> result.getResponse()).andReturn();
		String string = mvcResult.getResponse().getContentAsString();
		Assertions.assertTrue(string.contains("David in Ferryland-1_modified.jpg"));
	}

	@Test
	void testGetImage() throws Exception {
		MvcResult mvcResult = mvc.perform(get("/galleries/gallery1/test-image-horizontal.png"))
				.andExpect(status().isOk()).andReturn();
		byte[] bytes = mvcResult.getResponse().getContentAsByteArray();
		InputStream is = new ByteArrayInputStream(bytes);
		BufferedImage image = ImageIO.read(is);
		Assertions.assertEquals(1920, image.getWidth(), "Image wrong width.");
		Assertions.assertEquals(1241, image.getHeight(), "Image wrong height.");

		mvcResult = mvc.perform(get("/galleries/gallery1/test-image-vertical.png")).andExpect(status().isOk())
				.andReturn();
		bytes = mvcResult.getResponse().getContentAsByteArray();
		image = ImageIO.read(new ByteArrayInputStream(bytes));
		Assertions.assertEquals(1241, image.getWidth(), "Image wrong width.");
		Assertions.assertEquals(1920, image.getHeight(), "Image wrong height.");

		mvcResult = mvc.perform(get("/galleries/gallery1/test-image-square.png")).andExpect(status().isOk())
				.andReturn();
		bytes = mvcResult.getResponse().getContentAsByteArray();
		image = ImageIO.read(new ByteArrayInputStream(bytes));
		Assertions.assertEquals(1920, image.getWidth(), "Image wrong width.");
		Assertions.assertEquals(1920, image.getHeight(), "Image wrong height.");
	}

	@Test
	void badImageName() throws Exception {
		ServletException thrown = Assertions.assertThrows(ServletException.class, () -> {
			mvc.perform(get("/galleries/gallery1/test-image-bad.png"));
		}, "ServletException was expected");
		Assertions.assertEquals("gallery1/test-image-bad.png not found.", thrown.getCause().getMessage());
	}

	/**
	 * Test method for
	 * {@link ImageController#getThumbnail(java.lang.String, java.lang.String)}.
	 * @throws Exception 
	 */
	@Test
	void testGetThumbnail() throws Exception {
		MvcResult result = mvc.perform(get("/galleries/thumbnails/gallery1/test-image-horizontal.png")).andReturn();
		byte[] bytes = result.getResponse().getContentAsByteArray();
		Assertions.assertEquals(2781, bytes.length, "Wrong number of bytes for thumbnail.");
		BufferedImage image = ImageIO.read(new ByteArrayInputStream(bytes));
		Assertions.assertEquals(150, image.getWidth(), "Image wrong width.");
		Assertions.assertEquals(97, image.getHeight(), "Image wrong height.");

		result = mvc.perform(get("/galleries/thumbnails/gallery1/test-image-vertical.png")).andReturn();
		bytes = result.getResponse().getContentAsByteArray();
		Assertions.assertEquals(1395, bytes.length, "Wrong number of bytes for thumbnail.");
		image = ImageIO.read(new ByteArrayInputStream(bytes));
		Assertions.assertEquals(97, image.getWidth(), "Image wrong width.");
		Assertions.assertEquals(150, image.getHeight(), "Image wrong height.");

		result = mvc.perform(get("/galleries/thumbnails/gallery1/test-image-square.png")).andReturn();
		bytes = result.getResponse().getContentAsByteArray();
		Assertions.assertEquals(1617, bytes.length, "Wrong number of bytes for thumbnail.");
		image = ImageIO.read(new ByteArrayInputStream(bytes));
		Assertions.assertEquals(150, image.getWidth(), "Image wrong width.");
		Assertions.assertEquals(150, image.getHeight(), "Image wrong height.");

		ServletException thrown = Assertions.assertThrows(ServletException.class, () -> {
			mvc.perform(get("/galleries/gallery1/test-image-bad.png"));
		}, "ServletException was expected");
		Assertions.assertEquals("gallery1/test-image-bad.png not found.", thrown.getCause().getMessage());
	}

}
