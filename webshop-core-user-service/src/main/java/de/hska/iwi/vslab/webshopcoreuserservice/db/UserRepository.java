package de.hska.iwi.vslab.webshopcoreuserservice.db;

import de.hska.iwi.vslab.webshopcoreuserservice.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Integer> {
}
