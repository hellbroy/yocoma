package com.computacenter.yocoma.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.computacenter.yocoma.IntegrationTest;
import com.computacenter.yocoma.domain.Team;
import com.computacenter.yocoma.domain.TeamContact;
import com.computacenter.yocoma.repository.TeamRepository;
import com.computacenter.yocoma.service.criteria.TeamCriteria;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;

/**
 * Integration tests for the {@link TeamResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TeamResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_MOTTO = "AAAAAAAAAA";
    private static final String UPDATED_MOTTO = "BBBBBBBBBB";

    private static final byte[] DEFAULT_LOGO = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_LOGO = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_LOGO_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_LOGO_CONTENT_TYPE = "image/png";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/teams";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTeamMockMvc;

    private Team team;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Team createEntity(EntityManager em) {
        Team team = new Team()
            .name(DEFAULT_NAME)
            .motto(DEFAULT_MOTTO)
            .logo(DEFAULT_LOGO)
            .logoContentType(DEFAULT_LOGO_CONTENT_TYPE)
            .description(DEFAULT_DESCRIPTION);
        return team;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Team createUpdatedEntity(EntityManager em) {
        Team team = new Team()
            .name(UPDATED_NAME)
            .motto(UPDATED_MOTTO)
            .logo(UPDATED_LOGO)
            .logoContentType(UPDATED_LOGO_CONTENT_TYPE)
            .description(UPDATED_DESCRIPTION);
        return team;
    }

    @BeforeEach
    public void initTest() {
        team = createEntity(em);
    }

    @Test
    @Transactional
    void createTeam() throws Exception {
        int databaseSizeBeforeCreate = teamRepository.findAll().size();
        // Create the Team
        restTeamMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(team)))
            .andExpect(status().isCreated());

        // Validate the Team in the database
        List<Team> teamList = teamRepository.findAll();
        assertThat(teamList).hasSize(databaseSizeBeforeCreate + 1);
        Team testTeam = teamList.get(teamList.size() - 1);
        assertThat(testTeam.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testTeam.getMotto()).isEqualTo(DEFAULT_MOTTO);
        assertThat(testTeam.getLogo()).isEqualTo(DEFAULT_LOGO);
        assertThat(testTeam.getLogoContentType()).isEqualTo(DEFAULT_LOGO_CONTENT_TYPE);
        assertThat(testTeam.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void createTeamWithExistingId() throws Exception {
        // Create the Team with an existing ID
        team.setId(1L);

        int databaseSizeBeforeCreate = teamRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTeamMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(team)))
            .andExpect(status().isBadRequest());

        // Validate the Team in the database
        List<Team> teamList = teamRepository.findAll();
        assertThat(teamList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = teamRepository.findAll().size();
        // set the field null
        team.setName(null);

        // Create the Team, which fails.

        restTeamMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(team)))
            .andExpect(status().isBadRequest());

        List<Team> teamList = teamRepository.findAll();
        assertThat(teamList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTeams() throws Exception {
        // Initialize the database
        teamRepository.saveAndFlush(team);

        // Get all the teamList
        restTeamMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(team.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].motto").value(hasItem(DEFAULT_MOTTO)))
            .andExpect(jsonPath("$.[*].logoContentType").value(hasItem(DEFAULT_LOGO_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].logo").value(hasItem(Base64Utils.encodeToString(DEFAULT_LOGO))))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @Test
    @Transactional
    void getTeam() throws Exception {
        // Initialize the database
        teamRepository.saveAndFlush(team);

        // Get the team
        restTeamMockMvc
            .perform(get(ENTITY_API_URL_ID, team.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(team.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.motto").value(DEFAULT_MOTTO))
            .andExpect(jsonPath("$.logoContentType").value(DEFAULT_LOGO_CONTENT_TYPE))
            .andExpect(jsonPath("$.logo").value(Base64Utils.encodeToString(DEFAULT_LOGO)))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    void getTeamsByIdFiltering() throws Exception {
        // Initialize the database
        teamRepository.saveAndFlush(team);

        Long id = team.getId();

        defaultTeamShouldBeFound("id.equals=" + id);
        defaultTeamShouldNotBeFound("id.notEquals=" + id);

        defaultTeamShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultTeamShouldNotBeFound("id.greaterThan=" + id);

        defaultTeamShouldBeFound("id.lessThanOrEqual=" + id);
        defaultTeamShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllTeamsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        teamRepository.saveAndFlush(team);

        // Get all the teamList where name equals to DEFAULT_NAME
        defaultTeamShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the teamList where name equals to UPDATED_NAME
        defaultTeamShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTeamsByNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        teamRepository.saveAndFlush(team);

        // Get all the teamList where name not equals to DEFAULT_NAME
        defaultTeamShouldNotBeFound("name.notEquals=" + DEFAULT_NAME);

        // Get all the teamList where name not equals to UPDATED_NAME
        defaultTeamShouldBeFound("name.notEquals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTeamsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        teamRepository.saveAndFlush(team);

        // Get all the teamList where name in DEFAULT_NAME or UPDATED_NAME
        defaultTeamShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the teamList where name equals to UPDATED_NAME
        defaultTeamShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTeamsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        teamRepository.saveAndFlush(team);

        // Get all the teamList where name is not null
        defaultTeamShouldBeFound("name.specified=true");

        // Get all the teamList where name is null
        defaultTeamShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllTeamsByNameContainsSomething() throws Exception {
        // Initialize the database
        teamRepository.saveAndFlush(team);

        // Get all the teamList where name contains DEFAULT_NAME
        defaultTeamShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the teamList where name contains UPDATED_NAME
        defaultTeamShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTeamsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        teamRepository.saveAndFlush(team);

        // Get all the teamList where name does not contain DEFAULT_NAME
        defaultTeamShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the teamList where name does not contain UPDATED_NAME
        defaultTeamShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTeamsByMottoIsEqualToSomething() throws Exception {
        // Initialize the database
        teamRepository.saveAndFlush(team);

        // Get all the teamList where motto equals to DEFAULT_MOTTO
        defaultTeamShouldBeFound("motto.equals=" + DEFAULT_MOTTO);

        // Get all the teamList where motto equals to UPDATED_MOTTO
        defaultTeamShouldNotBeFound("motto.equals=" + UPDATED_MOTTO);
    }

    @Test
    @Transactional
    void getAllTeamsByMottoIsNotEqualToSomething() throws Exception {
        // Initialize the database
        teamRepository.saveAndFlush(team);

        // Get all the teamList where motto not equals to DEFAULT_MOTTO
        defaultTeamShouldNotBeFound("motto.notEquals=" + DEFAULT_MOTTO);

        // Get all the teamList where motto not equals to UPDATED_MOTTO
        defaultTeamShouldBeFound("motto.notEquals=" + UPDATED_MOTTO);
    }

    @Test
    @Transactional
    void getAllTeamsByMottoIsInShouldWork() throws Exception {
        // Initialize the database
        teamRepository.saveAndFlush(team);

        // Get all the teamList where motto in DEFAULT_MOTTO or UPDATED_MOTTO
        defaultTeamShouldBeFound("motto.in=" + DEFAULT_MOTTO + "," + UPDATED_MOTTO);

        // Get all the teamList where motto equals to UPDATED_MOTTO
        defaultTeamShouldNotBeFound("motto.in=" + UPDATED_MOTTO);
    }

    @Test
    @Transactional
    void getAllTeamsByMottoIsNullOrNotNull() throws Exception {
        // Initialize the database
        teamRepository.saveAndFlush(team);

        // Get all the teamList where motto is not null
        defaultTeamShouldBeFound("motto.specified=true");

        // Get all the teamList where motto is null
        defaultTeamShouldNotBeFound("motto.specified=false");
    }

    @Test
    @Transactional
    void getAllTeamsByMottoContainsSomething() throws Exception {
        // Initialize the database
        teamRepository.saveAndFlush(team);

        // Get all the teamList where motto contains DEFAULT_MOTTO
        defaultTeamShouldBeFound("motto.contains=" + DEFAULT_MOTTO);

        // Get all the teamList where motto contains UPDATED_MOTTO
        defaultTeamShouldNotBeFound("motto.contains=" + UPDATED_MOTTO);
    }

    @Test
    @Transactional
    void getAllTeamsByMottoNotContainsSomething() throws Exception {
        // Initialize the database
        teamRepository.saveAndFlush(team);

        // Get all the teamList where motto does not contain DEFAULT_MOTTO
        defaultTeamShouldNotBeFound("motto.doesNotContain=" + DEFAULT_MOTTO);

        // Get all the teamList where motto does not contain UPDATED_MOTTO
        defaultTeamShouldBeFound("motto.doesNotContain=" + UPDATED_MOTTO);
    }

    @Test
    @Transactional
    void getAllTeamsByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        teamRepository.saveAndFlush(team);

        // Get all the teamList where description equals to DEFAULT_DESCRIPTION
        defaultTeamShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the teamList where description equals to UPDATED_DESCRIPTION
        defaultTeamShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllTeamsByDescriptionIsNotEqualToSomething() throws Exception {
        // Initialize the database
        teamRepository.saveAndFlush(team);

        // Get all the teamList where description not equals to DEFAULT_DESCRIPTION
        defaultTeamShouldNotBeFound("description.notEquals=" + DEFAULT_DESCRIPTION);

        // Get all the teamList where description not equals to UPDATED_DESCRIPTION
        defaultTeamShouldBeFound("description.notEquals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllTeamsByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        teamRepository.saveAndFlush(team);

        // Get all the teamList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultTeamShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the teamList where description equals to UPDATED_DESCRIPTION
        defaultTeamShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllTeamsByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        teamRepository.saveAndFlush(team);

        // Get all the teamList where description is not null
        defaultTeamShouldBeFound("description.specified=true");

        // Get all the teamList where description is null
        defaultTeamShouldNotBeFound("description.specified=false");
    }

    @Test
    @Transactional
    void getAllTeamsByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        teamRepository.saveAndFlush(team);

        // Get all the teamList where description contains DEFAULT_DESCRIPTION
        defaultTeamShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION);

        // Get all the teamList where description contains UPDATED_DESCRIPTION
        defaultTeamShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllTeamsByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        teamRepository.saveAndFlush(team);

        // Get all the teamList where description does not contain DEFAULT_DESCRIPTION
        defaultTeamShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION);

        // Get all the teamList where description does not contain UPDATED_DESCRIPTION
        defaultTeamShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllTeamsByTeamContactIsEqualToSomething() throws Exception {
        // Initialize the database
        teamRepository.saveAndFlush(team);
        TeamContact teamContact;
        if (TestUtil.findAll(em, TeamContact.class).isEmpty()) {
            teamContact = TeamContactResourceIT.createEntity(em);
            em.persist(teamContact);
            em.flush();
        } else {
            teamContact = TestUtil.findAll(em, TeamContact.class).get(0);
        }
        em.persist(teamContact);
        em.flush();
        team.addTeamContact(teamContact);
        teamRepository.saveAndFlush(team);
        Long teamContactId = teamContact.getId();

        // Get all the teamList where teamContact equals to teamContactId
        defaultTeamShouldBeFound("teamContactId.equals=" + teamContactId);

        // Get all the teamList where teamContact equals to (teamContactId + 1)
        defaultTeamShouldNotBeFound("teamContactId.equals=" + (teamContactId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTeamShouldBeFound(String filter) throws Exception {
        restTeamMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(team.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].motto").value(hasItem(DEFAULT_MOTTO)))
            .andExpect(jsonPath("$.[*].logoContentType").value(hasItem(DEFAULT_LOGO_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].logo").value(hasItem(Base64Utils.encodeToString(DEFAULT_LOGO))))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));

        // Check, that the count call also returns 1
        restTeamMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultTeamShouldNotBeFound(String filter) throws Exception {
        restTeamMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTeamMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingTeam() throws Exception {
        // Get the team
        restTeamMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewTeam() throws Exception {
        // Initialize the database
        teamRepository.saveAndFlush(team);

        int databaseSizeBeforeUpdate = teamRepository.findAll().size();

        // Update the team
        Team updatedTeam = teamRepository.findById(team.getId()).get();
        // Disconnect from session so that the updates on updatedTeam are not directly saved in db
        em.detach(updatedTeam);
        updatedTeam
            .name(UPDATED_NAME)
            .motto(UPDATED_MOTTO)
            .logo(UPDATED_LOGO)
            .logoContentType(UPDATED_LOGO_CONTENT_TYPE)
            .description(UPDATED_DESCRIPTION);

        restTeamMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedTeam.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedTeam))
            )
            .andExpect(status().isOk());

        // Validate the Team in the database
        List<Team> teamList = teamRepository.findAll();
        assertThat(teamList).hasSize(databaseSizeBeforeUpdate);
        Team testTeam = teamList.get(teamList.size() - 1);
        assertThat(testTeam.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testTeam.getMotto()).isEqualTo(UPDATED_MOTTO);
        assertThat(testTeam.getLogo()).isEqualTo(UPDATED_LOGO);
        assertThat(testTeam.getLogoContentType()).isEqualTo(UPDATED_LOGO_CONTENT_TYPE);
        assertThat(testTeam.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void putNonExistingTeam() throws Exception {
        int databaseSizeBeforeUpdate = teamRepository.findAll().size();
        team.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTeamMockMvc
            .perform(
                put(ENTITY_API_URL_ID, team.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(team))
            )
            .andExpect(status().isBadRequest());

        // Validate the Team in the database
        List<Team> teamList = teamRepository.findAll();
        assertThat(teamList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTeam() throws Exception {
        int databaseSizeBeforeUpdate = teamRepository.findAll().size();
        team.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTeamMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(team))
            )
            .andExpect(status().isBadRequest());

        // Validate the Team in the database
        List<Team> teamList = teamRepository.findAll();
        assertThat(teamList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTeam() throws Exception {
        int databaseSizeBeforeUpdate = teamRepository.findAll().size();
        team.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTeamMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(team)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Team in the database
        List<Team> teamList = teamRepository.findAll();
        assertThat(teamList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTeamWithPatch() throws Exception {
        // Initialize the database
        teamRepository.saveAndFlush(team);

        int databaseSizeBeforeUpdate = teamRepository.findAll().size();

        // Update the team using partial update
        Team partialUpdatedTeam = new Team();
        partialUpdatedTeam.setId(team.getId());

        partialUpdatedTeam.logo(UPDATED_LOGO).logoContentType(UPDATED_LOGO_CONTENT_TYPE).description(UPDATED_DESCRIPTION);

        restTeamMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTeam.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTeam))
            )
            .andExpect(status().isOk());

        // Validate the Team in the database
        List<Team> teamList = teamRepository.findAll();
        assertThat(teamList).hasSize(databaseSizeBeforeUpdate);
        Team testTeam = teamList.get(teamList.size() - 1);
        assertThat(testTeam.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testTeam.getMotto()).isEqualTo(DEFAULT_MOTTO);
        assertThat(testTeam.getLogo()).isEqualTo(UPDATED_LOGO);
        assertThat(testTeam.getLogoContentType()).isEqualTo(UPDATED_LOGO_CONTENT_TYPE);
        assertThat(testTeam.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void fullUpdateTeamWithPatch() throws Exception {
        // Initialize the database
        teamRepository.saveAndFlush(team);

        int databaseSizeBeforeUpdate = teamRepository.findAll().size();

        // Update the team using partial update
        Team partialUpdatedTeam = new Team();
        partialUpdatedTeam.setId(team.getId());

        partialUpdatedTeam
            .name(UPDATED_NAME)
            .motto(UPDATED_MOTTO)
            .logo(UPDATED_LOGO)
            .logoContentType(UPDATED_LOGO_CONTENT_TYPE)
            .description(UPDATED_DESCRIPTION);

        restTeamMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTeam.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTeam))
            )
            .andExpect(status().isOk());

        // Validate the Team in the database
        List<Team> teamList = teamRepository.findAll();
        assertThat(teamList).hasSize(databaseSizeBeforeUpdate);
        Team testTeam = teamList.get(teamList.size() - 1);
        assertThat(testTeam.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testTeam.getMotto()).isEqualTo(UPDATED_MOTTO);
        assertThat(testTeam.getLogo()).isEqualTo(UPDATED_LOGO);
        assertThat(testTeam.getLogoContentType()).isEqualTo(UPDATED_LOGO_CONTENT_TYPE);
        assertThat(testTeam.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void patchNonExistingTeam() throws Exception {
        int databaseSizeBeforeUpdate = teamRepository.findAll().size();
        team.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTeamMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, team.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(team))
            )
            .andExpect(status().isBadRequest());

        // Validate the Team in the database
        List<Team> teamList = teamRepository.findAll();
        assertThat(teamList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTeam() throws Exception {
        int databaseSizeBeforeUpdate = teamRepository.findAll().size();
        team.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTeamMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(team))
            )
            .andExpect(status().isBadRequest());

        // Validate the Team in the database
        List<Team> teamList = teamRepository.findAll();
        assertThat(teamList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTeam() throws Exception {
        int databaseSizeBeforeUpdate = teamRepository.findAll().size();
        team.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTeamMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(team)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Team in the database
        List<Team> teamList = teamRepository.findAll();
        assertThat(teamList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTeam() throws Exception {
        // Initialize the database
        teamRepository.saveAndFlush(team);

        int databaseSizeBeforeDelete = teamRepository.findAll().size();

        // Delete the team
        restTeamMockMvc
            .perform(delete(ENTITY_API_URL_ID, team.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Team> teamList = teamRepository.findAll();
        assertThat(teamList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
