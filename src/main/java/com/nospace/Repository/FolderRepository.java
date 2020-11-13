package com.nospace.Repository;

import com.nospace.entities.Folder;
import com.nospace.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FolderRepository extends JpaRepository<Folder, String> {
    Optional<Folder> findByNameAndOwner(String name, User user);
}
