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
 * Criteria class for the {@link com.computacenter.yocoma.domain.Contact} entity. This class is used
 * in {@link com.computacenter.yocoma.web.rest.ContactResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /contacts?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
public class ContactCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter firstname;

    private StringFilter lastname;

    private StringFilter email;

    private StringFilter phone;

    private StringFilter remark;

    private LongFilter teamContactId;

    private StringFilter search;

    private Boolean distinct;

    public ContactCriteria() {}

    public ContactCriteria(ContactCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.firstname = other.firstname == null ? null : other.firstname.copy();
        this.lastname = other.lastname == null ? null : other.lastname.copy();
        this.email = other.email == null ? null : other.email.copy();
        this.phone = other.phone == null ? null : other.phone.copy();
        this.remark = other.remark == null ? null : other.remark.copy();
        this.teamContactId = other.teamContactId == null ? null : other.teamContactId.copy();
        this.search = other.search == null ? null : other.search.copy();
        this.distinct = other.distinct;
    }

    @Override
    public ContactCriteria copy() {
        return new ContactCriteria(this);
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

    public StringFilter getFirstname() {
        return firstname;
    }

    public StringFilter firstname() {
        if (firstname == null) {
            firstname = new StringFilter();
        }
        return firstname;
    }

    public void setFirstname(StringFilter firstname) {
        this.firstname = firstname;
    }

    public StringFilter getLastname() {
        return lastname;
    }

    public StringFilter lastname() {
        if (lastname == null) {
            lastname = new StringFilter();
        }
        return lastname;
    }

    public void setLastname(StringFilter lastname) {
        this.lastname = lastname;
    }

    public StringFilter getEmail() {
        return email;
    }

    public StringFilter email() {
        if (email == null) {
            email = new StringFilter();
        }
        return email;
    }

    public void setEmail(StringFilter email) {
        this.email = email;
    }

    public StringFilter getPhone() {
        return phone;
    }

    public StringFilter phone() {
        if (phone == null) {
            phone = new StringFilter();
        }
        return phone;
    }

    public void setPhone(StringFilter phone) {
        this.phone = phone;
    }

    public StringFilter getRemark() {
        return remark;
    }

    public StringFilter remark() {
        if (remark == null) {
            remark = new StringFilter();
        }
        return remark;
    }

    public void setRemark(StringFilter remark) {
        this.remark = remark;
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
        final ContactCriteria that = (ContactCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(firstname, that.firstname) &&
            Objects.equals(lastname, that.lastname) &&
            Objects.equals(email, that.email) &&
            Objects.equals(phone, that.phone) &&
            Objects.equals(remark, that.remark) &&
            Objects.equals(teamContactId, that.teamContactId) &&
            Objects.equals(search, that.search) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstname, lastname, email, phone, remark, teamContactId, search, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ContactCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (firstname != null ? "firstname=" + firstname + ", " : "") +
            (lastname != null ? "lastname=" + lastname + ", " : "") +
            (email != null ? "email=" + email + ", " : "") +
            (phone != null ? "phone=" + phone + ", " : "") +
            (remark != null ? "remark=" + remark + ", " : "") +
            (teamContactId != null ? "teamContactId=" + teamContactId + ", " : "") +
            (search != null ? "search=" + search + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
