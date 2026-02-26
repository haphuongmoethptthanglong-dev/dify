package com.dify.iam.repository;

import com.dify.iam.entity.TenantAccountJoin;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TenantAccountJoinRepository extends JpaRepository<TenantAccountJoin, UUID> {

    Optional<TenantAccountJoin> findByTenantIdAndAccountId(UUID tenantId, UUID accountId);

    List<TenantAccountJoin> findByAccountId(UUID accountId);

    List<TenantAccountJoin> findByTenantId(UUID tenantId);

    Optional<TenantAccountJoin> findByTenantIdAndRole(UUID tenantId, String role);

    Optional<TenantAccountJoin> findByAccountIdAndCurrentTrue(UUID accountId);
}
