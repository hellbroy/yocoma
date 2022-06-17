package com.computacenter.yocoma.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.computacenter.yocoma.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TeamContactTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TeamContact.class);
        TeamContact teamContact1 = new TeamContact();
        teamContact1.setId(1L);
        TeamContact teamContact2 = new TeamContact();
        teamContact2.setId(teamContact1.getId());
        assertThat(teamContact1).isEqualTo(teamContact2);
        teamContact2.setId(2L);
        assertThat(teamContact1).isNotEqualTo(teamContact2);
        teamContact1.setId(null);
        assertThat(teamContact1).isNotEqualTo(teamContact2);
    }
}
