package com.nospace.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.nospace.model.NewAccountRequest;
import com.nospace.security.permisions.Role;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "users")
@NoArgsConstructor
public class User implements Serializable {

    @Transient
    @JsonIgnoreProperties
    private final long serialVersionUid = 1984L;

    @Id
    private String id;

    @Column(name = "username", length = 50, nullable = false, unique = true)
    private String username;

    @Column(name = "nickname", length = 50, nullable = false)
    private String nickname;

    @Column(name = "password", length = 100, nullable = false)
    private String password;

    @Column(name = "email", length = 100, nullable = false, unique = true)
    private String email;

    @Column(name = "member_since", nullable = false)
    private LocalDate memberSince;

    @Column(name = "profile_picture", nullable = false)
    private String profilePicture;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "active", nullable = false)
    private boolean active;

    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonManagedReference(value = "user-folders")
    private List<Folder> folders = new ArrayList<>(0);

    public User(String id, String encodedPassword, String defaultProfilePicture, NewAccountRequest request){
        this.id = id;
        this.username = request.getUsername();
        this.nickname = request.getUsername();
        this.password = encodedPassword;
        this.email = request.getEmail();
        this.memberSince = LocalDate.now();
        this.role = Role.USER;
        this.active = false;
        this.profilePicture = defaultProfilePicture;
    }
}
