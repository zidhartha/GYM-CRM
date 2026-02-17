package com.gym.crm.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TrainingType{
    private Long id;
    private String name;

    public TrainingType(Long id, String name) {
        this.id = id;
        this.name = name;
    }
    public TrainingType(String name){
        this.name = name;
    }

    public TrainingType(){

    }
}
