package com.nospace.repository;

import com.nospace.entities.Folder;
import com.nospace.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FolderRepository extends JpaRepository<Folder, String> {
    Optional<Folder> findByFullRouteAndOwner(String name, User user);
    Optional<Folder> findByDepthAndFolderNameAndOwner(long depth, String folderName, User owner);
}
