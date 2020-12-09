package com.nospace.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "folders")
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Folder {

    @Id
    private String id;

    @Column(name="full_route", nullable = false, unique = true)
    private String fullRoute;

    @Column(name="name", nullable = false)
    private String folderName;

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
    private List<Folder> subFolders = new ArrayList<>();

    @OneToMany(mappedBy = "containingFolder", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonManagedReference(value = "folder-files")
    private List<File> files = new ArrayList<>();

    public void addSubFolder(Folder subFolder){
        this.subFolders.add(subFolder);
    }

}
