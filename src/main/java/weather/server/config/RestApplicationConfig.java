package weather.server.config;

import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.QueueConfig;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.util.ArrayList;
import java.util.Collection;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class RestApplicationConfig extends WebSecurityConfigurerAdapter {

    // Задержка между запросами к API ресурсу (ms)
    public static final int DELAY_BETWEEN_QUERY = 1000;

    // Время жизни кэша (сек.) По умолчанию 10 минут.
    public static final int MAP_TTL_SEC = 60 * 10;

    // Очередь запросов с клиентской части
    public static final String RQ_QUEUE_NAME = "rqQueue";

    // Основная кэш-мапа ответов
    public static final String RS_MAP_BY_ID = "rsByCityId";

    // Связующая мапа: запрос (city,country) -> id ответа
    public static final String RQ_MAP_CITY_COUNTRY_NAME = "rqByCityCountry";

    // Связующая мапа: запрос (geo координаты) -> id ответа
    public static final String RQ_MAP_GEO_NAME = "rqByGeoPoint";

    // Связующая мапа: запрос (city) -> id ответа
    public static final String RQ_MAP_CITY_NAME = "rqByCityName";

    /**
     * Конфигурация HTTP.
     * По умолчанию - базовая авторизация
     *
     * @param http http entity
     * @throws Exception exception if fail
     */
    public void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();

        http.authorizeRequests()
                .anyRequest().authenticated()
//                .anyRequest().anonymous()
                .and()
                .httpBasic()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Configuration
    class SecurityConfig extends GlobalAuthenticationConfigurerAdapter {

        @Override
        public void init(AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(userDetailsService());
        }

        @Bean
        UserDetailsService userDetailsService() {

            // Hardcoded список пользователей

            Collection<UserDetails> users = new ArrayList<>();

            Collection<GrantedAuthority> grantedAuthority = new ArrayList<>();
            grantedAuthority.add(new SimpleGrantedAuthority("ROLE_USER"));

            users.add(new User("user1", "user1", grantedAuthority));
            users.add(new User("user2", "user2", grantedAuthority));
            users.add(new User("user3", "user3", grantedAuthority));

            return new InMemoryUserDetailsManager(users);
        }
    }

    /**
     * Конфигурация Hazelcast.
     *  Храним in-memory thread-safe:
     *  - очередь запросов клиентов (String request)
     *  - основная кэш-мапа: ID => Weather
     *  связующие (request => ID):
     *  - запрос по полному GEO месту: City,Country
     *  - запрос по GEO координатам lat,lon
     *  - запрос по населенному пункту: City
     * @return
     */
    @Bean
    public Config hazelCastConfig() {
        Config config = new Config();
        config
            .addQueueConfig(
                    new QueueConfig().setName(RQ_QUEUE_NAME))
            .addMapConfig(
                    new MapConfig()
                            .setName(RS_MAP_BY_ID)
                            .setTimeToLiveSeconds(MAP_TTL_SEC))
            .addMapConfig(
                    new MapConfig()
                            .setName(RQ_MAP_CITY_COUNTRY_NAME)
                            .setTimeToLiveSeconds(MAP_TTL_SEC))
            .addMapConfig(
                    new MapConfig()
                            .setName(RQ_MAP_GEO_NAME)
                            .setTimeToLiveSeconds(MAP_TTL_SEC))
            .addMapConfig(
                    new MapConfig()
                            .setName(RQ_MAP_CITY_NAME)
                            .setTimeToLiveSeconds(MAP_TTL_SEC));

        return config;
    }

}
