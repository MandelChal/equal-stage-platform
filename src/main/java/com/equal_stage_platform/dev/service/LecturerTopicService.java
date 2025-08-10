package com.equal_stage_platform.dev.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.equal_stage_platform.dev.dto.CreateTopicDTO;
import com.equal_stage_platform.dev.dto.UpdateTopicDTO;
import com.equal_stage_platform.dev.exception.TopicException;
import com.equal_stage_platform.dev.model.LecturerTopic;
import com.equal_stage_platform.dev.repository.LecturerTopicRepository;

import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;

@Service
public class LecturerTopicService {
    private final LecturerTopicRepository lecturerTopicRepository;

    public LecturerTopicService(LecturerTopicRepository lecturerTopicRepository) {
        this.lecturerTopicRepository = lecturerTopicRepository;
    }

    @Transactional(readOnly = true)
    public List<LecturerTopic> getAllTopics() {
        return lecturerTopicRepository.findAll();
    }

    @Transactional(readOnly = true)
    public LecturerTopic getTopicById(Long id) {
        return lecturerTopicRepository.findById(id)
                .orElseThrow(() -> new TopicException("Topic not found with ID: " + id));
    }

    @Transactional
    public LecturerTopic createTopic(CreateTopicDTO topic) {
        try {
            return lecturerTopicRepository.save(new LecturerTopic(topic));
        } catch (DataIntegrityViolationException e) {
            throw new TopicException("Topic with name '" + topic.getName() + "' already exists", e);
        }
    }

    @Transactional
    public LecturerTopic updateTopic(Long id, UpdateTopicDTO topicDetails) {
        LecturerTopic topic = lecturerTopicRepository.findById(id)
                .orElseThrow(() -> new TopicException("Topic not found with ID: " + id));
                
        if(topicDetails.getName() != null)
            topic.setName(topicDetails.getName());
        if(topicDetails.getDescription() != null)
            topic.setDescription(topicDetails.getDescription());
        
        return lecturerTopicRepository.save(topic);
    }

    @Transactional
    public void deleteTopic(Long id) {
        LecturerTopic topic = lecturerTopicRepository.findById(id)
                .orElseThrow(() -> new TopicException("Topic not found with ID: " + id));
        lecturerTopicRepository.delete(topic);
    }

    @Transactional(readOnly = true)
    public List<LecturerTopic> findByPrefixName(String prefix) {
        return lecturerTopicRepository.findByNameStartingWithIgnoreCase(prefix);
    }
}