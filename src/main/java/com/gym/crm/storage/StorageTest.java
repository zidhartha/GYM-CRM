package com.gym.crm.storage;


import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class StorageTest {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext("com.gym.crm");

        TraineeStorage traineeStorage = context.getBean(TraineeStorage.class);
        TrainerStorage trainerStorage = context.getBean(TrainerStorage.class);
        TrainingStorage trainingStorage = context.getBean(TrainingStorage.class);

        System.out.println("=== Trainees ===");
        traineeStorage.getStorage().forEach((id, t) -> System.out.println(id + " -> " + t.getUsername()));

        System.out.println("\n=== Trainers ===");
        trainerStorage.getStorage().forEach((id, t) -> System.out.println(id + " -> " + t.getUsername()));

        System.out.println("\n=== Trainings ===");
        trainingStorage.getStorage().forEach((id, t) ->
                System.out.println(id + " -> " + t.getTrainingName() + " (" + t.getTrainingType().getName() + ")")
        );

        context.close();
    }
}
