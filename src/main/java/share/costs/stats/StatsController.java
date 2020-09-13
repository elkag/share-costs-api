package share.costs.stats;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/stats")
public class StatsController {

    private  final StatsService statsService;

    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping("/")
    public Model stats(Model model){
        model.addAttribute("Count", statsService.getRequestCount());
        model.addAttribute("StartedOn", statsService.getStartedOn());

        return model;
        //return "Request count: " + statsService.getRequestCount() + " started on: " + statsService.getStartedOn();
    }
}
