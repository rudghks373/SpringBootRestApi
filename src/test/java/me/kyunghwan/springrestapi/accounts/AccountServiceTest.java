package me.kyunghwan.springrestapi.accounts;

import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class AccountServiceTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Autowired
    AccountService accountService;

    @Autowired
    AccountRepository accountRepository;

    @Test
    public void findByUsername()  {
        //Given
        String username = "oh";
        String password = "kyung";
        Account account = new Account().builder()
                        .email(username)
                        .password(password)
                        .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                        .build();
        this.accountRepository.save(account);

        //When
        UserDetailsService userDetailsService = (UserDetailsService) accountService;
        UserDetails userDetails = userDetailsService.loadUserByUsername("oh");

        //Then
        assertThat(userDetails.getPassword()).isEqualTo(password);
    }

    @Test
    public void findByUsernameFail(){
        String username = "testtest";

//        //발생할 예외를 미리 적어준다
//        expectedException.expect(UsernameNotFoundException.class);
//        expectedException.expectMessage(Matchers.containsString(username));
//
//        //When
//        accountService.loadUserByUsername(username);

        try {
            accountService.loadUserByUsername(username);
            fail("supposed to be failed");
        }catch (UsernameNotFoundException e){
            assertThat(e.getMessage()).contains(username);
        }


    }

}