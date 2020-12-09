package com.nospace.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.nospace.security.permisions.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
public class User {

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
    private List<Folder> folders = new ArrayList<>();

}
