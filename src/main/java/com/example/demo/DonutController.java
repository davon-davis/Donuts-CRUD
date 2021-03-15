package com.example.demo;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/donuts")
public class DonutController {

    private final DonutRepository repository;

    public DonutController(DonutRepository repository) {
        this.repository = repository;
    }

    @GetMapping("")
    public Iterable<Donut> list(){
        return this.repository.findAll();
    }

    @PostMapping("")
    public Donut create(@RequestBody Donut donut){
        return this.repository.save(donut);
    }

    @GetMapping("/{id}")
    public Object show(@PathVariable Long id){
        return this.repository.existsById(id) ?
                this.repository.findById(id) :
                "This donut does not exist";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id){
        if(this.repository.existsById(id)){
            this.repository.deleteById(id);
            return "A donut was deleted. Donuts remaining: " + this.repository.count();
        }else{
            return "This donut does not exist";
        }
    }

    @PatchMapping("/{id}")
    public Donut update(@RequestBody Donut donut,
                             @PathVariable Long id){
        if(this.repository.existsById(id)){
            Donut currDonut = this.repository.findById(id).get();
            currDonut.setName(donut.getName());
            currDonut.setTopping(donut.getTopping());
            currDonut.setExpiration(donut.getExpiration());
            return this.repository.save(currDonut);
        }else{
            return this.repository.save(donut);
        }
    }

}
