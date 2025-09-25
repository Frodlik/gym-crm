package com.gym.crm.workloadservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Document(collection = "trainers")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Trainer {
    @Id
    private String id;

    @Field("username")
    private String username;

    @Field("first_name")
    private String firstName;

    @Field("last_name")
    private String lastName;

    @Field("is_active")
    private Boolean isActive;

    @Field("years")
    private List<Year> years;
}
