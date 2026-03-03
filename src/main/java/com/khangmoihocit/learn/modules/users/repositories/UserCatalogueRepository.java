package com.khangmoihocit.learn.modules.users.repositories;

import com.khangmoihocit.learn.modules.users.entities.UserCatalogue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserCatalogueRepository extends JpaRepository<UserCatalogue, Long> {

}
