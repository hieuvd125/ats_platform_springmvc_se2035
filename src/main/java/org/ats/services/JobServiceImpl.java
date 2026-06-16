package org.ats.services;

import lombok.RequiredArgsConstructor;
import org.ats.dao.JobDao;
import org.ats.dto.JobRequest;
import org.ats.entities.*;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {
    private final JobDao jobDao;

    @Override
    public Job createJob(JobRequest jobRequest) {
        // Validate

        return jobDao.createJob(toEntity(jobRequest));
    }

    @Override
    public List<Job> findByTitle(String title) {
        return List.of();
    }

    @Override
    public List<Job> getAll(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return jobDao.findAll();
        }

        System.out.println("--- TEST DEMO SUBQUERY LOG ---");
        List<Job> highSalaryJobs = jobDao.findJobsWithHighMinSalary();
        System.out.println("Số lượng job có lương trên trung bình: " + highSalaryJobs.size());
        System.out.println("--------------------------------");

        System.out.println("--- TEST DEMO CRITERIA UPDATE ---");
        int updatedRows = jobDao.safeUpdateExpiredJobs();
        System.out.println("Số lượng job hết hạn đã được cập nhật thành EXPIRED: " + updatedRows);
        System.out.println("--------------------------------");

        System.out.println("--- TEST DEMO CRITERIA DELETE ---");
        int deletedRows = jobDao.deleteMarkedJobs();
        System.out.println("Số lượng job đánh dấu xóa đã bị xóa sổ: " + deletedRows);
        System.out.println("--------------------------------");

        return jobDao.findAll(keyword);
    }

    private Job toEntity(JobRequest jobRequest) {
        Set<JobSkill> jobSkills = jobRequest.getSkillIds().stream().map((skillId) -> {
            JobSkill jobSkill = new JobSkill();
            jobSkill.setSkill(Skill.builder().id(skillId).build()); // 1, 3

            return jobSkill;
        }).collect(Collectors.toSet());


        Job job = Job.builder()
                .id(jobRequest.getId())
                .title(jobRequest.getTitle())
                .deadline(OffsetDateTime.of(jobRequest.getDeadline(), LocalTime.now(), ZoneOffset.ofHours(7)))
                .description(jobRequest.getDescription())
                .location(jobRequest.getLocation())
                .maxSalary(jobRequest.getMaxSalary())
                .minSalary(jobRequest.getMinSalary())
                .department(Department.builder().id(jobRequest.getDepartmentId()).build())
                .status(JobStatus.DRAFT.toString())
                .build();

        for (JobSkill jobSkill : jobSkills) {
            jobSkill.setJob(job);
        }

        job.setSkills(jobSkills);

        return job;
    }
}
