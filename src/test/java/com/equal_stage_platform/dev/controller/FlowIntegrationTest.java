package com.equal_stage_platform.dev.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import com.equal_stage_platform.dev.dto.ResponseLectureDTO;
import com.equal_stage_platform.dev.dto.ResponseLecturerDTO;
import com.equal_stage_platform.dev.model.enums.Area;
import com.equal_stage_platform.dev.model.enums.LectureStatus;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Set;
import java.util.UUID;

@SpringBootTest
@AutoConfigureMockMvc
public class FlowIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;



	// Helper to register and login, returns JWT token
	private String registerAndLogin(String email, String password) throws Exception {
		// Register
		mockMvc.perform(post("/api/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"email\":\"" + email + "\", \"password\":\"" + password + "\"}"))
				.andExpect(status().isOk());

		// Login
		MvcResult result = mockMvc.perform(post("/api/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"email\":\"" + email + "\", \"password\":\"" + password + "\"}"))
				.andExpect(status().isOk())
				.andReturn();

		// Extract token from response (adjust field as needed)
		String response = result.getResponse().getContentAsString();
		// Assuming response is {"token":"..."}
		return objectMapper.readTree(response).get("token").asText();
	}

	// Helper to register admin
	private void registerAdmin(String token) throws Exception {
		mockMvc.perform(post("/api/auth/registerAdmin")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	// Helper to attempt admin creation and expect failure
	private void attemptRegisterAdminExpectFail(String token) throws Exception {
		mockMvc.perform(post("/api/auth/registerAdmin")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isConflict());
	}


	// Helper to make admin by admin
	private void makeAdminWrongEmail(String adminToken, String email) throws Exception {
		mockMvc.perform(post("/api/auth/create-admin")
				.header("Authorization", "Bearer " + adminToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"email\":\"" + email + "\"}"))
				.andExpect(status().isForbidden());
	}

	// Helper to make admin by admin
	private void makeAdmin(String adminToken, String email) throws Exception {
		mockMvc.perform(post("/api/auth/create-admin")
				.header("Authorization", "Bearer " + adminToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"email\":\"" + email + "\"}"))
				.andExpect(status().isOk());
	}

	// Helper to create lecturer
	private void createLecturer(String token, String firstName, String lastName, String bio, String city, String email, String phone, String imageUrl, Area workingArea) throws Exception {
		mockMvc.perform(post("/lecturers/create")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"firstName\":\"" + firstName + "\", " +
				"\"lastName\":\"" + lastName + "\", " +
				"\"bio\":\"" + bio + "\", " +
				"\"city\":\"" + city + "\", " +
				"\"email\":\"" + email + "\", " +
				"\"phone\":\"" + phone + "\", " +
				"\"imageUrl\":\"" + imageUrl + "\", " +
				"\"workingArea\":\"" + workingArea.name() + "\"}"))
				.andExpect(status().isOk());
	}

	// Helper to create lecture
	private void createLectureBeforeApproval(String token, String title, String description, Integer duration, Integer price, LectureStatus lectureStatus, boolean isOnline, String imageUrl) throws Exception {
		mockMvc.perform(post("/lectures/create")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"title\":\"" + title + "\", " +
						 "\"description\":\"" + description + "\", " +
						 "\"duration\":" + duration + ", " +
						 "\"price\":" + price + ", " +
						 "\"lectureStatus\":\"" + lectureStatus + "\", " +
						 "\"isOnline\":" + isOnline + ", " +
						 "\"imageUrl\":\"" + imageUrl + "\"}"))
				.andExpect(status().isConflict());
	}

	// Helper to create lecture
	private void createLecture(String token, String title, String description, Integer duration, Integer price, LectureStatus lectureStatus, boolean isOnline, String imageUrl) throws Exception {
		mockMvc.perform(post("/lectures/create")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"title\":\"" + title + "\", " +
						 "\"description\":\"" + description + "\", " +
						 "\"duration\":" + duration + ", " +
						 "\"price\":" + price + ", " +
						 "\"lectureStatus\":\"" + lectureStatus + "\", " +
						 "\"isOnline\":" + isOnline + ", " +
						 "\"imageUrl\":\"" + imageUrl + "\"}"))
				.andExpect(status().isOk());
	}

	// Helper to get pending lecturers
	private Set<ResponseLecturerDTO> getPendingLecturers(String token) throws Exception {
		String response = mockMvc.perform(get("/lecturers/pending")
			.header("Authorization", "Bearer " + token))
			.andExpect(status().isOk())
			.andReturn()
			.getResponse()
			.getContentAsString();
		
		return objectMapper.readValue(response, 
			objectMapper.getTypeFactory().constructCollectionType(Set.class, ResponseLecturerDTO.class));
	}

	// Helper to approve lecturer
	private void approveLecturer(String token, UUID lecturerId) throws Exception {
		mockMvc.perform(post("/lecturers/approve/" + lecturerId)
			.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk());
	}

	private void searchLectureByTitle(String title) throws Exception {
		String response = mockMvc.perform(get("/lectures/search/" + title))
			.andExpect(status().isOk())
			.andReturn()
			.getResponse()
			.getContentAsString();
	
		ResponseLectureDTO dto = objectMapper.readValue(response, ResponseLectureDTO.class);
	
		// Assuming ResponseLectureDTO has a getLecture() method that returns a Lecture object
		assertEquals(title, dto.getTitle());
	}

	@Test
	public void testFullFlow() throws Exception {
		// 1. user1 registers
		String user1Token = registerAndLogin("user1@example.com", "Password!1234");

		// 2. user1 registers as admin
		registerAdmin(user1Token);

		// 3. user2 registers
		String user2Token = registerAndLogin("user2@example.com", "Password!4321");

		// 4. user2 tries to make himself admin (should fail)
		attemptRegisterAdminExpectFail(user2Token);

		// 5. user1 makes user2 admin
		makeAdminWrongEmail(user1Token, "user2");

		// 6. user1 makes user2 admin
		makeAdmin(user1Token, "user2@example.com");

		// 7. user1 creates a lecturer
		createLecturer(user1Token, "John", "Doe", "I am a lecturer", "New York", "john.doe@example.com", "1234567890", "https://example.com/image.jpg", Area.CENTER);

		// 7.1 user1 creates a lecture before approval
		createLectureBeforeApproval(user1Token, "Lecture1_user1", "Description of Lecture1_user1", 60, 100, LectureStatus.ON_AIR, true, "https://example.com/image3.jpg");
		
		// 7.2 Admin approves the lecturer
		Set<ResponseLecturerDTO> lecturers = getPendingLecturers(user1Token);
		approveLecturer(user1Token, lecturers.iterator().next().getUserId());

		// 8. user1 creates 3 lectures
		for (int i = 1; i <= 3; i++) {
			createLecture(user1Token, "Lecture" + i + "_user1", "Description of Lecture" + i + "_user1", 60, 100, LectureStatus.ON_AIR, i%2==0, "https://example.com/image"+i*100+".jpg");
		}

		// 9. user2 creates a lecturer
		createLecturer(user2Token, "Jane", "Smith", "I am a lecturer", "Los Angeles", "jane.smith@example.com", "0987654321", "https://example.com/image2.jpg", Area.NORTH);

		// 9.1 user2 creates a lecture before approval
		createLectureBeforeApproval(user2Token, "Lecture1_user2", "Description of Lecture1_user2", 60, 100, LectureStatus.ON_AIR, true, "https://example.com/image4.jpg");

		// 9.2 Admin approves the lecturer
		Set<ResponseLecturerDTO> lecturers2 = getPendingLecturers(user2Token);
		approveLecturer(user2Token, lecturers2.iterator().next().getUserId());

		// 10. user2 creates 3 lectures after approval
		for (int i = 1; i <= 3; i++) {
			createLecture(user2Token, "Lecture" + i + "_user2", "Description of Lecture" + i + "_user2", 60, 100, LectureStatus.ON_AIR, i%2==0, "https://example.com/image"+i*500+".jpg");
		}

		searchLectureByTitle("Lecture2_user2");
	}
}
