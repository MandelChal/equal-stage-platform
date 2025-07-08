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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

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
		String response = mockMvc.perform(get("/lecturers/admin/pending")
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
		mockMvc.perform(post("/lecturers/admin/approve/" + lecturerId)
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
	
		// Assuming ResponseLectureDTO has a getTitle() method that returns the lecture title
		assertEquals(title, dto.getTitle());
	}

	// Add helper for updating lecturer status
	private void updateLecturerStatus(String token, String status, boolean isAdmin, UUID lecturerId) throws Exception {
		if (isAdmin) {
			mockMvc.perform(patch("/lecturers/admin/" + lecturerId + "/status/" + status)
					.header("Authorization", "Bearer " + token))
					.andExpect(status().isOk());
		} else {
			mockMvc.perform(patch("/lecturers/update/status/" + status)
					.header("Authorization", "Bearer " + token))
					.andExpect(status().isOk());
		}
	}

	// Add helper for updating lecturer status expecting failure
	private void updateLecturerStatusExpectFail(String token, String status) throws Exception {
		mockMvc.perform(patch("/lecturers/update/status/" + status)
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isBadRequest());
	}

	// Add helper for getting a specific lecture by lecturer id
	private void getLectureByLecturerId(String token, UUID lecturerId, int lectureNum, int expectedStatus) throws Exception {
		mockMvc.perform(get("/lecturers/lectures/" + lecturerId + "/" + lectureNum)
				.header("Authorization", "Bearer " + token))
				.andExpect(status().is(expectedStatus));
	}

	// Add helper for getting all lectures by lecturer id
	private void getLecturesByLecturerId(String token, UUID lecturerId, int expectedStatus) throws Exception {
		mockMvc.perform(get("/lecturers/lectures/" + lecturerId + "/all")
				.header("Authorization", "Bearer " + token))
				.andExpect(status().is(expectedStatus));
	}

	// Add helper for searching lecturer by id
	private void searchLecturerById(String token, UUID lecturerId, int expectedStatus) throws Exception {
		mockMvc.perform(get("/lecturers/search/id/" + lecturerId)
				.header("Authorization", "Bearer " + token))
				.andExpect(status().is(expectedStatus));
	}

	// Add helper for searching lecturer by name
	private void searchLecturerByName(String token, String name, int expectedStatus) throws Exception {
		mockMvc.perform(get("/lecturers/search/name/" + name)
				.header("Authorization", "Bearer " + token))
				.andExpect(status().is(expectedStatus));
	}

	// Add helper for deleting lecturer by admin
	private void deleteLecturerByAdmin(String adminToken, UUID lecturerId) throws Exception {
		mockMvc.perform(delete("/lecturers/admin/del/" + lecturerId)
				.header("Authorization", "Bearer " + adminToken))
				.andExpect(status().isOk());
	}

	// Add helper for deleting lecturer by self
	private void deleteLecturerBySelf(String token) throws Exception {
		mockMvc.perform(delete("/lecturers/del/self")
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk());
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

		// === Begin user3 flow ===
		// 1. user3 registers
		String user3Token = registerAndLogin("user3@example.com", "Password!5678");

		// 2. user3 creates lecturer
		createLecturer(user3Token, "Alice", "Wonder", "I am user3", "Chicago", "alice.wonder@example.com", "5551234567", "https://example.com/image5.jpg", Area.SOUTH);

		// 3. user1 approves pending lecturers (user3)
		Set<ResponseLecturerDTO> lecturers3 = getPendingLecturers(user1Token);
		UUID user3LecturerId = lecturers3.iterator().next().getUserId();
		approveLecturer(user1Token, user3LecturerId);

		// 4. user3 creates 3 lectures
		for (int i = 1; i <= 3; i++) {
			createLecture(user3Token, "Lecture" + i + "_user3", "Description of Lecture" + i + "_user3", 60, 100, LectureStatus.ON_AIR, i%2==0, "https://example.com/image"+i*700+".jpg");
		}

		// 5. user3 tries to update status to PENDING and gets rejected
		updateLecturerStatusExpectFail(user3Token, "PENDING");

		// 6. user3 updates his status to FREEZE
		updateLecturerStatus(user3Token, "FREEZE", false, null);

		// 7. user2 tries to get a specific lecture of user3 lecturer and gets rejected
		getLectureByLecturerId(user2Token, user3LecturerId, 1, 404);

		// 8. user2 tries to get lectures of user3 lecturer and gets rejected
		getLecturesByLecturerId(user2Token, user3LecturerId, 404);

		// 9. user2 tries to search user3 by its id and gets rejected
		searchLecturerById(user2Token, user3LecturerId, 404);

		// 10. user2 tries to search lecturer of user3 by name and gets rejected
		searchLecturerByName(user2Token, "Alice", 404);

		// 11. user1 updates user3 status to Approved
		updateLecturerStatus(user3Token, "APPROVED", false, null);

		// 12. user2 tries to search lecturer of user3 by its id and succeeds
		searchLecturerById(user2Token, user3LecturerId, 200);

		// 13. user2 tries to search lecturer of user3 by name and succeeds
		searchLecturerByName(user2Token, "Alice", 200);

		// 14. user2 tries to get a specific lecture of user3 lecturer and succeeds
		getLectureByLecturerId(user2Token, user3LecturerId, 7, 200);

		// 15. user2 tries to get lectures of user3 lecturer and succeeds
		getLecturesByLecturerId(user2Token, user3LecturerId, 200);

		// 16. user3 deletes his own lecturer profile
		deleteLecturerBySelf(user3Token);

		// 17. user3 creates a new lecturer
		createLecturer(user3Token, "Alice", "Wonder", "I am user3 again", "Chicago", "alice.wonder2@example.com", "5551234568", "https://example.com/image6.jpg", Area.SOUTH);

		// 18. user1 deletes user3 lecturer profile
		// Get the new lecturer id
		Set<ResponseLecturerDTO> newLecturers3 = getPendingLecturers(user1Token);
		UUID newUser3LecturerId = newLecturers3.iterator().next().getUserId();
		approveLecturer(user1Token, newUser3LecturerId);
		deleteLecturerByAdmin(user1Token, newUser3LecturerId);
	}
}
