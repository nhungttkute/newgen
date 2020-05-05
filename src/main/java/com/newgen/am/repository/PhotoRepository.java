package com.newgen.am.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.newgen.am.model.Photo;

public interface PhotoRepository extends MongoRepository<Photo, String> { }
