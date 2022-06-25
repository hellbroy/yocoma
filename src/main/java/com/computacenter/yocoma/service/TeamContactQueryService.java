package com.computacenter.yocoma.service;

import com.computacenter.yocoma.domain.*; // for static metamodels
import com.computacenter.yocoma.domain.TeamContact;
import com.computacenter.yocoma.repository.TeamContactRepository;
import com.computacenter.yocoma.service.criteria.TeamContactCriteria;
import java.util.List;
import javax.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link TeamContact} entities in the database.
 * The main input is a {@link TeamContactCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link TeamContact} or a {@link Page} of {@link TeamContact} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class TeamContactQueryService extends QueryService<TeamContact> {

    private final Logger log = LoggerFactory.getLogger(TeamContactQueryService.class);

    private final TeamContactRepository teamContactRepository;

    public TeamContactQueryService(TeamContactRepository teamContactRepository) {
        this.teamContactRepository = teamContactRepository;
    }

    /**
     * Return a {@link List} of {@link TeamContact} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<TeamContact> findByCriteria(TeamContactCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<TeamContact> specification = createSpecification(criteria);
        return teamContactRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link TeamContact} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<TeamContact> findByCriteria(TeamContactCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<TeamContact> specification = createSpecification(criteria);
        return teamContactRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(TeamContactCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<TeamContact> specification = createSpecification(criteria);
        return teamContactRepository.count(specification);
    }

    /**
     * Function to convert {@link TeamContactCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<TeamContact> createSpecification(TeamContactCriteria criteria) {
        Specification<TeamContact> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), TeamContact_.id));
            }
            if (criteria.getRoleType() != null) {
                specification = specification.and(buildSpecification(criteria.getRoleType(), TeamContact_.roleType));
            }
            if (criteria.getRole() != null) {
                specification = specification.and(buildStringSpecification(criteria.getRole(), TeamContact_.role));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), TeamContact_.description));
            }
            if (criteria.getContactId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getContactId(), root -> root.join(TeamContact_.contact, JoinType.LEFT).get(Contact_.id))
                    );
            }
            if (criteria.getTeamId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getTeamId(), root -> root.join(TeamContact_.team, JoinType.LEFT).get(Team_.id))
                    );
            }
            // Build a specification for the search which covers the properties "role" and "description"
            if (criteria.getSearch() != null) {
                var containsRole = buildStringSpecification(criteria.getSearch(), TeamContact_.role);
                var containsDesc = buildStringSpecification(criteria.getSearch(), TeamContact_.description);
                specification = specification.and(containsRole.or(containsDesc));
            }
        }
        return specification;
    }
}
