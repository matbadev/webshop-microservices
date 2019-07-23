package de.hska.iwi.vslab.webshopcoreuserservice.db;

import de.hska.iwi.vslab.webshopcoreuserservice.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Integer> {

    Optional<User> findByUsername(String username);

}
