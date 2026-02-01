package ru.javabegin.micro.booksseller.rentapi.Controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.javabegin.micro.booksseller.rentapi.Domain.Rent;
import ru.javabegin.micro.booksseller.rentapi.Services.RentService;

@RestController
@RequestMapping(path = "/rent")
public class RentController {

    private final RentService rentService;

    public RentController(RentService rentService) {
        this.rentService = rentService;
    }

    @PostMapping(path = "/createRent")
    public void  createRent(@RequestBody Rent rent) {
        rentService.createRent(rent);
    }
}
