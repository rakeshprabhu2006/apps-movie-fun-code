package org.superbiz.moviefun;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionOperations;
import org.springframework.transaction.support.TransactionTemplate;
import org.superbiz.moviefun.movies.DatabaseServiceCredentialsConfiguration;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class
})
public class Application {

    public static void main(String... args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public ServletRegistrationBean actionServletRegistration(ActionServlet actionServlet) {
        return new ServletRegistrationBean(actionServlet, "/moviefun/*");
    }

    @Bean
    public DatabaseServiceCredentialsConfiguration getDatabaseConfiguration(){

        System.out.println("************************************VCAP****************" + System.getenv ("VCAP_SERVICES"));
        return new DatabaseServiceCredentialsConfiguration(System.getenv("VCAP_SERVICES"));
    }

    @Bean
    public DataSource albumsDataSource(DatabaseServiceCredentialsConfiguration serviceCredentials) {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setURL(serviceCredentials.jdbcUrl("albums-mysql", "p-mysql"));
        return dataSource;
    }


    @Bean
    public DataSource moviesDataSource(DatabaseServiceCredentialsConfiguration serviceCredentials) {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setURL(serviceCredentials.jdbcUrl("movies-mysql", "p-mysql"));
        return dataSource;
    }

    @Bean
    public HibernateJpaVendorAdapter jpaVendorAdapterAuth(){
        final HibernateJpaVendorAdapter adapater=new HibernateJpaVendorAdapter();
        adapater.setDatabase(Database.MYSQL);
        adapater.setGenerateDdl(true);
        adapater.setDatabasePlatform("org.hibernate.dialect.MySQL5Dialect");
        return adapater;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerBeanForAlbums(@Autowired
                                                                                 @Qualifier("albumsDataSource")DataSource dataSource, HibernateJpaVendorAdapter hibernateJpaVendorAdapter){

        LocalContainerEntityManagerFactoryBean bean = new LocalContainerEntityManagerFactoryBean();
        bean.setDataSource(dataSource);
        bean.setPackagesToScan("org.superbiz.moviefun.albums");
        bean.setJpaVendorAdapter(hibernateJpaVendorAdapter);
        bean.setPersistenceUnitName("albums");
        return bean;

    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerBeanForMovies(@Autowired
                                                                                 @Qualifier("moviesDataSource")DataSource dataSource, HibernateJpaVendorAdapter hibernateJpaVendorAdapter){

        LocalContainerEntityManagerFactoryBean bean = new LocalContainerEntityManagerFactoryBean();
        bean.setDataSource(dataSource);
        bean.setPackagesToScan("org.superbiz.moviefun.movies");
        bean.setJpaVendorAdapter(hibernateJpaVendorAdapter);
        bean.setPersistenceUnitName("movies");
        return bean;

    }

    @Bean
    public PlatformTransactionManager albumsPlatformTransactionManager(@Autowired @Qualifier("entityManagerBeanForAlbums") EntityManagerFactory entityManagerFactory){

        JpaTransactionManager jpaTransactionManager  = new JpaTransactionManager();
        jpaTransactionManager.setEntityManagerFactory(entityManagerFactory);
        return jpaTransactionManager;
    }

    @Bean
    public PlatformTransactionManager moviesPlatformTransactionManager(@Autowired @Qualifier("entityManagerBeanForMovies") EntityManagerFactory entityManagerFactory){

        JpaTransactionManager jpaTransactionManager  = new JpaTransactionManager();
        jpaTransactionManager.setEntityManagerFactory(entityManagerFactory);
        return jpaTransactionManager;
    }

    @Bean
    public TransactionOperations transactionOperationsForAlbums(@Autowired @Qualifier("albumsPlatformTransactionManager") PlatformTransactionManager manager){

        TransactionOperations transactionOperations = new TransactionTemplate(manager);
        return transactionOperations;
    }

    @Bean
    public TransactionOperations transactionOperationsForMovies(@Autowired @Qualifier("moviesPlatformTransactionManager") PlatformTransactionManager manager){

        TransactionOperations transactionOperations = new TransactionTemplate(manager);
        return transactionOperations;
    }
}
