package com.shelterTelegramBot.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.Set;

@Setter
@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Table(schema = "public", name = "shelter")
public class ShelterEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //Генерация ID
    Long id;
    @Column(name = "name", nullable = false)
    String name;
    @Column(name = "schedule", nullable = false)
    String schedule;
    @Column(name = "driving_directions")
    String drivingDirections;
    @Column(name = "guard_details", nullable = false)
    String guardDetails;
    @Column(name = "safety_precautions")
    String safetyPrecautions;
    @OneToMany
    @JsonIgnore
    Set<PetEntity> pets;
}
