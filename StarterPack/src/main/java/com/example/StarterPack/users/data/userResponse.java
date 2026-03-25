package com.example.StarterPack.users.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;

import com.example.StarterPack.users.Role;
import com.example.StarterPack.users.User;
import com.example.StarterPack.utils.Client;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Client
public class userResponse {
    String email;
    String password;
    @Nullable
    String firstName;
    @Nullable
    String lastName;
    private Long id;
    private Role role;
    private List<String> authorities = new ArrayList<>();
    String profileImageUrl;
    public userResponse(User user, Collection<? extends GrantedAuthority> authorities) {
        this.id = user.getId();
        this.role = user.getRole();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.profileImageUrl = user.getProfileImageUrl();
        // user.getConnectedAccounts().forEach((provider) -> {
        // this.connectedAccounts.add(new ConnectedAccountResponse(provider.getProvider(), provider.getConnectedAt()));
        // });
        authorities.forEach(authority -> {
        this.authorities.add(authority.getAuthority());
        });
  }

}
