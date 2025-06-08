package com.example.javaprojet.entity;

import com.example.javaprojet.enums.RoleSecondaire;
import com.example.javaprojet.enums.RoleType;
import jakarta.persistence.*;
import lombok.*;
import net.minidev.json.annotate.JsonIgnore;


@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class ProjetRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private Utilisateur utilisateur;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private Projet projet;

    @Enumerated(EnumType.STRING)
    private RoleType role;
    @Enumerated(EnumType.STRING)
    private RoleSecondaire roleSecondaire;


}
