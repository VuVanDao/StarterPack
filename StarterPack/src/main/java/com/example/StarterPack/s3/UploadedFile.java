package com.example.StarterPack.s3;

import java.time.LocalDateTime;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.hibernate.annotations.CreationTimestamp;

import com.example.StarterPack.entity.AbstractEntity;
import com.example.StarterPack.users.User;
import com.example.StarterPack.utils.Client;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Client
public class UploadedFile extends AbstractEntity{
    private String url;
    private Long size;
    private String originalFileName;
    private String extension;
    @CreationTimestamp
    private LocalDateTime createdAt;
    private LocalDateTime uploadedAt;

    @ManyToOne
    @JsonIgnore
    private User user;

    public UploadedFile(String originalFileName, Long size, User user) {
        this.originalFileName = originalFileName;
        this.size = size;
        this.user = user;
        this.extension = FilenameUtils.getExtension(originalFileName);
    }

    public void onUploaded(String url) {
        this.url = url;
        this.uploadedAt = LocalDateTime.now();
    }
    public String buildPath(String ...path) {
        StringBuilder sb = new StringBuilder();
        sb.append("user:").append(user.getId()).append("/");
        for (String p : path) {
            sb.append(p).append("/");
        }
        sb.append(UUID.randomUUID());
        sb.append(".").append(extension);
        return sb.toString();
    }
}
