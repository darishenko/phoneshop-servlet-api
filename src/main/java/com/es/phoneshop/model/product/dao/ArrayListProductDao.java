package com.es.phoneshop.model.product.dao;

import com.es.phoneshop.exception.DuplicateProductException;
import com.es.phoneshop.exception.ProductNotFoundException;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductMapper;
import com.es.phoneshop.model.product.SortField;
import com.es.phoneshop.model.product.SortOrder;
import com.es.phoneshop.model.product.comparator.SearchProductDescriptionComparator;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class ArrayListProductDao implements ProductDao {
    private static volatile ProductDao instance;
    private final List<Product> products;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private long maxId;

    private ArrayListProductDao() {
        products = new ArrayList<>();
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
    public Product getProduct(Long id) {
        Objects.requireNonNull(id);

        lock.readLock().lock();
        try {
            return products.stream()
                    .filter(product -> id.equals(product.getId()))
                    .findAny()
                    .orElseThrow(() -> new ProductNotFoundException(id));
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public List<Product> findProducts(String query, SortField sortField, SortOrder sortOrder) {
        lock.readLock().lock();
        try {
            String[] queryWords = splitQueryWords(query);
            Comparator<Product> productComparator = getProductSortingComparator(queryWords, sortField, sortOrder);

            return products.stream()
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
            Long productId = product.getId();
            if (productId != null) {
                Product currentProduct = getProduct(productId);
                ProductMapper.updateProduct(currentProduct, product);
            } else {
                products.stream()
                        .filter(p -> p.equals(product))
                        .findAny()
                        .ifPresent(p -> {
                            throw new DuplicateProductException();
                        });
                product.setId(++maxId);
                products.add(product);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void delete(Long id) {
        Objects.requireNonNull(id);

        lock.writeLock().lock();
        try {
            products.stream()
                    .filter(product -> id.equals(product.getId()))
                    .findAny()
                    .ifPresent(products::remove);
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