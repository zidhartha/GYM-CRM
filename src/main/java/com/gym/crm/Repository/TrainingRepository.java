package com.gym.crm.Repository;

import com.gym.crm.model.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

public interface TrainingRepository extends JpaRepository<Training,Long> {
    @Query(
            "SELECT t FROM Training t WHERE " +
                    "t.trainee.user.username = :traineeUsername " +
                    "AND (CAST(:fromDate AS date) IS NULL OR t.trainingDate >= :fromDate) " +
                    "AND (CAST(:toDate AS date) IS NULL OR t.trainingDate <= :toDate) " +
                    "AND (CAST(:trainerName AS string) IS NULL OR t.trainer.user.username = :trainerName) " +
                    "AND (CAST(:trainingType AS string) IS NULL OR t.trainingType.name = :trainingType)"
    )
    List<Training> findTraineeTrainings(
            @Param("traineeUsername") String traineeUsername,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("trainerName") String trainerName,
            @Param("trainingType") String trainingType
    );

    @Query(
            "SELECT t FROM Training t WHERE " +
                    "t.trainer.user.username = :trainerUsername " +
                    "AND (CAST(:fromDate AS date) IS NULL OR t.trainingDate >= :fromDate) " +
                    "AND (CAST(:toDate AS date) IS NULL OR t.trainingDate <= :toDate) " +
                    "AND (CAST(:traineeName AS string) IS NULL OR t.trainee.user.username = :traineeName)"
    )
    List<Training> findTrainerTrainings(
            @Param("trainerUsername") String trainerUsername,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("traineeName") String traineeName
    );

    List<Training> findAll();

}