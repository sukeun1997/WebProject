package com.studyforyou.event;

import com.studyforyou.account.CurrentAccount;
import com.studyforyou.domain.Account;
import com.studyforyou.domain.Event;
import com.studyforyou.domain.Study;
import com.studyforyou.dto.EventForm;
import com.studyforyou.study.StudyService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
@RequestMapping("/study/{path}")
public class EventController  {

    private final StudyService studyService;
    private final EventFormValidator eventFormValidator;
    private final EventService eventService;
    private final ModelMapper modelMapper;

    @InitBinder("eventForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(eventFormValidator);
    }

    @GetMapping("/new-event")
    public String createEventView(@CurrentAccount Account account, Model model, @PathVariable String path) {

        Study study = studyService.getStudyWithManagers(account, path);

        model.addAttribute(account);
        model.addAttribute(study);
        model.addAttribute(new EventForm());

        return "/event/form";
    }

    @PostMapping("/new-event")
    public String createEvent(@CurrentAccount Account account, Model model, @PathVariable String path,
                              @Valid EventForm eventForm, BindingResult bindingResult) {

        Study study = studyService.getStudyWithManagers(account, path);

        if (bindingResult.hasErrors()) {
            model.addAttribute(account);
            model.addAttribute(study);
            return "event/form";
        }

        Event event = eventService.createEvent(modelMapper.map(eventForm, Event.class), account, study);
        return "redirect:/study/" + study.getEncodedPath() + "/events/" + event.getId();
    }

    @GetMapping("/events/{eventId}")
    public String eventView(@CurrentAccount Account account, @PathVariable Long eventId, @PathVariable String path, Model model) {

        Study study = studyService.getStudy(path);
        Event event = eventService.getEvent(eventId);

        model.addAttribute(account);
        model.addAttribute(study);
        model.addAttribute(event);

        return "event/view";
    }
}
