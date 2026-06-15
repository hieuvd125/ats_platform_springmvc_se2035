package org.ats.controller;

import lombok.RequiredArgsConstructor;
import org.ats.dto.JobRequest;
import org.ats.dto.SkillResponse;
import org.ats.entities.Department;
import org.ats.entities.Job;
import org.ats.services.DepartmentService;
import org.ats.services.JobService;
import org.ats.services.SkillService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/jobs")
@RequiredArgsConstructor
public class JobController {
    private final JobService jobService;
    private final DepartmentService departmentService;
    private final SkillService skillService;

    @GetMapping("/detail")
    public ModelAndView showCreateJob() {
        // WWhat do?
        List<Department> departments = departmentService.findAll();
        List<SkillResponse> skills = skillService.findAll();
        // Add to model
        ModelAndView mv = new ModelAndView("jobs/job_detail");
        mv.setViewName("jobs/job_detail");
        mv.addObject("departments", departments);
        mv.addObject("skills", skills);
        mv.addObject("job", new JobRequest());
        mv.addObject("message", "");

        System.out.println("Skills:" + skills);

        return mv;
    }

    @RequestMapping(method = RequestMethod.POST)
    public String createJob(@ModelAttribute(name = "job") JobRequest jobRequest, Model model) {
        System.out.println("Create job" + jobRequest);

        jobService.createJob(jobRequest);
        model.addAttribute("message", "Create a new job successful");

        return "/jobs/job_detail";
    }

    @GetMapping
    public String listAll(@RequestParam(name = "keyword", required = false) String keyword, Model model) {
        String message = (String) model.asMap().get("message");
        System.out.println(keyword);

        // Call Job Service
        List<Job> jobs = jobService.getAll(keyword);
        model.addAttribute("jobs", jobs);

        System.out.println(jobs);

        return "jobs/general_dashboard";
    }

    @GetMapping(path = "/{id}")
    public String getById() {
        return null;
    }
}
