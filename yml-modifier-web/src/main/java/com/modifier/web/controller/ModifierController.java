package com.modifier.web.controller;

import com.company.ModifierConfig;
import com.company.ModifyService;
import company.config.ConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.xml.stream.XMLStreamException;


/**
 * Created by Naya on 16.01.2016.
 */

@RestController
@RequestMapping("/modifierService")
public class ModifierController {

    @Autowired
    private ConfigRepository<ModifierConfig> configRepository;

    @RequestMapping(value = "{id}/modify", method = RequestMethod.POST)
    public void modify(@PathVariable final String id) throws XMLStreamException {
        Runnable modifyTask = () -> {
            ModifierConfig config = configRepository.get(id);

            ModifyService modifyService = new ModifyService();
            modifyService.process(config);
        };

        new Thread(modifyTask).start();
    }
}
