package com.equal_stage_platform.dev.fake;

import com.equal_stage_platform.dev.model.Lecturer;
import com.github.javafaker.Faker;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class LecturerFakerService {

    private final Faker faker = new Faker();

    public Lecturer generateFakeLecturer() {
        Lecturer lecturer = new Lecturer();
        lecturer.setName(faker.name().fullName());
        lecturer.setBio(faker.lorem().paragraph(2));
        lecturer.setCity(faker.address().city());
        lecturer.setEmail(faker.internet().emailAddress());
        lecturer.setPhone(faker.phoneNumber().cellPhone());
        lecturer.setImageUrl(faker.internet().avatar());
        lecturer.setCreatedAt(LocalDateTime.now());
        return lecturer;
    }
}
