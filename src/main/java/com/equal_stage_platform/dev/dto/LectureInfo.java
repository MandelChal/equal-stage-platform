package com.equal_stage_platform.dev.dto;

import com.equal_stage_platform.dev.model.Lecture;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LectureInfo {
	private Long id;
	private String name;
	private int duration;
	private double rank;

    public LectureInfo(Lecture lecture) {
        this.id = lecture.getLectureId();
        this.name = lecture.getTitle();
        this.duration = lecture.getDuration();
        this.rank = lecture.getRank();
    }
}
