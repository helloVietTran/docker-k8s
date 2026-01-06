package com.vietanh.webmanh.dbs.mongo.repositories;

import com.vietanh.webmanh.dbs.mongo.models.ReadingHistory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReadingHistoryRepository extends MongoRepository<ReadingHistory, String> {

}