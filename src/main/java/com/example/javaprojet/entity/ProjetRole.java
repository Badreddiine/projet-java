package com.example.javaprojet.entity;

import com.example.javaprojet.enums.RoleSecondaire;
import com.example.javaprojet.enums.RoleType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.minidev.json.annotate.JsonIgnore;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
