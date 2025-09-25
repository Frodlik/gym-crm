package com.gym.crm.workloadservice.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Month {
    @Field("month_number")
    @Min(1)
    @Max(12)
    private Integer monthNumber;

    @Field("total_duration_minutes")
    @Min(0)
    private Integer totalDurationMinutes;
}
