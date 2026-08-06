@Entity
@Table(name="users")
@Getter
@Setter
public class User extends BaseEntity {


    private String username;

    private String password;

    private String firstName;

    private String lastName;

    private String email;

    private String phone;

    private Boolean active = true;


    @ManyToOne
    @JoinColumn(name="role_id")
    private Role role;

}