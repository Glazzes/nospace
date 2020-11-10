package com.nospace.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "files")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class File {

    @Id
    private String id;
    private String filename;
    private Long fileSize;
    private String storedIn;
    private LocalDate uploadedAt;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn
    private User uploadedBy;
}
