package com.techdegree.dto;

import com.techdegree.validator.PasswordMatches;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@PasswordMatches
public class UserDto {

    // fields

    @NotNull
    @NotEmpty
    private String name;

    @NotNull
    @NotEmpty
    private String username;

    @NotNull
    // taken from here:
    // http://regexlib.com/REDetails.aspx?regexp_id=31
    // tested in UserDto class
    @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,20}$")
    private String password;

    @NotEmpty
    @NotNull
    private String matchingPassword;

    // getters and setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMatchingPassword() {
        return matchingPassword;
    }

    public void setMatchingPassword(String matchingPassword) {
        this.matchingPassword = matchingPassword;
    }

    // constructors

    public UserDto() {

    }

    // overrides

    // equals and hashcode


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserDto userDto = (UserDto) o;

        if (name != null ? !name.equals(userDto.name) : userDto.name != null)
            return false;
        if (username != null ? !username.equals(userDto.username) : userDto.username != null)
            return false;
        if (password != null ? !password.equals(userDto.password) : userDto.password != null)
            return false;
        return matchingPassword != null ? matchingPassword.equals(userDto.matchingPassword) : userDto.matchingPassword == null;

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (matchingPassword != null ? matchingPassword.hashCode() : 0);
        return result;
    }
}
