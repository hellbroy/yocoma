package com.computacenter.yocoma.domain;

import com.computacenter.yocoma.domain.enumeration.RoleType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * The m-n-relationship between a Team and its Contacts.\nA Team can have multiple contacts.\nA Contact can belong to multiple Teams.
 */
@Schema(
    description = "The m-n-relationship between a Team and its Contacts.\nA Team can have multiple contacts.\nA Contact can belong to multiple Teams."
)
@Entity
@Table(name = "team_contact")
public class TeamContact implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    /**
     * The type of role the contact takes within the team
     */
    @Schema(description = "The type of role the contact takes within the team")
    @Enumerated(EnumType.STRING)
    @Column(name = "role_type")
    private RoleType roleType;

    /**
     * A brief name of the role the contact takes within the team
     */
    @Schema(description = "A brief name of the role the contact takes within the team", required = true)
    @NotNull
    @Column(name = "role", nullable = false)
    private String role;

    /**
     * A more detailed description of the contact's role within the team
     */
    @Schema(description = "A more detailed description of the contact's role within the team")
    @Column(name = "description")
    private String description;

    @ManyToOne
    @JsonIgnoreProperties(value = { "teamContacts" }, allowSetters = true)
    private Contact contact;

    @ManyToOne
    @JsonIgnoreProperties(value = { "teamContacts" }, allowSetters = true)
    private Team team;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public TeamContact id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RoleType getRoleType() {
        return this.roleType;
    }

    public TeamContact roleType(RoleType roleType) {
        this.setRoleType(roleType);
        return this;
    }

    public void setRoleType(RoleType roleType) {
        this.roleType = roleType;
    }

    public String getRole() {
        return this.role;
    }

    public TeamContact role(String role) {
        this.setRole(role);
        return this;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getDescription() {
        return this.description;
    }

    public TeamContact description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Contact getContact() {
        return this.contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public TeamContact contact(Contact contact) {
        this.setContact(contact);
        return this;
    }

    public Team getTeam() {
        return this.team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public TeamContact team(Team team) {
        this.setTeam(team);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TeamContact)) {
            return false;
        }
        return id != null && id.equals(((TeamContact) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TeamContact{" +
            "id=" + getId() +
            ", roleType='" + getRoleType() + "'" +
            ", role='" + getRole() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
