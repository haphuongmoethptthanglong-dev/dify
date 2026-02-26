package com.dify.iam.repository;

import com.dify.iam.entity.TenantAccountJoin;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TenantAccountJoinRepository extends JpaRepository<TenantAccountJoin, String> {

    Optional<TenantAccountJoin> findByTenantIdAndAccountId(String tenantId, String accountId);

    List<TenantAccountJoin> findByAccountId(String accountId);

    List<TenantAccountJoin> findByTenantId(String tenantId);

    Optional<TenantAccountJoin> findByTenantIdAndRole(String tenantId, String role);

    Optional<TenantAccountJoin> findByAccountIdAndCurrentTrue(String accountId);
}
