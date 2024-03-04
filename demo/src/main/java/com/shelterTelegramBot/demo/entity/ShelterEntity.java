package com.shelterTelegramBot.demo.entity;

import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.*;

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
}
