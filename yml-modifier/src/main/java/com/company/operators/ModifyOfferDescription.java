package com.company.operators;

import com.company.OfferDescriptionProvider;
import com.sun.xml.internal.stream.events.CharacterEvent;
import company.handlers.xml.buffered.BufferXmlEventOperator;
import company.util.XmlEventUtil;

import javax.xml.stream.events.Characters;
import javax.xml.stream.events.XMLEvent;
import java.util.List;
import java.util.Optional;

public class ModifyOfferDescription implements BufferXmlEventOperator {

    OfferDescriptionProvider descriptionProvider;

    public ModifyOfferDescription(OfferDescriptionProvider descriptionProvider) {
        this.descriptionProvider = descriptionProvider;
    }

    @Override
    public List<XMLEvent> apply(List<XMLEvent> events) {

        Optional<Characters> description = XmlEventUtil.getCharacterEventOfElement(events, "description");

        if (description.isPresent() ) {
            Optional<String> url = XmlEventUtil.getTextOfElement(events, "url");
            if (!url.isPresent())
                return events;

            String descrText = description.get().getData();

            ((CharacterEvent) description.get()).setData(descriptionProvider.get(url.get()) + descrText);
        }

        return events;
    }
}
