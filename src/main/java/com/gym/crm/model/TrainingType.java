package com.gym.crm.model;

import jakarta.persistence.*;
import lombok.*;


@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="training_type")
public class TrainingType{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="training_type_id")
    private Long id;
    @Column(name="training_type_name",nullable = false,unique = true)
    private String name;

    public TrainingType(String trainingTypeName){
        this.name = trainingTypeName;
    }
}
