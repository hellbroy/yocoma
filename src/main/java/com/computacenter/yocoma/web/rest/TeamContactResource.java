package com.computacenter.yocoma.web.rest;

import com.computacenter.yocoma.domain.TeamContact;
import com.computacenter.yocoma.repository.TeamContactRepository;
import com.computacenter.yocoma.service.TeamContactQueryService;
import com.computacenter.yocoma.service.TeamContactService;
import com.computacenter.yocoma.service.criteria.TeamContactCriteria;
import com.computacenter.yocoma.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.computacenter.yocoma.domain.TeamContact}.
 */
@RestController
@RequestMapping("/api")
public class TeamContactResource {

    private final Logger log = LoggerFactory.getLogger(TeamContactResource.class);

    private static final String ENTITY_NAME = "teamContact";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TeamContactService teamContactService;

    private final TeamContactRepository teamContactRepository;

    private final TeamContactQueryService teamContactQueryService;

    public TeamContactResource(
        TeamContactService teamContactService,
        TeamContactRepository teamContactRepository,
        TeamContactQueryService teamContactQueryService
    ) {
        this.teamContactService = teamContactService;
        this.teamContactRepository = teamContactRepository;
        this.teamContactQueryService = teamContactQueryService;
    }

    /**
     * {@code POST  /team-contacts} : Create a new teamContact.
     *
     * @param teamContact the teamContact to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new teamContact, or with status {@code 400 (Bad Request)} if the teamContact has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/team-contacts")
    public ResponseEntity<TeamContact> createTeamContact(@Valid @RequestBody TeamContact teamContact) throws URISyntaxException {
        log.debug("REST request to save TeamContact : {}", teamContact);
        if (teamContact.getId() != null) {
            throw new BadRequestAlertException("A new teamContact cannot already have an ID", ENTITY_NAME, "idexists");
        }
        TeamContact result = teamContactService.save(teamContact);
        return ResponseEntity
            .created(new URI("/api/team-contacts/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /team-contacts/:id} : Updates an existing teamContact.
     *
     * @param id the id of the teamContact to save.
     * @param teamContact the teamContact to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated teamContact,
     * or with status {@code 400 (Bad Request)} if the teamContact is not valid,
     * or with status {@code 500 (Internal Server Error)} if the teamContact couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/team-contacts/{id}")
    public ResponseEntity<TeamContact> updateTeamContact(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TeamContact teamContact
    ) throws URISyntaxException {
        log.debug("REST request to update TeamContact : {}, {}", id, teamContact);
        if (teamContact.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, teamContact.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!teamContactRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        TeamContact result = teamContactService.update(teamContact);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, teamContact.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /team-contacts/:id} : Partial updates given fields of an existing teamContact, field will ignore if it is null
     *
     * @param id the id of the teamContact to save.
     * @param teamContact the teamContact to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated teamContact,
     * or with status {@code 400 (Bad Request)} if the teamContact is not valid,
     * or with status {@code 404 (Not Found)} if the teamContact is not found,
     * or with status {@code 500 (Internal Server Error)} if the teamContact couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/team-contacts/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TeamContact> partialUpdateTeamContact(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TeamContact teamContact
    ) throws URISyntaxException {
        log.debug("REST request to partial update TeamContact partially : {}, {}", id, teamContact);
        if (teamContact.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, teamContact.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!teamContactRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TeamContact> result = teamContactService.partialUpdate(teamContact);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, teamContact.getId().toString())
        );
    }

    /**
     * {@code GET  /team-contacts} : get all the teamContacts.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of teamContacts in body.
     */
    @GetMapping("/team-contacts")
    public ResponseEntity<List<TeamContact>> getAllTeamContacts(TeamContactCriteria criteria) {
        log.debug("REST request to get TeamContacts by criteria: {}", criteria);
        List<TeamContact> entityList = teamContactQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /team-contacts/count} : count all the teamContacts.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/team-contacts/count")
    public ResponseEntity<Long> countTeamContacts(TeamContactCriteria criteria) {
        log.debug("REST request to count TeamContacts by criteria: {}", criteria);
        return ResponseEntity.ok().body(teamContactQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /team-contacts/:id} : get the "id" teamContact.
     *
     * @param id the id of the teamContact to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the teamContact, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/team-contacts/{id}")
    public ResponseEntity<TeamContact> getTeamContact(@PathVariable Long id) {
        log.debug("REST request to get TeamContact : {}", id);
        Optional<TeamContact> teamContact = teamContactService.findOne(id);
        return ResponseUtil.wrapOrNotFound(teamContact);
    }

    /**
     * {@code DELETE  /team-contacts/:id} : delete the "id" teamContact.
     *
     * @param id the id of the teamContact to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/team-contacts/{id}")
    public ResponseEntity<Void> deleteTeamContact(@PathVariable Long id) {
        log.debug("REST request to delete TeamContact : {}", id);
        teamContactService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
