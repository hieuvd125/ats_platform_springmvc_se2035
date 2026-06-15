package org.ats.dao;

import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.ats.entities.Job;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class JobDaoImpl implements JobDao {
    private final SessionFactory sessionFactory;

    @Override
    @Transactional
    public Job createJob(Job job) {
        Session session = sessionFactory.openSession();
        session.persist(job);
        return job;
    }

    @Override
    public List<Job> findByTitle(String title) {

        Session session = sessionFactory.openSession();
        // JPQL
        TypedQuery<Job> query = session.createQuery("" +
                "SELECT j FROM Job j WHERE j.title LIKE :param", Job.class);

        query.setParameter("param", "%" + title + "%"); // %java%

        return query.getResultList();
    }

    @Override
    public List<Job> findAll(String keyword) {

        Session session = sessionFactory.openSession();
        Query<Job> query = session.createQuery("FROM Job j WHERE j.title LIKE :keyword OR j.description LIKE :keyword");
        query.setParameter("keyword", keyword);


        return query.getResultList();
    }

    @Override
    public List<Job> findAll() {
        Session session = sessionFactory.openSession();
        // Type safe
        return session.createQuery("FROM Job", Job.class).getResultList();
    }

    @Override
    public Job updateJob(Job job) {
        return null;
    }
}
