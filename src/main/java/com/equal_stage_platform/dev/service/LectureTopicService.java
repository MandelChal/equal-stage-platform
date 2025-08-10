package com.equal_stage_platform.dev.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.equal_stage_platform.dev.dto.CreateTopicDTO;
import com.equal_stage_platform.dev.dto.UpdateTopicDTO;
import com.equal_stage_platform.dev.exception.TopicException;
import com.equal_stage_platform.dev.model.LectureTopic;
import com.equal_stage_platform.dev.repository.LectureTopicRepository;

import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;

@Service
public class LectureTopicService {
    private final LectureTopicRepository lectureTopicRepository;

    public LectureTopicService(LectureTopicRepository lectureTopicRepository) {
        this.lectureTopicRepository = lectureTopicRepository;
    }

    @Transactional(readOnly = true)
    public List<LectureTopic> getAllTopics() {
        return lectureTopicRepository.findAll();
    }

    @Transactional(readOnly = true)
    public LectureTopic getTopicById(Long id) {
        return lectureTopicRepository.findById(id)
                .orElseThrow(() -> new TopicException("Topic not found with ID: " + id));
    }

    @Transactional
    public LectureTopic createTopic(CreateTopicDTO topic) {
        try {
            return lectureTopicRepository.save(new LectureTopic(topic));
        } catch (DataIntegrityViolationException e) {
            throw new TopicException("Topic with name '" + topic.getName() + "' already exists", e);
        }
    }

    @Transactional
    public LectureTopic updateTopic(Long id, UpdateTopicDTO topicDetails) {
        LectureTopic topic = lectureTopicRepository.findById(id)
                .orElseThrow(() -> new TopicException("Topic not found with ID: " + id));
                
        if(topicDetails.getName() != null)
            topic.setName(topicDetails.getName());
        if(topicDetails.getDescription() != null)
            topic.setDescription(topicDetails.getDescription());
        
        return lectureTopicRepository.save(topic);
    }

    @Transactional
    public void deleteTopic(Long id) {
        LectureTopic topic = lectureTopicRepository.findById(id)
                .orElseThrow(() -> new TopicException("Topic not found with ID: " + id));
        lectureTopicRepository.delete(topic);
    }

    @Transactional(readOnly = true)
    public List<LectureTopic> findByPrefixName(String prefix) {
        return lectureTopicRepository.findByNameStartingWithIgnoreCase(prefix);
    }
}