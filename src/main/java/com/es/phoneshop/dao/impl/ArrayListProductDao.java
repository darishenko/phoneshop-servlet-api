package com.es.phoneshop.dao.impl;

import com.es.phoneshop.dao.ProductDao;
import com.es.phoneshop.exception.product.DuplicateProductException;
import com.es.phoneshop.exception.product.ProductNotFoundException;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductMapper;
import com.es.phoneshop.enam.sort.SortField;
import com.es.phoneshop.enam.sort.SortOrder;
import com.es.phoneshop.model.product.comparator.SearchProductDescriptionComparator;

import java.util.*;
import java.util.stream.Collectors;

public class ArrayListProductDao extends ArrayListGenericDao<Product> implements ProductDao {
    private static volatile ProductDao instance;

    private ArrayListProductDao() {
        super(Product.class);
    }

    public static ProductDao getInstance() {
        if (instance == null) {
            synchronized (ArrayListProductDao.class) {
                if (instance == null) {
                    instance = new ArrayListProductDao();
                }
            }
        }
        return instance;
    }

    @Override
    public List<Product> findProducts(String query, SortField sortField, SortOrder sortOrder) {
        lock.readLock().lock();
        try {
            String[] queryWords = splitQueryWords(query);
            Comparator<Product> productComparator = getProductSortingComparator(queryWords, sortField, sortOrder);

            return items.stream()
                    .filter(product -> product.getPrice() != null)
                    .filter(product -> product.getStock() > 0)
                    .filter(product -> queryWords == null
                            || containsAnyWordFromQuery(product.getDescription().toLowerCase(Locale.ROOT), queryWords))
                    .sorted(productComparator)
                    .collect(Collectors.toList());
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void save(Product product) {
        Objects.requireNonNull(product);

        lock.writeLock().lock();
        try {
            UUID productId = product.getId();
            if (productId != null) {
                Product currentProduct = getItem(productId);
                ProductMapper.updateProduct(currentProduct, product);
            } else {
                items.stream()
                        .filter(p -> p.equals(product))
                        .findAny()
                        .ifPresent(p -> {
                            throw new DuplicateProductException();
                        });
                product.setId(UUID.randomUUID());
                items.add(product);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void updateProductStock(UUID productId, int stock) {
        Product product = getItem(productId);

        lock.writeLock().lock();
        try {
            product.setStock(stock);
        } finally {
            lock.writeLock().unlock();
        }
    }

    private boolean containsAnyWordFromQuery(String text, String[] keyWords) {
        return Arrays.stream(keyWords)
                .anyMatch(text::contains);
    }

    private String[] splitQueryWords(String query) {
        if (query != null && !query.isEmpty()) {
            return query.trim().toLowerCase(Locale.ROOT).split("\\s+");
        }
        return null;
    }

    private Comparable getProductSortField(Product product, SortField sortField) {
        return switch (sortField) {
            case description -> product.getDescription();
            case price -> product.getPrice();
        };
    }

    private Comparator<Product> getProductSortingComparator(String[] queryWords, SortField sortField, SortOrder sortOrder) {
        Comparator<Product> productComparator = Comparator.comparing(product -> 0);
        if (sortField != null && sortOrder != null) {
            productComparator = Comparator.comparing(product -> getProductSortField(product, sortField));
            if (SortOrder.desc == sortOrder) {
                productComparator = productComparator.reversed();
            }
        } else if (queryWords != null) {
            productComparator = new SearchProductDescriptionComparator(queryWords).reversed();
        }
        return productComparator;
    }

}