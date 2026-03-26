package com.platformzeta.storage.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @Entity
@Table(name = "stored_files")
public class StoredFile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "email")
    private String email;

    @Column(name = "file_title")
    private String fileTitle;

    @Column(name = "file_description", length = 500)
    private String fileDescription;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_extension", length = 50)
    private String fileExtension;

    @Column(name = "file_data")
    private byte[] fileData;

}