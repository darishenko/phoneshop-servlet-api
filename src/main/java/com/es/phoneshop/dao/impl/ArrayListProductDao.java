package com.es.phoneshop.dao.impl;

import com.es.phoneshop.dao.ProductDao;
import com.es.phoneshop.enam.search.AcceptanceCriteria;
import com.es.phoneshop.exception.product.DuplicateProductException;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductMapper;
import com.es.phoneshop.enam.sort.SortField;
import com.es.phoneshop.enam.sort.SortOrder;
import com.es.phoneshop.model.product.comparator.SearchProductDescriptionComparator;

import java.math.BigDecimal;
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
    public List<Product> findProducts(String query, SortField sortField, SortOrder sortOrder,
                                      BigDecimal minPrice, BigDecimal maxPrice, AcceptanceCriteria criteria) {
        lock.readLock().lock();
        try {
            List<String> queryWords = splitWords(query);
            Comparator<Product> productComparator = getProductSortingComparator(queryWords, sortField, sortOrder);

            return items.stream()
                    .filter(product -> product.getPrice() != null)
                    .filter(product -> product.getStock() > 0)
                    .filter(product -> minPrice == null
                            || product.getPrice().doubleValue() >= minPrice.doubleValue())
                    .filter(product -> maxPrice == null
                            || product.getPrice().doubleValue() <= maxPrice.doubleValue())
                    .filter(product -> queryWords == null
                            || containsWordsFromQueryByAcceptanceCriteria(product, queryWords, criteria))
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

    private boolean containsWordsFromQueryByAcceptanceCriteria(Product product, List<String> keyWords,
                                                               AcceptanceCriteria criteria) {
        String text = product.getDescription().toLowerCase(Locale.ROOT);
        if (Objects.isNull(criteria)){
            return containsWordsPartsFromQuery(text, keyWords);
        }
        List<String> descriptionWords = splitWords(text);
        switch (criteria) {
            case any_word -> {
                return containsAnyWordFromQuery(descriptionWords, keyWords);
            }
            case all_words -> {
                return containsAllWordsFromQuery(descriptionWords, keyWords);
            }
        }
        return containsWordsPartsFromQuery(text, keyWords);
    }

    private boolean containsAnyWordFromQuery(List<String> words, List<String> keywords) {
        return words.stream()
                .anyMatch(keywords::contains);
    }

    private boolean containsAllWordsFromQuery(List<String> words, List<String> keywords) {
        return words.containsAll(keywords);
    }

    private boolean containsWordsPartsFromQuery(String text, List<String> keywords) {
        return keywords.stream()
                .anyMatch(text::contains);
    }

    private List<String> splitWords(String text) {
        if (text != null && !text.isEmpty()) {
            return Arrays.stream(text.trim().toLowerCase(Locale.ROOT).split("\\s+")).toList();
        }
        return null;
    }

    private Comparable getProductSortField(Product product, SortField sortField) {
        return switch (sortField) {
            case description -> product.getDescription();
            case price -> product.getPrice();
        };
    }

    private Comparator<Product> getProductSortingComparator(List<String> queryWords, SortField sortField, SortOrder sortOrder) {
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