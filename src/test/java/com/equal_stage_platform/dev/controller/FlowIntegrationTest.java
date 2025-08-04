package com.equal_stage_platform.dev.controller;

import com.fasterxml.jackson.core.type.TypeReference;
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
import com.equal_stage_platform.dev.model.Topic;
import com.equal_stage_platform.dev.model.TargetAudience;
import com.equal_stage_platform.dev.model.enums.Area;
import com.equal_stage_platform.dev.model.enums.LectureStatus;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@SpringBootTest
@AutoConfigureMockMvc
public class FlowIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	private Long testTopicId;
	private Long testTargetAudienceId;

	// Helper to register and login, returns JWT token
	private String registerAndLogin(String email, String password) throws Exception {
		// Register
		mockMvc.perform(post("/api/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"email\":\"" + email + "\", \"password\":\"" + password + "\"}"))
				.andExpect(status().isCreated());

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
	private void registerAdmin(String token, int expectedStatus) throws Exception {
		mockMvc.perform(post("/api/auth/registerAdmin")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().is(expectedStatus));
	}

	// Helper to make admin by admin
	private void makeAdmin(String adminToken, String email, int expectedStatus) throws Exception {
		mockMvc.perform(post("/api/auth/admin/create-admin")
				.header("Authorization", "Bearer " + adminToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"email\":\"" + email + "\"}"))
				.andExpect(status().is(expectedStatus));
	}

	// Helper to create lecturer
	private void createLecturer(String token, String firstName, String lastName, String bio, String city, String email, String phone, String imageUrl, Set<Area> workingAreas, int expectedStatus) throws Exception {
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
				"\"workingAreas\":[\"" + workingAreas.iterator().next().name() + "\"], " +
				"\"externalLinks\":[{\"url\":\"https://example.com\",\"description\":\"Test external link\"}], " +
				"\"videoLinks\":[{\"url\":\"https://youtube.com/test\",\"description\":\"Test video link\"}]}"))
				.andExpect(status().is(expectedStatus));
	}


	// Helper to create lecture
	private void createLecture(String token, String title, String description, Integer duration, Integer price, LectureStatus lectureStatus, boolean isOnline, String imageUrl, int expectedStatus) throws Exception {
		mockMvc.perform(post("/lectures/create")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"title\":\"" + title + "\", " +
						 "\"description\":\"" + description + "\", " +
						 "\"duration\":" + duration + ", " +
						 "\"price\":" + price + ", " +
						 "\"lectureStatus\":\"" + lectureStatus + "\", " +
						 "\"online\":" + isOnline + ", " +
						 "\"imageUrl\":\"" + imageUrl + "\", " +
						 "\"externalLinks\":[{\"url\":\"https://example.com\",\"description\":\"Test external link\"}], " +
						 "\"videoLinks\":[{\"url\":\"https://youtube.com/test\",\"description\":\"Test video link\"}], " +
						 "\"topicsIds\":[" + testTopicId + "], " +
						 "\"targetAudiencesIds\":[" + testTargetAudienceId + "]}"))
				.andExpect(status().is(expectedStatus));
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
	private void approveLecturer(String token, UUID lecturerId, int expectedStatus) throws Exception {
		mockMvc.perform(post("/lecturers/admin/approve/" + lecturerId)
			.header("Authorization", "Bearer " + token))
				.andExpect(status().is(expectedStatus));
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
	private void updateLecturerStatus(String token, String status, boolean isAdmin, UUID lecturerId, int expectedStatus) throws Exception {
		if (isAdmin) {
			mockMvc.perform(patch("/lecturers/admin/" + lecturerId + "/status/" + status)
					.header("Authorization", "Bearer " + token))
					.andExpect(status().is(expectedStatus));
		} else {
			mockMvc.perform(patch("/lecturers/update/status/" + status)
					.header("Authorization", "Bearer " + token))
					.andExpect(status().is(expectedStatus));
		}
	}

	// Add helper for updating lecturer status expecting failure
	private void updateLecturerStatusExpect(String token, String status, int expectedStatus) throws Exception {
		mockMvc.perform(patch("/lecturers/update/status/" + status)
				.header("Authorization", "Bearer " + token))
				.andExpect(status().is(expectedStatus));
	}

	// Add helper for getting a specific lecture by lecturer id
	private void getLectureByLecturerId(String token, UUID lecturerId, int lectureNum, int expectedStatus) throws Exception {
		mockMvc.perform(get("/lecturers/" + lecturerId + "/lectures/" + lectureNum)
				.header("Authorization", "Bearer " + token))
				.andExpect(status().is(expectedStatus));
	}

	// Add helper for getting all lectures by lecturer id
	private void getLecturesByLecturerId(String token, UUID lecturerId, int expectedStatus) throws Exception {
		mockMvc.perform(get("/lecturers/" + lecturerId + "/lectures/all")
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
	private void deleteLecturerByAdmin(String adminToken, UUID lecturerId, int expectedStatus) throws Exception {
		mockMvc.perform(delete("/lecturers/admin/del/" + lecturerId)
				.header("Authorization", "Bearer " + adminToken))
				.andExpect(status().is(expectedStatus));
	}

	// Add helper for deleting lecturer by self
	private void deleteLecturerBySelf(String token, int expectedStatus) throws Exception {
		mockMvc.perform(delete("/lecturers/del/self")
				.header("Authorization", "Bearer " + token))
				.andExpect(status().is(expectedStatus));
	}

	private void approveLecture(String token, Long lectureId, int expectedStatus) throws Exception {
		mockMvc.perform(patch("/lectures/admin/approve/" + lectureId)
				.header("Authorization", "Bearer " + token))
				.andExpect(status().is(expectedStatus));
	}

	private Set<ResponseLectureDTO> getPendingLectures(String token) throws Exception {
		String response = mockMvc.perform(get("/lectures/admin/pending")
			.header("Authorization", "Bearer " + token))
			.andExpect(status().isOk())
			.andReturn()
			.getResponse()
			.getContentAsString();
		
		return objectMapper.readValue(response, 
			objectMapper.getTypeFactory().constructCollectionType(Set.class, ResponseLectureDTO.class));
	}

	private void setupTopicsAndTargetAudiences(String adminToken) throws Exception {
		// Create a test topic (requires admin authentication)
		MvcResult topicResult = mockMvc.perform(post("/topics/admin/create")
				.header("Authorization", "Bearer " + adminToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"name\":\"Test Topic\", \"description\":\"A test topic for integration tests\"}"))
				.andExpect(status().isCreated())
				.andReturn();
		
		Topic topic = objectMapper.readValue(topicResult.getResponse().getContentAsString(), Topic.class);
		testTopicId = topic.getTopicId();
		
		// Create a test target audience (requires admin authentication)
		MvcResult targetAudienceResult = mockMvc.perform(post("/target-audiences/admin/create")
				.header("Authorization", "Bearer " + adminToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"type\":\"Test Audience\", \"description\":\"A test target audience for integration tests\"}"))
				.andExpect(status().isCreated())
				.andReturn();
		
		TargetAudience targetAudience = objectMapper.readValue(targetAudienceResult.getResponse().getContentAsString(), TargetAudience.class);
		testTargetAudienceId = targetAudience.getTargetAudienceId();
	}

	@Test
	public void testFullFlow() throws Exception {
		// 1. user1 registers
		String user1Token = registerAndLogin("user1@example.com", "Password!1234");

		// 2. user1 registers as admin
		registerAdmin(user1Token, 200);
		
		// 0. Setup topics and target audiences first (requires admin)
		setupTopicsAndTargetAudiences(user1Token);

		// 3. user2 registers
		String user2Token = registerAndLogin("user2@example.com", "Password!4321");

		// 4. user2 tries to make himself admin (should fail)
		registerAdmin(user2Token, 403);

		// 5. user1 makes user2 admin (wrong email, should fail)
		makeAdmin(user1Token, "user2", 400);

		// 6. user1 makes user2 admin (correct email)
		makeAdmin(user1Token, "user2@example.com", 200);

		// 7. user1 creates a lecturer
		createLecturer(user1Token, "John", "Doe", "I am a lecturer", "New York", "john.doe@example.com", "0542354687", "https://example.com/image.jpg", Set.of(Area.CENTER), 201);

		// 7.1 user1 creates a lecture before approval (should fail, assuming 403)
		createLecture(user1Token, "Lecture1_user1", "Description of Lecture1_user1", 60, 100, LectureStatus.ON_AIR, true, "https://example.com/image3.jpg", 403);

		// 7.2 Admin approves the lecturer
		Set<ResponseLecturerDTO> lecturers = getPendingLecturers(user1Token);
		approveLecturer(user1Token, lecturers.iterator().next().getUserId(), 200);

		// 8. user1 creates 3 lectures
		for (int i = 1; i <= 3; i++) {
			createLecture(user1Token, "Lecture" + i + "_user1", "Description of Lecture" + i + "_user1", 60, 100, LectureStatus.ON_AIR, i%2==0, "https://example.com/image"+i*100+".jpg", 201);
		}

		// 9. user2 creates a lecturer
		createLecturer(user2Token, "Jane", "Smith", "I am a lecturer", "Los Angeles", "jane.smith@example.com", "0598654321", "https://example.com/image2.jpg", Set.of(Area.NORTH), 201);

		// 9.1 user2 creates a lecture before approval (should fail, assuming 403)
		createLecture(user2Token, "Lecture1_user2", "Description of Lecture1_user2", 60, 100, LectureStatus.ON_AIR, true, "https://example.com/image4.jpg", 403);

		// 9.2 Admin approves the lecturer
		Set<ResponseLecturerDTO> lecturers2 = getPendingLecturers(user2Token);
		approveLecturer(user2Token, lecturers2.iterator().next().getUserId(), 200);

		// 10. user2 creates 3 lectures after approval
		for (int i = 1; i <= 3; i++) {
			createLecture(user2Token, "Lecture" + i + "_user2", "Description of Lecture" + i + "_user2", 60, 100, LectureStatus.ON_AIR, i%2==0, "https://example.com/image"+i*500+".jpg", 201);
		}

		// 11. Admin approves all lectures
		Set<ResponseLectureDTO> pendingLectures = getPendingLectures(user1Token);
		for (ResponseLectureDTO lecture : pendingLectures) {
			approveLecture(user1Token, lecture.getLectureId(), 200);
		}

		searchLectureByTitle("Lecture2_user2");

		// === Begin user3 flow ===
		// 1. user3 registers
		String user3Token = registerAndLogin("user3@example.com", "Password!5678");

		// 2. user3 creates lecturer
		createLecturer(user3Token, "Alice", "Wonder", "I am user3", "Chicago", "alice.wonder@example.com", "0555123457", "https://example.com/image5.jpg", Set.of(Area.SOUTH), 201);

		// 3. user1 approves pending lecturers (user3)
		Set<ResponseLecturerDTO> lecturers3 = getPendingLecturers(user1Token);
		UUID user3LecturerId = lecturers3.iterator().next().getUserId();
		approveLecturer(user1Token, user3LecturerId, 200);

		// 4. user3 creates 3 lectures
		for (int i = 1; i <= 3; i++) {
			createLecture(user3Token, "Lecture" + i + "_user3", "Description of Lecture" + i + "_user3", 60, 100, LectureStatus.ON_AIR, i%2==0, "https://example.com/image"+i*700+".jpg", 201);
		}

		// 5. user3 tries to update status to PENDING and gets rejected
		updateLecturerStatusExpect(user3Token, "PENDING", 400);

		// 6. user3 updates his status to FREEZE
		updateLecturerStatus(user3Token, "FREEZE", false, null, 200);

		// 7. user2 tries to get a specific lecture of user3 lecturer and gets rejected
		getLectureByLecturerId(user2Token, user3LecturerId, 1, 404);

		// 8. user2 tries to get lectures of user3 lecturer and gets rejected
		getLecturesByLecturerId(user2Token, user3LecturerId, 404);

		// 9. user2 tries to search user3 by its id and gets rejected
		searchLecturerById(user2Token, user3LecturerId, 404);

		// 10. user2 tries to search lecturer of user3 by name and gets rejected
		searchLecturerByName(user2Token, "Alice", 404);

		// 11. user3 updates status to Approved
		updateLecturerStatus(user3Token, "APPROVED", false, null, 200);

		// 12. user2 tries to search lecturer of user3 by its id and succeeds
		searchLecturerById(user2Token, user3LecturerId, 200);

		// 13. user2 tries to search lecturer of user3 by name and succeeds
		searchLecturerByName(user2Token, "Alice", 200);

		// 14. user2 tries to get a specific lecture of user3 lecturer and succeeds
		getLectureByLecturerId(user2Token, user3LecturerId, 7, 200);

		// 15. user2 tries to get lectures of user3 lecturer and succeeds
		getLecturesByLecturerId(user2Token, user3LecturerId, 200);

		// 16. user3 deletes his own lecturer profile
		deleteLecturerBySelf(user3Token, 200);

		// 17. user3 creates a new lecturer
		createLecturer(user3Token, "Alice", "Wonder", "I am user3 again", "Chicago", "alice.wonder2@example.com", "0555123457", "https://example.com/image6.jpg", Set.of(Area.SOUTH), 201);

		// 18. user1 deletes user3 lecturer profile
		// Get the new lecturer id
		Set<ResponseLecturerDTO> newLecturers3 = getPendingLecturers(user1Token);
		UUID newUser3LecturerId = newLecturers3.iterator().next().getUserId();
		approveLecturer(user1Token, newUser3LecturerId, 200);
		deleteLecturerByAdmin(user1Token, newUser3LecturerId, 200);
	}

	@Test
	public void testFakerSystemAndFiltering() throws Exception {
		// Initialize faker system with 10 lecturers and 3 lectures per lecturer
		int lecturersCount = 10;
		int lecturesPerLecturer = 3;
		MvcResult fakerResult = mockMvc.perform(post("/faker/initSystem")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"lecturersCount\":\"" + lecturersCount + "\", \"lecturesPerLecturer\":" + lecturesPerLecturer + "}"))
				.andExpect(status().isOk())
				.andReturn();

		// Verify faker system initialized successfully
		String fakerResponse = fakerResult.getResponse().getContentAsString();
		// The response should contain users and possibly failedUsers
		assertEquals(true, fakerResponse.contains("users"));

		MvcResult lecturesResult = mockMvc.perform(get("/lectures/topLectures/"+lecturersCount*lecturesPerLecturer))
				.andExpect(status().isOk())
				.andReturn();

		String lecturesResponse = lecturesResult.getResponse().getContentAsString();
		List<ResponseLectureDTO> lectures = objectMapper.readValue(lecturesResponse,
			new TypeReference<List<ResponseLectureDTO>>() {});
		

		// Test paginated filter for lectures
		testRegularFilterEndpoints(lectures);

		// Test paginated filter for lecturers  
		testRegularFilterLecturers(lectures);

	}

	private void testRegularFilterEndpoints(List<ResponseLectureDTO> allLectures) throws Exception {
		// Instead test regular lectures filter
		MvcResult res = mockMvc.perform(get("/lectures/filter")
				.param("priceMin", "100")
				.param("priceMax", "200"))
				.andExpect(status().isOk())
				.andReturn();

		//convert result json to ResponseLectureDTO
		String response = res.getResponse().getContentAsString();
		List<ResponseLectureDTO> lectures = objectMapper.readValue(response,
			new TypeReference<List<ResponseLectureDTO>>() {});

		int expectedSize = (int) allLectures.stream()
				.filter(lecture -> lecture.getPrice() >= 100 && lecture.getPrice() <= 200)
				.count();

		assertEquals(expectedSize, lectures.size(), "Filtered lectures count should match expected size");

	}

	private void testRegularFilterLecturers(List<ResponseLectureDTO> allLectures) throws Exception {
		MvcResult res = mockMvc.perform(get("/lectures/filter")
				.param("priceMin", "100")
				.param("priceMax", "200")
				.param("workingAreas", "NORTH,CENTER"))
				.andExpect(status().isOk())
				.andReturn();

		//convert result json to ResponseLectureDTO
		String response = res.getResponse().getContentAsString();
		List<ResponseLectureDTO> lectures = objectMapper.readValue(response,
			new TypeReference<List<ResponseLectureDTO>>() {});

		int expectedSize = (int) allLectures.stream()
				.filter(lecture -> lecture.getPrice() >= 100 && lecture.getPrice() <= 200)
				.filter(lecture -> lecture.getAreas().stream()
						.anyMatch(area -> area == Area.NORTH || area == Area.CENTER))
				.count();

		assertEquals(expectedSize, lectures.size(), "Filtered lectures count should match expected size");
	}
}
// running test in terminal:
// ./mvnw test -Dtest=FlowIntegrationTest
