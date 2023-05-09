package com.derek.batch4.sample.job.dataAccess;

import com.derek.batch4.sample.core.domain.accounts.Accounts;
import com.derek.batch4.sample.core.domain.accounts.AccountsRepository;
import com.derek.batch4.sample.core.domain.orders.Orders;
import com.derek.batch4.sample.core.domain.orders.OrdersRepository;
import com.derek.batch4.sample.job.jobListener.JobLoggerListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.Collections;

/**
 * desc: 주문 테이블에서 정산테이블로 데이터 이관
 */
@Configuration
public class TrMigrationConfig {
    private final Logger logger = LoggerFactory.getLogger(TrMigrationConfig.class.getName());

    private final OrdersRepository ordersRepository;

    private final AccountsRepository accountsRepository;
    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    public TrMigrationConfig(OrdersRepository ordersRepository, AccountsRepository accountsRepository, JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
        this.ordersRepository = ordersRepository;
        this.accountsRepository = accountsRepository;
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean
    public Job trMigrationJob(Step trMigrationStep) {
        return jobBuilderFactory.get("trMigrationJob")
                .incrementer(new RunIdIncrementer())
                .listener(new JobLoggerListener())
                .start(trMigrationStep)
                .build();
    }

    @JobScope
    @Bean
    public Step trMigrationStep(ItemReader trOrderReader, ItemProcessor trOrderProcessor, ItemWriter trOrderWriter) {
        return stepBuilderFactory.get("trMigrationStep")
                //.tasklet(trMigrationTasklet())
                .<Orders, Accounts>chunk(5)
                .reader(trOrderReader)
                .processor(trOrderProcessor)
                .writer(trOrderWriter)
                /*.writer(new ItemWriter() {
                    @Override
                    public void write(List items) throws Exception {
                        items.forEach(System.out::println);
                    }
                })*/
                .build();
    }


    @StepScope
    @Bean
    public RepositoryItemReader trOrderReader() {
        return new RepositoryItemReaderBuilder<Orders>()
                .name("trOrderReader")
                .repository(ordersRepository)
                .methodName("findAll")
                .pageSize(5)
                .arguments(Arrays.asList())
                .sorts(Collections.singletonMap("id", Sort.Direction.ASC))
                .build()
                ;
    }

    @StepScope
    @Bean
    public ItemProcessor<Orders, Accounts> trOrderProcessor() {
        return new ItemProcessor<Orders, Accounts>() {
            @Override
            public Accounts process(Orders item) throws Exception {
                return new Accounts(item);
            }
        };
    }

    @StepScope
    @Bean
    public RepositoryItemWriter<Accounts> trOrderWriter() {
        return new RepositoryItemWriterBuilder<Accounts>()
                .repository(accountsRepository)
                .methodName("save")
                .build()
                ;
    }

   /**
    *
    * RepositoryItemWriter 사용하지 않고 ItemWriter 사용하여 구현하는 방법
    * */
   /* @StepScope
    @Bean
    public ItemWriter<Accounts> trOrderWriter() {
        return new ItemWriter<Accounts>() {
            @Override
            public void write(List<? extends Accounts> items) throws Exception {
                items.forEach(item -> accountsRepository.save(item));
            }
        };
    }*/

}
