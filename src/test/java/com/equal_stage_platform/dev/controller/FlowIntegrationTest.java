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

import com.equal_stage_platform.dev.dto.PaginatedResponseDTO;
import com.equal_stage_platform.dev.dto.ResponseLectureDTO;
import com.equal_stage_platform.dev.dto.ResponseLecturerDTO;
import com.equal_stage_platform.dev.dto.fakerUserDTO;
import com.equal_stage_platform.dev.model.LectureTopic;
import com.equal_stage_platform.dev.model.LecturerTopic;
import com.equal_stage_platform.dev.model.TargetAudience;
import com.equal_stage_platform.dev.model.enums.Area;
import com.equal_stage_platform.dev.model.enums.LectureStatus;
import com.equal_stage_platform.dev.model.HomePageBanner;
import com.equal_stage_platform.dev.model.AboutUs;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private Long testLecturerTopicId;

	// Helper to register and login, returns JWT token
	private String registerAndLogin(String email, String password) throws Exception {
		// Register
		mockMvc.perform(post("/api/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"email\":\"" + email + "\", \"password\":\"" + password + "\"}"))
				.andExpect(status().isCreated());

		return login(email, password);
	}

   

	private String login(String email, String password) throws Exception {
		String body = objectMapper.writeValueAsString(Map.of("email", email, "password", password));
		// Login
		MvcResult result = mockMvc.perform(post("/api/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(body))
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
                "\"videoLinks\":[{\"url\":\"https://youtube.com/test\",\"description\":\"Test video link\"}], " +
                "\"lecturerTopicsIds\":[" + testLecturerTopicId + "]}"))
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
			.header("Authorization", "Bearer " + token)
			.contentType(MediaType.APPLICATION_JSON)
			.content("{\"note\":\"Approved by test\"}"))
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
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{}"))
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
        // Create a test lecture topic (requires admin authentication)
        MvcResult topicResult = mockMvc.perform(post("/lecture-topics/admin/create")
				.header("Authorization", "Bearer " + adminToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"name\":\"Test Topic\", \"description\":\"A test topic for integration tests\"}"))
				.andExpect(status().isCreated())
				.andReturn();
		
		LectureTopic topic = objectMapper.readValue(topicResult.getResponse().getContentAsString(), LectureTopic.class);
		testTopicId = topic.getTopicId();

        // Create a test lecturer topic (requires admin authentication)
        MvcResult lecturerTopicResult = mockMvc.perform(post("/lecturer-topics/admin/create")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Test Lecturer Topic\", \"description\":\"A test lecturer topic for integration tests\"}"))
                .andExpect(status().isCreated())
                .andReturn();

        LecturerTopic lecturerTopic = objectMapper.readValue(lecturerTopicResult.getResponse().getContentAsString(), LecturerTopic.class);
        testLecturerTopicId = lecturerTopic.getTopicId();
		
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

	// Helper to reject a lecture (requires note)
	private void rejectLecture(String token, Long lectureId, String note, int expectedStatus) throws Exception {
		mockMvc.perform(patch("/lectures/admin/reject/" + lectureId)
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"note\":\"" + note + "\"}"))
				.andExpect(status().is(expectedStatus));
	}

	// Helper to reject a lecturer (requires note)
	private void rejectLecturer(String token, UUID lecturerId, String note, int expectedStatus) throws Exception {
		mockMvc.perform(post("/lecturers/admin/reject/" + lecturerId)
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"note\":\"" + note + "\"}"))
				.andExpect(status().is(expectedStatus));
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
		testRegularFilterLectures1(lectures);

		// Test lecture filters with multiple params
		testRegularFilterLectures2(lectures);

		// Test regular filter for lecturers
		testRegularFilterLecturers();
		
		// Test paginated filter endpoint for lectures
		testPaginatedFilterLectures(lectures);

		// Test paginated filter endpoint for lecturers
		testPaginatedFilterLecturers();

	}

	private void testRegularFilterLectures1(List<ResponseLectureDTO> allLectures) throws Exception {
		// Instead test regular lectures filter
		MvcResult res = mockMvc.perform(get("/lectures/filter")
				.param("minRank", "3.5")
				.param("maxRank", "4.5"))
				.andExpect(status().isOk())
				.andReturn();

		//convert result json to ResponseLectureDTO
		String response = res.getResponse().getContentAsString();
		List<ResponseLectureDTO> lectures = objectMapper.readValue(response,
			new TypeReference<List<ResponseLectureDTO>>() {});

		int expectedSize = (int) allLectures.stream()
				.filter(lecture -> lecture.getRank() >= 3.5 && lecture.getRank() <= 4.5)
				.count();

		assertEquals(expectedSize, lectures.size(), "Filtered lectures count should match expected size");

	}

	private void testRegularFilterLectures2(List<ResponseLectureDTO> allLectures) throws Exception {
		MvcResult res = mockMvc.perform(get("/lectures/filter")
				.param("minRank", "3.5")
				.param("maxRank", "4.5")
				.param("workingAreas", "NORTH,CENTER"))
				.andExpect(status().isOk())
				.andReturn();

		//convert result json to ResponseLectureDTO
		String response = res.getResponse().getContentAsString();
		List<ResponseLectureDTO> lectures = objectMapper.readValue(response,
			new TypeReference<List<ResponseLectureDTO>>() {});

		int expectedSize = (int) allLectures.stream()
				.filter(lecture -> lecture.getRank() >= 3.5 && lecture.getRank() <= 4.5)
				.filter(lecture -> lecture.getAreas().stream()
						.anyMatch(area -> area == Area.NORTH || area == Area.CENTER))
				.count();

		assertEquals(expectedSize, lectures.size(), "Filtered lectures count should match expected size");
	}

	private void testPaginatedFilterLectures(List<ResponseLectureDTO> allLectures) throws Exception {
		// Test paginated filter endpoint with basic filters
		MvcResult res = mockMvc.perform(get("/lectures/paginated/filter")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"pageNum\":0,\"pageSize\":5}")
				.param("minRank", "0")
				.param("maxRank", "5"))
				.andExpect(status().isOk())
				.andReturn();

		String response = res.getResponse().getContentAsString();
		
		// 1. Convert content to ResponseLectureDTO
		PaginatedResponseDTO<ResponseLectureDTO> paginatedResponse = objectMapper.readValue(response, 
			objectMapper.getTypeFactory().constructParametricType(
				PaginatedResponseDTO.class,
				ResponseLectureDTO.class));
		
		List<ResponseLectureDTO> paginatedLectures = paginatedResponse.getContent();
		
		// 2. Verify that returned lectures are according to the filter
		for (ResponseLectureDTO lecture : paginatedLectures) {
			assertEquals(true, lecture.getRank() >= 0 && lecture.getRank() <= 5, 
				"Lecture rank should be between 0 and 5, but was: " + lecture.getRank());
		}
		
		// Verify pagination structure
		assertEquals(true, paginatedLectures.size() <= 5, "Page size should not exceed 5");
		assertEquals(0, paginatedResponse.getPageNumber(), "Page number should be 0");
		assertEquals(5, paginatedResponse.getPageSize(), "Page size should be 5");
		
		// 3. Test with extra fields in the filter
		MvcResult res2 = mockMvc.perform(get("/lectures/paginated/filter")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"pageNum\":0,\"pageSize\":3}")
				.param("minRank", "0")
				.param("maxRank", "5")
				.param("workingAreas", "NORTH,CENTER"))
				.andExpect(status().isOk())
				.andReturn();

		String response2 = res2.getResponse().getContentAsString();
		PaginatedResponseDTO<ResponseLectureDTO> paginatedResponse2 = objectMapper.readValue(response2, 
			objectMapper.getTypeFactory().constructParametricType(
				PaginatedResponseDTO.class,
				ResponseLectureDTO.class));
		
		List<ResponseLectureDTO> filteredLectures = paginatedResponse2.getContent();
		
		// Verify the extra filters are applied correctly
		for (ResponseLectureDTO lecture : filteredLectures) {
			assertEquals(true, lecture.getRank() >= 0 && lecture.getRank() <= 5, 
				"Lecture rank should be between 0 and 5, but was: " + lecture.getRank());
			assertEquals(true, lecture.getAreas().stream()
				.anyMatch(area -> area == Area.NORTH || area == Area.CENTER),
				"Lecture should have NORTH or CENTER area");
		}
		
		assertEquals(true, filteredLectures.size() <= 3, "Page size should not exceed 3");
		assertEquals(3, paginatedResponse2.getPageSize(), "Page size should be 3");
	}

	private void testRegularFilterLecturers() throws Exception {
		// Fetch all approved lecturers to compute expected size
		MvcResult allApprovedRes = mockMvc.perform(get("/lecturers/all/approved"))
				.andExpect(status().isOk())
				.andReturn();
		String allApprovedStr = allApprovedRes.getResponse().getContentAsString();
		List<ResponseLecturerDTO> allApproved = objectMapper.readValue(allApprovedStr,
				new TypeReference<List<ResponseLecturerDTO>>() {});

		// Apply filter via endpoint
		MvcResult res = mockMvc.perform(get("/lecturers/filter")
				.param("minRank", "3.5")
				.param("maxRank", "5.0")
				.param("workingAreas", "NORTH,CENTER"))
				.andExpect(status().isOk())
				.andReturn();

		String response = res.getResponse().getContentAsString();
		List<ResponseLecturerDTO> filtered = objectMapper.readValue(response,
				new TypeReference<List<ResponseLecturerDTO>>() {});

		int expectedSize = (int) allApproved.stream()
				.filter(l -> l.getRank() != null && l.getRank() >= 3.5 && l.getRank() <= 5.0)
				.filter(l -> l.getWorkingAreas() != null && l.getWorkingAreas().stream()
						.anyMatch(a -> a.equals("NORTH") || a.equals("CENTER")))
				.count();

		assertEquals(expectedSize, filtered.size(), "Filtered lecturers count should match expected size");
	}

	private void testPaginatedFilterLecturers() throws Exception {
		MvcResult res = mockMvc.perform(get("/lecturers/paginated/filter")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"pageNum\":0,\"pageSize\":4}")
				.param("minRank", "0")
				.param("maxRank", "5"))
				.andExpect(status().isOk())
				.andReturn();

		String response = res.getResponse().getContentAsString();
		PaginatedResponseDTO<ResponseLecturerDTO> paginated = objectMapper.readValue(response,
				objectMapper.getTypeFactory().constructParametricType(
						PaginatedResponseDTO.class,
						ResponseLecturerDTO.class));

		List<ResponseLecturerDTO> content = paginated.getContent();
		for (ResponseLecturerDTO lecturer : content) {
			if (lecturer.getRank() != null) {
				assertEquals(true, lecturer.getRank() >= 0 && lecturer.getRank() <= 5,
						"Lecturer rank should be between 0 and 5, but was: " + lecturer.getRank());
			}
		}
		assertEquals(true, content.size() <= 4, "Page size should not exceed 4");
		assertEquals(0, paginated.getPageNumber(), "Page number should be 0");
		assertEquals(4, paginated.getPageSize(), "Page size should be 4");
	}

	@Test
	public void testHomePage() throws Exception {
		// Initialize faker system with 10 lecturers and 3 lectures per lecturer
		int lecturersCount = 5;
		int lecturesPerLecturer = 3;
		MvcResult fakerResult = mockMvc.perform(post("/faker/initSystem")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"lecturersCount\":\"" + lecturersCount + "\", \"lecturesPerLecturer\":" + lecturesPerLecturer + "}"))
				.andExpect(status().isOk())
				.andReturn();

        String json = fakerResult.getResponse().getContentAsString();
        Map<String, Set<fakerUserDTO>> usersInfo = objectMapper.readValue(
            json, new TypeReference<Map<String, Set<fakerUserDTO>>>() {});  

        fakerUserDTO admin = usersInfo.get("users").stream()
            .filter(user -> user.isAdmin())
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Admin user not found"));
        String adminToken = login(admin.getEmail(), admin.getPassword());

		// Test 1: Get all banners (public endpoint)
		MvcResult bannersResult = mockMvc.perform(get("/HomePage/banner/urls"))
				.andExpect(status().isOk())
				.andReturn();
		
		String bannersResponse = bannersResult.getResponse().getContentAsString();
		List<HomePageBanner> initialBanners = objectMapper.readValue(bannersResponse,
			new TypeReference<List<HomePageBanner>>() {});
		
		// Verify that 4 banners were created by the faker system
		assertEquals(4, initialBanners.size(), "Should have 4 initial banners from faker system");
		
		// Verify banners are sorted by display order
		for (int i = 0; i < initialBanners.size() - 1; i++) {
			assertTrue(initialBanners.get(i).getPosition() <= initialBanners.get(i + 1).getPosition(),
				"Banners should be sorted by display order");
		}

		// Test 2: Add a new banner (admin only)
		String newBannerJson = "{\"url\":\"https://example.com/new-banner.jpg\",\"title\":\"New Test Banner\",\"mediaType\":\"PHOTO\",\"position\":2}";
		MvcResult addBannerResult = mockMvc.perform(post("/HomePage/admin/banner/url")
				.header("Authorization", "Bearer " + adminToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(newBannerJson))
				.andDo(result -> {
					System.out.println(result.getResponse().getContentAsString());
				})
				.andExpect(status().isOk())
				.andReturn();
		
		String addBannerResponse = addBannerResult.getResponse().getContentAsString();
		HomePageBanner addedBanner = objectMapper.readValue(addBannerResponse, HomePageBanner.class);
		assertEquals(2, addedBanner.getPosition(), "Banner should be added at position 2");

		// Test 3: Try to add banner without admin token (should fail)
		mockMvc.perform(post("/HomePage/admin/banner/url")
				.contentType(MediaType.APPLICATION_JSON)
				.content(newBannerJson))
				.andExpect(status().isForbidden());

		// Test 4: Try to add banner with duplicate URL (should fail)
		mockMvc.perform(post("/HomePage/admin/banner/url")
				.header("Authorization", "Bearer " + adminToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(newBannerJson))
				.andDo(result -> {
					System.out.println(result.getResponse().getContentAsString());
				})
				.andExpect(status().isBadRequest());

		// Test 5: Add another banner at the end (no display order specified)
		String endBannerJson = "{\"url\":\"https://example.com/end-banner.jpg\",\"title\":\"End Banner\",\"mediaType\":\"VIDEO\"}";
		mockMvc.perform(post("/HomePage/admin/banner/url")
				.header("Authorization", "Bearer " + adminToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(endBannerJson))
				.andExpect(status().isOk());

		// Test 6: Get updated banners and verify count
		MvcResult updatedBannersResult = mockMvc.perform(get("/HomePage/banner/urls"))
				.andExpect(status().isOk())
				.andReturn();
		
		String updatedBannersResponse = updatedBannersResult.getResponse().getContentAsString();
		List<HomePageBanner> updatedBanners = objectMapper.readValue(updatedBannersResponse,
			new TypeReference<List<HomePageBanner>>() {});
		
		assertEquals(6, updatedBanners.size(), "Should have 6 banners after adding 2 new ones");
			
		// Test 7: Update banner with new URL and title
		int oldBannerPosition = updatedBanners.get(2).getPosition();
		String updateSingleBannerJson = "[{\"id\":" + updatedBanners.get(2).getId() + ",\"url\":\"https://example.com/updated-banner.jpg\",\"title\":\"Updated Banner Title\",\"mediaType\":\"PHOTO\"}]";
		MvcResult updateSingleBannerResult = mockMvc.perform(put("/HomePage/admin/banner/urls")
				.header("Authorization", "Bearer " + adminToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(updateSingleBannerJson))
				.andExpect(status().isOk())
				.andReturn();

		String updateSingleBannerResponse = updateSingleBannerResult.getResponse().getContentAsString();
		List<HomePageBanner> updatedBanners2 = objectMapper.readValue(updateSingleBannerResponse,
			new TypeReference<List<HomePageBanner>>() {});
		assertEquals(oldBannerPosition, updatedBanners2.get(0).getPosition(), "Banner should be updated at position 2");
		assertEquals("https://example.com/updated-banner.jpg", updatedBanners2.get(0).getUrl(), "Banner URL should be updated");
		assertEquals("Updated Banner Title", updatedBanners2.get(0).getTitle(), "Banner title should be updated");
		assertEquals(com.equal_stage_platform.dev.model.enums.MediaType.PHOTO, updatedBanners2.get(0).getMediaType(), "Banner media type should be updated");


		// Test 8: Try to update banner without admin token (should fail)
		mockMvc.perform(put("/HomePage/admin/banner/urls")
				.contentType(MediaType.APPLICATION_JSON)
				.content(updateSingleBannerJson))
				.andExpect(status().isForbidden());

		// Test 9: Delete a banner
		Integer bannerToDeleteId = updatedBanners.get(3).getId();
		mockMvc.perform(delete("/HomePage/admin/banner/url/" + bannerToDeleteId)
				.header("Authorization", "Bearer " + adminToken))
				.andExpect(status().isOk());

		// Test 10: Try to delete banner without admin token (should fail)
		mockMvc.perform(delete("/HomePage/admin/banner/url/" + bannerToDeleteId))
				.andExpect(status().isForbidden());

		// Test 11: Try to delete non-existent banner (should fail)
		mockMvc.perform(delete("/HomePage/admin/banner/url/99999")
				.header("Authorization", "Bearer " + adminToken))
				.andExpect(status().isBadRequest());

		// Test 12: Verify final banner count
		MvcResult finalBannersResult = mockMvc.perform(get("/HomePage/banner/urls"))
				.andExpect(status().isOk())
				.andReturn();
		
		String finalBannersResponse = finalBannersResult.getResponse().getContentAsString();
		List<HomePageBanner> finalBanners = objectMapper.readValue(finalBannersResponse,
			new TypeReference<List<HomePageBanner>>() {});
		
		assertEquals(5, finalBanners.size(), "Should have 5 banners after deleting one");

		// Test 14: Verify banners are still sorted by display order
		for (int i = 0; i < finalBanners.size() - 1; i++) {
			assertTrue((int)finalBanners.get(i).getPosition() == ((int)finalBanners.get(i + 1).getPosition())-1,
				"Banners should remain sorted by display order after operations");
		}

		// Test 15: Reorder banners
		String reorderBannersJson = "[5,2,6,1,4]"; //id 3 was deleted
		mockMvc.perform(put("/HomePage/admin/banner/urls/reorder")
				.header("Authorization", "Bearer " + adminToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(reorderBannersJson))
				.andExpect(status().isOk());
		MvcResult reorderedBannersResult = mockMvc.perform(get("/HomePage/banner/urls"))
				.andExpect(status().isOk())
				.andReturn();
		
		String reorderedBannersResponse = reorderedBannersResult.getResponse().getContentAsString();
		List<HomePageBanner> reorderedBanners = objectMapper.readValue(reorderedBannersResponse,
			new TypeReference<List<HomePageBanner>>() {});
		Map<Integer, HomePageBanner> reorderedBannersMap = new HashMap<>();
		for (HomePageBanner banner : reorderedBanners) {
			reorderedBannersMap.put(banner.getId(), banner);
		}
		assertEquals(1, reorderedBannersMap.get(5).getPosition(), "banner with id 5 should be at position 1");
		assertEquals(2, reorderedBannersMap.get(2).getPosition(), "banner with id 2 should be at position 2");
		assertEquals(3, reorderedBannersMap.get(6).getPosition(), "banner with id 6 should be at position 3");
		assertEquals(4, reorderedBannersMap.get(1).getPosition(), "banner with id 1 should be at position 4");
		assertEquals(5, reorderedBannersMap.get(4).getPosition(), "banner with id 4 should be at position 5");

		// Test 16: Try to reorder banners with duplicate positions
		String duplicatePositionsJson = "[2,6,1,4,2]";
		mockMvc.perform(put("/HomePage/admin/banner/urls/reorder")
				.header("Authorization", "Bearer " + adminToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(duplicatePositionsJson))
				.andExpect(status().isBadRequest());

		// Test 17: Try to reorder banners with invalid positions
		String invalidPositionsJson = "[5,2,3,1,6]";
		mockMvc.perform(put("/HomePage/admin/banner/urls/reorder")
				.header("Authorization", "Bearer " + adminToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(invalidPositionsJson))
				.andExpect(status().isBadRequest());

		// Test 18: Try to reorder banners with negative positions
		String negativePositionsJson = "[5,2,3,4,-1]";
		mockMvc.perform(put("/HomePage/admin/banner/urls/reorder")
				.header("Authorization", "Bearer " + adminToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(negativePositionsJson))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testHomePageBannerErrorCases() throws Exception {
		// Initialize faker system
		int lecturersCount = 2;
		int lecturesPerLecturer = 2;
		MvcResult fakerResult = mockMvc.perform(post("/faker/initSystem")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"lecturersCount\":\"" + lecturersCount + "\", \"lecturesPerLecturer\":" + lecturesPerLecturer + "}"))
				.andExpect(status().isOk())
				.andReturn();

        String json = fakerResult.getResponse().getContentAsString();
        Map<String, Set<fakerUserDTO>> usersInfo = objectMapper.readValue(
            json, new TypeReference<Map<String, Set<fakerUserDTO>>>() {});  

        fakerUserDTO admin = usersInfo.get("users").stream()
            .filter(user -> user.isAdmin())
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Admin user not found"));
        String adminToken = login(admin.getEmail(), admin.getPassword());

		// Get initial banners
		MvcResult bannersResult = mockMvc.perform(get("/HomePage/banner/urls"))
				.andExpect(status().isOk())
				.andReturn();
		
		String bannersResponse = bannersResult.getResponse().getContentAsString();
		List<HomePageBanner> initialBanners = objectMapper.readValue(bannersResponse,
			new TypeReference<List<HomePageBanner>>() {});

		// Test 2: Update banners with non-existent banner ID
		String nonExistentBannerJson = "[{\"id\":99999,\"url\":\"https://example.com/test.jpg\",\"title\":\"Invalid URL Banner\",\"mediaType\":\"PHOTO\"}]";
		mockMvc.perform(put("/HomePage/admin/banner/urls")
				.header("Authorization", "Bearer " + adminToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(nonExistentBannerJson))
				.andExpect(status().isBadRequest());

		// Test 3: Add banner with invalid URL format
		String invalidUrlJson = "{\"url\":\"invalid-url\",\"title\":\"Invalid URL Banner\",\"mediaType\":\"PHOTO\"}";
		mockMvc.perform(post("/HomePage/admin/banner/url")
				.header("Authorization", "Bearer " + adminToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(invalidUrlJson))
				.andExpect(status().isBadRequest());

		// Test 4: Add banner with missing required fields
		String missingFieldsJson = "{\"url\":\"https://example.com/test.jpg\"}";
		mockMvc.perform(post("/HomePage/admin/banner/url")
				.header("Authorization", "Bearer " + adminToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(missingFieldsJson))
				.andExpect(status().isBadRequest());

		// Test 5: Update banners with conflicting display orders (same order for different banners)
		String conflictingOrdersJson = "[{\"id\":" + initialBanners.get(0).getId() + ",\"url\":\"https://example.com/test.jpg\",\"title\":\"Invalid URL Banner\",\"mediaType\":\"PHOTO\"},{\"id\":" + initialBanners.get(1).getId() + ",\"url\":\"https://example.com/test.jpg\",\"title\":\"Invalid URL Banner\",\"mediaType\":\"PHOTO\"},{\"id\":" + initialBanners.get(2).getId() + ",\"url\":\"https://example.com/test.jpg\",\"title\":\"Invalid URL Banner\",\"mediaType\":\"PHOTO\"}]";
		mockMvc.perform(put("/HomePage/admin/banner/urls")
				.header("Authorization", "Bearer " + adminToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(conflictingOrdersJson))
				.andExpect(status().isBadRequest());

		// Test 6: Update banner with duplicate URL
		// First, get a banner to update
		HomePageBanner firstBanner = initialBanners.get(0);
		HomePageBanner secondBanner = initialBanners.get(1);
		
		String duplicateUrlJson = "[{\"id\":" + firstBanner.getId() + ",\"url\":\"" + secondBanner.getUrl() + "\"}]";
		mockMvc.perform(put("/HomePage/admin/banner/urls")
				.header("Authorization", "Bearer " + adminToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(duplicateUrlJson))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testAboutUsEndpoints() throws Exception {
		// 1. Initialize faker system
		int lecturersCount = 2;
		int lecturesPerLecturer = 1;
		MvcResult fakerResult = mockMvc.perform(post("/faker/initSystem")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"lecturersCount\":\"" + lecturersCount + "\", \"lecturesPerLecturer\":" + lecturesPerLecturer + "}"))
				.andExpect(status().isOk())
				.andReturn();

		String json = fakerResult.getResponse().getContentAsString();
		Map<String, Set<fakerUserDTO>> usersInfo = objectMapper.readValue(
			json, new TypeReference<Map<String, Set<fakerUserDTO>>>() {});

		// 2. Login with admin
		fakerUserDTO admin = usersInfo.get("users").stream()
			.filter(f -> f.isAdmin())
			.findFirst()
			.orElseThrow(() -> new RuntimeException("Admin user not found"));
		String adminToken = login(admin.getEmail(), admin.getPassword());

		// 3.a GET /HomePage/about_us (public)
		MvcResult getRes = mockMvc.perform(get("/HomePage/about_us"))
				.andExpect(status().isOk())
				.andReturn();
		AboutUs aboutUs = objectMapper.readValue(getRes.getResponse().getContentAsString(), AboutUs.class);
		assertTrue(aboutUs.getText() != null && !aboutUs.getText().isBlank());
		assertTrue(aboutUs.getImageUrls() != null && !aboutUs.getImageUrls().isEmpty());
		assertTrue(aboutUs.getVideoUrls() != null && !aboutUs.getVideoUrls().isEmpty());

		// 3.b PUT /HomePage/admin/about_us (authorized)
		String updateJson = "{\"text\":\"Updated about us text\",\"imageUrls\":[\"https://example.com/img1.jpg\",\"https://example.com/img2.jpg\"],\"videoUrls\":[\"https://youtube.com/watch?v=abc\"]}";
		MvcResult putRes = mockMvc.perform(put("/HomePage/admin/about_us")
				.header("Authorization", "Bearer " + adminToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(updateJson))
				.andExpect(status().isOk())
				.andReturn();
		AboutUs updated = objectMapper.readValue(putRes.getResponse().getContentAsString(), AboutUs.class);
		assertEquals("Updated about us text", updated.getText());
		assertTrue(updated.getImageUrls().contains("https://example.com/img1.jpg"));
		assertTrue(updated.getVideoUrls().contains("https://youtube.com/watch?v=abc"));

		// 3.c GET again to confirm persistence
		MvcResult getRes2 = mockMvc.perform(get("/HomePage/about_us"))
				.andExpect(status().isOk())
				.andReturn();
		AboutUs updatedGet = objectMapper.readValue(getRes2.getResponse().getContentAsString(), AboutUs.class);
		assertEquals("Updated about us text", updatedGet.getText());

		// 3.d PUT /HomePage/admin/about_us (unauthorized)
		mockMvc.perform(put("/HomePage/admin/about_us")
				.contentType(MediaType.APPLICATION_JSON)
				.content(updateJson))
				.andExpect(status().isForbidden());

		// 3.e POST /HomePage/admin/about_us (authorized) – create another record
		String createJson = "{\"text\":\"Another about us\",\"imageUrls\":[\"https://example.com/img3.jpg\"],\"videoUrls\":[\"https://youtube.com/watch?v=def\"]}";
		MvcResult postRes = mockMvc.perform(post("/HomePage/admin/about_us")
				.header("Authorization", "Bearer " + adminToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(createJson))
				.andExpect(status().isOk())
				.andReturn();
		AboutUs created = objectMapper.readValue(postRes.getResponse().getContentAsString(), AboutUs.class);
		assertEquals("Another about us", created.getText());

		// 3.f POST /HomePage/admin/about_us (unauthorized)
		mockMvc.perform(post("/HomePage/admin/about_us")
				.contentType(MediaType.APPLICATION_JSON)
				.content(createJson))
				.andExpect(status().isForbidden());
	}

	@Test
public void testAdminApproveAndRejectFlow() throws Exception {
	// 1. create user
	String email = "flowuser+" + UUID.randomUUID() + "@example.com";
	String token = registerAndLogin(email, "Password!1234");

	// 2. make him admin
	registerAdmin(token, 200);

	// prerequisites for lecturer/lecture creation
	setupTopicsAndTargetAudiences(token);

	// 3. create lecturer
	String lecturerEmail = "lecturerA@example.com";
	createLecturer(token, "Flow", "User", "Bio", "Tel Aviv", lecturerEmail, "0500000000",
			"https://example.com/image.jpg", Set.of(Area.CENTER), 201);

	// 4. approve him
	Set<ResponseLecturerDTO> pendLects = getPendingLecturers(token);
	UUID lecturerId = pendLects.iterator().next().getUserId();
	approveLecturer(token, lecturerId, 200);

	// 5. create lecture
	String lectureTitle = "Flow Lecture A";
	createLecture(token, lectureTitle, "Desc", 60, 150, LectureStatus.ON_AIR, true,
			"https://example.com/lecture.jpg", 201);

	// find just-created pending lecture
	Set<ResponseLectureDTO> pendingLectures = getPendingLectures(token);
	Long lectureId = pendingLectures.stream()
		.filter(l -> lectureTitle.equals(l.getTitle()))
		.findFirst()
		.orElseThrow()
		.getLectureId();

	// 6. approve lecture
	approveLecture(token, lectureId, 200);

	// 7. reject lecture
	rejectLecture(token, lectureId, "Not suitable", 200);

	// 8. reject lecturer
	rejectLecturer(token, lecturerId, "Profile not acceptable", 200);
}
}
// running test in terminal:
// ./mvnw test -Dtest=FlowIntegrationTest