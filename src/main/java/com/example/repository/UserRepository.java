package com.example.repository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import com.example.model.User;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
@SuppressWarnings("rawtypes")
@Repository
public class UserRepository extends MainRepository<User> {
    public UserRepository() {
    }

    @Override
    protected String getDataPath() {
        return "src/main/java/com.example/data/users.json";
    }
    @Override
    protected Class<User[]> getArrayType() {
        return User[].class;
    }
    public Optional<User> findById(String id) {
        try {
            return findAll().stream()
                    .filter(user -> user.getId().equals(id))
                    .findFirst();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to read users.json");
        }
    }
    public void deleteById(String id) {
        try {
        ArrayList<User> users = findAll();
        users.removeIf(user -> user.getId().toString().equals(id));
        overrideData(users);}
        catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to delete users.json");
        }
    }


}


