package com.computacenter.yocoma.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.computacenter.yocoma.IntegrationTest;
import com.computacenter.yocoma.domain.Contact;
import com.computacenter.yocoma.domain.Team;
import com.computacenter.yocoma.domain.TeamContact;
import com.computacenter.yocoma.domain.enumeration.RoleType;
import com.computacenter.yocoma.repository.TeamContactRepository;
import com.computacenter.yocoma.service.criteria.TeamContactCriteria;
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

/**
 * Integration tests for the {@link TeamContactResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TeamContactResourceIT {

    private static final RoleType DEFAULT_ROLE_TYPE = RoleType.JUNIOR_MEMBER;
    private static final RoleType UPDATED_ROLE_TYPE = RoleType.SENIOR_MEMBER;

    private static final String DEFAULT_ROLE = "AAAAAAAAAA";
    private static final String UPDATED_ROLE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/team-contacts";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TeamContactRepository teamContactRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTeamContactMockMvc;

    private TeamContact teamContact;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TeamContact createEntity(EntityManager em) {
        TeamContact teamContact = new TeamContact().roleType(DEFAULT_ROLE_TYPE).role(DEFAULT_ROLE).description(DEFAULT_DESCRIPTION);
        return teamContact;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TeamContact createUpdatedEntity(EntityManager em) {
        TeamContact teamContact = new TeamContact().roleType(UPDATED_ROLE_TYPE).role(UPDATED_ROLE).description(UPDATED_DESCRIPTION);
        return teamContact;
    }

    @BeforeEach
    public void initTest() {
        teamContact = createEntity(em);
    }

    @Test
    @Transactional
    void createTeamContact() throws Exception {
        int databaseSizeBeforeCreate = teamContactRepository.findAll().size();
        // Create the TeamContact
        restTeamContactMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(teamContact)))
            .andExpect(status().isCreated());

        // Validate the TeamContact in the database
        List<TeamContact> teamContactList = teamContactRepository.findAll();
        assertThat(teamContactList).hasSize(databaseSizeBeforeCreate + 1);
        TeamContact testTeamContact = teamContactList.get(teamContactList.size() - 1);
        assertThat(testTeamContact.getRoleType()).isEqualTo(DEFAULT_ROLE_TYPE);
        assertThat(testTeamContact.getRole()).isEqualTo(DEFAULT_ROLE);
        assertThat(testTeamContact.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void createTeamContactWithExistingId() throws Exception {
        // Create the TeamContact with an existing ID
        teamContact.setId(1L);

        int databaseSizeBeforeCreate = teamContactRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTeamContactMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(teamContact)))
            .andExpect(status().isBadRequest());

        // Validate the TeamContact in the database
        List<TeamContact> teamContactList = teamContactRepository.findAll();
        assertThat(teamContactList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkRoleIsRequired() throws Exception {
        int databaseSizeBeforeTest = teamContactRepository.findAll().size();
        // set the field null
        teamContact.setRole(null);

        // Create the TeamContact, which fails.

        restTeamContactMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(teamContact)))
            .andExpect(status().isBadRequest());

        List<TeamContact> teamContactList = teamContactRepository.findAll();
        assertThat(teamContactList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTeamContacts() throws Exception {
        // Initialize the database
        teamContactRepository.saveAndFlush(teamContact);

        // Get all the teamContactList
        restTeamContactMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(teamContact.getId().intValue())))
            .andExpect(jsonPath("$.[*].roleType").value(hasItem(DEFAULT_ROLE_TYPE.toString())))
            .andExpect(jsonPath("$.[*].role").value(hasItem(DEFAULT_ROLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @Test
    @Transactional
    void getTeamContact() throws Exception {
        // Initialize the database
        teamContactRepository.saveAndFlush(teamContact);

        // Get the teamContact
        restTeamContactMockMvc
            .perform(get(ENTITY_API_URL_ID, teamContact.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(teamContact.getId().intValue()))
            .andExpect(jsonPath("$.roleType").value(DEFAULT_ROLE_TYPE.toString()))
            .andExpect(jsonPath("$.role").value(DEFAULT_ROLE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    void getTeamContactsByIdFiltering() throws Exception {
        // Initialize the database
        teamContactRepository.saveAndFlush(teamContact);

        Long id = teamContact.getId();

        defaultTeamContactShouldBeFound("id.equals=" + id);
        defaultTeamContactShouldNotBeFound("id.notEquals=" + id);

        defaultTeamContactShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultTeamContactShouldNotBeFound("id.greaterThan=" + id);

        defaultTeamContactShouldBeFound("id.lessThanOrEqual=" + id);
        defaultTeamContactShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllTeamContactsByRoleTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        teamContactRepository.saveAndFlush(teamContact);

        // Get all the teamContactList where roleType equals to DEFAULT_ROLE_TYPE
        defaultTeamContactShouldBeFound("roleType.equals=" + DEFAULT_ROLE_TYPE);

        // Get all the teamContactList where roleType equals to UPDATED_ROLE_TYPE
        defaultTeamContactShouldNotBeFound("roleType.equals=" + UPDATED_ROLE_TYPE);
    }

    @Test
    @Transactional
    void getAllTeamContactsByRoleTypeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        teamContactRepository.saveAndFlush(teamContact);

        // Get all the teamContactList where roleType not equals to DEFAULT_ROLE_TYPE
        defaultTeamContactShouldNotBeFound("roleType.notEquals=" + DEFAULT_ROLE_TYPE);

        // Get all the teamContactList where roleType not equals to UPDATED_ROLE_TYPE
        defaultTeamContactShouldBeFound("roleType.notEquals=" + UPDATED_ROLE_TYPE);
    }

    @Test
    @Transactional
    void getAllTeamContactsByRoleTypeIsInShouldWork() throws Exception {
        // Initialize the database
        teamContactRepository.saveAndFlush(teamContact);

        // Get all the teamContactList where roleType in DEFAULT_ROLE_TYPE or UPDATED_ROLE_TYPE
        defaultTeamContactShouldBeFound("roleType.in=" + DEFAULT_ROLE_TYPE + "," + UPDATED_ROLE_TYPE);

        // Get all the teamContactList where roleType equals to UPDATED_ROLE_TYPE
        defaultTeamContactShouldNotBeFound("roleType.in=" + UPDATED_ROLE_TYPE);
    }

    @Test
    @Transactional
    void getAllTeamContactsByRoleTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        teamContactRepository.saveAndFlush(teamContact);

        // Get all the teamContactList where roleType is not null
        defaultTeamContactShouldBeFound("roleType.specified=true");

        // Get all the teamContactList where roleType is null
        defaultTeamContactShouldNotBeFound("roleType.specified=false");
    }

    @Test
    @Transactional
    void getAllTeamContactsByRoleIsEqualToSomething() throws Exception {
        // Initialize the database
        teamContactRepository.saveAndFlush(teamContact);

        // Get all the teamContactList where role equals to DEFAULT_ROLE
        defaultTeamContactShouldBeFound("role.equals=" + DEFAULT_ROLE);

        // Get all the teamContactList where role equals to UPDATED_ROLE
        defaultTeamContactShouldNotBeFound("role.equals=" + UPDATED_ROLE);
    }

    @Test
    @Transactional
    void getAllTeamContactsByRoleIsNotEqualToSomething() throws Exception {
        // Initialize the database
        teamContactRepository.saveAndFlush(teamContact);

        // Get all the teamContactList where role not equals to DEFAULT_ROLE
        defaultTeamContactShouldNotBeFound("role.notEquals=" + DEFAULT_ROLE);

        // Get all the teamContactList where role not equals to UPDATED_ROLE
        defaultTeamContactShouldBeFound("role.notEquals=" + UPDATED_ROLE);
    }

    @Test
    @Transactional
    void getAllTeamContactsByRoleIsInShouldWork() throws Exception {
        // Initialize the database
        teamContactRepository.saveAndFlush(teamContact);

        // Get all the teamContactList where role in DEFAULT_ROLE or UPDATED_ROLE
        defaultTeamContactShouldBeFound("role.in=" + DEFAULT_ROLE + "," + UPDATED_ROLE);

        // Get all the teamContactList where role equals to UPDATED_ROLE
        defaultTeamContactShouldNotBeFound("role.in=" + UPDATED_ROLE);
    }

    @Test
    @Transactional
    void getAllTeamContactsByRoleIsNullOrNotNull() throws Exception {
        // Initialize the database
        teamContactRepository.saveAndFlush(teamContact);

        // Get all the teamContactList where role is not null
        defaultTeamContactShouldBeFound("role.specified=true");

        // Get all the teamContactList where role is null
        defaultTeamContactShouldNotBeFound("role.specified=false");
    }

    @Test
    @Transactional
    void getAllTeamContactsByRoleContainsSomething() throws Exception {
        // Initialize the database
        teamContactRepository.saveAndFlush(teamContact);

        // Get all the teamContactList where role contains DEFAULT_ROLE
        defaultTeamContactShouldBeFound("role.contains=" + DEFAULT_ROLE);

        // Get all the teamContactList where role contains UPDATED_ROLE
        defaultTeamContactShouldNotBeFound("role.contains=" + UPDATED_ROLE);
    }

    @Test
    @Transactional
    void getAllTeamContactsByRoleNotContainsSomething() throws Exception {
        // Initialize the database
        teamContactRepository.saveAndFlush(teamContact);

        // Get all the teamContactList where role does not contain DEFAULT_ROLE
        defaultTeamContactShouldNotBeFound("role.doesNotContain=" + DEFAULT_ROLE);

        // Get all the teamContactList where role does not contain UPDATED_ROLE
        defaultTeamContactShouldBeFound("role.doesNotContain=" + UPDATED_ROLE);
    }

    @Test
    @Transactional
    void getAllTeamContactsByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        teamContactRepository.saveAndFlush(teamContact);

        // Get all the teamContactList where description equals to DEFAULT_DESCRIPTION
        defaultTeamContactShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the teamContactList where description equals to UPDATED_DESCRIPTION
        defaultTeamContactShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllTeamContactsByDescriptionIsNotEqualToSomething() throws Exception {
        // Initialize the database
        teamContactRepository.saveAndFlush(teamContact);

        // Get all the teamContactList where description not equals to DEFAULT_DESCRIPTION
        defaultTeamContactShouldNotBeFound("description.notEquals=" + DEFAULT_DESCRIPTION);

        // Get all the teamContactList where description not equals to UPDATED_DESCRIPTION
        defaultTeamContactShouldBeFound("description.notEquals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllTeamContactsByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        teamContactRepository.saveAndFlush(teamContact);

        // Get all the teamContactList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultTeamContactShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the teamContactList where description equals to UPDATED_DESCRIPTION
        defaultTeamContactShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllTeamContactsByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        teamContactRepository.saveAndFlush(teamContact);

        // Get all the teamContactList where description is not null
        defaultTeamContactShouldBeFound("description.specified=true");

        // Get all the teamContactList where description is null
        defaultTeamContactShouldNotBeFound("description.specified=false");
    }

    @Test
    @Transactional
    void getAllTeamContactsByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        teamContactRepository.saveAndFlush(teamContact);

        // Get all the teamContactList where description contains DEFAULT_DESCRIPTION
        defaultTeamContactShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION);

        // Get all the teamContactList where description contains UPDATED_DESCRIPTION
        defaultTeamContactShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllTeamContactsByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        teamContactRepository.saveAndFlush(teamContact);

        // Get all the teamContactList where description does not contain DEFAULT_DESCRIPTION
        defaultTeamContactShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION);

        // Get all the teamContactList where description does not contain UPDATED_DESCRIPTION
        defaultTeamContactShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllTeamContactsByContactIsEqualToSomething() throws Exception {
        // Initialize the database
        teamContactRepository.saveAndFlush(teamContact);
        Contact contact;
        if (TestUtil.findAll(em, Contact.class).isEmpty()) {
            contact = ContactResourceIT.createEntity(em);
            em.persist(contact);
            em.flush();
        } else {
            contact = TestUtil.findAll(em, Contact.class).get(0);
        }
        em.persist(contact);
        em.flush();
        teamContact.setContact(contact);
        teamContactRepository.saveAndFlush(teamContact);
        Long contactId = contact.getId();

        // Get all the teamContactList where contact equals to contactId
        defaultTeamContactShouldBeFound("contactId.equals=" + contactId);

        // Get all the teamContactList where contact equals to (contactId + 1)
        defaultTeamContactShouldNotBeFound("contactId.equals=" + (contactId + 1));
    }

    @Test
    @Transactional
    void getAllTeamContactsByTeamIsEqualToSomething() throws Exception {
        // Initialize the database
        teamContactRepository.saveAndFlush(teamContact);
        Team team;
        if (TestUtil.findAll(em, Team.class).isEmpty()) {
            team = TeamResourceIT.createEntity(em);
            em.persist(team);
            em.flush();
        } else {
            team = TestUtil.findAll(em, Team.class).get(0);
        }
        em.persist(team);
        em.flush();
        teamContact.setTeam(team);
        teamContactRepository.saveAndFlush(teamContact);
        Long teamId = team.getId();

        // Get all the teamContactList where team equals to teamId
        defaultTeamContactShouldBeFound("teamId.equals=" + teamId);

        // Get all the teamContactList where team equals to (teamId + 1)
        defaultTeamContactShouldNotBeFound("teamId.equals=" + (teamId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTeamContactShouldBeFound(String filter) throws Exception {
        restTeamContactMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(teamContact.getId().intValue())))
            .andExpect(jsonPath("$.[*].roleType").value(hasItem(DEFAULT_ROLE_TYPE.toString())))
            .andExpect(jsonPath("$.[*].role").value(hasItem(DEFAULT_ROLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));

        // Check, that the count call also returns 1
        restTeamContactMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultTeamContactShouldNotBeFound(String filter) throws Exception {
        restTeamContactMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTeamContactMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingTeamContact() throws Exception {
        // Get the teamContact
        restTeamContactMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewTeamContact() throws Exception {
        // Initialize the database
        teamContactRepository.saveAndFlush(teamContact);

        int databaseSizeBeforeUpdate = teamContactRepository.findAll().size();

        // Update the teamContact
        TeamContact updatedTeamContact = teamContactRepository.findById(teamContact.getId()).get();
        // Disconnect from session so that the updates on updatedTeamContact are not directly saved in db
        em.detach(updatedTeamContact);
        updatedTeamContact.roleType(UPDATED_ROLE_TYPE).role(UPDATED_ROLE).description(UPDATED_DESCRIPTION);

        restTeamContactMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedTeamContact.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedTeamContact))
            )
            .andExpect(status().isOk());

        // Validate the TeamContact in the database
        List<TeamContact> teamContactList = teamContactRepository.findAll();
        assertThat(teamContactList).hasSize(databaseSizeBeforeUpdate);
        TeamContact testTeamContact = teamContactList.get(teamContactList.size() - 1);
        assertThat(testTeamContact.getRoleType()).isEqualTo(UPDATED_ROLE_TYPE);
        assertThat(testTeamContact.getRole()).isEqualTo(UPDATED_ROLE);
        assertThat(testTeamContact.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void putNonExistingTeamContact() throws Exception {
        int databaseSizeBeforeUpdate = teamContactRepository.findAll().size();
        teamContact.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTeamContactMockMvc
            .perform(
                put(ENTITY_API_URL_ID, teamContact.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(teamContact))
            )
            .andExpect(status().isBadRequest());

        // Validate the TeamContact in the database
        List<TeamContact> teamContactList = teamContactRepository.findAll();
        assertThat(teamContactList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTeamContact() throws Exception {
        int databaseSizeBeforeUpdate = teamContactRepository.findAll().size();
        teamContact.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTeamContactMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(teamContact))
            )
            .andExpect(status().isBadRequest());

        // Validate the TeamContact in the database
        List<TeamContact> teamContactList = teamContactRepository.findAll();
        assertThat(teamContactList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTeamContact() throws Exception {
        int databaseSizeBeforeUpdate = teamContactRepository.findAll().size();
        teamContact.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTeamContactMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(teamContact)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TeamContact in the database
        List<TeamContact> teamContactList = teamContactRepository.findAll();
        assertThat(teamContactList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTeamContactWithPatch() throws Exception {
        // Initialize the database
        teamContactRepository.saveAndFlush(teamContact);

        int databaseSizeBeforeUpdate = teamContactRepository.findAll().size();

        // Update the teamContact using partial update
        TeamContact partialUpdatedTeamContact = new TeamContact();
        partialUpdatedTeamContact.setId(teamContact.getId());

        partialUpdatedTeamContact.role(UPDATED_ROLE).description(UPDATED_DESCRIPTION);

        restTeamContactMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTeamContact.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTeamContact))
            )
            .andExpect(status().isOk());

        // Validate the TeamContact in the database
        List<TeamContact> teamContactList = teamContactRepository.findAll();
        assertThat(teamContactList).hasSize(databaseSizeBeforeUpdate);
        TeamContact testTeamContact = teamContactList.get(teamContactList.size() - 1);
        assertThat(testTeamContact.getRoleType()).isEqualTo(DEFAULT_ROLE_TYPE);
        assertThat(testTeamContact.getRole()).isEqualTo(UPDATED_ROLE);
        assertThat(testTeamContact.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void fullUpdateTeamContactWithPatch() throws Exception {
        // Initialize the database
        teamContactRepository.saveAndFlush(teamContact);

        int databaseSizeBeforeUpdate = teamContactRepository.findAll().size();

        // Update the teamContact using partial update
        TeamContact partialUpdatedTeamContact = new TeamContact();
        partialUpdatedTeamContact.setId(teamContact.getId());

        partialUpdatedTeamContact.roleType(UPDATED_ROLE_TYPE).role(UPDATED_ROLE).description(UPDATED_DESCRIPTION);

        restTeamContactMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTeamContact.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTeamContact))
            )
            .andExpect(status().isOk());

        // Validate the TeamContact in the database
        List<TeamContact> teamContactList = teamContactRepository.findAll();
        assertThat(teamContactList).hasSize(databaseSizeBeforeUpdate);
        TeamContact testTeamContact = teamContactList.get(teamContactList.size() - 1);
        assertThat(testTeamContact.getRoleType()).isEqualTo(UPDATED_ROLE_TYPE);
        assertThat(testTeamContact.getRole()).isEqualTo(UPDATED_ROLE);
        assertThat(testTeamContact.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void patchNonExistingTeamContact() throws Exception {
        int databaseSizeBeforeUpdate = teamContactRepository.findAll().size();
        teamContact.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTeamContactMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, teamContact.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(teamContact))
            )
            .andExpect(status().isBadRequest());

        // Validate the TeamContact in the database
        List<TeamContact> teamContactList = teamContactRepository.findAll();
        assertThat(teamContactList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTeamContact() throws Exception {
        int databaseSizeBeforeUpdate = teamContactRepository.findAll().size();
        teamContact.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTeamContactMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(teamContact))
            )
            .andExpect(status().isBadRequest());

        // Validate the TeamContact in the database
        List<TeamContact> teamContactList = teamContactRepository.findAll();
        assertThat(teamContactList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTeamContact() throws Exception {
        int databaseSizeBeforeUpdate = teamContactRepository.findAll().size();
        teamContact.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTeamContactMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(teamContact))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TeamContact in the database
        List<TeamContact> teamContactList = teamContactRepository.findAll();
        assertThat(teamContactList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTeamContact() throws Exception {
        // Initialize the database
        teamContactRepository.saveAndFlush(teamContact);

        int databaseSizeBeforeDelete = teamContactRepository.findAll().size();

        // Delete the teamContact
        restTeamContactMockMvc
            .perform(delete(ENTITY_API_URL_ID, teamContact.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<TeamContact> teamContactList = teamContactRepository.findAll();
        assertThat(teamContactList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
