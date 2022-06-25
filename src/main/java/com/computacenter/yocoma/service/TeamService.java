package com.computacenter.yocoma.service;

import com.computacenter.yocoma.domain.Team;
import com.computacenter.yocoma.repository.TeamRepository;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Team}.
 */
@Service
@Transactional
public class TeamService {

    private final Logger log = LoggerFactory.getLogger(TeamService.class);

    private final TeamRepository teamRepository;

    public TeamService(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    /**
     * Save a team.
     *
     * @param team the entity to save.
     * @return the persisted entity.
     */
    public Team save(Team team) {
        log.debug("Request to save Team : {}", team);
        return teamRepository.save(team);
    }

    /**
     * Update a team.
     *
     * @param team the entity to save.
     * @return the persisted entity.
     */
    public Team update(Team team) {
        log.debug("Request to save Team : {}", team);
        return teamRepository.save(team);
    }

    /**
     * Partially update a team.
     *
     * @param team the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Team> partialUpdate(Team team) {
        log.debug("Request to partially update Team : {}", team);

        return teamRepository
            .findById(team.getId())
            .map(existingTeam -> {
                if (team.getName() != null) {
                    existingTeam.setName(team.getName());
                }
                if (team.getMotto() != null) {
                    existingTeam.setMotto(team.getMotto());
                }
                if (team.getLogo() != null) {
                    existingTeam.setLogo(team.getLogo());
                }
                if (team.getLogoContentType() != null) {
                    existingTeam.setLogoContentType(team.getLogoContentType());
                }
                if (team.getDescription() != null) {
                    existingTeam.setDescription(team.getDescription());
                }

                return existingTeam;
            })
            .map(teamRepository::save);
    }

    /**
     * Get all the teams.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<Team> findAll() {
        log.debug("Request to get all Teams");
        return teamRepository.findAll();
    }

    /**
     * Get one team by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Team> findOne(Long id) {
        log.debug("Request to get Team : {}", id);
        return teamRepository.findById(id);
    }

    /**
     * Delete the team by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Team : {}", id);
        teamRepository.deleteById(id);
    }
}
