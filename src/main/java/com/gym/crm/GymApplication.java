package com.gym.crm;

import com.gym.crm.config.AppConfig;
import com.gym.crm.facade.GymFacade;
import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.Training;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.time.LocalDate;
import java.util.List;

public class GymApplication {

    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        GymFacade gym = context.getBean(GymFacade.class);

        Trainer trainer = gym.createTrainer("dato", "jincharadze", "Yoga");
        Trainee trainee = gym.createTrainee(
                "sandro", "qochiashvili",
                LocalDate.of(2006, 3, 4),
                "zastava"
        );

        System.out.println("Trainer: " + trainer.getUser().getUsername());
        System.out.println("Trainer password: " + trainer.getUser().getPassword());
        System.out.println("Trainee: " + trainee.getUser().getUsername());
        System.out.println("Trainee password: " + trainee.getUser().getPassword());

        String traineeUsername = trainee.getUser().getUsername();
        String trainerUsername = trainer.getUser().getUsername();
        String traineePassword = trainee.getUser().getPassword();
        String trainerPassword = trainer.getUser().getPassword();
        Trainee foundTrainee = gym.selectTrainee(traineeUsername, traineePassword, traineeUsername);
        Trainer foundTrainer = gym.selectTrainer(trainerUsername, trainerPassword, trainerUsername);

        System.out.println("Selected trainee: " + foundTrainee.getUser().getUsername());
        System.out.println("Selected trainer: " + foundTrainer.getUser().getUsername());

        gym.updateTrainee(traineeUsername, traineePassword, "safichxia", LocalDate.of(1995, 4, 20));
        gym.updateTrainer(trainerUsername, trainerPassword, "CrossFit");

        gym.toggleTraineeActive(traineeUsername, traineePassword);
        gym.toggleTrainerActive(trainerUsername, trainerPassword);

        List<Trainer> unassigned = gym.getUnassignedTrainers(traineeUsername, traineePassword);
        System.out.println("Unassigned trainers: " + unassigned.size());

        gym.updateTraineeTrainers(traineeUsername, traineePassword, List.of(trainerUsername));

        Training training = gym.createTraining(
                traineeUsername, traineePassword,
                trainerUsername,
                "Power Session", "Strength Training",
                LocalDate.of(2025, 6, 1), 60L
        );
        System.out.println("Training created: " + training.getTrainingName());

        LocalDate from = LocalDate.of(2025, 1, 1);
        LocalDate to = LocalDate.of(2025, 12, 31);

        List<Training> traineeTrainings = gym.getTraineeTrainings(traineeUsername, traineePassword, from, to, null, null);
        System.out.println("Trainee trainings: " + traineeTrainings.size());

        List<Training> trainerTrainings = gym.getTrainerTrainings(trainerUsername, trainerPassword, from, to);
        System.out.println("Trainer trainings: " + trainerTrainings.size());

        gym.changeTraineePassword(traineeUsername, traineePassword, "axaliparoli");
        gym.changeTrainerPassword(trainerUsername, trainerPassword, "axaliparoli");

        int before = gym.selectAllTrainings().size();
        gym.deleteTrainee(traineeUsername, "axaliparoli");
        int after = gym.selectAllTrainings().size();
        System.out.println("Trainee deleted. Trainings removed by cascade: " + (before - after));
    }
}