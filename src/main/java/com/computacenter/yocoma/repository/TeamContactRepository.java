package com.computacenter.yocoma.repository;

import com.computacenter.yocoma.domain.TeamContact;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the TeamContact entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TeamContactRepository extends JpaRepository<TeamContact, Long> {}
