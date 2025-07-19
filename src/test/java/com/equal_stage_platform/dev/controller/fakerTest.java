package com.equal_stage_platform.dev.controller;

// import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import com.equal_stage_platform.dev.dto.fakerUserDTO;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.Set;
// import java.util.UUID;

// import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class fakerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testInitSystem() throws Exception {
        //TODO - check why imageURL is NULL in SQL Table
        // 1. POST /api/advanced-faker/init-system
        MvcResult initResult = mockMvc.perform(post("/faker/initSystem")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"lecturersCount\":\"" + 5 + "\", \"lecturesPerLecturer\":\"" + 3 + "\"}"))
            .andExpect(status().isOk())
            .andReturn();
        String json = initResult.getResponse().getContentAsString();
        Set<fakerUserDTO> usersInfo = objectMapper.readValue(json, new TypeReference<Set<fakerUserDTO>>(){});
        System.out.println("\nReturned fakerUserDTOs:");
        for (fakerUserDTO user : usersInfo) {
            System.out.println(user);
        }
}

//     @Test
//     public void testFakerEndpointsFlow() throws Exception {
//         // 1. POST /api/advanced-faker/create-complete-system
//         MvcResult completeSystemResult = mockMvc.perform(post("/api/advanced-faker/create-complete-system")
//                 .param("lecturerCount", "3")
//                 .param("lectureCount", "5"))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("success").value(true))
//                 .andReturn();
//         String completeSystemJsonStr = completeSystemResult.getResponse().getContentAsString();
//         System.out.println("\n[create-complete-system] Response:\n" + objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(objectMapper.readTree(completeSystemJsonStr)));

//         // 2. POST /api/advanced-faker/lecturers/create-single
//         MvcResult singleLecturerResult = mockMvc.perform(post("/api/advanced-faker/lecturers/create-single"))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.success").value(true))
//                 .andReturn();
//         JsonNode singleLecturerJson = objectMapper.readTree(singleLecturerResult.getResponse().getContentAsString());
//         UUID lecturerId = UUID.fromString(singleLecturerJson.get("lecturer").get("userId").asText());

//         // 3. POST /api/advanced-faker/lecturers/create-multiple?count=5
//         mockMvc.perform(post("/api/advanced-faker/lecturers/create-multiple")
//                 .param("count", "5"))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.success").value(true))
//                 .andExpect(jsonPath("$.created_count").value(5));

//         // 4. GET /api/advanced-faker/lecturers/pending
//         MvcResult pendingLecturersResult = mockMvc.perform(get("/api/advanced-faker/lecturers/pending"))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.success").value(true))
//                 .andReturn();
//         JsonNode pendingLecturersJson = objectMapper.readTree(pendingLecturersResult.getResponse().getContentAsString());
//         assertTrue(pendingLecturersJson.get("pending_count").asInt() >= 1);

//         // 5. POST /api/advanced-faker/lecturers/approve-all
//         mockMvc.perform(post("/api/advanced-faker/lecturers/approve-all"))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.success").value(true));

//         // 6. POST /api/advanced-faker/lecturers/approve/{lecturerId}
//         mockMvc.perform(post("/api/advanced-faker/lecturers/approve/" + lecturerId))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.success").value(true))
//                 .andExpect(jsonPath("$.new_status").value("APPROVED"));

//         // 7. POST /api/advanced-faker/lectures/create-single
//         mockMvc.perform(post("/api/advanced-faker/lectures/create-single"))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.success").value(true));

//         // 8. POST /api/advanced-faker/lectures/create-multiple?count=5
//         mockMvc.perform(post("/api/advanced-faker/lectures/create-multiple")
//                 .param("count", "5"))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.success").value(true))
//                 .andExpect(jsonPath("$.created_count").value(5));

//         // 9. POST /api/advanced-faker/lectures/create-with-existing-lecturer?lecturerId=...
//         mockMvc.perform(post("/api/advanced-faker/lectures/create-with-existing-lecturer")
//                 .param("lecturerId", lecturerId.toString()))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.success").value(true))
//                 .andExpect(jsonPath("$.lecturer_status").value("APPROVED"));

//         // 10. POST /api/advanced-faker/lectures/create-upcoming?count=6
//         mockMvc.perform(post("/api/advanced-faker/lectures/create-upcoming")
//                 .param("count", "6"))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.success").value(true))
//                 .andExpect(jsonPath("$.count").value(6));
//     }
} 