package com.computacenter.yocoma.service;

import com.computacenter.yocoma.domain.*; // for static metamodels
import com.computacenter.yocoma.domain.Contact;
import com.computacenter.yocoma.repository.ContactRepository;
import com.computacenter.yocoma.service.criteria.ContactCriteria;
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
 * Service for executing complex queries for {@link Contact} entities in the database.
 * The main input is a {@link ContactCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Contact} or a {@link Page} of {@link Contact} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ContactQueryService extends QueryService<Contact> {

    private final Logger log = LoggerFactory.getLogger(ContactQueryService.class);

    private final ContactRepository contactRepository;

    public ContactQueryService(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    /**
     * Return a {@link List} of {@link Contact} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Contact> findByCriteria(ContactCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Contact> specification = createSpecification(criteria);
        return contactRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Contact} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Contact> findByCriteria(ContactCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Contact> specification = createSpecification(criteria);
        return contactRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ContactCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Contact> specification = createSpecification(criteria);
        return contactRepository.count(specification);
    }

    /**
     * Function to convert {@link ContactCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Contact> createSpecification(ContactCriteria criteria) {
        Specification<Contact> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Contact_.id));
            }
            if (criteria.getFirstname() != null) {
                specification = specification.and(buildStringSpecification(criteria.getFirstname(), Contact_.firstname));
            }
            if (criteria.getLastname() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLastname(), Contact_.lastname));
            }
            if (criteria.getEmail() != null) {
                specification = specification.and(buildStringSpecification(criteria.getEmail(), Contact_.email));
            }
            if (criteria.getPhone() != null) {
                specification = specification.and(buildStringSpecification(criteria.getPhone(), Contact_.phone));
            }
            if (criteria.getRemark() != null) {
                specification = specification.and(buildStringSpecification(criteria.getRemark(), Contact_.remark));
            }
            if (criteria.getTeamContactId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getTeamContactId(),
                            root -> root.join(Contact_.teamContacts, JoinType.LEFT).get(TeamContact_.id)
                        )
                    );
            }
            // Build a specification for the search which covers the properties "firstname", "lastname" and "remark"
            if (criteria.getSearch() != null) {
                var containsFirstname = buildStringSpecification(criteria.getSearch(), Contact_.firstname);
                var containsLastname = buildStringSpecification(criteria.getSearch(), Contact_.lastname);
                var containsRemark = buildStringSpecification(criteria.getSearch(), Contact_.remark);
                specification = specification.and(containsFirstname.or(containsLastname).or(containsRemark));
            }
        }
        return specification;
    }
}
