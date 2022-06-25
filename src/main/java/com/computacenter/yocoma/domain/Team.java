package com.computacenter.yocoma.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A Team.
 */
@Entity
@Table(name = "team")
public class Team implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "motto")
    private String motto;

    @Lob
    @Column(name = "logo")
    private byte[] logo;

    @Column(name = "logo_content_type")
    private String logoContentType;

    @Column(name = "description")
    private String description;

    // FetchType.EAGER so that the teamContacts will be loaded.
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "team")
    // No @JsonIgnoreProperties so that "team" and "contact" will be transferred to angular.
    //@JsonIgnoreProperties(value = { "contact", "team" }, allowSetters = true)
    private Set<TeamContact> teamContacts = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Team id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Team name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMotto() {
        return this.motto;
    }

    public Team motto(String motto) {
        this.setMotto(motto);
        return this;
    }

    public void setMotto(String motto) {
        this.motto = motto;
    }

    public byte[] getLogo() {
        return this.logo;
    }

    public Team logo(byte[] logo) {
        this.setLogo(logo);
        return this;
    }

    public void setLogo(byte[] logo) {
        this.logo = logo;
    }

    public String getLogoContentType() {
        return this.logoContentType;
    }

    public Team logoContentType(String logoContentType) {
        this.logoContentType = logoContentType;
        return this;
    }

    public void setLogoContentType(String logoContentType) {
        this.logoContentType = logoContentType;
    }

    public String getDescription() {
        return this.description;
    }

    public Team description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<TeamContact> getTeamContacts() {
        return this.teamContacts;
    }

    public void setTeamContacts(Set<TeamContact> teamContacts) {
        if (this.teamContacts != null) {
            this.teamContacts.forEach(i -> i.setTeam(null));
        }
        if (teamContacts != null) {
            teamContacts.forEach(i -> i.setTeam(this));
        }
        this.teamContacts = teamContacts;
    }

    public Team teamContacts(Set<TeamContact> teamContacts) {
        this.setTeamContacts(teamContacts);
        return this;
    }

    public Team addTeamContact(TeamContact teamContact) {
        this.teamContacts.add(teamContact);
        teamContact.setTeam(this);
        return this;
    }

    public Team removeTeamContact(TeamContact teamContact) {
        this.teamContacts.remove(teamContact);
        teamContact.setTeam(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Team)) {
            return false;
        }
        return id != null && id.equals(((Team) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Team{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", motto='" + getMotto() + "'" +
            ", logo='" + getLogo() + "'" +
            ", logoContentType='" + getLogoContentType() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
