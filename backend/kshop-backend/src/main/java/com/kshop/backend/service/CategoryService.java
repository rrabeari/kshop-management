/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kshop.backend.service;

import com.kshop.backend.entity.Category;
import com.kshop.backend.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 * @author Iris-PC
 */

/**
 * Service métier responsable de la gestion des catégories.
 *
 * Le Controller appelle ce Service.
 *
 * Le Service appelle ensuite le Repository pour communiquer
 * avec PostgreSQL.
 *
 * Architecture :
 *
 * Controller
 *      ↓
 * CategoryService
 *      ↓
 * CategoryRepository
 *      ↓
 * PostgreSQL
 */
@Service
@RequiredArgsConstructor
public class CategoryService {
    /*
     * Repository permettant d'accéder à la table category.
     *
     * @RequiredArgsConstructor de Lombok crée automatiquement
     * le constructeur nécessaire à l'injection de dépendance.
     */
    private final CategoryRepository categoryRepository;


    /**
     * Récupère toutes les catégories.
     * SELECT * FROM category;
     */
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }


    /**
     * Recherche une catégorie par son identifiant.
     *
     * @param id identifiant de la catégorie
     * @return catégorie trouvée
     *
     * Si aucune catégorie n'existe avec cet ID,
     * une exception est générée.
     */
    public Category findById(Long id) {

        return categoryRepository.findById(id).orElseThrow(() ->
                        new RuntimeException(
                                "Catégorie introuvable avec l'id : " + id
                        )
                );
    }


    /**
     * Crée une nouvelle catégorie.
     *
     * Avant l'enregistrement, on vérifie que le nom
     * n'existe pas déjà.
     *
     * @param category catégorie à créer
     * @return catégorie enregistrée
     */
    public Category create(Category category) {

        /*
         * Vérification du nom.
         */
        if (category.getName() == null ||category.getName().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Le nom de la catégorie est obligatoire."
            );
        }

        /*
         * Nettoyage du nom.
         *
         * Exemple :
         *
         * "  Boissons  "
         *
         * devient :
         *
         * "Boissons"
         */
        category.setName(category.getName().trim());


        /*
         * Vérification des doublons.
         */
        if (categoryRepository.existsByNameIgnoreCase(category.getName())) {

            throw new IllegalArgumentException(
                    "Une catégorie avec ce nom existe déjà."
            );
        }


        /*
         * Enregistrement dans PostgreSQL.
         */
        return categoryRepository.save(category);
    }


    /**
     * Modifie une catégorie existante.
     *
     * @param id identifiant de la catégorie
     * @param category nouvelles données
     * @return catégorie mise à jour
     */
    public Category update(Long id, Category category) {

        /*
         * On vérifie d'abord que la catégorie existe.
         */
        Category existingCategory = findById(id);


        /*
         * Vérification du nouveau nom.
         */
        if (category.getName() == null ||category.getName().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Le nom de la catégorie est obligatoire."
            );
        }


        String newName = category.getName().trim();


        /*
         * Vérification d'un éventuel doublon.
         *
         * On autorise évidemment la catégorie
         * à conserver son propre nom.
         */
        categoryRepository.findByNameIgnoreCase(newName).ifPresent(foundCategory -> {

                    if (!foundCategory.getId().equals(id)) {

                        throw new IllegalArgumentException(
                                "Une autre catégorie utilise déjà ce nom."
                        );
                    }
                });


        /*
         * Mise à jour des informations.
         *
         * On conserve l'ID existant.
         */
        existingCategory.setName(newName);


        /*
         * Si ton entité Category possède une description,
         * on la met également à jour.
         */
        existingCategory.setDescription(
                category.getDescription()
        );


        /*
         * Enregistrement de la modification.
         */
        return categoryRepository.save(existingCategory);
    }


    /**
     * Supprime une catégorie.
     *
     * @param id identifiant de la catégorie
     */
    public void delete(Long id) {

        /*
         * Vérification de l'existence avant suppression.
         */
        Category category = findById(id);


        /*
         * Suppression.
         */
        categoryRepository.delete(category);
    }
}
