package com.gym.crm.model;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Setter
@Getter
@ToString()
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@Entity
@Table(name="trainer")
public class Trainer{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="trainer_id")
    @ToString.Include
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "specialization", nullable = false)
    @ToString.Include
    private TrainingType specialization;

    @OneToOne(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    @ToString.Include
    private User user;

    @OneToMany(mappedBy = "trainer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Training> trainings;

    public Trainer(User user, TrainingType specialization) {
        this.user = user;
        this.specialization = specialization;
    }

}
