package org.example.realworldapi.infrastructure.repository.panache;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import lombok.AllArgsConstructor;
import org.example.realworldapi.domain.model.repository.NewUserRepository;
import org.example.realworldapi.domain.model.user.User;
import org.example.realworldapi.domain.model.user.UserModelBuilder;
import org.example.realworldapi.infrastructure.repository.hibernate.entity.UserEntity;

import javax.enterprise.context.ApplicationScoped;
import java.util.Optional;
import java.util.UUID;

import static io.quarkus.panache.common.Parameters.with;

@ApplicationScoped
@AllArgsConstructor
public class NewUserRepositoryPanache
    implements NewUserRepository, PanacheRepositoryBase<UserEntity, UUID> {

  private final UserModelBuilder userBuilder;

  @Override
  public void save(User user) {
    persist(new UserEntity(user));
  }

  @Override
  public boolean existsBy(String field, String value) {
    return count("upper(" + field + ")", value.toUpperCase().trim()) > 0;
  }

  @Override
  public Optional<User> findByEmail(String email) {
    return find("upper(email)", email.toUpperCase().trim()).firstResultOptional().map(this::user);
  }

  @Override
  public Optional<User> findUserById(UUID id) {
    return findByIdOptional(id).map(this::user);
  }

  @Override
  public boolean existsUsername(UUID excludeId, String username) {
    return count(
            "id != :excludeId and upper(username) = :username",
            with("excludeId", excludeId).and("username", username.toUpperCase().trim()))
        > 0;
  }

  @Override
  public boolean existsEmail(UUID excludeId, String email) {
    return count(
            "id != :excludeId and upper(email) = :email",
            with("excludeId", excludeId).and("email", email.toUpperCase().trim()))
        > 0;
  }

  @Override
  public void update(User user) {
    final var userEntity = findById(user.getId());
    userEntity.update(user);
  }

  private User user(UserEntity userEntity) {
    final var id = userEntity.getId();
    final var username = userEntity.getUsername();
    final var bio = userEntity.getBio();
    final var image = userEntity.getImage();
    final var password = userEntity.getPassword();
    final var email = userEntity.getEmail();
    return userBuilder.build(id, username, bio, image, password, email);
  }
}
