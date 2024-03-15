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
@Table(schema = "public", name = "rule")
public class RuleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //Генерация ID
    private Long id;

    @Column(name = "rules_dating_pets")
    private String rules;

    @Column(name = "documents_pets", nullable = false)
    private Boolean documents;

    @Column(name = "refusal_to_issue_animal")
    private String refusal;

    @Column(name = "transportation_recommendations", nullable = false)
    private String transportation;

    @Column(name = "recommendations_puppy_house")
    private String house;

}
