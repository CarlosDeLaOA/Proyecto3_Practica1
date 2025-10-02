package com.project.demo.logic.entity.categories;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.demo.logic.entity.products.Products;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "categories")
public class Categories {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 255)
    private String description;

    @JsonIgnore
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Products> products;


    public Categories() {}

    public Long getId() {
        return id;

    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Products> getProducts() {return products;}
    public void setProducts(List<Products> products) {this.products = products;}
}