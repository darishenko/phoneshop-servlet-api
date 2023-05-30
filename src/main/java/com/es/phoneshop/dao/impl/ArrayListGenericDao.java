package com.es.phoneshop.dao.impl;

import com.es.phoneshop.dao.GenericDao;
import com.es.phoneshop.exception.factory.ItemNotFoundExceptionFactory;
import com.es.phoneshop.model.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public abstract class ArrayListGenericDao<T extends Item> implements GenericDao<T> {
    protected ReadWriteLock lock;
    protected List<T> items;
    private final ItemNotFoundExceptionFactory itemNotFoundExceptionFactory;
    private final Class<T> itemClass;

    public ArrayListGenericDao (Class<T> itemClass){
        lock = new ReentrantReadWriteLock();
        items = new ArrayList<>();
        this.itemClass = itemClass;
        itemNotFoundExceptionFactory = ItemNotFoundExceptionFactory.getInstance();
    }

    @Override
    public T getItem(UUID id) {
        Objects.requireNonNull(id);

        lock.readLock().lock();
        try {
            return items.stream()
                    .filter(item -> id.equals(item.getId()))
                    .findAny()
                    .orElseThrow(() -> itemNotFoundExceptionFactory.createItemNotFoundException(itemClass, id));
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void save(T item) {
        Objects.requireNonNull(item);

        lock.writeLock().lock();
        try {
            item.setId(UUID.randomUUID());
            items.add(item);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void delete(UUID itemId) {
        Objects.requireNonNull(itemId);

        lock.writeLock().lock();
        try {
            items.stream()
                    .filter(item -> itemId.equals(item.getId()))
                    .findAny()
                    .ifPresent(items::remove);
        } finally {
            lock.writeLock().unlock();
        }
    }

}
