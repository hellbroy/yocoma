package com.computacenter.yocoma.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.computacenter.yocoma.IntegrationTest;
import com.computacenter.yocoma.domain.Contact;
import com.computacenter.yocoma.domain.TeamContact;
import com.computacenter.yocoma.repository.ContactRepository;
import com.computacenter.yocoma.service.criteria.ContactCriteria;
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
 * Integration tests for the {@link ContactResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ContactResourceIT {

    private static final String DEFAULT_FIRSTNAME = "AAAAAAAAAA";
    private static final String UPDATED_FIRSTNAME = "BBBBBBBBBB";

    private static final String DEFAULT_LASTNAME = "AAAAAAAAAA";
    private static final String UPDATED_LASTNAME = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "4RpG@`.U}>0!";
    private static final String UPDATED_EMAIL = "R<@)..:AG";

    private static final String DEFAULT_PHONE = "AAAAAAAAAA";
    private static final String UPDATED_PHONE = "BBBBBBBBBB";

    private static final String DEFAULT_REMARK = "AAAAAAAAAA";
    private static final String UPDATED_REMARK = "BBBBBBBBBB";

    private static final byte[] DEFAULT_IMAGE = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_IMAGE = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_IMAGE_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_IMAGE_CONTENT_TYPE = "image/png";

    private static final String ENTITY_API_URL = "/api/contacts";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restContactMockMvc;

    private Contact contact;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Contact createEntity(EntityManager em) {
        Contact contact = new Contact()
            .firstname(DEFAULT_FIRSTNAME)
            .lastname(DEFAULT_LASTNAME)
            .email(DEFAULT_EMAIL)
            .phone(DEFAULT_PHONE)
            .remark(DEFAULT_REMARK)
            .image(DEFAULT_IMAGE)
            .imageContentType(DEFAULT_IMAGE_CONTENT_TYPE);
        return contact;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Contact createUpdatedEntity(EntityManager em) {
        Contact contact = new Contact()
            .firstname(UPDATED_FIRSTNAME)
            .lastname(UPDATED_LASTNAME)
            .email(UPDATED_EMAIL)
            .phone(UPDATED_PHONE)
            .remark(UPDATED_REMARK)
            .image(UPDATED_IMAGE)
            .imageContentType(UPDATED_IMAGE_CONTENT_TYPE);
        return contact;
    }

    @BeforeEach
    public void initTest() {
        contact = createEntity(em);
    }

    @Test
    @Transactional
    void createContact() throws Exception {
        int databaseSizeBeforeCreate = contactRepository.findAll().size();
        // Create the Contact
        restContactMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(contact)))
            .andExpect(status().isCreated());

        // Validate the Contact in the database
        List<Contact> contactList = contactRepository.findAll();
        assertThat(contactList).hasSize(databaseSizeBeforeCreate + 1);
        Contact testContact = contactList.get(contactList.size() - 1);
        assertThat(testContact.getFirstname()).isEqualTo(DEFAULT_FIRSTNAME);
        assertThat(testContact.getLastname()).isEqualTo(DEFAULT_LASTNAME);
        assertThat(testContact.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testContact.getPhone()).isEqualTo(DEFAULT_PHONE);
        assertThat(testContact.getRemark()).isEqualTo(DEFAULT_REMARK);
        assertThat(testContact.getImage()).isEqualTo(DEFAULT_IMAGE);
        assertThat(testContact.getImageContentType()).isEqualTo(DEFAULT_IMAGE_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void createContactWithExistingId() throws Exception {
        // Create the Contact with an existing ID
        contact.setId(1L);

        int databaseSizeBeforeCreate = contactRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restContactMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(contact)))
            .andExpect(status().isBadRequest());

        // Validate the Contact in the database
        List<Contact> contactList = contactRepository.findAll();
        assertThat(contactList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkFirstnameIsRequired() throws Exception {
        int databaseSizeBeforeTest = contactRepository.findAll().size();
        // set the field null
        contact.setFirstname(null);

        // Create the Contact, which fails.

        restContactMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(contact)))
            .andExpect(status().isBadRequest());

        List<Contact> contactList = contactRepository.findAll();
        assertThat(contactList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkLastnameIsRequired() throws Exception {
        int databaseSizeBeforeTest = contactRepository.findAll().size();
        // set the field null
        contact.setLastname(null);

        // Create the Contact, which fails.

        restContactMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(contact)))
            .andExpect(status().isBadRequest());

        List<Contact> contactList = contactRepository.findAll();
        assertThat(contactList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllContacts() throws Exception {
        // Initialize the database
        contactRepository.saveAndFlush(contact);

        // Get all the contactList
        restContactMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(contact.getId().intValue())))
            .andExpect(jsonPath("$.[*].firstname").value(hasItem(DEFAULT_FIRSTNAME)))
            .andExpect(jsonPath("$.[*].lastname").value(hasItem(DEFAULT_LASTNAME)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)))
            .andExpect(jsonPath("$.[*].remark").value(hasItem(DEFAULT_REMARK)))
            .andExpect(jsonPath("$.[*].imageContentType").value(hasItem(DEFAULT_IMAGE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].image").value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE))));
    }

    @Test
    @Transactional
    void getContact() throws Exception {
        // Initialize the database
        contactRepository.saveAndFlush(contact);

        // Get the contact
        restContactMockMvc
            .perform(get(ENTITY_API_URL_ID, contact.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(contact.getId().intValue()))
            .andExpect(jsonPath("$.firstname").value(DEFAULT_FIRSTNAME))
            .andExpect(jsonPath("$.lastname").value(DEFAULT_LASTNAME))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
            .andExpect(jsonPath("$.phone").value(DEFAULT_PHONE))
            .andExpect(jsonPath("$.remark").value(DEFAULT_REMARK))
            .andExpect(jsonPath("$.imageContentType").value(DEFAULT_IMAGE_CONTENT_TYPE))
            .andExpect(jsonPath("$.image").value(Base64Utils.encodeToString(DEFAULT_IMAGE)));
    }

    @Test
    @Transactional
    void getContactsByIdFiltering() throws Exception {
        // Initialize the database
        contactRepository.saveAndFlush(contact);

        Long id = contact.getId();

        defaultContactShouldBeFound("id.equals=" + id);
        defaultContactShouldNotBeFound("id.notEquals=" + id);

        defaultContactShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultContactShouldNotBeFound("id.greaterThan=" + id);

        defaultContactShouldBeFound("id.lessThanOrEqual=" + id);
        defaultContactShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllContactsByFirstnameIsEqualToSomething() throws Exception {
        // Initialize the database
        contactRepository.saveAndFlush(contact);

        // Get all the contactList where firstname equals to DEFAULT_FIRSTNAME
        defaultContactShouldBeFound("firstname.equals=" + DEFAULT_FIRSTNAME);

        // Get all the contactList where firstname equals to UPDATED_FIRSTNAME
        defaultContactShouldNotBeFound("firstname.equals=" + UPDATED_FIRSTNAME);
    }

    @Test
    @Transactional
    void getAllContactsByFirstnameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        contactRepository.saveAndFlush(contact);

        // Get all the contactList where firstname not equals to DEFAULT_FIRSTNAME
        defaultContactShouldNotBeFound("firstname.notEquals=" + DEFAULT_FIRSTNAME);

        // Get all the contactList where firstname not equals to UPDATED_FIRSTNAME
        defaultContactShouldBeFound("firstname.notEquals=" + UPDATED_FIRSTNAME);
    }

    @Test
    @Transactional
    void getAllContactsByFirstnameIsInShouldWork() throws Exception {
        // Initialize the database
        contactRepository.saveAndFlush(contact);

        // Get all the contactList where firstname in DEFAULT_FIRSTNAME or UPDATED_FIRSTNAME
        defaultContactShouldBeFound("firstname.in=" + DEFAULT_FIRSTNAME + "," + UPDATED_FIRSTNAME);

        // Get all the contactList where firstname equals to UPDATED_FIRSTNAME
        defaultContactShouldNotBeFound("firstname.in=" + UPDATED_FIRSTNAME);
    }

    @Test
    @Transactional
    void getAllContactsByFirstnameIsNullOrNotNull() throws Exception {
        // Initialize the database
        contactRepository.saveAndFlush(contact);

        // Get all the contactList where firstname is not null
        defaultContactShouldBeFound("firstname.specified=true");

        // Get all the contactList where firstname is null
        defaultContactShouldNotBeFound("firstname.specified=false");
    }

    @Test
    @Transactional
    void getAllContactsByFirstnameContainsSomething() throws Exception {
        // Initialize the database
        contactRepository.saveAndFlush(contact);

        // Get all the contactList where firstname contains DEFAULT_FIRSTNAME
        defaultContactShouldBeFound("firstname.contains=" + DEFAULT_FIRSTNAME);

        // Get all the contactList where firstname contains UPDATED_FIRSTNAME
        defaultContactShouldNotBeFound("firstname.contains=" + UPDATED_FIRSTNAME);
    }

    @Test
    @Transactional
    void getAllContactsByFirstnameNotContainsSomething() throws Exception {
        // Initialize the database
        contactRepository.saveAndFlush(contact);

        // Get all the contactList where firstname does not contain DEFAULT_FIRSTNAME
        defaultContactShouldNotBeFound("firstname.doesNotContain=" + DEFAULT_FIRSTNAME);

        // Get all the contactList where firstname does not contain UPDATED_FIRSTNAME
        defaultContactShouldBeFound("firstname.doesNotContain=" + UPDATED_FIRSTNAME);
    }

    @Test
    @Transactional
    void getAllContactsByLastnameIsEqualToSomething() throws Exception {
        // Initialize the database
        contactRepository.saveAndFlush(contact);

        // Get all the contactList where lastname equals to DEFAULT_LASTNAME
        defaultContactShouldBeFound("lastname.equals=" + DEFAULT_LASTNAME);

        // Get all the contactList where lastname equals to UPDATED_LASTNAME
        defaultContactShouldNotBeFound("lastname.equals=" + UPDATED_LASTNAME);
    }

    @Test
    @Transactional
    void getAllContactsByLastnameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        contactRepository.saveAndFlush(contact);

        // Get all the contactList where lastname not equals to DEFAULT_LASTNAME
        defaultContactShouldNotBeFound("lastname.notEquals=" + DEFAULT_LASTNAME);

        // Get all the contactList where lastname not equals to UPDATED_LASTNAME
        defaultContactShouldBeFound("lastname.notEquals=" + UPDATED_LASTNAME);
    }

    @Test
    @Transactional
    void getAllContactsByLastnameIsInShouldWork() throws Exception {
        // Initialize the database
        contactRepository.saveAndFlush(contact);

        // Get all the contactList where lastname in DEFAULT_LASTNAME or UPDATED_LASTNAME
        defaultContactShouldBeFound("lastname.in=" + DEFAULT_LASTNAME + "," + UPDATED_LASTNAME);

        // Get all the contactList where lastname equals to UPDATED_LASTNAME
        defaultContactShouldNotBeFound("lastname.in=" + UPDATED_LASTNAME);
    }

    @Test
    @Transactional
    void getAllContactsByLastnameIsNullOrNotNull() throws Exception {
        // Initialize the database
        contactRepository.saveAndFlush(contact);

        // Get all the contactList where lastname is not null
        defaultContactShouldBeFound("lastname.specified=true");

        // Get all the contactList where lastname is null
        defaultContactShouldNotBeFound("lastname.specified=false");
    }

    @Test
    @Transactional
    void getAllContactsByLastnameContainsSomething() throws Exception {
        // Initialize the database
        contactRepository.saveAndFlush(contact);

        // Get all the contactList where lastname contains DEFAULT_LASTNAME
        defaultContactShouldBeFound("lastname.contains=" + DEFAULT_LASTNAME);

        // Get all the contactList where lastname contains UPDATED_LASTNAME
        defaultContactShouldNotBeFound("lastname.contains=" + UPDATED_LASTNAME);
    }

    @Test
    @Transactional
    void getAllContactsByLastnameNotContainsSomething() throws Exception {
        // Initialize the database
        contactRepository.saveAndFlush(contact);

        // Get all the contactList where lastname does not contain DEFAULT_LASTNAME
        defaultContactShouldNotBeFound("lastname.doesNotContain=" + DEFAULT_LASTNAME);

        // Get all the contactList where lastname does not contain UPDATED_LASTNAME
        defaultContactShouldBeFound("lastname.doesNotContain=" + UPDATED_LASTNAME);
    }

    @Test
    @Transactional
    void getAllContactsByEmailIsEqualToSomething() throws Exception {
        // Initialize the database
        contactRepository.saveAndFlush(contact);

        // Get all the contactList where email equals to DEFAULT_EMAIL
        defaultContactShouldBeFound("email.equals=" + DEFAULT_EMAIL);

        // Get all the contactList where email equals to UPDATED_EMAIL
        defaultContactShouldNotBeFound("email.equals=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllContactsByEmailIsNotEqualToSomething() throws Exception {
        // Initialize the database
        contactRepository.saveAndFlush(contact);

        // Get all the contactList where email not equals to DEFAULT_EMAIL
        defaultContactShouldNotBeFound("email.notEquals=" + DEFAULT_EMAIL);

        // Get all the contactList where email not equals to UPDATED_EMAIL
        defaultContactShouldBeFound("email.notEquals=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllContactsByEmailIsInShouldWork() throws Exception {
        // Initialize the database
        contactRepository.saveAndFlush(contact);

        // Get all the contactList where email in DEFAULT_EMAIL or UPDATED_EMAIL
        defaultContactShouldBeFound("email.in=" + DEFAULT_EMAIL + "," + UPDATED_EMAIL);

        // Get all the contactList where email equals to UPDATED_EMAIL
        defaultContactShouldNotBeFound("email.in=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllContactsByEmailIsNullOrNotNull() throws Exception {
        // Initialize the database
        contactRepository.saveAndFlush(contact);

        // Get all the contactList where email is not null
        defaultContactShouldBeFound("email.specified=true");

        // Get all the contactList where email is null
        defaultContactShouldNotBeFound("email.specified=false");
    }

    @Test
    @Transactional
    void getAllContactsByEmailContainsSomething() throws Exception {
        // Initialize the database
        contactRepository.saveAndFlush(contact);

        // Get all the contactList where email contains DEFAULT_EMAIL
        defaultContactShouldBeFound("email.contains=" + DEFAULT_EMAIL);

        // Get all the contactList where email contains UPDATED_EMAIL
        defaultContactShouldNotBeFound("email.contains=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllContactsByEmailNotContainsSomething() throws Exception {
        // Initialize the database
        contactRepository.saveAndFlush(contact);

        // Get all the contactList where email does not contain DEFAULT_EMAIL
        defaultContactShouldNotBeFound("email.doesNotContain=" + DEFAULT_EMAIL);

        // Get all the contactList where email does not contain UPDATED_EMAIL
        defaultContactShouldBeFound("email.doesNotContain=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllContactsByPhoneIsEqualToSomething() throws Exception {
        // Initialize the database
        contactRepository.saveAndFlush(contact);

        // Get all the contactList where phone equals to DEFAULT_PHONE
        defaultContactShouldBeFound("phone.equals=" + DEFAULT_PHONE);

        // Get all the contactList where phone equals to UPDATED_PHONE
        defaultContactShouldNotBeFound("phone.equals=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllContactsByPhoneIsNotEqualToSomething() throws Exception {
        // Initialize the database
        contactRepository.saveAndFlush(contact);

        // Get all the contactList where phone not equals to DEFAULT_PHONE
        defaultContactShouldNotBeFound("phone.notEquals=" + DEFAULT_PHONE);

        // Get all the contactList where phone not equals to UPDATED_PHONE
        defaultContactShouldBeFound("phone.notEquals=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllContactsByPhoneIsInShouldWork() throws Exception {
        // Initialize the database
        contactRepository.saveAndFlush(contact);

        // Get all the contactList where phone in DEFAULT_PHONE or UPDATED_PHONE
        defaultContactShouldBeFound("phone.in=" + DEFAULT_PHONE + "," + UPDATED_PHONE);

        // Get all the contactList where phone equals to UPDATED_PHONE
        defaultContactShouldNotBeFound("phone.in=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllContactsByPhoneIsNullOrNotNull() throws Exception {
        // Initialize the database
        contactRepository.saveAndFlush(contact);

        // Get all the contactList where phone is not null
        defaultContactShouldBeFound("phone.specified=true");

        // Get all the contactList where phone is null
        defaultContactShouldNotBeFound("phone.specified=false");
    }

    @Test
    @Transactional
    void getAllContactsByPhoneContainsSomething() throws Exception {
        // Initialize the database
        contactRepository.saveAndFlush(contact);

        // Get all the contactList where phone contains DEFAULT_PHONE
        defaultContactShouldBeFound("phone.contains=" + DEFAULT_PHONE);

        // Get all the contactList where phone contains UPDATED_PHONE
        defaultContactShouldNotBeFound("phone.contains=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllContactsByPhoneNotContainsSomething() throws Exception {
        // Initialize the database
        contactRepository.saveAndFlush(contact);

        // Get all the contactList where phone does not contain DEFAULT_PHONE
        defaultContactShouldNotBeFound("phone.doesNotContain=" + DEFAULT_PHONE);

        // Get all the contactList where phone does not contain UPDATED_PHONE
        defaultContactShouldBeFound("phone.doesNotContain=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllContactsByRemarkIsEqualToSomething() throws Exception {
        // Initialize the database
        contactRepository.saveAndFlush(contact);

        // Get all the contactList where remark equals to DEFAULT_REMARK
        defaultContactShouldBeFound("remark.equals=" + DEFAULT_REMARK);

        // Get all the contactList where remark equals to UPDATED_REMARK
        defaultContactShouldNotBeFound("remark.equals=" + UPDATED_REMARK);
    }

    @Test
    @Transactional
    void getAllContactsByRemarkIsNotEqualToSomething() throws Exception {
        // Initialize the database
        contactRepository.saveAndFlush(contact);

        // Get all the contactList where remark not equals to DEFAULT_REMARK
        defaultContactShouldNotBeFound("remark.notEquals=" + DEFAULT_REMARK);

        // Get all the contactList where remark not equals to UPDATED_REMARK
        defaultContactShouldBeFound("remark.notEquals=" + UPDATED_REMARK);
    }

    @Test
    @Transactional
    void getAllContactsByRemarkIsInShouldWork() throws Exception {
        // Initialize the database
        contactRepository.saveAndFlush(contact);

        // Get all the contactList where remark in DEFAULT_REMARK or UPDATED_REMARK
        defaultContactShouldBeFound("remark.in=" + DEFAULT_REMARK + "," + UPDATED_REMARK);

        // Get all the contactList where remark equals to UPDATED_REMARK
        defaultContactShouldNotBeFound("remark.in=" + UPDATED_REMARK);
    }

    @Test
    @Transactional
    void getAllContactsByRemarkIsNullOrNotNull() throws Exception {
        // Initialize the database
        contactRepository.saveAndFlush(contact);

        // Get all the contactList where remark is not null
        defaultContactShouldBeFound("remark.specified=true");

        // Get all the contactList where remark is null
        defaultContactShouldNotBeFound("remark.specified=false");
    }

    @Test
    @Transactional
    void getAllContactsByRemarkContainsSomething() throws Exception {
        // Initialize the database
        contactRepository.saveAndFlush(contact);

        // Get all the contactList where remark contains DEFAULT_REMARK
        defaultContactShouldBeFound("remark.contains=" + DEFAULT_REMARK);

        // Get all the contactList where remark contains UPDATED_REMARK
        defaultContactShouldNotBeFound("remark.contains=" + UPDATED_REMARK);
    }

    @Test
    @Transactional
    void getAllContactsByRemarkNotContainsSomething() throws Exception {
        // Initialize the database
        contactRepository.saveAndFlush(contact);

        // Get all the contactList where remark does not contain DEFAULT_REMARK
        defaultContactShouldNotBeFound("remark.doesNotContain=" + DEFAULT_REMARK);

        // Get all the contactList where remark does not contain UPDATED_REMARK
        defaultContactShouldBeFound("remark.doesNotContain=" + UPDATED_REMARK);
    }

    @Test
    @Transactional
    void getAllContactsByTeamContactIsEqualToSomething() throws Exception {
        // Initialize the database
        contactRepository.saveAndFlush(contact);
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
        contact.addTeamContact(teamContact);
        contactRepository.saveAndFlush(contact);
        Long teamContactId = teamContact.getId();

        // Get all the contactList where teamContact equals to teamContactId
        defaultContactShouldBeFound("teamContactId.equals=" + teamContactId);

        // Get all the contactList where teamContact equals to (teamContactId + 1)
        defaultContactShouldNotBeFound("teamContactId.equals=" + (teamContactId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultContactShouldBeFound(String filter) throws Exception {
        restContactMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(contact.getId().intValue())))
            .andExpect(jsonPath("$.[*].firstname").value(hasItem(DEFAULT_FIRSTNAME)))
            .andExpect(jsonPath("$.[*].lastname").value(hasItem(DEFAULT_LASTNAME)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)))
            .andExpect(jsonPath("$.[*].remark").value(hasItem(DEFAULT_REMARK)))
            .andExpect(jsonPath("$.[*].imageContentType").value(hasItem(DEFAULT_IMAGE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].image").value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE))));

        // Check, that the count call also returns 1
        restContactMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultContactShouldNotBeFound(String filter) throws Exception {
        restContactMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restContactMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingContact() throws Exception {
        // Get the contact
        restContactMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewContact() throws Exception {
        // Initialize the database
        contactRepository.saveAndFlush(contact);

        int databaseSizeBeforeUpdate = contactRepository.findAll().size();

        // Update the contact
        Contact updatedContact = contactRepository.findById(contact.getId()).get();
        // Disconnect from session so that the updates on updatedContact are not directly saved in db
        em.detach(updatedContact);
        updatedContact
            .firstname(UPDATED_FIRSTNAME)
            .lastname(UPDATED_LASTNAME)
            .email(UPDATED_EMAIL)
            .phone(UPDATED_PHONE)
            .remark(UPDATED_REMARK)
            .image(UPDATED_IMAGE)
            .imageContentType(UPDATED_IMAGE_CONTENT_TYPE);

        restContactMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedContact.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedContact))
            )
            .andExpect(status().isOk());

        // Validate the Contact in the database
        List<Contact> contactList = contactRepository.findAll();
        assertThat(contactList).hasSize(databaseSizeBeforeUpdate);
        Contact testContact = contactList.get(contactList.size() - 1);
        assertThat(testContact.getFirstname()).isEqualTo(UPDATED_FIRSTNAME);
        assertThat(testContact.getLastname()).isEqualTo(UPDATED_LASTNAME);
        assertThat(testContact.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testContact.getPhone()).isEqualTo(UPDATED_PHONE);
        assertThat(testContact.getRemark()).isEqualTo(UPDATED_REMARK);
        assertThat(testContact.getImage()).isEqualTo(UPDATED_IMAGE);
        assertThat(testContact.getImageContentType()).isEqualTo(UPDATED_IMAGE_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void putNonExistingContact() throws Exception {
        int databaseSizeBeforeUpdate = contactRepository.findAll().size();
        contact.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restContactMockMvc
            .perform(
                put(ENTITY_API_URL_ID, contact.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(contact))
            )
            .andExpect(status().isBadRequest());

        // Validate the Contact in the database
        List<Contact> contactList = contactRepository.findAll();
        assertThat(contactList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchContact() throws Exception {
        int databaseSizeBeforeUpdate = contactRepository.findAll().size();
        contact.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restContactMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(contact))
            )
            .andExpect(status().isBadRequest());

        // Validate the Contact in the database
        List<Contact> contactList = contactRepository.findAll();
        assertThat(contactList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamContact() throws Exception {
        int databaseSizeBeforeUpdate = contactRepository.findAll().size();
        contact.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restContactMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(contact)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Contact in the database
        List<Contact> contactList = contactRepository.findAll();
        assertThat(contactList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateContactWithPatch() throws Exception {
        // Initialize the database
        contactRepository.saveAndFlush(contact);

        int databaseSizeBeforeUpdate = contactRepository.findAll().size();

        // Update the contact using partial update
        Contact partialUpdatedContact = new Contact();
        partialUpdatedContact.setId(contact.getId());

        partialUpdatedContact.phone(UPDATED_PHONE);

        restContactMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedContact.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedContact))
            )
            .andExpect(status().isOk());

        // Validate the Contact in the database
        List<Contact> contactList = contactRepository.findAll();
        assertThat(contactList).hasSize(databaseSizeBeforeUpdate);
        Contact testContact = contactList.get(contactList.size() - 1);
        assertThat(testContact.getFirstname()).isEqualTo(DEFAULT_FIRSTNAME);
        assertThat(testContact.getLastname()).isEqualTo(DEFAULT_LASTNAME);
        assertThat(testContact.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testContact.getPhone()).isEqualTo(UPDATED_PHONE);
        assertThat(testContact.getRemark()).isEqualTo(DEFAULT_REMARK);
        assertThat(testContact.getImage()).isEqualTo(DEFAULT_IMAGE);
        assertThat(testContact.getImageContentType()).isEqualTo(DEFAULT_IMAGE_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void fullUpdateContactWithPatch() throws Exception {
        // Initialize the database
        contactRepository.saveAndFlush(contact);

        int databaseSizeBeforeUpdate = contactRepository.findAll().size();

        // Update the contact using partial update
        Contact partialUpdatedContact = new Contact();
        partialUpdatedContact.setId(contact.getId());

        partialUpdatedContact
            .firstname(UPDATED_FIRSTNAME)
            .lastname(UPDATED_LASTNAME)
            .email(UPDATED_EMAIL)
            .phone(UPDATED_PHONE)
            .remark(UPDATED_REMARK)
            .image(UPDATED_IMAGE)
            .imageContentType(UPDATED_IMAGE_CONTENT_TYPE);

        restContactMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedContact.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedContact))
            )
            .andExpect(status().isOk());

        // Validate the Contact in the database
        List<Contact> contactList = contactRepository.findAll();
        assertThat(contactList).hasSize(databaseSizeBeforeUpdate);
        Contact testContact = contactList.get(contactList.size() - 1);
        assertThat(testContact.getFirstname()).isEqualTo(UPDATED_FIRSTNAME);
        assertThat(testContact.getLastname()).isEqualTo(UPDATED_LASTNAME);
        assertThat(testContact.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testContact.getPhone()).isEqualTo(UPDATED_PHONE);
        assertThat(testContact.getRemark()).isEqualTo(UPDATED_REMARK);
        assertThat(testContact.getImage()).isEqualTo(UPDATED_IMAGE);
        assertThat(testContact.getImageContentType()).isEqualTo(UPDATED_IMAGE_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void patchNonExistingContact() throws Exception {
        int databaseSizeBeforeUpdate = contactRepository.findAll().size();
        contact.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restContactMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, contact.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(contact))
            )
            .andExpect(status().isBadRequest());

        // Validate the Contact in the database
        List<Contact> contactList = contactRepository.findAll();
        assertThat(contactList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchContact() throws Exception {
        int databaseSizeBeforeUpdate = contactRepository.findAll().size();
        contact.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restContactMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(contact))
            )
            .andExpect(status().isBadRequest());

        // Validate the Contact in the database
        List<Contact> contactList = contactRepository.findAll();
        assertThat(contactList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamContact() throws Exception {
        int databaseSizeBeforeUpdate = contactRepository.findAll().size();
        contact.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restContactMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(contact)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Contact in the database
        List<Contact> contactList = contactRepository.findAll();
        assertThat(contactList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteContact() throws Exception {
        // Initialize the database
        contactRepository.saveAndFlush(contact);

        int databaseSizeBeforeDelete = contactRepository.findAll().size();

        // Delete the contact
        restContactMockMvc
            .perform(delete(ENTITY_API_URL_ID, contact.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Contact> contactList = contactRepository.findAll();
        assertThat(contactList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
