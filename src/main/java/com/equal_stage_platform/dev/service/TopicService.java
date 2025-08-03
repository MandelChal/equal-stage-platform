package com.equal_stage_platform.dev.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.equal_stage_platform.dev.dto.CreateTopicDTO;
import com.equal_stage_platform.dev.dto.UpdateTopicDTO;
import com.equal_stage_platform.dev.exception.TopicException;
import com.equal_stage_platform.dev.model.Topic;
import com.equal_stage_platform.dev.repository.TopicRepository;

import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;

@Service
public class TopicService {
    private final TopicRepository topicRepository;

    public TopicService(TopicRepository topicRepository) {
        this.topicRepository = topicRepository;
    }

    @Transactional(readOnly = true)
    public List<Topic> getAllTopics() {
        return topicRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Topic getTopicById(Long id) {
        return topicRepository.findById(id)
                .orElseThrow(() -> new TopicException("Topic not found with ID: " + id));
    }

    @Transactional
    public Topic createTopic(CreateTopicDTO topic) {
        try {
            return topicRepository.save(new Topic(topic));
        } catch (DataIntegrityViolationException e) {
            throw new TopicException("Topic with name '" + topic.getName() + "' already exists", e);
        }
    }

    @Transactional
    public Topic updateTopic(Long id, UpdateTopicDTO topicDetails) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new TopicException("Topic not found with ID: " + id));
                
        if(topicDetails.getName() != null)
            topic.setName(topicDetails.getName());
        if(topicDetails.getDescription() != null)
            topic.setDescription(topicDetails.getDescription());
        
        return topicRepository.save(topic);
    }

    @Transactional
    public void deleteTopic(Long id) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new TopicException("Topic not found with ID: " + id));
        topicRepository.delete(topic);
    }

    @Transactional(readOnly = true)
    public List<Topic> findByPrefixName(String prefix) {
        return topicRepository.findByNameStartingWithIgnoreCase(prefix);
    }
}