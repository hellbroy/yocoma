package com.computacenter.yocoma.service.criteria;

import com.computacenter.yocoma.domain.enumeration.RoleType;
import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.DoubleFilter;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.FloatFilter;
import tech.jhipster.service.filter.IntegerFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link com.computacenter.yocoma.domain.TeamContact} entity. This class is used
 * in {@link com.computacenter.yocoma.web.rest.TeamContactResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /team-contacts?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
public class TeamContactCriteria implements Serializable, Criteria {

    /**
     * Class for filtering RoleType
     */
    public static class RoleTypeFilter extends Filter<RoleType> {

        public RoleTypeFilter() {}

        public RoleTypeFilter(RoleTypeFilter filter) {
            super(filter);
        }

        @Override
        public RoleTypeFilter copy() {
            return new RoleTypeFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private RoleTypeFilter roleType;

    private StringFilter role;

    private StringFilter description;

    private LongFilter contactId;

    private LongFilter teamId;

    private StringFilter search;

    private Boolean distinct;

    public TeamContactCriteria() {}

    public TeamContactCriteria(TeamContactCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.roleType = other.roleType == null ? null : other.roleType.copy();
        this.role = other.role == null ? null : other.role.copy();
        this.description = other.description == null ? null : other.description.copy();
        this.contactId = other.contactId == null ? null : other.contactId.copy();
        this.teamId = other.teamId == null ? null : other.teamId.copy();
        this.search = other.search == null ? null : other.search.copy();
        this.distinct = other.distinct;
    }

    @Override
    public TeamContactCriteria copy() {
        return new TeamContactCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public RoleTypeFilter getRoleType() {
        return roleType;
    }

    public RoleTypeFilter roleType() {
        if (roleType == null) {
            roleType = new RoleTypeFilter();
        }
        return roleType;
    }

    public void setRoleType(RoleTypeFilter roleType) {
        this.roleType = roleType;
    }

    public StringFilter getRole() {
        return role;
    }

    public StringFilter role() {
        if (role == null) {
            role = new StringFilter();
        }
        return role;
    }

    public void setRole(StringFilter role) {
        this.role = role;
    }

    public StringFilter getDescription() {
        return description;
    }

    public StringFilter description() {
        if (description == null) {
            description = new StringFilter();
        }
        return description;
    }

    public void setDescription(StringFilter description) {
        this.description = description;
    }

    public LongFilter getContactId() {
        return contactId;
    }

    public LongFilter contactId() {
        if (contactId == null) {
            contactId = new LongFilter();
        }
        return contactId;
    }

    public void setContactId(LongFilter contactId) {
        this.contactId = contactId;
    }

    public LongFilter getTeamId() {
        return teamId;
    }

    public LongFilter teamId() {
        if (teamId == null) {
            teamId = new LongFilter();
        }
        return teamId;
    }

    public void setTeamId(LongFilter teamId) {
        this.teamId = teamId;
    }

    public StringFilter getSearch() {
        return search;
    }

    public StringFilter search() {
        if (search == null) {
            search = new StringFilter();
        }
        return search;
    }

    public void setSearch(StringFilter search) {
        this.search = search;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final TeamContactCriteria that = (TeamContactCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(roleType, that.roleType) &&
            Objects.equals(role, that.role) &&
            Objects.equals(description, that.description) &&
            Objects.equals(contactId, that.contactId) &&
            Objects.equals(teamId, that.teamId) &&
            Objects.equals(search, that.search) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, roleType, role, description, contactId, teamId, search, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TeamContactCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (roleType != null ? "roleType=" + roleType + ", " : "") +
            (role != null ? "role=" + role + ", " : "") +
            (description != null ? "description=" + description + ", " : "") +
            (contactId != null ? "contactId=" + contactId + ", " : "") +
            (teamId != null ? "teamId=" + teamId + ", " : "") +
            (search != null ? "search=" + search + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
