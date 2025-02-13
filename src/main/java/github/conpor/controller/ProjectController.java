package github.conpor.controller;


import github.conpor.logic.ProjectService;
import github.conpor.model.Project;
import github.conpor.model.ProjectStep;
import github.conpor.model.projection.ProjectWriteModel;
import io.micrometer.core.annotation.Timed;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService service;

    ProjectController(final ProjectService service) {
        this.service = service;
    }

    @GetMapping
    String showProjects(Model model) {
        model.addAttribute("project", new ProjectWriteModel());
        return "projects";
    }

    @PostMapping
    String addProject(
            @ModelAttribute("project") @Valid ProjectWriteModel current, BindingResult bindingResult,
            Model model) {
        if (bindingResult.hasErrors()) {
            return  "projects";

        }
        service.save(current);
        model.addAttribute("project", new ProjectWriteModel());
        model.addAttribute("project", getProjects());
        model.addAttribute("message", "Dodano projekt");
        return "projects";

    }

    @PostMapping(params = "addStep")
    String addProjectStep(@ModelAttribute("project") ProjectWriteModel current) {
        current.getSteps().add(new ProjectStep());
        return "projects";
    }


    @PostMapping(params = "/fake/{id}")
    String createGroupFake(
            @ModelAttribute("projects") ProjectWriteModel current, Model model,
            @PathVariable int id,
            @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime deadline

    ) {
        return createGroup(current, model, id, deadline);
    }

    @Timed(value = "project.create.group", histogram = false, percentiles = {0.5, 0.95, 0.99})
    @PostMapping(params = "/{id}")
    String createGroup(
            @ModelAttribute("projects") ProjectWriteModel current, Model model,
            @PathVariable int id,
            @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime deadline

    ) {
        try {
            service.createGroup(deadline, id);
            model.addAttribute("message", "Dodano grupę!");
        } catch (IllegalStateException | IllegalArgumentException e) {
            model.addAttribute("message", "Błąd podczas tworzenia grupy!");
        }
        return "projects";
    }

    @ModelAttribute("projects")
    List<Project> getProjects(){
        return service.readAll();

    }
}
