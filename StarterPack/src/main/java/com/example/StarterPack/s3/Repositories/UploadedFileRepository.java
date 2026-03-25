package com.example.StarterPack.s3.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.StarterPack.s3.UploadedFile;

public interface UploadedFileRepository extends JpaRepository<UploadedFile, Long>{
    
}
