package com.gs.spider.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.repository.Repository;
import org.springframework.data.util.CloseableIterator;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2017</p>
 * <p>Company: Voyageone Inc.</p>
 *
 * @author holysky.zhao 2017/11/8 17:21
 * @version 1.0
 */
public abstract class BaseMongoDao<T, ID extends Serializable> implements Repository<T, ID> {

    private Class<T> entityClass;

    @Autowired
    private MongoTemplate mongoTemplate;

    public BaseMongoDao() {
        this.entityClass =  (Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];;
    }

    public MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }

    public List<T> find(final Query query) {
        return mongoTemplate.find(query, entityClass);
    }

    public T findOne(final Query query) {
        return mongoTemplate.findOne(query, entityClass);
    }

    public void update(final Query query, final Update update) {
        mongoTemplate.findAndModify(query, update, entityClass);
    }

    public T save(final T entity) {
        mongoTemplate.save(entity);
        return entity;
    }

    public T findById(final String id) {
        return mongoTemplate.findById(id, entityClass);
    }

    public T findById(final String id, final String collectionName) {
        return mongoTemplate.findById(id, entityClass, collectionName);
    }

    public Page<T> findPage(final Pageable pageable) {
        long count = mongoTemplate.count(new BasicQuery("{}"), entityClass);
        List<T> list = mongoTemplate.find(new Query().with(pageable),entityClass);
        return new PageImpl<T>(list, pageable, count);

    }

    public CloseableIterator<T> stream(final Query query) {
        return mongoTemplate.stream(query, entityClass);
    }

    public CloseableIterator<T> stream() {
        return mongoTemplate.stream(new BasicQuery("{}"), entityClass);
    }

    public long count(final Query query) {
        return mongoTemplate.count(query, entityClass);
    }

    public void delete(final Query query) {
        mongoTemplate.remove(query, entityClass);
    }


}
