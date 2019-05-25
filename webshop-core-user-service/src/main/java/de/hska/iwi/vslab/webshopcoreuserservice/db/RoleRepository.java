package de.hska.iwi.vslab.webshopcoreuserservice.db;

import de.hska.iwi.vslab.webshopcoreuserservice.model.Role;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RoleRepository extends CrudRepository<Role, Integer> {

    Optional<Role> findByTypeEquals(String type);

}
