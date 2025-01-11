package com.example.demo.room.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.room.model.Room;

@Repository
public interface RoomRepository extends MongoRepository<Room, String> {
    
}