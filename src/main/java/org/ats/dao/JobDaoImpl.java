package org.ats.dao;

import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.ats.entities.Job;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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

//    @Override
//    public List<Job> findAll(String keyword) {
//
//        Session session = sessionFactory.openSession();
//        Query<Job> query = session.createQuery("FROM Job j WHERE j.title LIKE :keyword OR j.description LIKE :keyword");
//        query.setParameter("keyword", keyword);
//
//        return query.getResultList();
//    }

    @Override
    @Transactional(readOnly = true)
    public List<Job> findAll(String keyword) {
        Session session = sessionFactory.getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Job> cq = cb.createQuery(Job.class);
        Root<Job> root = cq.from(Job.class);

        List<Predicate> predicates = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            String likePattern = "%" + keyword + "%";

            Predicate titleLike = cb.like(root.get("title"), likePattern, '\\');
            predicates.add(titleLike);
        }

        cq.select(root).where(predicates.toArray(new Predicate[0]));

        return session.createQuery(cq).getResultList();
    }

//    @Override
//    public List<Job> findAll() {
//        Session session = sessionFactory.openSession();
//        // Type safe
//        return session.createQuery("FROM Job", Job.class).getResultList();
//    }

    @Override
    @Transactional(readOnly = true)
    public List<Job> findAll() {
        Session session = sessionFactory.getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Job> cq = cb.createQuery(Job.class);
        Root<Job> root = cq.from(Job.class);

        root.fetch("department", JoinType.LEFT);

        cq.select(root);

        return session.createQuery(cq).getResultList();
    }

    @Override
    public Job updateJob(Job job) {
        return null;
    }

    // === DEMO SUBQUERIES (Slide 6) ===
    @Transactional(readOnly = true)
    public List<Job> findJobsWithHighMinSalary() {
        Session session = sessionFactory.getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Job> cq = cb.createQuery(Job.class);
        Root<Job> root = cq.from(Job.class);

        Subquery<Double> subquery = cq.subquery(Double.class);
        Root<Job> subRoot = subquery.from(Job.class);

        subquery.select(cb.avg(subRoot.get("minSalary")));

        cq.select(root).where(cb.gt(root.get("minSalary"), subquery));

        return session.createQuery(cq).getResultList();
    }
}
