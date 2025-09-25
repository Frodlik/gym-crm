package com.gym.crm.workloadservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Year {
    @Field("year_number")
    private Integer yearNumber;

    @Field("months")
    private List<Month> months;
}
