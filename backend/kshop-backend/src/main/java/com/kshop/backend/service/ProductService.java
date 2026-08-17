/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kshop.backend.service;

import com.kshop.backend.entity.Product;
import com.kshop.backend.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 *
 * @author Iris-PC
 */


/**
 * Service métier pour la gestion des produits.
 *
 * Le Service se situe entre le Controller et le Repository :
 *
 * Controller
 *     ↓
 * ProductService
 *     ↓
 * ProductRepository
 *     ↓
 * PostgreSQL
 *
 * Le Controller ne communique donc pas directement
 * avec la base de données.
 */
@Service
@Transactional
public class ProductService {
    /*
     * Repository utilisé pour accéder à la table product.
     *
     * Spring injecte automatiquement cette dépendance
     * grâce à l'annotation @Service et au constructeur.
     */
    private final ProductRepository productRepository;

    /**
     * Injection du ProductRepository.
     *
     * L'injection par constructeur est recommandée
     * car elle rend la dépendance obligatoire.
     */
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Récupère tous les produits.
     *
     * @return liste de tous les produits
     */
    @Transactional(readOnly = true)
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    /**
     * Recherche un produit par son ID.
     *
     * Si le produit n'existe pas, une exception est levée.
     *
     * @param id identifiant du produit
     * @return produit trouvé
     */
    @Transactional(readOnly = true)
    public Product findById(Long id) {

        return productRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Produit introuvable avec l'id : " + id
                        )
                );
    }

    /**
     * Recherche un produit par son code.
     *
     * Exemple :
     *
     * PRD001
     *
     * @param code code du produit
     * @return produit trouvé
     */
    @Transactional(readOnly = true)
    public Product findByCode(String code) {

        return productRepository.findByCode(code)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Produit introuvable avec le code : " + code
                        )
                );
    }

    /**
     * Recherche un produit par son code-barres.
     *
     * Exemple :
     *
     * 6001234567890
     *
     * @param barcode code-barres
     * @return produit trouvé
     */
    @Transactional(readOnly = true)
    public Product findByBarcode(String barcode) {

        return productRepository.findByBarcode(barcode)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Produit introuvable avec le code-barres : "
                                        + barcode
                        )
                );
    }

    /**
     * Récupère tous les produits d'une catégorie.
     *
     * @param categoryId ID de la catégorie
     * @return liste des produits de la catégorie
     */
    @Transactional(readOnly = true)
    public List<Product> findByCategory(Long categoryId) {

        return productRepository.findByCategoryId(categoryId);
    }

    /**
     * Récupère uniquement les produits actifs.
     *
     * active = true
     *
     * @return liste des produits actifs
     */
    @Transactional(readOnly = true)
    public List<Product> findActiveProducts() {

        return productRepository.findByActiveTrue();
    }

    /**
     * Récupère les produits actifs d'une catégorie.
     *
     * @param categoryId ID de la catégorie
     * @return produits actifs de la catégorie
     */
    @Transactional(readOnly = true)
    public List<Product> findActiveProductsByCategory(Long categoryId) {

        return productRepository.findByCategoryIdAndActiveTrue(categoryId);
    }

    /**
     * Crée un nouveau produit.
     *
     * Avant l'enregistrement, nous vérifions :
     *
     * 1. Le code n'existe pas déjà.
     * 2. Le code-barres n'existe pas déjà s'il est renseigné.
     *
     * @param product produit à créer
     * @return produit enregistré
     */
    public Product create(Product product) {

        /*
         * Vérification du code produit.
         */
        if (product.getCode() == null|| product.getCode().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Le code du produit est obligatoire."
            );
        }

        /*
         * Vérification du nom du produit.
         */
        if (product.getName() == null| product.getName().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Le nom du produit est obligatoire."
            );
        }

        /*
         * Vérification de l'unicité du code.
         */
        if (productRepository.existsByCode(product.getCode())) {

            throw new IllegalArgumentException(
                    "Le code produit existe déjà : "
                            + product.getCode()
            );
        }

        /*
         * Le code-barres est facultatif.
         *
         * On vérifie son unicité uniquement
         * lorsqu'il est renseigné.
         */
        if (product.getBarcode() != null&& !product.getBarcode().trim().isEmpty()) {

            if (productRepository.existsByBarcode(product.getBarcode())) {

                throw new IllegalArgumentException(
                        "Le code-barres existe déjà : "
                                + product.getBarcode()
                );
            }
        }

        /*
         * Si active n'est pas renseigné,
         * le produit est actif par défaut.
         *
         * Cela correspond également à :
         *
         * private Boolean active = true;
         *
         * dans ton Entity Product.
         */
        if (product.getActive() == null) {
            product.setActive(true);
        }

        /*
         * Enregistrement dans PostgreSQL.
         */
        return productRepository.save(product);
    }

    /**
     * Modifie un produit existant.
     *
     * On récupère d'abord le produit existant
     * puis on met à jour ses informations.
     *
     * @param id ID du produit à modifier
     * @param product données du nouveau produit
     * @return produit modifié
     */
    public Product update(Long id, Product product) {

        /*
         * Recherche du produit existant.
         */
        Product existingProduct = findById(id);

        /*
         * Vérification du code.
         */
        if (product.getCode() == null|| product.getCode().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Le code du produit est obligatoire."
            );
        }

        /*
         * Vérification du nom.
         */
        if (product.getName() == null || product.getName().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Le nom du produit est obligatoire."
            );
        }

        /*
         * Si le code est modifié,
         * nous devons vérifier qu'il n'est pas
         * déjà utilisé par un autre produit.
         */
        if (!product.getCode().equals(existingProduct.getCode())
                && productRepository.existsByCode(product.getCode())) {

            throw new IllegalArgumentException(
                    "Le code produit existe déjà : "
                            + product.getCode()
            );
        }

        /*
         * Vérification du code-barres.
         *
         * Ici, pour rester simple et compatible
         * avec ton Repository actuel, on vérifie
         * uniquement lorsqu'il change.
         */
        if (product.getBarcode() != null
                && !product.getBarcode().trim().isEmpty()
                && !product.getBarcode().equals(existingProduct.getBarcode())
                && productRepository.existsByBarcode(product.getBarcode())) {

            throw new IllegalArgumentException(
                    "Le code-barres existe déjà : "
                            + product.getBarcode()
            );
        }

        /*
         * Mise à jour des champs correspondant
         * exactement à ton Product.java.
         */
        existingProduct.setCode(product.getCode());
        existingProduct.setBarcode(product.getBarcode());
        existingProduct.setName(product.getName());
        existingProduct.setDescription(product.getDescription());

        existingProduct.setPurchasePrice(
                product.getPurchasePrice()
        );

        existingProduct.setSellingPrice(
                product.getSellingPrice()
        );

        existingProduct.setQuantity(
                product.getQuantity()
        );

        existingProduct.setMinimumStock(
                product.getMinimumStock()
        );

        existingProduct.setUnit(
                product.getUnit()
        );

        existingProduct.setActive(
                product.getActive()
        );

        /*
         * Mise à jour de la catégorie.
         *
         * La catégorie reçue doit correspondre
         * à une Category existante.
         *
         * La validation de cette relation sera
         * renforcée dans le Category/Product Service.
         */
        existingProduct.setCategory(
                product.getCategory()
        );

        /*
         * Sauvegarde du produit modifié.
         */
        return productRepository.save(existingProduct);
    }

    /**
     * Supprime un produit.
     *
     * ATTENTION :
     * Cette méthode supprime réellement la ligne
     * de la table product.
     *
     * Pour un système commercial, nous utiliserons
     * probablement plus tard la désactivation
     * avec active = false plutôt que la suppression.
     *
     * @param id ID du produit
     */
    public void delete(Long id) {

        /*
         * Vérifie d'abord que le produit existe.
         */
        Product product = findById(id);

        /*
         * Suppression.
         */
        productRepository.delete(product);
    }

    /**
     * Désactive un produit sans le supprimer
     * de la base de données.
     *
     * C'est généralement préférable pour
     * conserver l'historique des ventes.
     *
     * @param id ID du produit
     * @return produit désactivé
     */
    public Product deactivate(Long id) {

        Product product = findById(id);

        product.setActive(false);

        return productRepository.save(product);
    }

    /**
     * Réactive un produit.
     *
     * @param id ID du produit
     * @return produit réactivé
     */
    public Product activate(Long id) {

        Product product = findById(id);

        product.setActive(true);

        return productRepository.save(product);
    }

    /**
     * Vérifie si un produit est en stock faible.
     *
     * Exemple :
     *
     * quantity = 5
     * minimumStock = 10
     *
     * 5 <= 10
     *
     * Donc le produit est en stock faible.
     *
     * @param product produit à vérifier
     * @return true si le stock est faible
     */
    public boolean isLowStock(Product product) {

        /*
         * Si quantity ou minimumStock n'est pas défini,
         * nous ne pouvons pas déterminer le niveau du stock.
         */
        if (product.getQuantity() == null
                || product.getMinimumStock() == null) {

            return false;
        }

        return product.getQuantity()
                .compareTo(product.getMinimumStock()) <= 0;
    }
}
