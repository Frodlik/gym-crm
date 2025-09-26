package com.gym.crm.workloadservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Document(collection = "trainers")
@CompoundIndex(def = "{'first_name': 1, 'last_name': 1}", name = "full_name_index")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Trainer {
    @Id
    private String id;

    @Field("username")
    @Indexed(unique = true)
    private String username;

    @Field("first_name")
    @Indexed
    private String firstName;

    @Field("last_name")
    @Indexed
    private String lastName;

    @Field("is_active")
    private Boolean isActive;

    @Field("years")
    private List<Year> years;
}
