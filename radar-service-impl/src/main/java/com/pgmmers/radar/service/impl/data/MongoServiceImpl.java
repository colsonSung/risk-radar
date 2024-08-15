package com.pgmmers.radar.service.impl.data;

import com.pgmmers.radar.service.data.MongoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class MongoServiceImpl implements MongoService {

    @Resource
    private MongoTemplate mongoTemplate;

    @Override
    public MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }
}
