package com.vietanh.webmanh.dbs.mongo.repositories;

import com.vietanh.webmanh.dbs.mongo.models.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends MongoRepository<Comment, String> {

}