package com.computacenter.yocoma.service.criteria;

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
 * Criteria class for the {@link com.computacenter.yocoma.domain.Team} entity. This class is used
 * in {@link com.computacenter.yocoma.web.rest.TeamResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /teams?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
public class TeamCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private StringFilter motto;

    private StringFilter description;

    private LongFilter teamContactId;

    private StringFilter search;

    private Boolean distinct;

    public TeamCriteria() {}

    public TeamCriteria(TeamCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.motto = other.motto == null ? null : other.motto.copy();
        this.description = other.description == null ? null : other.description.copy();
        this.teamContactId = other.teamContactId == null ? null : other.teamContactId.copy();
        this.search = other.search == null ? null : other.search.copy();
        this.distinct = other.distinct;
    }

    @Override
    public TeamCriteria copy() {
        return new TeamCriteria(this);
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

    public StringFilter getName() {
        return name;
    }

    public StringFilter name() {
        if (name == null) {
            name = new StringFilter();
        }
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public StringFilter getMotto() {
        return motto;
    }

    public StringFilter motto() {
        if (motto == null) {
            motto = new StringFilter();
        }
        return motto;
    }

    public void setMotto(StringFilter motto) {
        this.motto = motto;
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

    public LongFilter getTeamContactId() {
        return teamContactId;
    }

    public LongFilter teamContactId() {
        if (teamContactId == null) {
            teamContactId = new LongFilter();
        }
        return teamContactId;
    }

    public void setTeamContactId(LongFilter teamContactId) {
        this.teamContactId = teamContactId;
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
        final TeamCriteria that = (TeamCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(motto, that.motto) &&
            Objects.equals(description, that.description) &&
            Objects.equals(teamContactId, that.teamContactId) &&
                Objects.equals(search, that.search) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, motto, description, teamContactId, search, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TeamCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (name != null ? "name=" + name + ", " : "") +
            (motto != null ? "motto=" + motto + ", " : "") +
            (description != null ? "description=" + description + ", " : "") +
            (teamContactId != null ? "teamContactId=" + teamContactId + ", " : "") +
            (search != null ? "search=" + search + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
