package com.computacenter.yocoma.service;

import com.computacenter.yocoma.domain.TeamContact;
import com.computacenter.yocoma.repository.TeamContactRepository;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link TeamContact}.
 */
@Service
@Transactional
public class TeamContactService {

    private final Logger log = LoggerFactory.getLogger(TeamContactService.class);

    private final TeamContactRepository teamContactRepository;

    public TeamContactService(TeamContactRepository teamContactRepository) {
        this.teamContactRepository = teamContactRepository;
    }

    /**
     * Save a teamContact.
     *
     * @param teamContact the entity to save.
     * @return the persisted entity.
     */
    public TeamContact save(TeamContact teamContact) {
        log.debug("Request to save TeamContact : {}", teamContact);
        return teamContactRepository.save(teamContact);
    }

    /**
     * Update a teamContact.
     *
     * @param teamContact the entity to save.
     * @return the persisted entity.
     */
    public TeamContact update(TeamContact teamContact) {
        log.debug("Request to save TeamContact : {}", teamContact);
        return teamContactRepository.save(teamContact);
    }

    /**
     * Partially update a teamContact.
     *
     * @param teamContact the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<TeamContact> partialUpdate(TeamContact teamContact) {
        log.debug("Request to partially update TeamContact : {}", teamContact);

        return teamContactRepository
            .findById(teamContact.getId())
            .map(existingTeamContact -> {
                if (teamContact.getRoleType() != null) {
                    existingTeamContact.setRoleType(teamContact.getRoleType());
                }
                if (teamContact.getRole() != null) {
                    existingTeamContact.setRole(teamContact.getRole());
                }
                if (teamContact.getDescription() != null) {
                    existingTeamContact.setDescription(teamContact.getDescription());
                }

                return existingTeamContact;
            })
            .map(teamContactRepository::save);
    }

    /**
     * Get all the teamContacts.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<TeamContact> findAll() {
        log.debug("Request to get all TeamContacts");
        return teamContactRepository.findAll();
    }

    /**
     * Get one teamContact by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<TeamContact> findOne(Long id) {
        log.debug("Request to get TeamContact : {}", id);
        return teamContactRepository.findById(id);
    }

    /**
     * Delete the teamContact by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete TeamContact : {}", id);
        teamContactRepository.deleteById(id);
    }
}
