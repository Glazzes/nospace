package com.nospace.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "folders")
@Data
@NoArgsConstructor
public class Folder implements Serializable {

    @Transient
    @JsonIgnoreProperties
    private final long serialVersionUid = 1984L;

    @Id
    private String id;

    @Column(name="name", nullable = false)
    private String folderName;

    @Column(name="full_route", nullable = false, unique = true)
    private String fullRoute;

    @Column(name = "depth", nullable = false)
    private long depth;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    @JsonBackReference(value = "user-folders")
    public User owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "base_folder_id")
    @JsonBackReference(value = "base-sub")
    private Folder baseFolder;

    @OneToMany(mappedBy = "baseFolder", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonManagedReference(value = "base-sub")
    private List<Folder> subFolders = new ArrayList<>(0);

    @OneToMany(mappedBy = "containingFolder", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonManagedReference(value = "folder-files")
    private List<File> files = new ArrayList<>(0);

    public Folder(String id, User owner){
        this.id = id;
        this.folderName = "root";
        this.baseFolder = null;
        this.fullRoute = String.format("%s-%s/", owner.getId(), "root");
        this.depth = 1;
        this.owner = owner;
    }

    public Folder(String id, String folderName, Folder baseFolder){
        this.id = id;
        this.folderName = folderName;
        this.baseFolder = baseFolder;
        this.fullRoute = String.format("%s%s/", baseFolder.getFullRoute(), folderName);
        this.depth = baseFolder.getDepth()+1;
        this.owner = baseFolder.getOwner();
    }
}
