package com.gettimhired.repository;

import com.gettimhired.model.mongo.ChangeSet;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChangeSetRepository extends MongoRepository<ChangeSet, String> {
}
