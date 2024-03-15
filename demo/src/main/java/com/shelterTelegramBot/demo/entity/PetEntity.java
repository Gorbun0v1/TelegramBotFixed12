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
@Table(schema = "public", name = "pets")
public class PetEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //Генерация ID
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "owner_id", referencedColumnName = "id")
    private UserEntity owner;

    @Column(name = "age", nullable = false)
    private Integer age;

    @Column(name = "breed", nullable = false)
    private String breed;

    @Column(name = "comment")
    private String comment;

    @ManyToOne
    @JoinColumn(name = "shelter_id", referencedColumnName = "id")
    private ShelterEntity shelter;

//    @Column(name = "documents_pets")
//    String documentsPets;
//    @Column(name = "refusal_to_issue_animal")
//    String refusalAnimal;
//    @Column(name = "transportation_recommendations", nullable = false)
//    String transportationRecommendations;
//    @Column(name = "recommendations_puppy_house")
//    String recommendationsPuppyHouse;
//    @Column(name = "recommendations_house")
//    String recommendationsHouse;
//    @Column(name = "recommendations_animal_with_disabilities")
//    String recommendationsDisabilities;
//    @Column(name = "recommendations_dog_handler")
//    String recommendationsDogHandler;
}
