package com.dify.iam.repository;

import com.dify.iam.entity.DifySetup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DifySetupRepository extends JpaRepository<DifySetup, String> {
}
