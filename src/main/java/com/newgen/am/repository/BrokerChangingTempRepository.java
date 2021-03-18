package com.newgen.am.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.newgen.am.model.BrokerChangingTemp;

public interface BrokerChangingTempRepository extends MongoRepository<BrokerChangingTemp, String> {

}
