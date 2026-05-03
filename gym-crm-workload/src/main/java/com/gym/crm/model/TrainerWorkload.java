    package com.gym.crm.model;

    import jakarta.validation.constraints.Max;
    import jakarta.validation.constraints.Min;
    import lombok.*;
    import org.springframework.data.annotation.Id;
    import org.springframework.data.mongodb.core.index.CompoundIndex;
    import org.springframework.data.mongodb.core.index.Indexed;
    import org.springframework.data.mongodb.core.mapping.Document;

    import java.util.ArrayList;
    import java.util.HashMap;
    import java.util.List;
    import java.util.Map;

    @Document(collection = "trainer_workload")
    @CompoundIndex(name = "name_idx", def = "{'firstName': 1, 'lastName': 1}")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class TrainerWorkload {
        @Id
        private String id;

        @Indexed(unique = true)
        private String username;

        @Indexed
        private String firstName;

        @Indexed
        private String lastName;
        private boolean active;
        private List<YearlySummary> yearlySummary;

        public TrainerWorkload(String username) {
            this.username = username;
            this.yearlySummary = new ArrayList<>();
        }

        @NoArgsConstructor
        @Getter
        @Setter
        public static class YearlySummary {
            @Min(1800)
            private int year;
            private List<MonthSummary> months;

            public YearlySummary(int year){
                this.year = year;
                this.months = new ArrayList<>();
            }
        }

        @Getter
        @Setter
        @NoArgsConstructor
        public static class MonthSummary{
            private int month;
            private int totalDurationMinutes;

            public MonthSummary(int month){
                this.month = month;
            }
        }
    }
