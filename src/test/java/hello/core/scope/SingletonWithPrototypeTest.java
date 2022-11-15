package hello.core.scope;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Scope;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Provider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.in;

public class SingletonWithPrototypeTest {
    @Test
    void prototypeFind(){
        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(PrototypeBean.class);
        PrototypeBean prototypeBean1 = ac.getBean(PrototypeBean.class);
        prototypeBean1.addCount();
        assertThat(prototypeBean1.getCount()).isEqualTo(1);

        PrototypeBean prototypeBean2 = ac.getBean(PrototypeBean.class);
        prototypeBean2.addCount();
        assertThat(prototypeBean2.getCount()).isEqualTo(1);
    }

    @Test
    void singletonClientUsePrototype(){
        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(ClientBean.class, PrototypeBean.class);
        ClientBean ClientBean1 = ac.getBean(ClientBean.class);
        int count1 = ClientBean1.logic();
        assertThat(count1).isEqualTo(1);
        ClientBean ClientBean2 = ac.getBean(ClientBean.class);
        int count2 = ClientBean2.logic();
        assertThat(count2).isEqualTo(1);
    }
    @Scope("singleton")
    @RequiredArgsConstructor
    static class ClientBean{


        /**
         * Dependency Lookup(DL)
         * */
        @Autowired
        private Provider<PrototypeBean> prototypeBeanProvider;

        public int logic(){
            PrototypeBean prototypeBean = prototypeBeanProvider.get();
            prototypeBean.addCount();
            int count = prototypeBean.getCount();
            return count;
        }
    }
    @Scope("prototype")
    static class PrototypeBean{
        private int count = 0;

        public void addCount(){
            count++;
        }
        public int getCount(){
            return count;
        }
        @PostConstruct // 자바 표준 애노테이션
        public void init(){
            System.out.println("PrototypeBean.init" + this);
        }
        @PreDestroy // 자바 표준 애노테이션
        public void destroy(){
            System.out.println("PrototypeBean.destroy");
        }


    }
}
