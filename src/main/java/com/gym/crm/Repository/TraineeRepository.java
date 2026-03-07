package com.gym.crm.Repository;

import com.gym.crm.model.Trainee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;
public interface TraineeRepository extends JpaRepository<Trainee,Long> {
    @Query("SELECT t FROM Trainee t WHERE t.user.username = :username")
    Optional<Trainee> findByUsername(@Param("username") String username);

    List<Trainee> findAll();

    @Query("SELECT t.user.username FROM Trainee t")
    List<String> findAllUsernames();
}
