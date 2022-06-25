package com.computacenter.yocoma.service;

import com.computacenter.yocoma.domain.*; // for static metamodels
import com.computacenter.yocoma.domain.Team;
import com.computacenter.yocoma.repository.TeamRepository;
import com.computacenter.yocoma.service.criteria.TeamCriteria;
import java.util.List;
import java.util.Optional;
import javax.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;
import tech.jhipster.service.filter.StringFilter;

/**
 * Service for executing complex queries for {@link Team} entities in the database.
 * The main input is a {@link TeamCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Team} or a {@link Page} of {@link Team} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class TeamQueryService extends QueryService<Team> {

    private final Logger log = LoggerFactory.getLogger(TeamQueryService.class);

    private final TeamRepository teamRepository;

    public TeamQueryService(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    /**
     * Return a {@link List} of {@link Team} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Team> findByCriteria(TeamCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Team> specification = createSpecification(criteria);
        return teamRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Team} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Team> findByCriteria(TeamCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Team> specification = createSpecification(criteria);
        return teamRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(TeamCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Team> specification = createSpecification(criteria);
        return teamRepository.count(specification);
    }

    /**
     * Function to convert {@link TeamCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Team> createSpecification(TeamCriteria criteria) {
        Specification<Team> specification = Specification.where(null);
        if (criteria == null) {
            return specification;
        }
        // This has to be called first, because the distinct method returns null
        if (criteria.getDistinct() != null) {
            specification = specification.and(distinct(criteria.getDistinct()));
        }
        if (criteria.getId() != null) {
            specification = specification.and(buildRangeSpecification(criteria.getId(), Team_.id));
        }
        if (criteria.getName() != null) {
            specification = specification.and(buildStringSpecification(criteria.getName(), Team_.name));
        }
        if (criteria.getMotto() != null) {
            specification = specification.and(buildStringSpecification(criteria.getMotto(), Team_.motto));
        }
        if (criteria.getDescription() != null) {
            specification = specification.and(buildStringSpecification(criteria.getDescription(), Team_.description));
        }
        if (criteria.getTeamContactId() != null) {
            specification =
                specification.and(
                    buildSpecification(
                        criteria.getTeamContactId(),
                        root -> root.join(Team_.teamContacts, JoinType.LEFT).get(TeamContact_.id)
                    )
                );
        }
        // Build a specification for the search which covers the properties "name", "motto" and "description"
        if (criteria.getSearch() != null) {
            var containsName = buildStringSpecification(criteria.getSearch(), Team_.name);
            var containsMotto = buildStringSpecification(criteria.getSearch(), Team_.motto);
            var containsDesc = buildStringSpecification(criteria.getSearch(), Team_.description);
            specification = specification.and(containsName.or(containsMotto).or(containsDesc));
        }
        return specification;
    }
}
