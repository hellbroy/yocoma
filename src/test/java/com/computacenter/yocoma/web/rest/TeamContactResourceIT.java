package com.computacenter.yocoma.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.computacenter.yocoma.IntegrationTest;
import com.computacenter.yocoma.domain.TeamContact;
import com.computacenter.yocoma.domain.enumeration.RoleType;
import com.computacenter.yocoma.repository.TeamContactRepository;
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
