package com.gym.crm;


import com.gym.crm.config.AppConfig;

import com.gym.crm.facade.GymFacade;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.TrainingType;
import com.gym.crm.model.User;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.time.LocalDate;

public class GymApplication {
    public static void main(String[] args) {

        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        GymFacade facade = context.getBean(GymFacade.class);
        facade.createTrainee("Davit","Jincharadze", LocalDate.of(2005,7,26),"Javakhishvili");
        TrainingType kungfu = new TrainingType(1L,"Kung-fu");
        facade.createTrainer("Giorgi","Janelidze",kungfu);
        System.out.println(facade.selectAllTrainees());
        System.out.println(facade.selectAllTrainers());
    }
}
