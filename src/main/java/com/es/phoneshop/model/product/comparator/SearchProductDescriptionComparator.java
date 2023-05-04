package com.es.phoneshop.model.product.comparator;

import com.es.phoneshop.model.product.Product;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;

public class SearchProductDescriptionComparator implements Comparator<Product> {
    private String[] searchWords;

    public SearchProductDescriptionComparator(String[] searchWords) {
        this.searchWords = searchWords;
    }

    @Override
    public int compare(Product product1, Product product2) {
        return Comparator.comparingLong((Product p) ->
                        getMatchedWordsCount(p.getDescription().toLowerCase(Locale.ROOT)))
                .thenComparing(new ProductDescriptionWordsCountComparator().reversed())
                .compare(product1, product2);
    }

    private long getMatchedWordsCount(String description) {
        return Arrays.stream(searchWords)
                .filter(description::contains)
                .count();
    }

}
