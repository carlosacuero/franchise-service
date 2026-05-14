package com.practice.franquicias_management_api.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.practice.franquicias_management_api.domain.Franquicia;

public interface FranquiciaRepository extends ReactiveMongoRepository<Franquicia, String> {
}
