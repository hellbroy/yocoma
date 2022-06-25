package com.computacenter.yocoma.service;

import com.computacenter.yocoma.domain.Contact;
import com.computacenter.yocoma.repository.ContactRepository;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Contact}.
 */
@Service
@Transactional
public class ContactService {

    private final Logger log = LoggerFactory.getLogger(ContactService.class);

    private final ContactRepository contactRepository;

    public ContactService(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    /**
     * Save a contact.
     *
     * @param contact the entity to save.
     * @return the persisted entity.
     */
    public Contact save(Contact contact) {
        log.debug("Request to save Contact : {}", contact);
        return contactRepository.save(contact);
    }

    /**
     * Update a contact.
     *
     * @param contact the entity to save.
     * @return the persisted entity.
     */
    public Contact update(Contact contact) {
        log.debug("Request to save Contact : {}", contact);
        return contactRepository.save(contact);
    }

    /**
     * Partially update a contact.
     *
     * @param contact the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Contact> partialUpdate(Contact contact) {
        log.debug("Request to partially update Contact : {}", contact);

        return contactRepository
            .findById(contact.getId())
            .map(existingContact -> {
                if (contact.getFirstname() != null) {
                    existingContact.setFirstname(contact.getFirstname());
                }
                if (contact.getLastname() != null) {
                    existingContact.setLastname(contact.getLastname());
                }
                if (contact.getEmail() != null) {
                    existingContact.setEmail(contact.getEmail());
                }
                if (contact.getPhone() != null) {
                    existingContact.setPhone(contact.getPhone());
                }
                if (contact.getRemark() != null) {
                    existingContact.setRemark(contact.getRemark());
                }
                if (contact.getImage() != null) {
                    existingContact.setImage(contact.getImage());
                }
                if (contact.getImageContentType() != null) {
                    existingContact.setImageContentType(contact.getImageContentType());
                }

                return existingContact;
            })
            .map(contactRepository::save);
    }

    /**
     * Get all the contacts.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<Contact> findAll() {
        log.debug("Request to get all Contacts");
        return contactRepository.findAll();
    }

    /**
     * Get one contact by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Contact> findOne(Long id) {
        log.debug("Request to get Contact : {}", id);
        return contactRepository.findById(id);
    }

    /**
     * Delete the contact by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Contact : {}", id);
        contactRepository.deleteById(id);
    }
}
